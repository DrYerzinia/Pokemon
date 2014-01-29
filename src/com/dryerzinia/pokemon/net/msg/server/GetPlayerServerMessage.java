package com.dryerzinia.pokemon.net.msg.server;
/*
GetPlayerServerMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.net.msg.client.PlayerMovement;
import com.dryerzinia.pokemon.obj.Player;

public class GetPlayerServerMessage extends ServerMessage {

    static final long serialVersionUID = -8148715313433245460L;

    public GetPlayerServerMessage() {
    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        p.sendPlayerUpdate(p.getPlayer(), true);
        PokemonServer.pokes.addPlayer(p);

        /*
         * Tell the client about nearby players and nearby players about client
         */
        for(PokemonServer.PlayerInstanceData nearbyPID : PokemonServer.players) {
        	 
        	if(nearbyPID != p){

        		Player nearbyPlayer = nearbyPID.getPlayer();
        		Player player = p.getPlayer();
        		int distance = PokemonServer.distance(player, nearbyPlayer);

        		if(distance < PokemonServer.VISIBLE_DISTANCE){
        			nearbyPID.writeClientMessage(new PlayerMovement(player.getID(), player.getPose()));
        			p.writeClientMessage(new PlayerMovement(nearbyPlayer.getID(), nearbyPlayer.getPose()));
        		}

        	}

        }

    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {

        ois.defaultReadObject();

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

}
