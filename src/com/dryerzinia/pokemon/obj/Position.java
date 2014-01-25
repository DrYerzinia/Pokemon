package com.dryerzinia.pokemon.obj;

import java.io.Serializable;

import com.dryerzinia.pokemon.map.Direction;

public class Position implements Serializable {

	private static final long serialVersionUID = 5663646265119040753L;

	private int x;
	private int y;

	private int level;

	private Direction facing;

	public Position(int x, int y, int level, Direction facing){

		this.x = x;
		this.y = y;

		this.level = level;

		this.facing = facing;

	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public int getLevel(){
		return level;
	}

	public Direction getFacing(){
		return facing;
	}

}
