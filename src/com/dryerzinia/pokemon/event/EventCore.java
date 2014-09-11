package com.dryerzinia.pokemon.event;

import java.util.HashMap;
import java.util.Map;

import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.util.StringStream;

public class EventCore {

	private static Map<Integer, Event> events = new HashMap<Integer, Event>();

	private EventCore() {
		// no instantiation
	}
	
	public static void fireEvent(int id) {
		if (id >= 0) {
			System.out.println("Firing " + id);
			System.out.println("Firing " + events.get(id));
			events.get(id).fire();
		}
	}

	public static void loadEventsFromJSON(){

		String json = ResourceLoader.getJSON("Events.json");

		Object[] ev = JSONObject.JSONToArray(new StringStream(json));

		for(int i = 0; i < ev.length; i++){

			Event event = (Event) ev[i];
			events.put(event.getID(), event);

		}

	}

}
