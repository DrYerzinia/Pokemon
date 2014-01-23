package com.dryerzinia.pokemon.obj;

import java.awt.Graphics;

public class Flower extends Tile {

	@Override
    public void draw(int x, int y, int xo, int yo, Graphics g) {


		long time = System.currentTimeMillis();

		int period = 1500;

		int pos = (int) (time%period);

		int dx1 = x * 16 + pixelOffsetX - xo;
		int dy1 = y * 16 + pixelOffsetY - yo;
		int dx2 = dx1 + 8;
		int dy2 = dy1 + 8;

		if(pos < period*0.5)
			g.drawImage(img, dx1, dy1, dx2, dy2, 0, 0, 8, 8, null);

		else if(pos >= period*0.5 && pos < period*0.75)
			g.drawImage(img, dx1, dy1, dx2, dy2, 8, 8, 16, 16, null);

		else if(pos >= period*0.75)
			g.drawImage(img, dx1, dy1, dx2, dy2, 8, 0, 16, 8, null);

	}

}
