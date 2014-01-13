/*
PingServerMessage.java
 */

import java.io.*;

public class PingServerMessage extends ServerMessage {

    static final long serialVersionUID = 6177261564125944892L;

    public PingServerMessage() {
    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {
        System.out.println("Pong");
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
