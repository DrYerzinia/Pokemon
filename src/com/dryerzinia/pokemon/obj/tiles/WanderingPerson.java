package com.dryerzinia.pokemon.obj.tiles;

import java.util.HashMap;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.obj.Actor;

public class WanderingPerson extends Person implements Actor {

    static final long serialVersionUID = -3752479366750590617L;

    protected int w, h;
    protected int rx = 0, ry = 0;
    protected int con = 0;

    protected transient Grid g;

    public WanderingPerson() {
    }

    public WanderingPerson(String imgName, boolean cbso, int w, int h, int rx, int ry, Pose location, Grid g) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;

        this.w = w;
        this.h = h;

        this.rx = rx;
        this.ry = ry;

        this.location = location;

        this.g = g;

        loadImage();

    }

    protected boolean wander() {

    	boolean canMove = false;
        boolean changed = false;

        int pos = (int) (Math.random() * 20);

        if (pos < 4) {
            if (location.facing().getValue() != pos) {
                location.changeDirection(Direction.get(pos));
                changed = true;
            }
        }

        Direction dir = location.facing();
        int level = location.getLevel();
        int x = (int) location.getX();
        int y = (int) location.getY();

        pos = (int) (Math.random() * 20);

        if (pos > 2 && con % 4 == 0) {
            if (dir == Direction.UP) {
                if (!PokemonServer.isPlayer(x, y - 1, level)
                        && g.canStepOn(x, y - 1))
                    canMove = true;
            } else if (dir == Direction.DOWN) {
                if (!PokemonServer.isPlayer(x, y + 1, level)
                        && g.canStepOn(x, y + 1))
                    canMove = true;
            } else if (dir == Direction.LEFT) {
                if (!PokemonServer.isPlayer(x - 1, y, level)
                        && g.canStepOn(x - 1, y))
                    canMove = true;
            } else if (dir == Direction.RIGHT) {
                if (!PokemonServer.isPlayer(x + 1, y, level)
                        && g.canStepOn(x + 1, y))
                    canMove = true;
            }
        }

        if (canMove && con % 7 == 0) {

        	changed = true;

        	if (dir == Direction.UP) {

        		g.move(x, y - 1, x, y, this);
                ry--;
                y--;

        	} else if (dir == Direction.DOWN) {

        		g.move(x, y + 1, x, (int)y, this);
                ry++;
                y++;

        	} else if (dir == Direction.LEFT) {

        		g.move(x - 1, y, x, y, this);
                rx--;
                x--;

        	} else if (dir == Direction.RIGHT) {

        		g.move(x + 1, y, x, y, this);
                rx++;
                x++;

        	}
        }

        con++;

        if(changed){
        	location.setX(x);
        	location.setY(y);
        	location.changeDirection(dir);
        }

        return changed;

    }

    public boolean act() {

    	return wander();

    }

    public void initializeSecondaryReferences(Grid g) {
        this.g = g;
    }

    public Object deepCopy() {
        return new WanderingPerson(new String(imgName), canBeSteppedOn,
                w, h, rx, ry, location, this.g);
    }

    public void fromJSON(HashMap<String, Object> json){

    	super.fromJSON(json);

        w = ((Float) json.get("w")).intValue();
        h = ((Float) json.get("h")).intValue();;
        rx = ((Float) json.get("rx")).intValue();
        ry = ((Float) json.get("ry")).intValue();
        con = ((Float) json.get("con")).intValue();

    }

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

}
