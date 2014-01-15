package com.dryerzinia.pokemon.net.msg.server.act;
/*
SendActTalkingToServerMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.Person;

public class SendActTalkingToServerMessage extends SendActServerMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    private static final long serialVersionUID = -5709531541228914310L;
    
    boolean b;

    public SendActTalkingToServerMessage() {
    }

    public SendActTalkingToServerMessage(int id_i, int x_i, int y_i, int dir_i,
            int level_i, boolean b_i) {

        id = id_i;
        x = x_i;
        y = y_i;
        dir = dir_i;
        level = level_i;

        b = b_i;

    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        Iterator<Actor> act = PokemonGame.actors.iterator();
        while (act.hasNext()) {
            Actor a = act.next();
            Person p5 = (Person) a;
            if (p5.id == id) {
                p5.dir = dir;
                p5.onClick.active = b;
                Iterator<PokemonServer.PlayerInstanceData> pidt = PokemonServer.pokes.players
                        .iterator();
                while (pidt.hasNext()) {
                    PokemonServer.PlayerInstanceData pid = pidt.next();
                    if (pid != p) {
                        pid.sendActor((Person) a, Person.A_TALKING_TO);
                    }
                }
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
