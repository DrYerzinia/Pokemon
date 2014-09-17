package com.dryerzinia.pokemon.event;

import java.util.HashMap;

import com.dryerzinia.pokemon.util.JSON;
import com.dryerzinia.pokemon.util.JSONObject;

public abstract class Event implements JSON {

	public int id;

	public abstract void fire();

	protected int nextEvent;

	public int getID(){
		return id;
	}

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		id = ((Float) json.get("id")).intValue();
		nextEvent = ((Float) json.get("nextEvent")).intValue();

	}

}
