package com.dryerzinia.pokemon.ui.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.logging.Logger;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.ui.UI;
import com.dryerzinia.pokemon.util.ResourceLoader;

public class Login implements View, KeyListener {

	private static final Logger LOG =
			Logger.getLogger(Login.class.getName());


	public static final byte NORMAL = 0;
	public static final byte ATTEMPT_LOGIN = 1;
	public static final byte CONNECTION_ERROR = 2;
	public static final byte RECONNECT = 3;

	public static final byte USERNAME = 0;
	public static final byte PASSWORD = 1;
	public static final byte LOCATION = 2;
	public static final byte LOGIN = 3;

    private byte state;
    private byte selection;

    public Login(){

    	state = NORMAL;
    	selection = USERNAME;

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

        graphics.drawString("Username: " + ClientState.username, 10, 40);
        graphics.setColor(Color.WHITE);

        if (selection == 1)
        	graphics.setColor(Color.BLUE);

        String s = "";
        for (int i = 0; i < ClientState.password.length(); i++)
            s += "*";
        graphics.drawString("Password: " + s, 10, 60);
        graphics.setColor(Color.WHITE);

        if (selection == 2)
        	graphics.setColor(Color.BLUE);

        graphics.drawString("Location:", 10, 80);

        if (ClientState.location.length() < 9)
        	graphics.drawString(ClientState.location, 80, 80);

        else {

            int start = 9;

            graphics.drawString(ClientState.location.substring(0, 9), 80, 80);

            int i = 1;
            while (start < ClientState.location.length()) {
                int end = start + 20;
                if (end > ClientState.location.length())
                    end = ClientState.location.length();
                graphics.drawString(ClientState.location.substring(start, end), 10, 80 + 20 * i);
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

            	ClientState.saveSettings();

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
                    ClientState.username = ClientState.username.substring(0, ClientState.username.length() - 1);
                    break;
                case PASSWORD:
                	ClientState.password = ClientState.password.substring(0, ClientState.password.length() - 1);
                    break;
                case LOCATION:
                	ClientState.location = ClientState.location.substring(0, ClientState.location.length() - 1);
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
                	ClientState.username += d;
                    break;
                case 1:
                	ClientState.password += d;
                    break;
                case 2:
                	ClientState.location += d;
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
