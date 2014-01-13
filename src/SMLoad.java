/*
LoadServerMessage.java
 */

import java.io.*;

public class SMLoad extends ServerMessage {

    static final long serialVersionUID = -566627905170279688L;

    public SMLoad() {
    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        p.sendLoad();

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
