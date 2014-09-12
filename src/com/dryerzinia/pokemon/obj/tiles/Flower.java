package com.dryerzinia.pokemon.obj.tiles;

import java.awt.Graphics;

public class Flower extends Tile {

	private static final long serialVersionUID = -5927606477512395929L;

	@Override
    public void draw(float x, float y, Graphics g) {


		long time = System.currentTimeMillis();

		int period = 1500;

		int pos = (int) (time%period);

		int dx1 = (int) (x * 16 + pixelOffsetX);
		int dy1 = (int) (y * 16 + pixelOffsetY);
		int dx2 = dx1 + 8;
		int dy2 = dy1 + 8;

		if(pos < period*0.375)
			g.drawImage(img, dx1, dy1, dx2, dy2, 0, 0, 8, 8, null);

		else if(pos >= period*0.375 && pos < period*0.75)
			g.drawImage(img, dx1, dy1, dx2, dy2, 8, 8, 16, 16, null);

		else if(pos >= period*0.75)
			g.drawImage(img, dx1, dy1, dx2, dy2, 8, 0, 16, 8, null);

	}

}
