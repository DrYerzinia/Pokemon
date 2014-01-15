package com.dryerzinia.pokemon.net.msg.client.fight;
/*
ReturnFightMessage.java
 */

import java.io.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.msg.client.ClientMessage;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Pokeball;
import com.dryerzinia.pokemon.ui.Fight;
import com.dryerzinia.pokemon.ui.menu.GMenu;

public class FMTCSendAttackResult extends ClientMessage {

    // static final long serialVersionUID = 6177261564125944892L;

    boolean go_first;

    boolean sucessful; // Escape/Pokeball/Miss
    boolean enemy_sucessful; // Escape/Pokeball/Miss

    int damage_1;
    int damage_2;

    int enemy_move;

    int pokemon_change_to;

    public FMTCSendAttackResult() {
    }

    public FMTCSendAttackResult(boolean gf, boolean s, boolean es, int d1,
            int d2, int em, int ct) {

        go_first = gf;
        sucessful = s;
        enemy_sucessful = es;
        damage_1 = d1;
        damage_2 = d2;
        enemy_move = em;
        pokemon_change_to = ct;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        Fight f = (Fight) PokemonGame.pokeg.overlay.o;

        f.WaitingForServerMessageReturn = false;

        f.toUseE = f.enemy.getMove(enemy_move);

        f.missE = enemy_sucessful;
        f.missU = sucessful;

        f.damageE = damage_1;
        f.damageU = damage_2;

        System.out.println("go_first: " + go_first);

        if (go_first)
            f.firstTurn = f.out;
        else
            f.firstTurn = f.enemy;

        // You move first!
        if (go_first) {
            boolean skip_attack = false;

            // If its a switch pokemon move
            if (f.toUseU.getName().equals(Fight.SwitchPokemon.getName())) {
                f.info.set(new GMenu("Go! " + f.out.name + "!", 0, 6, 10, 3)); // Use
                                                                               // appropriate
                                                                               // switch
                                                                               // message
                f.toUseU.pp = 10; // Make sure it doesn't question the amount of
                                  // PP the move has
            }
            // If its a use item move
            else if (f.toUseU.getName().equals(Fight.UseItem.getName())) {
                Item it = PokemonGame.pokeg.Char.items.get(f.itemToUse);
                if (it instanceof Pokeball && !f.isTrainer()) {
                    // if(it.number == 0)
                    // ((ItemMenu)e.getSelectMenu()).items.remove(e.getSelection());
                    f.capture(sucessful);
                    skip_attack = true;
                }
            }
            // If its a normal move
            else {
                // Use the normal message
                f.info.set(new GMenu(f.out.getName() + "\nused "
                        + f.toUseU.getName() + "!", 0, 6, 10, 3));
            }
            if (!skip_attack)
                f.attack();
        } else {
            f.ai();
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
