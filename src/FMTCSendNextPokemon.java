import java.io.IOException;

public class FMTCSendNextPokemon extends ClientMessage {

    Pokemon next_pokemon;

    public FMTCSendNextPokemon(Pokemon np) {

        next_pokemon = np;

    }

    public void proccess() throws ClassNotFoundException, IOException {

        // Debug Message
        System.out.println("Recived FMTCSendNextPokemon");
        // END

        Fight f = (Fight) PokemonGame.pokeg.overlay.o;

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
