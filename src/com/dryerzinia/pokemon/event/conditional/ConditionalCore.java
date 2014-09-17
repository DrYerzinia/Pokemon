package com.dryerzinia.pokemon.event.conditional;

import java.util.HashMap;
import java.util.Map;

public class ConditionalCore {

	private static Map<Integer, Conditional> conditions = new HashMap<Integer, Conditional>();

	static {

		conditions.put(0, new BooleanConditional(0, false, 76, 35));

	}

	private ConditionalCore(){}

	public static void fireConditional(int conditionalID){

		Conditional c = conditions.get(conditionalID);

		if(c != null)
			c.fire();

	}

}
