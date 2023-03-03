package nl.uva.science.esc.math.integration;

import java.util.function.DoubleUnaryOperator;

/**
 * Ability to calculate numerical approximations of definite integrals, given the function
 * to integrate, the two boundaries and the number of (equal) sub-intervals to use in the approximation.
 * 
 * Subclasses exist for various methods of numerical integration.
 * @author Wolter2
 *
 */
public abstract class Integrator {
	
	/**
	 * Calculate one definite integral
	 */
	public abstract double integrate(DoubleUnaryOperator function, double lowerbound, double upperbound, int numIntervals);

	/**
	 * Find the number of intervals that satisfies the given convergence criterion, by successively
	 * doubling (tripling etc.) the number of intervals, until the difference falls below the given
	 * criterion (option: for two, or three, ... etc. times in a row).
	 * @return the last used number of intervals
	 */
	public int tuneIntervals(
			DoubleUnaryOperator function, double lowerbound, double upperbound, 
			int initialIntervals, int growFactor, double convergenceCriterion, int convergenceRepetitions,
			boolean verbose
	) {
		int converged = 0; //how many times in a row did we see the convergence criterion met?
		int intervals = initialIntervals;
		double prevApprox = -99999;   //just a strange unlikely value
		if (verbose) { //print headers for table
			System.out.println("intervals, apptoximation, difference, converged");
		}
		while (converged < convergenceRepetitions) {
			double approx = integrate(function, lowerbound, upperbound, intervals);
			double diff = Math.abs(approx - prevApprox);
			if (diff < convergenceCriterion) {
				converged++;
			}
			else {
				converged = 0;
			}
			if (verbose) {
				//print a line showing current approximation
				System.out.print(intervals); 
				System.out.print(", "); 
				System.out.print(approx);
				System.out.print(", "); 
				System.out.print(approx - prevApprox); 
				System.out.print(", "); 
				System.out.print(converged);
				System.out.println();
			}
			intervals = intervals * growFactor;
		}
		return intervals;
	}	
}
