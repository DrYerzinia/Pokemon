package com.dryerzinia.pokemon.map;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.obj.RandomFight;
import com.dryerzinia.pokemon.ui.UI;
import com.dryerzinia.pokemon.util.JSON;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.MultiIterator;

public class Level implements Serializable, JSON {

    static final long serialVersionUID = -5293673761061322573L;

    private transient CopyOnWriteArrayList<Person> peopleInLevel = new CopyOnWriteArrayList<Person>();
    private transient CopyOnWriteArrayList<Player> playersInLevel = new CopyOnWriteArrayList<Player>();

    public Grid grid;

    public transient boolean midmove = false;
    public transient boolean canMove = true;
    public transient Level borderL[] = new Level[9];

    public int borders[] = new int[9];
    public int borderoffset[] = new int[9];
    public int id;

    public Level() {
    }

    public Level(Grid g) {

    	this.grid = g;

    	for (int i = 0; i < 9; i++)
            borders[i] = -1;
    }

    /**
     * Draws the level based on the players x y coordinates
     * @param graphics Graphics object to draw the level onto
     * @param x Players x coordinate
     * @param y Players y coordinate
     */
    public void draw(Graphics graphics, float x, float y) {

        grid.draw(x, y, graphics);

        if (borderL != null) {
            if (borderL[0] != null)
            	borderL[0].drawOffset(x, y, borderL[0].grid.grid.length, borderoffset[0], graphics);
            if (borderL[3] != null)
            	borderL[3].drawOffset(x, y, -1*grid.grid.length, borderoffset[3], graphics);
            if (borderL[1] != null)
            	borderL[1].drawOffset(x, y, borderoffset[1], borderL[1].grid.grid[0].length, graphics);
            if (borderL[7] != null)
            	borderL[7].drawOffset(x, y, borderoffset[7], -1*grid.grid[0].length, graphics);
        }

		for(Person person : peopleInLevel)
    		if(UI.visibleManhattanDistance > GameState.getMap().manhattanDistance(ClientState.player.getPose(), new Pose((int)person.x, (int)person.y, person.level, Direction.NONE)))
    			person.draw(person.x - x, person.y - y, 0, 0, graphics);

		for(Player player : playersInLevel)
			if(PokemonServer.VISIBLE_DISTANCE > GameState.getMap().manhattanDistance(ClientState.player.getPose(), player.getPose()))
				player.draw(x, y, graphics);

    }

    public void drawOffset(float x, float y, int xOffset, int yOffset, Graphics graphics){

    	grid.draw(x + xOffset, y + yOffset, graphics);

    	for(Person person : peopleInLevel)
    		if(UI.visibleManhattanDistance > GameState.getMap().manhattanDistance(ClientState.player.getPose(), person.getPose()))
    			person.draw(person.x - x - xOffset, person.y - y - yOffset, 0, 0, graphics);

		for(Player player : playersInLevel)
			if(PokemonServer.VISIBLE_DISTANCE > GameState.getMap().manhattanDistance(ClientState.player.getPose(), player.getPose()))
				player.draw(x + xOffset, y + yOffset, graphics);
    	
    }

    /**
     * Update other players and persons so they do there movement animations
     * @param deltaTime Time Delta since last update
     */
    public void update(int deltaTime){

    	updateLocal(deltaTime);
    	
    	/*
		 * TODO create adjacent level list with adjacent level wrapper
		 */
        if (borderL != null) {
            if (borderL[0] != null)
            	borderL[0].updateLocal(deltaTime);
            if (borderL[3] != null)
            	borderL[3].updateLocal(deltaTime);
            if (borderL[1] != null)
            	borderL[1].updateLocal(deltaTime);
            if (borderL[7] != null)
            	borderL[7].updateLocal(deltaTime);
        }

    }

    /**
     * Updates players and people in this level only
     * @param deltaTime Time Delta since last update
     */
    private void updateLocal(int deltaTime){

    	for(Actor actor : peopleInLevel)
			actor.update(deltaTime);
	
		for(Player player : playersInLevel)
			player.update(Direction.NONE, deltaTime);
	
	}
    
    public BorderOffset borderOffset(Level level){

    	/*
    	 * DOWN  7
    	 * UP    1
    	 */
    	for(int i = 0; i < 8; i++)
    		if(borderL[i] == level)
    			switch(i){
    			case 1:
    				return new BorderOffset(Direction.UP, borderoffset[i], borderL[i].grid.getHeight());
    			case 7:
    				return new BorderOffset(Direction.DOWN, borderoffset[i], grid.getHeight());
    			case 0:
        			return new BorderOffset(Direction.LEFT, borderoffset[i], borderL[i].grid.getWidth());
    			case 3:
    				return new BorderOffset(Direction.RIGHT, borderoffset[i], grid.getWidth());
    			default:
    			}

    	return null;

    }

    public boolean isAdjacentTo(Level level){

    	if(borderL != null && (
   		   borderL[0] == level
   		|| borderL[3] == level
   		|| borderL[1] == level
   		|| borderL[7] == level
    	))
    		return true;

    	return false;

    }

