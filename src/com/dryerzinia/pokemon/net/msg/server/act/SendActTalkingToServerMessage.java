package com.dryerzinia.pokemon.net.msg.server.act;
/*
SendActTalkingToServerMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.tiles.Person;
import com.dryerzinia.pokemon.obj.Player;
// TODO FIX
public class SendActTalkingToServerMessage extends SendActServerMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    private static final long serialVersionUID = -5709531541228914310L;
    
    boolean b;

    public SendActTalkingToServerMessage() {
    }

    public SendActTalkingToServerMessage(int id, int x, int y, Direction dir, int level, boolean b) {

        this.id = id;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.level = level;

        this.b = b;

    }

    public void proccess(ObjectInputStream ois, PokemonServer.PlayerInstanceData p) throws ClassNotFoundException, IOException {

        Person person = GameState.people.get(id);

		person.getPose().changeDirection(dir);
        //person.onClick.active = b;

        Iterator<Player> playerIterator = GameState.getMap().getLevel(person.getPose().getLevel()).nearbyPlayerIterator();
        while(playerIterator.hasNext()) {
            PokemonServer.PlayerInstanceData pid = PokemonServer.players.get(playerIterator.next().getID());
            if (pid != p)
                pid.sendActor(person, Person.A_TALKING_TO);
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
