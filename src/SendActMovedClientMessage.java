/*
SendActMovedClientMessage.java
 */

import java.io.*;
import java.util.*;

public class SendActMovedClientMessage extends SendActClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    public SendActMovedClientMessage(int id_i, int x_i, int y_i, int dir_i,
            int level_i) {

        id = id_i;
        x = x_i;
        y = y_i;
        dir = dir_i;
        level = level_i;
    }

    public void proccess() throws ClassNotFoundException, IOException {

        Iterator<Actor> act = PokemonGame.pokeg.actors.iterator();
        while (act.hasNext()) {
            Actor a = act.next();
            Person p2 = (Person) a;
            if (p2.id == id) {
                PokemonGame.pokeg.level.get(p2.level).g.move(x, y, p2.x, p2.y,
                        (Tile) p2);
                p2.x = x;
                p2.y = y;
                p2.dir = dir;
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
