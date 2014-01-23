package com.dryerzinia.pokemon.net.msg.client;
/*
LoadClientMessage.java
 */

import java.io.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokemon;

public class CMLoad extends ClientMessage {

    // static final long serialVersionUID = -566627905170279688L;

    int pokeBeltidNo[];
    ArrayList<Pokemon> allPokemon;
    ArrayList<Item> allItems;

    public CMLoad() {
    }

    public CMLoad(int ids[], ArrayList<Pokemon> pokes,
            ArrayList<Item> items) {

        pokeBeltidNo = ids;
        allPokemon = pokes;
        allItems = items;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        Iterator<Pokemon> pokeIt = allPokemon.iterator();
        while (pokeIt.hasNext()) {
            Pokemon nextPokemon = pokeIt.next();
            nextPokemon.getBase();
            for (int j = 0; j < 6; j++)
                if (nextPokemon.idNo == pokeBeltidNo[j])
                    ClientState.player.poke.belt[j] = nextPokemon;
        }
        ClientState.player.poke.box = allPokemon;
        ClientState.player.items = allItems;

        // TODO PokemonGame.pokeg.setupMenus();
        ClientState.setLoaded();

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
