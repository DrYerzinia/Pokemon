import java.io.*;
import java.awt.*;
import java.util.*;

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
        if (PokemonGame.pokeg.Char != null) {
            if (midmove && canMove) {
                int xo = 0;
                int yo = 0;
                if (PokemonGame.pokeg.Char.dir == 0)
                    yo = 8;
                if (PokemonGame.pokeg.Char.dir == 1)
                    yo = -8;
                if (PokemonGame.pokeg.Char.dir == 2)
                    xo = 8;
                if (PokemonGame.pokeg.Char.dir == 3)
                    xo = -8;
                g.draw(PokemonGame.pokeg.Char.x, PokemonGame.pokeg.Char.y, xo,
                        yo, gg);
                if (borderL != null) {
                    if (borderL[0] != null)
                        borderL[0].g.draw(PokemonGame.pokeg.Char.x
                                + borderL[0].g.g.length,
                                PokemonGame.pokeg.Char.y + borderoffset[0], xo,
                                yo, gg, false);
                    if (borderL[3] != null)
                        borderL[3].g.draw(
                                PokemonGame.pokeg.Char.x - g.g.length,
                                PokemonGame.pokeg.Char.y + borderoffset[3], xo,
                                yo, gg, false);
                    if (borderL[1] != null)
                        borderL[1].g.draw(PokemonGame.pokeg.Char.x
                                + borderoffset[1], PokemonGame.pokeg.Char.y
                                + borderL[1].g.g[0].length, xo, yo, gg, false);
                    if (borderL[7] != null)
                        borderL[7].g.draw(PokemonGame.pokeg.Char.x
                                + borderoffset[7], PokemonGame.pokeg.Char.y
                                - g.g[0].length, xo, yo, gg, false);
                }
            } else {
                g.draw(PokemonGame.pokeg.Char.x, PokemonGame.pokeg.Char.y, gg);
                if (borderL != null) {
                    if (borderL[0] != null)
                        borderL[0].g.draw(PokemonGame.pokeg.Char.x
                                + borderL[0].g.g.length,
                                PokemonGame.pokeg.Char.y + borderoffset[0], gg,
                                false);
                    if (borderL[3] != null)
                        borderL[3].g.draw(
                                PokemonGame.pokeg.Char.x - g.g.length,
                                PokemonGame.pokeg.Char.y + borderoffset[3], gg,
                                false);
                    if (borderL[1] != null)
                        borderL[1].g.draw(PokemonGame.pokeg.Char.x
                                + borderoffset[1], PokemonGame.pokeg.Char.y
                                + borderL[1].g.g[0].length, gg, false);
                    if (borderL[7] != null)
                        borderL[7].g.draw(PokemonGame.pokeg.Char.x
                                + borderoffset[7], PokemonGame.pokeg.Char.y
                                - g.g[0].length, gg, false);
                }
            }
            // if(midmove) c[direction+4].draw(4, 4, gg);
            // else c[direction].draw(4, 4, gg);
            PokemonGame.pokeg.Char.draw(gg);
        }
    }

    public void act() {
        if (PokemonGame.pokeg.Char != null)
            g.act(PokemonGame.pokeg.Char.x, PokemonGame.pokeg.Char.y);
    }

    public Pokemon attacked(Player p) {
        RandomFight rf = g.getRF(p.x, p.y);
        if (rf != null)
            return rf.getAttack();
        return null;
    }

    public void moveRight() {
        canMove = g.canStepOn(PokemonGame.pokeg.Char.x + 1,
                PokemonGame.pokeg.Char.y);
        if (canMove)
            PokemonGame.pokeg.Char.x++;
    }

    public void moveLeft() {
        canMove = g.canStepOn(PokemonGame.pokeg.Char.x - 1,
                PokemonGame.pokeg.Char.y);
        if (canMove)
            PokemonGame.pokeg.Char.x--;
    }

    public void moveUp() {
        canMove = g.canStepOn(PokemonGame.pokeg.Char.x,
                PokemonGame.pokeg.Char.y - 1);
        if (canMove)
            PokemonGame.pokeg.Char.y--;
    }

    public void moveDown() {
        canMove = g.canStepOn(PokemonGame.pokeg.Char.x,
                PokemonGame.pokeg.Char.y + 1);
        if (canMove)
            PokemonGame.pokeg.Char.y++;
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

    	String json = "{'class':'Level'";
    	
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
