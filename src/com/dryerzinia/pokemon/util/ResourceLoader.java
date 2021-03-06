/**
 * Static Loader for Image and JSON resources for the Pokemon Game
 * 
 * @author DrYerzinia <DrYerzinia@gmail.com>
 */

package com.dryerzinia.pokemon.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.dryerzinia.pokemon.PokemonGame;

public final class ResourceLoader {

	private static final Logger LOGGER =			
			Logger.getLogger(ResourceLoader.class.getName());

	/*
	 * Default color profiles
	 * TODO Change to ENUM
	 */
	public static final int GREEN[] = {128, 248, 32, 255};

	/*
	 * Default colorProfile Green
	 */
	private static int colorProfile[] = GREEN;

	/*
	 * Sprite map for caching images so we don't load duplicates
	 */
    private static ConcurrentHashMap<String, BufferedImage> sprites;

	private static HashMap<String, Font> fonts = new HashMap<String, Font>();

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
    	sprites = new ConcurrentHashMap<String, BufferedImage>();

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
    private static BufferedImage loadImage(String imageFilePath) {

    	/*
    	 * Don't load any images if we are headless
    	 */
    	if (!do_load)
            return null;

        try {
            URL url = null;

            if(in_a_jar)
                url = new URL(path_to_jar + "!/" + imageFilePath);
        	else
        		url = ResourceLoader.class.getClassLoader().getResource(imageFilePath);

            if(url == null)
            	return null;

            return ImageIO.read(url);

        } catch (MalformedURLException e) {

        	System.err.println("Bad image URL: " + e.getMessage());

        } catch (IOException e) {

			System.err.println("IO Error reading image " + imageFilePath + ": " + e.getMessage());
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
    	BufferedImage image = sprites.get(filename);

    	/*
    	 * If its not in the catch lets load it
    	 */
    	if (image == null) {

    		/*
    		 * Load the sprite from the sprites folder
    		 */
    		image = loadImage("sprites/" + filename);

    		/*
    		 * Add the loaded image to the cache if we
    		 * found it
    		 */
    		if(image != null){
    			changeImageColorProfile(image);
    			sprites.put(filename, image);
    		}

    	}

    	/*
    	 * Return the loaded image or null if it was not found
    	 */
        return image;
    }

	/**
	 * @param filename name of the font including the TTF extension.
	 * @return the font from the jar or cache if it's been loaded.
	 */
	public static Font getFont(String filename) {
		Font font = fonts.get(filename);
		if (font == null) {
			LOGGER.info("Font " + filename + " not cached; retrieving...\n");
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.class
						.getResourceAsStream("/fonts/" + filename));
				fonts.put(filename, font);
			} catch (IOException | FontFormatException e) {
				LOGGER.warning("Error loading custom font " + filename + ": "
						+ e.getMessage() + "\n");
			} 
		} else {
			LOGGER.info("Retrieving cached font: " + filename + "\n");
		}
		return font;
	}

	//

    public static void setColorProfile(int[] newColorProfile){

    	colorProfile = newColorProfile;

    }

    public static void changeImageColorProfile(BufferedImage img){

    	int pixelColor[] = new int[4];

    	if(img.getHeight() == 16 && img.getWidth() == 16){
    		
	    	for(int x = 0; x < 16; x++){
	    		for(int y = 0; y < 16; y++){

	    			img.getRaster().getPixel(x, y, pixelColor);
	    			if(
	    					pixelColor[3] != 0
	  					&& !(pixelColor[0] ==  24 && pixelColor[1] ==  24 && pixelColor[2] == 24)
	  					&& !(pixelColor[0] == 248 && pixelColor[1] == 248 && pixelColor[2] == 248)
	  					&& !(pixelColor[0] ==  88 && pixelColor[1] == 184 && pixelColor[2] == 248)
	  				){

	    				img.getRaster().setPixel(x, y, colorProfile);
	    			}
	    		}
	    	}
		}

    }

    public static void changeColorProfile(){

    	for(BufferedImage img : sprites.values())
    		changeImageColorProfile(img);

    }

    /**
     * Get JSON file as a string
     * @param filename Name of JSON file to retrieve
     */
    public static String getJSON(String filename){

    	try(BufferedReader jsonReader = new BufferedReader(new InputStreamReader(
                ResourceLoader.class.getClassLoader().getResourceAsStream(filename)));){
    	
    		StringBuilder json = new StringBuilder();
    		String line;

    		while((line = jsonReader.readLine()) != null){
    			json.append(line);
    			json.append("\n");
			}

        	return json.toString();

        } catch(IOException ioe){
        	System.err.println("Unable to read Strings file: " + ioe.getMessage());
        }

    	return null;

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
