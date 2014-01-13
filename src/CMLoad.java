/*
LoadClientMessage.java
 */

import java.io.*;
import java.util.*;

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
            nextPokemon.getBase(PokemonGame.pokeg.basePokemon,
                    PokemonGame.pokeg.baseMoves);
            for (int j = 0; j < 6; j++)
                if (nextPokemon.idNo == pokeBeltidNo[j])
                    PokemonGame.pokeg.Char.poke.belt[j] = nextPokemon;
        }
        PokemonGame.pokeg.Char.poke.box = allPokemon;
        PokemonGame.pokeg.Char.items = allItems;

        PokemonGame.pokeg.setupMenus();

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
