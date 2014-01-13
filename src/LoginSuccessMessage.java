/*
LoginSuccessMessage.java
 */

import java.util.*;
import java.io.*;

public class LoginSuccessMessage extends ClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    public LoginSuccessMessage() {
    }

    public void proccess() throws ClassNotFoundException, IOException {

        PokemonGame.pokeg.writeServerMessage(new GetPlayerServerMessage());

        PokemonGame.pokeg.pinger = new Timer();
        PokemonGame.pokeg.pinger.scheduleAtFixedRate(
                PokemonGame.pokeg.new PingerTask(), 0, 15000);

        System.out.println("Logged IN!!!");

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
