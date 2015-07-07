package game;

import gameObjects.GameObject;
import gameObjects.Robot;

import java.util.ArrayList;
import java.util.LinkedList;

import util.GridCoordinate;

public class RobotInfo{
	public enum NextAction{
		NONE,
		ROTATE,
		UNFREEZE,
		CHANGE_ACTION
	}
	public Robot robot;
	public String identifier; //for scripting purposes
	public ArrayList<GridCoordinate> defaultRoute;
	public GameObject defaultTargetObj;
	public GridCoordinate station;
	public LinkedList<RobotInfo> defaultActions;
	public LinkedList<RobotInfo> futureActions = new LinkedList<RobotInfo>();
	public boolean defaultCycleActions = false;
	public boolean cycleActions = false;
	public int defaultFramesLeft = 0; //for timed actions, in conjunction with nextAction.
	public int maxFramesLeft = 0;
	public int framesLeft = 0; 
	public NextAction defaultAction;
	public NextAction nextAction = NextAction.NONE;
	
	public RobotInfo(String identifier, Robot robot){
		this.identifier = identifier;
		this.robot = robot;
		this.defaultRoute = null;
		this.defaultTargetObj = null;
		this.station = null;
		this.defaultActions = null;
		this.defaultAction = NextAction.NONE;
	}
	public RobotInfo(String identifier, Robot robot, ArrayList<GridCoordinate> defaultRoute, GameObject defaultTargetObj){
		this.identifier = identifier;
		this.robot = robot;
		this.defaultRoute = defaultRoute;
		this.defaultTargetObj = defaultTargetObj; //this can be null
		this.station = null;
		this.defaultActions = null;
		this.defaultAction = NextAction.NONE;
	}
	public RobotInfo(String identifier, Robot robot, GameObject defaultTargetObj){
		this.identifier = identifier;
		this.robot = robot;
		this.defaultRoute = null;
		this.defaultTargetObj = defaultTargetObj;
		this.station = null;
		this.defaultActions = null;
		this.defaultAction = NextAction.NONE;
	}
	public RobotInfo(String identifier, Robot robot, GridCoordinate station){
		this.identifier = identifier;
		this.robot = robot;
		this.defaultRoute = null;
		this.defaultTargetObj = null;
		this.station = station;
		this.defaultActions = null;
		this.defaultAction = NextAction.NONE;
	}
	public RobotInfo(String identifier, Robot robot, LinkedList<RobotInfo> actionList, boolean cycleList, int timeout){
		//NOTE: Set "timeout" to 0 if you do not want it used.
		this.identifier = identifier;
		this.robot = robot;
		this.defaultRoute = null;
		this.defaultTargetObj = null;
		this.station = null;
		this.defaultActions = actionList;
		this.defaultCycleActions = cycleList;
		this.defaultFramesLeft = timeout;
		this.defaultAction = timeout > 0 ? NextAction.CHANGE_ACTION : NextAction.NONE;
	}
	public void Freeze(int frames){
		robot.Freeze();
		nextAction = NextAction.UNFREEZE;
		framesLeft = frames;
	}
	public void Rotate(int times, int framesPause, boolean onceNow){
		//This rotates the robot the # of times specified with 'framesPause' time in between each.
		//onceNow means to rotate the robot immediately now instead of first waiting 'framesPause'.
		if (framesPause < 1){
			for (int i = 0; i < times; ++i){
				robot.Rotate();
			}
			return;
		}
		if (onceNow){
			robot.Rotate();
			--times;
		}
		if (times == 0){
			return;
		}
		futureActions.clear();
		cycleActions = false;
		framesLeft = maxFramesLeft = framesPause;
		nextAction = NextAction.CHANGE_ACTION;
		RobotInfo ri;
		for (int i = 0; i < times; ++i){
			ri = new RobotInfo(identifier, robot);
			ri.defaultAction = NextAction.ROTATE;
			futureActions.add(ri);
		}
	}
	@SuppressWarnings("unchecked")
	public void ResumeDefault(){
		if (defaultRoute != null){
			if (defaultTargetObj != null){
				robot.SetPatrol(defaultRoute, defaultTargetObj);
			} else {
				robot.SetPatrol(defaultRoute);
			}				
		} else if (defaultTargetObj != null){
			robot.SetTarget(defaultTargetObj);
		} else if (station != null){
			robot.Goto(station);
		} else if (defaultActions != null){
			cycleActions = defaultCycleActions;
			futureActions = (LinkedList<RobotInfo>)defaultActions.clone();
			nextAction = defaultAction;
			framesLeft = maxFramesLeft = defaultFramesLeft;
		} else if (defaultAction != NextAction.NONE){ //1 time action
			if (defaultAction == NextAction.ROTATE){
				robot.Rotate();
			} else if (defaultAction == NextAction.UNFREEZE) {
				robot.Unfreeze();
			}
		}
	}
	public void ChaseTarget(GameObject target){
		robot.SetTarget(target);
	}
	public void ChaseTarget(GridCoordinate lastSeen){
		robot.Goto(lastSeen);
	}
	public boolean ChasingTarget(GameObject target){
		return robot.GetTarget() == target && robot.ChasingTarget();
	}
	public boolean ChasingTarget(GridCoordinate location){
		return robot.GoingTo().Equals(location) && robot.GoingSomewhere();
	}
	public void ScanArea(Level level, GridCoordinate start, int size, GameObject target, boolean actImmediately){
		//uses size as a maximum, as may not be enough area to hold the scanning area
		//size of 1 means 1x1 area
		ArrayList<GameObject> ignoreList = new ArrayList<GameObject>(2);
		ignoreList.add(target);
		ignoreList.add(robot);
		int i;
		for (i = 2; i <= size; ++i){
			int left = start.X - (int)Math.floor(i/2.0);
			int right = start.X + (int)Math.ceil(i/2.0);
			int top = start.Y - (int)Math.floor(i/2.0);
			int bottom = start.Y + (int)Math.ceil(i/2.0);
			boolean acceptable = true;
			//Horizontal path check				
			for (int y = top; y != bottom && acceptable; y = bottom){
				for (int x = left; x <= right; ++x){
					if (!level.spaceAvailable(new GridCoordinate(x, y), ignoreList)){
						acceptable = false;
						break;
					}
				}
			}
			//Vertical path check
			if (acceptable){
				for (int x = left; x != right && acceptable; x = right){
					for (int y = top; y <= bottom; ++y){
						if (!level.spaceAvailable(new GridCoordinate(x, y), ignoreList)){
							acceptable = false;
							break;
						}
					}
				}
			}
			if (!acceptable){
				--i;
				break;
			}
		}
		int left = start.X - (int)Math.floor(i/2.0);
		int right = start.X + (int)Math.ceil(i/2.0);
		int top = start.Y - (int)Math.floor(i/2.0);
		int bottom = start.Y + (int)Math.ceil(i/2.0);
		futureActions.clear();
		futureActions.add(new RobotInfo(identifier, robot, new GridCoordinate(left, top)));
		futureActions.add(new RobotInfo(identifier, robot, new GridCoordinate(right, top)));
		futureActions.add(new RobotInfo(identifier, robot, new GridCoordinate(right, bottom)));
		futureActions.add(new RobotInfo(identifier, robot, new GridCoordinate(left, bottom)));
		for (int c = 0; c < (int)(Math.random()*4); ++c){
			CycleActions();
		}
		cycleActions = false; //Means robot does 1 scan and then goes back to what it was doing
		futureActions.add(futureActions.getFirst()); //TODO confirm ok
		if (actImmediately){
			futureActions.getFirst().ResumeDefault();
		}
	}	
	private void CycleActions(){
		futureActions.addLast(futureActions.removeFirst());
	}
	private void UpdateActions(){
		if (cycleActions){
			CycleActions();
		} else {
			futureActions.removeFirst();
		}
		if (futureActions.size() > 0){
			futureActions.getFirst().ResumeDefault();
		} else {
			ResumeDefault();
		}
	}
	public void Update(){
		if (futureActions.size() > 0){
			if (nextAction != NextAction.NONE){ //update frames
				if (--framesLeft <= 0){
					if (nextAction == NextAction.CHANGE_ACTION){
						UpdateActions();
					} else if (nextAction == NextAction.UNFREEZE){
						robot.Unfreeze();
						nextAction = NextAction.NONE;
					} else if (nextAction == NextAction.ROTATE){
						robot.Rotate();
						nextAction = NextAction.NONE;
					}
				}										
			} else {
				if (!robot.Active()){
					UpdateActions();
				}
			}
		}
	}
}
