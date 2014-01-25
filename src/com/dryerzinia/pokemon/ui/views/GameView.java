package com.dryerzinia.pokemon.ui.views;

import java.awt.Graphics;
import java.awt.event.KeyListener;

import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Person;

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

		ClientState.player.update(ClientState.getKeyboard().direction(), deltaTime);

		for(Actor actor : GameState.actors)
			actor.update(deltaTime);
	

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

		ClientState.getPlayerLevel().draw(graphics, ClientState.player.x, ClientState.player.y);

		for(Actor actor : GameState.actors){
			Person person = (Person) actor;
			if(person.level == ClientState.player.level)
				person.draw(0, 0, 0, 0, graphics);
		}
	
		ClientState.player.draw(graphics);

	}

	@Override
	public KeyListener getKeyListener() {

		return ClientState.getKeyboard();

	}

}
