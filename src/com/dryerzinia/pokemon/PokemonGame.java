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
import com.dryerzinia.pokemon.util.event.AbstractMenuListener;
import com.dryerzinia.pokemon.util.event.JXpad;

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

    public String controller = "/dev/js0";

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

    JXpad xp;

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

        basePokemon = new ArrayList<Pokemon>();

        Pokemon.BaseStats bs = new Pokemon.BaseStats();
        bs.baseExp = 208;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 3;
        bs.hp = 80;
        bs.attack = 82;
        bs.defense = 83;
        bs.special = 100;
        bs.speed = 80;
        bs.type = "Grass";
        bs.type2 = "Poison";
        bs.smallImageName = "Bulbasaur";
        Pokemon p2 = new Pokemon("Venasaur", "", "", 5, 21, 21, 10, 10, 10, 10,
                0, 3, 0, "", "", "", "", null, bs);
        p2.Species = "Venasaur";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 141;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 2;
        bs.hp = 60;
        bs.attack = 62;
        bs.defense = 63;
        bs.special = 80;
        bs.speed = 60;
        bs.evolvesTo = p2;
        bs.evolvesAt = 32;
        bs.type = "Grass";
        bs.type2 = "Poison";
        bs.smallImageName = "Bulbasaur";
        Pokemon p1 = new Pokemon("Ivysaur", "", "", 5, 21, 21, 10, 10, 10, 10,
                0, 2, 0, "", "", "", "", null, bs);
        p1.Species = "Ivysaur";
        basePokemon.add(p1);
        bs.baseExp = 64;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 1;
        bs.hp = 45;
        bs.attack = 49;
        bs.defense = 49;
        bs.special = 65;
        bs.speed = 45;
        bs.evolvesTo = p1;
        bs.evolvesAt = 16;
        bs.type = "Grass";
        bs.type2 = "Poison";
        bs.smallImageName = "Bulbasaur";
        Pokemon p = new Pokemon("Bulbasaur", "", "", 5, 21, 21, 10, 10, 10, 10,
                0, 1, 0, "", "", "", "", null, bs);
        p.Species = "Bulbasaur";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 209;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 3;
        bs.hp = 78;
        bs.attack = 84;
        bs.defense = 78;
        bs.special = 85;
        bs.speed = 100;
        bs.type = "Fire";
        bs.type2 = "Flying";
        bs.smallImageName = "";
        p2 = new Pokemon("Charzard", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 3,
                0, "", "", "", "", null, bs);
        p2.Species = "Charzard";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 142;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 5;
        bs.hp = 58;
        bs.attack = 64;
        bs.defense = 58;
        bs.special = 65;
        bs.speed = 80;
        bs.evolvesTo = p2;
        bs.evolvesAt = 32;
        bs.type = "Fire";
        bs.smallImageName = "";
        p1 = new Pokemon("Charmeleon", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 5,
                0, "", "", "", "", null, bs);
        p1.Species = "Charmeleon";
        basePokemon.add(p1);
        bs.baseExp = 65;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 4;
        bs.hp = 39;
        bs.attack = 52;
        bs.defense = 43;
        bs.special = 50;
        bs.speed = 65;
        bs.evolvesTo = p1;
        bs.evolvesAt = 16;
        bs.type = "Fire";
        bs.smallImageName = "";
        p = new Pokemon("Charmander", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 4,
                0, "", "", "", "", null, bs);
        p.Species = "Charmander";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 210;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 9;
        bs.hp = 79;
        bs.attack = 83;
        bs.defense = 100;
        bs.special = 85;
        bs.speed = 78;
        bs.type = "Water";
        bs.smallImageName = "";
        p2 = new Pokemon("Blastoise", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 9,
                0, "", "", "", "", null, bs);
        p2.Species = "Blastoise";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 143;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 8;
        bs.hp = 59;
        bs.attack = 63;
        bs.defense = 80;
        bs.special = 65;
        bs.speed = 58;
        bs.evolvesTo = p2;
        bs.evolvesAt = 32;
        bs.type = "Water";
        bs.smallImageName = "";
        p1 = new Pokemon("Wartortle", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 8,
                0, "", "", "", "", null, bs);
        p1.Species = "Wartortle";
        basePokemon.add(p1);
        bs.baseExp = 66;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 7;
        bs.hp = 44;
        bs.attack = 48;
        bs.defense = 65;
        bs.special = 50;
        bs.speed = 43;
        bs.evolvesTo = p1;
        bs.evolvesAt = 16;
        bs.type = "Water";
        bs.smallImageName = "";
        p = new Pokemon("Squirtle", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 4, 0,
                "", "", "", "", null, bs);
        p.Species = "Squirtle";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 160;
        bs.growthRate = 1;
        bs.rareness = 45;
        bs.no = 12;
        bs.hp = 60;
        bs.attack = 45;
        bs.defense = 50;
        bs.special = 80;
        bs.speed = 70;
        bs.type = "Bug";
        bs.type2 = "Flying";
        bs.smallImageName = "Caterpie";
        p2 = new Pokemon("Butterfree", "", "", 5, 21, 21, 10, 10, 10, 10, 0,
                17, 0, "", "", "", "", null, bs);
        p2.Species = "Butterfree";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 72;
        bs.growthRate = 1;
        bs.rareness = 120;
        bs.no = 11;
        bs.hp = 50;
        bs.attack = 20;
        bs.defense = 55;
        bs.special = 25;
        bs.speed = 30;
        bs.type = "Bug";
        bs.evolvesTo = p2;
        bs.evolvesAt = 10;
        bs.smallImageName = "Caterpie";
        p1 = new Pokemon("Metapod", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p1.Species = "Metapod";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 53;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 10;
        bs.hp = 45;
        bs.attack = 30;
        bs.defense = 35;
        bs.special = 20;
        bs.speed = 45;
        bs.type = "Bug";
        bs.evolvesTo = p1;
        bs.evolvesAt = 7;
        bs.smallImageName = "Caterpie";
        p = new Pokemon("Caterpie", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p.Species = "Caterpie";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 159;
        bs.growthRate = 1;
        bs.rareness = 45;
        bs.no = 15;
        bs.hp = 65;
        bs.attack = 80;
        bs.defense = 40;
        bs.special = 45;
        bs.speed = 75;
        bs.type = "Bug";
        bs.type2 = "Poison";
        bs.smallImageName = "Caterpie";
        p2 = new Pokemon("Beedrill", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p2.Species = "Beedrill";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 71;
        bs.growthRate = 1;
        bs.rareness = 120;
        bs.no = 14;
        bs.hp = 45;
        bs.attack = 25;
        bs.defense = 50;
        bs.special = 25;
        bs.speed = 35;
        bs.type = "Bug";
        bs.type2 = "Poison";
        bs.evolvesTo = p2;
        bs.evolvesAt = 10;
        bs.smallImageName = "Caterpie";
        p1 = new Pokemon("Kakuna", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19, 0,
                "", "", "", "", null, bs);
        p1.Species = "Kakuna";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 52;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 13;
        bs.hp = 40;
        bs.attack = 35;
        bs.defense = 30;
        bs.special = 20;
        bs.speed = 50;
        bs.type = "Bug";
        bs.type2 = "Poison";
        bs.evolvesTo = p1;
        bs.evolvesAt = 7;
        bs.smallImageName = "Caterpie";
        p = new Pokemon("Weedle", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19, 0,
                "", "", "", "", null, bs);
        p.Species = "Weedle";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 172;
        bs.growthRate = 1;
        bs.rareness = 45;
        bs.no = 18;
        bs.hp = 83;
        bs.attack = 80;
        bs.defense = 75;
        bs.special = 70;
        bs.speed = 91;
        bs.type = "Normal";
        bs.type2 = "Flying";
        bs.smallImageName = "Pidgey";
        p2 = new Pokemon("Pidgeot", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 18,
                0, "", "", "", "", null, bs);
        p2.Species = "Pidgeot";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 113;
        bs.growthRate = 3;
        bs.rareness = 120;
        bs.no = 17;
        bs.hp = 63;
        bs.attack = 60;
        bs.defense = 55;
        bs.special = 50;
        bs.speed = 71;
        bs.type = "Normal";
        bs.type2 = "Flying";
        bs.smallImageName = "Pidgey";
        p1 = new Pokemon("Pidgeotto", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 17,
                0, "", "", "", "", null, bs);
        p1.Species = "Pidgeotto";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 55;
        bs.growthRate = 3;
        bs.rareness = 255;
        bs.evolvesTo = p1;
        bs.evolvesAt = 18;
        bs.no = 16;
        bs.hp = 40;
        bs.attack = 45;
        bs.defense = 40;
        bs.special = 35;
        bs.speed = 56;
        bs.type = "Normal";
        bs.type2 = "Flying";
        bs.smallImageName = "Pidgey";
        p = new Pokemon("Pidgey", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 16, 0,
                "", "", "", "", null, bs);
        p.Species = "Pidgey";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 116;
        bs.growthRate = 1;
        bs.rareness = 90;
        bs.no = 20;
        bs.hp = 55;
        bs.attack = 81;
        bs.defense = 60;
        bs.special = 50;
        bs.speed = 97;
        bs.type = "Normal";
        bs.smallImageName = "Rattata";
        p = new Pokemon("Raticate", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p.Species = "Raticate";
        basePokemon.add(p);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 55;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 19;
        bs.hp = 30;
        bs.attack = 56;
        bs.defense = 35;
        bs.special = 25;
        bs.speed = 72;
        bs.type = "Normal";
        bs.evolvesTo = p1;
        bs.evolvesAt = 20;
        bs.smallImageName = "Rattata";
        p = new Pokemon("Rattata", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19, 0,
                "", "", "", "", null, bs);
        p.Species = "Rattata";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 58;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 22;
        bs.hp = 65;
        bs.attack = 90;
        bs.defense = 65;
        bs.special = 61;
        bs.speed = 100;
        bs.type = "Normal";
        bs.type2 = "Flying";
        bs.smallImageName = "Pidgey";
        p = new Pokemon("Fearow", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19, 0,
                "", "", "", "", null, bs);
        p.Species = "Fearow";
        basePokemon.add(p);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 58;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 21;
        bs.hp = 40;
        bs.attack = 60;
        bs.defense = 30;
        bs.special = 31;
        bs.speed = 70;
        bs.type = "Normal";
        bs.type2 = "Flying";
        bs.evolvesTo = p1;
        bs.evolvesAt = 20;
        bs.smallImageName = "Pidgey";
        p = new Pokemon("Spearow", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19, 0,
                "", "", "", "", null, bs);
        p.Species = "Spearow";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 147;
        bs.growthRate = 1;
        bs.rareness = 90;
        bs.no = 24;
        bs.hp = 60;
        bs.attack = 85;
        bs.defense = 69;
        bs.special = 65;
        bs.speed = 80;
        bs.type = "Poison";
        bs.smallImageName = "";
        p = new Pokemon("Arbok", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19, 0,
                "", "", "", "", null, bs);
        p.Species = "Arbok";
        basePokemon.add(p);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 62;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 23;
        bs.hp = 35;
        bs.attack = 60;
        bs.defense = 44;
        bs.special = 40;
        bs.speed = 55;
        bs.type = "Poison";
        bs.evolvesTo = p1;
        bs.evolvesAt = 22;
        bs.smallImageName = "";
        p = new Pokemon("Ekans", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 23, 0,
                "", "", "", "", null, bs);
        p.Species = "Ekans";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 122;
        bs.growthRate = 1;
        bs.rareness = 75;
        bs.no = 26;
        bs.hp = 60;
        bs.attack = 90;
        bs.defense = 55;
        bs.special = 90;
        bs.speed = 100;
        bs.type = "Eletric";
        bs.smallImageName = "";
        p2 = new Pokemon("Raichu", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 26, 0,
                "", "", "", "", null, bs);
        p2.Species = "Raichu";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 82;
        bs.growthRate = 1;
        bs.rareness = 190;
        bs.no = 25;
        bs.hp = 35;
        bs.attack = 55;
        bs.defense = 30;
        bs.special = 50;
        bs.speed = 90;
        bs.evolvesTo = p1;
        bs.evolvesAt = Item.THUNDER_STONE;
        bs.type = "Eletric";
        bs.smallImageName = "Pikachu";
        p = new Pokemon("Pikachu", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 25, 0,
                "", "", "", "", null, bs);
        p.Species = "Pikachu";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 163;
        bs.growthRate = 1;
        bs.rareness = 90;
        bs.no = 28;
        bs.hp = 75;
        bs.attack = 100;
        bs.defense = 110;
        bs.special = 55;
        bs.speed = 65;
        bs.type = "Ground";
        bs.smallImageName = "";
        p2 = new Pokemon("Sandslash", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 26,
                0, "", "", "", "", null, bs);
        p2.Species = "Sandslash";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 93;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 27;
        bs.hp = 50;
        bs.attack = 75;
        bs.defense = 85;
        bs.special = 30;
        bs.speed = 40;
        bs.evolvesTo = p1;
        bs.evolvesAt = 22;
        bs.type = "Ground";
        bs.smallImageName = "";
        p = new Pokemon("Sandshrew", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 27,
                0, "", "", "", "", null, bs);
        p.Species = "Sandshrew";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 194;
        bs.growthRate = 1;
        bs.rareness = 45;
        bs.no = 31;
        bs.hp = 90;
        bs.attack = 82;
        bs.defense = 87;
        bs.special = 75;
        bs.speed = 76;
        bs.type = "Poison";
        bs.type2 = "Ground";
        bs.smallImageName = "Mankey";
        p2 = new Pokemon("Nidoqueen", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 31,
                0, "", "", "", "", null, bs);
        p2.Species = "Nidoqueen";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 117;
        bs.growthRate = 3;
        bs.rareness = 120;
        bs.no = 30;
        bs.hp = 70;
        bs.attack = 62;
        bs.defense = 67;
        bs.special = 55;
        bs.speed = 56;
        bs.evolvesTo = p2;
        bs.evolvesAt = Item.MOON_STONE;
        bs.type = "Poison";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Nidorina", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 30,
                0, "", "", "", "", null, bs);
        p1.Species = "Nidorina";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 59;
        bs.growthRate = 3;
        bs.rareness = 235;
        bs.no = 29;
        bs.hp = 55;
        bs.attack = 47;
        bs.defense = 52;
        bs.special = 40;
        bs.speed = 41;
        bs.type = "Poison";
        bs.smallImageName = "Mankey";
        p = new Pokemon("NidoranF", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p.Species = "NidoranF";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 195;
        bs.growthRate = 1;
        bs.rareness = 45;
        bs.no = 34;
        bs.hp = 81;
        bs.attack = 92;
        bs.defense = 77;
        bs.special = 75;
        bs.speed = 85;
        bs.type = "Poison";
        bs.type2 = "Ground";
        bs.smallImageName = "Mankey";
        p2 = new Pokemon("Nidoking", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 34,
                0, "", "", "", "", null, bs);
        p2.Species = "Nidoking";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 118;
        bs.growthRate = 3;
        bs.rareness = 120;
        bs.no = 33;
        bs.hp = 61;
        bs.attack = 72;
        bs.defense = 57;
        bs.special = 55;
        bs.speed = 65;
        bs.evolvesTo = p2;
        bs.evolvesAt = Item.MOON_STONE;
        bs.type = "Poison";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Nidorino", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p1.Species = "Nidorino";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 60;
        bs.growthRate = 3;
        bs.rareness = 235;
        bs.no = 32;
        bs.hp = 46;
        bs.attack = 57;
        bs.defense = 40;
        bs.special = 40;
        bs.speed = 50;
        bs.evolvesTo = p1;
        bs.evolvesAt = 16;
        bs.type = "Poison";
        bs.smallImageName = "Mankey";
        p = new Pokemon("NidoranM", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p.Species = "NidoranM";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 129;
        bs.growthRate = 0;
        bs.rareness = 25;
        bs.no = 36;
        bs.hp = 95;
        bs.attack = 70;
        bs.defense = 73;
        bs.special = 85;
        bs.speed = 60;
        bs.type = "Normal";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Clefable", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 36,
                0, "", "", "", "", null, bs);
        p1.Species = "Clefable";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 68;
        bs.growthRate = 0;
        bs.rareness = 150;
        bs.no = 35;
        bs.hp = 70;
        bs.attack = 45;
        bs.defense = 48;
        bs.special = 60;
        bs.speed = 35;
        bs.evolvesTo = p1;
        bs.evolvesAt = Item.MOON_STONE;
        bs.type = "Normal";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Clefairy", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 35,
                0, "", "", "", "", null, bs);
        p.Species = "Clefairy";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 178;
        bs.growthRate = 1;
        bs.rareness = 75;
        bs.no = 38;
        bs.hp = 73;
        bs.attack = 76;
        bs.defense = 75;
        bs.special = 100;
        bs.speed = 100;
        bs.type = "Fire";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Ninetales", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 38,
                0, "", "", "", "", null, bs);
        p1.Species = "Ninetales";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 63;
        bs.growthRate = 2;
        bs.rareness = 190;
        bs.no = 37;
        bs.hp = 38;
        bs.attack = 41;
        bs.defense = 40;
        bs.special = 65;
        bs.speed = 65;
        bs.evolvesTo = p1;
        bs.evolvesAt = Item.FIRE_STONE;
        bs.type = "Fire";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Vulpix", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 37, 0,
                "", "", "", "", null, bs);
        p.Species = "Vulpix";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 109;
        bs.growthRate = 0;
        bs.rareness = 75;
        bs.no = 40;
        bs.hp = 140;
        bs.attack = 70;
        bs.defense = 75;
        bs.special = 45;
        bs.speed = 50;
        bs.type = "Normal";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Wigglytuff", "", "", 5, 21, 21, 10, 10, 10, 10, 0,
                40, 0, "", "", "", "", null, bs);
        p1.Species = "Wigglytuff";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 76;
        bs.growthRate = 0;
        bs.rareness = 170;
        bs.no = 37;
        bs.hp = 115;
        bs.attack = 45;
        bs.defense = 20;
        bs.special = 25;
        bs.speed = 20;
        bs.evolvesTo = p1;
        bs.evolvesAt = Item.MOON_STONE;
        bs.type = "Normal";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Jigglypuff", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 39,
                0, "", "", "", "", null, bs);
        p.Species = "Jigglypuff";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 171;
        bs.growthRate = 1;
        bs.rareness = 90;
        bs.no = 42;
        bs.hp = 75;
        bs.attack = 80;
        bs.defense = 70;
        bs.special = 75;
        bs.speed = 90;
        bs.type = "Poison";
        bs.type2 = "Flying";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Golbat", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 42, 0,
                "", "", "", "", null, bs);
        p1.Species = "Golbat";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 54;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 41;
        bs.hp = 40;
        bs.attack = 45;
        bs.defense = 35;
        bs.special = 40;
        bs.speed = 55;
        bs.evolvesTo = p1;
        bs.evolvesAt = 22;
        bs.type = "Poison";
        bs.type2 = "Flying";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Zubat", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 41, 0,
                "", "", "", "", null, bs);
        p.Species = "Zubat";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 184;
        bs.growthRate = 3;
        bs.rareness = 45;
        bs.no = 45;
        bs.hp = 75;
        bs.attack = 80;
        bs.defense = 85;
        bs.special = 100;
        bs.speed = 50;
        bs.type = "Grass";
        bs.type2 = "Poison";
        bs.smallImageName = "Mankey";
        p2 = new Pokemon("Vileplume", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 45,
                0, "", "", "", "", null, bs);
        p2.Species = "Vileplume";
        basePokemon.add(p2);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 132;
        bs.growthRate = 3;
        bs.rareness = 120;
        bs.no = 44;
        bs.hp = 60;
        bs.attack = 65;
        bs.defense = 70;
        bs.special = 85;
        bs.speed = 40;
        bs.type = "Grass";
        bs.type2 = "Poison";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Gloom", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 44, 0,
                "", "", "", "", null, bs);
        p1.Species = "Gloom";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 78;
        bs.growthRate = 3;
        bs.rareness = 255;
        bs.no = 43;
        bs.hp = 45;
        bs.attack = 50;
        bs.defense = 55;
        bs.special = 75;
        bs.speed = 30;
        bs.evolvesTo = p1;
        bs.evolvesAt = 21;
        bs.type = "Grass";
        bs.type2 = "Poison";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Oddish", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 43, 0,
                "", "", "", "", null, bs);
        p.Species = "Oddish";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 128;
        bs.growthRate = 1;
        bs.rareness = 75;
        bs.no = 47;
        bs.hp = 60;
        bs.attack = 95;
        bs.defense = 80;
        bs.special = 80;
        bs.speed = 30;
        bs.type = "Bug";
        bs.type2 = "Grass";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Parasect", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 47,
                0, "", "", "", "", null, bs);
        p1.Species = "Parasect";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 70;
        bs.growthRate = 1;
        bs.rareness = 190;
        bs.no = 46;
        bs.hp = 35;
        bs.attack = 70;
        bs.defense = 55;
        bs.special = 55;
        bs.speed = 25;
        bs.evolvesTo = p1;
        bs.evolvesAt = 24;
        bs.type = "Bug";
        bs.type2 = "Grass";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Paras", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 46, 0,
                "", "", "", "", null, bs);
        p.Species = "Paras";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 138;
        bs.growthRate = 1;
        bs.rareness = 75;
        bs.no = 49;
        bs.hp = 70;
        bs.attack = 65;
        bs.defense = 60;
        bs.special = 90;
        bs.speed = 90;
        bs.type = "Bug";
        bs.type2 = "Grass";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Venomoth", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 49,
                0, "", "", "", "", null, bs);
        p1.Species = "Venomoth";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 75;
        bs.growthRate = 1;
        bs.rareness = 190;
        bs.no = 47;
        bs.hp = 60;
        bs.attack = 55;
        bs.defense = 50;
        bs.special = 40;
        bs.speed = 45;
        bs.evolvesTo = p1;
        bs.evolvesAt = 31;
        bs.type = "Bug";
        bs.type2 = "Grass";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Venonat", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 46, 0,
                "", "", "", "", null, bs);
        p.Species = "Venonat";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 153;
        bs.growthRate = 1;
        bs.rareness = 50;
        bs.no = 51;
        bs.hp = 35;
        bs.attack = 80;
        bs.defense = 50;
        bs.special = 70;
        bs.speed = 120;
        bs.type = "Ground";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Dugtrio", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 51,
                0, "", "", "", "", null, bs);
        p1.Species = "Dugtrio";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 81;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 50;
        bs.hp = 10;
        bs.attack = 55;
        bs.defense = 25;
        bs.special = 45;
        bs.speed = 95;
        bs.evolvesTo = p1;
        bs.evolvesAt = 26;
        bs.type = "Ground";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Diglett", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 50, 0,
                "", "", "", "", null, bs);
        p.Species = "Diglett";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 148;
        bs.growthRate = 1;
        bs.rareness = 75;
        bs.no = 53;
        bs.hp = 65;
        bs.attack = 70;
        bs.defense = 60;
        bs.special = 65;
        bs.speed = 115;
        bs.type = "Normal";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Persian", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 53,
                0, "", "", "", "", null, bs);
        p1.Species = "Persian";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 69;
        bs.growthRate = 1;
        bs.rareness = 255;
        bs.no = 52;
        bs.hp = 40;
        bs.attack = 45;
        bs.defense = 35;
        bs.special = 40;
        bs.speed = 90;
        bs.evolvesTo = p1;
        bs.evolvesAt = 28;
        bs.type = "Normal";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Meowth", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 52, 0,
                "", "", "", "", null, bs);
        p.Species = "Meowth";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 159;
        bs.growthRate = 1;
        bs.rareness = 75;
        bs.no = 55;
        bs.hp = 80;
        bs.attack = 82;
        bs.defense = 78;
        bs.special = 80;
        bs.speed = 85;
        bs.type = "Water";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Golduck", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 55,
                0, "", "", "", "", null, bs);
        p1.Species = "Golduck";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 80;
        bs.growthRate = 1;
        bs.rareness = 190;
        bs.no = 54;
        bs.hp = 50;
        bs.attack = 52;
        bs.defense = 48;
        bs.special = 50;
        bs.speed = 55;
        bs.evolvesTo = p1;
        bs.evolvesAt = 33;
        bs.type = "Water";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Psyduck", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 54, 0,
                "", "", "", "", null, bs);
        p.Species = "Psyduck";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 149;
        bs.growthRate = 1;
        bs.rareness = 75;
        bs.no = 57;
        bs.hp = 65;
        bs.attack = 105;
        bs.defense = 60;
        bs.special = 60;
        bs.speed = 95;
        bs.type = "Fighting";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Primeape", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p1.Species = "Primeape";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 74;
        bs.growthRate = 1;
        bs.rareness = 190;
        bs.no = 56;
        bs.hp = 40;
        bs.attack = 80;
        bs.defense = 35;
        bs.special = 35;
        bs.speed = 70;
        bs.evolvesTo = p1;
        bs.evolvesAt = 28;
        bs.type = "Fighting";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Mankey", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19, 0,
                "", "", "", "", null, bs);
        p.Species = "Mankey";
        basePokemon.add(p);

        bs = new Pokemon.BaseStats();
        bs.baseExp = 213;
        bs.growthRate = 2;
        bs.rareness = 75;
        bs.no = 59;
        bs.hp = 90;
        bs.attack = 110;
        bs.defense = 80;
        bs.special = 80;
        bs.speed = 95;
        bs.type = "Fire";
        bs.smallImageName = "Mankey";
        p1 = new Pokemon("Arcanine", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p1.Species = "Arcanine";
        basePokemon.add(p1);
        bs = new Pokemon.BaseStats();
        bs.baseExp = 91;
        bs.growthRate = 2;
        bs.rareness = 190;
        bs.no = 58;
        bs.hp = 55;
        bs.attack = 70;
        bs.defense = 45;
        bs.special = 50;
        bs.speed = 60;
        bs.evolvesTo = p1;
        bs.evolvesAt = Item.FIRE_STONE;
        bs.type = "Fire";
        bs.smallImageName = "Mankey";
        p = new Pokemon("Growlithe", "", "", 5, 21, 21, 10, 10, 10, 10, 0, 19,
                0, "", "", "", "", null, bs);
        p.Species = "Growlithe";
        basePokemon.add(p);

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
        load(new File("saveReg.dat"), true);

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

            FileOutputStream fos = new FileOutputStream(f);
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
            oos.close();

        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    public void load(File f, boolean local) {

        try {

            mtile = new ArrayList<Tile>();
            level = new ArrayList<Level>();

            ObjectInputStream ois;
            
            System.out.println("f.getName(): "+ f.getCanonicalPath());
            
            if (local) {
                ois = new ObjectInputStream(getClass().getClassLoader()
                        .getResource(f.getName()).openStream()); // ??? WTH IS
                                                                 // LOCAL
            } else {
                FileInputStream fis = new FileInputStream(f);
                ois = new ObjectInputStream((InputStream) fis);
            }
            int l = ois.readInt();
            for (int i = 0; i < l; i++) {
                try {
                    Tile t = (Tile) ois.readObject();
                    mtile.add(t);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
            numberOfLevels = ois.readInt();
            for (int i = 0; i < numberOfLevels; i++) {
                level.add((Level) ois.readObject());
            }

        } catch (Exception x) {
            x.printStackTrace();
        }

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

    public void writeSettingsData(File f) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(f));
            oos.writeObject(username);
            oos.writeObject(password);
            oos.writeObject(location);
        } catch (Exception x) {
            System.out.println("Failed to write settings data.");
        }
    }

    public void readSettingsData(File f) {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(f));
            username = (String) ois.readObject();
            password = (String) ois.readObject();
            location = (String) ois.readObject();
        } catch (Exception x) {
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
