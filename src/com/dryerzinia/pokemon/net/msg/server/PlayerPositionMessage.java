package com.dryerzinia.pokemon.net.msg.server;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Position;

public class PlayerPositionMessage extends ServerMessage {

	private static final long serialVersionUID = -6642155985356809953L;

	private Position playerPosition;

	public PlayerPositionMessage(Position playerPosition){

		this.playerPosition = playerPosition;

	}

    public void proccess(ObjectInputStream ois, PokemonServer.PlayerInstanceData p) throws ClassNotFoundException, IOException {

        Player player = p.getPlayer();

        /*
         * Check for level change to know if we need to remove our selves
         * from people in that level
         * TODO fix this to players who have us in there
         * SEEN list we need to implement this
         */
        boolean levelchange = false;
        if (player.getLevel() != playerPosition.getLevel())
            levelchange = true;

        /*
         * Update servers copy of the players position
         * TODO VALIDATE THIS MOVEMENT!
         */
        player.setPosition(playerPosition);

        /*
         * Update near-by players to our position
         */
        for(PokemonServer.PlayerInstanceData nearbyPID : PokemonServer.players) {
 
        	Player nearbyPlayer = nearbyPID.getPlayer();

        	/*
        	 * If the player is not SELF and
        	 * is nearby send a update to let them know he moved
        	 */
        	if(player != nearbyPlayer
              && PokemonServer.localized(player, nearbyPlayer)) { // TODO improve localization via manhattan distance

        		try {
                   nearbyPID.sendPlayerUpdate(player, false);
                } catch (IOException ioe) {
                    System.err.println("Failed to Update Player: " + ioe.getMessage());
                }

        	/*
        	 * If they are not nearby
        	 * TODO we shouldent send remove self to SELF!
        	 */
        	} else {

        		/*
        		 * If we left the level
        		 * this can be merged with ELSE!
        		 * TODO fuck this is just retarted fix this shit
        		 */
                if (levelchange) {
                    // Player has left there level TODO: Show players in range
                    // but different level
                    // Also this will spam on level changes fix that
                    Player pGone = new Player();
                    pGone.set(player);
                    pGone.level = -1;
                    try {
                        p.sendPlayerUpdate(pGone, false);
                    } catch (IOException x) {
                        System.err.println("Failed to Update Player");
                    }
                }
            }
        }

        /*
         * Check for random fights to send to the player
         * TODO uncomment when menu system is working again
         */
        /*
        Pokemon pRandom = GameState.level.get(player.level).attacked(player);
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

        }*/

    }

}
