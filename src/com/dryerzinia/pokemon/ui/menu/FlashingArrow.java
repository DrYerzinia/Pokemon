package com.dryerzinia.pokemon.ui.menu;
import java.awt.Graphics;
import java.awt.Image;

import com.dryerzinia.pokemon.util.ResourceLoader;

/**
 * The flashing arrow indicating that there is more text
 * to read in a text box.
 * @author jc
 *
 */
public class FlashingArrow {	
	private static final int FLASH_DELAY = 750; 	// ms between flashes 
	private static final int X = 145, Y = 129;
	private static final int WIDTH = 7, HEIGHT = 5;
	
	private Image sprite;
	private boolean visible;
	private long remainingTime;	// accumulated left over time between updates
	
	public FlashingArrow() {
		sprite = ResourceLoader.getSprite("ArrowDown.png");
		reset();
	}
	
	/**
	 * Update the state of the arrow's visibility.
	 * @param updateTime time between now and the last update.
	 */
	public void update(long updateTime) {
		if ((updateTime + remainingTime) / FLASH_DELAY > 0) {
			// toggle the arrow's visibility every FLASH_DELAY ms.
			visible = !visible;
		}
		// accumulate the remaining time so we can catch up on frames.
		remainingTime = (updateTime + remainingTime) % FLASH_DELAY;
	}
	
	/**
	 * Display the arrow if it is currently visible.
	 */
	public void render(Graphics g) {
		if (visible) {
			g.drawImage(sprite, X, Y, null);
		}
	}
	
	/**
	 * Reset the animation of the arrow.
	 */
	public void reset() {
		remainingTime = 0;
		visible = true;
	}
}
