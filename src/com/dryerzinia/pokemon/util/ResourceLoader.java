/**
 * Static Loader for Image and JSON resources for the Pokemon Game
 * 
 * @author DrYerzinia <DrYerzinia@gmail.com>
 */

package com.dryerzinia.pokemon.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public final class ResourceLoader {

	/*
	 * Sprite map for caching images so we don't load duplicates
	 */
    private static HashMap<String, Image> sprites;

    /*
     * If we are headless or a server we don't need to load any images
     * so we don't
     */
    private static boolean do_load = true;

    /*
     * If we are executing from a jar we don't want to check for missing
     * resources outside the JAR, and we want to save the path to the jar
     * for when we reference paths to it
     */
    private static boolean in_a_jar = false;
    private static String path_to_jar;

    /*
     * Initialization code
     */
    static {

    	/*
    	 * Check if we are running in a jar so we don't make useless loads from outside the jar
    	 * using classLoader.getResource()
    	 */
    	String class_path = ResourceLoader.class.getResource("ResourceLoader.class").toString();
    	int jar_index = class_path.indexOf(".jar!");
    	if(jar_index != -1){

    		jar_index += 4;
    		in_a_jar = true;
    		path_to_jar = class_path.substring(0, jar_index);

    	}

    	/*
    	 * Create Map to store cached images so we don't have multiple copies of same image
    	 */
    	sprites = new HashMap<String, Image>();

    }

    /**
     * Empty private constructor to prevent instantiation
     */
    private ResourceLoader(){}

    /**
     * Load an image based on its path in the jar
     * @param filename Path to image file to load relative to the root
     * directory of the code
     */
    private static Image loadImage(String image_file_path) {

    	/*
    	 * Don't load any images if we are headless
    	 */
    	if (!do_load)
            return null;

        try {
            URL url = null;

            if(in_a_jar)
                url = new URL(path_to_jar + "!/" + image_file_path);
        	else
        		url = ResourceLoader.class.getClassLoader().getResource(image_file_path);

            if(url == null)
            	return null;

            return Toolkit.getDefaultToolkit().getImage(url);

        } catch (MalformedURLException e) {

        	System.err.println("Bad image URL: " + e.getMessage());

        }

        return null;

    }

    /**
     * Get a sprite from its path relative to the root of the java code
     * @param filename file name of the sprite i.e. "CharD.png"
     */
    public static Image getSprite(String filename) {

    	/*
    	 * Check the Cache for the image
    	 */
    	Image image = sprites.get(filename);

    	/*
    	 * If its not in the catch lets load it
    	 */
    	if (image == null) {

    		/*
    		 * Load the sprite from the sprites folder
    		 */
    		image = loadImage("sprites/" + filename);
    		/*
    		 * Add the loaded image to the cache
    		 */
            sprites.put(filename, image);

    	}

    	/*
    	 * Return the loaded image or null if it was not found
    	 */
        return image;
    }

    /**
     * Get JSON file as a string
     * @param filename Name of JSON file to retrieve
     */
    public static String getJSON(String filename){

    	Scanner scanner = new Scanner(ResourceLoader.class.getResource(filename).getPath());
    	String json = scanner.next();
    	scanner.close();
    	return json;
    	
    }

    /**
     * Set if we should load images for the game
     * or not load images for the server
     * @param do_load true to tell the Resource Loader to load images
     */
    public static void setDoLoad(boolean do_load){

    	ResourceLoader.do_load = do_load;

    }

    /**
     * Check if we are loading images
     */
    public static boolean isLoading(){

    	return do_load;

    }

}
