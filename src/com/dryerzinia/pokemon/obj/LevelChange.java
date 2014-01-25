package com.dryerzinia.pokemon.obj;

import com.dryerzinia.pokemon.map.Direction;

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
	private Direction toLeaveDirection;
	private Direction newDirection;

	public LevelChange(int newX, int newY, int newLevel, Direction toLeaveDirection, Direction newDirection){

		this.newX = newX;
		this.newY = newY;
		this.newLevel = newLevel;
		this.toLeaveDirection = toLeaveDirection;
		this.newDirection = newDirection;

	}

	public boolean rightDirection(Direction direction){
		return direction == toLeaveDirection;
	}

	public Position getNewPosition(){
		return new Position(newX, newY, newLevel, newDirection);
	}

}
