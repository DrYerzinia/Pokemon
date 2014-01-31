package com.dryerzinia.pokemon.ui.menu;
import java.awt.*;
import java.util.HashMap;

import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.util.JSONObject;

public class MoneyMenu extends GMenu {

    private static final long serialVersionUID = -4996520434232882739L;

    public MoneyMenu(Player p) {

    	message = "";
        init();
        x = 5;
        y = 0;
        w = 5;
        h = 2;

    }

    public void draw(Graphics g) {

    	super.draw(g);

        g.setColor(Color.WHITE);
        g.fillRect(103, 0, 38, 12);
        g.setColor(Color.BLACK);
        g.drawString("MONEY", 105, 10);
        g.drawString("$ " + ClientState.player.getMoney(), 90, 20);

    }

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

	@Override
    public void fromJSON(HashMap<String, Object> json){

		super.fromJSON(json);

	}
    
}
