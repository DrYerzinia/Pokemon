package com.dryerzinia.pokemon.net.msg.server;
/*
GetItemServerMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.obj.Item;

public class GetItemServerMessage extends ServerMessage {

    static final long serialVersionUID = 5852257554143444083L;

    Item recievedItem;

    public GetItemServerMessage() {
    }

    public GetItemServerMessage(Item i) {

        recievedItem = i;

    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        boolean found = false;
        Iterator<Item> iti = p.getPlayer().items.iterator();
        while (iti.hasNext()) {
            Item ite = iti.next();
            if (ite.name.equals(recievedItem.name)) {
                ite.set(recievedItem);
                found = true;
                break;
            }
        }
        if (!found) {
            recievedItem.added = true;
            p.getPlayer().items.add(recievedItem);
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
