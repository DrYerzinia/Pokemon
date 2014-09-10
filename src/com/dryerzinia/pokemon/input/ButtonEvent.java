package com.dryerzinia.pokemon.input;

import com.dryerzinia.pokemon.input.InputController.Button;

public class ButtonEvent {

	/*
	 * Which button was pressed by this event
	 */
	private Button which;

	/*
	 * Create new button event specifying which button
	 * was pressed
	 */
	public ButtonEvent(Button which){

		this.which = which;

	}

	/*
	 * Get which button was pressed
	 */
	public Button which(){

		return which;

	}

}
