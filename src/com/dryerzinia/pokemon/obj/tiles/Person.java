package com.dryerzinia.pokemon.obj.tiles;

import java.io.*;
import java.util.HashMap;
import java.awt.*;

import com.dryerzinia.pokemon.event.EventCore;
import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.MovementAnimator;

public class Person extends Tile implements Actor, OnClick {

	static final long serialVersionUID = -47859827707060573L;

	public static final int A_MOVED = 0;
	public static final int A_TALKING_TO = 1;

	public static final GMenu ALREADY_ACTIVE_MENU = new GMenu("He's talking to \nsomeone else...", null, 0, 6, 10, 3);

	protected transient Image sprite[];
	protected int px = -1, py = -1;

	private int onClickEventID;

	protected Pose location;

	protected transient MovementAnimator movement;

	protected transient Direction directionBeforeTalk;
	protected transient boolean wasTalking = false;
	protected transient boolean wasTalkingToYou = false;

	public transient boolean animationEnabled;

    public Person() {

    	init();

    }

    public Person(String imgName, boolean cbso, Pose location) {

    	this.imgName = imgName;

    	this.location = location;

    	pixelOffsetX = 0;
        pixelOffsetY = 0;

        canBeSteppedOn = cbso;

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

    	animationEnabled = true;

    	loadImage();

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

        movement = new MovementAnimator(true, false);

    }

    public void deactivate() {
        px = -1;
        py = -1;
    }

    public Pose getPose(){
    	return location;
    }

    @Override
    public void draw(float x, float y, Graphics graphics) {

    	movement.draw(location, sprite, graphics);

    }

    /*
     * Updates animation variables
     */
    @Override
    public void update(int deltaTime){

		if(!animationEnabled) return;

		movement.update(Direction.NONE, location, deltaTime);

    }

    @Override
    public boolean act() {
        return false;
    }

    public void addMovement(Pose position){

    	movement.addMovement(position);

    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {

    	ois.defaultReadObject();

        loadImage();
        init();

        // TODO: Validate loaded object
    }

    public Object deepCopy() {

        return new Person(new String(imgName), canBeSteppedOn, location);
    }

    @Override
    public void fromJSON(HashMap<String, Object> json){

    	super.fromJSON(json);

    	px = ((Float)json.get("px")).intValue(); 
    	py = ((Float)json.get("py")).intValue();

    	location = (Pose) json.get("location");

    	onClickEventID = ((Float)json.get("onClickEventID")).intValue();

    	animationEnabled = true;

    	init();

    }

	@Override
	public void click() {

		EventCore.fireEvent(onClickEventID);

	}
}
