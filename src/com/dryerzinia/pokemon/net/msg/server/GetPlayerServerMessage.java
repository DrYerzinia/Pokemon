package com.dryerzinia.pokemon.net.msg.server;
/*
GetPlayerServerMessage.java
 */

import java.io.*;
import java.util.Iterator;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.net.msg.client.PlayerMovement;
import com.dryerzinia.pokemon.net.msg.client.act.SendActMovedClientMessage;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.tiles.Person;
import com.dryerzinia.pokemon.obj.Player;

public class GetPlayerServerMessage extends ServerMessage {

    static final long serialVersionUID = -8148715313433245460L;

    public GetPlayerServerMessage() {
    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        Player player = p.getPlayer();

        /*
    	 * Tell the client who they are
    	 * add the players instance data to the master list
    	 * add the player to the player list for the level they are in
    	 */
        p.sendPlayerUpdate(player, true);
        PokemonServer.pokes.addPlayer(p);
        GameState.getMap().getLevel(player.getPose().getLevel()).addPlayer(p.getPlayer());

        Level currentLevel = GameState.getMap().getLevel(player.getPose().getLevel());

        /*
         * Tell the client about nearby players and nearby players about client
         */
        Iterator<Player> playerIterator = currentLevel.nearbyPlayerIterator();
        while(playerIterator.hasNext()){

        	Player nearbyPlayer = playerIterator.next();

        	// TODO look at other messages and make sure to change != to ! .equals
        	if(!nearbyPlayer.equals(player)){

        		int distance = PokemonServer.distance(player, nearbyPlayer);

        		if(distance < PokemonServer.VISIBLE_DISTANCE){
        			PokemonServer.players.get(nearbyPlayer.getID()).writeClientMessage(new PlayerMovement(player.getID(), player.getPose()));
        			p.writeClientMessage(new PlayerMovement(nearbyPlayer.getID(), nearbyPlayer.getPose()));
        		}

        	}

        }

        /*
         * Tell the client about nearby people
         */
        Iterator<Person> peopleIterator = currentLevel.nearbyPersonIterator();
        while(peopleIterator.hasNext()){

        	Person person = peopleIterator.next();

        	int distance = GameState.getMap().manhattanDistance(p.getPlayer().getPose(), person.getPose());

    		if(distance < PokemonServer.VISIBLE_DISTANCE)
    			p.writeClientMessage(new SendActMovedClientMessage(person.id, person.getPose()));

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
