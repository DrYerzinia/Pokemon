package com.dryerzinia.pokemon.obj;
import java.awt.*;
import java.util.HashMap;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.ui.menu.GMenu;

public class WanderingPerson extends Person implements Actor {

    static final long serialVersionUID = -3752479366750590617L;

    protected int w, h;
    protected int rx = 0, ry = 0;
    protected int con = 0;

    protected transient Grid g;

    public WanderingPerson() {
    }

    public WanderingPerson(String imgName, boolean cbso, GMenu onClick,
            int dir, int w, int h, int rx, int ry, int x, int y, Grid g) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        this.onClick = onClick;

        this.w = w;
        this.h = h;

        this.rx = rx;
        this.ry = ry;

        this.x = x;
        this.y = y;

        this.g = g;

        onClick.container = this;

        loadImage();

        dir = (int) (Math.random() * 3);

    }

    protected boolean wander(int xx, int yy) {
        boolean canMove = false;
        boolean changed = false;
        int pos = (int) (Math.random() * 20);
        if (pos < 4) {
            if (dir != pos) {
                dir = pos;
                changed = true;
            }
        }
        pos = (int) (Math.random() * 20);
        if (pos > 2 && con % 4 == 0) {
            if (dir == 0) {
                if (!PokemonServer.isPlayer(x, y - 1, level)
                        && g.canStepOnB(x, y - 1))
                    canMove = true;
            } else if (dir == 1) {
                if (!PokemonServer.isPlayer(x, y + 1, level)
                        && g.canStepOnB(x, y + 1))
                    canMove = true;
            } else if (dir == 2) {
                if (!PokemonServer.isPlayer(x - 1, y, level)
                        && g.canStepOnB(x - 1, y))
                    canMove = true;
            } else if (dir == 3) {
                if (!PokemonServer.isPlayer(x + 1, y, level)
                        && g.canStepOnB(x + 1, y))
                    canMove = true;
            }
        }
        if (canMove && con % 7 == 0) {
            changed = true;
            if (dir == 0) {
                g.move(x, y - 1, x, y, this);
                ry--;
                y--;
            } else if (dir == 1) {
                g.move(x, y + 1, x, y, this);
                ry++;
                y++;
            } else if (dir == 2) {
                g.move(x - 1, y, x, y, this);
                rx--;
                x--;
            } else if (dir == 3) {
                g.move(x + 1, y, x, y, this);
                rx++;
                x++;
            }
        }
        con++;
        return changed;
    }

    public boolean act(int x, int y) {
        if ((onClick != null && !onClick.active) || onClick == null)
            return wander(x, y);
        return false;
    }

    public void draw(int x, int y, Graphics g) {
        setImage(x, y);
        super.draw(x, y, g);
    }

    public void draw(int x, int y, int xo, int yo, Graphics g) {
        setImage(x, y);
        super.draw(x, y, xo, yo, g);
    }

    public void initializeSecondaryReferences(Grid g) {
        this.g = g;
    }

    public Object deepCopy() {
        GMenu g = null;
        if (onClick != null)
            g = (GMenu) onClick.deepCopy();
        return new WanderingPerson(new String(imgName), canBeSteppedOn, g, dir,
                w, h, rx, ry, x, y, this.g);
    }

	@Override
	public String toJSON() {

		String json = super.toJSON();
		json = json.substring(0,json.length()-1);

		json += ",'px':" + px;
		json += ",'py':" + py;

		json += ",'w':" + w;
		json += ",'h':" + h;
		json += ",'rx':" + rx;
		json += ",'ry':" + ry;

		json += ",'con':" + con;

        json += "}";

        return json;

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {
		// TODO Auto-generated method stub
	}

}
