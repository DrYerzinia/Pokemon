package com.dryerzinia.pokemon.net.msg.client.act;
/*
SendActTalkingToClientMessage.java
 */
// TODO FIX
import java.io.*;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.tiles.Person;

public class SendActTalkingToClientMessage extends SendActClientMessage {

	private static final long serialVersionUID = -8660483740507972045L;

	boolean b;

    public SendActTalkingToClientMessage(int id, Pose location, boolean b) {

        this.id = id;
        position = location;

        this.b = b;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        Person person = GameState.people.get(id);

        person.getPose().changeDirection(position.facing());
        //person.onClick.active = b;

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
