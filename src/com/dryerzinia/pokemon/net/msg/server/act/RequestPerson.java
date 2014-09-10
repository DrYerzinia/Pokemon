package com.dryerzinia.pokemon.net.msg.server.act;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.client.act.SendPerson;
import com.dryerzinia.pokemon.net.msg.server.ServerMessage;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.tiles.Person;

public class RequestPerson extends ServerMessage {

	private static final long serialVersionUID = 7986115389606957111L;

	private int id;

	public RequestPerson(int id){
		this.id = id;
	}


    public void proccess(ObjectInputStream ois, PokemonServer.PlayerInstanceData p) throws ClassNotFoundException, IOException {

        Person person = GameState.people.get(id);
       	p.writeClientMessage(new SendPerson(person));
 
    }

}
