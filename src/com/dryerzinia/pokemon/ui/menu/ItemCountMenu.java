package com.dryerzinia.pokemon.ui.menu;
import java.awt.*;
import java.util.HashMap;

import com.dryerzinia.pokemon.util.JSONObject;

public class ItemCountMenu extends GMenu {

	private static final long serialVersionUID = 3982078572621853775L;

	int index;
    int price;
    int count;

    public ItemCountMenu(int index, int price) {
        this.index = index;
        this.price = price;
        this.count = 1;
        message = "";
        init();
        x = 3;
        y = 5;
        w = 7;
        h = 2;
    }

    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(Color.BLACK);
        g.drawString("x" + count, x * 16 + 10, y * 16 + 20);
        g.drawString("$" + (price * count), x * 16 + 50, y * 16 + 20);
    }

    public int totalCost() {
        return price * count;
    }

    public void pressUp() {
        count++;
        if (count == 100)
            count = 1;
    }

    public void pressDown() {
        count--;
        if (count == 0)
            count = 99;
    }


	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

	@Override
    public void fromJSON(HashMap<String, Object> json){

		super.fromJSON(json);

	    index = ((Float) json.get("index")).intValue();
	    price = ((Float) json.get("price")).intValue();
	    count = ((Float) json.get("count")).intValue();

	}
    
}
