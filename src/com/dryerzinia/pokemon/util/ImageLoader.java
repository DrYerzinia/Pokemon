package com.dryerzinia.pokemon.util;
import java.net.URL;
import java.util.HashMap;
import java.awt.*;

import javax.imageio.ImageIO;

public class ImageLoader {

    private HashMap<String, Image> sprites;
    private boolean doLoad = true;

    public static boolean useImageIO = false;

    public boolean in_a_jar = false;
    
    public ImageLoader() {

    	if(-1 != ImageLoader.class.getResource("ImageLoader.class").toString().indexOf(".jar!"))
    		in_a_jar = true;
    	
    	sprites = new HashMap<String, Image>();

    }

    public Image loadImage(String filename) {
        if (!doLoad)
            return null;

        URL url = null;

        try {

        	if(in_a_jar)
                url = new URL("jar:" + ImageLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI() + "!/" + filename);
        	else
        		url = getClass().getClassLoader().getResource(filename);

            if (useImageIO)
                return ImageIO.read(url);
            else
                return Toolkit.getDefaultToolkit().getImage(url);
        } catch (Exception ex) {
            System.out.println("Image not found: " + filename);
        }
        return null;
    }

    public Image getSprite(String filename) {
        Image img = sprites.get(filename);
        if (img == null) {
            img = loadImage("sprites/" + filename);
            sprites.put(filename, img);
        }

        return img;
    }

    public void setDoLoad(boolean dl) {
        doLoad = dl;
    }

    public boolean isLoading() {
        return doLoad;
    }

}
