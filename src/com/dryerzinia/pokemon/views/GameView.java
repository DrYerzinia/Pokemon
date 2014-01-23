package com.dryerzinia.pokemon.views;

import java.awt.Graphics;
import java.awt.event.KeyListener;

import com.dryerzinia.pokemon.obj.ClientState;

public class GameView implements View {

	public GameView(){
		//
	}

	/**
	 * Update any objects that would have changed
	 * The player
	 * Other players they might have received position updates and need
	 * to work there way across the map
	 */
	@Override
	public void update(int deltaTime) {

		if(ClientState.isLoaded())
			ClientState.player.update(ClientState.getKeyboard().direction(), deltaTime);

		/*
		 * Process menu related input if no animations are running
		 */
		
	}

	/**
	 * Render all the objects on the map
	 * Render the player, he is not references in the map
	 * Render the chat window
	 */
	@Override
	public void draw(Graphics graphics) {

		if(ClientState.isLoaded()){
			ClientState.getPlayerLevel().draw(graphics, ClientState.player.x, ClientState.player.y);;
			ClientState.player.draw(graphics);
		}

	}

	@Override
	public KeyListener getKeyListener() {

		return ClientState.getKeyboard();

	}

}
