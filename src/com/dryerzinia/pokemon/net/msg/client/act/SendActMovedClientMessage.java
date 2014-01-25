package com.dryerzinia.pokemon.net.msg.client.act;
/*
SendActMovedClientMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Direction;
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

        for(Actor actor : GameState.actors) {
 
        	Person person = (Person) actor;
            if (person.id == id) {
            	person.addMovment(position);
            	/*
                GameState.level.get(person.level).grid.move(position.getX(), position.getY(), person.x, person.y, (Tile) person);
                person.x = position.getX();
                person.y = position.getY();
                person.dir = position.getFacing();
                */
                break;
            }
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
