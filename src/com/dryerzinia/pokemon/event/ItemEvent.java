package com.dryerzinia.pokemon.event;

import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.ui.menu.MenuStack;
import com.dryerzinia.pokemon.ui.menu.TextMenu;
import com.dryerzinia.pokemon.ui.menu.TextMenuListener;
import com.dryerzinia.pokemon.ui.menu.TextMenuState;
import com.dryerzinia.pokemon.util.audio.AudioListener;
import com.dryerzinia.pokemon.util.audio.AudioPlayer;

public class ItemEvent implements Event, 
	TextMenuListener, AudioListener {
	private Item item;
	private String text;
	private int nextEvent;
	private boolean soundPlayed, textFinished;
	
	public ItemEvent(String text, Item item, int nextEvent) {
		this.text = text;

		this.item = item;
		this.nextEvent = nextEvent;
	}

	@Override
	public void fire() {
		TextMenu menu = new TextMenu(text);
		menu.registerListener(this);
		MenuStack.push(menu);
		
	}
	
	@Override
	public void stateChanged(TextMenuState state) {
		if (textFinished) return;
		if (state == TextMenuState.FINISHED) {
			textFinished = true;
			new AudioPlayer(this).play("item_get.wav");
		}
	}

	@Override
	public void buttonPressed() {
		if (textFinished && soundPlayed) {
			MenuStack.pop();
			soundPlayed = false;
			textFinished = false;
			EventCore.fireEvent(nextEvent);
		}
	}

	@Override
	public void soundPlayed() {
		soundPlayed = true;		
	}
	
	
	@Override
	public String toString() {
		return "ItemEvent";
	}

}
