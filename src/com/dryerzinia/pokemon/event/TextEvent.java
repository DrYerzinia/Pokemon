package com.dryerzinia.pokemon.event;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import com.dryerzinia.pokemon.ui.menu.MenuStack;
import com.dryerzinia.pokemon.ui.menu.TextMenu;
import com.dryerzinia.pokemon.ui.menu.TextMenuListener;
import com.dryerzinia.pokemon.ui.menu.TextMenuState;

public class TextEvent implements Event, TextMenuListener {
	private List<String> lines;
	private int nextEvent;
	private boolean complete;
	
	public TextEvent(int nextEvent, String... text) {
		lines = new ArrayList<String>();
		for (String line : text)
			lines.add(line);
		this.nextEvent = nextEvent;
	}
	
	@Override
	public void fire() {
		TextMenu menu = new TextMenu(lines);
		menu.registerListener(this);
		MenuStack.push(menu);
	}

	@Override
	public void stateChanged(TextMenuState state) {
		if (state == TextMenuState.FINISHED)
			complete = true;
	}
	
	@Override
	public String toString() {
		return "TextEvent";
	}

	@Override
	public void buttonPressed() {
		if (complete) {
			complete = false;
			MenuStack.pop();
			EventCore.fireEvent(nextEvent);
		}
	}
}
