package com.dryerzinia.pokemon.ui.views;

import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.dryerzinia.pokemon.input.KeyboardInputController;
import com.dryerzinia.pokemon.input.InputController;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.server.PlayerPositionMessage;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.tiles.OnClickTile;
import com.dryerzinia.pokemon.obj.tiles.Person;
import com.dryerzinia.pokemon.obj.tiles.Tile;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.ui.menu.MenuStack;

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

		Pose updated = ClientState.player.update(ClientState.inputDevice.direction(), deltaTime);

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

		if(MenuStack.isEmpty() && ClientState.inputDevice.isButtonDown(InputController.Button.A)){

			Direction dir = ClientState.player.getPose().facing();
			int px = Math.round(ClientState.player.getPose().getX());
			int py = Math.round(ClientState.player.getPose().getY());
			ArrayList<Tile> tiles = null;

			switch(dir){
				case UP:
					tiles = ClientState.getPlayerLevel().grid.grid[px][py - 1];
					break;
				case DOWN:
					tiles = ClientState.getPlayerLevel().grid.grid[px][py + 1];
					break;
				case LEFT:
					tiles = ClientState.getPlayerLevel().grid.grid[px - 1][py];
					break;
				case RIGHT:
					tiles = ClientState.getPlayerLevel().grid.grid[px + 1][py];
					break;
			}

			if(tiles != null){
				Iterator<Tile> it = tiles.iterator();
				while(it.hasNext()){

					Tile t = it.next();
					if(t instanceof OnClickTile){
						((OnClickTile) t).click();
						break;
					}

				}
			}

		}

		/*
		 * Process menu related input if no animations are running
		 */
		MenuStack.handleInput();
		MenuStack.update(deltaTime);

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

		MenuStack.render(graphics);

	}

	@Override
	public KeyListener getKeyListener() {

		if(ClientState.inputDevice instanceof KeyboardInputController)
			return (KeyListener) ClientState.inputDevice;

		return null;

	}

}
