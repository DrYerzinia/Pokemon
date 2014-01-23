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

import java.util.*;
import java.util.Timer;

import javax.swing.*;

import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.ui.UI;

public class PokemonGame {

    private static Timer game_loop_timer;

    public static void init() {

        Pokemon.readPokemonBaseStats();

        GameState.init();
        ClientState.init();

        UI.init();

        UI.addKeyListener(ClientState.getKeyboard());

        // start login window

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

    	private static final int loopDelay = 50;

    	private Timer self;

    	private long lastUpdateTime;
 
    	/**
    	 * Initialize the game loop
    	 * or reset it if it was already running
    	 */
    	public void init(){

    		/*
    		 * Make sure we don't run this twice!
    		 */
  			stop();

    		/*
    		 * Set update time so it doesn't seem like we blasted a long ass
    		 * time into the future on the first iteration
    		 */
    		updateTime();

    		/*
    		 * Create the timer and start the game loop
    		 */
    		self = new Timer();
    		self.schedule(this, 0, loopDelay);

    	}

    	/**
    	 * Stop the game loop if it is running
    	 */
    	public void stop(){
 
    		if(self != null){

    			self.cancel();
    			self.purge();
    			self = null;

    		}

    	}

    	/**
    	 * Set the last update time to the current update time
    	 */
    	private void updateTime(){

    		lastUpdateTime = System.currentTimeMillis();    		

    	}

    	/**
    	 * One iteration of the game loop
    	 */
    	public void run() {

    		int deltaTime = (int)(System.currentTimeMillis() - lastUpdateTime);

    		/*
    		 * Update any objects that would have changed
    		 * The player
    		 * Other players they might have received position updates and need
    		 * to work there way across the map
    		 */
    		ClientState.player.update(ClientState.getKeyboard().direction(), deltaTime);

    		/*
    		 * Process menu related input if no animations are running
    		 */

    		/*
    		 * Render all the objects on the map
    		 * Render the player, he is not references in the map
    		 * Render the chat window
    		 */

    		/*
    		 * Now is the last time we updated
    		 */
    		updateTime();

    	}
/*
    	Graphics g = getGraphics();

                if (overlay.o.active) {

                	// Draw code
                    overlay.o.draw(bg);

                } else {

                	// mid-move code
                    if (ClientState.player != null && level.get(ClientState.player.level).midmove) {

                        if (ClientState.player.dir == 0) {
                            level.get(ClientState.player.level).moveUp();
                        } else if(ClientState.player.dir == 1) {
                            level.get(ClientState.player.level).moveDown();
                        } else if(ClientState.player.dir == 2) {
                            level.get(ClientState.player.level).moveLeft();
                        } else if(ClientState.player.dir == 3) {
                            level.get(ClientState.player.level).moveRight();
                        }

                        Client.writePlayer();

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

                    // if we dident do the last thing
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
                    // so if we are mid moving are we mid moving to a new level???
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

                    //select code
                    // heal
                    // 
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

                    // Draw level, other players, self, menus, jump bolders
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

                        // draw menus
                        if (currMenu != null)
                            currMenu.draw(bg);
                        if (healMenu != null && healMenuActive)
                            healMenu.draw(bg);
                        if (startMenuActive)
                            startMenu.draw(bg);

                        // jump boulders
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

                //shopping menu draw 
                if (moneyMenu != null && moneyMenuActive) {
                    moneyMenu.draw(bg);
                    shoppingMainMenu.draw(bg);
                }

                UI.drawChat();

                g.drawImage(bi, 0, 0, 320, 288, 0, 0, 160, 144, null);
                g.drawImage(cbi, 0, 288, null);

                g.dispose();

            }
            */
    }

    public static void main(String[] args) {

        PokemonGame.init();

        JFrame frame = new JFrame("Pokemon");

        UI.addToContainer(frame.getContentPane());
        UI.addAsWindowListener(frame);

        frame.setSize(UI.APP_WIDTH * UI.scale + 10, UI.APP_HEIGHT * UI.scale + UI.CHAT_HEIGHT + 30);
        frame.setVisible(true);

    }

}
