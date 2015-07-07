package game;
import java.util.*;

import gameObjects.*;
import util.*;

public class RobotAI {	
	boolean lastPlayerSeen, playerSeen;
	GridCoordinate lastKnownPlayerPosition;
	public ArrayList<RobotInfo> robots;
	ArrayList<RobotInfo> robotsThatSeePlayer = new ArrayList<RobotInfo>(); //should only reference elements in robots list
	Level level;
	Player player;
	boolean robotsCommunicate = true;
	//float communicationDistance = -1; //-1 means infinite
	//communicationDistance not yet implemented
	
//	public void SetRobotCommunication(boolean communicationAllowed, float communicationDistance){
//		robotsCommunicate = communicationAllowed;
//		this.communicationDistance = communicationDistance;
//	}
	public void SetRobotCommunication(boolean communicationAllowed){
		robotsCommunicate = communicationAllowed;
	}
	
	public RobotAI(Level level, Player player){
		robots = new ArrayList<RobotInfo>();
		this.level = level;
		this.player = player;
		playerSeen = false;
		lastPlayerSeen = false;
	}
	public void AddRobot(RobotInfo robotInfo){
		robots.add(robotInfo);
		robotInfo.ResumeDefault();
	}
		
	public void Update(){
		playerSeen = false; //true any robot can see the player this turn
		boolean updateChasingRobots = false;
		for (int i = 0; i < robots.size(); ++i){
			if (robots.get(i).robot.CanSee(player)){
				playerSeen = true;					

				if (!lastPlayerSeen){ //Did not see the player last update
					robotsThatSeePlayer.clear();
					lastPlayerSeen = true;
					updateChasingRobots = true;
					lastKnownPlayerPosition = player.Location().Copy();
				} else if (!player.Location().Equals(lastKnownPlayerPosition)){ //see the player in a new spot
					updateChasingRobots = true;
					lastKnownPlayerPosition = player.Location().Copy();
				} //else a robot sees the player in the same spot as before
				
				if (!robotsThatSeePlayer.contains(robots.get(i))){ //TODO inefficient; should use tree
					robotsThatSeePlayer.add(robots.get(i));
				}
				//robots.get(i).ChaseTarget(player); //make robots that see player chase
			}
		}
		if (!playerSeen && lastPlayerSeen){ 
			//The player is no longer in sight of the robots
			//Make robots that saw the player continue the chase
			//Make all other robots go back to what they were doing
			for (int i = 0; i < robotsThatSeePlayer.size(); ++i){
				robotsThatSeePlayer.get(i).ChaseTarget(lastKnownPlayerPosition);
				if (i == 0){ //TODO should change this to "nearest robot"
					robotsThatSeePlayer.get(i).ScanArea(level, lastKnownPlayerPosition, 3, player, false);
				}				
			}
			for (int i = 0; i < robots.size(); ++i){ //Make all robots that are chasing the player stop
				if (robots.get(i).ChasingTarget(player)){
					robots.get(i).ResumeDefault();
				}
			}
			robotsThatSeePlayer.clear();
			lastPlayerSeen = false;
		} else if (updateChasingRobots){
			//All robots that see player unioned with those that are within communicationDistance are to:
			//	All chase player
			//NOTE: Currently, just set all robots to chase the player
			for (int i = 0; i < robots.size(); ++i){
				robots.get(i).ChaseTarget(player);
			}
		}
		for (int i = 0; i < robots.size(); ++i){
			robots.get(i).Update();
		}
	}
}
