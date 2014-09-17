package com.dryerzinia.pokemon.event;

import java.util.HashMap;

import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.ui.menu.MenuStack;
import com.dryerzinia.pokemon.ui.menu.TextMenu;
import com.dryerzinia.pokemon.ui.menu.TextMenuListener;
import com.dryerzinia.pokemon.ui.menu.TextMenuState;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.string.StringStore;

public class TextEvent extends Event implements TextMenuListener {

	private int textID;

	protected transient boolean complete;

	public TextEvent(){}

	public TextEvent(int id, int nextEvent, int textID) {

		this.id = id;

		this.textID = textID;
		this.nextEvent = nextEvent;
	}
	
	@Override
	public void fire() {

		String text = StringStore.getString(textID, ClientState.locale);
		TextMenu menu = new TextMenu(text);

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

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		super.fromJSON(json);

		textID = ((Float) json.get("textID")).intValue();
		nextEvent = ((Float) json.get("nextEvent")).intValue();

		complete = false;

	}
}
