package turtle;

import battlecode.common.*;

public class archon extends robot{


	int scouts = 0;
	int counter =0;
	int turrets = 0;


	Direction awayFromEnemy;
	Signal[] signals;

	MapLocation target;

	boolean leader = false;
	boolean scout = false;

	public archon(){
		awayFromEnemy = rc.getInitialArchonLocations(rc.getTeam().opponent())[0].directionTo(baseLocation);
		baseLocation = rc.getInitialArchonLocations(rc.getTeam())[0];
		currentLocation = rc.getLocation();
		if(baseLocation.distanceSquaredTo(currentLocation)<2)
			leader = true;
	}

	@Override
	public void run() throws GameActionException {		
		currentLocation = rc.getLocation();

		if(leader)
			leaderCode();
		else
			followerCode();

		if(!scout && rc.isCoreReady() && rc.getTeamParts()>50){
			for(Direction D:diagonal){
				if(rc.canBuild(D, RobotType.SCOUT)){
					rc.build(D,RobotType.SCOUT);
					scout = true;
					break;
				}
			}			
		}

		RobotInfo[] robots = rc.senseNearbyRobots(RobotType.ARCHON.sensorRadiusSquared, rc.getTeam());
		for(RobotInfo r : robots){
			if(r.maxHealth-r.health>0 && !r.type.equals(RobotType.ARCHON)){
				heal(r);
				return;
			}
		}
	}


	private void leaderCode() throws GameActionException{
		
		if((3000-rc.getRoundNum())%200==0){
			rc.broadcastSignal(2500);
		}

		RobotInfo[] team = rc.senseNearbyRobots(RobotType.ARCHON.sensorRadiusSquared, rc.getTeam());
		
		turrets = 0;
		for(RobotInfo r: team){			
			if(r.type.equals(RobotType.SCOUT))
				scouts++;
			else if(r.type.equals(RobotType.TURRET)||r.type.equals(RobotType.TTM))
				turrets++;
		}	


		//deffensive code
		if(turrets<15){
			RobotInfo[] enemies = rc.senseHostileRobots(currentLocation,RobotType.ARCHON.sensorRadiusSquared);
			signals = rc.emptySignalQueue();
			
			if(enemies.length>0){
				for(RobotInfo r:enemies){
					if(r.type.equals(RobotType.SCOUT))
						continue;
					target =r.location;
					pastLocation.clear();
					awayFromEnemy = currentLocation.directionTo(enemies[0].location).opposite();
					break;
				}
							
			}
			else if(rc.isCoreReady() && rc.getTeamParts()>130){
				for(Direction D:moves){
					if(rc.canBuild(D, RobotType.TURRET)){
						rc.build(D,RobotType.TURRET);
						return;
					}
				}
				rc.broadcastMessageSignal(4, 4, RobotType.ARCHON.sensorRadiusSquared);
				return;
			}
			else if(signals.length>0){
				for(Signal s:signals){
					if(!s.getTeam().equals(rc.getTeam())){
						//
						continue;
					}
					if(s.getMessage()[0]==1){
						String message = s.getMessage()[1]+"";
						String x = message.substring(0, 3);
						String y = message.substring(3);
						target = new MapLocation(Integer.parseInt(x),Integer.parseInt(y));						
						awayFromEnemy = currentLocation.directionTo(target).opposite();	
						break;
					}
				}
			}
		
			else{
			}

			if(awayFromEnemy==null)
				awayFromEnemy = rc.getInitialArchonLocations(rc.getTeam().opponent())[0].directionTo(baseLocation);
			if(rc.isCoreReady()){
				moveTowards(awayFromEnemy);				
				rc.broadcastMessageSignal(3, awayFromEnemy.ordinal(), RobotType.ARCHON.sensorRadiusSquared*3);
			}

		}
		
		//offensive code
		else{
			RobotInfo[] enemies = rc.senseHostileRobots(currentLocation,RobotType.ARCHON.sensorRadiusSquared);
			for(RobotInfo r : enemies){	
				if(r.type.equals(RobotType.SCOUT))
					continue;
				if(r.type.attackRadiusSquared+3> rc.getLocation().distanceSquaredTo(r.location))
					pastLocation.clear();
					moveTowards(currentLocation.directionTo(enemies[0].location).opposite());
					return;
			}
			
			if(rc.isCoreReady() && rc.getTeamParts()>130){
				for(Direction D:moves){
					if(rc.canBuild(D, RobotType.TURRET)){
						rc.build(D,RobotType.TURRET);
						rc.broadcastMessageSignal(4, 4, RobotType.ARCHON.sensorRadiusSquared);
						return;
					}
				}
				rc.broadcastMessageSignal(4, 4, RobotType.ARCHON.sensorRadiusSquared);
				return;
				//randomDiagonalMove();
			}
			if(target != null && rc.isCoreReady()){
			//	System.out.println("moveout");
				Direction towards = currentLocation.directionTo(target);				
				rc.broadcastMessageSignal(3, towards.ordinal(),  RobotType.ARCHON.sensorRadiusSquared*3);
				moveTowards(currentLocation.directionTo(target));
			}
			else if(currentLocation.distanceSquaredTo(target)<15){
				signals = rc.emptySignalQueue();
				for(Signal s:signals){
					if(s.getTeam().equals(rc.getTeam().opponent())){
						target = s.getLocation();
						break;
					}
				}
				Direction towards = currentLocation.directionTo(target);
				rc.broadcastMessageSignal(3, towards.ordinal(),  RobotType.ARCHON.sensorRadiusSquared*3);
				moveTowards(currentLocation.directionTo(target));
				
			}
			else{
				target = rc.getInitialArchonLocations(rc.getTeam().opponent())[0];
				Direction towards = currentLocation.directionTo(target);
				rc.broadcastMessageSignal(3, towards.ordinal(),  RobotType.ARCHON.sensorRadiusSquared*3);
				moveTowards(currentLocation.directionTo(target));
			}

		}


	}

	private void followerCode() throws GameActionException{
		
		RobotInfo[] enemies = rc.senseHostileRobots(currentLocation,RobotType.ARCHON.sensorRadiusSquared);
		for(RobotInfo r : enemies){					
			if(r.type.attackRadiusSquared+3> rc.getLocation().distanceSquaredTo(r.location))
				pastLocation.clear();
				moveTowards(currentLocation.directionTo(enemies[0].location).opposite());
				return;
		}
		
		//If too far from leader, move towards it
		if(currentLocation.distanceSquaredTo(baseLocation)>5){
			if(rc.isCoreReady()){
				moveTo(baseLocation);
				return;
			}
		}
		

		//Find out what the leaders ID is
		while(leaderID ==0){
			signals = rc.emptySignalQueue();
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

		//If leader is known, find his position
		if(leaderID !=0){
			signals = rc.emptySignalQueue();
			for(Signal s:signals){
				if(s.getID() == leaderID)
					baseLocation = s.getLocation();
			}
		}


		//Move to leader, if too far apart
		if(currentLocation.distanceSquaredTo(baseLocation)>15){
			moveTo(baseLocation);
		}



	}
	private static void heal(RobotInfo r) throws GameActionException {
		if(rc.isCoreReady()){
			while(currentLocation.distanceSquaredTo(r.location)>RobotType.ARCHON.attackRadiusSquared){
				moveTo(r.location);
				return;
			}
			rc.repair(r.location);
		}

	}


}
