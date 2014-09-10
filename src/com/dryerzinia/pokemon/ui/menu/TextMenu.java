package com.dryerzinia.pokemon.ui.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.ui.menu.RevealingTextGroup.RevealingText;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.input.InputController;

public class TextMenu implements Menu {	
	private static final int x = 0, y = 86;
	
	private enum Speed {
		SLOW(100), NORMAL(50), FAST(15);
		
		Speed(int delay) {
			this.delay = delay;
		}
		
		private int getDelay() {
			return delay;
		}
		
		private int delay;
	};
	
	private Speed speed;
	
	private List<RevealingTextGroup> groups;
	private int currentGroup;
	private RevealingText firstLine, secondLine;

	private long remainingTime;	
	private long elapsedTime;
	private int gracePeriod;
	
	private int animationSpeed; 
	private int animationOffset;
	private boolean animatingText;
	
	private FlashingArrow arrow;
	private TextMenuListener listener;
	
	private boolean hasPressedEnter;
	private boolean hasUpdatedListener;
	
	public TextMenu(String text) {
		List<String> lines = new ArrayList<String>();
		lines.add(text);
		initialize(lines);
	}
	
	public TextMenu(List<String> text) {
		initialize(text);
	}
	
	private void initialize(List<String> text) {
		groups = new ArrayList<RevealingTextGroup>();
		for (String line : text) {
			groups.add(LineSplitter.split(line));
		}
		gracePeriod = 350;
		animationSpeed = 300;
		animatingText = false;
		speed = Speed.NORMAL;
		arrow = new FlashingArrow();
		prepareLines();
	}
	
	private void prepareLines() {
		RevealingTextGroup group = groups.get(currentGroup);
		firstLine = group.getNextLine();
		
		secondLine = null;
		if (group.hasNextLine()) {
			secondLine = group.getNextLine();
		}
	}

	@Override
	public void handleInput() {
		if (ClientState.inputDevice.isButtonDown(InputController.Button.A)) {			
			if (listener != null) {
				listener.buttonPressed();
			}
			
			setSpeed(Speed.FAST);
			if (!hasPressedEnter && !isAnimating())
				advanceText();
			
			hasPressedEnter = true;		
			
		} else {
			// enter is not being pressed
			hasPressedEnter = false;
			setSpeed(Speed.NORMAL);
		}
	}

	@Override
	public void update(long deltaTime) {
		if ((elapsedTime += deltaTime) < gracePeriod)
			return;
		
		if (isFinished() && listener != null)
			listener.stateChanged(TextMenuState.FINISHED);
		
		if (animatingText)
			animateText(deltaTime);
		else {
						
			int delay = speed.getDelay();
			int nUpdates = (int) ((remainingTime + deltaTime) / delay);
			remainingTime = (remainingTime + deltaTime) % delay;
				
			for (int i = 0; i < nUpdates; i++) {
				if (!firstLine.isRevealed()) {
					firstLine.revealCharacter();
				} else {
					if (secondLine != null && !secondLine.isRevealed()) {
						secondLine.revealCharacter();
					}
				}
			}
			if (isArrowVisible())
				arrow.update(deltaTime);
		}
	}
	
	private void animateText(long deltaTime) {
		animationOffset -= animationSpeed/1000.0 * deltaTime;
		if (animationOffset < -30) {
			animationOffset = 0;
			animatingText = false;
			firstLine = secondLine;
			secondLine = groups.get(currentGroup).getNextLine();
			arrow.reset();
			remainingTime = 0;
		}
	}
	
	private boolean isArrowVisible() {
		if (isAnimating() || animatingText)
			return false;
		
		// only hide arrow if its the last group and no more lines are shown.
		if (isLastGroup() && !hasMoreLines())
			return false;
		
		return true;
	}
	
	public boolean isFinished() {
		return !isAnimating() && !hasMoreLines() && isLastGroup();
	}
	
	private boolean isAnimating() {
		return (!firstLine.isRevealed() || 
				(secondLine != null && !secondLine.isRevealed()));
	}
	
	private boolean isLastGroup() {
		return currentGroup == groups.size() - 1;
	}
	
	private boolean hasMoreLines() {
		if (currentGroup < groups.size()) {
			return groups.get(currentGroup).hasNextLine();
		}
		return false;
	}

	@Override
	public void render(Graphics g) {
		renderBox(g);
		renderText(g);
		
		if (isArrowVisible())
			arrow.render(g);
	}

	private void renderBox(Graphics g) {
		// draw the corners of the box
		g.drawImage(tlCorner, x, y, null);
		g.drawImage(blCorner, x, y+(16*2), null);
		g.drawImage(trCorner, x+(16*9), y, null);
		g.drawImage(brCorner, x+(16*9), y+(16*2), null);

		// draw the top and bottom edges of the box
		for (int i = 1; i < 9; i++) {
			g.drawImage(tEdge, x+(16*i), y, null);
			g.drawImage(bEdge, x+(16*i), y+(16*2), null);
		}

		// draw the left and right edges of the box
		g.drawImage(lEdge, x, y+16, null);
		g.drawImage(rEdge, x+(16*9), y+16, null);
		
		// fill in the middle
		g.setColor(new Color(247, 247, 247));
		g.fillRect(x+16, y+16, 16*8, 16);
	}
	
	private void renderText(Graphics g) {
		// clipping goodness to make text go away when animating
		g.setClip(new Rectangle(x, y, 160, 48));

		g.setFont(menuFont);
		g.setColor(new Color(24, 24, 24));
		g.drawString(firstLine.getCurrentText(), x+16, 
				y+22+animationOffset);
		if (secondLine != null) {
			g.drawString(secondLine.getCurrentText(), x+16, 
					y+38+(int)animationOffset);
		}
		
		g.setClip(null);
	}
	
	private void advanceText() {		
		RevealingTextGroup group = groups.get(currentGroup);
		if (group.hasNextLine()) {
			animatingText = true;			
		} else {
			// group is complete; switch groups!
			if (!isLastGroup()) {
				currentGroup++;		
				prepareLines();
				gracePeriod = 400;
				elapsedTime = 0;
			}
		}
		arrow.reset();
		remainingTime = 0;
	}
	
	private void setSpeed(Speed speed) {
		this.speed = speed;
	}

	public void registerListener(TextMenuListener listener) {
		this.listener = listener;
		
	}
	
	public String toString() {
		return this.firstLine.getTargetText();
	}

}
