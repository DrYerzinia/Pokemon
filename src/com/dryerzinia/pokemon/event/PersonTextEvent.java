package com.dryerzinia.pokemon.event;

import java.util.HashMap;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.tiles.Person;

public class PersonTextEvent extends TextEvent {

	private int actorID;

	private transient Direction oldDirection;

	public PersonTextEvent(){}

	@Override
	public void fire() {

		Person person = GameState.people.get(actorID);
		oldDirection = person.dir;

		if(ClientState.player.getPose().getX() > person.x)
			person.dir = Direction.RIGHT;
		else if(ClientState.player.getPose().getX() < person.x)
			person.dir = Direction.LEFT;
		else if(ClientState.player.getPose().getY() > person.y)
			person.dir = Direction.DOWN;
		else if(ClientState.player.getPose().getY() < person.y)
			person.dir = Direction.UP;

		person.animationEnabled = false;

		super.fire();
	}

	@Override
	public void buttonPressed() {

		// Reset actor rotation
		// reenable animation updates
		if(complete){

			Person person = GameState.people.get(actorID);
			person.dir = oldDirection;

			person.animationEnabled = true;

		}

		super.buttonPressed();

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		super.fromJSON(json);
		actorID = ((Float) json.get("actorID")).intValue();

	}

}
