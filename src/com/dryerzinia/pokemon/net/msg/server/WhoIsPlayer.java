package com.dryerzinia.pokemon.net.msg.server;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.PokemonServer.PlayerInstanceData;
import com.dryerzinia.pokemon.net.msg.client.PlayerInfo;

public class WhoIsPlayer extends ServerMessage {

	private static final long serialVersionUID = 5247332137323901529L;

	int id;

	public WhoIsPlayer(int id){
		this.id = id;
	}

	@Override
	public void proccess(ObjectInputStream ois, PlayerInstanceData p) throws ClassNotFoundException, IOException {

        for(PokemonServer.PlayerInstanceData nearbyPID : PokemonServer.players)
        	if(nearbyPID.getPlayer().getID() == id){
        		p.writeClientMessage(new PlayerInfo(nearbyPID.getPlayer(), false));
        		return;
        	}


	}

}
