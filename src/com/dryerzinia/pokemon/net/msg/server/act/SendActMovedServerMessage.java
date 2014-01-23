package com.dryerzinia.pokemon.net.msg.server.act;
/*
SendActMovedServerMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Tile;

public class SendActMovedServerMessage extends SendActServerMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    public SendActMovedServerMessage() {
    }

    public SendActMovedServerMessage(int id_i, int x_i, int y_i, int dir_i,
            int level_i) {

        id = id_i;
        x = x_i;
        y = y_i;
        dir = dir_i;
        level = level_i;

    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        Iterator<Actor> act = GameState.actors.iterator();
        while (act.hasNext()) {
            Actor a = act.next();
            Person p5 = (Person) a;
            if (p5.id == id) {
                GameState.level.get(p5.level).grid.move(x, y, p5.x, p5.y,
                        (Tile) p5);
                p5.x = x;
                p5.y = y;
                p5.dir = dir;

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
