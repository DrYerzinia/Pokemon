package com.dryerzinia.pokemon.net.msg.client;
/*
GetPokemonClientMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.obj.Pokemon;

public class GetPokemonClientMessage extends ClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    Pokemon receivedPokemon;

    public GetPokemonClientMessage() {
    }

    public GetPokemonClientMessage(Pokemon poke) {

        receivedPokemon = poke;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        // TODO: THIS RECIVES ENEMY POKEMON IN A FIGHT!!! FINISH

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
