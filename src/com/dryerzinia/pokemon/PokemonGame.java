package com.dryerzinia.pokemon;

import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.net.ByteInputStream;
import com.dryerzinia.pokemon.net.DatagramInputStream;
import com.dryerzinia.pokemon.net.DatagramOutputStream;
import com.dryerzinia.pokemon.net.DatagramSocketStreamer;
import com.dryerzinia.pokemon.net.msg.client.ClientMessage;
import com.dryerzinia.pokemon.net.msg.server.GetItemServerMessage;
import com.dryerzinia.pokemon.net.msg.server.GetPokemonServerMessage;
import com.dryerzinia.pokemon.net.msg.server.MessageServerMessage;
import com.dryerzinia.pokemon.net.msg.server.PingServerMessage;
import com.dryerzinia.pokemon.net.msg.server.PlayerServerMessage;
import com.dryerzinia.pokemon.net.msg.server.SMLoad;
import com.dryerzinia.pokemon.net.msg.server.SMLogOff;
import com.dryerzinia.pokemon.net.msg.server.SMLogin;
import com.dryerzinia.pokemon.net.msg.server.ServerMessage;
import com.dryerzinia.pokemon.net.msg.server.act.SendActMovedServerMessage;
import com.dryerzinia.pokemon.net.msg.server.act.SendActTalkingToServerMessage;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Move;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokeball;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.obj.Tile;
import com.dryerzinia.pokemon.obj.Pokemon.BaseStats;
import com.dryerzinia.pokemon.ui.Login;
import com.dryerzinia.pokemon.ui.Overlay;
import com.dryerzinia.pokemon.ui.OverlayO;
import com.dryerzinia.pokemon.ui.PokemonView;
import com.dryerzinia.pokemon.ui.editor.AddLevel;
import com.dryerzinia.pokemon.ui.editor.EditLevel;
import com.dryerzinia.pokemon.ui.editor.MapEditor;
import com.dryerzinia.pokemon.ui.editor.Sub;
import com.dryerzinia.pokemon.ui.editor.SubListener;
import com.dryerzinia.pokemon.ui.editor.UltimateEdit;
import com.dryerzinia.pokemon.ui.editor.UltimateEdit.SuperCommandLine;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.ui.menu.ItemMenu;
import com.dryerzinia.pokemon.ui.menu.MenuEvent;
import com.dryerzinia.pokemon.ui.menu.MoneyMenu;
import com.dryerzinia.pokemon.ui.menu.SelectionMenu;
import com.dryerzinia.pokemon.util.ImageLoader;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.event.AbstractMenuListener;

import java.awt.event.*;
import java.applet.*;

