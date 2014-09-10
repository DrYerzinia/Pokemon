package com.dryerzinia.pokemon.event;

import com.dryerzinia.pokemon.ui.menu.MenuStack;
import com.dryerzinia.pokemon.ui.menu.TextMenu;
import com.dryerzinia.pokemon.ui.menu.TextMenuListener;
import com.dryerzinia.pokemon.ui.menu.TextMenuState;
import com.dryerzinia.pokemon.ui.menu.YesNoMenu;
import com.dryerzinia.pokemon.ui.menu.YesNoMenuListener;

import com.dryerzinia.pokemon.ui.menu.YesNoMenu.Selection;


public class YesNoQuestionEvent implements Event, 
	TextMenuListener, YesNoMenuListener {
	private String questionText;
	private int yesEventChain, noEventChain;
	
	public YesNoQuestionEvent(String questionText,
			int yesEventChain, int noEventChain) {
		this.questionText = questionText;
		this.yesEventChain = yesEventChain;
		this.noEventChain = noEventChain;
	}
	
	
	@Override
	public void fire() {
		// create a new TextMenu showing the question and push it to the stack
		TextMenu textMenu = new TextMenu(questionText);
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

}
