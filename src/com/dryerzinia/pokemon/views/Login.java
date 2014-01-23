package com.dryerzinia.pokemon.views;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.ui.Overlay;
import com.dryerzinia.pokemon.ui.UI;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;

public class Login implements View, KeyListener {

	public static final byte NORMAL = 0;
	public static final byte ATTEMPT_LOGIN = 1;
	public static final byte CONNECTION_ERROR = 2;
	public static final byte RECONNECT = 3;

	public static final byte USERNAME = 0;
	public static final byte PASSWORD = 1;
	public static final byte LOCATION = 2;
	public static final byte LOGIN = 3;

	/*
	 * Global login information
	 */
    public static String username = "";
    public static String password = "";
    public static String location = "";

    private byte state;
    private byte selection;

    public Login(){

    	state = NORMAL;
    	selection = USERNAME;

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

    /**
     * Empty because there are no time based updates
     * for this view
     */
	@Override
	public void update(int deltaTime) {}

	/**
	 * Draws login window depending on current state
	 */
	@Override
    public void draw(Graphics graphics) {

		switch(state){
		case NORMAL:
			drawNormal(graphics);
			break;
		case ATTEMPT_LOGIN:
			drawAttemptingLogin(graphics);
			break;
		case CONNECTION_ERROR:
			drawConnectionError(graphics);
			break;
		case RECONNECT:
			drawReconnect(graphics);
			break;
		}

    }

	/**
	 * This class is its own KeyListener
	 */
	@Override
	public KeyListener getKeyListener() {

		return this;

	}

	public void drawNormal(Graphics graphics){

		graphics.setColor(Color.BLACK);
    	graphics.fillRect(0, 0, UI.getWidth(), UI.getTotalHeight());

    	graphics.setColor(Color.WHITE);

    	graphics.setFont(new Font("monospaced", 0, 16));
    	graphics.drawString("LOGIN", 50, 20);

    	graphics.setFont(new Font("monospaced", 0, 12));

        if (selection == 0)
        	graphics.setColor(Color.BLUE);

        graphics.drawString("Username: " + username, 10, 40);
        graphics.setColor(Color.WHITE);

        if (selection == 1)
        	graphics.setColor(Color.BLUE);

        String s = "";
        for (int i = 0; i < password.length(); i++)
            s += "*";
        graphics.drawString("Password: " + s, 10, 60);
        graphics.setColor(Color.WHITE);

        if (selection == 2)
        	graphics.setColor(Color.BLUE);

        graphics.drawString("Location:", 10, 80);

        if (location.length() < 9)
        	graphics.drawString(location, 80, 80);

        else {

            int start = 9;

            graphics.drawString(location.substring(0, 9), 80, 80);

            int i = 1;
            while (start < location.length()) {
                int end = start + 20;
                if (end > location.length())
                    end = location.length();
                graphics.drawString(location.substring(start, end), 10, 80 + 20 * i);
                i++;
                start += 20;
            }

        }

        graphics.setColor(Color.WHITE);
        if (selection == 3)
        	graphics.setColor(Color.BLUE);
        graphics.drawString("GO", 75, 140);

	}

	public void drawReconnect(Graphics graphics){

    	graphics.setColor(Color.BLACK);
    	graphics.fillRect(0, 0, UI.APP_HEIGHT, UI.APP_WIDTH + UI.CHAT_HEIGHT);

    	graphics.setColor(Color.RED);
    	graphics.drawString("Lost Connection", 25, 50);
    	graphics.drawString("Attempting Reconnect...", 10, 90);


    }

    public void drawAttemptingLogin(Graphics graphics){
    
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 160, 144);

        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("monospaced", 0, 16));
        graphics.drawString("LOGGING IN...", 20, 50);
        graphics.drawImage(ResourceLoader.getSprite("PikachuP.png"), 40, 55, null);


    }

    public void drawConnectionError(Graphics graphics){

    	graphics.setColor(Color.BLACK);
    	graphics.fillRect(0, 0, 160, 144);
    	graphics.setColor(Color.WHITE);
    	graphics.setFont(new Font("monospaced", 0, 16));
    	graphics.drawString("CONNECTION ERROR", 1, 50);
    	graphics.drawImage(ResourceLoader.getSprite("PikachuSad.png"), 50, 35, null);
    	graphics.drawString("Press Any Key To", 1, 100);
    	graphics.drawString("Restart", 50, 120);


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

	@Override
    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();

        if(state == ATTEMPT_LOGIN || state == RECONNECT) return;

        if(state == CONNECTION_ERROR){
        	if(c == KeyEvent.VK_ENTER) state = NORMAL;
        	return;
        }

        if (c == KeyEvent.VK_UP) {
            selection--;
            if (selection == -1)
                selection = LOCATION;
        } else if (c == KeyEvent.VK_DOWN) {
            selection++;
            if (selection == 4)
                selection = 0;
        } else if (c == KeyEvent.VK_ENTER) {
            selection++;
            if (selection >= LOGIN) {

            	selection = NORMAL;

            	writeSettingsData(Login.getSettingsFile());

            	state = ATTEMPT_LOGIN;

            	// CONNECT
            	try {

            		Client.startConnect();
            		PokemonGame.switchToGame();

            	} catch(IOException ioe){

            		System.err.println("Connection Failed: " + ioe.getMessage());
            		state = CONNECTION_ERROR;
            	}

            }
        } else if (c == KeyEvent.VK_BACK_SPACE) {
            try {
                switch (selection) {
                case USERNAME:
                    username = username.substring(0, username.length() - 1);
                    break;
                case PASSWORD:
                    password = password.substring(0, password.length() - 1);
                    break;
                case LOCATION:
                    location = location.substring(0, location.length() - 1);
                    break;
                }
            } catch (Exception x) {
            }
        } else {
            if (65535 != (int) e.getKeyChar() && 10 != (int) e.getKeyChar()
                    && !e.isActionKey()) {
                char d = e.getKeyChar();
                switch (selection) {
                case 0:
                    username += d;
                    break;
                case 1:
                    password += d;
                    break;
                case 2:
                    location += d;
                    break;
                }
            }
        }
    
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
