package com.dryerzinia.pokemon.obj;

import java.util.*;
import java.awt.*;
import java.io.*; // Serializable

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.obj.tiles.OnClick;
import com.dryerzinia.pokemon.obj.tiles.Person;
import com.dryerzinia.pokemon.obj.tiles.Tile;
import com.dryerzinia.pokemon.util.Database;
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

    private Pose location;

    private MovementAnimator movement;

    public Pose lastPokemonCenter;

    public int money; // players amount of money

    public String name; // Player ID/Handle/UserName

    public String imgName; // Character Sprite Base Name

    public transient Image sprite[]; // Character sprite image references for
                                     // draw

    public transient Image img; // Character large back image for game

    public transient Database.PokemonContainer poke; // Container for
                                                         // players pokemon
    public transient ArrayList<Item> items; // Container for players items

    public Player() {

        poke = new Database.PokemonContainer();
        items = new ArrayList<Item>();

    }

    public Player(int id, Pose location, String name) {

        this.id = id;

        this.location = location;

        this.name = name;

        poke = new Database.PokemonContainer();
        items = new ArrayList<Item>();

    }

    public Player(int id, Pose location, String name,
            String imgName) {

        this.id = id;

        this.location = location;

        this.name = name;
        this.imgName = imgName;

        poke = new Database.PokemonContainer();
        items = new ArrayList<Item>();

    }

    /**
     * Returns true if the people have the same Unique Identifiers
     */
    @Override
    public boolean equals(Object other){

    	if(other == null) return false;
    	if(other == this) return true;
    	if(!(other instanceof Player)) return false;
    	return id == ((Player)other).id;

    }

    /**
     * Drives the animation of the player
     * 
     * @param direction The direction that the player wants the character to go
     * @param deltaTime Amount of time in ms that have elapsed since last update
     */
    public Pose update(Direction direction, int deltaTime) {

    	Level oldLevel = GameState.getMap().getLevel(location.getLevel());

    	Pose newPose = movement.update(direction, location, deltaTime);

    	Level newLevel = GameState.getMap().getLevel(location.getLevel());

    	/*
    	 * If we changed levels and are not the main character switch levels
    	 */
    	if(oldLevel != newLevel){

    		if(this != ClientState.player){

	    		if(oldLevel != null)
	    			oldLevel.removePlayer(this);
	    		if(newLevel != null)
	    			newLevel.addPlayer(this);

    		}

    		/*
    		 * If we are the main character tell the leve we chagned
    		 */
    		else {
    			if(newLevel != null)
    				newLevel.notifyChangedTo();
    		}

    	}

    	return newPose;

    }

    public void draw(Graphics graphics) {

    	movement.draw(location, sprite, graphics);

    }


    public void draw(float x, float y, Graphics graphics) {

    	movement.draw(location, sprite, graphics);

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

        movement = new MovementAnimator(false, false);

    }

    public int getID() {
        return id;
    }

    public Pose getPose(){
    	return location;
    }

    public Pokemon getFirstOut() {
        return poke.getFirstOut();
    }

    public Database.PokemonContainer getPokemonContainer() {
        return poke;
    }

    public void goToLastPokemonCenter(){

    	location.set(lastPokemonCenter);

    }

    public void setMainCharacter(){

    	movement.setMainCharacter();

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
    public void setPosition(Pose position){

       	location = position.copy();

    }

    public void addMovement(Pose position){
    	movement.addMovement(position);
    }

    public void clearMovements(){
    	movement.clearMovements();
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public void setPokemon(Database.PokemonContainer poke) {
        this.poke = poke;
    }

    public String toString() {
        return name;
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        poke = new Database.PokemonContainer();
        items = new ArrayList<Item>();
        
        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

	public void click() {

    	Direction dir = ClientState.player.getPose().facing();
		int px = Math.round(ClientState.player.getPose().getX());
		int py = Math.round(ClientState.player.getPose().getY());
		ArrayList<Tile> tiles = null;

		switch(dir){
			case UP:
				py--;
				break;
			case DOWN:
				py++;
				break;
			case LEFT:
				px--;
				break;
			case RIGHT:
				px++;
				break;
			default:
				break;
		}

		tiles = ClientState.getPlayerLevel().grid.grid[px][py];

		if(tiles != null){
			Iterator<Tile> it = tiles.iterator();
			while(it.hasNext()){

				Tile t = it.next();
				if(t instanceof OnClick){
					((OnClick) t).click();
					break;
				}

			}
		}

		Iterator<Person> people = ClientState.getPlayerLevel().nearbyPersonIterator();
		while(people.hasNext()){
			Person person = people.next();
			if(person.x == px && person.y == py && person.level == ClientState.getPlayerLevel().id){
				person.click();
				break;
			}
		}

	}

}
