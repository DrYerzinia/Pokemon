package com.dryerzinia.pokemon.obj;

import java.util.ArrayList;

import com.dryerzinia.pokemon.input.KeyboardInputController;

public class ClientState {

	public static Player player;
    public static ArrayList<Player> players;

    private static KeyboardInputController keyboard;

    public static void init(){

		players = new ArrayList<Player>();

		keyboard = new KeyboardInputController();

	}

    public static KeyboardInputController getKeyboard(){

    	return keyboard;

    }

}
