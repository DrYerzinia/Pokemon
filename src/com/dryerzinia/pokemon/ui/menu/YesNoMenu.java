package com.dryerzinia.pokemon.ui.menu;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.List;

import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.util.StringStore;
import com.dryerzinia.pokemon.input.InputController;
import com.dryerzinia.pokemon.obj.ClientState;

public class YesNoMenu implements Menu {
	private static final int x = 224, y = 112;
	public enum Selection {
		YES, NO;
	}	
	private Selection selection;
	private Image arrow;
	private YesNoMenuListener listener;
	
	public YesNoMenu() {
		selection = Selection.YES;
		arrow = ResourceLoader.getSprite("ArrowRight.png");
	}

	@Override
	public void handleInput() {
		boolean up = ClientState.inputDevice.isButtonDown(InputController.Button.UP);
		boolean down = ClientState.inputDevice.isButtonDown(InputController.Button.DOWN);
		
		if (ClientState.inputDevice.isButtonDown(InputController.Button.START)) {
			listener.madeSelection(selection);
		}
		
		if (up && down) return;
		
		if (up) {
			if (selection == Selection.NO) 
				selection = Selection.YES;
		}
		if (down) {
			if (selection == Selection.YES)
				selection = Selection.NO;
		}		
	}

	@Override
	public void update(long deltaTime) {
		
	}

	@Override
	public void render(Graphics g) {
		renderBox(g);
		renderText(g);
		renderArrow(g);
	}
	
	private void renderBox(Graphics g) {
		// draw the left and right edges first since we need to
		// overlap them by 16px for the right sized box.
		g.drawImage(rEdge, x+64, y+16, x+96, y+48, 0, 0, 16, 16, null);
		g.drawImage(lEdge, x, y+16, x+32, y+48, 0, 0, 16, 16, null);
		
		g.drawImage(tlCorner, x, y, x+32, y+32, 0, 0, 16, 16, null);
		g.drawImage(blCorner, x, y+48, x+32, y+80, 0, 0, 16, 16, null);
		
		g.drawImage(tEdge, x+32, y, x+64, y+32, 0, 0, 16, 16, null);
		g.drawImage(bEdge, x+32, y+48, x+64, y+80, 0, 0, 16, 16, null);
		
		g.drawImage(trCorner, x+64, y, x+96, y+32, 0, 0, 16, 16, null);
		g.drawImage(brCorner, x+64, y+48, x+96, y+80, 0, 0, 16, 16, null);
		
		// fill in the center
		g.setColor(Menu.bgColor);
		g.fillRect(x+32, y+32, 32, 16);
	}
	
	private void renderText(Graphics g) {
		g.setFont(Menu.menuFont);
		g.setColor(Menu.fontColor);
		g.drawString(StringStore.getString(4, ClientState.LOCALE), x+32, y+32);
		g.drawString(StringStore.getString(5, ClientState.LOCALE), x+32, y+57);
	}
	
	private void renderArrow(Graphics g) {
		int arrowX = x+16, arrowY = y+20;
		if (selection == Selection.NO)
			arrowY += 25;
		g.drawImage(arrow, arrowX, arrowY, arrowX+10, arrowY+14,
				0, 0, 5, 7, null);
	}
	
	public void registerListener(YesNoMenuListener listener) {
		this.listener = listener;
	}

}
