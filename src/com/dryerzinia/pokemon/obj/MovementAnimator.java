package com.dryerzinia.pokemon.obj;

import java.awt.Image;
import java.util.LinkedList;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.map.Position;

public class MovementAnimator {

    public static final int JUMPING		= 0;
    public static final int STEPPING	= 1;
    public static final int SLIDING		= 2;
    public static final int NORMAL		= 3;

    private static final int LAZY_STEP_TIME = 666;
    private static final int FAST_STEP_TIME = 250;
    private static final int ROTATION_TIME	= 0; // TODO figure this out
    private static final int JUMP_TIME		= 0; // TODO figure this out

	private LinkedList<Position> movements;

	private Position newPosition;

	private int state;
    private int elapsedTime;

    private float stepTime;

    private boolean stepSide;

    private boolean isLazy;

    public MovementAnimator(boolean isLazy){

    	this.isLazy = isLazy;

    	movements = new LinkedList<Position>();

    	stepSide = false;

    	state = NORMAL;
    	elapsedTime = 0;

    }

    /**
	 * Animate character movement from keyboard input
	 * 
	 * @param direction
	 * @param position
	 * @param deltaTime
	 */
	public Position update(Direction direction, Position position, int deltaTime){
	
    	/*
    	 * If there is an animation going we finish it
    	 */
    	if(elapsedTime != 0){
    		return continueAnimation(direction, position, deltaTime);
    	}

		if(isLazy)
			stepTime = LAZY_STEP_TIME;
		else
			stepTime = FAST_STEP_TIME;

    	/*
    	 * Otherwise we either turn or start a new animation
    	 */
    	if(direction != Direction.NONE){

    		/*
        	 * If we change directions we just return
        	 */
    		if(direction != position.facing()){
    			position.setDirection(direction);
    			return position.copy();
    		}

    		/*
    		 * If we are same direction as before we start a movement animation
    		 * increment animation timer by deltaTime
    		 * move by deltaTime over time step
    		 */
    		continueAnimation(direction, position, deltaTime);

    	}

    	/*
    	 * If there are movements we need to animate we start there animations
    	 */
    	else if(!movements.isEmpty()){

    		newPosition = movements.remove();

    		position.setDirection(newPosition.facing());

        	int futureX = 0;
        	int futureY = 0;
        	
        	if(position.facing() == Direction.UP){
        		futureX = (int) Math.ceil(position.getX());
        		futureY = (int) Math.ceil(position.getY() - 1);
        	}

        	if(position.facing() == Direction.DOWN){
        		futureX = (int) Math.floor(position.getX());
        		futureY = (int) Math.floor(position.getY() + 1);
        	}

        	if(position.facing() == Direction.LEFT){
        		futureX = (int) Math.ceil(position.getX() - 1);
        		futureY = (int) Math.ceil(position.getY());
        	}

        	if(position.facing() == Direction.RIGHT){
        		futureX = (int) Math.floor(position.getX() + 1);
        		futureY = (int) Math.floor(position.getY());
        	}

        	Level level = GameState.getMap().getLevel(position.getLevel());

        	/*
        	 * If level is null then they are in the Fog of War
        	 */
        	if(level == null){
        		if(newPosition.getLevel() != -1)
        			position.setLevel(newPosition.getLevel());
        		else
        			return null;
        	}

    		if((Math.round(position.getX()) != Math.round(newPosition.getX())
    		||  Math.round(position.getY()) != Math.round(newPosition.getY()))
    		|| !level.canStepOn(futureX, futureY)){

    			elapsedTime += deltaTime;
    			animationMove(position, deltaTime);

    		}
    	}

    	return null;

	}

