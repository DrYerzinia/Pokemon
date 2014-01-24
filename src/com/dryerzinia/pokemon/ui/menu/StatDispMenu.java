package com.dryerzinia.pokemon.ui.menu;
import java.awt.*;
import java.util.HashMap;

import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.util.JSONObject;

public class StatDispMenu extends GMenu {

    Pokemon p;

    public StatDispMenu(Pokemon p) {
        this.p = p;
        message = "";
        init();
        x = 6;
        y = 0;
        w = 4;
        h = 6;
    }

    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font("monospaced", 0, 12));
        g.drawString("ATTACK", 5 + x * 16, 15 + y * 16);
        g.drawString("DEFENSE", 5 + x * 16, 35 + y * 16);
        g.drawString("SPEED", 5 + x * 16, 55 + y * 16);
        g.drawString("SPECIAL", 5 + x * 16, 75 + y * 16);
        g.setFont(new Font("monospaced", Font.BOLD, 12));
        g.drawString("" + p.getAttack(), 45 + x * 16, 25 + y * 16);
        g.drawString("" + p.getDefense(), 45 + x * 16, 45 + y * 16);
        g.drawString("" + p.getSpeed(), 45 + x * 16, 65 + y * 16);
        g.drawString("" + p.getSpecial(), 45 + x * 16, 85 + y * 16);
    }

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {
		// TODO Auto-generated method stub
	}

}
