package com.dryerzinia.pokemon.event;

import java.util.ArrayList;
import java.util.List;

import com.dryerzinia.pokemon.util.StringStore;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.Item;

public class EventCore {
	private static List<Event> events = new ArrayList<Event>();
	
	static 
	{
		Event helixItem = new ItemEvent(StringStore.getString(2, ClientState.LOCALE),
				new Item("Helix Fossil", "A fancy helix thing", "Awesome", 0), 1);
		Event helixAns = new TextEvent(-1, StringStore.getString(3, ClientState.LOCALE));
		
		Event helixQuestion = new YesNoQuestionEvent(
				StringStore.getString(1, ClientState.LOCALE),
				0, // yes chain starts with get item
				-1 // no chain is null and just ends
				);
		events.add(helixItem);
		events.add(helixAns);
		events.add(helixQuestion);

		Event namesHouse = new TextEvent(-1, StringStore.getString(6, ClientState.LOCALE));
		events.add(namesHouse);

	}	

	private EventCore() {
		// no instantiation
	}
	
	public static void fireEvent(int id) {
		if (id >= 0) {
			System.out.println("Firing " + events.get(id));
			events.get(id).fire();
		}
	}
	
}
