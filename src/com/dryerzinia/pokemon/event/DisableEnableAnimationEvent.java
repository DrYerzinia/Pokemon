package com.dryerzinia.pokemon.event;

import java.util.HashMap;

import com.dryerzinia.pokemon.obj.GameState;

public class DisableEnableAnimationEvent extends Event {

	protected boolean isEnabled = false;
	protected int actorID;

	public DisableEnableAnimationEvent(){}

	@Override
	public void fire() {

		GameState.people.get(actorID).animationEnabled = isEnabled;
		EventCore.fireEvent(nextEvent);

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		super.fromJSON(json);
		isEnabled = ((Boolean) json.get("isEnabled")).booleanValue();
		actorID = ((Float) json.get("actorID")).intValue();

	}

}
