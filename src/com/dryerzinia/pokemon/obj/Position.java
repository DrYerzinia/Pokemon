package com.dryerzinia.pokemon.obj;

import java.io.Serializable;

public class Position implements Serializable {

	private static final long serialVersionUID = 5663646265119040753L;

	private int x;
	private int y;

	private int level;

	public Position(int x, int y, int level){

		this.x = x;
		this.y = y;

		this.level = level;

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

}
