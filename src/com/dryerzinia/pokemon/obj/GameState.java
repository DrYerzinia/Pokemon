package com.dryerzinia.pokemon.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.ui.editor.UltimateEdit;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.util.JSONObject;

public class GameState {

    public static ArrayList<Actor> actors = new ArrayList<Actor>();

    public static ArrayList<Level> level;

    public static  ArrayList<Tile> mtile;
    public static  ArrayList<String> chathist;

    public static void init(){

    	load("save.json");

        for(Tile tile : mtile)
            if (tile instanceof Actor)
                actors.add((Actor)tile);

    }

    public static void initTileSecondary(Grid g) {
        for (int x = 0; x < g.g.length; x++) {
            for (int y = 0; y < g.g[0].length; y++) {
                for (int j = 0; j < g.g[x][y].size(); j++) {
                    g.g[x][y].get(j).initializeSecondaryReferences(g);
                }
            }
        }
    }

    public static void save(File f) {

    	try {

        	PrintWriter json_writer = new PrintWriter(f);

        	json_writer.print(JSONArray.arrayListToJSON(mtile));
        	json_writer.print("\n");
        	json_writer.print(JSONArray.arrayListToJSON(level));
        	
        	json_writer.close();

        } catch (Exception x) {
            x.printStackTrace();
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

        try {

            mtile = new ArrayList<Tile>();
            level = new ArrayList<Level>();

            System.out.println("Loading game from: " + filename);

            BufferedReader json_reader = new BufferedReader(new InputStreamReader(
                    PokemonGame.class.getClassLoader().getResourceAsStream(filename)));

			String json_1 = json_reader.readLine();
			String json_2 = json_reader.readLine();

			json_reader.close();
		
			Object[] tiles = JSONObject.JSONToArray(json_1);
			Object[] levels = JSONObject.JSONToArray(json_2);

            for(int i = 0; i < tiles.length; i++)
            	mtile.add((Tile)tiles[i]);

            for(int i = 0; i < levels.length; i++)
                level.add((Level)levels[i]);

        } catch (Exception x) {
            x.printStackTrace();
        }

        for (int i = 0; i < level.size(); i++) {

            for (int x = 0; x < level.get(i).g.g.length; x++) {
                for (int y = 0; y < level.get(i).g.g[0].length; y++) {
                    for (int j = 0; j < level.get(i).g.g[x][y].size(); j++) {
                        int id = level.get(i).g.g[x][y].get(j).id;
                        Tile t = mtile.get(id);
                        level.get(i).g.g[x][y].set(j, t);
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
            initTileSecondary(level.get(i).g);

        }

    }

}
