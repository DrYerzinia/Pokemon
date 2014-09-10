package com.dryerzinia.pokemon.obj;

import java.util.concurrent.ConcurrentHashMap;

import com.dryerzinia.pokemon.input.InputController;
import com.dryerzinia.pokemon.input.KeyboardInputController;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.util.string.StringStore.Locale;

public class ClientState {

	public static Player player;
    public static ConcurrentHashMap<Integer, Player> players;

    public static InputController inputDevice;

    public static final Locale LOCALE = Locale.EN;

    private static boolean loaded;

    public static void init(){

    	loaded = false;

		players = new ConcurrentHashMap<Integer, Player>();

		inputDevice = new KeyboardInputController();

	}

    public static boolean isLoaded(){
    	return loaded;
    }

    public static void setLoaded(){
    	loaded = true;
    }

    public static Level getPlayerLevel(){

    	return GameState.getMap().getLevel(player.getPose().getLevel());

    }

}
