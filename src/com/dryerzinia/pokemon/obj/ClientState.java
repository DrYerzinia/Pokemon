package com.dryerzinia.pokemon.obj;

import java.util.ArrayList;

import com.dryerzinia.pokemon.input.KeyboardInputController;
import com.dryerzinia.pokemon.map.Level;

public class ClientState {

	public static Player player;
    public static ArrayList<Player> players;

    private static KeyboardInputController keyboard;

    private static boolean loaded;

    public static void init(){

    	loaded = false;

		players = new ArrayList<Player>();

		keyboard = new KeyboardInputController();

	}

    public static boolean isLoaded(){
    	return loaded;
    }

    public static void setLoaded(){
    	loaded = true;
    }

    public static Level getPlayerLevel(){

    	return GameState.getMap().getLevel(player.getLocation().getLevel());

    }

    public static KeyboardInputController getKeyboard(){

    	return keyboard;

    }

}
