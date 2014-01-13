/*
SendActTalkingToClientMessage.java
 */

import java.io.*;
import java.util.*;

public class SendActTalkingToClientMessage extends SendActClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    boolean b;

    public SendActTalkingToClientMessage(int id_i, int x_i, int y_i, int dir_i,
            int level_i, boolean b_i) {

        id = id_i;
        x = x_i;
        y = y_i;
        dir = dir_i;
        level = level_i;

        b = b_i;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        Iterator<Actor> act = PokemonGame.pokeg.actors.iterator();
        while (act.hasNext()) {
            Actor a = act.next();
            Person p2 = (Person) a;
            if (p2.id == id) {
                p2.dir = dir;
                p2.onClick.active = b;
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
