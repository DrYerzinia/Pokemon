package com.dryerzinia.pokemon.map;
import java.io.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.obj.RandomFight;
import com.dryerzinia.pokemon.util.JSON;
import com.dryerzinia.pokemon.util.JSONObject;

public class Level implements Serializable, JSON {

    static final long serialVersionUID = -5293673761061322573L;

    private transient CopyOnWriteArrayList<Person> peopleInLevel = new CopyOnWriteArrayList<Person>();

    public Grid grid;

    public transient boolean midmove = false;
    public transient boolean canMove = true;
    public transient Level borderL[] = new Level[9];
    public int borders[] = new int[9];
    public int borderoffset[] = new int[9];
    public int id; // TODO: appears unused figureout wtf todo with this

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
                borderL[0].grid.draw(x + borderL[0].grid.grid.length, y + borderoffset[0], graphics);
            if (borderL[3] != null)
                borderL[3].grid.draw(x - grid.grid.length, y + borderoffset[3], graphics);
            if (borderL[1] != null)
                borderL[1].grid.draw(x + borderoffset[1], y + borderL[1].grid.grid[0].length, graphics);
            if (borderL[7] != null)
                borderL[7].grid.draw(x + borderoffset[7], y - grid.grid[0].length, graphics);
        }

		for(Person person : peopleInLevel)
			person.draw(0, 0, 0, 0, graphics);

    }

    public Pokemon attacked(Player player) {

    	RandomFight rf = grid.getRF((int) player.x, (int) player.y);

    	if (rf != null)
            return rf.getAttack();

    	return null;

    }

    public boolean canStepOn(int x, int y){

    	if(!grid.canStepOn(x, y)) return false;

    	for(Person person : peopleInLevel)
    		if(((int)person.x) == x + 4 && ((int)person.y) == y + 4)
    			return false;

    	return true;

    }

    public void addPerson(Person newPerson){

    	ListIterator<Person> personIterator = peopleInLevel.listIterator();
        while(personIterator.hasNext()) {

        	Person person = personIterator.next();
            if (person.id == newPerson.id) {
            	peopleInLevel.set(personIterator.previousIndex(), newPerson);
                return;
            }
        }

        peopleInLevel.add(newPerson);

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

    public void fromJSON(HashMap<String, Object> json){

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
