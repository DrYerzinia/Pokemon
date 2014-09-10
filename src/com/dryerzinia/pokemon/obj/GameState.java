package com.dryerzinia.pokemon.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Map;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.StringStream;
import com.dryerzinia.pokemon.obj.tiles.Person;

public class GameState {

	public static int peopleExpected = 100;
	public static int threadsForPeople = 1;

    public static ConcurrentHashMap<Integer, Person> people;

    private static Map map;

    public static void init(){

    	Move.readMoveBase();
        Pokemon.readPokemonBaseStats();

        map = new Map();

    	map.load("Tiles.json", "Levels.json");

    	people = new ConcurrentHashMap<Integer, Person> (peopleExpected, 0.75f, threadsForPeople);

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
    	
        	json_writer.print(JSONArray.setToJSON(people.values()));

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

		if(people == null)
			new ConcurrentHashMap<Integer, Person> (peopleExpected, 0.75f, threadsForPeople);

		try(BufferedReader jsonReader = new BufferedReader(new InputStreamReader(
                PokemonGame.class.getClassLoader().getResourceAsStream(filename)));){

			StringBuilder stringBuilder = new StringBuilder();
			String line = null;

			while((line = jsonReader.readLine()) != null ){
				stringBuilder.append(line);
				stringBuilder.append("\n");
			}

			String json = stringBuilder.toString();

			Object[] actors = JSONObject.JSONToArray(new StringStream(json));


            for(Object actor : actors){

            	Person person = (Person) actor;

            	person.initializeSecondaryReferences(map.getLevel(person.level).grid);
            	people.put(person.id, person);
            	getMap().getLevel(person.level).addPerson(person);

            }

        } catch(IOException ioe){
        	System.err.println("Failed to read actors: " + ioe.getMessage());
        }

	}

}
