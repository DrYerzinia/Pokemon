package com.dryerzinia.pokemon.net.msg.server.fight;
/*
FightServerMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.net.msg.server.ServerMessage;

public class FMServer extends ServerMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    public FMServer() {
    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

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
