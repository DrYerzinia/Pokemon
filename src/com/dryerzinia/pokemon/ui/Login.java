package com.dryerzinia.pokemon.ui;
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

import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.util.JSONObject;

public class Login extends Overlay {

	/*
	 * Global login information
	 */
    public static String username = "";
    public static String password = "";
    public static String location = "";

    int selection = 0;

    public Login(){

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

    public void draw(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, UI.APP_HEIGHT, UI.APP_WIDTH + UI.CHAT_HEIGHT);

        g.setColor(Color.WHITE);

        g.setFont(new Font("monospaced", 0, 16));
        g.drawString("LOGIN", 50, 20);

        g.setFont(new Font("monospaced", 0, 12));

        if (selection == 0)
            g.setColor(Color.BLUE);
        g.drawString("Username: " + username, 10, 40);
        g.setColor(Color.WHITE);

        if (selection == 1)
            g.setColor(Color.BLUE);

        String s = "";
        for (int i = 0; i < password.length(); i++)
            s += "*";
        g.drawString("Password: " + s, 10, 60);
        g.setColor(Color.WHITE);

        if (selection == 2)
            g.setColor(Color.BLUE);

        g.drawString("Location:", 10, 80);

        if (location.length() < 9)
            g.drawString(location, 80, 80);

        else {

            int start = 9;

            g.drawString(location.substring(0, 9), 80, 80);

            int i = 1;
            while (start < location.length()) {
                int end = start + 20;
                if (end > location.length())
                    end = location.length();
                g.drawString(location.substring(start, end), 10, 80 + 20 * i);
                i++;
                start += 20;
            }

        }

        g.setColor(Color.WHITE);
        if (selection == 3)
            g.setColor(Color.BLUE);
        g.drawString("GO", 75, 140);

    }

    public void set(Overlay o) {
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

    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();
        if (c == KeyEvent.VK_UP) {
            selection--;
            if (selection == -1)
                selection = 3;
        } else if (c == KeyEvent.VK_LEFT) {
        } else if (c == KeyEvent.VK_RIGHT) {
        } else if (c == KeyEvent.VK_DOWN) {
            selection++;
            if (selection == 4)
                selection = 0;
        } else if (c == KeyEvent.VK_ENTER) {
            selection++;
            if (selection == 4) {
                active = false;
                selection = 0;
                Client.attemptLogin();
            }
        } else if (c == KeyEvent.VK_BACK_SPACE) {
            try {
                switch (selection) {
                case 0:
                    username = username.substring(0, username.length() - 1);
                    break;
                case 1:
                    password = password.substring(0, password.length() - 1);
                    break;
                case 2:
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

        UI.redrawLogin();
    
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}
