package com.dryerzinia.pokemon.net.msg.client.act;
/*
SendActClientMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.map.Position;
import com.dryerzinia.pokemon.net.msg.client.ClientMessage;

public class SendActClientMessage extends ClientMessage {

    static final long serialVersionUID = -168421746034798020L;

    int id;
    Position position;

    public SendActClientMessage() {
    }

    public void proccess() throws ClassNotFoundException, IOException {

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
