package nl.uva.science.esc.statistics.distributionsequal;

/**
 * Static functions involved in calculation the cumulative probability for the Studentized
 * Range Distribution as used in Tukey's Honestly Significant Difference test.
 * 
 * See: https://en.wikipedia.org/wiki/Studentized_range_distribution
 * And: Dunlap, W.P.; Powell, R.S.; Konnerth, T.K. (1977). "A FORTRAN IV function for calculating 
 * probabilities associated with the studentized range statistic". Behavior Research Methods 
 * & Instrumentation. 9 (4): 373–375. doi:10.3758/BF03202264
 * 
 * We follow Dunlap at al. in their description of 3 nested integrals which they call the inner,
 * the middle and the outer integral.
 * The inner integral is the cumulative probability for the normal distribution, which they '
 * approximate by a polynomial taken from Abramowith & Stegun (1964) Handbook of Mathematical 
 * Functions, 0931-932, formula's 26.2.2 and 26.2.17.
 * @author wkaper1
 */
public class StudentizedRangeDistribution {
	
	//Constants used in the "inner integral":
	public static final double b1 =  .319381530;
	public static final double b2 = -.356563782;
	public static final double b3 = 1.781477937;
	public static final double b4 =-1.821255978;
	public static final double b5 = 1.330274429;
	public static final double p  =  .2316419;
	public static final double sqrtTwoPi = Math.sqrt(2 * Math.PI);
	
	/**
	 * Calculates the cumulative probability for the normal distribution
	 * from minus infinity up to x.
	 * @param x, normalised variable for normal distribution
	 */
	public static double CumulativeProbabilityNormalDistribution(double x) {
		boolean positive = (x >= 0);
		//The given polynomial is only valid for positive x
		if (!positive) {
			x = -x;
		}
		double Z = Math.exp(-x * x /2) / sqrtTwoPi;
		double t = 1 / (1 + p * x);
		double Px = 1 - Z * (b1*t + b2*t*t + b3*t*t*t + b4*t*t*t*t + b5*t*t*t*t*t);
		//But we can find the negative part of the curve by symmetry!
		if (positive) {
			return Px;
		}
		else {
			return 1 - Px;
		}
	}
}
