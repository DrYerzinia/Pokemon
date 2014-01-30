package com.dryerzinia.pokemon.ui.views;

import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.io.IOException;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.server.PlayerPositionMessage;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Player;

public class GameView implements View {

	public GameView(){
		//
	}

	/**
	 * Update any objects that would have changed
	 * the player
	 * Other players they might have received position updates and need
	 * to work there way across the map
	 */
	@Override
	public void update(int deltaTime) {

		if(!ClientState.isLoaded()) return;

		Pose updated = ClientState.player.update(ClientState.getKeyboard().direction(), deltaTime);

		if(updated != null){

			/*
    		 * Update server to our new position
    		 */
    		try {
				Client.writeServerMessage(new PlayerPositionMessage(updated));
    		} catch (IOException e) {
				System.out.println("Failed to update server on our position: " + e.getMessage());
			}

		}

		/*
		 * Runs update method on players and people in current level and
		 * adjacent levels
		 */
		ClientState.getPlayerLevel().update(deltaTime);

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

		if(!ClientState.isLoaded()) return;

		/*
		 * Draws the level and the characters in it
		 */
		ClientState.getPlayerLevel().draw(graphics, ClientState.player.getPose().getX() - 4, ClientState.player.getPose().getY() - 4);
	
		ClientState.player.draw(graphics);

	}

	@Override
	public KeyListener getKeyListener() {

		return ClientState.getKeyboard();

	}

}
