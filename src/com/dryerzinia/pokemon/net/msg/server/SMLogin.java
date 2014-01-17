package com.dryerzinia.pokemon.net.msg.server;
/*
LoginServerMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.net.msg.client.AlreadyLoggedInMessage;
import com.dryerzinia.pokemon.net.msg.client.BadPasswordMessage;
import com.dryerzinia.pokemon.net.msg.client.LoginSuccessMessage;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.util.MysqlConnect;

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
            PokemonServer.PlayerInstanceData pid) throws ClassNotFoundException,
            IOException {

        Player player = pid.getPlayer();
        player.name = username;

        if (PokemonServer.pokes.isLoggedIn(player)) {

        	System.out.println("Error: Player is already logged into server!");

            pid.writeClientMessage(new AlreadyLoggedInMessage());
            // throw new Exception("Already Logged In...");

        }

        System.out.println("Loggin Attempt: " + username + ":" + password);

        Player login_result = MysqlConnect.login(username, password);
        if (login_result == null) {

        	System.out.println("Error: Credentials do not exist in Database!");

        	pid.writeClientMessage(new BadPasswordMessage());
            // throw new Exception("Login Failure...");

        } else {

            System.out.println("Client logged in successfully!");
            System.out.println("Loading Player information from database!");

        	pid.writeClientMessage(new LoginSuccessMessage());
            pid.setLoggedIn(true);

            player.set(login_result);
            player.poke = MysqlConnect.getCharacterPokemon(player.getID());
            player.items = MysqlConnect.getCharacterItems(player.getID());
            
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
