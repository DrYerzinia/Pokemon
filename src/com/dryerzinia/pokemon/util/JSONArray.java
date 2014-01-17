package com.dryerzinia.pokemon.util;
import java.util.ArrayList;

public class JSONArray {

	/**
	 * Convert Object to JSON
	 * @param JSON object array to convert to JSON
	 * @return JSON representation of Object
	 */
    public static String objectArrayToJSON(JSON[] array){
   
    	if(array == null) return "null";
    	
    	StringBuilder json = new StringBuilder();

    	json.append("[");

    	for(JSON obj : array){

    		json.append(JSONObject.objectToJSON(obj));
    		json.append(",");

    	}

    	json.deleteCharAt(json.length() - 1);

       	json.append("]");

        return json.toString();

    }

	/**
	 * Convert Object to JSON
	 * @param String object array to convert to JSON
	 * @return JSON representation of Object
	 */
    public static String arrayListToJSON(@SuppressWarnings("rawtypes") ArrayList array){
   
    	if(array == null) return "null";
    	
    	String json = "";

    	json += "[";
        for(int i = 0; i < array.size()-1; i++)
        	json += JSONObject.objectToJSON((JSON)array.get(i)) + ",";
        json += JSONObject.objectToJSON((JSON)array.get(array.size()-1)) + "]";

        return json;

    }

	/**
	 * Convert Object to JSON
	 * @param String object array to convert to JSON
	 * @return JSON representation of Object
	 */
    public static String stringArrayToJSON(String[] array){
   
    	if(array == null) return "null";
    	
    	String json = "";

    	json += "[";
        for(int i = 0; i < array.length-1; i++)
        	json += JSONObject.stringToJSON(array[i]) + ",";
        json += JSONObject.stringToJSON(array[array.length-1]) + "]";

        return json;

    }

	/**
	 * Convert Object to JSON
	 * @param int object array to convert to JSON
	 * @return JSON representation of Object
	 */
    public static String intArrayToJSON(int[] array){
   
    	if(array == null) return "null";
    	
    	String json = "";

    	json += "[";
        for(int i = 0; i < array.length-1; i++)
        	json += array[i] + ",";
        json += array[array.length-1] + "]";

        return json;

    }

}
