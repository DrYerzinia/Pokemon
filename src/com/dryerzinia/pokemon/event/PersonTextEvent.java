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
		oldDirection = person.getPose().facing();

		Direction newDirection = Direction.NONE;

		if(ClientState.player.getPose().getX() > person.x)
			newDirection = Direction.RIGHT;
		else if(ClientState.player.getPose().getX() < person.x)
			newDirection = Direction.LEFT;
		else if(ClientState.player.getPose().getY() > person.y)
			newDirection = Direction.DOWN;
		else if(ClientState.player.getPose().getY() < person.y)
			newDirection = Direction.UP;

		person.getPose().changeDirection(newDirection);

		person.animationEnabled = false;

		super.fire();
	}

	@Override
	public void buttonPressed() {

		// Reset actor rotation
		// reenable animation updates
		if(complete){

			Person person = GameState.people.get(actorID);
			person.getPose().changeDirection(oldDirection);

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
