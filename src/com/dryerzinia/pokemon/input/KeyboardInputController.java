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

	private boolean aPressed;
	private boolean bPressed;
	private boolean startPressed;
	private boolean selectPressed;

	private int aKey		= KeyEvent.VK_Z;
	private int bKey		= KeyEvent.VK_X;
	private int selectKey	= KeyEvent.VK_BACK_SPACE;
	private int startKey	= KeyEvent.VK_ENTER;
	private int upKey		= KeyEvent.VK_UP;
	private int downKey		= KeyEvent.VK_DOWN;
	private int leftKey		= KeyEvent.VK_LEFT;
	private int rightKey	= KeyEvent.VK_RIGHT;

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
	public boolean isButtonDown(Button b) {
		switch(b){
			case A:
				return aPressed;
			case B:
				return bPressed;
			case START:
				return startPressed;
			case SELECT:
				return selectPressed;
			case UP:
				return upPressed;
			case DOWN:
				return downPressed;
			case LEFT:
				return leftPressed;
			case RIGHT:
				return rightPressed;
		}
		return false;
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

        if(c == upKey){
           	upPressed = true;
           	for(ButtonListener buttonListener : buttonListeners){
           		buttonListener.buttonDown(new ButtonEvent(Button.UP));
           	}
        } else if(c == leftKey){
           	leftPressed = true;
           	for(ButtonListener buttonListener : buttonListeners){
           		buttonListener.buttonDown(new ButtonEvent(Button.LEFT));
       		}
        } else if(c == rightKey){
           	rightPressed = true;
           	for(ButtonListener buttonListener : buttonListeners)
           		buttonListener.buttonDown(new ButtonEvent(Button.RIGHT));
        } else if(c == downKey){
           	downPressed = true;
           	for(ButtonListener buttonListener : buttonListeners)
           		buttonListener.buttonDown(new ButtonEvent(Button.DOWN));
        } else if(c == aKey){
           	aPressed = true;
           	for(ButtonListener buttonListener : buttonListeners)
           		buttonListener.buttonDown(new ButtonEvent(Button.A));
        } else if(c == bKey){
           	bPressed = true;
           	for(ButtonListener buttonListener : buttonListeners)
           		buttonListener.buttonDown(new ButtonEvent(Button.B));
        } else if(c == startKey){
           	startPressed = true;
           	for(ButtonListener buttonListener : buttonListeners)
           		buttonListener.buttonDown(new ButtonEvent(Button.START));
        } else if(c == selectKey){
           	selectPressed = true;
           	for(ButtonListener buttonListener : buttonListeners)
           		buttonListener.buttonDown(new ButtonEvent(Button.SELECT));
        }

        updateOnlyPressed();

    }

    @Override
    public void keyReleased(KeyEvent e) {

        int c = e.getKeyCode();

        if(c == upKey)
            upPressed = false;

        else if(c == leftKey)
            leftPressed = false;

        else if(c == rightKey)
            rightPressed = false;

        else if(c == downKey)
            downPressed = false;

        else if(c == aKey)
        	aPressed = false;

        else if(c == bKey)
        	bPressed = false;

        else if(c == startKey)
        	startPressed = false;

        else if(c == selectKey)
        	selectPressed = false;

        updateOnlyPressed();

    }

    @Override
    public void keyTyped(KeyEvent e){}

}
