package turtle;

import battlecode.common.*;

public class RobotPlayer extends robot{
	

	
	public static void run(RobotController rcIn) throws GameActionException{
		
		rc = rcIn;		
		robot unit = null;
		
		if(rc.getType()==RobotType.ARCHON){
			unit = new archon();
		}else if(rc.getType()==RobotType.TURRET){
			unit= new turret();
		}
		else if(rc.getType()==RobotType.SCOUT){
			unit= new scout();
		}
		else{
			while(true);
		}
				
		
		while(true){
			if(rc.isCoreReady())
			try{				
				unit.run();
			}catch(Exception e){
				e.printStackTrace();
			}

			Clock.yield();
		}
	}
	

	





	
	
}