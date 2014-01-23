package com.dryerzinia.pokemon.net.msg.client.fight;
/*
SendFightClientMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.msg.client.ClientMessage;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.ui.Fight;
import com.dryerzinia.pokemon.ui.UI;
import com.dryerzinia.pokemon.util.MysqlConnect;
import com.dryerzinia.pokemon.util.MysqlConnect.PokemonContainer;

public class SendFightClientMessage extends ClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    private static final long serialVersionUID = 2068019970666860885L;

    Fight receivedFight;

    public SendFightClientMessage() {
    }

    public SendFightClientMessage(Fight f) {

        receivedFight = f;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        if (receivedFight.enemyPlayer.id != -1) {

            // Set up unknown Pokemon and enemy in enemyPlayer belt
            receivedFight.enemyPlayer.poke = new MysqlConnect.PokemonContainer(); // Create
                                                                                  // container
                                                                                  // for
                                                                                  // player
            for (int i = 0; i < receivedFight.pokemonCountE; i++) {
                // If first out
                if (receivedFight.activePokemonE == i) {
                    // Set this pokemon to the enemy pokemon
                    receivedFight.enemyPlayer.poke.belt[i] = receivedFight.enemy;
                }
                // If not first out
                else {
                    // New blank pokemon with -1 id
                    receivedFight.enemyPlayer.poke.belt[i] = new Pokemon();
                    receivedFight.enemyPlayer.poke.belt[i].idNo = -1;
                }

            }

        }

        UI.overlay.o = receivedFight;
        receivedFight.init();
        UI.overlay.o.active = true;

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
