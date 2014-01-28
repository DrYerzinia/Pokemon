package com.dryerzinia.pokemon.net.msg.server;
/*
LogOffServerMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.PokemonServer;

public class SMLogOff extends ServerMessage {

    static final long serialVersionUID = -5647010552503731718L;

    public SMLogOff() {
    }

    public void proccess(ObjectInputStream ois, PokemonServer.PlayerInstanceData p) throws ClassNotFoundException, IOException {

        PokemonServer.pokes.remove(p);

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
