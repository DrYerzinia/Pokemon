/*
GetPlayerServerMessage.java
 */

import java.io.*;

public class GetPlayerServerMessage extends ServerMessage {

    static final long serialVersionUID = -8148715313433245460L;

    public GetPlayerServerMessage() {
    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        p.sendPlayerUpdate(p.getPlayer(), true);
        PokemonServer.pokes.addPlayer(p);

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
