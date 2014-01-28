package com.dryerzinia.pokemon.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Map;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.StringStream;

public class GameState {

    public static CopyOnWriteArrayList<Person> people;

    private static Map map;

    public static void init(){

    	Move.readMoveBase();
        Pokemon.readPokemonBaseStats();

        map = new Map();

    	map.load("save.json");

    	people = new CopyOnWriteArrayList<Person>();

    }

    public static Map getMap(){

    	return map;

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

            	person.initializeSecondaryReferences(map.getLevel(person.level).grid);
            	newPeople.add((Person)person);

            }
            people.addAll(newPeople);

        } catch (Exception x) {
            x.printStackTrace();
        }

	}

}
