import java.net.URL;
import java.util.HashMap;
import java.awt.*;

import javax.imageio.ImageIO;

public class ImageLoader {

    private HashMap<String, Image> sprites;
    private boolean doLoad = true;

    public static boolean useImageIO = false;

    public ImageLoader() {
        sprites = new HashMap<String, Image>();
    }

    public Image loadImage(String filename) {
        if (!doLoad)
            return null;

        URL url = null;

        try {
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
