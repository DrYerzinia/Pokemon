package com.dryerzinia.pokemon.obj;
import java.util.*;
import java.awt.*;
import java.io.*; // Serializable

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.map.Level;
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

    public static final int JUMPING		= 0;
    public static final int STEPPING	= 1;
    public static final int SLIDING		= 2;
    public static final int NORMAL		= 3;

    /*
     * Global reference of Character for Client
     */
    public static Player self;

    public int id; // Player MYSQL_ID

    private Position location;

    /*
     * Drives animation
     */
    private transient int animationState;
    private transient boolean stepSide;
    private transient int animationElapsed;
    
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

        stepSide = false;
        animationElapsed = 0;

    }

    public Player(int id, Position location, String name) {

        this.id = id;

        this.location = location;

        this.name = name;

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();

        stepSide = false;
        animationElapsed = 0;

    }

    public Player(int id, Position location, String name,
            String imgName) {

        this.id = id;

        this.location = location;

        this.name = name;
        this.imgName = imgName;

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();

        stepSide = false;
        animationElapsed = 0;

    }

    private void animationMove(int deltaTime){

    	/*
    	 * Before we can move we have to make sure the tile can be stepped on
    	 * We still are incrementing the animation timer which lets us animate
    	 * the characters sprite so we can do the wall bump
    	 */

    	int futureX = 0;
    	int futureY = 0;
    	
    	if(location.facing() == Direction.UP){
    		futureX = (int) Math.ceil(location.getX());
    		futureY = (int) Math.ceil(location.getY() - 1);
    	}

    	if(location.facing() == Direction.DOWN){
    		futureX = (int) Math.floor(location.getX());
    		futureY = (int) Math.floor(location.getY() + 1);
    	}

    	if(location.facing() == Direction.LEFT){
    		futureX = (int) Math.ceil(location.getX() - 1);
    		futureY = (int) Math.ceil(location.getY());
    	}

    	if(location.facing() == Direction.RIGHT){
    		futureX = (int) Math.floor(location.getX() + 1);
    		futureY = (int) Math.floor(location.getY());
    	}

    	Level lev = ClientState.getPlayerLevel();
    	boolean canStep = lev.canStepOn(futureX, futureY);
    	LevelChange levelChange = lev.grid.changeLevel(futureX, futureY);

    	/*
    	 * We need to continue the step through to the next level
    	 */
    	if(levelChange != null && levelChange.rightDirection(location.facing())){

    		location.set(levelChange.getNewPosition());
    		canStep = true;

    	}

    	if(canStep){
    		/*
    		 * If we are facing the direction
	    	 */
	    	if(location.facing() == Direction.UP)
	    		location.subY(deltaTime/ANIMATION_TIME_STEP);
	
	    	if(location.facing() == Direction.DOWN)
	    		location.addY(deltaTime/ANIMATION_TIME_STEP);
	
	    	if(location.facing() == Direction.LEFT)
	    		location.subX(deltaTime/ANIMATION_TIME_STEP);
	
	    	if(location.facing() == Direction.RIGHT)
	    		location.addX(deltaTime/ANIMATION_TIME_STEP);
    	}

    }

    private void continueAnimation(Direction direction, int deltaTime){

		animationElapsed += deltaTime;

    	/*
    	 * If we exceed time step
    	 */
    	if(animationElapsed > ANIMATION_TIME_STEP){
    
    		/*
    		 * change step side for alternating gait
    		 */
    		stepSide = !stepSide;
    		
    		/*
    		 * we have to get even with the square
    		 */
   			animationMove((int)(deltaTime-(animationElapsed-ANIMATION_TIME_STEP)));
    		location.setX(Math.round(location.getX()));
    		location.setY(Math.round(location.getY()));

    		/*
    		 * Update server to our new position
    		 */
    		try {
				Client.writeServerMessage(new PlayerPositionMessage(location));
    		} catch (IOException e) {
				System.out.println("Failed to update server on our position: " + e.getMessage());
			}

    		if(direction == Direction.NONE){

    			/*
    			 * If we have no where to go we end the animation
    			 */
    			animationElapsed = 0;

    		} else {
    			/*
    			 * But if we do have a direction to go we move the animation overflow time
    			 * over to the next animation and change directions in case we turned and move
    			 * that way
    			 */
    			animationElapsed -= ANIMATION_TIME_STEP;
    			location.setDirection(direction);
    			animationMove(animationElapsed);

    		}

    	} else {
    		/*
    		 * Otherwise we just move like normal
    		 */
    		animationMove(deltaTime);
    	}

    }
    
    /**
     * Drives the animation of the player
     * 
     * @param direction The direction that the player wants the character to go
     * @param deltaTime Amount of time in ms that have elapsed since last update
     */
    public void update(Direction direction, int deltaTime) {

    	/*
    	 * If there is an animation going we finish it
    	 */
    	if(animationElapsed != 0){
    		continueAnimation(direction, deltaTime);
    		return;
    	}

    	/*
    	 * Otherwise we either turn or start a new animation
    	 */
    	if(direction != Direction.NONE){
        	/*
        	 * If we change directions we just return
        	 */
    		if(direction != location.facing()){
    			location.setDirection(direction);
    			return;
    		}

    		/*
    		 * If we are same direction as before we start a movement animation
    		 * increment animation timer by deltaTime
    		 * move by deltaTime over time step
    		 */
    		continueAnimation(direction, deltaTime);

    	}

    }

    public void draw(Graphics graphics) {

        setImage(0, 0);
        graphics.drawImage(img, 4 * 16, 4 * 16 - CHARACTER_OFFSET, null);

    }


    public void draw(float x, float y, Graphics graphics) {

    	setImage(0, 0);
        graphics.drawImage(img, (int) ((getLocation().getX() - x) * 16), (int)((getLocation().getY() - y) * 16) - CHARACTER_OFFSET, null);

    }

    protected void setImage(int x, int y) {

    	/*
    	 * For animation of the character pokemon does 2 images for the
    	 * animations but up down animations have the sprite mirrored during
    	 * alternating steps always starting with the right side of him moving
    	 */

    	/*
    	 * If animation is in first 1/4 or last 1/4 we use normal image
    	 */
    	if(animationElapsed < ANIMATION_TIME_STEP*0.25
    	 || animationElapsed > ANIMATION_TIME_STEP*0.75)
    		img = sprite[location.facing().getValue()];

    	/*
    	 * If its in the middle 1/2 we use the moving animation
    	 */
    	else {

    		if((location.facing() == Direction.UP || location.facing() == Direction.DOWN) && stepSide)
    			img = sprite[location.facing().getValue()+8];
    		else
    			img = sprite[location.facing().getValue()+4];

    	}

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

        stepSide = false;
        animationElapsed = 0;
        
        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

}
