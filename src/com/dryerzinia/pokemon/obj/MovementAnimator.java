package com.dryerzinia.pokemon.obj;

import java.awt.Image;
import java.util.LinkedList;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.map.Point;
import com.dryerzinia.pokemon.map.Position;

/**
 * Handles all animation of Player and Person objects and updating there
 * positions
 * 
 * TODO JUMPing requires us to draw a shadow so this class needs a draw
 * function
 * 
 * @author DrYerzinia
 */
public class MovementAnimator {

	/*
	 * Possible animation states for players and persons
	 * They can Jump down off of ledges
	 * They can be in the process of taking a step
	 * They can Spin/Slide from a Arrow Tile to a Stop tile
	 * They can be in a normal standing state
	 */
    private static final int JUMPING	= 0;
    private static final int STEPPING	= 1;
    private static final int SLIDING	= 2;
    private static final int NORMAL		= 3;

    /*
     * Animation timing constants
     * A lazy step that a wandering character does takes 2/3 of a second
     * A fast step that Oak, Trainers, and Players take is 1/4 of a second
     * The time it takes to make one revolution when sliding is
     * The time it takes to make it down a ledge is
     */
    private static final int LAZY_STEP_TIME = 666;
    private static final int FAST_STEP_TIME = 250;
    private static final int ROTATION_TIME	= 0; // TODO figure this out
    private static final int JUMP_TIME		= 0; // TODO figure this out

    /*
     * When movement is not being driven from the keyboard the character
     * executes a sequence of movements determined by a list of positions
     * the character should be in
     */
	private LinkedList<Position> movements;

	/*
	 * When the character is making a movement from the movements list the
	 * character needs to keep track of where he is going so he cant set
	 * his X, Y and level variables accordingly if there is a malfunction
	 * and he misses a movement update
	 */
	private Position newPosition;

	/*
	 * The animation state (Jump, Step, Slide, Normal)
	 * How many millisecond into the animation we are
	 */
	private int state;
    private int elapsedTime;

    /*
     * Time it takes to complete a step in the current state
     */
    private float stepTime;

    /*
     * Which foot the character should step with next
     */
    private boolean stepSide;

    /*
     * Does the character walk lazily or fast
     */
    private boolean isLazy;

    /**
     * Creates a MovmentAnimator to manage a characters motion
     * 
     * @param isLazy Does the character take long lazy steps 666ms or short
     * fast ones 250ms
     */
    public MovementAnimator(boolean isLazy){

    	this.isLazy = isLazy;

    	movements = new LinkedList<Position>();

    	stepSide = false;

    	state = NORMAL;
    	elapsedTime = 0;

    }

    /**
	 * Animate character movement from keyboard input or the movements queue
	 * 
	 * @param direction Key that is being pressed if not being driven from
	 * KeyBoard Direction.NONE must be sent
	 * @param position Current position of the character
	 * @param deltaTime Change in time in milliseconds
	 */
	public Position update(Direction direction, Position position, int deltaTime){

    	/*
    	 * If there is an animation going we finish it
    	 */
    	if(elapsedTime != 0){
    		return continueAnimation(direction, position, deltaTime);
    	}

    	/*
    	 * If its a lazy character we take our time with steps
    	 */
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

    		/*
    		 * If we get a new position where character is facing
    		 * Direction.NONE that means player is in fog of war so we need to
    		 * forget where player is
    		 */
    		if(newPosition.facing() == Direction.NONE){
    			position.set(newPosition);
    			return null;
    		}

    		boolean directionChange = position.facing() != newPosition.facing();
    		/*
    		 * Turn the character in new direction specified by movement
    		 */
    		position.setDirection(newPosition.facing());

    		/*
    		 * See where character is going
    		 */
        	Point futurePoint = nextTile(position);
        	Level level = GameState.getMap().getLevel(position.getLevel());

        	boolean levelChanged = false;

        	/*
        	 * If level is null then they are in the Fog of War
        	 */
        	if(level == null){
        		if(newPosition.getLevel() != -1)
        			position.setLevel(newPosition.getLevel());
        		else
        			return null;
        	} else {
            	LevelChange levelChange = level.grid.changeLevel(futurePoint.getX(), futurePoint.getY());
            	if(levelChange != null)
            		levelChanged = true;
        	}

        	boolean sameSpot = (Math.round(position.getX()) != Math.round(newPosition.getX()) ||  Math.round(position.getY()) != Math.round(newPosition.getY()));
        	boolean canStepNew = level.canStepOn(futurePoint.getX(), futurePoint.getY());

        	/*
        	 * If we are in different spot move
        	 * or if we are in same spot but next tile can not be stepped on
        	 * we bump the wall and don't move
        	 * or we just don't move (Probably a turn)
        	 * or if it is a level change and we are in same position it was
        	 * just a turn
        	 */
    		if(sameSpot || !canStepNew && !(levelChanged && directionChange)){

    			elapsedTime += deltaTime;
    			animationMove(position, deltaTime);

    		}
    	}

