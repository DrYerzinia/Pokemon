package com.dryerzinia.pokemon.obj.tiles;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.awt.*;

import com.dryerzinia.pokemon.event.EventCore;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.Player;

public class Person extends Tile implements Actor, OnClick {

	static final long serialVersionUID = -47859827707060573L;

	public static final int A_MOVED = 0;
	public static final int A_TALKING_TO = 1;

	public static final GMenu ALREADY_ACTIVE_MENU = new GMenu("He's talking to \nsomeone else...", null, 0, 6, 10, 3);

	public static final float ANIMATION_TIME_STEP = 666; // 2/3 of a second

	protected transient Image sprite[];
	protected int px = -1, py = -1;

	private int onClickEventID;

	public Direction dir;
	public int level;

	public float x, y;
	/*
	 * Animation variables
	 * movements keeps a list of the positions we need to move to received
	 *  from the server
	 * animationElapsedTime keeps track of how far into the animation we are
	 */
	protected transient LinkedList<Pose> movements;
	protected transient Pose newPosition;
	protected transient int animationElapsedTime;

	protected transient Direction directionBeforeTalk;
	protected transient boolean wasTalking = false;
	protected transient boolean wasTalkingToYou = false;

	public transient boolean animationEnabled;

    public Person() {

    	init();

    }

    public Person(String imgName, boolean cbso, Direction dir) {

    	this.imgName = imgName;
        this.dir = dir;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;

        loadImage();
        init();

    }

    /**
     * Returns true if the people have the same Unique Identifiers
     */
    @Override
    public boolean equals(Object other){

    	if(other == null) return false;
    	if(other == this) return true;
    	if(!(other instanceof Person)) return false;
    	return id == ((Person)other).id;

    }

    public void init(){

    	animationElapsedTime = 0;
    	animationEnabled = true;

    	movements = new LinkedList<Pose>();

    }
    
    public void loadImage() {

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

    public void deactivate() {
        px = -1;
        py = -1;
    }

    protected void setImage(int x, int y) {

    	if(     animationElapsedTime < ANIMATION_TIME_STEP*0.15
    	 ||     animationElapsedTime > ANIMATION_TIME_STEP*0.85
    	 || (   animationElapsedTime > ANIMATION_TIME_STEP*0.45
    	     && animationElapsedTime < ANIMATION_TIME_STEP*0.55
    		))
    		img = sprite[dir.getValue()];

    	else if(animationElapsedTime < ANIMATION_TIME_STEP*0.45){

    			img = sprite[dir.getValue()+4];

    	} else if(animationElapsedTime > ANIMATION_TIME_STEP*0.55){

    		if(dir == Direction.UP || dir == Direction.DOWN)
    			img = sprite[dir.getValue()+8];
    		else
    			img = sprite[dir.getValue()+4];

    	}

    }

    public Pose getPose(){
    	return new Pose(x, y, level, dir);
    }

    @Override
    public void draw(float x, float y, int xo, int yo, Graphics graphics) {

    	setImage((int)x, (int)y);
    	graphics.drawImage(img, (int)(x*16), (int)(y*16) - Player.CHARACTER_OFFSET, null);

    }

    private void animationMove(int deltaTime){

    	if(dir == Direction.UP)
    		y -= deltaTime/ANIMATION_TIME_STEP;

    	if(dir == Direction.DOWN)
    		y += deltaTime/ANIMATION_TIME_STEP;

    	if(dir == Direction.LEFT)
    		x -= deltaTime/ANIMATION_TIME_STEP;

    	if(dir == Direction.RIGHT)
    		x += deltaTime/ANIMATION_TIME_STEP;

    };

    /*
     * Updates animation variables
     */
    @Override
    public void update(int deltaTime){

		if(!animationEnabled) return;

    	/*
    	 * If we are doing an animation we continue it
    	 */
    	if(animationElapsedTime > 0){

    		animationElapsedTime += deltaTime;

	    	if(animationElapsedTime > ANIMATION_TIME_STEP){

	   			animationMove((int)(deltaTime-(animationElapsedTime-ANIMATION_TIME_STEP)));
	    		x = newPosition.getX();
	    		y = newPosition.getY();

	    		// Start next movement
	    		if(!movements.isEmpty()){

	    				newPosition = movements.remove();
	    	    		dir = newPosition.facing();

	    	    		if(((int)this.x) != newPosition.getX() || ((int)this.y) != newPosition.getY()){

	    	    			animationElapsedTime += deltaTime;
	    	    			animationMove((int)(deltaTime-(animationElapsedTime-ANIMATION_TIME_STEP)));

	    	    		}

	    		} else animationElapsedTime = 0;

	    	} else animationMove(deltaTime);

    	}

    	/*
    	 * If there are movements we need to animate we start there animations
    	 */
    	else if(!movements.isEmpty()){

    		newPosition = movements.remove();

    		dir = newPosition.facing();

    		if(((int)this.x) != newPosition.getX() || ((int)this.y) != newPosition.getY()){

    			animationElapsedTime += deltaTime;
    			animationMove(deltaTime);

    		}
    	}
    }

    @Override
    public boolean act() {
        return false;
    }

    public void addMovement(Pose position){

    	movements.add(position);

    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {

    	ois.defaultReadObject();

        loadImage();
        init();

        // TODO: Validate loaded object
    }

    public Object deepCopy() {

        return new Person(new String(imgName), canBeSteppedOn, dir);
    }

    @Override
    public void fromJSON(HashMap<String, Object> json){

    	super.fromJSON(json);

    	px = ((Float)json.get("px")).intValue(); 
    	py = ((Float)json.get("py")).intValue();

    	dir = Direction.getFromString((String) json.get("dir"));
    	level = ((Float)json.get("level")).intValue();

    	x = ((Float)json.get("x")).floatValue();
    	y = ((Float)json.get("y")).floatValue();

    	onClickEventID = ((Float)json.get("onClickEventID")).intValue();

    	animationEnabled = true;

    }

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

	@Override
	public void click() {

		EventCore.fireEvent(onClickEventID);

	}
}
