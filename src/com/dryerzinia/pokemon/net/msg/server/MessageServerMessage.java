package com.dryerzinia.pokemon.net.msg.server;
/*
MessageServerMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.PokemonServer;

public class MessageServerMessage extends ServerMessage {

    static final long serialVersionUID = -6011789508904842442L;

    String message;

    public MessageServerMessage() {
    }

    public MessageServerMessage(String m) {

        message = m;

    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        PokemonServer.pokes.sendMessage(message, p);

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
