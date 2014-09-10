package com.dryerzinia.pokemon.obj.tiles;

import java.awt.Graphics;

public class Water extends Tile {

	private static final long serialVersionUID = 2585536972543470396L;

	@Override
    public void draw(float x, float y, int xo, int yo, Graphics g) {

		long time = System.currentTimeMillis();

		int period = 2000;
		int segments = 8;
		
		int offset = (int) ((time/(period/segments))%segments);
		if(offset > 4) offset = 8 - offset;

		int dx1 = (int) (x * 16 + pixelOffsetX - xo + offset);
		int dy1 = (int) (y * 16 + pixelOffsetY - yo);
		int dx2 = dx1 + 16 - offset;
		int dy2 = dy1 + 16;

		int sx1 = 0;
		int sy1 = 0;
		int sx2 = 16-offset;
		int sy2 = 16;

		g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);

		if(offset != 0){

			dx1 = (int) (x * 16 + pixelOffsetX - xo);
			dx2 = dx1 + offset;

			sx1 = 16-offset;
			sx2 = 16;

			g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);

		}
		
	}

}