    public Pokemon attacked(Player player) {

    	RandomFight rf = grid.getRandomFight((int) player.getPose().getY(), (int) player.getPose().getY());

    	if (rf != null)
            return rf.getAttack();

    	return null;

    }

    public boolean canStepOn(int x, int y){

    	if(!grid.canStepOn(x, y)) return false;

    	for(Person person : peopleInLevel)
    		if(((int)person.x) == x && ((int)person.y) == y)
    			return false;

    	return true;

    }

    /**
     * Checks if the tile is a ledge
     * @param x X position of tile
     * @param y Y position of tile
     * @return True if the tile is a ledge
     */
    public boolean isLedge(int x, int y){

    	return grid.isLedge(x, y);

    }

    /**
     * Returns an iterator that iterates all the people in this level and the
     * adjacent levels
     * @return Iterator of nearby people
     */
    public Iterator<Person> nearbyPersonIterator(){
    	
    	MultiIterator<Person> iterator = new MultiIterator<Person>(5);

    	iterator.addIterator(peopleInLevel.iterator());

        if (borderL != null) {
            if (borderL[0] != null)
            	iterator.addIterator(borderL[0].peopleInLevel.iterator());
            if (borderL[3] != null)
            	iterator.addIterator(borderL[3].peopleInLevel.iterator());
            if (borderL[1] != null)
            	iterator.addIterator(borderL[1].peopleInLevel.iterator());
            if (borderL[7] != null)
            	iterator.addIterator(borderL[7].peopleInLevel.iterator());
        }

        return iterator;

    }

    /**
     * Returns an iterator that iterates all the players in this level and the
     * adjacent levels
     * @return Iterator of nearby players
     */
    public Iterator<Player> nearbyPlayerIterator(){

    	MultiIterator<Player> iterator = new MultiIterator<Player>(5);

    	iterator.addIterator(playersInLevel.iterator());

        if (borderL != null) {
            if (borderL[0] != null)
            	iterator.addIterator(borderL[0].playersInLevel.iterator());
            if (borderL[3] != null)
            	iterator.addIterator(borderL[3].playersInLevel.iterator());
            if (borderL[1] != null)
            	iterator.addIterator(borderL[1].playersInLevel.iterator());
            if (borderL[7] != null)
            	iterator.addIterator(borderL[7].playersInLevel.iterator());
        }

        return iterator;

    }

    /**
     * Adds a person to the levels local list of people
     * @param newPerson Person to add to list
     */
    public void addPerson(Person newPerson){

    	int index = peopleInLevel.indexOf(newPerson);
    	if(index != -1)
    		peopleInLevel.set(index, newPerson);
    	else
    		peopleInLevel.add(newPerson);

    }

    /**
     * Adds a player to the levels local list of players
     * @param newPlayer Player to add to list
     */
    public void addPlayer(Player newPlayer){

    	int index = playersInLevel.indexOf(newPlayer);
    	if(index != -1)
    		playersInLevel.set(index, newPlayer);
    	else
    		playersInLevel.add(newPlayer);

    }

    /**
     * Removes a player from the level list if the player
     * is in it
     * @param toRemove Player to remove
     */
    public void removePlayer(Player toRemove){

    	int index = playersInLevel.indexOf(toRemove);
    	if(index != -1)
    		playersInLevel.remove(index);

    }

    /**
     * Swaps a player from this level to another level
     * @param toSwap Player to swap
     * @param levelToSwapTo Level to move player to
     */
    public void swapPlayer(Player toSwap, Level levelToSwapTo){

    	int index = playersInLevel.indexOf(toSwap);

    	/*
    	 * If player is not in fog of war
    	 */
    	if(index != -1)
    		playersInLevel.remove(index);

    	/*
    	 * If player is not going to fog of war
    	 */
    	if(levelToSwapTo != null)
    		levelToSwapTo.addPlayer(toSwap);

    }
    
    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        grid.initLevelReference(this);
        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

	@Override
    public void fromJSON(HashMap<String, Object> json){

		grid = (Grid) json.get("grid");

        Object[] bordersArray = (Object[]) json.get("borders");
	    borders = new int[9];
        for(int i = 0; i < bordersArray.length; i++)
        	if(bordersArray[i] != null)
        		borders[i] = ((Float) bordersArray[i]).intValue();

        Object[] borderoffsetArray = (Object[]) json.get("borderoffset");
	    borderoffset = new int[9];
        for(int i = 0; i < bordersArray.length; i++)
        	if(borderoffsetArray[i] != null)
        		borderoffset[i] = ((Float) borderoffsetArray[i]).intValue();

	    id = ((Float) json.get("id")).intValue();

    	grid.initLevelReference(this);

    }
    
    public void initLevelReferences(ArrayList<Level> l) {
        borderL = new Level[9];
        for (int i = 0; i < 9; i++) {
            if (borders[i] != -1)
                borderL[i] = l.get(borders[i]);
        }
    }

}
