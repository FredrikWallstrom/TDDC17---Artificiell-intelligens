public class StateAndReward {

	private static final int intervalsAngle = 20;
	private static final int intervalsHorVelocity = 10;
	private static final int intervalsVertVelocity = 10;
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
		if (Math.abs(intervalsAngle/2 - discAngle) != 0) reward = reward / Math.abs(intervalsAngle/2 - discAngle);
		//System.out.println(reward);
		return reward;
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		int discAngle = discretize2(angle, intervalsAngle, -3, 3);
		int discHorVelocity = discretize(vx, intervalsHorVelocity, -10, 10);
		int discVertVelocity = discretize(vy, intervalsVertVelocity, -6, 6);
		String state = String.valueOf(discAngle) + String.valueOf(discHorVelocity) + String.valueOf(discVertVelocity);
		//System.out.println(state);
		
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {
		int discAngle = discretize2(angle, intervalsAngle, -3, 3);
		int discHorVelocity = discretize(vx, intervalsHorVelocity, -10, 10);
		int discVertVelocity = discretize(vy, intervalsVertVelocity, -10, 10);
		
		double angleReward = intervalsAngle/2 - Math.abs(intervalsAngle/2 - discAngle);
		if (Math.abs(intervalsAngle/2 - discAngle) != 0) angleReward = angleReward / Math.abs(intervalsAngle/2 - discAngle);
		
		double horReward = intervalsHorVelocity /2 - Math.abs(intervalsHorVelocity/2 - discHorVelocity);
		if (Math.abs(intervalsHorVelocity/2 - discHorVelocity) != 0) horReward = horReward / Math.abs(intervalsHorVelocity/2 - discHorVelocity);
		
		
		//TODO problem med att den gasar för mkt 
		double vertReward = Math.pow(intervalsVertVelocity /2 - Math.abs(intervalsVertVelocity/2 - discVertVelocity),2);
		if (Math.abs(intervalsVertVelocity/2 - discVertVelocity) != 0) vertReward = vertReward / Math.pow((Math.abs(intervalsVertVelocity/2 - discVertVelocity)), 5);
		if (Math.abs(intervalsVertVelocity/2 - discVertVelocity) == 1) vertReward = vertReward / 9;
		//System.out.println(vertReward);
		
		double reward = angleReward * horReward * vertReward;
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
