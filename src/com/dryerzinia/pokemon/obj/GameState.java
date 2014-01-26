package com.dryerzinia.pokemon.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.ui.editor.UltimateEdit;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.StringStream;

public class GameState {

    public static CopyOnWriteArrayList<Person> people;

    public static ArrayList<Level> level;
    public static  ArrayList<Tile> mtile;

    public static void init(){

    	Move.readMoveBase();
        Pokemon.readPokemonBaseStats();

    	load("save.json");

    	people = new CopyOnWriteArrayList<Person>();

    }

    public static void initTileSecondary(Grid g) {
        for (int x = 0; x < g.grid.length; x++) {
            for (int y = 0; y < g.grid[0].length; y++) {
                for (int j = 0; j < g.grid[x][y].size(); j++) {
                    g.grid[x][y].get(j).initializeSecondaryReferences(g);
                }
            }
        }
    }

    /**
     * Save the actors as a JSON array
     * @param filename file to save actors in
     */
    public static void saveActors(String filename){

    	try(PrintWriter json_writer = new PrintWriter(filename)){
    	
        	json_writer.print(JSONArray.arrayListToJSON(new ArrayList<Person>(people)));

    	} catch(FileNotFoundException e){

        	System.err.println("Could not find Actors file: " + filename);
        	System.err.println(e.getMessage());

    	} catch(IllegalAccessException e) {

        	System.err.println("Unable to write Actors JSON with reflection: " + e.getMessage());

        }

    }

    /**
     * Loads the actors from a JSON file
     * @param filename actors JSON file
     */
	public static void loadActors(String filename) {

    	people = new CopyOnWriteArrayList<Person>();

		try(BufferedReader json_reader = new BufferedReader(new InputStreamReader(
                PokemonGame.class.getClassLoader().getResourceAsStream(filename)));){

			String json = json_reader.readLine();

			Object[] actors = JSONObject.JSONToArray(new StringStream(json));

			/*
			 * Add all actors to CopyOnWrite array using array list as 
			 * inbetween to prevent ton of new array allocations
			 */
			ArrayList<Person> newPeople = new ArrayList<Person>();
            for(Object actor : actors){

            	Person person = (Person) actor;

            	person.initializeSecondaryReferences(level.get(person.level).grid);
            	newPeople.add((Person)person);

            }
            people.addAll(newPeople);

        } catch (Exception x) {
            x.printStackTrace();
        }

	}

	/**
     * Save the map file
     * @param filename file to save map in
     */
    public static void save(String filename) {

    	try(PrintWriter json_writer = new PrintWriter(filename)){

        	json_writer.print(JSONArray.arrayListToJSON(mtile));
        	json_writer.print("\n");
        	json_writer.print(JSONArray.arrayListToJSON(level));
        	
        } catch(FileNotFoundException e){

        	System.err.println("Could not find Map file: " + filename);
        	System.err.println(e.getMessage());

        } catch(IllegalAccessException e) {

        	System.err.println("Unable to write Map JSON with reflection: " + e.getMessage());

        }

    }

    /**
     * Load the game from a file
     * 
     * @param filename Save File Name
     * 
     * TODO we need to remove actor locations so we can get them
     * from the server and actors don't "JUMP" when you move into a level
     */
    public static void load(String filename) {

        mtile = new ArrayList<Tile>();
        level = new ArrayList<Level>();

        System.out.println("Loading game from: " + filename);

        try(BufferedReader json_reader = new BufferedReader(new InputStreamReader(
                PokemonGame.class.getClassLoader().getResourceAsStream(filename)));){

			String json_1 = json_reader.readLine();
			String json_2 = json_reader.readLine();

			Object[] tiles = JSONObject.JSONToArray(new StringStream(json_1));
			Object[] levels = JSONObject.JSONToArray(new StringStream(json_2));

            for(int i = 0; i < tiles.length; i++)
            	mtile.add((Tile)tiles[i]);

            for(int i = 0; i < levels.length; i++)
                level.add((Level)levels[i]);

        } catch (Exception x) {
            x.printStackTrace();
        }

        for (int i = 0; i < level.size(); i++) {

            for (int x = 0; x < level.get(i).grid.grid.length; x++) {
                for (int y = 0; y < level.get(i).grid.grid[0].length; y++) {
                    for (int j = 0; j < level.get(i).grid.grid[x][y].size(); j++) {
                        int id = level.get(i).grid.grid[x][y].get(j).id;
                        Tile t = mtile.get(id);
                        level.get(i).grid.grid[x][y].set(j, t);
                        if (UltimateEdit.Extends(t, "Person")) {
                            Person p = (Person) t;
                            p.x = x;
                            p.y = y;
                            p.level = i;
                        }
                    }
                }
            }
            level.get(i).initLevelReferences(level);
            initTileSecondary(level.get(i).grid);

        }

    }

}
