package turtle;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Signal;

public class turret extends robot {

	static int lastEncounter;
	MapLocation target;
	MapLocation attack;
	public turret(){
		leaderID =0;


		currentLocation = rc.getLocation();
	}

	@Override
	public void run() throws GameActionException {	

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

		if(rc.getType().equals(RobotType.TURRET))
			turretCode();
		else
			ttmCode();
	}

	public void ttmCode() throws GameActionException{

		MapLocation inRangeTarget = null;  
		currentLocation = rc.getLocation();

		RobotInfo[] enemies = rc.senseHostileRobots(currentLocation, rc.getType().sensorRadiusSquared);
		if(enemies.length>0){
			rc.unpack();
			return;
		}

		if(currentLocation.distanceSquaredTo(baseLocation)>25){
			moveTo(baseLocation);
		}

		if(!rc.isCoreReady())
			return;

		if(attack != null)
			moveTowards(currentLocation.directionTo(target));

		Signal[] signals = rc.emptySignalQueue();
		if(signals.length!=0){
			for(Signal s:signals){
				if(s.getID() == leaderID){
					baseLocation = s.getLocation();
					if(s.getMessage()==null)
						continue;
					if(s.getMessage()[0]==4){						
						moveTowards(s.getLocation().directionTo(currentLocation));
						return;
					}
					else if(s.getMessage()[0]==3){	
						Direction direction = Direction.values()[s.getMessage()[1]];
						moveTowards(direction);						
					}
					else if(s.getMessage()[0]==2){
						attack = decipher(s.getMessage()[1]);	;		
					}
				}
				else if(!s.getTeam().equals(rc.getTeam())){
					continue;
				}
				if(s.getMessage()[0]==1){
					target = decipher(s.getMessage()[1]);	
					if(currentLocation.distanceSquaredTo(target)<RobotType.TURRET.attackRadiusSquared){
						inRangeTarget = target;
					}
				}
			}


		}
		if(inRangeTarget!=null)
			rc.unpack();

	}

	public void turretCode() throws GameActionException{
		MapLocation inRangeTarget = null;  

		if(rc.isCoreReady()){
			currentLocation = rc.getLocation();
			RobotInfo[] enemies = rc.senseHostileRobots(currentLocation, rc.getType().sensorRadiusSquared);
			if(enemies.length>0){
				lastEncounter = rc.getRoundNum();
				if(rc.canAttackLocation(enemies[0].location)){
					rc.attackLocation(enemies[0].location);
				}
			}
			else{
				if(!rc.isCoreReady())
					return;

				Signal[] signals = rc.emptySignalQueue();

				for(Signal s:signals){
					if(!s.getTeam().equals(rc.getTeam()))
						continue;
					if(s.getMessage()!=null){
						if(s.getMessage()[0]==1){
							target = decipher(s.getMessage()[1]);								
							if(rc.canAttackLocation(target)){
								inRangeTarget = target;
								if(!rc.isCoreReady())
									break;
								rc.attackLocation(target);
							}
						}
					}

					if(s.getID() == leaderID){
						baseLocation = s.getLocation();
						if(s.getMessage()!=null){
							if(s.getMessage()[0]==4 || s.getMessage()[0]==3 || s.getMessage()[0]==2 ){						
								rc.pack();
								return;
							}
						}

					}



				}


				if(inRangeTarget==null)
					rc.pack();

			}

		}
	}


}
