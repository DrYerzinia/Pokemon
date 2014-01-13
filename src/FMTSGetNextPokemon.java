import java.io.IOException;
import java.io.ObjectInputStream;

public class FMTSGetNextPokemon extends ServerMessage {

    public FMTSGetNextPokemon() {

    }

    @Override
    public void proccess(ObjectInputStream ois,
            PokemonServer.PlayerInstanceData p) throws ClassNotFoundException,
            IOException {

        // Debug Message
        System.out.println("Recived FMTSGetNextPokemon");
        // END

        // Fight Instance
        Fight f = p.getFight();

        if (f == null || f.enemyPlayer.id == -1) {

            p.writeClientMessage(new FMTCSendNextPokemon(null));
            System.out.println();

        }
    }

}
