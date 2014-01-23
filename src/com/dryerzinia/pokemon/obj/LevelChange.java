package com.dryerzinia.pokemon.obj;

public class LevelChange {

	/*
	 * New level you will be in once you go though the change
	 * X Position you will be in in new level
	 * Y Position you will be in in new level
	 * Direction you have to be facing to leave
	 * Direction you will be facing once you leave
	 */
	int newLevel;
	int newX;
	int newY;
	int toLeaveDirection;
	int newDirection;

	public LevelChange(int newX, int newY, int newLevel, int toLeaveDirection, int newDirection){

		this.newX = newX;
		this.newY = newY;
		this.newLevel = newLevel;
		this.toLeaveDirection = toLeaveDirection;
		this.newDirection = newDirection;

	}

}
