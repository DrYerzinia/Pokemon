package com.dryerzinia.pokemon.ui.menu;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import com.dryerzinia.pokemon.util.ResourceLoader;


public interface Menu {
	
	static Image tlCorner = ResourceLoader.getSprite("CornerMenuTL.png");
	static Image blCorner = ResourceLoader.getSprite("CornerMenuBL.png");
	static Image trCorner = ResourceLoader.getSprite("CornerMenuTR.png");
	static Image brCorner = ResourceLoader.getSprite("CornerMenuBR.png");
	static Image tEdge = ResourceLoader.getSprite("TopEdgeMenu.png");
	static Image bEdge = ResourceLoader.getSprite("BottomEdgeMenu.png");
	static Image lEdge = ResourceLoader.getSprite("LeftEdgeMenu.png");
	static Image rEdge = ResourceLoader.getSprite("RightEdgeMenu.png");
	
	static Font menuFont = ResourceLoader.getFont(
			"Pokemon GB.ttf").deriveFont(8.0f);
	static Color fontColor = new Color(24, 24, 24);
	static Color bgColor = new Color(247, 247, 247);	

	public void handleInput();
	public void update(long deltaTime);
	public void render(Graphics g);
}
