package com.dryerzinia.pokemon.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import com.dryerzinia.pokemon.map.Direction;

public class KeyboardInputController implements InputController, KeyListener {

	/*
	 * Last key to be the only one that was pressed
	 */
	private Direction onlyPressed;

	/*
	 * States of the directional keys
	 */
	private boolean upPressed;
	private boolean downPressed;
	private boolean leftPressed;
	private boolean rightPressed;

	/*
	 * Button listeners to fire button events to
	 */
	ArrayList<ButtonListener> buttonListeners;

	/**
	 * Initialize new KeyboardInputController
	 */
	public KeyboardInputController(){

		buttonListeners = new ArrayList<ButtonListener>();

	}

	/*
	 * Return the direction the keyboard in indicating to go in the case of
	 * multiple keys down it sends the last key to be the only one pressed
	 */
	@Override
	public Direction direction() {

		return onlyPressed;

	}

	@Override
	public void addButtonListener(ButtonListener buttonListener) {

		buttonListeners.add(buttonListener);

	}

	@Override
	public void removeButtonListener(ButtonListener buttonListener){

		buttonListeners.remove(buttonListener);

	}

	/*
	 * Update the onlyPressed variable if this direction is the only
	 * one currently down
	 */
	private void updateOnlyPressed(){

		if(upPressed && !downPressed && !leftPressed && !rightPressed)
			onlyPressed = Direction.UP;

		else if(!upPressed && downPressed && !leftPressed && !rightPressed)
			onlyPressed = Direction.DOWN;

		else if(!upPressed && !downPressed && leftPressed && !rightPressed)
			onlyPressed = Direction.LEFT;

		else if(!upPressed && !downPressed && !leftPressed && rightPressed)
			onlyPressed = Direction.RIGHT;

		else if(!upPressed && !downPressed && !leftPressed && !rightPressed)
			onlyPressed = Direction.NONE;

	}

	@Override
    public void keyPressed(KeyEvent e) {

        int c = e.getKeyCode();

        if(c == KeyEvent.VK_UP){
        	upPressed = true;
        	for(ButtonListener buttonListener : buttonListeners)
        		buttonListener.buttonDown(new ButtonEvent(ButtonEvent.UP));
        }

        if(c == KeyEvent.VK_LEFT){
        	leftPressed = true;
        	for(ButtonListener buttonListener : buttonListeners)
        		buttonListener.buttonDown(new ButtonEvent(ButtonEvent.LEFT));
        }

        if(c == KeyEvent.VK_RIGHT){
        	rightPressed = true;
        	for(ButtonListener buttonListener : buttonListeners)
        		buttonListener.buttonDown(new ButtonEvent(ButtonEvent.RIGHT));
        }

        if(c == KeyEvent.VK_DOWN){
        	downPressed = true;
        	for(ButtonListener buttonListener : buttonListeners)
        		buttonListener.buttonDown(new ButtonEvent(ButtonEvent.DOWN));
        }

        if(c == KeyEvent.VK_Z)
        	for(ButtonListener buttonListener : buttonListeners)
        		buttonListener.buttonDown(new ButtonEvent(ButtonEvent.A));

        if(c == KeyEvent.VK_X)
        	for(ButtonListener buttonListener : buttonListeners)
        		buttonListener.buttonDown(new ButtonEvent(ButtonEvent.B));

        if(c == KeyEvent.VK_ENTER)
        	for(ButtonListener buttonListener : buttonListeners)
        		buttonListener.buttonDown(new ButtonEvent(ButtonEvent.START));

        updateOnlyPressed();

    }

    @Override
    public void keyReleased(KeyEvent e) {

        int c = e.getKeyCode();

        if(c == KeyEvent.VK_UP)
            upPressed = false;

        if(c == KeyEvent.VK_LEFT)
            leftPressed = false;

        if(c == KeyEvent.VK_RIGHT)
            rightPressed = false;

        if(c == KeyEvent.VK_DOWN)
            downPressed = false;

        updateOnlyPressed();

    }

    @Override
    public void keyTyped(KeyEvent e){}

}
