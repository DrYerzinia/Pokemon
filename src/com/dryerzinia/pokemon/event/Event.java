package com.dryerzinia.pokemon.event;

import java.util.HashMap;

import com.dryerzinia.pokemon.util.JSON;

public abstract class Event implements JSON {

	public int id;

	public abstract void fire();

	public int getID(){
		return id;
	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		id = ((Float) json.get("id")).intValue();

	}

}
