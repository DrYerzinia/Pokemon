package com.dryerzinia.pokemon.ui.menu;
import java.awt.*;
import java.util.HashMap;

import com.dryerzinia.pokemon.obj.Player;

public class MoneyMenu extends GMenu {

    private static final long serialVersionUID = -4996520434232882739L;

    Player p;

    public MoneyMenu(Player p) {
        this.p = p;
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
        g.drawString("$ " + p.getMoney(), 90, 20);
    }

	@Override
	public String toJSON() {

		String json = super.toJSON();

        return json;

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {
		// TODO Auto-generated method stub
	}
    
}
