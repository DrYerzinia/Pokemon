package com.dryerzinia.pokemon.net.msg.server.fight;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.net.msg.client.fight.FMTCSendNextPokemon;
import com.dryerzinia.pokemon.net.msg.server.ServerMessage;
import com.dryerzinia.pokemon.ui.Fight;


public class FMTSSendNextPokemon extends ServerMessage {

    int next_pokemon_index;
    
    public FMTSSendNextPokemon(int npi) {

        next_pokemon_index = npi;

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
        System.out.println("Recived FMTSSendNextPokemon");

        // END

        // Fight Instance
        Fight f = p.getFight();
        
        // Players
        PokemonServer.PlayerInstanceData otherPlayer;
        if (p.isChallenger()) {
            otherPlayer = getOtherPlayer(f.currentPlayer.getName());
        } else {
            otherPlayer = getOtherPlayer(f.enemyPlayer.getName());
        }

        // Challenger/Enemy
        if (p.isChallenger()){
            f.enemy = p.p.poke.belt[next_pokemon_index];
            f.activePokemonE = next_pokemon_index;
        } else {
            f.out = p.p.poke.belt[next_pokemon_index];
            f.activePokemonC = next_pokemon_index;
        }

        otherPlayer.writeClientMessage(new FMTCSendNextPokemon(p.p.poke.belt[next_pokemon_index]));

        //
    }
}
