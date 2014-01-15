package com.dryerzinia.pokemon.util;
import java.util.HashMap;

public interface JSON {

	/**
	 * Convert Object to JSON
	 * @return JSON representation of Object
	 */
    public String toJSON();

    /**
     * Get object fromJSON
     * @param JSON to turn into object
     */
    public void fromJSON(HashMap<String, Object> json);

}
