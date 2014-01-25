package com.dryerzinia.pokemon.map;

public enum Direction {

	UP(0, "UP"),
	DOWN(1, "DOWN"),
	LEFT(2, "LEFT"),
	RIGHT(3, "RIGHT"),

	NONE(4, "NONE");

	private int value;
	private String strValue;

	Direction(int value, String strValue){
		this.value = value;
		this.strValue = strValue;
	}

	public int getValue(){
		return value;
	}

	public String getStringValue(){
		return strValue;
	}

	public static Direction get(int value){

		for(Direction direction : Direction.values())
			if(direction.getValue() == value) return direction;

		return NONE;

	}

	public static Direction getFromString(String string){

		for(Direction direction : Direction.values())
			if(direction.getStringValue().equals(string)) return direction;

		return NONE;

	}

}
