package com.dryerzinia.pokemon.input;

import com.dryerzinia.pokemon.map.Direction;

public interface InputController {

	/**
	 * Direction the controller in indicating to go in
	 * @return byte {@link com.dryerzinia.pokemon.map.Direction} Enum value
	 * indicating direction to go in
	 */
	public Direction direction();

	/**
	 * Add a button listener so we can fire button down events for the
	 * A, B, Start and Directional buttons
	 */
	public void addButtonListener(ButtonListener buttonListener);

	/**
	 * Remove a button listener so it no longer receives button events
	 */
	public void removeButtonListener(ButtonListener buttonListener);

}
