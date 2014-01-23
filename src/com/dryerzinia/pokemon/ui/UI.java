package com.dryerzinia.pokemon.ui;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
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
import java.util.TimerTask;

import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.ui.editor.MapEditor;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;

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

    public static OverlayO overlay = new OverlayO();

    /*
     * Chat var's
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
        }

        @Override
        public void keyReleased(KeyEvent e) {
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

    public static void drawChat(){

    	chat_buffer_graphics.setColor(Color.BLACK);
    	chat_buffer_graphics.fillRect(0, 0, 320, 100);

    	chat_buffer_graphics.setColor(Color.WHITE);
    	chat_buffer_graphics.drawString(chat_entry, 10, 12);

        int j = 0;
        for (int i = chat_history.size() - 1; i > chat_history.size() - 7; i--) {

        	if (i < 0)
                break;

            String s = chat_history.get(i);
            if (s.indexOf("whisper") == s.indexOf(" ") + 1)
            	chat_buffer_graphics.setColor(Color.BLUE);

            else
            	chat_buffer_graphics.setColor(Color.WHITE);

            chat_buffer_graphics.drawString(s, 10, 27 + 15 * j);

            j++;

        }
    }

    /**
     * Write buffered image to the screen
     */
    private static void drawBuffer(){

    	Graphics g = ui_applet.getGraphics();
        g.drawImage(buffer_image, 0, 0, 320, 288, 0, 0, 160, 144, null);
        g.dispose();    	

    }

    public static void addToContainer(Container contentPane){

    	contentPane.add(ui_applet);

    }

    public static void addAsWindowListener(Window window){

    	window.addWindowListener((WindowListener) ui_applet);

    }

    public static void enableInput(){

    	ui_applet.addKeyListener((KeyListener) ui_applet);

    }

    public static void disableInput(){

    	ui_applet.removeKeyListener((KeyListener) ui_applet);

    }

    public static void addKeyListener(KeyListener keyListener){

    	ui_applet.addKeyListener(keyListener);

    }

    public static void initloginScreen() {

        Login l = new Login();
        overlay.o = l;
    }
	
}
