package com.dryerzinia.pokemon.obj;
import java.io.*;
import java.awt.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.util.DeepCopy;
import com.dryerzinia.pokemon.util.ReferenceInit;

public class Tile implements Serializable, ReferenceInit, DeepCopy {

    static final long serialVersionUID = 5044137701176237619L;

    protected transient Image img;
    protected String imgName;

    public boolean canBeSteppedOn;

    public int id;

    public int pixelOffsetX;
    public int pixelOffsetY;

    public int changeToLevel = -1;
    public int leaveDirection = -1;
    public int exitDir = -1;
    public int xnew = -1;
    public int ynew = -1;

    public GMenu onClick;

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
        onClick = null;
    }

    public Tile(String imgName, boolean cbso, RandomFight rf) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        loadImage();
        onClick = null;
        this.rf = rf;
    }

    public Tile(int px, int py, String imgName, boolean cbso) {
        this.imgName = imgName;
        pixelOffsetX = px;
        pixelOffsetY = py;
        canBeSteppedOn = cbso;
        loadImage();
        onClick = null;
    }

    public Tile(String imgName, boolean cbso, GMenu onClick) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        loadImage();
        this.onClick = onClick;
    }

    public Tile(int px, int py, String imgName, boolean cbso, GMenu onClick) {
        this.imgName = imgName;
        pixelOffsetX = px;
        pixelOffsetY = py;
        canBeSteppedOn = cbso;
        loadImage();
        this.onClick = onClick;
    }

    public Tile(String imgName, boolean cbso, int ctl, int ld, int xnew,
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
        onClick = null;
    }

    public Tile(String imgName, boolean cbso, int ctl, int ld, int xnew,
            int ynew, int ed) {
        this.imgName = imgName;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        loadImage();
        changeToLevel = ctl;
        leaveDirection = ld;
        this.xnew = xnew;
        this.ynew = ynew;
        exitDir = ed;
        onClick = null;
    }

    public GMenu getMenu(int x, int y) {
        onClick.active = true;
        return onClick;
    }

    public void draw(int x, int y, Graphics g) {
        g.drawImage(img, x * 16 + pixelOffsetX, y * 16 + pixelOffsetY, null);
    }

    public void draw(int x, int y, int xo, int yo, Graphics g) {
        g.drawImage(img, x * 16 + pixelOffsetX + xo,
                y * 16 + pixelOffsetY + yo, null);
    }

    public void loadImage() {
        if (PokemonGame.images == null)
            return;
        img = PokemonGame.images.getSprite(imgName);
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

    public void initializeSecondaryReferences(Grid g) {
    }

    public Object deepCopy() {

        Tile t = new Tile(new String(imgName), canBeSteppedOn, changeToLevel,
                leaveDirection, xnew, ynew, exitDir);

        if (onClick != null)
            t.onClick = (GMenu) onClick.deepCopy();

        t.id = -1;

        t.pixelOffsetX = pixelOffsetX;
        t.pixelOffsetY = pixelOffsetY;

        if (rf != null)
            t.rf = (RandomFight) rf.deepCopy();

        return t;
    }

}
