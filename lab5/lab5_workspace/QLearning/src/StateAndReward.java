public class StateAndReward {

	private static final int angleStates = 10;
	private static final int horStates = 4;
	private static final int vertStates = 6; 
	private static final double MAX_ANGLE = 2; 
	private static final double MIN_ANGLE = -2; 
	private static final double MIN_VX = -1.5;			 
	private static final double MAX_VX = 1.5;			 
	private static final int MIN_VY = -3;
	private static final int MAX_VY = 3;
	
	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {

		int discAngle = discretize2(angle, angleStates, MIN_ANGLE, MAX_ANGLE);
		String state = String.valueOf(discAngle);
		//System.out.println(state);
		
		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
		int discAngle = discretize2(angle, angleStates, MIN_ANGLE, MAX_ANGLE);
		// GOAL FOR ANGLE IS 4
		double angleReward = 0;
		if(Math.abs(angle) <= MAX_ANGLE){
			angleReward = 1 - Math.abs(angle)/MAX_ANGLE;
		}
	
		//System.out.println(reward);
		return angleReward;
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		int discAngle = discretize(angle, angleStates, MIN_ANGLE, MAX_ANGLE);
		int discHorVelocity = discretize(vx, horStates, MIN_VX, MAX_VX);
		int discVertVelocity = discretize(vy, vertStates, MIN_VY, MAX_VY); 
		String state = "angle" + String.valueOf(discAngle) + "hor" + String.valueOf(discHorVelocity) + "vert" +  String.valueOf(discVertVelocity);
		//System.out.println(discAngle);
		
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {

		// GOAL FOR ANGLE IS 4
		double angleReward = 0;
		if(Math.abs(angle) <= MAX_ANGLE){
			angleReward = 1 - Math.abs(angle)/MAX_ANGLE;
		}
		 
		
		// GOAL FOR HORIZONTAL IS state 2
		double horReward = 0;
		if(Math.abs(vx) <= MAX_VX){
			horReward = 1 - Math.abs(vx)/MAX_VX ;
		}
		
 		// GOAL STATE FOR VERTICAL IS 3
		double vertReward = 0;
		if(Math.abs(vy) <= MAX_VY){
			vertReward = 1 - Math.abs(vy)/MAX_VY ;
		}
		
		double reward = Math.pow(angleReward,2) + Math.pow(horReward,2) + Math.pow(vertReward,2);
	//	System.out.println(angleReward);
		
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
