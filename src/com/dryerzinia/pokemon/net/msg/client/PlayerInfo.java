package com.dryerzinia.pokemon.net.msg.client;
/*
PlayerUpdateMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Player;

public class PlayerInfo extends ClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    Player receivedPlayer;

    boolean isSelf;

    public PlayerInfo() {
    }

    public PlayerInfo(Player updatePlayer, boolean self) {

        receivedPlayer = updatePlayer;

        isSelf = self;

    }

    public void proccess() throws ClassNotFoundException, IOException {

    	// TODO fix what is causing this!!!
    	if(receivedPlayer.getPose().getLevel() == -1) return;

        if (isSelf) {

            ClientState.player = receivedPlayer;
            ClientState.player.loadImages();

            ClientState.getPlayerLevel().notifyChangedTo();

            Client.writeLoadMessage();

        } else {

        	/*
        	 * If its a -1 we remove player FOW
        	 * TODO believe this is unused was for log offs but
        	 * should just send a player movement update
        	 */
        	if(receivedPlayer.getPose().getLevel() == -1){
        		ClientState.players.remove(receivedPlayer.getID());
        		return;
        	}

        	/*
        	 * If we did get a real level for this player to be in than we have
        	 * to either set his new attributes (Probably  unused)
        	 * or we load his images and add him to players list
        	 */
        	Player player = ClientState.players.get(receivedPlayer.getID());

        	if(player != null){

        		player.set(receivedPlayer);

            	/*
            	 * If there was a level change we have to swap him between level
            	 * lists
            	 */
            	if(player.getPose().getLevel() != receivedPlayer.getPose().getLevel())
            		GameState.getMap().getLevel(player.getPose().getLevel()).swapPlayer(receivedPlayer, GameState.getMap().getLevel(receivedPlayer.getPose().getLevel()));

        	} else {

            	receivedPlayer.loadImages();
                ClientState.players.put(receivedPlayer.getID(), receivedPlayer);

            	/*
            	 * If he is new player also add him to per level player list
            	 */
                GameState.getMap().getLevel(receivedPlayer.getPose().getLevel()).addPlayer(receivedPlayer);

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
