package com.dryerzinia.pokemon.ui;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;

import javax.swing.JFileChooser;

import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokeball;
import com.dryerzinia.pokemon.obj.Tile;
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
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.util.event.AbstractMenuListener;

@SuppressWarnings("serial")
public class UI {

    public static final int APP_WIDTH = 160;
    public static final int APP_HEIGHT = 144;
    public static final int CHAT_HEIGHT = 100;

    private static Applet ui_applet;

    public static MapEditor me;

    private static Image buffer_image;
    private static Graphics buffer_graphics;

    private static Image chat_buffer_image;
    private static Graphics chat_buffer_graphics;

    /* Replace with keyboard key state HashMap
     */
    private static boolean left = false;
    private static boolean right = false;
    private static boolean up = false;
    private static boolean down = false;
    
    private static boolean healMenuActive = false;
    private static boolean healing = false;
    private static boolean healcancel = false;
    private static SelectionMenu healMenu;

    private static boolean moneyQuiting = false;
    private static boolean moneyMenuActive = false;
    private static boolean shopping = false;
    private static MoneyMenu moneyMenu;
    private static SelectionMenu shoppingMainMenu;

    private static GMenu currMenu = null;

    public static ArrayList<Item> shopitems;

    private static SelectionMenu startMenu = null;

    public static OverlayO overlay = new OverlayO();

    private static boolean startMenuActive = false;

    /*
     * Chat vars
     */
    public static boolean chating = false;
    private static String chat_entry;
    private static ArrayList<String> chat_history;

    /*
     * Amount to scale the Applet/JFrame
     */
    public static int scale = 2;

    private UI(){}

    private static final class UIApplet extends Applet implements WindowListener, KeyListener {

    	public UIApplet(){}
 
    	/**
    	 * Draw buffer image to the screen
    	 */
    	public void paint(Graphics g) {
    		g.drawImage(buffer_image, 0, 0, 320, 288, 0, 0, 160, 144, null);
    		g.dispose();
    	}