public class PokemonGame extends Applet implements Runnable, WindowListener,
        KeyListener {

    private static final long serialVersionUID = -4243635201957739501L;
    
    public static final int APP_WIDTH = 160;
    public static final int APP_HEIGHT = 144;
    public static final int CHAT_HEIGHT = 100;

    public static int scale = 2;

    public static PokemonGame pokeg;

    public MapEditor me;

    public static ArrayList<Class> allClasses = new ArrayList<Class>();
    public static ArrayList<Actor> actors = new ArrayList<Actor>();

    public static boolean localDataReadingAllowed = true;

    public static ImageLoader images = new ImageLoader();

    public ArrayList<Level> level;

    public ArrayList<Tile> mtile;
    public ArrayList<String> chathist;

    private static int connectMode = PokemonServer.CM_TCP;

    private String username = "";
    private String password = "";
    private String location = "5vddatjhjhvybqwo.onion:53879|localhost:9050"; // TODO:
                                                                             // put
                                                                             // this
                                                                             // default
                                                                             // in
                                                                             // a
                                                                             // file
                                                                             // somewhere

    public Image bi;
    public Graphics bg;
    public Image cbi;
    public Graphics cbg;

    public boolean delay = true;

    private boolean reconnecting = false;

    public static ObjectOutputStream oos2;

    private GMenu currMenu = null;

    public ArrayList<Pokemon> basePokemon;
    public ArrayList<Move> baseMoves;

    public ArrayList<Item> shopitems;

    private SelectionMenu startMenu = null;

    public OverlayO overlay = new OverlayO();

    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;

    private boolean wait = false;

    private int z = 0;
    private int x = 0;

    private boolean modif = false;

    private boolean jmoved = false;
    private boolean lvlchn = false;

    private boolean healMenuActive = false;
    private boolean healing = false;
    private boolean healcancel = false;
    private SelectionMenu healMenu;

    private boolean moneyQuiting = false;
    private boolean moneyMenuActive = false;
    private boolean shopping = false;
    private MoneyMenu moneyMenu;
    private SelectionMenu shoppingMainMenu;

    public boolean run = true;
    public boolean read = true;

    public boolean chating = false;

    private boolean startMenuActive = false;

    private int changeLevel = -1;
    private int clx = -1;
    private int cly = -1;

    private int nextLevel[] = null;

    public int numberOfLevels = 7;

    public Player Char;

    public ArrayList<Player> players;

    public String charName = "NAME";

    public String chattemp = "";

    private Thread t;

    public java.util.Timer pinger;

    public class PingerTask extends TimerTask {
        public void run() {
            PokemonGame.pokeg.writePing();
            System.out.println("ping");
        }
    }

    public void init() {

        loadAllClasses();

        pokeg = this;

        players = new ArrayList<Player>();

        chathist = new ArrayList<String>();

        addKeyListener(this);

        readPokemonBaseStats();
        
        // * - An accuracy check is performed for this attack. Thus,
        // Brightpowder, if held by the opponent, can decrease this attack's
        // accuracy by 7.8%.
        // ^ - If King's Rock is attached to the user, there is an 11.7% chance
        // that the opponent will flinch if the opponent lost HP due to, and the
        // user struck first with, this attack.
        // 00: *^ Deal Damage

        baseMoves = new ArrayList<Move>();
        Move m = new Move("Pound", "", "", "Normal", 0, 35, 40, 255);
        baseMoves.add(m);
        m = new Move("Mega Punch", "", "", "Normal", 0, 20, 80, 216);
        baseMoves.add(m);
        m = new Move("Scratch", "", "", "Normal", 0, 35, 40, 255);
        baseMoves.add(m);
        m = new Move("Vicegrip", "", "", "Normal", 0, 30, 55, 255);
        baseMoves.add(m);
        m = new Move("Cut", "", "", "Normal", 0, 30, 50, 242);
        baseMoves.add(m);
        m = new Move("Wing Attack", "", "", "Flying", 0, 35, 60, 255);
        baseMoves.add(m);
        m = new Move("Slam", "", "", "Normal", 0, 20, 80, 191);
        baseMoves.add(m);
        m = new Move("Vine Whip", "", "", "Grass", 0, 20, 80, 191);
        baseMoves.add(m);
        m = new Move("Mega Kick", "", "", "Normal", 0, 5, 120, 191);
        baseMoves.add(m);
        m = new Move("Horn Attack", "", "", "Normal", 0, 25, 65, 255);
        baseMoves.add(m);
        m = new Move("Tackle", "", "", "Normal", 0, 35, 35, 242);
        baseMoves.add(m);
        m = new Move("Water Gun", "", "", "Water", 0, 25, 40, 255);
        baseMoves.add(m);
        m = new Move("Hydro Pump", "", "", "Water", 0, 5, 120, 204);
        baseMoves.add(m);
        m = new Move("Surf", "", "", "Water", 0, 15, 95, 255);
        baseMoves.add(m);
        m = new Move("Peck", "", "", "Flying", 0, 35, 35, 255);
        baseMoves.add(m);
        m = new Move("Drill Peck", "", "", "Flying", 0, 20, 80, 255);
        baseMoves.add(m);
        m = new Move("Strength", "", "", "Normal", 0, 15, 80, 255);
        baseMoves.add(m);
        m = new Move("Rock Throw", "", "", "Rock", 0, 15, 50, 229);
        baseMoves.add(m);
        m = new Move("Egg Bomb", "", "", "Normal", 0, 10, 100, 191);
        baseMoves.add(m);
        m = new Move("Waterfall", "", "", "Water", 0, 15, 80, 255);
        baseMoves.add(m);
        m = new Move("Megahorn", "", "", "Bug", 0, 10, 120, 216);
        baseMoves.add(m);

        // Good Chance for critical hit

        m = new Move("Karate Chop", "", "", "Fighting", 0, 25, 50, 255);
        baseMoves.add(m);
        m = new Move("Razor Leaf", "", "", "Grass", 0, 25, 55, 242);
        baseMoves.add(m);
        m = new Move("Crabhammer", "", "", "Water", 0, 10, 90, 216);
        baseMoves.add(m);
        m = new Move("Slash", "", "", "Normal", 0, 20, 70, 255);
        baseMoves.add(m);
        m = new Move("Aeroblast", "", "", "Flying", 0, 5, 100, 242);
        baseMoves.add(m);
        m = new Move("Cross Chop", "", "", "Fighting", 0, 5, 100, 204);
        baseMoves.add(m);

        // 01: * Puts Opponent to Sleep

        m = new Move("Sing", "", "", "Normal", 0, 15, 0, 140);
        baseMoves.add(m);
        m = new Move("Sleep Powder", "", "", "Grass", 0, 15, 0, 191);
        baseMoves.add(m);
        m = new Move("Hypnosis", "", "", "Phychic", 0, 20, 0, 153);
        baseMoves.add(m);
        m = new Move("Lovely Kiss", "", "", "Normal", 0, 10, 0, 191);
        baseMoves.add(m);
        m = new Move("Spore", "", "", "Grass", 0, 15, 0, 255);
        baseMoves.add(m);

        // 02: * May Poison Opponent

        m = new Move("Poison Sting", "", "", "Poison", 76, 15, 35, 255);
        baseMoves.add(m);
        m = new Move("Smog", "", "", "Poison", 102, 20, 20, 178);
        baseMoves.add(m);
        m = new Move("Sludge", "", "", "Poison", 76, 20, 65, 255);
        baseMoves.add(m);
        m = new Move("Sludge Bomb", "", "", "Poison", 76, 10, 90, 255);
        baseMoves.add(m);

        // 03: *^ Recovers to user half of HP lost by opponent due to this
        // attack

        m = new Move("Absorb", "", "", "Grass", 0, 20, 20, 255);
        baseMoves.add(m);
        m = new Move("Mega Drain", "", "", "Grass", 0, 10, 40, 255);
        baseMoves.add(m);
        m = new Move("Leech Life", "", "", "Bug", 0, 15, 20, 255);
        baseMoves.add(m);
        m = new Move("Giga Drain", "", "", "Grass", 5, 10, 60, 255);
        baseMoves.add(m);

        // 04: * May burn opponent

        m = new Move("Fire Punch", "", "", "Fire", 25, 15, 75, 255);
        baseMoves.add(m);
        m = new Move("Ember", "", "", "Fire", 25, 25, 40, 255);
        baseMoves.add(m);
        m = new Move("Flamethrower", "", "", "Fire", 25, 15, 95, 255);
        baseMoves.add(m);
        m = new Move("Fire Blast", "", "", "Fire", 25, 5, 120, 216);
        baseMoves.add(m);

        // 05: * May freeze opponent

        m = new Move("Ice Punch", "", "", "Ice", 25, 15, 75, 255);
        baseMoves.add(m);
        m = new Move("Ice Beam", "", "", "Ice", 25, 10, 95, 255);
        baseMoves.add(m);
        m = new Move("Blizzard", "", "", "Ice", 25, 5, 120, 178);
        baseMoves.add(m);
        m = new Move("Powder Snow", "", "", "Ice", 25, 25, 40, 255);
        baseMoves.add(m);

        // 06: * May paralyze opponent

        m = new Move("Thunderpunch", "", "", "Eletric", 25, 15, 75, 255);
        baseMoves.add(m);
        m = new Move("Body Slam", "", "", "Normal", 76, 15, 85, 255);
        baseMoves.add(m);
        m = new Move("Thunder Shock", "", "", "Eletric", 25, 30, 40, 255);
        baseMoves.add(m);
        m = new Move("Thunderbolt", "", "", "Eletric", 25, 15, 95, 255);
        baseMoves.add(m);
        m = new Move("Lick", "", "", "Ghost", 76, 20, 30, 255);
        baseMoves.add(m);
        m = new Move("Zap Cannon", "", "", "Eletric", 255, 5, 100, 127);
        baseMoves.add(m);
        m = new Move("Spark", "", "", "Eletric", 76, 20, 65, 255);
        baseMoves.add(m);
        m = new Move("Dragonbreath", "", "", "Dragon", 76, 20, 60, 255);
        baseMoves.add(m);

        // 07: * User faints as part of this attack's use. Opponent's Defense is
        // temporarily halved in damage calculation. Fainting will not fail,
        // though attack may miss or fail for other reasons. If opponent also
        // faints, opponent's fainting message is shown first.

        m = new Move("Selfdestruct", "", "", "Normal", 0, 5, 200, 255);
        baseMoves.add(m);
        m = new Move("Explosion", "", "", "Normal", 0, 5, 250, 255);
        baseMoves.add(m);

        // 08: * Only effective while opponent is asleep. Recovers to user half
        // of HP lost by opponent due to this attack

        m = new Move("Dream Eater", "", "", "Psychic", 0, 15, 100, 255);
        baseMoves.add(m);

        // 09: Uses last move opponent used. Fails if opponent had used Mirror
        // Move, Sketch, Sleep Talk, Transform, Mimic, Metronome, or any attack
        // user knows.

        m = new Move("Mirror Move", "", "", "Flying", 0, 20, 100, 255);
        baseMoves.add(m);

        // 0A: Increases user's Attack by 1 stage

        m = new Move("Mediate", "", "", "Psychic", 0, 0, 40, 255);
        baseMoves.add(m);
        m = new Move("Sharpen", "", "", "Normal", 0, 0, 30, 255);
        baseMoves.add(m);

        // 0B: Increases user's Defense by 1 stage

        m = new Move("Harden", "", "", "Psychic", 0, 0, 30, 255);
        baseMoves.add(m);
        m = new Move("Withdraw", "", "", "Normal", 0, 0, 40, 255);
        baseMoves.add(m);

        // 0D: Increases user's Special Attack by 1 stage

        m = new Move("Growth", "", "", "Normal", 0, 0, 40, 255);
        baseMoves.add(m);

        // 10: Increases user's evasion by 1 stage

        m = new Move("Double Team", "", "", "Normal", 0, 0, 15, 255);
        baseMoves.add(m);

        // After a successful use of this attack by the user, every use of Stomp
        // by the opponent deals double base damage, even when the opponent uses
        // other attacks in between. This effect ends when the user is switched.

        m = new Move("Minimize", "", "", "Normal", 0, 0, 20, 255);
        baseMoves.add(m);

        // 11: ^ Hits without fail. Fails if opponent is using Dig or Fly

        m = new Move("Swift", "", "", "Normal", 0, 20, 60, 255);
        baseMoves.add(m);
        m = new Move("Faint Attack", "", "", "Dark", 0, 20, 60, 255);
        baseMoves.add(m);

        // Fourth Priority

        m = new Move("Vital Throw", "", "", "Fighting", 0, 10, 70, 255);
        baseMoves.add(m);

        // 12: * Decreases opponent's Attack by 1 stage

        m = new Move("Growl", "", "", "Normal", 0, 40, 0, 255);
        baseMoves.add(m);

        // 13: * Decreases opponent's Defense by 1 stage.

        m = new Move("Tail Whip", "", "", "Normal", 0, 30, 0, 255);
        baseMoves.add(m);
        m = new Move("Leer", "", "", "Normal", 0, 30, 0, 255);
        baseMoves.add(m);

        // 14: * Decreases opponent's Speed by 1 stage.

        m = new Move("String Shot", "", "", "Bug", 0, 30, 0, 242);
        baseMoves.add(m);

        // 17: * Decreases opponent's Accuracy by 1 stage.

        m = new Move("Sand-Attack", "", "", "Ground", 0, 15, 0, 255);
        baseMoves.add(m);
        m = new Move("Smokescreen", "", "", "Normal", 0, 20, 0, 255);
        baseMoves.add(m);
        m = new Move("Kinesis", "", "", "Psychic", 0, 15, 0, 204);
        baseMoves.add(m);
        m = new Move("Flash", "", "", "Normal", 0, 20, 0, 178);
        baseMoves.add(m);

        // 18: * Decreases opponent's evasion by 1 stage.

        m = new Move("Sweet Scent", "", "", "Normal", 0, 20, 0, 255);
        baseMoves.add(m);

        // 19: Resets the stat stages for all stats (including evasion and
        // Accuracy) on both active Pokemon to zero.

        m = new Move("Haze", "", "", "Ice", 0, 30, 0, 255);
        baseMoves.add(m);

        // 1A: *^ When this attack is used, the effect begins and X is set to 0.
        // During effect, user uses this attack during each of its turns and
        // cannot use any other attack, but may switch, and whenever the user is
        // hit by an attack by the opponent (other than Pain Split), the HP lost
        // by user due to that attack is added to X. After the second or third
        // use of this attack after this one, returns to opponent X times 2 and
        // ends effect. Accuracy check is performed only when attack is
        // returned. Counts all hits of multi-hit attacks. Attack returned is
        // affected by type immunities. Returned attack is considered an attack
        // by the user. If user is replaced, the user finishes its turn by doing
        // something other than attack, or this attack is prevented from being
        // used during effect, effect ends without returning an attack.

        m = new Move("Bide", "", "", "Normal", 0, 10, 0, 255);
        baseMoves.add(m);

        // 1B: *^ User uses this attack (even if the attack misses) for two or
        // three turns (including this turn), during which user cannot use any
        // other attack or switch, and after which user becomes confused (even
        // if it was already confused). If user is prevented from using this
        // attack or is replaced, effect ends without causing confusion. If user
        // is asleep, this attack deals damage and lasts one turn (doesn't cause
        // confusion).

        m = new Move("Thrash", "", "", "Normal", 0, 20, 90, 255);
        baseMoves.add(m);
        m = new Move("Pedal Dance", "", "", "Grass", 0, 20, 70, 255);
        baseMoves.add(m);
        m = new Move("Outrage", "", "", "Dragon", 0, 15, 90, 255);
        baseMoves.add(m);

        m = new Move("Gust", "", "", "Flying", 0, 0, 40, 255);
        baseMoves.add(m);
        m = new Move("Thunder Wave", "", "", "Eletric", 0, 30, 0, 255);
        baseMoves.add(m);

        if (run) {

            // XPAD: xp = new JXpad(this, controller);

            bi = createImage(320, 388);
            bg = bi.getGraphics();
            cbi = createImage(320, 200);
            cbg = cbi.getGraphics();

            healMenu = new SelectionMenu("Heal\nCancel", null, 5, 4, 4, 3, 1);

            healMenu.exitOnLast = false;
            healMenu.addMenuListener(new AbstractMenuListener() {
                public void MenuPressed(MenuEvent e) {
                    int i = e.getSelection();
                    if (i == 0) {
                        heal();
                    } else {
                        healCancel();
                    }
                }
            });

            shoppingMainMenu = new SelectionMenu("Buy\nSell\nQuit", null, 0, 0,
                    5, 4, 1);

            shoppingMainMenu.exitOnLast = false;
            shoppingMainMenu.addMenuListener(new AbstractMenuListener() {
                public void MenuPressed(MenuEvent e) {
                    int i = e.getSelection();
                    if (e.isLast() || e.getButton() == MenuEvent.X) {
                        if (!currMenu.message.equals("Thank You!\n ")) {
                            currMenu = new GMenu("Thank You!\n ", 0, 6, 10, 3);
                            moneyQuiting = true;
                            Iterator<Item> iti = Char.items.iterator();
                            while (iti.hasNext()) {
                                Item ite = iti.next();
                                if (ite.number == 0)
                                    iti.remove();
                                writeItem(ite);
                            }
                        } else {
                            moneyMenuActive = false;
                            currMenu = null;
                            moneyQuiting = false;
                        }
                    } else {
                        currMenu = new GMenu("Take your time.\n ", 0, 6, 10, 3);
                        System.out.println("tt");
                    }
                    System.out.println("ps");
                }
            });

            shoppingMainMenu.submenus = new GMenu[3];

            shoppingMainMenu.submenus[0] = null;
            shoppingMainMenu.submenus[1] = null;
            shoppingMainMenu.submenus[2] = null;

            shopitems = new ArrayList<Item>();
            shopitems.add(new Pokeball("Pokeball", "", "", 1, 90, 200));
            shopitems.add(new Item("Potion", "", "", 1, 300));
            shopitems.add(new Item("Parlyz Heal", "", "", 1, 200));
            shopitems.add(new Item("Burn Heal", "", "", 1, 250));

        }

        // if(!read) {
        System.out.println("Loading from file");
        load(new File("save.json"), true);

        Iterator<Tile> itt = mtile.iterator();
        while (itt.hasNext()) {
            Tile t = itt.next();
            if (t instanceof Actor)
                actors.add((Actor) t);
        }

        // while(this.level.size()>12){
        // this.level.remove(12);
        // }
        // }
        /*
         * Iterator<Tile> itt = mtile.iterator(); while(itt.hasNext()){ Tile t =
         * itt.next(); if(t.imgName.equals("ComputerBottom.png")){ GMenu oc =
         * new GMenu("Name turned on\nthe PC.", 0, 6, 10, 3); SelectionMenu smp
         * = new SelectionMenu("Widthdraw\nDeposit\nRelease\nSee Ya!", 0, 0, 7,
         * 5, 1); smp.submenus = new GMenu[4]; smp.submenus[0] = new
         * PokemonBoxMenu(PokemonBoxMenu.WIDTHDRAW); smp.submenus[1] = new
         * PokemonBoxMenu(PokemonBoxMenu.DEPOSIT); smp.submenus[2] = new
         * PokemonBoxMenu(PokemonBoxMenu.RELEASE); smp.submenus[3] = null;
         * SelectionMenu sm = new
         * SelectionMenu("BILL's PC\nNAME's PC\nPROF. OAK's PC\nLOG OFF", 0, 0,
         * 8, 5, 1); sm.submenus = new GMenu[4]; sm.submenus[0] = smp;
         * sm.submenus[1] = null; sm.submenus[2] = null; sm.submenus[3] = null;
         * oc.nextmenu = sm; t.onClick = oc; } }
         */
        // mtile.add(new TrainerPerson("Oak", false, null, 2, 14, 5, 5));
        // level.get(6).g.add(14, 5, mtile.get(mtile.size()-1));
        // mtile.get(mtile.size()-1).id = mtile.size()-1;

        /*
         * Grid g = new Grid(38, 36); for(int x = 0; x < 38; x++) for(int y = 0;
         * y < 36; y++) g.add(x,y,mtile.get(29));
         * 
         * level[6] = new Level(-2, 0, g, 1, Char); level[6].id = 6; g.l =
         * level[6];
         * 
         * level[5].borderL[1] = level[6]; level[5].borders[1] = 6;
         * level[5].borderoffset[1] = 5;
         * 
         * level[6].borderL[7] = level[5]; level[6].borders[7] = 5;
         * level[6].borderoffset[7] = -5;
         */

        // save(new File("save.dat"));

        if (run) {
            t = new Thread(this);
            t.start();
        }

    }

    // All the outputs to the server class, Syncronized to insure no stream
    // corruption from possible sending of 1 message interupting another
    public synchronized void writeItem(Item ite) {
        try {
            oos2.writeObject(new GetItemServerMessage(ite));
            oos2.flush();
        } catch (Exception x) {
            System.err.println("Write Item Failed...");
            x.printStackTrace();
        }
    }

    public synchronized void writeLoadMessage() {
        try {
            oos2.writeObject(new SMLoad());
            oos2.flush();
        } catch (Exception x) {
            System.err.println("Write Item Failed...");
            x.printStackTrace();
        }
    }

    public synchronized void writePokemon(Pokemon p) {
        try {
            oos2.writeObject(new GetPokemonServerMessage(p));
            oos2.flush();
        } catch (Exception x) {
            System.err.println("Write Pokemon Failed...");
            x.printStackTrace();
        }
    }

    public synchronized void writeLogoff() {
        try {
            oos2.writeObject(new SMLogOff());
            oos2.flush();
        } catch (Exception x) {
            System.err.println("Write Logoff Failed...");
            x.printStackTrace();
        }
    }

    public synchronized void writePing() {
        try {
            oos2.writeObject(new PingServerMessage());
            oos2.flush();
            oos2.reset();
        } catch (Exception x) {
            System.err.println("Write PING Failed...");
            x.printStackTrace();
        }
    }

    public synchronized void writeMessage(String msg) {
        try {
            oos2.writeObject(new MessageServerMessage(msg));
            oos2.flush();
        } catch (Exception x) {
            System.err.println("Write Message Failed...");
            x.printStackTrace();
        }
    }

    public synchronized void writeServerMessage(ServerMessage sm) {

        try {
            oos2.writeObject(sm);
            oos2.flush();
        } catch (Exception x) {
            System.err.println("writeServerMessage() failed");
            x.printStackTrace();
        }

    }

    public synchronized void writePlayer() {
        try {
            oos2.writeObject(new PlayerServerMessage(Char));
            oos2.flush();
            oos2.reset();
        } catch (Exception x) {
            System.err.println("Write Player Failed...");
            x.printStackTrace();
        }
    }

    // Tell server that you have engaged the actor in some activity
    public synchronized void writeActor(Actor a, int activity) {
        try {
            Person p = (Person) a;
            if (activity == Person.A_TALKING_TO)
                oos2.writeObject(new SendActTalkingToServerMessage(p.id, p.x,
                        p.y, p.dir, p.level, p.onClick.getActive()));
            else
                oos2.writeObject(new SendActMovedServerMessage(p.id, p.x, p.y,
                        p.dir, p.level));
            oos2.flush();
            oos2.reset();
        } catch (Exception x) {
            System.err.println("Write Actor Failed");
            x.printStackTrace();
        }
    }

    public static synchronized void SendPokemonSwitch(Pokemon p)
            throws IOException {
        // oos2.writeInt(PokemonServer.ID_FIGHT_MESSAGE);
        // oos2.writeInt(Fight.FM_POKEMON_SWITCH);
        // oos2.writeObject(p);
        // oos2.flush();
    }

    public void loadAllClasses() {
        try {
            URL url = this.getClass().getClassLoader().getResource(".");
            String s1 = url.toString();
            File dir = new File(new URI(s1));
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".class");
                }
            };
            String[] list = dir.list(filter);
            for (int i = 0; i < list.length; i++) {
                String className = list[i].substring(0, list[i].length() - 6);
                allClasses.add(this.getClass().getClassLoader()
                        .loadClass(className));
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public void healCancel() {
        healcancel = true;
        healing = false;
        healMenuActive = false;
        currMenu = new GMenu("We hope to see\nyou again!", 0, 6, 10, 3);
    }

    public void heal() {
        Char.lpcx = Char.x;
        Char.lpcy = Char.y;
        Char.lpclevel = Char.level;
        System.out.println("heal() called");
        System.out.println("Char.x: " + Char.x);
        System.out.println("Char.y: " + Char.y);
        System.out.println("Char.level: " + Char.level);
        healcancel = true;
        healing = false;
        healMenuActive = false;
        currMenu = new GMenu(
                "Ok we'll need\nyour POKeMON.\n  \nThank you!\nYour POKeMON are\nfighting fit!",
                0, 6, 10, 3);
        healp();
        for (int i = 0; i < 6; i++) {
            if (Char.poke.belt[i] == null)
                break;
            Char.poke.belt[i].location = i;
            writePokemon(Char.poke.belt[i]);
        }
        Iterator<Item> iti = Char.items.iterator();
        while (iti.hasNext()) {
            Item ite = iti.next();
            if (ite.number == 0)
                iti.remove();
            writeItem(ite);
        }
    }

    public void healp() {
        for (int i = 0; i < 6; i++) {
            if (Char.poke.belt[i] == null)
                break;
            Char.poke.belt[i].currentHP = Char.poke.belt[i].getTotalHP();
            for (int j = 0; j < 4; j++) {
                if (Char.poke.belt[i].moves[j] == null)
                    break;
                Char.poke.belt[i].moves[j].currentpp = Char.poke.belt[i].moves[j].pp;
            }
        }
    }

    public class listener extends Thread {

        ObjectInputStream ois;

        public listener(ObjectInputStream ois) {
            this.ois = ois;
        }

        public void run() {

            try {
                while (true) {
                    try {

                        ClientMessage receivedMessage = (ClientMessage) ois
                                .readObject();
                        receivedMessage.proccess();

                    } catch (Exception x) {
                        x.printStackTrace();
                        break;
                    }
                }

            } catch (Exception x) {

                x.printStackTrace();

            } finally {

                // ois.close();

            }
        }

        public Player findPlayer(String name) {
            Iterator<Player> i = players.iterator();
            while (i.hasNext()) {
                Player p = i.next();
                if (p.name.equals(name)) {
                    return p;
                }
            }
            return null;
        }

    }

    public static void initTileSecondary(Grid g) {
        for (int x = 0; x < g.g.length; x++) {
            for (int y = 0; y < g.g[0].length; y++) {
                for (int j = 0; j < g.g[x][y].size(); j++) {
                    g.g[x][y].get(j).initializeSecondaryReferences(g);
                }
            }
        }
    }

    public void setupMenus() {

        startMenu.submenus[1] = new ItemMenu(Char.items, 2, 1, 8, 5, 1);
        moneyMenu = new MoneyMenu(Char);
        shoppingMainMenu.submenus[0] = new ItemMenu(shopitems, 2, 1, 8, 5, 1);
        shoppingMainMenu.submenus[1] = new ItemMenu(Char.items, 2, 1, 8, 5, 1);
        ItemMenu buymenu = (ItemMenu) shoppingMainMenu.submenus[0];
        ItemMenu sellmenu = (ItemMenu) shoppingMainMenu.submenus[1];
        buymenu.p = Char;
        sellmenu.p = Char;
        buymenu.prices = true;
        buymenu.addMenuListener(new AbstractMenuListener() {
            public void MenuPressed(MenuEvent e) {
                int i = e.getSelection();
                if ((e.isLast() && e.getButton() == MenuEvent.Z)
                        || e.getButton() == MenuEvent.X) {
                    currMenu = new GMenu("Is there anything\nelse I can do?",
                            0, 6, 10, 3);
                    System.out.println("else");
                }
            }
        });
        sellmenu.addMenuListener(new AbstractMenuListener() {
            public void MenuPressed(MenuEvent e) {
                int i = e.getSelection();
                if ((e.isLast() && e.getButton() == MenuEvent.Z)
                        || e.getButton() == MenuEvent.X) {
                    currMenu = new GMenu("Is there anything\nelse I can do?",
                            0, 6, 10, 3);
                    System.out.println("else");
                }
            }
        });
    }

    public void save(File f) {
        try {

        	PrintWriter json_writer = new PrintWriter(f);

        	json_writer.print(JSONArray.arrayListToJSON(mtile));
        	json_writer.print("\n");
        	json_writer.print(JSONArray.arrayListToJSON(level));
        	
        	json_writer.close();

          /*FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream((OutputStream) fos);
            oos.writeInt(mtile.size());
            for (int i = 0; i < mtile.size(); i++) {
                oos.writeObject(mtile.get(i));
            }
            oos.writeInt(level.size());
            for (int i = 0; i < level.size(); i++) {
                oos.writeObject(level.get(i));
            }
            oos.flush();
            oos.close();*/

        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    public void load(File f, boolean local) {

        try {

            mtile = new ArrayList<Tile>();
            level = new ArrayList<Level>();

            System.out.println("f.getName(): "+ f.getCanonicalPath());

            BufferedReader json_reader = new BufferedReader(new InputStreamReader(
                    PokemonGame.class.getClassLoader().getResourceAsStream("save.json")));

			String json_1 = json_reader.readLine();
			String json_2 = json_reader.readLine();

			json_reader.close();
		
			Object[] tiles = JSONObject.JSONToArray(json_1);
			Object[] levels = JSONObject.JSONToArray(json_2);

            for(int i = 0; i < tiles.length; i++)
            	mtile.add((Tile)tiles[i]);

            for(int i = 0; i < levels.length; i++)
                level.add((Level)levels[i]);

        } catch (Exception x) {
            x.printStackTrace();
        }

        numberOfLevels = level.size();// TODO Erradicate this variable
        for (int i = 0; i < numberOfLevels; i++) {

            for (int x = 0; x < level.get(i).g.g.length; x++) {
                for (int y = 0; y < level.get(i).g.g[0].length; y++) {
                    for (int j = 0; j < level.get(i).g.g[x][y].size(); j++) {
                        int id = level.get(i).g.g[x][y].get(j).id;
                        Tile t = mtile.get(id);
                        level.get(i).g.g[x][y].set(j, t);
                        if (UltimateEdit.Extends(t, "Person")) {
                            Person p = (Person) t;
                            p.x = x;
                            p.y = y;
                            p.level = i;
                        }
                    }
                }
            }
            level.get(i).initLevelReferences(level);
            initTileSecondary(level.get(i).g);

        }

    }

    public static void switchPokemon(Pokemon p[], int i, int j) {
        Pokemon p1 = p[i];
        p[i] = p[j];
        p[j] = p1;
        p[i].location = i;
        p[j].location = j;
    }

    public void initTCPConnect() throws Exception {

        Socket s;

        SocketAddress address = null;
        InetSocketAddress inet = null;

        java.net.Proxy proxy = null;
        boolean use_proxy = false;

        String proxyHost = "localhost";
        int proxyPort = 9050;

        String host = "5vddatjhjhvybqwo.onion";
        int port = PokemonServer.PORT_NUM; // 53879

        StringTokenizer st = new StringTokenizer(location, "|:");

        host = st.nextToken();
        if (st.hasMoreTokens()) {
            port = Integer.parseInt(st.nextToken());
        }
        if (st.hasMoreTokens()) {
            use_proxy = true;
            proxyHost = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            proxyPort = Integer.parseInt(st.nextToken());
        }

        if (use_proxy) {

            address = new InetSocketAddress(proxyHost, proxyPort);
            proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS, address);

            inet = InetSocketAddress.createUnresolved(host, port);

            s = new Socket(proxy);

        } else {

            inet = new InetSocketAddress(host, port);

            s = new Socket();

        }

        // DEBUG
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("ProxHost: " + proxyHost);
        System.out.println("ProxPort: " + proxyPort);
        System.out.println("use_proxy: " + use_proxy);
        // ENDDEBUG

        s.connect(inet);

        oos2 = new ObjectOutputStream(s.getOutputStream());

        oos2.writeObject(new SMLogin(username, password));
        oos2.flush();

        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

        new listener(ois).start();

    }

    public class DatagramListener extends Thread {

        public DatagramSocket ds;
        private DatagramSocketStreamer dss;

        public DatagramListener(DatagramSocket ds, DatagramSocketStreamer dss) {
            this.ds = ds;
            this.dss = dss;
        }

        public void run() {
            DatagramPacket dp = new DatagramPacket(new byte[10000], 10000);

            try {
                while (true) {
                    ds.receive(dp);
                    ByteInputStream bis = new ByteInputStream(dp.getData());
                    int id = bis.readInt();
                    int len = bis.readInt();
                    byte[] data = new byte[len];
                    for (int i = 0; i < len; i++)
                        data[i] = (byte) bis.read();
                    dss.addToByteArray(data);
                }
            } catch (Exception x) {
                if (!reconnecting)
                    attemptReconnect();
                System.err.println("Failed to recive packet.");
                x.printStackTrace();
            }
        }
    }

    public void attemptReconnect() {
        removeKeyListener(this);
        t.stop();
        Image rci = createImage(APP_WIDTH, APP_HEIGHT - CHAT_HEIGHT);
        Graphics g = rci.getGraphics();
        g.drawImage(bi, 0, 0, null);
        g.setColor(Color.RED);
        g.drawString("Lost Connection", 25, 50);
        g.drawString("Attempting Reconnect...", 10, 90);
        Graphics g2 = getGraphics();
        bg.drawImage(rci, 0, 0, null);
        g2.drawImage(bi, 0, 0, 320, 288, 0, 0, 160, 144, null);
        reconnecting = true;
        int failed = 0;
        while (true) {
            try {
                if (failed >= 6) {
                    reconnecting = false;
                    t = new Thread(this);
                    t.start();
                    break;
                } else {
                    t = new Thread(this);
                    t.start();
                }
                Thread.sleep(5000);
                if (reconnecting) {
                    failed++;
                    t.stop();
                } else
                    break;
            } catch (Exception x) {
                System.out.println("Reconnect failed...");
            }
        }
        addKeyListener(this);
    }

    public void startConnect() throws Exception {
        switch (connectMode) {
        case PokemonServer.CM_TCP:
            initTCPConnect();
            break;
        case PokemonServer.CM_DATAGRAM:
            initDatagramConnect();
            break;
        }
    }

    public void initDatagramConnect() throws Exception {

        DatagramSocket ds = new DatagramSocket();
        InetAddress loc = InetAddress.getByName(location);
        ds.connect(loc, PokemonServer.PORT_NUM);

        DatagramSocketStreamer dss = new DatagramSocketStreamer(ds,
                new InetSocketAddress(loc, PokemonServer.PORT_NUM), -1);

        DatagramInputStream dis = (DatagramInputStream) dss.getInputStream();
        DatagramOutputStream dos = (DatagramOutputStream) dss.getOutputStream();

        (new DatagramListener(ds, dss)).start();

        dos.flush();

        ObjectInputStream ois = new ObjectInputStream(dis);

        int id = ois.readInt();
        dss.setID(id);

        System.out.println("ID:" + id);

        ObjectOutputStream oos = new ObjectOutputStream(dos);

        // oos2.writeInt(PokemonServer.ID_LOGIN); // TODO: FIX TO NEW SIGNALING
        // oos2.writeObject(username);
        // oos2.writeObject(password);
        // oos2.flush();

        (new listener(ois)).start();

        // oos22.writeInt(PokemonServer.ID_GET_PLAYER);
        // oos22.flush();

    }

    public void writePokemonBaseStats(ArrayList<Pokemon> basePokemon){

    	// Write Pokemon Base stats to File
        File file = new File("PokemonBaseStats.json");
        StringBuilder json = new StringBuilder("[");

        for(Pokemon pokemon : basePokemon){

        	json.append(pokemon.toJSON());
        	json.deleteCharAt(json.length() - 1);	// Strip } from JSON
        	json.append(",'pokeBase':" + pokemon.getBaseStats().toJSON());	// Add BasePokemon information to the JSON string
        	json.append("},"); // Close JSON object

        }
        
        json.replace(json.length()-1, json.length(), "]"); // Replace last extra , with array closure

        // Attempt to write stats to the file
        try (BufferedWriter json_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {

        	json_writer.write(json.toString());

        } catch(IOException ioe) {

        	System.err.println("ERROR: Pokemon Base Stats not written to file: " + file.getAbsolutePath());

        	ioe.printStackTrace();
        	
        }

    }

    public void readPokemonBaseStats(){
    	
    	try (BufferedReader json_reader = new BufferedReader(new InputStreamReader(
                PokemonGame.class.getClassLoader().getResourceAsStream("PokemonBaseStats.json")))) {
    		
    		String json = json_reader.readLine();

    		basePokemon = new ArrayList<Pokemon>();

    		// Base Pokemon loads add themselves to the master list automatically
    		// in there fromJSON methods
    		JSONObject.JSONToArray(json);
    		
    	} catch(IOException ioe) {

    		System.err.println("ERROR: Failed to load Pokemon Bast Stats!");
    		ioe.printStackTrace();
    		
    		// TODO Terminate program

    	}

    }
    
    public void writeSettingsData(File f) {

    	try (BufferedWriter json_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)))) {

        	json_writer.write("{'username':'" + username + "','password':'" + password + "','location':'" + location + "'}");
        	json_writer.flush();
        	json_writer.close();

    	} catch (IOException ioe) {

    		System.out.println("Error: Failed to write settings data!");

    	}
    }

    public void readSettingsData(File f) {

    	try (BufferedReader json_reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
        	
        	String json = json_reader.readLine();

        	@SuppressWarnings("unchecked")
			HashMap<String, Object> json_obj = (HashMap<String, Object>) JSONObject.JSONToObject(json);

        	username = (String) json_obj.get("username");
            password = (String) json_obj.get("password");
            location = (String) json_obj.get("location");

        } catch (IOException ioe) {

        	System.out.println("Failed to read settings data.");

        }
    }

    public File getSettingsFile() {

    	String fs = System.getProperty("file.separator");

    	return new File(System.getProperty("user.home") + fs + ".pokemonData"
                + fs + "defaultSettings");
    }

    public void loginScreen() {

        System.out.println("Login");

        boolean repeat = true;

        Login l = new Login();

        overlay.o = l;

        username = "";
        password = "";
        location = "75.70.0.170";

        if (localDataReadingAllowed) {
            try {
                String fs = System.getProperty("file.separator");
                File f = new File(System.getProperty("user.home") + fs
                        + ".pokemonData");
                if (!f.exists())
                    f.mkdir();
                f = getSettingsFile();
                if (!f.exists()) {
                    f.createNewFile();
                    writeSettingsData(f);
                } else
                    readSettingsData(f);
            } catch (Exception x) {
                System.out.println("Failed to proccess settings data.");
            }
        }

        l.username = username;
        l.password = password;
        l.location = location;

        while (repeat) {
            repeat = false;
            if (read) {
                System.out.println("Loading via Socket");
                try {
                    overlay.o.active = true;

                    while (true) {
                        Graphics g = getGraphics();
                        overlay.o.draw(bg);
                        try {
                            g.drawImage(bi, 0, 0, 320, 288, 0, 0, 160, 144,
                                    null);
                            g.dispose();
                        } catch (Exception x) {
                            x.printStackTrace();
                        }
                        try {
                            Thread.sleep(100);
                        } catch (Exception x) {
                            x.printStackTrace();
                        }
                        if (!overlay.o.active)
                            break;
                    }

                    username = l.username;
                    password = l.password;
                    location = l.location;

                    Graphics g = getGraphics();
                    bg.setColor(Color.BLACK);
                    bg.fillRect(0, 0, 160, 144);
                    bg.setColor(Color.WHITE);
                    bg.setFont(new Font("monospaced", 0, 16));
                    bg.drawString("LOGGING IN...", 20, 50);
                    bg.drawImage(images.getSprite("PikachuP.png"), 40, 55, null);
                    g.drawImage(bi, 0, 0, 320, 288, 0, 0, 160, 144, null);
                    g.dispose();

                    charName = username;

                    writeSettingsData(getSettingsFile());

                    startConnect();

                } catch (Exception x) {
                    x.printStackTrace();
                    System.out.println("Connection Error!");

                    Graphics g = getGraphics();
                    bg.setColor(Color.BLACK);
                    bg.fillRect(0, 0, 160, 144);
                    bg.setColor(Color.WHITE);
                    bg.setFont(new Font("monospaced", 0, 16));
                    bg.drawString("CONNECTION ERROR", 1, 50);
                    bg.drawImage(images.getSprite("PikachuSad.png"), 50, 35,
                            null);
                    bg.drawString("Press Any Key To", 1, 100);
                    bg.drawString("Restart", 50, 120);
                    g.drawImage(bi, 0, 0, 320, 288, 0, 0, 160, 144, null);
                    g.dispose();

                    wait = true;
                    repeat = true;

                    while (wait) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception x2) {
                        }
                    }
                    overlay.o.active = true;

                }
            }
        }
    }

    public class ReconnectThread extends Thread {

        public ReconnectThread() {
        }

        public void run() {
            attemptReconnect();
        }

    }

    public void run() {

        if (!reconnecting)
            loginScreen();
        else {
            try {
                startConnect();
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        reconnecting = false;

        startMenu = new SelectionMenu(
                "POKeMON\nITEM\nNAME\nSAVE\nOPTION\nEXIT", 5, 0, 5, 7, 1);
        startMenu.pg = this;

        Overlay ol[] = new Overlay[6];

        final PokemonGame pg = this;
        ((PokemonView) (overlay.o = new PokemonView()))
                .addSubListener(new SubListener() {
                    public void SubEvent(Sub s) {
                        System.out.println("subbed");
                        // Fight.sendNowPokemon();
                        // TODO: Figure out why this is here
                    }
                });

        ol[0] = overlay.o;
        ol[1] = null;
        ol[2] = null;
        ol[3] = null;
        ol[4] = null;
        ol[5] = null;

        startMenu.toset = overlay;

        startMenu.overlays = ol;

        startMenu.submenus = new GMenu[6];

        startMenu.submenus[0] = null;
        startMenu.submenus[1] = null;
        startMenu.submenus[2] = null;
        startMenu.submenus[3] = null;
        startMenu.submenus[4] = null;
        startMenu.submenus[5] = null;

        while (true) {

            Graphics g = getGraphics();

            if (overlay.o.active) {

                overlay.o.draw(bg);

            } else {

                if (Char != null && level.get(Char.level).midmove) {

                    System.out.println("Char.x: " + Char.x);
                    System.out.println("Char.y: " + Char.y);
                    System.out.println("Char.level: " + Char.level);

                    if (Char.dir == 0) {
                        level.get(Char.level).moveUp();
                    } else if (Char.dir == 1) {
                        level.get(Char.level).moveDown();
                    } else if (Char.dir == 2) {
                        level.get(Char.level).moveLeft();
                    } else if (Char.dir == 3) {
                        level.get(Char.level).moveRight();
                    }

                    try {
                        writePlayer();
                    } catch (Exception x) {
                        // TODO: MAKE SURE THIS IS FULL PROOF
                        if (!reconnecting)
                            (new ReconnectThread()).start();
                    }

                    // TODO: get attacks from the server!!!
                    /*
                     * Pokemon p = level.get(Char.level).attacked(); if(p !=
                     * null){ Pokemon p2 = new Pokemon(p);
                     * p2.getBase(basePokemon, baseMoves); p2.currentHP =
                     * p2.getTotalHP(); overlay.o = new Fight(p2);
                     * overlay.o.active = true; }
                     */
                    // END TODO

                    level.get(Char.level).midmove = false;
                    modif = true;
                    jmoved = false;
                    if (changeLevel != -1) {
                        Char.level = changeLevel;
                        changeLevel = -1;
                        Char.x = clx;
                        Char.y = cly;
                        level.get(Char.level).midmove = false;
                    }
                }

                if (!modif && currMenu == null && !startMenuActive) {
                    if (up) {
                        if (!level.get(Char.level).midmove) {
                            if (Char.dir == 0) {
                                level.get(Char.level).midmove = true;
                                if (nextLevel != null && nextLevel[4] == 0)
                                    changeLevel();
                                else
                                    nextLevel = null;
                                checkLevelChange();
                                jmoved = true;
                            }
                            Char.dir = 0;
                        }
                    } else if (down) {
                        if (!level.get(Char.level).midmove) {
                            if (Char.dir == 1) {
                                level.get(Char.level).midmove = true;
                                if (nextLevel != null && nextLevel[4] == 1)
                                    changeLevel();
                                else
                                    nextLevel = null;
                                checkLevelChange();
                                jmoved = true;
                            }
                            Char.dir = 1;
                        }
                    } else if (left) {
                        if (!level.get(Char.level).midmove) {
                            if (Char.dir == 2) {
                                level.get(Char.level).midmove = true;
                                if (nextLevel != null && nextLevel[4] == 2)
                                    changeLevel();
                                else
                                    nextLevel = null;
                                checkLevelChange();
                                jmoved = true;
                            }
                            Char.dir = 2;
                        }
                    } else if (right) {
                        if (!level.get(Char.level).midmove) {
                            if (Char.dir == 3) {
                                level.get(Char.level).midmove = true;
                                if (nextLevel != null && nextLevel[4] == 3)
                                    changeLevel();
                                else
                                    nextLevel = null;
                                checkLevelChange();
                                jmoved = true;
                            }
                            Char.dir = 3;
                        }
                    }
                }

                if (Char != null && level.get(Char.level).midmove) {
                    if (changeLevel != -1) {
                        level.get(Char.level).canMove = level.get(changeLevel).g
                                .canStepOn(clx, cly);
                        if (changeLevel != -1) {
                            Char.level = changeLevel;
                            changeLevel = -1;
                            Char.x = clx;
                            Char.y = cly;
                            level.get(Char.level).midmove = false;
                        }
                    } else {
                        if (Char.dir == 0) {
                            level.get(Char.level).canMove = level
                                    .get(Char.level).g.canStepOn(Char.x,
                                    Char.y - 1);
                        } else if (Char.dir == 1) {
                            level.get(Char.level).canMove = level
                                    .get(Char.level).g.canStepOn(Char.x,
                                    Char.y + 1);
                        } else if (Char.dir == 2) {
                            level.get(Char.level).canMove = level
                                    .get(Char.level).g.canStepOn(Char.x - 1,
                                    Char.y);
                        } else if (Char.dir == 3) {
                            level.get(Char.level).canMove = level
                                    .get(Char.level).g.canStepOn(Char.x + 1,
                                    Char.y);
                        }
                    }
                    // if(level.get(Char.level).canMove) lvlchn = false;
                }

                if (z > 0 && !startMenuActive) {
                    z = 0;
                    boolean cont = true;
                    try {
                        if (Char.dir == 0) {
                            if (Char.y + 2 < level.get(Char.level).g
                                    .getHeight()) {
                                for (int i = 0; i < level.get(Char.level).g.g[Char.x + 4][Char.y + 2]
                                        .size(); i++) {
                                    if (level.get(Char.level).g.g[Char.x + 4][Char.y + 2]
                                            .get(i).toString()
                                            .equals("NurseJoyD.png")
                                            && currMenu == null) {
                                        currMenu = new GMenu(
                                                "Welcome to our\nPOKeMON CENTER!\n  \nWe heal your\nPOKeMON back to\nperfect health!",
                                                0, 6, 10, 3);
                                        cont = false;
                                        healing = true;
                                        System.out.println("Heal...");
                                    }
                                }
                            }
                        } else if (Char.dir == 2) {
                            if (Char.x + 2 < level.get(Char.level).g.getWidth()) {
                                for (int i = 0; i < level.get(Char.level).g.g[Char.x + 2][Char.y + 4]
                                        .size(); i++) {
                                    if (level.get(Char.level).g.g[Char.x + 2][Char.y + 4]
                                            .get(i).toString()
                                            .equals("ShopClerkR.png")
                                            && currMenu == null) {
                                        currMenu = new GMenu(
                                                "Hi there!\nMay I help you?",
                                                0, 6, 10, 3);
                                        cont = false;
                                        moneyMenuActive = true;
                                        shopping = true;
                                        System.out.println("Shop...");
                                    }
                                }
                            }
                        }
                    } catch (Exception x) {
                        // System.out.println("Heal out of bounds");
                    }
                    if (cont) {
                        if (currMenu == null && Char != null) {
                            if (Char.dir == 0) {
                                currMenu = level.get(Char.level).g.hasMenu(
                                        Char.x, Char.y - 1);
                            } else if (Char.dir == 1) {
                                currMenu = level.get(Char.level).g.hasMenu(
                                        Char.x, Char.y + 1);
                            } else if (Char.dir == 2) {
                                currMenu = level.get(Char.level).g.hasMenu(
                                        Char.x - 1, Char.y);
                            } else if (Char.dir == 3) {
                                currMenu = level.get(Char.level).g.hasMenu(
                                        Char.x + 1, Char.y);
                            }
                        } else {
                            if (healing) {
                                if (!healMenuActive) {
                                    if (currMenu.push()) {
                                        healMenuActive = true;
                                        currMenu.push();
                                        currMenu.push();
                                    }
                                } else {
                                    healMenu.push();
                                }
                            } else if (shopping) {
                                if (moneyQuiting) {
                                    moneyMenuActive = false;
                                    currMenu = null;
                                    moneyQuiting = false;
                                } else if (moneyMenuActive)
                                    shoppingMainMenu.push();
                            } else {
                                if (currMenu != null && currMenu.push()) {
                                    if (currMenu.nextmenu != null) {
                                        currMenu = currMenu.nextmenu;
                                        currMenu.setActive(false);
                                    } else
                                        currMenu = null;
                                }
                            }
                        }
                    }
                }

                if (Char != null) {
                    level.get(Char.level).act();
                    level.get(Char.level).draw(bg);
                    Iterator<Player> it = players.iterator();
                    while (it.hasNext()) {
                        Player p = it.next();
                        if (p.level == Char.level) {
                            if (!level.get(Char.level).midmove
                                    || !level.get(Char.level).canMove)
                                p.draw(Char.x, Char.y, bg);
                            else {
                                int xo = 0;
                                int yo = 0;
                                int direction = Char.dir;
                                if (direction == 0)
                                    yo = 8;
                                if (direction == 1)
                                    yo = -8;
                                if (direction == 2)
                                    xo = 8;
                                if (direction == 3)
                                    xo = -8;
                                p.draw(Char.x, Char.y, xo, yo, bg);
                            }
                        }
                    }

                    if (currMenu != null)
                        currMenu.draw(bg);
                    if (healMenu != null && healMenuActive)
                        healMenu.draw(bg);
                    if (startMenuActive)
                        startMenu.draw(bg);

                    modif = false;

                    if (level.get(Char.level).midmove) {
                        if (down) {
                            try {
                                if (Char.y + 5 < level.get(Char.level).g
                                        .getHeight())
                                    for (int i = 0; i < level.get(Char.level).g.g[Char.x + 4][Char.y + 5]
                                            .size(); i++) {
                                        if (level.get(Char.level).g.g[Char.x + 4][Char.y + 5]
                                                .get(i).toString()
                                                .equals("Bolder1.png")
                                                || level.get(Char.level).g.g[Char.x + 4][Char.y + 5]
                                                        .get(i).toString()
                                                        .equals("Bolder2.png"))
                                            Char.y = Char.y + 1;
                                    }
                            } catch (Exception x) {
                            }
                        }
                    }
                }
            }

            if (moneyMenu != null && moneyMenuActive) {
                moneyMenu.draw(bg);
                shoppingMainMenu.draw(bg);
            }

            cbg.setColor(Color.BLACK);
            cbg.fillRect(0, 0, 320, 100);
            cbg.setColor(Color.WHITE);
            cbg.drawString(chattemp, 10, 12);

            int j = 0;
            for (int i = chathist.size() - 1; i > chathist.size() - 7; i--) {
                if (i < 0)
                    break;
                String s = chathist.get(i);
                if (s.indexOf("whisper") == s.indexOf(" ") + 1)
                    cbg.setColor(Color.BLUE);
                else
                    cbg.setColor(Color.WHITE);
                cbg.drawString(s, 10, 27 + 15 * j);
                j++;
            }

            g.drawImage(bi, 0, 0, 320, 288, 0, 0, 160, 144, null);
            g.drawImage(cbi, 0, 288, null);

            g.dispose();

            if (delay) {
                try {
                    Thread.sleep(150);
                } catch (Exception x) {
                }
            }
        }
    }

    public void changeLevel() {
        changeLevel = nextLevel[0];
        clx = nextLevel[1];
        cly = nextLevel[2];
        if (nextLevel[3] != -1)
            Char.dir = nextLevel[3];
        jmoved = false;
        lvlchn = true;
        nextLevel = null;
        System.out.println("PokemonGame::ChangeLevel() called");
    }

    public void paint(Graphics g) {
        g.drawImage(bi, 0, 0, 320, 288, 0, 0, 160, 144, null);
        g.dispose();
    }

    public void checkLevelChange() {
        int l[];
        l = level.get(Char.level).g.changeLevel(Char.x, Char.y);
        if (l[0] != -1 && l[4] != -1) {
            nextLevel = l;
        } else {
            if (Char.dir == 0)
                l = level.get(Char.level).g.changeLevel(Char.x, Char.y - 1);
            else if (Char.dir == 1)
                l = level.get(Char.level).g.changeLevel(Char.x, Char.y + 1);
            else if (Char.dir == 2)
                l = level.get(Char.level).g.changeLevel(Char.x - 1, Char.y);
            else if (Char.dir == 3)
                l = level.get(Char.level).g.changeLevel(Char.x + 1, Char.y);
            if (l[0] != -1 && l[4] == -1) {
                changeLevel = l[0];
                clx = l[1];
                cly = l[2];
                System.out.println("changeLevel: " + changeLevel);
                System.out.println("clx: " + clx);
                System.out.println("cly: " + cly);
                if (l[3] != -1)
                    Char.dir = l[3];
                jmoved = false;
                lvlchn = true;
            }
        }
        // System.out.println("PokemonGame::checkLevelChange() called");
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        writeLogoff();
        System.exit(0);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public static void main(String[] args) {

        Applet pg = new PokemonGame();
        /*
         * XPAD: if(args.length > -1){ ((PokemonGame)pg).controller = args[0];
         * System.out.println(((PokemonGame)pg).controller); }
         */
        JFrame frame = new JFrame("Pokemon");
        frame.getContentPane().add(pg);
        frame.addWindowListener((WindowListener) pg);
        frame.setSize(APP_WIDTH * scale + 10, APP_HEIGHT * scale + CHAT_HEIGHT
                + 30);
        frame.setVisible(true);

        pg.init();
    }

    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();
        boolean ja = false;
        if (wait) {
            wait = false;
            return;
        }
        if (c == KeyEvent.VK_UP) {
            if (healMenuActive)
                healMenu.pressUp();
            if (moneyMenuActive)
                shoppingMainMenu.pressUp();
            if (startMenuActive && !overlay.o.active)
                startMenu.pressUp();
            if (currMenu != null && currMenu instanceof SelectionMenu)
                ((SelectionMenu) currMenu).pressUp();
            if (!up)
                changeLevel = -1;
            up = true;
            chating = false;
        } else if (c == KeyEvent.VK_LEFT) {
            if (healMenuActive)
                healMenu.pressLeft();
            if (moneyMenuActive)
                shoppingMainMenu.pressLeft();
            if (!left)
                changeLevel = -1;
            left = true;
            chating = false;
        } else if (c == KeyEvent.VK_RIGHT) {
            if (healMenuActive)
                healMenu.pressRight();
            if (moneyMenuActive)
                shoppingMainMenu.pressRight();
            if (!right)
                changeLevel = -1;
            right = true;
            chating = false;
        } else if (c == KeyEvent.VK_DOWN) {
            if (healMenuActive)
                healMenu.pressDown();
            if (moneyMenuActive)
                shoppingMainMenu.pressDown();
            if (startMenuActive && !overlay.o.active)
                startMenu.pressDown();
            if (currMenu != null && currMenu instanceof SelectionMenu)
                ((SelectionMenu) currMenu).pressDown();
            if (!down)
                changeLevel = -1;
            down = true;
            chating = false;
        } else if (c == KeyEvent.VK_Z) {
            if (startMenuActive && !overlay.o.active) {
                if (!overlay.o.active)
                    ja = true;
                if (startMenu.push()) {
                    startMenuActive = false;
                }
                if (!overlay.o.active && ja)
                    ja = false;
            } else
                z++;
        } else if (c == KeyEvent.VK_ENTER) {
            if (e.isShiftDown()) {
                chating = !chating;
            } else {
                if (chattemp.length() > 0 && chating) {
                    writeMessage(chattemp);
                    String s = new String(chattemp);
                    String s2 = "";
                    if (s.charAt(0) == '/') {
                        switch (s.charAt(1)) {
                        case 'w':
                            try {
                                int sp = s.indexOf(' ', 3);
                                String name = s.substring(3, sp);
                                String message = s.substring(sp, s.length());
                                s2 += "You whisper to " + name + ": " + message;
                            } catch (Exception x) {
                            }
                            break;
                        }
                    } else {
                        s2 += Char.name + ": " + s;
                    }
                    chathist.add(s2);
                    chattemp = "";
                }
                if (currMenu == null && !chating && !overlay.o.active)
                    startMenuActive = !startMenuActive;
            }
        } else if (c == KeyEvent.VK_X) {
            if (startMenuActive && !overlay.o.active) {
                if (startMenu.pushB()) {
                    startMenuActive = false;
                }
            } else if (currMenu != null) {
                if (currMenu.pushB()) {
                    currMenu = null;
                }
            } else
                x++;
            if (moneyMenuActive)
                shoppingMainMenu.pushB();
        } else if (c == KeyEvent.VK_S) {
            if (e.getModifiers() == InputEvent.CTRL_MASK) {
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    save(fc.getSelectedFile());
                }

            }
        } else if (c == KeyEvent.VK_L) {
            if (e.getModifiers() == InputEvent.CTRL_MASK) {
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    t.stop();
                    t = null;
                    load(fc.getSelectedFile(), false);
                    t = new Thread(this);
                    t.start();
                }

            }
        } else if (c == KeyEvent.VK_M) { // In Game Map Editor
            if (e.getModifiers() == InputEvent.CTRL_MASK) {
                if (me == null) {
                    me = new MapEditor(this, basePokemon, baseMoves);
                    addMouseListener(me);
                }
            }
        } else if (c == KeyEvent.VK_K) { // In Game1 Level Adder
            if (e.getModifiers() == InputEvent.CTRL_MASK) {
                new AddLevel(mtile, level, Char);
            }
        } else if (c == KeyEvent.VK_J) { // Initialize Jesus Powers
            if (e.getModifiers() == InputEvent.CTRL_MASK) {
                Iterator<Tile> itile = mtile.iterator();
                while (itile.hasNext()) {
                    Tile t = itile.next();
                    String img_name = t.getImageName();
                    if (img_name.equals("Water1.png")
                            || img_name.equals("LightBank.png")
                            || img_name.equals("DarkBank.png")
                            || img_name.equals("DarkBankL.png")) {
                        t.canBeSteppedOn = !t.canBeSteppedOn;
                    }
                }
            }
        } else if (c == KeyEvent.VK_E) { // In Game Map Editor
            if (e.getModifiers() == InputEvent.CTRL_MASK) {
                EditLevel el = new EditLevel(level, Char.level, mtile);
                System.out.println("edit level");
            }
        } else if (c == KeyEvent.VK_U) {
            if (e.getModifiers() == InputEvent.CTRL_MASK)
                new UltimateEdit.SuperCommandLine();
        } else if (c == KeyEvent.VK_D) {
            if (e.getModifiers() == InputEvent.CTRL_MASK)
                delay = !delay;
        }

        if (chating) {
            if (c == KeyEvent.VK_BACK_SPACE) {
                chattemp = chattemp.substring(0, chattemp.length() - 1);
            } else if (65535 != (int) e.getKeyChar()
                    && 10 != (int) e.getKeyChar() && !e.isActionKey()) {
                chattemp += e.getKeyChar();
            }
        }

        if (overlay.o.active && !ja) {
            overlay.o.keyPressed(e);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (overlay == null || overlay.o == null)
            return;
        if (overlay.o.active) {
            overlay.o.keyReleased(e);
        }
        int c = e.getKeyCode();
        if (c == KeyEvent.VK_UP) {
            up = false;
        } else if (c == KeyEvent.VK_LEFT) {
            left = false;
        } else if (c == KeyEvent.VK_RIGHT) {
            right = false;
        } else if (c == KeyEvent.VK_DOWN) {
            down = false;
        }
    }

    public void keyTyped(KeyEvent e) {
    }

}
