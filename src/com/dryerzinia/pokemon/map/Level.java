package com.dryerzinia.pokemon.map;
import java.io.*;
import java.awt.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.obj.RandomFight;
import com.dryerzinia.pokemon.util.JSON;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.util.JSONObject;

public class Level implements Serializable, JSON {

    static final long serialVersionUID = -5293673761061322573L;

    public Grid g;
    // public Player c; // Character Tile

    // int x, y;
    // public int direction;

    public transient boolean midmove = false;
    public transient boolean canMove = true;
    public transient Level borderL[] = new Level[9];
    public int borders[] = new int[9];
    public int borderoffset[] = new int[9];
    public int id; // TODO: appearse unused figureout wtf todo with this

    public Level() {
    }

    // public Level(int x, int y, Grid g, int d, Player c){
    public Level(Grid g) {
        // this.direction = d;
        // this.x = x;
        // this.y = y;
        this.g = g;
        // this.c = c;
        for (int i = 0; i < 9; i++)
            borders[i] = -1;
    }

    public void draw(Graphics gg) {
        if (ClientState.player != null) {
            if (midmove && canMove) {
                int xo = 0;
                int yo = 0;
                if (ClientState.player.dir == 0)
                    yo = 8;
                if (ClientState.player.dir == 1)
                    yo = -8;
                if (ClientState.player.dir == 2)
                    xo = 8;
                if (ClientState.player.dir == 3)
                    xo = -8;
                g.draw(ClientState.player.x, ClientState.player.y, xo,
                        yo, gg);
                if (borderL != null) {
                    if (borderL[0] != null)
                        borderL[0].g.draw(ClientState.player.x
                                + borderL[0].g.g.length,
                                ClientState.player.y + borderoffset[0], xo,
                                yo, gg, false);
                    if (borderL[3] != null)
                        borderL[3].g.draw(
                        		ClientState.player.x - g.g.length,
                        		ClientState.player.y + borderoffset[3], xo,
                                yo, gg, false);
                    if (borderL[1] != null)
                        borderL[1].g.draw(ClientState.player.x
                                + borderoffset[1], ClientState.player.y
                                + borderL[1].g.g[0].length, xo, yo, gg, false);
                    if (borderL[7] != null)
                        borderL[7].g.draw(ClientState.player.x
                                + borderoffset[7], ClientState.player.y
                                - g.g[0].length, xo, yo, gg, false);
                }
            } else {
                g.draw(ClientState.player.x, ClientState.player.y, gg);
                if (borderL != null) {
                    if (borderL[0] != null)
                        borderL[0].g.draw(ClientState.player.x
                                + borderL[0].g.g.length,
                                ClientState.player.y + borderoffset[0], gg,
                                false);
                    if (borderL[3] != null)
                        borderL[3].g.draw(
                                ClientState.player.x - g.g.length,
                                ClientState.player.y + borderoffset[3], gg,
                                false);
                    if (borderL[1] != null)
                        borderL[1].g.draw(ClientState.player.x
                                + borderoffset[1], ClientState.player.y
                                + borderL[1].g.g[0].length, gg, false);
                    if (borderL[7] != null)
                        borderL[7].g.draw(ClientState.player.x
                                + borderoffset[7], ClientState.player.y
                                - g.g[0].length, gg, false);
                }
            }
            // if(midmove) c[direction+4].draw(4, 4, gg);
            // else c[direction].draw(4, 4, gg);
            ClientState.player.draw(gg);
        }
    }

    public void act() {
        if (ClientState.player != null)
            g.act(ClientState.player.x, ClientState.player.y);
    }

    public Pokemon attacked(Player p) {
        RandomFight rf = g.getRF(p.x, p.y);
        if (rf != null)
            return rf.getAttack();
        return null;
    }

    public void moveRight() {
        canMove = g.canStepOn(ClientState.player.x + 1,
                ClientState.player.y);
        if (canMove)
            ClientState.player.x++;
    }

    public void moveLeft() {
        canMove = g.canStepOn(ClientState.player.x - 1,
                ClientState.player.y);
        if (canMove)
            ClientState.player.x--;
    }

    public void moveUp() {
        canMove = g.canStepOn(ClientState.player.x,
                ClientState.player.y - 1);
        if (canMove)
            ClientState.player.y--;
    }

    public void moveDown() {
        canMove = g.canStepOn(ClientState.player.x,
                ClientState.player.y + 1);
        if (canMove)
            ClientState.player.y++;
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        g.initLevelReference(this);
        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    public String toJSON(){

    	String json = "{'class':'" + this.getClass().getName() + "'";
    	
    	json += ",'g':" + JSONObject.objectToJSON(g);

        json += ",'borders':" + JSONArray.intArrayToJSON(borders);
        json += ",'borderoffset':" + JSONArray.intArrayToJSON(borderoffset);
        json += ",'id':" + id;

        json += "}";

        return json;

    }

    public void fromJSON(HashMap<String, Object> json){

    	g.initLevelReference(this);

    }
    
    public void initLevelReferences(ArrayList<Level> l) {
        borderL = new Level[9];
        for (int i = 0; i < 9; i++) {
            if (borders[i] != -1)
                borderL[i] = l.get(borders[i]);
        }
    }

}
