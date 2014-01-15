package com.dryerzinia.pokemon.net.msg.server;
/*
GetPokemonServerMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.obj.Pokemon;

public class GetPokemonServerMessage extends ServerMessage {

    static final long serialVersionUID = 4002936693389648134L;

    Pokemon receivedPokemon;

    public GetPokemonServerMessage() {
    }

    public GetPokemonServerMessage(Pokemon p) {
        receivedPokemon = p;
    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        boolean found = false;
        Iterator<Pokemon> itp = p.getPlayer().poke.box.iterator();
        while (itp.hasNext()) {
            Pokemon p2 = itp.next();
            if (p2.idNo == receivedPokemon.idNo) {
                p2.set(receivedPokemon);
                found = true;
            }
        }
        if (!found) {
            p.getPlayer().poke.box.add(receivedPokemon);
            receivedPokemon.added = true;
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
