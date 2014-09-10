package com.dryerzinia.pokemon.util;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

					else if(type.isEnum()){

						try {

							json.append(stringToJSON((String)type.getMethod("getStringValue").invoke(field.get(jsonObj))));

						} catch (InvocationTargetException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {

							System.err.println("Enum " + type.getName() + " failed to convert to JSON");
							e.printStackTrace();

						}

					}

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

	public static void defaultToObject(HashMap<String, Object> properties, Object object){

    	try {

    		Class<?> c = object.getClass();

    		boolean has_superclass = true;
    		
    		while(has_superclass){
    		
    			Field[] fields = c.getDeclaredFields();

    			for(Field field : fields){
    				Object param = properties.get(field.getName());

    				boolean is_transient = Modifier.isTransient(field.getModifiers());

    				if(param != null && !is_transient){

    					// Need to get into protected stuff
    					field.setAccessible(true);

    					Class<?> type = field.getType();

    					if(type == int.class)
    						field.setInt(object, ((Float)param).intValue());

    					else if(type == float.class)
    						field.setFloat(object, ((Float)param).floatValue());

    					else if(type == boolean.class)
    						field.setBoolean(object, ((Boolean)param).booleanValue());

    					else if(type.isEnum())
    						field.set(object, type.getMethod("getFromString", String.class).invoke(null, (String) param));

    					else if(type.isArray()){

    						Class<?> componentType = type.getComponentType();

							Object[] params = (Object[])param;

							if(componentType == int.class){

    							int[] ints = new int[params.length];

    							for(int i = 0; i < params.length; i++)
    								ints[i] = ((Float)params[i]).intValue();

    							field.set(object, ints);

							} else {

    							Object arr = Array.newInstance(componentType, params.length);
    							for(int i = 0; i < params.length; i++)
    								Array.set(arr, i, params[i]);
    							field.set(object, arr);

    						}
    					
    					} else
    						field.set(object, type.cast(param));
    				}
    			}
    			
    			c = c.getSuperclass();

    			if(c == Object.class) has_superclass = false;

    		}
    		
    	} catch(Exception x){
    		x.printStackTrace();
    	}

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

    public static Object[] JSONToArrayArray(StringStream json){

    	ArrayList<Object> objs = new ArrayList<Object>();

    	while(true){

    		objs.add(JSONToArray(json));
    		json.ignoreUntil("[]");

    		if(json.peek() == ']') break;

    	}

    	json.ignore();

    	return objs.toArray();

    }
    
    public static Object[] JSONToObjectArray(StringStream json){

    	ArrayList<Object> objs = new ArrayList<Object>();

    	while(true){

    		objs.add(JSONToObject(json));
    		json.ignoreUntil("{]");

    		char c = json.peek();
    		if(c == ']') break;

    	}

    	json.ignore();

    	return objs.toArray();

    }
    
    public static Object[] JSONToStringArray(StringStream json){
    	// TODO IMPLEMENT
		return null;
    }
    
    public static Object[] JSONToNumericalArray(StringStream json){

    	ArrayList<Float> flts = new ArrayList<Float>();

    	while(true){

    		flts.add(new Float(json.readUntil(" \n\t,]")));

    		json.skipWhitespace();
    		if(json.peek() == ']') break;
    		json.ignore();
    		json.skipWhitespace();

    	}

    	json.ignore();

    	return flts.toArray();
    	
    }

    public static Object[] JSONToArray(StringStream json){

    	// Return empty array of len 0 if array is 0 length

    	json.ignore();
    	json.skipWhitespace();
    	char c = json.peek();

    	if(c == ']'){
    		json.ignore();
    		return new Object[0];
    	}

    	switch(c){
    	case '[':
    		return JSONToArrayArray(json);
    	case '{':
    		return JSONToObjectArray(json);
    	case '"':
    		return JSONToStringArray(json);
    	default:
    		// TODO BOOLEAN
    		return JSONToNumericalArray(json);
    	}

    }

	/**
	 * Convert JSON to Object
	 * @param JSON to be converted into an object
	 * @return Object that was represented by JSON
	 */
	@SuppressWarnings("unchecked")
	public static Object JSONToObject(StringStream json){

    	HashMap<String, Object> parameters = new HashMap<String, Object>();

    	while(true){

    		Object param = null;

    		json.skipWhitespace();						//		WHITESPACE
    		if(json.peek() == '}'){
        		json.ignore();							// }
        		break;
        	}
        	json.ignore();								// ,
        	json.skipWhitespace();						//		WHITESPACE
        	json.ignore();								// "
        	String paramName = json.readUntil("\"");	// PARAM NAME
    		json.ignore();								// "
    		json.skipWhitespace();						//		WHITESPACE
    		json.ignore();								// :
    		json.skipWhitespace();						//		WHITESPACE

    		switch(json.peek()){						// [ or { or " or number or boolean or null
    			case '[':
    				param = JSONToArray(json);
    				break;
    			case '{':
    				param = JSONToObject(json);
    				break;
    			case '"':
    				json.ignore();
   					param = unescapeJava(json.readUntil("\""));
   					json.ignore();
   					json.skipWhitespace();
    				break;
    			default:
   					String value = json.readUntil(" \n\t,}");

       				if(value.equals("false") || value.equals("true"))
       					param = new Boolean(value);
 
       				else if(value.equals("null"))
       					param = null;

       				else
           				param = new Float(value);
    				break;
    		}

    		parameters.put(paramName, param);

    	}

    	String className = (String) parameters.get("class");

    	if(className == null)
    		return parameters;

		try {

			Class<? extends JSON> objectClass;
			objectClass = (Class<? extends JSON>) Class.forName(className);

			JSON obj = objectClass.newInstance();

			obj.fromJSON(parameters);
    	
    		return obj;

		} catch (ClassNotFoundException e) {

			System.err.println("Could not find class specified by JSON: " + e.getMessage());

		} catch (InstantiationException e) {

			System.err.println("Could not Instantiate new object from JSON: " + e.getMessage());

		} catch (IllegalAccessException e) {

			System.err.println("JSONObject access disallowed: " + e.getMessage());

		}

		return parameters;

    }
/*
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
*/
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