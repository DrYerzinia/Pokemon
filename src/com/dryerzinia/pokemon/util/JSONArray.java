package com.dryerzinia.pokemon.util;
import java.util.ArrayList;

public class JSONArray {

	/**
	 * Convert Object to JSON
	 * @param String object array to convert to JSON
	 * @return JSON representation of Object
	 */
    public static String arrayListToJSON(ArrayList<? extends JSON> array) throws IllegalAccessException {
   
    	if(array == null) return "null";
    	
    	String json = "";

    	json += "[";
        for(int i = 0; i < array.size()-1; i++)
        	json += JSONObject.objectToJSON((JSON)array.get(i)) + ",";
        json += JSONObject.objectToJSON((JSON)array.get(array.size()-1)) + "]";

        return json;

    }

}
