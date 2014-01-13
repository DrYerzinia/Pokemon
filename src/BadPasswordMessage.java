/*
BadPasswordMessage.java
 */

import java.io.*;

public class BadPasswordMessage extends ClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    public BadPasswordMessage() {
    }

    public void proccess() throws ClassNotFoundException, IOException {

        System.out.println("Bad Password!!!");

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
