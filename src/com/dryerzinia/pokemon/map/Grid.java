package com.dryerzinia.pokemon.map;
import java.io.*;
import java.awt.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.LevelChange;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.RandomFight;
import com.dryerzinia.pokemon.obj.Tile;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.util.JSON;

public class Grid implements Serializable, JSON {

    static final long serialVersionUID = 8900706544643827271L;

    public transient Level l;

    public transient ArrayList<Tile> grid[][];

    public Grid() {
    }

    @SuppressWarnings("unchecked")
	public Grid(int x, int y) {
        grid = new ArrayList[x][y];
    }

    public void add(int x, int y, Tile t) {
        if (grid.length < x || grid[0].length < y)
            throw new ArrayIndexOutOfBoundsException("Not in grid.");
        if (grid[x][y] == null) {
            grid[x][y] = new ArrayList<Tile>();
            grid[x][y].add(t);
        } else {
            grid[x][y].add(t);
        }
    }

    public Object get(int x, int y, int i) {
        if (grid[x][y] == null)
            return null;
        if (grid[x][y].size() >= i)
            return null;
        return grid[x][y].get(i);
    }

    public boolean move(int nx, int ny, int x, int y, Tile t) {
        for (int i = 0; i < grid[x][y].size(); i++) {
            if (grid[x][y].get(i) == t) {
                grid[nx][ny].add(grid[x][y].remove(i));
                return true;
            }
        }
        return false;
    }

    public RandomFight getRF(int x, int y) {
        if (grid.length <= x || grid[0].length <= y || x < 0 || y < 0)
            return null;
        for (int i = 0; i < grid[x][y].size(); i++)
            if (grid[x][y].get(i).rf != null)
                return grid[x][y].get(i).rf;
        return null;

    }

    public Object remove(int x, int y, int i) {
        if (grid[x][y] == null)
            return null;
        if (grid[x][y].size() >= i)
            return null;
        return grid[x][y].remove(i);
    }

    /**
     * Draw all visible parts of the grid to the screen based on the
     * players coordinates
     * 
     * @param x Player x coordinate
     * @param y Player y coordinate
     * @param graphics Graphics object to draw the grid to
     */
    public void draw(float x, float y, Graphics graphics) {

    	/*
    	 * Floor the player coordinates to get which tiles to draw
    	 */
    	int x_square = (int) x;
    	int y_square = (int) y;

    	/*
    	 * Pixel shift due to characters position float
    	 */
    	int x_off = (int) ((x - ((int)x))*16);
    	int y_off = (int) ((y - ((int)y))*16);

    	/*
    	 * Draw 10x10 swath of tiles for the game
    	 */
    	for(int j = 0; j < 11; j++)
    		for(int k = 0; k < 10; k++)
    			if(j + x_square >= 0 && j + x_square < getWidth() && k + y_square >= 0 && k + y_square < getHeight())
    				for(int i = 0; i < grid[j + x_square][k + y_square].size(); i++)
    					grid[j + x_square][k + y_square].get(i).draw(j, k, x_off, y_off, graphics);

    }

    public boolean canStepOn(int x, int y) {
        if (getWidth() <= x || getHeight() <= y || x < 0 || y < 0)
            return false;
        for (int i = 0; i < grid[x][y].size(); i++)
            if (!grid[x][y].get(i).canBeSteppedOn)
                return false;
        return true;
    }

    public GMenu hasMenu(int x, int y) {
        if (grid.length <= x || grid[0].length <= y || x < 0 || y < 0)
            return null;
        for (int i = 0; i < grid[x][y].size(); i++)
            if (grid[x][y].get(i).onClick != null)
                return grid[x][y].get(i).getMenu(x, y);
        return null;
    }