        @Override
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
                }
            } else if (c == KeyEvent.VK_ENTER) {
                if (e.isShiftDown()) {
                    chating = !chating;
                } else {
                    if (chat_entry.length() > 0 && chating) {
                        Client.writeMessage(chat_entry);
                        String s = new String(chat_entry);
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
                            s2 += Player.self.name + ": " + s;
                        }
                        chat_history.add(s2);
                        chat_entry = "";
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
                }
                if (moneyMenuActive)
                    shoppingMainMenu.pushB();
            } else if (c == KeyEvent.VK_S) {
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        GameState.save(fc.getSelectedFile());
                    }

                }
            } else if (c == KeyEvent.VK_L) {
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                    	// Stop game loop
                    	GameState.load(fc.getSelectedFile().getPath());
                    	// redo init
                    	//start game loop
                    }

                }
            } else if (c == KeyEvent.VK_M) { // In Game Map Editor
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    if (me == null) {
                        me = new MapEditor();
                        addMouseListener(me);
                    }
                }
            } else if (c == KeyEvent.VK_K) { // In Game1 Level Adder
                if (e.getModifiers() == InputEvent.CTRL_MASK) {
                    new AddLevel();
                }
            } else if (c == KeyEvent.VK_J) { // Initialize Jesus Powers
                if (e.getModifiers() == InputEvent.CTRL_MASK) {

                    for(Tile tile : GameState.mtile) {

                        String img_name = tile.getImageName();
                        if(img_name.equals("Water1.png")
                        || img_name.equals("LightBank.png")
                        || img_name.equals("DarkBank.png")
                        || img_name.equals("DarkBankL.png")){
                            tile.canBeSteppedOn = !tile.canBeSteppedOn;
                        }
                    }
                }
            } else if (c == KeyEvent.VK_E) { // In Game Map Editor
                    new EditLevel();
            } else if (c == KeyEvent.VK_U) {
                if (e.getModifiers() == InputEvent.CTRL_MASK)
                    new UltimateEdit.SuperCommandLine();
            } else if (c == KeyEvent.VK_D) {
                if (e.getModifiers() == InputEvent.CTRL_MASK);
                   // Set loop delay on or off
            }

            if (chating) {
                if (c == KeyEvent.VK_BACK_SPACE) {
                    chat_entry = chat_entry.substring(0, chat_entry.length() - 1);
                } else if (65535 != (int) e.getKeyChar()
                        && 10 != (int) e.getKeyChar() && !e.isActionKey()) {
                    chat_entry += e.getKeyChar();
                }
            }

            if (overlay.o.active && !ja) {
                overlay.o.keyPressed(e);
            }
        }

        @Override
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

        @Override
        public void keyTyped(KeyEvent e){}

    	@Override
        public void windowClosed(WindowEvent e){}

    	@Override
        public void windowClosing(WindowEvent e){

    		Client.writeLogoff();
            System.exit(0);

    	}

    	@Override
        public void windowActivated(WindowEvent e){}

    	@Override
        public void windowDeactivated(WindowEvent e){}

    	@Override
        public void windowIconified(WindowEvent e){}

    	@Override
        public void windowDeiconified(WindowEvent e){}

    	@Override
        public void windowOpened(WindowEvent e){}

    }
    
    public static void init(){

    	ui_applet = new UIApplet();

    	/*
    	 * Create Double Buffering for drawing the game
    	 */
    	buffer_image = ui_applet.createImage(320, 388);
        buffer_graphics = buffer_image.getGraphics();

        chat_buffer_image = ui_applet.createImage(320, 200);
        chat_buffer_graphics = chat_buffer_image.getGraphics();

        /*
         * Set up chat buffer variables
         */
        chat_entry = "";
        chat_history = new ArrayList<String>();

        /*
         * Attach key listener to applet
         */
        enableInput();

        /*
         * Init game menus
         */
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

        		if (e.isLast() || e.getButton() == MenuEvent.X) {

        			if (!currMenu.message.equals("Thank You!\n ")) {

        				currMenu = new GMenu("Thank You!\n ", 0, 6, 10, 3);
                        moneyQuiting = true;

                        Iterator<Item> item_iterator = Player.self.items.iterator();

                        while(item_iterator.hasNext()) {

                        	Item item = item_iterator.next();

                        	if (item.number == 0)
                                item_iterator.remove();

                        	Client.writeItem(item);

                        }

        			} else {

        				moneyMenuActive = false;
                        currMenu = null;
                        moneyQuiting = false;

        			}

        		} else {

        			currMenu = new GMenu("Take your time.\n ", 0, 6, 10, 3);

        		}

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

        startMenu = new SelectionMenu(
                "POKeMON\nITEM\nNAME\nSAVE\nOPTION\nEXIT", 5, 0, 5, 7, 1);

        Overlay ol[] = new Overlay[6];

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

    }

    public static void drawReconnect(){

    	buffer_graphics.setColor(Color.BLACK);
    	buffer_graphics.fillRect(0, 0, APP_HEIGHT, APP_WIDTH + CHAT_HEIGHT);

    	buffer_graphics.setColor(Color.RED);
    	buffer_graphics.drawString("Lost Connection", 25, 50);
    	buffer_graphics.drawString("Attempting Reconnect...", 10, 90);

        drawBuffer();

    }

    public static void redrawLogin(){

        overlay.o.draw(buffer_graphics);

        drawBuffer();
    }

    public static void drawAttemptingLogin(){
    
        buffer_graphics.setColor(Color.BLACK);
        buffer_graphics.fillRect(0, 0, 160, 144);
        buffer_graphics.setColor(Color.WHITE);
        buffer_graphics.setFont(new Font("monospaced", 0, 16));
        buffer_graphics.drawString("LOGGING IN...", 20, 50);
        buffer_graphics.drawImage(ResourceLoader.getSprite("PikachuP.png"), 40, 55, null);

        drawBuffer();

        Player.self.name = Login.username;

        writeSettingsData(getSettingsFile());

    }

    public static void drawConnectionError(){

    	buffer_graphics.setColor(Color.BLACK);
    	buffer_graphics.fillRect(0, 0, 160, 144);
    	buffer_graphics.setColor(Color.WHITE);
    	buffer_graphics.setFont(new Font("monospaced", 0, 16));
    	buffer_graphics.drawString("CONNECTION ERROR", 1, 50);
    	buffer_graphics.drawImage(ResourceLoader.getSprite("PikachuSad.png"), 50, 35, null);
    	buffer_graphics.drawString("Press Any Key To", 1, 100);
    	buffer_graphics.drawString("Restart", 50, 120);

    	drawBuffer();

    }
    
    /**
     * Write buffered image to the 
     */
    private static void drawBuffer(){
        Graphics g = ui_applet.getGraphics();
        g.drawImage(buffer_image, 0, 0, 320, 288, 0, 0, 160, 144, null);
        g.dispose();    	
    }

    public static void enableInput(){

    	ui_applet.addKeyListener((KeyListener) ui_applet);

    }

    public static void disableInput(){

    	ui_applet.removeKeyListener((KeyListener) ui_applet);

    }

    public static void writeSettingsData(File f) {

    	try (BufferedWriter json_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)))) {

        	json_writer.write("{'username':'" + Login.username + "','password':'" + Login.password + "','location':'" + Login.location + "'}");
        	json_writer.flush();
        	json_writer.close();

    	} catch (IOException ioe) {

    		System.out.println("Error: Failed to write settings data!");

    	}
    }

    public static void readSettingsData(File f) {

    	try (BufferedReader json_reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
        	
        	String json = json_reader.readLine();

        	@SuppressWarnings("unchecked")
			HashMap<String, Object> json_obj = (HashMap<String, Object>) JSONObject.JSONToObject(json);

        	Login.username = (String) json_obj.get("username");
            Login.password = (String) json_obj.get("password");
            Login.location = (String) json_obj.get("location");

        } catch (IOException ioe) {

        	System.out.println("Failed to read settings data.");

        }
    }

    public static File getSettingsFile() {

    	String fs = System.getProperty("file.separator");

    	return new File(System.getProperty("user.home") + fs + ".pokemonData"
                + fs + "defaultSettings");
    }

    public static void initloginScreen() {

        Login l = new Login();

        overlay.o = l;

        String fs = System.getProperty("file.separator");
        File f = new File(System.getProperty("user.home") + fs + ".pokemonData");

        if(!f.exists())
        	f.mkdir();

        f = getSettingsFile();
        
        if(!f.exists()) {

        	try {

        		f.createNewFile();
        		writeSettingsData(f);

        	} catch(IOException ioe){

        		System.err.println("Coulden't create settings file: " + ioe.getMessage());

        	}

        } else
        	readSettingsData(f);

    }

    public static final class DoLogin extends TimerTask {
    	public void run(){
            try {

            	drawAttemptingLogin();

                Client.startConnect();
            	
            } catch (Exception x) {
                x.printStackTrace();
                System.out.println("Connection Error!");

                drawConnectionError();

                overlay.o.active = true;

            }
    	}
	}

    public static void setupMenus() {

        startMenu.submenus[1] = new ItemMenu(Player.self.items, 2, 1, 8, 5, 1);
        moneyMenu = new MoneyMenu(Player.self);
        shoppingMainMenu.submenus[0] = new ItemMenu(shopitems, 2, 1, 8, 5, 1);
        shoppingMainMenu.submenus[1] = new ItemMenu(Player.self.items, 2, 1, 8, 5, 1);
        ItemMenu buymenu = (ItemMenu) shoppingMainMenu.submenus[0];
        ItemMenu sellmenu = (ItemMenu) shoppingMainMenu.submenus[1];
        buymenu.p = Player.self;
        sellmenu.p = Player.self;
        buymenu.prices = true;

        buymenu.addMenuListener(new AbstractMenuListener() {

        	public void MenuPressed(MenuEvent e) {
            
        		if((e.isLast() && e.getButton() == MenuEvent.Z)
                 || e.getButton() == MenuEvent.X)
        			currMenu = new GMenu("Is there anything\nelse I can do?", 0, 6, 10, 3);

            }
        });

        sellmenu.addMenuListener(new AbstractMenuListener() {
            public void MenuPressed(MenuEvent e) {

                if((e.isLast() && e.getButton() == MenuEvent.Z)
                 || e.getButton() == MenuEvent.X)
                	currMenu = new GMenu("Is there anything\nelse I can do?", 0, 6, 10, 3);

            }
        });
    }
    
    /**
     * Stop healing pokemon at pokecenter
     */
    private static void healCancel() {
        healcancel = true;
        healing = false;
        healMenuActive = false;
        currMenu = new GMenu("We hope to see\nyou again!", 0, 6, 10, 3);
    }

    /**
     * Heal pokemon at pokecenter
     * TODO Obviously we need to inform the server of this
     */
    private static void heal() {

    	/*
    	 * Update last poke center
    	 */
    	Player.self.lpcx = Player.self.x;
    	Player.self.lpcy = Player.self.y;
    	Player.self.lpclevel = Player.self.level;

        /*
         * Annoying state bools
         * TODO REPLACE THIS FUCKING SHIT
         */
        healcancel = true;
        healing = false;
        healMenuActive = false;
        currMenu = new GMenu(
                "Ok we'll need\nyour POKeMON.\n  \nThank you!\nYour POKeMON are\nfighting fit!",
                0, 6, 10, 3);
        /*
         * Actually heal the fucking pokemon
         */
        healp();
        /*
         * Bullshit update to the server that it SHOLDENT TRUST AT ALL!!!
         * TODO fucking replace this shit
         */
        for (int i = 0; i < 6; i++) {
            if (Player.self.poke.belt[i] == null)
                break;
            Player.self.poke.belt[i].location = i;
            Client.writePokemon(Player.self.poke.belt[i]);
        }
        /*
         * Why the fuck am I updating items in heal function
         * this is just fucking stupid server should be informed
         * when they are used so it has chance to call
         * BULLSHIT and kick client
         * TODO ^^^ FIX THAT SHIT
         */
        Iterator<Item> iti = Player.self.items.iterator();
        while (iti.hasNext()) {
            Item ite = iti.next();
            if (ite.number == 0)
                iti.remove();
            Client.writeItem(ite);
        }
    }

    /**
     * Heal the pokemon and refresh there move PP
     */
    private static void healp() {
        for (int i = 0; i < 6; i++) {
            if (Player.self.poke.belt[i] == null)
                break;
            Player.self.poke.belt[i].currentHP = Player.self.poke.belt[i].getTotalHP();
            for (int j = 0; j < 4; j++) {
                if (Player.self.poke.belt[i].moves[j] == null)
                    break;
                Player.self.poke.belt[i].moves[j].currentpp = Player.self.poke.belt[i].moves[j].pp;
            }
        }
    }
	
}
