public class StateAndReward {

	private static final int intervalsAngle = 13;
	private static final int intervalsHorVelocity = 5;
	private static final int intervalsVertVelocity = 7; 
	//private static final int intervalsHorVelocity = 10;
	//private static final int intervalsVertVelocity = 10;
	
	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {

		int discAngle = discretize2(angle, intervalsAngle, -3, 3);
		String state = String.valueOf(discAngle);
		//System.out.println(state);
		
		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
		int discAngle = discretize2(angle, intervalsAngle, -3, 3);

		double reward = intervalsAngle/2 - Math.abs(intervalsAngle/2 - discAngle);
		if (Math.abs(intervalsAngle/2 - discAngle) != 0) reward = reward / (Math.abs(intervalsAngle/2 - discAngle));
		else{
			reward = reward*2;
		}
		//System.out.println(reward);
		return reward;
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		int discAngle = discretize2(angle, intervalsAngle, -3, 3);
		int discHorVelocity = discretize(vx, intervalsHorVelocity, -3, 3);
		int discVertVelocity = discretize(vy, intervalsVertVelocity, -5.5, 5.5); 
		String state = "angle" + String.valueOf(discAngle) + "hor" + String.valueOf(discHorVelocity) + "vert" +  String.valueOf(discVertVelocity);
		//System.out.println(discAngle);
		
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {
		int discAngle = discretize2(angle, intervalsAngle, -3, 3);
		int discHorVelocity = discretize(vx, intervalsHorVelocity, -3, 3);
		int discVertVelocity = discretize(vy, intervalsVertVelocity, -5.5, 5.5); 
		
		/*
		// GOAL FOR ANGLE IS 6
		double angleReward = intervalsAngle/2 - Math.abs(intervalsAngle/2 - discAngle);
		if (Math.abs(intervalsAngle/2 - discAngle) != 0) angleReward = angleReward / Math.abs(intervalsAngle/2 - discAngle);
		else{
			angleReward = angleReward*2;
		}
		*/
		
		int distToGoalAngle = Math.abs(intervalsAngle/2 - discAngle);
		double angleReward = 0;
		switch(distToGoalAngle) {
			case 0:
				angleReward = 35;
				break;
			case 1:
				angleReward = 15;
				break;
			case 2:
				angleReward = 8;
				break;
			case 3:
				angleReward = 1;
				break;
			default: break;
		}
		
		
		
		// GOAL FOR HORIZONTAL IS state 2

		/*
		double horReward = intervalsHorVelocity /2 - Math.abs(intervalsHorVelocity/2 - discHorVelocity);
 		if (Math.abs(intervalsHorVelocity/2 - discHorVelocity) != 0) horReward = horReward / Math.abs(intervalsHorVelocity/2 - discHorVelocity);
 		else{
 			horReward = horReward*5;
		}
 		*/
		
		int distToGoalVX = Math.abs(intervalsHorVelocity/2 - discHorVelocity);
		double horReward = 0;
		
		switch(distToGoalVX) {
		case 0:
			horReward = 10;
			break;
		case 1:
			horReward = 2;
			break;
		default: 
			break;
	}
		

		
		//GOAL FOR VERTICAL IS state (3,4)
		
 		int distToGoalVY = Math.abs(intervalsVertVelocity/2 - discVertVelocity);
		double vertReward = 0;
		
		switch(distToGoalVY) {
		case 0:
			vertReward = 35; 
			break;
		case 1:
			vertReward = 5;
			break;
		case 2:
			vertReward = 1;
			break;
		default: break;
		}
		
 		/*
		double vertReward = Math.pow(intervalsVertVelocity /2 - Math.abs(intervalsVertVelocity/2 - discVertVelocity),2);
		if (Math.abs(intervalsVertVelocity/2 - discVertVelocity) != 0) vertReward = vertReward / Math.pow((Math.abs(intervalsVertVelocity/2 - discVertVelocity)), 2);
		else{
 			vertReward = vertReward*2;
		}
		*/
		double reward = angleReward + horReward + vertReward;
		//System.out.println(vertReward);
		
		//double reward = angleReward * horReward * vertReward;
		return reward;
	}

	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than max, nrValues-1 is returned
	// otherwise a value between 1 and nrValues-2 is returned.
	//
	// Use discretize2() if you want a discretization method that does
	// not handle values lower than min and higher than max.
	// ///////////////////////////////////////////////////////////
	public static int discretize(double value, int nrValues, double min,
			double max) {
		if (nrValues < 2) {
			return 0;
		}

		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * (nrValues - 2)) + 1;
	}

	// ///////////////////////////////////////////////////////////
	// discretize2() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 0 and nrValues-1 is returned.
	// ///////////////////////////////////////////////////////////
	public static int discretize2(double value, int nrValues, double min,
			double max) {
		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * nrValues);
	}

}
