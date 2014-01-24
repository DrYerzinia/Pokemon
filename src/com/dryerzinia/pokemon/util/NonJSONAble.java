package com.dryerzinia.pokemon.util;

public class NonJSONAble extends RuntimeException {

	private static final long serialVersionUID = 3438600491039464308L;

	public NonJSONAble(String message){
		super(message);
	}

}
