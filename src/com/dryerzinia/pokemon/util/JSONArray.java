package com.dryerzinia.pokemon.util;

import java.util.Collection;
import java.util.Iterator;

public class JSONArray {

	/**
	 * Convert Object to JSON
	 * @param String object array to convert to JSON
	 * @return JSON representation of Object
	 */
    public static String setToJSON(Collection<? extends JSON> collection) throws IllegalAccessException {
   
    	if(collection == null) return "null";
    	
    	StringBuilder json = new StringBuilder();

    	json.append('[');

    	Iterator<? extends JSON> collectionIterator = collection.iterator();
        while(true){
        	json.append(JSONObject.objectToJSON((JSON)collectionIterator.next()));
        	if(collectionIterator.hasNext()) json.append(',');
        	else break;
        }

        json.append(']');

        return json.toString();

    }

}
