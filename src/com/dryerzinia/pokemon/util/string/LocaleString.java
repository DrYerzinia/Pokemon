package com.dryerzinia.pokemon.util.string;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.dryerzinia.pokemon.util.JSON;
import com.dryerzinia.pokemon.util.string.StringStore.Locale;

public class LocaleString implements JSON {

	private HashMap<Locale, String> strings = new HashMap<Locale, String>();

	public LocaleString(){
	}

	public LocaleString(HashMap<Locale, String> strings){
		this.strings = strings;
	}

	public String get(Locale l){
		return strings.get(l);
	}

	public void add(Locale l, String s){

		strings.put(l, s);

	}

	@Override
	public String toJSON() throws IllegalAccessException {

		Iterator<Entry<Locale, String>> it = strings.entrySet().iterator();
		StringBuilder json = new StringBuilder();

		json.append('{');

		json.append("\"class\":\"");
		json.append(this.getClass().getName());
		json.append('"');

		while(it.hasNext()){

			Entry<Locale, String> pair = it.next();

			json.append('"');
			json.append(pair.getKey().toString());
			json.append("\":\"");
			json.append(pair.getValue());
			json.append('"');

			if(it.hasNext())
				json.append(',');

		}

		json.append('}');

		return json.toString();

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		Iterator<Entry<String, Object>> it = json.entrySet().iterator();

		while(it.hasNext()){

			Entry<String, Object> pair = it.next();

			strings.put(Locale.fromString(pair.getKey()), (String) pair.getValue());

		}

	}

}
