/*
SendActServerMessage.java
 */

import java.io.*;

public class SendActServerMessage extends ServerMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    int id;
    int x;
    int y;
    int dir;
    int level;

    public SendActServerMessage() {
    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

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
