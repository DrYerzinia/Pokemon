package com.dryerzinia.pokemon.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
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

import javax.swing.JPanel;

import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.ui.editor.MapEditor;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.views.Login;
import com.dryerzinia.pokemon.views.View;

@SuppressWarnings("serial")
public class UI {

    public static final int APP_WIDTH = 160;
    public static final int APP_HEIGHT = 144;
    public static final int CHAT_HEIGHT = 100;

    /*
     * Amount to scale the Applet/JFrame
     */
    public static int scale = 2;

    private static Canvas uiCanvas;

    public static MapEditor me;

    private static BufferStrategy bufferStrategy;

    public static OverlayO overlay = new OverlayO();

    public static float animationTimeStep = 250;

    /*
     * Chat var's
     */
    public static boolean chating = false;
    private static String chat_entry;
    private static ArrayList<String> chat_history;

    private UI(){}
    
    public static void init(Container container){

    	uiCanvas = new Canvas();
    	uiCanvas.setSize(getWidth(), getTotalHeight());
    	container.add(uiCanvas);

    	/*
    	 * Create Double Buffering for drawing the game
    	 */
    	uiCanvas.createBufferStrategy(2);
    	bufferStrategy = uiCanvas.getBufferStrategy();

        /*
         * Set up chat buffer variables
         */
        chat_entry = "";
        chat_history = new ArrayList<String>();

    }

    public static void draw(View view){

    	Graphics graphics = bufferStrategy.getDrawGraphics();

    	BufferedImage bufferImage = new BufferedImage(APP_WIDTH, APP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    	Graphics bufferGraphics = bufferImage.getGraphics();

    	
    	/*
    	 * Clear buffer to black
    	 */
    	bufferGraphics.setColor(Color.BLACK);
    	bufferGraphics.fillRect(0, 0, UI.getWidth(), UI.getTotalHeight());

    	view.draw(bufferGraphics);

    	graphics.drawImage(bufferImage, 0, 0, APP_WIDTH*scale, APP_HEIGHT*scale, 0, 0, APP_WIDTH, APP_HEIGHT, null);
    	drawChat(graphics);

    	graphics.dispose();
    	bufferStrategy.show();

    }

    public static int getWidth(){

    	return APP_WIDTH * scale;

    }

    public static int getTotalHeight(){

    	return APP_HEIGHT * scale + CHAT_HEIGHT * scale;

    }

    public static void drawChat(Graphics graphics){

    	graphics.setColor(Color.WHITE);
    	graphics.drawString(chat_entry, 10, 12);

        int j = 0;
        for (int i = chat_history.size() - 1; i > chat_history.size() - 7; i--) {

        	if (i < 0)
                break;

            String s = chat_history.get(i);
            if (s.indexOf("whisper") == s.indexOf(" ") + 1)
            	graphics.setColor(Color.BLUE);

            else
            	graphics.setColor(Color.WHITE);

            graphics.drawString(s, 10, 27 + 15 * j);

            j++;

        }
    }

    public static synchronized void addChatMessage(String message){

    	chat_history.add(message);

    }

    public static void addKeyListener(KeyListener keyListener){

    	uiCanvas.addKeyListener(keyListener);

    }

    public static void removeKeyListener(KeyListener keyListener){

    	uiCanvas.removeKeyListener(keyListener);

    }
	
}
