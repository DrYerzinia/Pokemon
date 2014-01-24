package com.dryerzinia.pokemon.obj;

public class LevelChange {

	/*
	 * New level you will be in once you go though the change
	 * X Position you will be in in new level
	 * Y Position you will be in in new level
	 * Direction you have to be facing to leave
	 * Direction you will be facing once you leave
	 */
	private int newLevel;
	private int newX;
	private int newY;
	private int toLeaveDirection;
	private int newDirection;

	public LevelChange(int newX, int newY, int newLevel, int toLeaveDirection, int newDirection){

		this.newX = newX;
		this.newY = newY;
		this.newLevel = newLevel;
		this.toLeaveDirection = toLeaveDirection;
		this.newDirection = newDirection;

	}

	public boolean rightDirection(int direction){
		return direction == toLeaveDirection;
	}

	public Position getNewPosition(){
		return new Position(newX, newY, newLevel, newDirection);
	}

}
