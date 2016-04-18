package turtle;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Signal;

public class scout extends robot {
	
	static int lastEncounter = -150;
	
	public scout(){

		leaderID  =0;
		while(leaderID ==0){
			Signal[] signals = rc.emptySignalQueue();
			for(Signal s:signals){
				if(!s.getTeam().equals(rc.getTeam()))
					continue;
				int[] msg = s.getMessage();
				if(msg!=null){
					if(s.getMessage()[0]!=1){
						baseLocation = s.getLocation();
						leaderID = s.getID();
					}
				}
			}
		}
		
		currentLocation = rc.getLocation();
	}
	
	
	@Override
	public void run() throws GameActionException {	
		
		Signal[] signals = rc.emptySignalQueue();
		for(Signal s:signals){
			if(s.getID() == leaderID){
				baseLocation = s.getLocation();
			}
		}

		if(rc.isCoreReady()){	
			
			RobotInfo[] enemies = rc.senseHostileRobots(currentLocation, RobotType.SCOUT.sensorRadiusSquared);
			//RobotInfo[] team = rc.senseNearbyRobots(RobotType.ARCHON.sensorRadiusSquared, rc.getTeam());		
			
			if(enemies.length>0){
				lastEncounter = rc.getRoundNum();
				for(RobotInfo r : enemies){					
					if(r.type.attackRadiusSquared+3> rc.getLocation().distanceSquaredTo(r.location))
						moveTo(baseLocation);
						break;
				}
				int counter = 0;
				for(RobotInfo r : enemies){
					counter++;
					if(counter<20){
						int location = r.location.x*1000+r.location.y; 
						rc.broadcastMessageSignal(1, location, RobotType.SCOUT.sensorRadiusSquared*2);
					}
				}
			
				
			}
			else if(rc.getRoundNum()-lastEncounter>50){
				currentLocation = rc.getLocation();
				if(currentLocation.distanceSquaredTo(baseLocation) < RobotType.SCOUT.sensorRadiusSquared){
					if((currentLocation.x+currentLocation.y)%2==0){
						randomStraightMove();
						return;
					}
					randomDiagonalMove();
				}
				else
					moveTo(baseLocation);
			}


		}
	}
	
	
}
