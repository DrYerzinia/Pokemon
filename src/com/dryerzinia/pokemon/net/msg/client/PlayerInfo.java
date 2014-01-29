package com.dryerzinia.pokemon.net.msg.client;
/*
PlayerUpdateMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.obj.ClientState;
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

            Client.writeLoadMessage();

        } else {

            boolean found = false;
            Iterator<Player> i = ClientState.players.iterator();
            while (i.hasNext()) {
                Player foundPlayer = i.next();
                if (receivedPlayer.id == foundPlayer.id) {
                    if (receivedPlayer.getPose().getLevel() == -1) {
                        i.remove();
                    } else {
                        foundPlayer.set(receivedPlayer);
                        found = true;
                    }
                    break;
                }
            }
            if (!found) {
                receivedPlayer.loadImages();
                ClientState.players.add(receivedPlayer);
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
