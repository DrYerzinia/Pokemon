package com.dryerzinia.pokemon.net.msg.server;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.net.msg.client.PlayerMovement;
import com.dryerzinia.pokemon.net.msg.client.act.SendActMovedClientMessage;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Player;

public class PlayerPositionMessage extends ServerMessage {

	private static final long serialVersionUID = -6642155985356809953L;

	private Pose playerPosition;

	public PlayerPositionMessage(Pose playerPosition){

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
        boolean levelChange = false;
        if (player.getPose().getLevel() != playerPosition.getLevel())
            levelChange = true;

        /*
         * Update servers copy of the players position
         * TODO VALIDATE THIS MOVEMENT!
         */
        player.setPosition(playerPosition);

        /*
         * Update near-by players to our position
         * TODO leve list search
         */
        for(PokemonServer.PlayerInstanceData nearbyPID : PokemonServer.players) {

        	/*
        	 * If the player is not SELF and
        	 * is nearby send a update to let them know he moved
        	 */
        	if(p != nearbyPID) {

            	Player nearbyPlayer = nearbyPID.getPlayer();
        		int distance = PokemonServer.distance(player, nearbyPlayer);

        		/*
        		 * people who are only 2 tiles away from being on screen are updated
        		 */
        		if(distance < PokemonServer.VISIBLE_DISTANCE)
        			nearbyPID.writeClientMessage(new PlayerMovement(player.getID(), playerPosition));

        		/*
        		 * people who are in the leaving sweet spot need to have there level
        		 * set to -1 so they wont be drawn where they were
        		 */
        		else if((distance >= PokemonServer.FOG_OF_WAR-PokemonServer.TRANSITION_ZONE && distance < PokemonServer.FOG_OF_WAR) || (distance >= PokemonServer.FOG_OF_WAR-PokemonServer.TRANSITION_ZONE && levelChange))
        			nearbyPID.writeClientMessage(new PlayerMovement(player.getID(), Pose.NOWHERE_LAND));

        		/*
        		 * Sweet spot for visibility change at this distance stationary
        		 * players send updates to players moving near them
        		 */
        		if((distance < PokemonServer.VISIBLE_DISTANCE && distance > PokemonServer.VISIBLE_DISTANCE-PokemonServer.TRANSITION_ZONE) || (distance < PokemonServer.VISIBLE_DISTANCE && levelChange))
        			p.writeClientMessage(new PlayerMovement(nearbyPlayer.getID(), nearbyPlayer.getPose()));

        	}
        }

        /*
         * Actor fog of war
         * TODO level list search
         */
        for(Person person : GameState.people.values()){

        	int distance = GameState.getMap().manhattanDistance(player.getPose(), person.getPose());

        	/*
        	 * Sweet spot for Spotting people
        	 */
    		if((distance < PokemonServer.VISIBLE_DISTANCE && distance > PokemonServer.VISIBLE_DISTANCE-PokemonServer.TRANSITION_ZONE) || (distance < PokemonServer.VISIBLE_DISTANCE && levelChange))
    			p.writeClientMessage(new SendActMovedClientMessage(person.id, (int)person.x, (int)person.y, person.dir, person.level));

    		/*
        	 * Sweet spot for people leaving
        	 */
    		if((distance >= PokemonServer.FOG_OF_WAR-PokemonServer.TRANSITION_ZONE && distance < PokemonServer.FOG_OF_WAR) || (distance >= PokemonServer.FOG_OF_WAR-PokemonServer.TRANSITION_ZONE && levelChange))
    			p.writeClientMessage(new SendActMovedClientMessage(person.id, 0, 0, Direction.NONE, -1));

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
