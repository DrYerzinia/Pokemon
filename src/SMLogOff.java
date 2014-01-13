/*
LogOffServerMessage.java
 */

import java.io.*;

public class SMLogOff extends ServerMessage {

    static final long serialVersionUID = -5647010552503731718L;

    public SMLogOff() {
    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        PokemonServer.pokes.remove(p.getPlayer());

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
