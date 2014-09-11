package com.dryerzinia.pokemon.obj.tiles;
import java.io.*;
import java.util.HashMap;
import java.awt.*;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.obj.RandomFight;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.util.DeepCopy;
import com.dryerzinia.pokemon.util.JSON;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ReferenceInit;
import com.dryerzinia.pokemon.util.ResourceLoader;

public class Tile implements Serializable, ReferenceInit, DeepCopy, JSON {

    static final long serialVersionUID = 5044137701176237619L;

    protected transient Image img;
    protected String imgName;

    public boolean canBeSteppedOn;

    public int id;

    public int pixelOffsetX;
    public int pixelOffsetY;

    public int changeToLevel = -1;
    public Direction leaveDirection = Direction.NONE;
    public Direction exitDir = Direction.NONE;
    public int xnew = -1;
    public int ynew = -1;

    public RandomFight rf;

    public Tile() {
    }

    public Tile(int id) {
        this.id = id;
    }

    public Tile(String imgName, boolean cbso) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        loadImage();
    }

    public Tile(String imgName, boolean cbso, RandomFight rf) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        loadImage();
        this.rf = rf;
    }

    public Tile(int px, int py, String imgName, boolean cbso) {
        this.imgName = imgName;
        pixelOffsetX = px;
        pixelOffsetY = py;
        canBeSteppedOn = cbso;
        loadImage();
    }

    public Tile(String imgName, boolean cbso, int ctl, Direction ld, int xnew,
            int ynew) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        loadImage();
        changeToLevel = ctl;
        leaveDirection = ld;
        this.xnew = xnew;
        this.ynew = ynew;
    }


    public Tile(String imgName, boolean cbso, int ctl, Direction ld, int xnew,
            int ynew, Direction exitDir) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        loadImage();
        changeToLevel = ctl;
        leaveDirection = ld;
        this.xnew = xnew;
        this.ynew = ynew;
        this.exitDir = exitDir;
    }

    public void draw(float x, float y, int xo, int yo, Graphics g) {
        g.drawImage(img, (int)(x * 16 + pixelOffsetX - xo), (int)(y * 16 + pixelOffsetY - yo), null);
    }

    public void loadImage() {
        img = ResourceLoader.getSprite(imgName);
    }

    public Image getImage() {
        return img;
    }

    public String toString() {
        return imgName;
    }

    public void setImageName(String in) {
        imgName = in;
    }

    public String getImageName(){
    	return imgName;
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        loadImage();

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

    public void fromJSON(HashMap<String, Object> json){

        imgName = (String) json.get("imgName");

        canBeSteppedOn = ((Boolean)json.get("canBeSteppedOn")).booleanValue();

        id = ((Float)json.get("id")).intValue();

        pixelOffsetX = ((Float)json.get("pixelOffsetX")).intValue();
        pixelOffsetY = ((Float)json.get("pixelOffsetY")).intValue();

        changeToLevel = ((Float)json.get("changeToLevel")).intValue();

        leaveDirection = Direction.getFromString((String) json.get("leaveDirection"));
        exitDir =  Direction.getFromString((String) json.get("exitDir"));
        xnew = ((Float)json.get("xnew")).intValue();
        ynew = ((Float)json.get("ynew")).intValue();

        rf = (RandomFight) json.get("rf");

        loadImage();

    }
    
    public void initializeSecondaryReferences(Grid g) {
    }

    public Object deepCopy() {

        Tile t = new Tile(new String(imgName), canBeSteppedOn, changeToLevel,
                leaveDirection, xnew, ynew, exitDir);


        t.id = -1;

        t.pixelOffsetX = pixelOffsetX;
        t.pixelOffsetY = pixelOffsetY;

        if (rf != null)
            t.rf = (RandomFight) rf.deepCopy();

        return t;
    }

}
