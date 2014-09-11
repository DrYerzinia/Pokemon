package com.dryerzinia.pokemon.event;

import java.util.HashMap;

import com.dryerzinia.pokemon.event.conditional.ConditionalCore;

public class ConditionalEvent extends Event {

	int conditionalID;

	@Override
	public void fire() {

		ConditionalCore.fireConditional(conditionalID);

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		super.fromJSON(json);

		conditionalID = ((Float) json.get("conditionalID")).intValue();

	}

}
