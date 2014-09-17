package com.dryerzinia.pokemon.event;

import java.util.HashMap;

import com.dryerzinia.pokemon.obj.GameState;

public class FacePlayerEvent extends Event {

	protected int actorID;

	public FacePlayerEvent(){}

	@Override
	public void fire() {

		GameState.people.get(actorID).facePlayer();
		EventCore.fireEvent(nextEvent);

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		super.fromJSON(json);
		actorID = ((Float) json.get("actorID")).intValue();

	}

}
