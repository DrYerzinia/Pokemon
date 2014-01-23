package com.dryerzinia.pokemon.views;

import java.awt.Graphics;
import java.awt.event.KeyListener;

public interface View {

	/**
	 * Make any necessary updates
	 * @param deltaTime Change in time since last update
	 */
	public void update(int deltaTime);

	/**
	 * Draws the view
	 * 
	 * @param graphics Graphics object to draw to
	 */
	public void draw(Graphics graphics);

	/**
	 * Gets to KeyListener to bind to UI's
	 * @return Views KeyListener
	 */
	public KeyListener getKeyListener();

}
