package com.dryerzinia.pokemon.event;

import java.util.HashMap;

import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.ui.menu.MenuStack;
import com.dryerzinia.pokemon.ui.menu.TextMenu;
import com.dryerzinia.pokemon.ui.menu.TextMenuListener;
import com.dryerzinia.pokemon.ui.menu.TextMenuState;
import com.dryerzinia.pokemon.ui.menu.YesNoMenu;
import com.dryerzinia.pokemon.ui.menu.YesNoMenuListener;
import com.dryerzinia.pokemon.ui.menu.YesNoMenu.Selection;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.string.StringStore;


public class YesNoQuestionEvent extends Event 
	implements TextMenuListener, YesNoMenuListener {

	private int questionTextID;
	private int yesEventChain, noEventChain;

	public YesNoQuestionEvent(){}

	public YesNoQuestionEvent(int id, int questionTextID,
			int yesEventChain, int noEventChain) {

		this.id = id;

		this.questionTextID = questionTextID;
		this.yesEventChain = yesEventChain;
		this.noEventChain = noEventChain;
	}
	
	
	@Override
	public void fire() {
		// create a new TextMenu showing the question and push it to the stack
		TextMenu textMenu = new TextMenu(StringStore.getString(questionTextID, ClientState.LOCALE));
		textMenu.registerListener(this);
		MenuStack.push(textMenu);
	}
	

	@Override
	public void stateChanged(TextMenuState state) {
		if (state == TextMenuState.FINISHED) {
			YesNoMenu menu = new YesNoMenu();
			menu.registerListener(this);
			MenuStack.push(menu);
		}
	}
	
	@Override
	public void buttonPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void madeSelection(Selection selection) {
		MenuStack.pop();
		MenuStack.pop();
		
		if (selection == Selection.YES)
			EventCore.fireEvent(yesEventChain);
		else
			EventCore.fireEvent(noEventChain);
	}
	
	@Override
	public String toString() {
		return "YesNoQuestionEvent";
	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		super.fromJSON(json);

		questionTextID = ((Float) json.get("questionTextID")).intValue();

		yesEventChain = ((Float) json.get("yesEventChain")).intValue();
		noEventChain = ((Float) json.get("noEventChain")).intValue();

	}

}
