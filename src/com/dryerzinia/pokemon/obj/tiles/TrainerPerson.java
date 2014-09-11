package com.dryerzinia.pokemon.obj.tiles;
import java.awt.*;
import java.util.HashMap;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.ui.Fight;
import com.dryerzinia.pokemon.ui.UI;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.Database;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.GameState;

public class TrainerPerson extends Person implements Actor {

    static final long serialVersionUID = -4342729814303857101L;

    protected Direction face;
    protected int x, y;
    protected int con = 0;
    protected int viewDist;
    protected transient Grid g;
    protected boolean hasBeenFought;

    public TrainerPerson() {
    }

    public TrainerPerson(String imgName, boolean cbso, GMenu onClick, Direction dir,
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

        Grid g = GameState.getMap().getLevel(ClientState.player.getPose().getLevel()).grid;

        face = dir;// (int)(Math.random()*3);

    }

    protected void setImage(int x, int y) {
        // if(px == -1) img = sprite[dir];
        if (px == -1)
            img = sprite[face.getValue()];
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

    public boolean act() {
/*
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
        */
        return false;
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

    public void fromJSON(HashMap<String, Object> json){

    	super.fromJSON(json);

    	face = Direction.getFromString((String) json.get("face"));
    	x = ((Float) json.get("x")).intValue();
    	y = ((Float) json.get("y")).intValue();

    	con = ((Float) json.get("con")).intValue();
    	viewDist = ((Float) json.get("viewDist")).intValue();

    	hasBeenFought = ((Boolean) json.get("hasBeenFought")).booleanValue();

	}
    
    @Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

}
