/*
ClientMessage.java
 */

import java.io.*;

public class ClientMessage implements Serializable {

    // static final long serialVersionUID = 7256252232322597017L;

    public ClientMessage() {
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
