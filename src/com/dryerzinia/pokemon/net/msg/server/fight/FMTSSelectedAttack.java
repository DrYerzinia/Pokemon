package com.dryerzinia.pokemon.net.msg.server.fight;
/*
SelectedFightMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.net.msg.client.fight.FMTCSendAttackResult;
import com.dryerzinia.pokemon.net.msg.server.ServerMessage;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Pokeball;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.ui.Fight;

public class FMTSSelectedAttack extends ServerMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    public static final int RUN_MOVE = -1;
    public static final int ITEM_MOVE = -2;
    public static final int CHANGE_POKEMON_MOVE = -3;

    int move;
    int aux;

    public FMTSSelectedAttack(int move, int aux) {

        this.move = move;
        this.aux = aux;

    }

    // TODO: Store player instance data in fight object so we have immediate
    // access
    private PokemonServer.PlayerInstanceData getOtherPlayer(String name) {

        Iterator<PokemonServer.PlayerInstanceData> i = PokemonServer.pokes.players
                .iterator();
        while (i.hasNext()) {
            PokemonServer.PlayerInstanceData pid = i.next();
            if (pid.getPlayer().getName().equals(name))
                return pid;
        }
        return null;

    }

    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        // Debug Message
        System.out.println("Recived SelectedFightMessage");
        if (move >= 0)
            System.out.println(p.getPlayer().getName() + ": "
                    + p.getPlayer().poke.belt[0].getMove(move).getName()
                    + " | " + move);
        System.out.println("isChallenger: " + p.isChallenger());
        // END

        // Fight over
        boolean fightOver = false;

        // Miss variables
        boolean c_miss = false;
        boolean nc_miss = false;

        // Players
        PokemonServer.PlayerInstanceData challengerPlayer = null;
        PokemonServer.PlayerInstanceData notChallengerPlayer = null;

        // Fight Instance
        Fight f = p.getFight();

        // Set the appropriate move based on player and message
        if (p.isChallenger()) {

            // Set Player Instance data
            challengerPlayer = p;
            notChallengerPlayer = getOtherPlayer(f.currentPlayer.getName());

            // Player to Player Fight : from the Enemy who is the Challenger set
            // move from int
            f.setToUseEnemy(f.enemyPlayer.poke.belt[f.activePokemonE]
                    .getMove(move)); // TODO: check if move is null, this should
                                     // be impossible so boot if it happens

            System.out.println("Player: " + p.p.name + ", Move: " + f.enemyPlayer.poke.belt[f.activePokemonE].getMove(move));

            // DEBUG
            System.out.println("setToUseEnemy()");
            // END

        } else {

            // Set Player Instance data | if this is a wild encounter enemy will
            // have ID == -1
            if (f.enemyPlayer.id != -1)
                challengerPlayer = getOtherPlayer(f.enemyPlayer.getName());
            notChallengerPlayer = p;

            // Message is from not challenger who is the Current, select his
            // move.
            // If we are switching pokemon | Switch out is always fastest
            if (move == CHANGE_POKEMON_MOVE) {

                // First set the move
                f.setToUseCurrent(Fight.SwitchPokemon);

                // Next change the pokemon aux holds the switch too
                // TODO: validate aux, boot if invalid cause that should be
                // impossible and cheating
                f.currentPlayer.poke.belt[aux].getBase(); // Setup base for stats
                f.setOutPokemon(f.currentPlayer.poke.belt[aux]); // Set the out
                                                                 // pokemon to
                                                                 // the switched
                                                                 // pokemon
                f.activePokemonC = aux; // Set the index of the active Pokemon

            }
            // If using an item
            else if (move == ITEM_MOVE) {

                f.setToUseCurrent(Fight.UseItem);

                Item it = notChallengerPlayer.getPlayer().items.get(aux);

                if (it instanceof Pokeball && challengerPlayer == null) {

                    if (((Pokeball) it).captured(f.enemy)) {

                        nc_miss = true;
                        fightOver = true;
                        f.addPokemon(notChallengerPlayer.getPlayer(), f.enemy);

                        System.out.println("Enemy pokemon Captured!");

                    } else {

                        System.out.println("Capture FAILED!!!");

                    }

                }

            }
            // Normal move
            else {
                f.setToUseCurrent(f.currentPlayer.poke.belt[f.activePokemonC]
                        .getMove(move)); // TODO: check if move is NULL, this
                                         // would be cheating boot player from
                                         // server

                System.out.println("setToUseCurrent()");

            }

            // If it is a wild pokemon use the fight function to select a move
            if (challengerPlayer == null)
                f.aiAttackSelection(); // This function will set the toUseE to a
                                       // random enemy attack

        }

        // If both players have selected attacks, send the messages

        // DEBUG
        if (f.toUseE != null)
            System.out.println("toUseE: " + f.toUseE.getName() + " "
                    + f.enemy.getMove(f.toUseE));
        if (f.toUseU != null)
            System.out.println("toUseU: " + f.toUseU.getName() + " "
                    + f.out.getMove(f.toUseU));
        System.out.println("attackSet(): " + f.attacksSet());
        // END

        if (f.attacksSet()) {

            // Variables to hold if any pokemon died during fight
            boolean challengerFainted = false;
            boolean notChallengerFainted = false;
            boolean firstFainted = false;
            boolean secondFainted = false;

            // Check to see which Move/Pokemon is faster
            int sc = f.speedCheck();
            boolean speed_check = true;
            if (sc == 0)
                speed_check = false;

            // Damage done by speed
            int fd, sd;

            // Get the damage done
            int ed = f.getEnemyDamage();
            int pd = f.getOutDamage();

            // DEBUG
            System.out.println("Enemy Damage: " + ed);
            System.out.println("Out Damage: " + pd);
            // END

            // Pokemon references by speed
            Pokemon first;
            Pokemon second;

            // Get pokemon by speed
            if (speed_check) { // This means enemy/challenger goes first
                first = f.enemy;
                second = f.out;
                fd = ed;
                sd = pd;
            } else {
                first = f.out;
                second = f.enemy;
                fd = pd;
                sd = ed;
            }

            // DO the damage
            second.currentHP -= fd; // Do the damage
            if (second.currentHP <= 0) { // If its a kill
                secondFainted = true;
                second.currentHP = 0; // Zero the health of second
            }
            // If its the end
            else if (fightOver) {
                // Do nothing
            }
            // Second gets to do damage
            else {
                first.currentHP -= sd; // Damage done to first
                if (first.currentHP <= 0) { // If its a kill
                    firstFainted = true;
                    f.enemy.currentHP = 0; // Zero the helth
                }
            }

            // DEBUG MESSAGES
            System.out.println(f.out.getName() + " HP: " + f.out.currentHP
                    + "/" + f.out.getTotalHP());
            System.out.println(f.enemy.getName() + " HP: " + f.enemy.currentHP
                    + "/" + f.enemy.getTotalHP());
            // END

            // DEBUG WILL DO STUFF AT SOME POINT
            // Set faints by who
            if (speed_check) { // Challenger/Enemy
                challengerFainted = firstFainted;
                notChallengerFainted = secondFainted;
            } else {
                notChallengerFainted = firstFainted;
                challengerFainted = secondFainted;
            }

            if(f.currentPlayer.poke.getFirstHealthy() == -1) System.out.println("BLACKED OUT");
            if(f.enemyPlayer.poke.getFirstHealthy() == -1) System.out.println("BLACKED OUT");

            System.out.println("Current:");
            f.currentPlayer.poke.printHP();
            System.out.println("Enemy:");
            f.enemyPlayer.poke.printHP();
            
            if (challengerFainted) {
                // TODO: if has another pokemon send it
                System.out.println("WIN!");
                if(f.enemyPlayer.poke.getFirstHealthy() == -1) fightOver = true;
            }
            if (notChallengerFainted) {
                f.notChallengerKOed = true;
                System.out.println("Pick next pokemon");
                if(f.currentPlayer.poke.getFirstHealthy() == -1) fightOver = true;
            }
            if (f.toUseE != null)
                System.out.println("toUseE: " + f.toUseE.getName() + " "
                        + f.enemy.getMove(f.toUseE));
            if (f.toUseU != null)
                System.out.println("toUseU: " + f.toUseU.getName() + " "
                        + f.out.getMove(f.toUseU));
            // END

            // SEND MESSAGES TO PLAYERS
            notChallengerPlayer.writeClientMessage(new FMTCSendAttackResult(
                    !speed_check, nc_miss, c_miss, ed, pd, f.enemy
                            .getMove(f.toUseE), -1));
            // If there is a challenger player send him the message to
            if (challengerPlayer != null)
                challengerPlayer.writeClientMessage(new FMTCSendAttackResult(
                        speed_check, c_miss, nc_miss, pd, ed, f.out
                                .getMove(f.toUseU), -1));

            // Null attacks for next round
            f.toUseU = null;
            f.toUseE = null;

        }

        // Remove fight references
        if (fightOver) {
            
            System.out.println("fightOver: true");
            
            notChallengerPlayer.setFight(null);
            if (challengerPlayer != null)
                challengerPlayer.setFight(null);

            // DEBUG
            System.out.println(notChallengerPlayer.getPlayer().getName()
                    + "'s Pokemon: ");
            for (int i = 0; i < 6; i++) {
                if (notChallengerPlayer.getPlayer().poke.belt[i] != null) {
                    notChallengerPlayer.getPlayer().poke.belt[i].getBase();
                    System.out
                            .println(notChallengerPlayer.getPlayer().poke.belt[i]
                                    + " HP: "
                                    + notChallengerPlayer.getPlayer().poke.belt[i].currentHP
                                    + "/"
                                    + notChallengerPlayer.getPlayer().poke.belt[i]
                                            .getTotalHP());
                }
            }
            // END

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