    	return null;

	}

	/**
	 * If there are no obsturctions moves character forward based on how much of time step has occurred
	 * @param position Current character position to be moved (mutated) forward
	 * @param deltaTime Change in time scince last move in milliseconds
	 */
    private void animationMove(Position position, int deltaTime){

    	/*
    	 * Before we can move we have to make sure the tile can be stepped on
    	 * We still are incrementing the animation timer which lets us animate
    	 * the characters sprite so we can do the wall bump
    	 */
    	
    	Point futurePoint = nextTile(position);

    	Level level = GameState.getMap().getLevel(position.getLevel());
    	boolean canStep = level.canStepOn(futurePoint.getX(), futurePoint.getY());
    	LevelChange levelChange = level.grid.changeLevel(futurePoint.getX(), futurePoint.getY());

    	/*
    	 * We need to continue the step through to the next level
    	 */
    	if(levelChange != null && levelChange.rightDirection(position.facing())){

    		/*
    		 * If we are the clients player we change positions immediatly
    		 */
    		if(newPosition == null)
    			position.set(levelChange.getNewPosition());
    		canStep = true;

    	}

    	if(canStep){

    		/*
    		 * If we are facing the direction move the character in that direction
	    	 */
    		switch(position.facing()){
    		case UP:
	    		position.subY(deltaTime/stepTime);
	    		break;

    		case DOWN:
	    		position.addY(deltaTime/stepTime);
	    		break;

    		case LEFT:
	    		position.subX(deltaTime/stepTime);
	    		break;
	
    		case RIGHT:
	    		position.addX(deltaTime/stepTime);
	    		break;

    		case NONE:
    			throw new IllegalArgumentException("Character can not be animated in Direction.NONE");
    		}

    	}

    }

    /**
     * Helper function to continue animation
     * 
     * @param direction Direction pressed down
     * @param position Current character position
     * @param deltaTime Change in time in milliseconds
     * @return new position of character to send to server if its The PLAYER
     */
    private Position continueAnimation(Direction direction, Position position, int deltaTime){

    	/*
    	 * Increment elapsed time
    	 */
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

   			/*
   			 * Copy position right after we finish moving to return to the
   			 * server at end of block
   			 */
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

   			/*
   			 * Tell the server where we are
   			 */
    		return updated;

    	}

    	/*
   		 * Otherwise we just move like normal
   		 */
    	else
    		animationMove(position, deltaTime);

    	/*
    	 * Tell the server we havent finsished moving
    	 */
    	return null;

    }

    /**
     * Returns the x y of the next tile player will step on
     * @param position Location of the player
     * @return Next point the player will step on
     */
    private Point nextTile(Position position){
    
    	switch(position.facing()){
    	case UP:
    		return new Point((int) Math.ceil(position.getX()), (int) Math.ceil(position.getY() - 1));

    	case DOWN:
    		return new Point((int) Math.floor(position.getX()), (int) Math.floor(position.getY() + 1));

    	case LEFT:
    		return new Point((int) Math.ceil(position.getX() - 1), (int) Math.ceil(position.getY()));

    	case RIGHT:
    		return new Point((int) Math.floor(position.getX() + 1), (int) Math.floor(position.getY()));
    	
    	case NONE:
    		throw new IllegalArgumentException("Player is facing direction NONE!");
    	}

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

		/*
		 * Slow stepping, wandering motion
		 * 5 part step with alternating feet
		 */
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

		/*
		 * Fast stepping, trainers, oak, player
		 * 3 Part step only one side used
		 */
		} else {

		
			if(elapsedTime < FAST_STEP_TIME*0.25
			|| elapsedTime > FAST_STEP_TIME*0.75)
				return sprites[facing.getValue()];

    		if((facing == Direction.UP || facing == Direction.DOWN) && stepSide)
    			return sprites[facing.getValue()+8];

    	}

		/*
		 * Default Mid step if we are not in normal position or other side
		 */
    	return sprites[facing.getValue()+4];

	}

	/**
	 * Add a new position for the character to animate into
	 * @param position Additional position for character to animate into
	 */
    public void addMovement(Position position){

    	movements.add(position);

    }
	
}
