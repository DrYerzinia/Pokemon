package com.dryerzinia.pokemon.net.msg.server;
/*
ServerMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.PokemonServer;

public abstract class ServerMessage implements Serializable {

    static final long serialVersionUID = 7256252232322597017L;

    public ServerMessage() {
    }

    public abstract void proccess(ObjectInputStream ois, PokemonServer.PlayerInstanceData p) throws ClassNotFoundException, IOException;

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {

        ois.defaultReadObject();

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

}
