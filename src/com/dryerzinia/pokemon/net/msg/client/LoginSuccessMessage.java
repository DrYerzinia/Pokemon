package com.dryerzinia.pokemon.net.msg.client;
/*
LoginSuccessMessage.java
 */

import java.util.*;
import java.io.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.server.GetPlayerServerMessage;

public class LoginSuccessMessage extends ClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    public LoginSuccessMessage() {
    }

    public void proccess() throws ClassNotFoundException, IOException {

        Client.writeServerMessage(new GetPlayerServerMessage());

        Client.startPinger();

        System.out.println("Logged IN!!!");

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
