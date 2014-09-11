package com.dryerzinia.pokemon.event.conditional;

import com.dryerzinia.pokemon.event.EventCore;

public class BooleanConditional extends Conditional {

	private boolean state;

	private int falseID;
	private int trueID;

	public BooleanConditional(){}

	public BooleanConditional(int id, boolean state, int falseID, int trueID){

		this.id = id;

		this.state = state;

		this.falseID = falseID;
		this.trueID = trueID;

	}

	@Override
	public void fire() {

		if(state){

			EventCore.fireEvent(trueID);

		} else {

			EventCore.fireEvent(falseID);
			state = true;

			// Update server on conditional

		}

	}

}
