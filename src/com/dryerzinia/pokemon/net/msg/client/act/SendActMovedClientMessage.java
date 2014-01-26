package com.dryerzinia.pokemon.net.msg.client.act;
/*
SendActMovedClientMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.server.act.RequestPerson;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Position;
import com.dryerzinia.pokemon.obj.Tile;

public class SendActMovedClientMessage extends SendActClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    public SendActMovedClientMessage(int id, int x, int y, Direction dir, int level) {

        this.id = id;
        position = new Position(x, y, level, dir);

    }

    public void proccess() throws ClassNotFoundException, IOException {

        for(Person person : GameState.people) {
 
            if(person.id == id){
            	person.addMovment(position);
                return;
            }
        }

        /*
         * We don't know this actor
         */
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
