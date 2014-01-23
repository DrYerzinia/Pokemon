package com.dryerzinia.pokemon.obj;
import java.awt.*;
import java.util.HashMap;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.ui.Fight;
import com.dryerzinia.pokemon.ui.UI;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.util.MysqlConnect;
import com.dryerzinia.pokemon.util.ResourceLoader;

public class TrainerPerson extends Person implements Actor {

    static final long serialVersionUID = -4342729814303857101L;

    protected int face;
    protected int x, y;
    protected int con = 0;
    protected int viewDist;
    protected transient Grid g;
    protected boolean hasBeenFought;

    public TrainerPerson() {
    }

    public TrainerPerson(String imgName, boolean cbso, GMenu onClick, int dir,
            int x, int y, int viewDist) {
        this.imgName = imgName;
        this.dir = dir;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        this.onClick = onClick;

        this.x = x;
        this.y = y;

        this.viewDist = viewDist;

        hasBeenFought = false;

        // onClick.container = this;

        sprite = new Image[4];

        sprite[0] = ResourceLoader.getSprite(imgName + "U.png");
        sprite[1] = ResourceLoader.getSprite(imgName + "D.png");
        sprite[2] = ResourceLoader.getSprite(imgName + "L.png");
        sprite[3] = ResourceLoader.getSprite(imgName + "R.png");

        Grid g = GameState.level.get(ClientState.player.level).grid;

        face = dir;// (int)(Math.random()*3);

    }

    protected void setImage(int x, int y) {
        // if(px == -1) img = sprite[dir];
        if (px == -1)
            img = sprite[face];
        else if (x <= 3) {
            img = sprite[3];
        } else if (x >= 5) {
            img = sprite[2];
        } else if (y <= 3) {
            img = sprite[1];
        } else if (y >= 5) {
            img = sprite[0];
        }
    }

    public boolean act(int x, int y) {
        Level l = GameState.level.get(level);
        for (int i = 0; i < viewDist; i++) {
            int x2 = x;
            int y2 = y;
            switch (face) {
            case 0:
                y2 -= i;
                break;
            case 1:
                y2 += i;
                break;
            case 2:
                x2 -= i;
                break;
            case 3:
                x2 += i;
                break;
            }
            if (x2 == x + 4 && y2 == y + 4 && !hasBeenFought) {
                Pokemon poke2[] = ClientState.player.poke.belt;
                Pokemon poke[] = new Pokemon[6];
                for (int j = 0; j < 6; j++) {
                    if (poke2[j] == null)
                        break;
                    poke[j] = new Pokemon(poke2[j]);
                }
                Fight f = new Fight(poke[0]);
                Player pl = new Player();
                MysqlConnect.PokemonContainer pc = new MysqlConnect.PokemonContainer(
                        null, poke);
                pl.poke = pc;
                f.enemyPlayer = pl;
                UI.overlay.o = f;
                UI.overlay.o.active = true;
                hasBeenFought = true;
            }
        }
        return false;
    }

    public void draw(int x, int y, Graphics g) {
        setImage(x, y);
        super.draw(x, y, g);
        act(this.x, this.y);
    }

    public void draw(int x, int y, int xo, int yo, Graphics g) {
        setImage(x, y);
        super.draw(x, y, xo, yo, g);
        act(this.x, this.y);
    }

    public void initializeSecondaryReferences(Grid g) {
        this.g = g;
    }

    public Object deepCopy() {
        GMenu g = null;
        if (onClick != null)
            g = (GMenu) onClick.deepCopy();
        return new TrainerPerson(new String(imgName), canBeSteppedOn, onClick,
                dir, x, y, viewDist);
    }
    
	@Override
	public String toJSON() {

		String json = super.toJSON();
		json = json.substring(0,json.length()-1);

		json += ",'px':" + px;
		json += ",'py':" + py;

		json += ",'face':" + face;
		json += ",'x':" + x;
		json += ",'y':" + y;
		json += ",'con':" + con;
		json += ",'viewDist':" + viewDist;

		json += ",'hasBeenFought':" + hasBeenFought;

        json += "}";

        return json;

	}

}
