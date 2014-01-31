package com.dryerzinia.pokemon.obj;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.awt.*;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;

public class Person extends Tile implements Actor {

	static final long serialVersionUID = -47859827707060573L;

	public static final int A_MOVED = 0;
	public static final int A_TALKING_TO = 1;

	public static final GMenu ALREADY_ACTIVE_MENU = new GMenu("He's talking to \nsomeone else...", null, 0, 6, 10, 3);

	public static final float ANIMATION_TIME_STEP = 666; // 2/3 of a second

	protected transient Image sprite[];
	protected int px = -1, py = -1;

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

    public Person() {

    	init();

    }

    public Person(String imgName, boolean cbso, GMenu onClick, Direction dir) {

    	this.imgName = imgName;
        this.dir = dir;
        pixelOffsetX = 0;
        pixelOffsetY = 0;
        canBeSteppedOn = cbso;
        this.onClick = onClick;

        onClick.container = this;

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

    public GMenu getMenu(int x, int y) {
        if (!onClick.active) {
            px = x;
            py = y;
            onClick.active = true;
            wasTalkingToYou = true;
            setImage(x - (int)ClientState.player.getPose().getX(), y - (int)ClientState.player.getPose().getY());
            Client.writeActor(this, A_TALKING_TO);
            return onClick;
        } else {
            return ALREADY_ACTIVE_MENU;
        }

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

    	/*
    	 * TODO Fix talking to crap
    	 */
    	/*
        try {
            if ((onClick != null && !onClick.active) || onClick == null) {
                if (wasTalking && wasTalkingToYou) {
                    dir = directionBeforeTalk;
                    wasTalkingToYou = false;
                    Client.writeActor(this, A_TALKING_TO);
                }
                img = sprite[dir.getValue()];
                directionBeforeTalk = dir;
                wasTalking = false;
            } else if (!wasTalkingToYou) {
                img = sprite[dir.getValue()];
                wasTalking = true;
            } else if (x <= 3) {
                wasTalking = true;
                dir = Direction.RIGHT;
                img = sprite[3];
            } else if (x >= 5) {
                wasTalking = true;
                dir = Direction.LEFT;
                img = sprite[2];
            } else if (y <= 3) {
                wasTalking = true;
                dir = Direction.DOWN;
                img = sprite[1];
            } else if (y >= 5) {
                wasTalking = true;
                dir = Direction.UP;
                img = sprite[0];
            }
        } catch (NullPointerException npe) {
            if (sprite == null) {
                System.out.println("Sprites not loaded!");
                if (imgName != null && !imgName.equals(""))
                    loadImage();
            }
        }
        */
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
        GMenu g = null;
        if (onClick != null)
            g = (GMenu) onClick.deepCopy();
        return new Person(new String(imgName), canBeSteppedOn, g, dir);
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


    }

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}
}
