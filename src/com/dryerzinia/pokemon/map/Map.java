package com.dryerzinia.pokemon.map;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.tiles.Person;
import com.dryerzinia.pokemon.obj.tiles.Tile;
import com.dryerzinia.pokemon.ui.editor.UltimateEdit;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.StringStream;

public class Map {

    private ArrayList<Level> levels;
    private ArrayList<Tile> masterTileSet;

    public Map(){
    }

    public Level getLevel(int id){
 
    	if(id == -1) return null;
    	return levels.get(id);

    }

    public int manhattanDistance(Pose a, Pose b){

    	if(a.getLevel() == -1 || b.getLevel() == -1)
    		return Integer.MAX_VALUE;

    	if(a.getLevel() == b.getLevel())
    		return Math.abs((int) a.getX() - (int) b.getX()) + Math.abs((int) a.getY() - (int) b.getY());

    	BorderOffset borderOffset = this.getLevel(a.getLevel()).borderOffset(this.getLevel(b.getLevel()));

   		if(borderOffset != null)
   			return borderOffset.manhattanDistance((int) a.getX(), (int) b.getX(), (int) a.getY(), (int) b.getY());

   		return Integer.MAX_VALUE;

    }

    private void initTileSecondary(Grid g) {
        for (int x = 0; x < g.grid.length; x++) {
            for (int y = 0; y < g.grid[0].length; y++) {
                for (int j = 0; j < g.grid[x][y].size(); j++) {
                    g.grid[x][y].get(j).initializeSecondaryReferences(g);
                }
            }
        }
    }

    /**
     * Save the map file
     * @param filename file to save map in
     */
    public void save(String filename) {

    	try(PrintWriter json_writer = new PrintWriter(filename)){

        	json_writer.print(JSONArray.setToJSON(masterTileSet));
        	json_writer.print("\n");
        	json_writer.print(JSONArray.setToJSON(levels));
        	
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
    public void load(String tileFilename, String levelFilename) {

        masterTileSet = new ArrayList<Tile>();
        levels = new ArrayList<Level>();

        System.out.println("Loading game from: " + tileFilename);

		try(BufferedReader jsonReader = new BufferedReader(new InputStreamReader(
                PokemonGame.class.getClassLoader().getResourceAsStream(tileFilename)));){

			StringBuilder stringBuilder = new StringBuilder();
			String line = null;

			while((line = jsonReader.readLine()) != null ){
				stringBuilder.append(line);
				stringBuilder.append("\n");
			}

			String json = stringBuilder.toString();

			Object[] loadedTiles = JSONObject.JSONToArray(new StringStream(json));

			for(int i = 0; i < loadedTiles.length; i++)
            	masterTileSet.add((Tile)loadedTiles[i]);

        } catch(IOException ioe){
        	System.err.println("Unable to read Tile file: " + ioe.getMessage());
        }

		try(BufferedReader jsonReader = new BufferedReader(new InputStreamReader(
                PokemonGame.class.getClassLoader().getResourceAsStream(levelFilename)));){

			StringBuilder stringBuilder = new StringBuilder();
			String line = null;

			while((line = jsonReader.readLine()) != null ){
				stringBuilder.append(line);
				stringBuilder.append("\n");
			}

			String json = stringBuilder.toString();

			Object[] loadedLevels = JSONObject.JSONToArray(new StringStream(json));

			for(int i = 0; i < loadedLevels.length; i++)
                levels.add((Level)loadedLevels[i]);

        } catch(IOException ioe){
        	System.err.println("Unable to read Level file: " + ioe.getMessage());
        }

        for(Level level : levels) {

            for(int x = 0; x < level.grid.grid.length; x++) {
                for(int y = 0; y < level.grid.grid[0].length; y++) {
                    for(int j = 0; j < level.grid.grid[x][y].size(); j++) {

                    	int id = level.grid.grid[x][y].get(j).id;

                    	Tile tile = masterTileSet.get(id);
                    	level.grid.grid[x][y].set(j, tile);

                    }
                }
            }
            level.initLevelReferences(levels);
            initTileSecondary(level.grid);

        }

    }
	
}
