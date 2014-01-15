package com.dryerzinia.pokemon.net.msg.client;
/*
MessageClientMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.PokemonGame;

public class MessageClientMessage extends ClientMessage {

    static final long serialVersionUID = -6011789508904842442L;

    String message;

    public MessageClientMessage() {
    }

    public MessageClientMessage(String m) {

        message = m;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        PokemonGame.pokeg.chathist.add(message);

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
