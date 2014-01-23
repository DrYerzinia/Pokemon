package com.dryerzinia.pokemon.net.msg.server;
/*
PlayerServerMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.net.msg.client.fight.SendFightClientMessage;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.ui.Fight;

public class PlayerServerMessage extends ServerMessage {

    static final long serialVersionUID = 1398424266564606936L;

    private Player receivedPlayer;

    public PlayerServerMessage() {
    }

    public PlayerServerMessage(Player p) {

        receivedPlayer = p;

    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        Player thisPlayer = p.getPlayer();

        boolean levelchange = false;
        if (thisPlayer.level != receivedPlayer.level)
            levelchange = true;

        thisPlayer.set(receivedPlayer);

        Iterator<PokemonServer.PlayerInstanceData> plyrIt = PokemonServer.pokes.players
                .iterator();
        while (plyrIt.hasNext()) {
            PokemonServer.PlayerInstanceData otherPlayerPID = plyrIt.next();
            Player otherPlayer = otherPlayerPID.getPlayer();
            if (thisPlayer != otherPlayer
                    && PokemonServer.localized(thisPlayer, otherPlayer)) { // Add
                                                                           // localization
                                                                           // for
                                                                           // updates
                try {
                    otherPlayerPID.sendPlayerUpdate(thisPlayer, false);
                } catch (IOException x) {
                    System.err.println("Failed to Update Player");
                }
            } else {
                if (levelchange) {
                    // Player has left there level TODO: Show players in range
                    // but different level
                    // Also this will spam on level changes fix that
                    Player pGone = new Player();
                    pGone.set(thisPlayer);
                    pGone.level = -1;
                    try {
                        p.sendPlayerUpdate(pGone, false);
                    } catch (IOException x) {
                        System.err.println("Failed to Update Player");
                    }
                }
            }
        }

        Pokemon pRandom = GameState.level.get(thisPlayer.level)
                .attacked(thisPlayer);
        if (pRandom != null) {
            Pokemon p2Random = new Pokemon(pRandom);
            p2Random.getBase();
            p2Random.currentHP = p2Random.getTotalHP();

            Fight f = new Fight();
            f.enemyPlayer = new Player();
            f.enemyPlayer.id = -1;
            f.pokemonCountE = 1;
            f.activePokemonE = 0;

            f.currentPlayer = p.getPlayer();

            int i;
            for (i = 0; i < 6; i++) {
                if (f.currentPlayer.poke.belt[i].currentHP != 0) {
                    f.out = f.currentPlayer.poke.belt[i];
                    f.out.getBase();
                    f.activePokemonC = i;
                    break;
                }
            }

            Pokemon pokemon[] = f.currentPlayer.poke.belt;
            for (i = 0; i < 6; i++)
                if (pokemon[i] == null)
                    break;

            f.pokemonCountC = i + 1;

            f.enemy = p2Random;

            p.setFight(f);
            p.setIsChallenger(false);

            p.writeClientMessage(new SendFightClientMessage(f));

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