    private void animationMove(Position position, int deltaTime){

    	/*
    	 * Before we can move we have to make sure the tile can be stepped on
    	 * We still are incrementing the animation timer which lets us animate
    	 * the characters sprite so we can do the wall bump
    	 */

    	int futureX = 0;
    	int futureY = 0;
    	
    	if(position.facing() == Direction.UP){
    		futureX = (int) Math.ceil(position.getX());
    		futureY = (int) Math.ceil(position.getY() - 1);
    	}

    	if(position.facing() == Direction.DOWN){
    		futureX = (int) Math.floor(position.getX());
    		futureY = (int) Math.floor(position.getY() + 1);
    	}

    	if(position.facing() == Direction.LEFT){
    		futureX = (int) Math.ceil(position.getX() - 1);
    		futureY = (int) Math.ceil(position.getY());
    	}

    	if(position.facing() == Direction.RIGHT){
    		futureX = (int) Math.floor(position.getX() + 1);
    		futureY = (int) Math.floor(position.getY());
    	}

    	Level level = GameState.getMap().getLevel(position.getLevel());
    	boolean canStep = level.canStepOn(futureX, futureY);
    	LevelChange levelChange = level.grid.changeLevel(futureX, futureY);

    	/*
    	 * We need to continue the step through to the next level
    	 */
    	if(levelChange != null && levelChange.rightDirection(position.facing())){

    		position.set(levelChange.getNewPosition());
    		canStep = true;

    	}

    	if(canStep){

    		/*
    		 * If we are facing the direction
	    	 */

    		if(position.facing() == Direction.UP)
	    		position.subY(deltaTime/stepTime);
	
	    	if(position.facing() == Direction.DOWN)
	    		position.addY(deltaTime/stepTime);
	
	    	if(position.facing() == Direction.LEFT)
	    		position.subX(deltaTime/stepTime);
	
	    	if(position.facing() == Direction.RIGHT)
	    		position.addX(deltaTime/stepTime);

    	}

    }
	
    private Position continueAnimation(Direction direction, Position position, int deltaTime){

		elapsedTime += deltaTime;

    	/*
    	 * If we exceed time step
    	 */
    	if(elapsedTime > stepTime){
    
    		/*
    		 * change step side for alternating gait
    		 */
    		stepSide = !stepSide;
    		
    		/*
    		 * we have to get even with the square
    		 */
   			animationMove(position, (int)(deltaTime-(elapsedTime-stepTime)));
   			if(newPosition == null){
   				position.setX(Math.round(position.getX()));
   				position.setY(Math.round(position.getY()));
   			} else {
   				position.setX(newPosition.getX());
   				position.setY(newPosition.getY());
   				position.setLevel(newPosition.getLevel());
   				newPosition = null;
   			}

   			Position updated = position.copy();

			/*
			 * If we have no where to go we end the animation
			 */
   			if(direction == Direction.NONE)
    			elapsedTime = 0;

    		else if(!movements.isEmpty()){

				newPosition = movements.remove();
	    		position.setDirection(newPosition.facing());

	    		if(position.getX() != newPosition.getX() || position.getY() != newPosition.getY()){

	    			elapsedTime -= stepTime;
	    			animationMove(position, elapsedTime);

	    		}

    		}

   			/*
			 * But if we do have a direction to go we move the animation overflow time
			 * over to the next animation and change directions in case we turned and move
			 * that way
			 */
    		else {
    			elapsedTime -= stepTime;
    			position.setDirection(direction);
    			animationMove(position, elapsedTime);

    		}

    		return updated;

    	}

    	/*
   		 * Otherwise we just move like normal
   		 */
    	else
    		animationMove(position, deltaTime);

    	return null;

    }

	/**
	 * Returns correct sprite for current point in the animation
	 * 
	 * @param sprites Array of character sprites
	 * @param Facing Direction character is facing
	 * @return Sprite for current point in animation
	 */
	public Image animationImage(Image[] sprites, Direction facing){

		// TODO jumping and sliding sprite selection

		// Slow stepping, wandering motion
		if(isLazy){

			if(elapsedTime < LAZY_STEP_TIME*0.15
			|| elapsedTime > LAZY_STEP_TIME*0.85
			||(elapsedTime > LAZY_STEP_TIME*0.45
			&& elapsedTime < LAZY_STEP_TIME*0.55
			))
				return sprites[facing.getValue()];

			if(elapsedTime < LAZY_STEP_TIME*0.45)
				return sprites[facing.getValue() + 4];

			if(elapsedTime > LAZY_STEP_TIME*0.55
			&& (facing == Direction.UP || facing == Direction.DOWN))
				return sprites[facing.getValue() + 8];

		// Fast stepping, trainers, oak, player
		} else {

		
			if(elapsedTime < FAST_STEP_TIME*0.25
			|| elapsedTime > FAST_STEP_TIME*0.75)
				return sprites[facing.getValue()];

    		if((facing == Direction.UP || facing == Direction.DOWN) && stepSide)
    			return sprites[facing.getValue()+8];

    	}

    	return sprites[facing.getValue()+4];

	}

    public void addMovement(Position position){

    	movements.add(position);

    }
	
}