    public LevelChange changeLevel(int x, int y) {

        if(x < 0 && l.borderL != null && l.borderL[0] != null){
        	if(l.borderL[0].grid.canStepOn(l.borderL[0].grid.getWidth() - 1, (int) ClientState.player.getPose().getY() + l.borderoffset[0]))
                return new LevelChange(l.borderL[0].grid.getWidth(), (int) ClientState.player.getPose().getY() + l.borderoffset[0], l.borders[0], Direction.LEFT, Direction.LEFT);

        } else if(x >= l.grid.getWidth() && l.borderL != null && l.borderL[3] != null){
        	if(l.borderL[3].grid.canStepOn(0, (int) ClientState.player.getPose().getY() + l.borderoffset[3]))
        		return new LevelChange(-1, (int) ClientState.player.getPose().getY() + l.borderoffset[3], l.borders[3], Direction.RIGHT, Direction.RIGHT);

        // top border level change, need to set variables based on transition...
        } else if(y < 0 && l.borderL != null && l.borderL[1] != null){
            if(l.borderL[1].grid.canStepOn((int) ClientState.player.getPose().getX() + l.borderoffset[1], l.borderL[1].grid.getHeight() - 1))
            	return new LevelChange((int) ClientState.player.getPose().getX() + l.borderoffset[1], l.borderL[1].grid.getHeight(), l.borders[1], Direction.UP, Direction.UP);

        } else if(y >= l.grid.getHeight() && l.borderL != null && l.borderL[7] != null) {
            if(l.borderL[7].grid.canStepOn((int) ClientState.player.getPose().getX() + l.borderoffset[7], 0))
            	return new LevelChange((int) ClientState.player.getPose().getX() + l.borderoffset[7], -1, l.borders[7], Direction.DOWN, Direction.DOWN);

        }

        if(getWidth() <= x || getHeight() <= y || x < 0 || y < 0)
            return null;

        // TODO fix the 4 4 offset in the maps
        for(int i = 0; i < grid[x][y].size(); i++)
            if(grid[x][y].get(i).changeToLevel != -1)
            	return new LevelChange(grid[x][y].get(i).xnew+4, grid[x][y].get(i).ynew+4, grid[x][y].get(i).changeToLevel, grid[x][y].get(i).leaveDirection, grid[x][y].get(i).exitDir);
        

        return null;
    }

    public int getHeight() {
        return grid[0].length;
    }

    public int getWidth() {
        return grid.length;
    }

    // !!!IMPORTANT
    // GRID loads reference from master tile list so we want to load the tile
    // ids manualy instead of the tiles

    @SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        int x = ois.readInt();
        int y = ois.readInt();
        grid = new ArrayList[x][y];
        for (x = 0; x < grid.length; x++) {
            for (y = 0; y < grid[0].length; y++) {
                int l = ois.readInt();
                grid[x][y] = new ArrayList<Tile>();
                for (int j = 0; j < l; j++) {
                    grid[x][y].add(new Tile(ois.readInt()));
                }
            }
        }

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();

        oos.writeInt(grid.length);
        oos.writeInt(grid[0].length);
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                oos.writeInt(grid[x][y].size());
                for (int j = 0; j < grid[x][y].size(); j++) {
                    oos.writeInt(grid[x][y].get(j).id);
                }
            }
        }

    }

    public void initLevelReference(Level l) {
        this.l = l;
    }

	@Override
	public String toJSON() {

		String json = "{\"class\":\"" + this.getClass().getName() + "\"";
		
		json += ",\"grid\":[";
        for (int x = 0; x < grid.length; x++) {
    		json += "[";
        	for (int y = 0; y < grid[0].length; y++) {
        		json += "[";
                for (int j = 0; j < grid[x][y].size(); j++) {
                    json += grid[x][y].get(j).id;
            		if(j != grid[x][y].size()-1) json+= ",";
                }
        		json += "]";
        		if(y != grid[0].length-1) json+= ",";
            }
        	json += "]";
    		if(x != grid.length-1) json+= ",";
        }
        json += "]";	

		json += "}";

		return json;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromJSON(HashMap<String, Object> json) {

		Object[] g_map_0 = (Object[]) json.get("grid");
		
		grid = new ArrayList[g_map_0.length][((Object[])g_map_0[0]).length];

		for(int x = 0; x < g_map_0.length; x++){
			
			Object[] g_map_1 = (Object[]) g_map_0[x];
			for(int y = 0; y < g_map_1.length; y++){

				Object[] g_map_2 = (Object[]) g_map_1[y];
				grid[x][y] = new ArrayList<Tile>();

				for(int z = 0; z < g_map_2.length; z++){
					grid[x][y].add(
						new Tile(
							((Float)g_map_2[z]).intValue()
						)
					);
				}

			}
		}
	}
    
}
