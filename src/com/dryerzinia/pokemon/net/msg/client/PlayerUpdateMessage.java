package com.dryerzinia.pokemon.net.msg.client;
/*
PlayerUpdateMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.obj.Player;

public class PlayerUpdateMessage extends ClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    Player receivedPlayer;

    boolean isSelf;

    public PlayerUpdateMessage() {
    }

    public PlayerUpdateMessage(Player updatePlayer, boolean self) {

        receivedPlayer = updatePlayer;

        isSelf = self;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        if (isSelf) {

            Player.self = receivedPlayer;
            Player.self.loadImages();

            PokemonGame.pokeg.writeLoadMessage();

        } else {

            boolean found = false;
            Iterator<Player> i = PokemonGame.pokeg.players.iterator();
            while (i.hasNext()) {
                Player foundPlayer = i.next();
                if (receivedPlayer.id == foundPlayer.id) {
                    if (receivedPlayer.level == -1) {
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
                PokemonGame.pokeg.players.add(receivedPlayer);
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
