package com.dryerzinia.pokemon.util.string;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.util.StringStream;

public final class StringStore {
	
	public enum Locale {
		EN("en"), ES("es");

		private Locale(String repr) {
			this.repr = repr;
		}

		public static Locale fromString(String localeString) {

			for (Locale locale : Locale.values())
				if (localeString.equals(locale.repr))
					return locale;

			return null;

		}
		
		@Override
		public String toString() {
			return repr;
		}

		private final String repr;
	}

	private static Map<Integer, LocaleString> strings = new HashMap<Integer, LocaleString>();
	
	private StringStore() {
	}

	@SuppressWarnings("unchecked")
	public static void loadStringsFromJSON(){

		String json = ResourceLoader.getJSON("Strings.json");

		Object[] locObjs = JSONObject.JSONToArray(new StringStream(json));

		for(int i = 0; i < locObjs.length; i++){

			HashMap<String, Object> lsmap = (HashMap<String, Object>) locObjs[i];

			LocaleString ls = new LocaleString();

			Iterator<Entry<String, Object>> it = lsmap.entrySet().iterator();

			while(it.hasNext()){

				Entry<String, Object> pair = it.next();

				if(!pair.getKey().equals("id")){

					Locale locale = Locale.fromString(pair.getKey());

					if(locale != null)
						ls.add(locale, (String) pair.getValue());
					else
						System.out.println("There is no Locale " + pair.getValue() + " in the Locale ENUM!");

				}

			}

			strings.put(((Float)lsmap.get("id")).intValue(), ls);

		}

	}

	public static String getString(int id, Locale locale) {

		String s = strings.get(id).get(locale);
		if(s == null)
			s = strings.get(id).get(Locale.EN);

		return s.replace("{Player}", ClientState.username);

	}

}
