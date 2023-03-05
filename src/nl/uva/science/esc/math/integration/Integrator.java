package nl.uva.science.esc.math.integration;

import java.util.function.DoubleUnaryOperator;

/**
 * Ability to calculate numerical approximations of definite integrals, given the function
 * to integrate, the two boundaries and the number of (equal) sub-intervals to use in the approximation.
 * 
 * Subclasses exist for various methods of numerical integration.
 * Tools to test and tune the integration method are added.
 * @author Wolter2
 *
 */
public abstract class Integrator {
	
	public enum method {
		TRAPEZOIDAL_RULE,
		SIMPSONS_RULE
	}
	
	public enum boundary { LEFT, RIGHT }
	
	public static final method defaultMethod = method.SIMPSONS_RULE;
	
	protected Integrator() {
	}
	
	/**
	 * Calculate one definite integral
	 */
	public abstract double integrate(DoubleUnaryOperator function, double lowerbound, double upperbound, int numIntervals);

	/**
	 * Factory: returns an Integrator that implements the chosen method
	 */
	public static Integrator Create(method method1) {
		switch (method1) {
			case TRAPEZOIDAL_RULE: return new TrapezoidalRuleIntegrator();
			case SIMPSONS_RULE: return new SimpsonsRuleIntegrator();
			default: return null;
		}
	}
	
	/**
	 * Factory: returns an integrator that implements the application-wide default
	 */
	public static Integrator Create() {
		return Create(defaultMethod);
	}

	/**
	 * Tabulate the integrand as well as the integral at given points
	 * @param integrand, the integrand function
	 * @param numIntervals, the number on intervals for the integral approximation
	 * @param points, array of values of the independent variable
	 * @param varyBoundary, which boundary of the integral to vary?
	 * @param otherBoundary, fixed value for the other boundary
	 */
	public void tabulate(DoubleUnaryOperator integrand, int numIntervals, 
			double[] points, boundary varyBoundary, double otherBoundary) {
		//TODO
	}

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
			//prepare next iteration
			intervals = intervals * growFactor;
			prevApprox = approx;
		}
		return intervals;
	}
	
	//Tools for tuning improper integrals. i.e. one or both boundaries are at infinity in principle.
	
	/**
	 * Tuning: find the point that's closest enough to infinity for the integral to vanish within a
	 * given accuracy. We want the point "just" close enough, to avoid future wasting of calculation 
	 * time on intervals where nothings is happening. So this procedure is deliberately wasteful to 
	 * avoid future waste!
	 * @param function, the function to integrate
	 * @param numIntervals, the number on intervals for the integral approximation
	 * @param whichSide, at which side are we investigating the vanish point?
	 * @param safeValue,  a value for the boundary that's surely far enough to the left or right
	 * @param initialValue, initial, surely UN-safe value for the boundary
	 * @param stepSize, absolute value of the desired step size, going from safe to unsafe
	 * @param criterion, difference with asymptote that's close enough to zero
	 * @param verbose, do we want a table of attempts printed? 
	 * @return first point where criterion was met, going from unsafe to safe with given step size
	 */
	public double vanishes(DoubleUnaryOperator function, int numIntervals, 
			boundary whichSide, double safeValue, double initialValue, double stepSize, double criterion,
			boolean verbose) {
		//TODO
		return 0;
	}
	
	//Tools for testing limiting behavior on either side
	
	public double asymptoteReached(DoubleUnaryOperator function, int numIntervals, 
			DoubleUnaryOperator asymptote, boundary whichSide, double initialValue, double stepSize,
			double otherBoundary, double criterion, boolean verbose
			) {
		//TODO
		return 0;
	}
}
