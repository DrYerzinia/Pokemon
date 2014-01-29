package com.dryerzinia.pokemon.net.msg.client;

import java.io.IOException;

import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.server.WhoIsPlayer;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.Player;

public class PlayerMovement extends ClientMessage {

	private static final long serialVersionUID = 7827666281450884190L;

	int id;
	Pose newPosition;

	public PlayerMovement(int id, Pose newPosition){

		this.id = id;
		this.newPosition = newPosition;

	}

    public void proccess() throws ClassNotFoundException, IOException {

    	for(Player player : ClientState.players)
    		if(player.getID() == id){
    			player.addMovement(newPosition);
    			return;
    		}

    	Client.writeServerMessage(new WhoIsPlayer(id));

    }

}
