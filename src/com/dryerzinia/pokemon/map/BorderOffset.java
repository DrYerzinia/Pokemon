package com.dryerzinia.pokemon.map;

public class BorderOffset {

	private Direction crossingDirection;

	private int offset;
	private int crossingDistance;

	public BorderOffset(Direction crossingDirection, int offset, int crossingDistance){

		this.crossingDirection = crossingDirection;

		this.offset = offset;
		this.crossingDistance = crossingDistance;

	}

	public int manhattanDistance(int x1, int x2, int y1, int y2){

		switch(crossingDirection){
		case UP:
			return Math.abs((crossingDistance - y2) + y1) + Math.abs((x1 + offset) - x2);
		case DOWN:
			return Math.abs((crossingDistance - y1) + y2) + Math.abs((x1 + offset) - x2);
		case LEFT:
			return Math.abs((y1 + offset) - y2) + Math.abs((crossingDistance - x2) + x1);
		case RIGHT:
			return Math.abs((y1 + offset) - y2) + Math.abs((crossingDistance - x1) + x2);
		case NONE:
		default:
			return Integer.MAX_VALUE;
		}

	}

}
