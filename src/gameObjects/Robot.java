package gameObjects;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.util.*;
import util.*;
import game.*;

public class Robot extends GameObject {
	final static float TARGET_THRESHOLD = 0.1f;

	public static final int X_OFFSET = 12; // the coordinates of the robot
											// relative to the image
	public static final int Y_OFFSET = 22; // basically where the robot's feet
											// are relative to image location

	private int dir;

	private static BufferedImage[] pics;

	static {
		pics = new BufferedImage[4]; // one for every direction

		for (int d = 0; d < 4; d++) {
			pics[d] = FileInput.loadImage("img\\robot\\walk" + d + ".png");
		}
	}

	public Vector2 position;

	protected void Position(Vector2 position) {
		this.position = position.Copy();
		this.location = level.ScreenToGrid(this.position);
	}

	public void Location(GridCoordinate location) {
		this.location = location.Copy();
		this.position = level.CentreGridToScreen(this.location);
	}

	GridCoordinate target = null;
	GameObject targetObj;
	ArrayList<GridCoordinate> route;
	GridCoordinate targetLocation;
	int currentCheckpoint = 0;
	//float maxSpeed = 0.75f;
	float patrolSpeed = 0.6f;
	float persueSpeed = 1.1f;
	public float speed;
	boolean patrolling;
	boolean frozen; // True if robot is to be unresponsive to tasks etc.
	boolean active; // True if robot is actively [planning on] going somewhere
	boolean moveTo;
	Vector2 lastDirection = new Vector2();
	float maxSeeDistance = -1; //-1 represents infinite

	boolean moving; // True if the robot has moved this frame

	public Robot(Level level, GridCoordinate location) {
		super(level, location);
		patrolling = false;
		frozen = false;
		moveTo = false;
		speed = 0.75f;
		active = false;
	}
	
	public void Freeze() {
		frozen = true;
	}

	public void Unfreeze() {
		frozen = false;
		target = null;
	}

	public boolean Active() {
		return active;
	}
	public boolean Patrolling() {
		return patrolling;
	}
	public boolean GoingSomewhere() {
		return moveTo;
	}
	public boolean ChasingTarget(){
		return moveTo == false && patrolling == false;
	}
	public void SetTarget(GameObject targetObj) {
		this.targetObj = targetObj;
		target = level.GetNextLocation(location, targetObj);
		patrolling = false;
		moveTo = false;
		active = true;
	}
	public GameObject GetTarget(){
		return targetObj;
	}

	

	

	public void SetPatrol(ArrayList<GridCoordinate> route) {
		patrolling = true;
		moveTo = false;
		this.route = route;
		currentCheckpoint = 0;
		for (int i = 0; i < route.size(); ++i) {
			if (route.get(i).X == location.X && route.get(i).Y == location.Y) {
				currentCheckpoint = (i + 1) % route.size();
				break;
			}
		}
		active = true;
	}

	public void SetPatrol(ArrayList<GridCoordinate> route, GameObject targetObj) {
		this.targetObj = targetObj;
		SetPatrol(route);
	}

	public void Goto(GridCoordinate location) {
		this.targetLocation = location.Copy();
		patrolling = false;
		moveTo = true;
		active = true;
	}
	public GridCoordinate GoingTo(){
		return targetLocation;
	}

	public void Draw(Graphics g) {
		g.drawImage(pics[dir], (int) position.X - X_OFFSET, (int) position.Y
				- Y_OFFSET, null);
	}

	private void AssignRandomTargetSquare() { // May assign 'null' if no valid
												// target
		ArrayList<GridCoordinate> validTargets = new ArrayList<GridCoordinate>();
		for (int x = location.X - 1; x <= location.X + 1; ++x) {
			for (int y = location.Y - 1; y <= location.Y + 1; ++y) {
				if (x == location.X && y == location.Y) continue;
				if (level.spaceAvailable(new GridCoordinate(x, y))
						&& Math.abs(x) - Math.abs(y) != 0
						|| (level.spaceAvailable(new GridCoordinate(x, location.Y))
						&& level.spaceAvailable(new GridCoordinate(location.X, y)))) {
					validTargets.add(new GridCoordinate(x, y));
				}
			}
		}
		if (validTargets.size() == 0) {
			target = null;
		} else {
			target = validTargets.get((int) (Math.random() * validTargets
					.size()));
		}
	}

