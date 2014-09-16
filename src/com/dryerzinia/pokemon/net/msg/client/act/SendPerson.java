package com.dryerzinia.pokemon.net.msg.client.act;

import java.io.IOException;

import com.dryerzinia.pokemon.net.msg.client.ClientMessage;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.tiles.Person;

public class SendPerson extends ClientMessage {

	private static final long serialVersionUID = -1266668251394462790L;

	private Person receivedPerson;

	/**
	 * Creates a message to send all information about a person to the client
	 * @param recievedPerson person to send to the client
	 */
	public SendPerson(Person receivedPerson){
		this.receivedPerson = receivedPerson;
	}

	/**
	 * Updates or adds received person to the Clients list of people
	 */
    public void proccess() throws ClassNotFoundException, IOException {

    	GameState.getMap().getLevel(receivedPerson.getPose().getLevel()).addPerson(receivedPerson);
    	GameState.people.put(receivedPerson.id, receivedPerson);

    }

}
