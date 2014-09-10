package com.dryerzinia.pokemon.util;

import java.util.HashMap;
import java.util.Map;

public final class StringStore {
	
	public enum Locale {
		EN, ES;
	}
	
	private static Map<Integer, String> enStrings;
	private static Map<Integer, String> esStrings;
	
	static {
		enStrings = new HashMap<Integer, String>();
		enStrings.put(1, "You want the HELIX FOSSIL?");
		enStrings.put(2, "YOU got the HELIX FOSSIL!");
		enStrings.put(3, "All right. Then this is mine!");
		enStrings.put(4, "YES");
		enStrings.put(5, "NO");
		enStrings.put(6, "NAME's house");

		esStrings = new HashMap<Integer, String>();
		esStrings.put(1, "¿Quieres el Fósil Hélix?");
		esStrings.put(2, "¡Has conseguido el Fósil Hélix!");
		esStrings.put(3, "Vale. ¡Pues esto es mío!");
		esStrings.put(4, "Sí");
		esStrings.put(5, "No");
		esStrings.put(6, "NAME's house");
	}
	
	
	
	private StringStore() {
	}
	
	public static String getString(int id, Locale locale) {
		switch(locale) {
		case EN:
			return enStrings.get(id);
		case ES:
			return esStrings.get(id);
		default: return "";			
		}
	}
}
