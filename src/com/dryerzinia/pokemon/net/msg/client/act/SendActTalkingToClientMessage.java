package com.dryerzinia.pokemon.net.msg.client.act;
/*
SendActTalkingToClientMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Position;

public class SendActTalkingToClientMessage extends SendActClientMessage {

	private static final long serialVersionUID = -8660483740507972045L;

	boolean b;

    public SendActTalkingToClientMessage(int id, int x, int y, int dir, int level, boolean b) {

        this.id = id;
        position = new Position(x, y, level, dir);

        this.b = b;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        for(Actor actor : GameState.actors) {

            Person person = (Person) actor;
            if (person.id == id) {

            	person.dir = position.getFacing();
            	person.onClick.active = b;
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
