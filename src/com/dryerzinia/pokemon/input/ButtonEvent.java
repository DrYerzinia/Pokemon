package com.dryerzinia.pokemon.input;

public class ButtonEvent {

	/*
	 * Button definitions
	 */
	public static final byte A = 0;
	public static final byte B = 1;
	public static final byte START = 2;

	/*
	 * Directional button definitions
	 */
	public static final byte UP = 3;
	public static final byte DOWN = 4;
	public static final byte LEFT = 5;
	public static final byte RIGHT = 6;

	/*
	 * Which button was pressed by this event
	 */
	private byte which;

	/*
	 * Create new button event specifying which button
	 * was pressed
	 */
	public ButtonEvent(byte which){

		this.which = which;

	}

	/*
	 * Get which button was pressed
	 */
	public byte getWhich(){

		return which;

	}

}
