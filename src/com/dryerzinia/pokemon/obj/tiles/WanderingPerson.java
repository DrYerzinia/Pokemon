package com.dryerzinia.pokemon.obj.tiles;

import java.util.HashMap;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.ui.menu.GMenu;
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

    public WanderingPerson(String imgName, boolean cbso,
            Direction dir, int w, int h, int rx, int ry, float x, float y, Grid g) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;

        this.w = w;
        this.h = h;

        this.rx = rx;
        this.ry = ry;

        this.x = x;
        this.y = y;

        this.g = g;

        loadImage();

        this.dir = dir;

    }

    protected boolean wander() {
        boolean canMove = false;
        boolean changed = false;
        int pos = (int) (Math.random() * 20);
        if (pos < 4) {
            if (dir.getValue() != pos) {
                dir = Direction.get(pos);
                changed = true;
            }
        }
        pos = (int) (Math.random() * 20);
        if (pos > 2 && con % 4 == 0) {
            if (dir == Direction.UP) {
                if (!PokemonServer.isPlayer((int)x, (int)y - 1, level)
                        && g.canStepOn((int)x, (int)y - 1))
                    canMove = true;
            } else if (dir == Direction.DOWN) {
                if (!PokemonServer.isPlayer((int)x, (int)y + 1, level)
                        && g.canStepOn((int)x, (int)y + 1))
                    canMove = true;
            } else if (dir == Direction.LEFT) {
                if (!PokemonServer.isPlayer((int)x - 1, (int)y, level)
                        && g.canStepOn((int)x - 1, (int)y))
                    canMove = true;
            } else if (dir == Direction.RIGHT) {
                if (!PokemonServer.isPlayer((int)x + 1, (int)y, level)
                        && g.canStepOn((int)x + 1, (int)y))
                    canMove = true;
            }
        }
        if (canMove && con % 7 == 0) {
            changed = true;
            if (dir == Direction.UP) {
                g.move((int)x, (int)y - 1, (int)x,(int)y, this);
                ry--;
                y--;
            } else if (dir == Direction.DOWN) {
                g.move((int)x, (int)y + 1, (int)x, (int)y, this);
                ry++;
                y++;
            } else if (dir == Direction.LEFT) {
                g.move((int)x - 1, (int)y, (int)x, (int)y, this);
                rx--;
                x--;
            } else if (dir == Direction.RIGHT) {
                g.move((int)x + 1, (int)y, (int)x, (int)y, this);
                rx++;
                x++;
            }
        }
        con++;
        return changed;
    }

    public boolean act() {
    	return wander();
        //return false;
    }

    public void initializeSecondaryReferences(Grid g) {
        this.g = g;
    }

    public Object deepCopy() {
        return new WanderingPerson(new String(imgName), canBeSteppedOn, dir,
                w, h, rx, ry, x, y, this.g);
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
