package com.dryerzinia.pokemon.net.msg.client.act;
/*
SendActMovedClientMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.server.act.RequestPerson;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.tiles.Person;
import com.dryerzinia.pokemon.obj.tiles.Tile;

public class SendActMovedClientMessage extends SendActClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    public SendActMovedClientMessage(int id, int x, int y, Direction dir, int level) {

        this.id = id;
        position = new Pose(x, y, level, dir);

    }

    public void proccess() throws ClassNotFoundException, IOException {

        Person person = GameState.people.get(id);

        if(person != null)
        	person.addMovement(position);

        /*
         * We don't know this actor
         */
        else
        	Client.writeServerMessage(new RequestPerson(id));

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
