package com.dryerzinia.pokemon.net.msg.client;

import java.io.IOException;

import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.server.WhoIsPlayer;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
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

    	Player player = ClientState.players.get(id);
    	if(player != null) {

    		Level playerLevel = GameState.getMap().getLevel(player.getPose().getLevel());
    		Level mainCharLevel = ClientState.getPlayerLevel();

    		/*
    		 * If the player to update is not visible just update them
    		 * and clear out any movements that are waiting to occur
    		 */
    		if(playerLevel == null || (playerLevel != mainCharLevel && !playerLevel.isAdjacentTo(mainCharLevel))){

    			player.clearMovements();
    			player.setPosition(newPosition);	
    			

    			/*
    			 * If we changed levels move the character
    			 */
    			Level newLevel = GameState.getMap().getLevel(player.getPose().getLevel());
    			if(playerLevel != newLevel){

    				if(playerLevel != null)
    					playerLevel.removePlayer(player);
    				
    				if(newLevel != null)
    					newLevel.addPlayer(player);

    			}

    		}

    		/*
    		 * If they are visible set up the animation sequence
    		 */
    		else
    			player.addMovement(newPosition);

    	} else
    		Client.writeServerMessage(new WhoIsPlayer(id));

    }

}