	public void Update() {
		// Update target if needed
		// If have a targetObj, patrol, or moveTo, but have no actual target
		// location, then randomly assign target to be an available adjacent square
		//TODO consider having the robots trying to go 'towards' target, even if cannot reach
		//	To do this, must go to non-wall obstacle

		if (frozen || !active) {
			moving = false;
			return;
		}
		// The following is true if:
		// We don't have a target
		// Robot arrived at target location
		// A different object other than the target went into the square
		ArrayList<GameObject> ignoreList = new ArrayList<GameObject>(2);
		ignoreList.add(this);
		ignoreList.add(targetObj);
		if (target == null || !level.spaceAvailable(target, targetObj)) {
			// Update target
			if (patrolling) {
				if (location.Equals(route.get(currentCheckpoint))) {
					currentCheckpoint = (currentCheckpoint + 1) % route.size();
					// //System.out.println("At target of (" + target.X + ", " +
					// target.Y + ") New checkpoint: " + currentCheckpoint);
				}
				target = level.GetNextLocation(location,
						route.get(currentCheckpoint), ignoreList);
				if (target == null) {
					// //System.out.println("AssignRandomTargetSquare, cannot reach "
					// + route.get(currentCheckpoint).X + ", " +
					// route.get(currentCheckpoint).Y);
					AssignRandomTargetSquare();
				}
			} else if (moveTo) {
				if (location.Equals(targetLocation)) {
					// //System.out.println("Target location reached.");
					target = null;
					targetObj = null;
					moveTo = false;
				} else {
					target = level.GetNextLocation(location, targetLocation, ignoreList);
					if (target == null) {
						// //System.out.println("AssignRandomTargetSquare, cannot reach "
						// + targetLocation.X + ", " + targetLocation.Y);
						AssignRandomTargetSquare();
					}
				}
			} else if (targetObj != null) {
				// //System.out.println("Heading towards target");
				target = level.GetNextLocation(location, targetObj);
				if (target == null) {
					// //System.out.println("No path to target. Randomizing.");
					AssignRandomTargetSquare();
				}
			} else {
				// //System.out.println("Nothing to do. Target = null.");
				target = null;
				active = false;
			}
		}
		// If target == null, go to centre of current square. Otherwise, go
		// towards target.
		Vector2 direction;
		if (target != null) {
			Vector2 targetVector = level.CentreGridToScreen(target);
			Vector2 negation = level.CentreGridToScreen(location.Copy());
			negation.Negate();
			direction = targetVector.Plus(negation); // targetVector -
														// currentLocation
		} else if (location.equals(level.ScreenToGrid(position))) { // WILL
																	// ALWAYS BE
																	// TRUE!!!
			//System.out.println("Going to middle of the screen.");
			direction = position.Plus(level
					.CentreGridToScreen(new GridCoordinate(location.X,
							location.Y)));
		} else {
			return;
		}
		moving = true;
		// if (Math.abs(direction.X) < TARGET_THRESHOLD && Math.abs(direction.Y)
		// < TARGET_THRESHOLD){
		// if (moveTo){
		// Location(target); //NOTE: Reason collisions look so weird is because
		// of this! target.X/Y is often at edge of square!
		// active = false;
		// return;
		// } //TODO consider doing something if after a specific target (ex
		// player)?
		// }
		direction.Normalize();
		if (Math.abs(direction.X) > Math.abs(direction.Y)) {
			if (direction.X < 0) {
				dir = DIR_LEFT;
			} else {
				dir = DIR_RIGHT;
			}
		} else {
			if (direction.Y < 0) {
				dir = DIR_UP;
			} else {
				dir = DIR_DOWN;
			}
		}
		
		if (patrolling)
		{
			speed = patrolSpeed; //0.75f * maxSpeed;
		}
		else
		{
			speed = persueSpeed; //maxSpeed;
		}
		
		GridCoordinate oldLocation = location.Copy();
		
		Vector2 newPosition = position.Plus(direction.Times(speed));
		Position(newPosition);
		
		if (!location.Equals(oldLocation))
        {
            boolean canGoThere = level.spaceAvailable(location, this);

            if (!canGoThere)
            {
                position.PlusEquals(direction.Times(-1));
                location = oldLocation;

                position.X += direction.X;
                location = level.ScreenToGrid(position);

                if (!location.Equals(oldLocation))
                {
                    canGoThere = level.spaceAvailable(location, this);

                    if (!canGoThere)
                    {
                        position.X -= direction.X;
                        location = oldLocation;

                        position.Y += direction.Y;
                        location = level.ScreenToGrid(position);

                        if (!location.Equals(oldLocation))
                        {
                            canGoThere = level.spaceAvailable(location, this);

                            if (!canGoThere)
                            {
                                position.Y -= direction.Y;
                                location = oldLocation;
                            }
                        }
                    }
                }
            }
        }
		lastDirection = direction;
	}

	public boolean Collides() {
		return true;
	}

	public void Rotate() {
		// TODO FINISH based on 'dir' property! [assume clockwise]
	}

	public boolean CanSee(GameObject target) {
		/*int dx = 0;
    	int dy = 0;
    	
    	switch (dir)
    	{
    	case DIR_RIGHT:
    		dx = 1;
    		break;
    	case DIR_LEFT:
    		dx = -1;
    		break;
    	case DIR_UP:
    		dy = -1;
    		break;
    	case DIR_DOWN:
    		dy = 1;
    		break;
    	}*/
    	
    	Vector2 direction = lastDirection;
    	Vector2 difference = level.CentreGridToScreen(target.location).Subtract(position);
    	
		double theta = Math.acos((direction.X * difference.X + direction.Y * difference.Y) / (direction.Length() * difference.Length()));
		if (theta > Math.PI / 4) { // 90 degree FOV
			return false;
		}
		
		GridCoordinate gridDifference = target.location.Subtract(level.ScreenToGrid(position));
		Vector2 vectorGridDifference = new Vector2(gridDifference.X, gridDifference.Y);
		if (maxSeeDistance != -1 && vectorGridDifference.Length() > maxSeeDistance){
			return false;
		}
		float delta = 0.1f;
		Vector2 increment = vectorGridDifference.GetNormalized().Times(delta);
		Vector2 incrementPosition = new Vector2(location.X, location.Y);
		GridCoordinate lastPosition = location.Copy();
		GridCoordinate currentPosition = lastPosition;
		
		float maxDistance = vectorGridDifference.Length();
		float distanceCounter = 0;
		
		while (distanceCounter < maxDistance)
		{
			incrementPosition.PlusEquals(increment);
			distanceCounter += delta;
			
			currentPosition = new GridCoordinate((int)incrementPosition.X, (int)incrementPosition.Y);
			
			if (!currentPosition.Equals(lastPosition))
			{
				if (!level.spaceAvailable(currentPosition)) {
					GameObject collidingObject = level.collidingObjectAt(currentPosition);
					if (collidingObject != this && collidingObject != target) {
						return false;
					} else if (collidingObject == target) {
						return true;
					}
				}
			}
			
			lastPosition = currentPosition;
		}
		
		return true;
	}

}
