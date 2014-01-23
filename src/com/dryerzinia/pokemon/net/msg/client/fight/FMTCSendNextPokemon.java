package com.dryerzinia.pokemon.net.msg.client.fight;
import java.io.IOException;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.msg.client.ClientMessage;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.ui.Fight;
import com.dryerzinia.pokemon.ui.UI;
import com.dryerzinia.pokemon.ui.menu.GMenu;

public class FMTCSendNextPokemon extends ClientMessage {

    Pokemon next_pokemon;

    public FMTCSendNextPokemon(Pokemon np) {

        next_pokemon = np;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        // Debug Message
        System.out.println("Recived FMTCSendNextPokemon");
        // END

        Fight f = (Fight) UI.overlay.o;

        f.WaitingForServerMessageReturn = false;

        // If opponent dosn't have any more Pokemon we win!
        if (next_pokemon == null) {

            // sendNowPokemon();
            f.active = false;

        // Switch out next Pokemon
        } else {

            f.enemy = next_pokemon;
            f.end = false;
            f.info.set(new GMenu(" \n ", 0, 6, 10, 3));
            f.fightMenuActive = true;
            f.faint = false;
            f.turn = true;
            f.exp = false;

        }

    }

}
