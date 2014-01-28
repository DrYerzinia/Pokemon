package com.dryerzinia.pokemon.obj;
import java.util.*;
import java.awt.*;
import java.io.*; // Serializable

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.map.Position;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.server.PlayerPositionMessage;
import com.dryerzinia.pokemon.util.MysqlConnect;
import com.dryerzinia.pokemon.util.ResourceLoader;

public class Player implements Serializable {

    static final long serialVersionUID = 3191532376844138757L;

    /*
     * CHARACTER_OFFSET is added because all characters are drawn with
     * a 1/4 tile offset so they can stand overlapped with walls
     * and stand on chairs etc
     */
    public static final int CHARACTER_OFFSET = 4;

    public static final float ANIMATION_TIME_STEP = 250;

    public int id; // Player MYSQL_ID

    private Position location;

    private MovementAnimator movement;

    public Position lastPokemonCenter;

    public int money; // players amount of money

    public String name; // Player ID/Handle/UserName

    public String imgName; // Character Sprite Base Name

    public transient Image sprite[]; // Character sprite image references for
                                     // draw

    public transient Image img; // Character large back image for game

    public transient MysqlConnect.PokemonContainer poke; // Container for
                                                         // players pokemon
    public transient ArrayList<Item> items; // Container for players items

    public Player() {

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();

    }

    public Player(int id, Position location, String name) {

        this.id = id;

        this.location = location;

        this.name = name;

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();

    }

    public Player(int id, Position location, String name,
            String imgName) {

        this.id = id;

        this.location = location;

        this.name = name;
        this.imgName = imgName;

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();

    }
    
    /**
     * Drives the animation of the player
     * 
     * @param direction The direction that the player wants the character to go
     * @param deltaTime Amount of time in ms that have elapsed since last update
     */
    public Position update(Direction direction, int deltaTime) {

    	return movement.update(direction, location, deltaTime);

    }

    public void draw(Graphics graphics) {

    	img = movement.animationImage(sprite, location.facing());
        graphics.drawImage(img, 4 * 16, 4 * 16 - CHARACTER_OFFSET, null);

    }


    public void draw(float x, float y, Graphics graphics) {

    	img = movement.animationImage(sprite, location.facing());
        graphics.drawImage(img, (int) ((getLocation().getX() - x) * 16), (int)((getLocation().getY() - y) * 16) - CHARACTER_OFFSET, null);

    }

    public void loadImages() {

    	sprite = new Image[10];

        sprite[0] = ResourceLoader.getSprite(imgName + "U.png");
        sprite[1] = ResourceLoader.getSprite(imgName + "D.png");
        sprite[2] = ResourceLoader.getSprite(imgName + "L.png");
        sprite[3] = ResourceLoader.getSprite(imgName + "R.png");

        sprite[4] = ResourceLoader.getSprite(imgName + "U1.png");
        sprite[5] = ResourceLoader.getSprite(imgName + "D1.png");
        sprite[6] = ResourceLoader.getSprite(imgName + "L1.png");
        sprite[7] = ResourceLoader.getSprite(imgName + "R1.png");

        sprite[8] = ResourceLoader.getSprite(imgName + "U2.png");
        sprite[9] = ResourceLoader.getSprite(imgName + "D2.png");

        movement = new MovementAnimator(false);

    }

    public int getID() {
        return id;
    }

    public Position getLocation(){
    	return location;
    }

    public Pokemon getFirstOut() {
        return poke.getFirstOut();
    }

    public MysqlConnect.PokemonContainer getPokemonContainer() {
        return poke;
    }

    public void goToLastPokemonCenter(){

    	location.set(lastPokemonCenter);

    }

    public void set(Player p) {

        id = p.id;

       	location = p.location.copy();

        lastPokemonCenter = p.lastPokemonCenter;

        name = p.name;
        imgName = p.imgName;

        money = p.money;

    }

    /**
     * Updates the players position
     * 
     * @param position New player position
     */
    public void setPosition(Position position){

       	location = position.copy();

    }

    public void addMovement(Position position){
    	movement.addMovement(position);
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public void setPokemon(MysqlConnect.PokemonContainer poke) {
        this.poke = poke;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        Player p = (Player) o;
        if (p.name.equals(name))
            return true;
        return false;
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();
        
        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

}
