/*
SendActClientMessage.java
 */

import java.io.*;

public class SendActClientMessage extends ClientMessage {

    static final long serialVersionUID = -168421746034798020L;

    int id;
    int x;
    int y;
    int dir;
    int level;

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
