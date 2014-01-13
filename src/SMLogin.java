/*
LoginServerMessage.java
 */

import java.io.*;

public class SMLogin extends ServerMessage {

    static final long serialVersionUID = 3722766493876686574L;

    String username;
    String password;

    public SMLogin() {
    }

    public SMLogin(String un, String pw) {

        username = un;
        password = pw;

    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        p.writeClientMessage(new LoginSuccessMessage());

        Player p2 = p.getPlayer();
        p2.name = username;

        if (PokemonServer.pokes.isLoggedIn(p2)) {
            p.writeClientMessage(new BadPasswordMessage());
            // throw new Exception("Already Logged In...");
        }

        Player p3 = MysqlConnect.login(username, password);
        if (p3 == null) {
            p.writeClientMessage(new AlreadyLoggedInMessage());
            // throw new Exception("Loggin Failure...");
        } else {
            p.setLoggedIn(true);
        }

        System.out.println("Logged IN!");

        p2.set(p3);
        p2.poke = MysqlConnect.getCharacterPokemon(p2.getID());
        p2.items = MysqlConnect.getCharacterItems(p2.getID());

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
