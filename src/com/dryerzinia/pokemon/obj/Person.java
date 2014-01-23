package com.dryerzinia.pokemon.obj;
import java.io.*;
import java.util.HashMap;
import java.awt.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.util.ResourceLoader;

public class Person extends Tile implements Actor {

    static final long serialVersionUID = -47859827707060573L;

    public static final int A_MOVED = 0;
    public static final int A_TALKING_TO = 1;

    public static final GMenu ALREADY_ACTIVE_MENU = new GMenu(
            "He's talking to \nsomeone else...", null, 0, 6, 10, 3);

    protected transient Image sprite[];
    protected int px = -1, py = -1;

    public int dir;
    public int x, y;
    public int level;

    protected transient int directionBeforeTalk;
    protected transient boolean wasTalking = false;
    protected transient boolean wasTalkingToYou = false;

    public Person() {
    }

    public Person(String imgName, boolean cbso, GMenu onClick, int dir) {
        this.imgName = imgName;
        this.dir = dir;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        this.onClick = onClick;

        onClick.container = this;

        loadImage();
    }

    public void loadImage() {

        sprite = new Image[4];

        sprite[0] = ResourceLoader.getSprite(imgName + "U.png");
        sprite[1] = ResourceLoader.getSprite(imgName + "D.png");
        sprite[2] = ResourceLoader.getSprite(imgName + "L.png");
        sprite[3] = ResourceLoader.getSprite(imgName + "R.png");

    }

    public void draw(int x, int y, Graphics g) {
        setImage(x, y);
        super.draw(x, y, 0, 0, g);
    }

    public void draw(int x, int y, int xo, int yo, Graphics g) {
        setImage(x, y);
        super.draw(x, y, xo, yo, g);
    }

    public void deactivate() {
        px = -1;
        py = -1;
    }

    protected void setImage(int x, int y) {

        try {
            if ((onClick != null && !onClick.active) || onClick == null) {
                if (wasTalking && wasTalkingToYou) {
                    dir = directionBeforeTalk;
                    wasTalkingToYou = false;
                    Client.writeActor(this, A_TALKING_TO);
                }
                img = sprite[dir];
                directionBeforeTalk = dir;
                wasTalking = false;
            } else if (!wasTalkingToYou) {
                img = sprite[dir];
                wasTalking = true;
            } else if (x <= 3) {
                wasTalking = true;
                dir = 3;
                img = sprite[3];
            } else if (x >= 5) {
                wasTalking = true;
                dir = 2;
                img = sprite[2];
            } else if (y <= 3) {
                wasTalking = true;
                dir = 1;
                img = sprite[1];
            } else if (y >= 5) {
                wasTalking = true;
                dir = 0;
                img = sprite[0];
            }
        } catch (NullPointerException npe) {
            if (sprite == null) {
                System.out.println("Sprites not loaded!");
                if (imgName != null && !imgName.equals(""))
                    loadImage();
            }
        }
    }

    public GMenu getMenu(int x, int y) {
        if (!onClick.active) {
            px = x;
            py = y;
            onClick.active = true;
            wasTalkingToYou = true;
            setImage(x - (int)ClientState.player.x, y - (int)ClientState.player.y);
            Client.writeActor(this, A_TALKING_TO);
            return onClick;
        } else {
            return ALREADY_ACTIVE_MENU;
        }

    }

    public boolean act(int x, int y) {
        return false;
    }

    public void writePersonID(ObjectOutputStream out) throws IOException {

        // TODO: Make PokemonGame function Serializable

        out.writeInt(id);
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(dir);
        out.writeInt(level);
    }

    public Object deepCopy() {
        GMenu g = null;
        if (onClick != null)
            g = (GMenu) onClick.deepCopy();
        return new Person(new String(imgName), canBeSteppedOn, g, dir);
    }
    
	@Override
	public String toJSON() {

		String json = super.toJSON();
		json = json.substring(0,json.length()-1);

		json += ",'px':" + px;
		json += ",'py':" + py;

		json += ",'dir':" + dir;
		json += ",'x':" + x;
		json += ",'y':" + y;
		json += ",'level':" + level;

        json += "}";

        return json;

	}

}
