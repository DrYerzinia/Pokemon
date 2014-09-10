package com.dryerzinia.pokemon.event;

import java.util.HashMap;

import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.ui.menu.MenuStack;
import com.dryerzinia.pokemon.ui.menu.TextMenu;
import com.dryerzinia.pokemon.ui.menu.TextMenuListener;
import com.dryerzinia.pokemon.ui.menu.TextMenuState;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.audio.AudioListener;
import com.dryerzinia.pokemon.util.audio.AudioPlayer;
import com.dryerzinia.pokemon.util.string.StringStore;

public class ItemEvent extends Event 
	implements TextMenuListener, AudioListener {

	private Item item;
	private int textID;
	private int nextEvent;
	private boolean soundPlayed, textFinished;
	
	public ItemEvent(){}

	public ItemEvent(int id, int textID, Item item, int nextEvent) {

		this.id = id;

		this.textID = textID;

		this.item = item;
		this.nextEvent = nextEvent;
	}

	@Override
	public void fire() {
		TextMenu menu = new TextMenu(StringStore.getString(textID, ClientState.LOCALE));
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

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		super.fromJSON(json);

		item = (Item) json.get("item");

		textID = ((Float) json.get("textID")).intValue();
		nextEvent = ((Float) json.get("nextEvent")).intValue();
		soundPlayed = ((Boolean) json.get("soundPlayed"));
		textFinished = ((Boolean) json.get("textFinished"));

	}

	//

}
