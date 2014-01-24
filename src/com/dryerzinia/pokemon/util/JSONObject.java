package com.dryerzinia.pokemon.util;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class JSONObject {

	/**
	 * Default conversion from JSON object to JSON string of the object using
	 * Reflection
	 * @param jsonObj the JSON object we are converting to a JSON string
	 * 
	 * TODO - Finish implementing for all basic TYPES!
	 */
	public static String defaultToJSON(JSON jsonObj) throws IllegalAccessException {

		/*
		 * Use a string builder to make the JSON
		 * to reduce concatenation as Strings are
		 * immutable
		 */
		StringBuilder json = new StringBuilder();

		json.append('{');

		/*
		 * Need the class of the object so we can
		 * iterate its fields and turn it into JSON
		 */
		Class<?> classJSONObj = jsonObj.getClass();

		/*
		 * Add the class type to the JSON
		 */
		json.append("\"class\":\"");
		json.append(classJSONObj.getName());
		json.append('"');

		boolean hasSuperClass = true;

		while(hasSuperClass){
		
			Field[] fields = classJSONObj.getDeclaredFields();

			for(Field field : fields){

				/*
				 * Don't want to convert transient, static or final variables to
				 * JSON by default
				 */
				int modifiers = field.getModifiers();
				boolean isTransient = Modifier.isTransient(modifiers);
				boolean isStatic = Modifier.isStatic(modifiers);
				boolean isFinal = Modifier.isFinal(modifiers);

				if(!isFinal && !isStatic && !isTransient){

					/*
					 * Need to be able to access protected and private parts
					 * of the object
					 */
					field.setAccessible(true);

					/*
					 * Depending on the type of the field we need to convert it
					 * to JSON differently
					 */
					Class<?> type = field.getType();

					/*
					 * Add a comma to the JSON to separate entries
					 */
					json.append(",");

					/*
					 * Add the fields name to the JSON
					 */
					json.append('"');
					json.append(field.getName());
					json.append("\":");

					if(type == int.class)
						json.append(field.getInt(jsonObj));

					else if(type == float.class)
						json.append(field.getFloat(jsonObj));

					else if(type == boolean.class)
						json.append(field.getBoolean(jsonObj));

					else if(type == String.class)
						json.append(stringToJSON((String)field.get(jsonObj)));

					else if(type.isArray()){

						Class<?> componentType = type.getComponentType();

						Object array = field.get(jsonObj);

						if(array == null){

							json.append("null");

						} else {

							json.append('[');
	
							int arrayLength = Array.getLength(array);
	
							for(int i = 0; i < arrayLength; i++){
	
								if(i != 0) json.append(',');
	
								if(componentType == int.class)
									json.append(Array.getInt(array, i));
	
								else if(JSON.class.isAssignableFrom(componentType))
									json.append(JSONObject.objectToJSON((JSON) Array.get(array, i)));
	
								else
									throw new NonJSONAble("Cound not convert array object type " + componentType.getName() + " to JSON in object " + classJSONObj.getName());
							}
	
							json.append(']');

						}

					}

					else if(JSON.class.isAssignableFrom(field.getType()))
						json.append(JSONObject.objectToJSON((JSON) field.get(jsonObj)));

					else
						throw new NonJSONAble("Cound not convert object type " + field.getType().getName() + " to JSON in object " + classJSONObj.getName());

				}
			}

			if(JSON.class.isAssignableFrom(classJSONObj.getSuperclass()))
				classJSONObj = classJSONObj.getSuperclass();

			else hasSuperClass = false;

		}

		/*
		 * JSON objects always end with }
		 */
		json.append('}');

		return json.toString();

	}

	/**
	 * Convert Object to JSON
	 * @param Object to convert to JSON
	 * @return JSON representation of Object
	 */
    public static String objectToJSON(JSON obj) throws IllegalAccessException {
   
    	if(obj == null) return "null";
    	return obj.toJSON();

    }

    public static Object[] JSONToArrayArray(String json){

    	ArrayList<Object> objs = new ArrayList<Object>();

    	boolean parsing = true;
    	int last_comma = 0;

    	while(parsing){

    		int comma = 0;

    		if(json.length() <= last_comma || json.charAt(last_comma) == ']') break;

    		comma = indexOfEndingParenthetical(json, '[', ']', last_comma + 1);
    		objs.add(JSONToArray(json.substring(last_comma + 1, comma)));

    		last_comma = comma;
    		
    	}

    	return objs.toArray();

    }
    
    public static Object[] JSONToObjectArray(String json){

    	ArrayList<Object> objs = new ArrayList<Object>();

    	boolean parsing = true;
    	int last_comma = 0;

    	while(parsing){

    		int comma = 0;

    		if(json.length() <= last_comma || json.charAt(last_comma) == ']') break;

    		if(json.substring(last_comma + 1, last_comma + 5).equals("null")){
    			objs.add(null);
    			comma = last_comma + 5;
    		} else {
    			comma = indexOfEndingParenthetical(json, '{', '}', last_comma + 1);
    			objs.add(JSONToObject(json.substring(last_comma + 1, comma)));
    		}

    		last_comma = comma;
    		
    	}

    	return objs.toArray();

    }
    
    public static Object[] JSONToStringArray(String json){
    	// TODO IMPLEMENT
		return null;
    }
    
    public static Object[] JSONToIntArray(String json){
    	ArrayList<Float> flts = new ArrayList<Float>();

    	boolean parsing = true;
    	int last_comma = 0;

    	while(parsing){

    		int comma = json.indexOf(',', last_comma + 1);
    		if(comma == -1)
    			comma = json.indexOf(']', last_comma + 1);

    		if(json.length() <= last_comma || json.charAt(last_comma) == ']') break;
    		
    		flts.add(new Float(json.substring(last_comma + 1, comma)));

    		last_comma = comma;
    		
    	}

    	return flts.toArray();
    	
    }

    public static Object[] JSONToArray(String json){

    	// Return empty array of len 0 if array is 0 length

    	if(json.charAt(0) == '[' && json.charAt(1) == ']') return new Object[0];

    	switch(json.charAt(1)){
    	case '[':
    		return JSONToArrayArray(json);
    	case '{':
    		return JSONToObjectArray(json);
    	case '"':
    		return JSONToStringArray(json);
    	default:
    		// TODO BOOLEAN
    		return JSONToIntArray(json);
    	}

    }

	/**
	 * Convert JSON to Object
	 * @param JSON to be converted into an object
	 * @return Object that was represented by JSON
	 */
	public static Object JSONToObject(String json){

    	HashMap<String, Object> parameters = new HashMap<String, Object>();

    	boolean parsing = true;
    	int last_comma = 0;

    	while(parsing){

    		int colon = json.indexOf(':', last_comma);
    		int comma;

    		Object param = null;

    		if(colon == -1) break;

    		switch(json.charAt(colon + 1)){
    			case '[':
    				// Parse Array
    				comma = indexOfEndingParenthetical(json, '[', ']', colon + 1) + 1;
    				param = JSONToArray(json.substring(colon + 1, comma - 1));
    				break;
    			case '{':
    				// Parse Object
    				comma = indexOfEndingParenthetical(json, '{', '}', colon + 1) + 1;
    				param = JSONToObject(json.substring(colon + 1, comma - 1));
    				break;
    			case '"':
    				// Parse String
    				comma = json.indexOf('\"', colon + 2);
    				if(comma == -1)
    					comma = json.indexOf('}', colon);
    				if(colon + 2 > comma -1)
    					param = "";
    				else
    					param = unescapeJava(json.substring(colon + 2, comma));
    				comma++;
    				break;
    			default:
    				// Parse Number or Boolean
    				comma = json.indexOf(',', colon);
    				if(comma == -1)
    					comma = json.indexOf('}', colon);
    				{

    					String value = json.substring(colon + 1, comma);

        				if(value.equals("false") || value.equals("true"))
        					param = new Boolean(value);
        				else if(value.equals("null"))
        					param = null;
        				else
            				param = new Float(value);
    					
    				}
    				break;
    		}

    		String param_name = json.substring(json.indexOf("\"", last_comma) + 1, colon - 1);

    		parameters.put(param_name, param);

    		last_comma = comma;
    	}

    	String class_name = (String) parameters.get("class");
    	
    	if(class_name == null)
    		return parameters;

    	try {

    		Class<?> c = Class.forName(class_name);
    		Object o = c.newInstance();

    		boolean has_superclass = true;
    		
    		while(has_superclass){
    		
    			Field[] fields = c.getDeclaredFields();

    			for(Field field : fields){
    				Object param = parameters.get(field.getName());

    				boolean is_transient = Modifier.isTransient(field.getModifiers());

    				if(param != null && !is_transient){

    					// Need to get into protected stuff
    					field.setAccessible(true);

    					Class<?> type = field.getType();

    					if(type == int.class)
    						field.setInt(o, ((Float)param).intValue());

    					else if(type == float.class)
    						field.setFloat(o, ((Float)param).floatValue());

    					else if(type == boolean.class)
    						field.setBoolean(o, ((Boolean)param).booleanValue());

    					else if(type.isArray()){

    						Class<?> componentType = type.getComponentType();

							Object[] params = (Object[])param;

							if(componentType == int.class){

    							int[] ints = new int[params.length];

    							for(int i = 0; i < params.length; i++)
    								ints[i] = ((Float)params[i]).intValue();

    							field.set(o, ints);

							} else {

    							Object arr = Array.newInstance(componentType, params.length);
    							for(int i = 0; i < params.length; i++)
    								Array.set(arr, i, params[i]);
    							field.set(o, arr);

    						}
    					
    					} else
    						field.set(o, type.cast(param));
    				}
    			}
    			
    			c = c.getSuperclass();

    			if(c == Object.class) has_superclass = false;

    		}

    		((JSON)o).fromJSON(parameters);

    		return o;
    		
    	} catch(Exception x){
    		x.printStackTrace();
    	}

    	return null;

    }

    public static int indexOfEndingParenthetical(String s, char left_paren, char right_paren, int cur_pos){

    	boolean searching = true;

		int left_count = 0;
		int right_count = 0;

		while(searching){

			int left = s.indexOf(left_paren, cur_pos);
			int right = s.indexOf(right_paren, cur_pos);

			if(left < right && left != -1){

				cur_pos = left + 1;
				left_count++;

			} else {
	
				cur_pos = right + 1;
				right_count++;

			}

			if(left_count == right_count)
				searching = false;

		}

		return cur_pos;

    }

	/**
	 * Convert String to JSON
	 * @param String object to convert to JSON
	 * @return JSON representation of Object
	 */
    public static String stringToJSON(String str){
   
    	if(str == null) return "null";
    	return "\"" + escapeJava(str) + "\"";

    }

	/**
	 * Escape special characters in string
	 * @param String with special characters
	 * @return String with special characters escaped
	 */
    public static String escapeJava(String str){
    	return str.replaceAll("\n", "\\\\n").replaceAll("'", "\\\\c");
    }

	/**
	 * UnEscape special characters in string
	 * @param String with special characters escaped
	 * @return String with special characters
	 */
    public static String unescapeJava(String str){
    	return str.replaceAll("\\\\n", "\n").replaceAll("\\\\c", "'");
    }

    
    
}