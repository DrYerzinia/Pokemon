/**
 * The Entry point for the PokemonGame application.  A configuration file should
 * be saved in the same folder as the JAR named PokemonGame.json with the default
 * username password location and transport mode (TCP, UDP) to populate in the
 * Login menu.
 * 
 * TODO fucking seperate NET CODE, GAME STATE CODE, UI CODE into seperate SINGLETONS
 * 
 * @author DrYerzinia <dryerzinia@gmail.com>
 */

package com.dryerzinia.pokemon;

import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.util.Timer;

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
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Move;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokeball;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.obj.Tile;
import com.dryerzinia.pokemon.ui.Login;
import com.dryerzinia.pokemon.ui.Overlay;
import com.dryerzinia.pokemon.ui.OverlayO;
import com.dryerzinia.pokemon.ui.PokemonView;
import com.dryerzinia.pokemon.ui.UI;
import com.dryerzinia.pokemon.ui.editor.AddLevel;
import com.dryerzinia.pokemon.ui.editor.EditLevel;
import com.dryerzinia.pokemon.ui.editor.MapEditor;
import com.dryerzinia.pokemon.ui.editor.Sub;
import com.dryerzinia.pokemon.ui.editor.SubListener;
import com.dryerzinia.pokemon.ui.editor.UltimateEdit;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.ui.menu.ItemMenu;
import com.dryerzinia.pokemon.ui.menu.MenuEvent;
import com.dryerzinia.pokemon.ui.menu.MoneyMenu;
import com.dryerzinia.pokemon.ui.menu.SelectionMenu;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.event.AbstractMenuListener;

import java.awt.event.*;
import java.applet.*;

public class PokemonGame {

    public boolean delay = true;

    private boolean wait = false;

    private int z = 0;
    private int x = 0;

    private boolean modif = false;

    private boolean jmoved = false;
    private boolean lvlchn = false;

    private int changeLevel = -1;
    private int clx = -1;
    private int cly = -1;

    private int nextLevel[] = null;

    public int numberOfLevels = 7;

    private static Timer game_loop_timer;

    public static void init() {

        Pokemon.readPokemonBaseStats();

        GameState.init();
        ClientState.init();

    }
    
    public static void stopGameLoop(){

    	game_loop_timer.cancel();
    	game_loop_timer.purge();

    }

    public static void startGameLoop(){

    	game_loop_timer = new Timer();
        game_loop_timer.schedule(new GameLoop(), 0, 150);    	

    }

    public static void switchPokemon(Pokemon p[], int i, int j) {
        Pokemon p1 = p[i];
        p[i] = p[j];
        p[j] = p1;
        p[i].location = i;
        p[j].location = j;
    }

    public static final class GameLoop extends TimerTask {

    	public void run() {

                Graphics g = getGraphics();

                if (overlay.o.active) {

                    overlay.o.draw(bg);

                } else {

                    if (Player.self != null && level.get(Player.self.level).midmove) {

                        if (Player.self.dir == 0) {
                            level.get(Player.self.level).moveUp();
                        } else if(Player.self.dir == 1) {
                            level.get(Player.self.level).moveDown();
                        } else if(Player.self.dir == 2) {
                            level.get(Player.self.level).moveLeft();
                        } else if(Player.self.dir == 3) {
                            level.get(Player.self.level).moveRight();
                        }

                        try {
                            writePlayer();
                        } catch (Exception x) {
                            // TODO: MAKE SURE THIS IS FULL PROOF
                            if (!reconnecting)
                                (new ReconnectThread()).start();
                        }

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

            }
    }

    public void changeLevel() {
        changeLevel = nextLevel[0];
        clx = nextLevel[1];
        cly = nextLevel[2];
        if (nextLevel[3] != -1)
            Player.self.dir = nextLevel[3];
        jmoved = false;
        lvlchn = true;
        nextLevel = null;
        System.out.println("PokemonGame::ChangeLevel() called");
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
    public static void main(String[] args) {

        Applet pg = new PokemonGame();

        JFrame frame = new JFrame("Pokemon");
        frame.getContentPane().add(pg);
        frame.addWindowListener((WindowListener) pg);
        frame.setSize(UI.APP_WIDTH * UI.scale + 10, UI.APP_HEIGHT * UI.scale + UI.CHAT_HEIGHT
                + 30);
        frame.setVisible(true);

        pg.init();
    }

}
