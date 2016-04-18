/***
signal info: 
fist int:
1 = target spotted
2 = move towards location
3 = move to direction
4 = move outwards

second int:
Xcord*1000+Ycord
 */



package turtle;

import java.util.ArrayList;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class robot {

	static RobotController rc;
	static MapLocation currentLocation;
	static Direction[] straight = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
	static Direction[] diagonal = new Direction[]{Direction.NORTH_WEST, Direction.NORTH_EAST, Direction.SOUTH_EAST, Direction.SOUTH_WEST};
	static Direction[] moves = new Direction[]{Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST,Direction.EAST, Direction.SOUTH_EAST,  Direction.SOUTH,Direction.SOUTH_WEST, Direction.WEST};
	static int[] possibleDirections = new int[]{0,1,-1,2,-2,3,-3,4};
	static MapLocation baseLocation;
	static boolean reachedBased = false;
	static int leaderID =0;
	static ArrayList<MapLocation> pastLocation = new ArrayList<>();
	static boolean patient = true;



	protected static void moveTo(MapLocation target) throws GameActionException {

		if(!rc.isCoreReady())
			return;
		Direction ahead = currentLocation.directionTo(target);
		for(int i:possibleDirections){
			Direction candidateDirection = Direction.values()[(ahead.ordinal()+i+8)%8];
			MapLocation candidateLocation = rc.getLocation().add(candidateDirection);
			if(rc.canMove(candidateDirection) &&!pastLocation.contains(candidateLocation)){
				pastLocation.add(rc.getLocation());
				if(pastLocation.size() > 15)
					pastLocation.remove(0);
				rc.move(candidateDirection);
				return;
			}
		}
		if(!patient)
			pastLocation.clear();
		patient = false;

	}

	protected static void moveDiagonallyTo(MapLocation target) throws GameActionException {

		for(Direction candidateDirection:diagonal){
			if(rc.canMove(candidateDirection)){
				rc.move(candidateDirection);
				return;
			}
		}

	}

	protected static void moveTowards(Direction direction) throws GameActionException{
		if(!rc.isCoreReady())
			return;
		for(int i:possibleDirections){
			Direction candidateDirection = Direction.values()[(direction.ordinal()+i+8)%8];
			MapLocation candidateLocation = rc.getLocation().add(candidateDirection);
			if(patient){
				if(rc.canMove(candidateDirection) &&!pastLocation.contains(candidateLocation)){
					pastLocation.add(rc.getLocation());
					if(pastLocation.size() > 25)
						pastLocation.remove(0);
					rc.move(candidateDirection);
					return;
				}
			}
			else {
				if(rc.canMove(candidateDirection)){
					rc.move(candidateDirection);
					return;					
				}
				else{
				
				}
				
			}
		}
		if(!patient)
			pastLocation.clear();
		patient = false;
	}

	protected static void randomStraightMove() throws GameActionException {


		while(true){
			Direction d = straight[(int) (Math.random()*straight.length)];
			if(rc.canMove(d)){
				rc.move(d);
				return;
			}
		}

	}

	protected static void randomDiagonalMove() throws GameActionException {
		while(true){
			Direction d = diagonal[(int) (Math.random()*diagonal.length)];
			if(rc.canMove(d)){
				rc.move(d);
				return;
			}
		}

	}

	protected static void randomMove() throws GameActionException {
		while(true){
			Direction d = moves[(int) (Math.random()*diagonal.length)];
			if(rc.canMove(d)){
				rc.move(d);
				return;
			}
		}

	}
	public void run() throws GameActionException {

	}

	public MapLocation decipher(int msg){
		String message = msg+"";
		String x = message.substring(0, 3);
		String y = message.substring(3);
		return new MapLocation(Integer.parseInt(x),Integer.parseInt(y));	
	}

}
