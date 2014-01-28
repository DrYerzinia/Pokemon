package com.dryerzinia.pokemon.map;

import java.io.Serializable;

public class Position implements Serializable {

	private static final long serialVersionUID = 5663646265119040753L;

	/*
	 * The place that logged of clients get sent to
	 */
	public static final Position NOWHERE_LAND = new Position(0, 0, -1, Direction.NONE);

	private float x;
	private float y;

	private int level;

	private Direction facing;

	public Position(float x, float y, int level, Direction facing){

		this.x = x;
		this.y = y;

		this.level = level;

		this.facing = facing;

	}

	public Position copy(){
		return new Position(x, y, level, facing);
	}

	public float getX(){
		return x;
	}

	public float getY(){
		return y;
	}

	public int getLevel(){
		return level;
	}

	public Direction facing(){
		return facing;
	}

	public void addX(float value){
		x += value;
	}

	public void addY(float value){
		y += value;
	}

	public void subX(float value){
		x -= value;
	}

	public void subY(float value){
		y -= value;
	}

	public void changeDirection(Direction newDirection){
		facing = newDirection;
	}

	public void set(Position position){

		this.x = position.x;
		this.y = position.y;

		this.level = position.level;

	}

	public void setDirection(Direction direction){
		facing = direction;
	}

	public void setX(float x){
		this.x = x;
	}

	public void setY(float y){
		this.y = y;
	}

	public void setLevel(int level){
		this.level = level;
	}

	public String toString(){
		return "{x:"+x+",y:"+y+",level:"+level+",facing:"+facing+"}";
	}

}
