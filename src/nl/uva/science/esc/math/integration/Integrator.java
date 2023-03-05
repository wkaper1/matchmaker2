package nl.uva.science.esc.math.integration;

import java.util.function.DoubleUnaryOperator;

import nl.uva.science.esc.math.Tabulator;

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
	
	public enum boundary { LOWER, UPPER }
	
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
	 * Factory: returns an Integrator that implements the application-wide default
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
		System.out.println("The function to integrate:");
		Tabulator.tabulate(integrand, points);
		
		DoubleUnaryOperator integral = (varyBoundary == boundary.LOWER) ?
				(lower) -> integrate(integrand, lower, otherBoundary, numIntervals) :
				(upper) -> integrate(integrand, otherBoundary, upper, numIntervals) ;
		System.out.println("The integral as a function of the " + varyBoundary + " boundary.");
		Tabulator.tabulate(integral, points);		
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
	
	//Tool for tuning improper integrals. i.e. one or both boundaries are at infinity in principle.
	
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
		double approx = 999999;  //nonsensical first value, but supposedly larger than criterion
		double lowerBound;
		double upperBound;
		if (verbose) {
			System.out.println("lowerbound, upperbound, approximation");
		}
		if (whichSide == boundary.LOWER) {
			lowerBound = safeValue;
			upperBound = initialValue;
		}
		else {
			lowerBound = initialValue;
			upperBound = safeValue;
		}
		while (approx > criterion) {
			approx = integrate(function, lowerBound, upperBound, numIntervals);
			if (verbose) {
				System.out.print(lowerBound);
				System.out.print(", ");
				System.out.print(upperBound);
				System.out.print(", ");
				System.out.println(approx);
			}
			//prepare next iteration
			if (whichSide == boundary.LOWER) {
				upperBound -= stepSize;
			}
			else {
				lowerBound += stepSize;
			}
		}
		if (verbose) {
			System.out.println();
		}
		double variedBound = (whichSide == boundary.LOWER) ? upperBound : lowerBound;
		return variedBound;
	}
	
	//For testing limiting behavior on either side

	/**
	 * At one side the boundary is held fixed, at the other side the boundary is changed in outward
	 * direction. The integral is believed to approach an asymptote that is known as an exact formula.
	 * We want to find the point where the integral is within criterion distance from the asymptote.
	 * Use it e.g. to avoid calculating the integral, replacing it with the asymptotic value, when appropriate.
	 * @param function, the function to integrate
	 * @param numIntervals, the number on intervals for the integral approximation
	 * @param asymptote, the function that's being approached on one of the sides
	 * @param whichSide, at which side is the asymptote believed to be approached? lower or upper!
	 * @param initialValue, initial value for the boundary that's on the side where the asymptote is expected
	 * @param stepSize, size of the steps for changing the outward-moving boundary, toward the side where the asymptote is expected
	 * @param otherBoundary, fixed value for the boundary at the other side, where we're not looking for the asymptote
	 * @param criterion, how small do we want the difference between integral and asymptote to be?
	 * @param verbose, print a table showing each step
	 * @return the most inward point of the moving boundary where the integral is close enough, given the steps series
	 */
	public double asymptoteReached(DoubleUnaryOperator function, int numIntervals, 
			DoubleUnaryOperator asymptote, boundary whichSide, double initialValue, double stepSize,
			double otherBoundary, double criterion, boolean verbose
			) {
		double diff = 999999;  //nonsensical first value, but supposedly larger than criterion
		double lowerBound;
		double upperBound;
		if (verbose) {
			System.out.println("lowerbound, upperbound, integral, asymptote, difference");
		}
		if (whichSide == boundary.LOWER) {
			lowerBound = initialValue;
			upperBound = otherBoundary;
		}
		else {
			lowerBound = otherBoundary;
			upperBound = initialValue;
		}
		while (diff > criterion) {
			double approx = integrate(function, lowerBound, upperBound, numIntervals);
			double asympt = (whichSide == boundary.LOWER) ? 
					asymptote.applyAsDouble(lowerBound) :
					asymptote.applyAsDouble(upperBound);
			diff = Math.abs(approx - asympt);
			if (verbose) {
				System.out.print(lowerBound);
				System.out.print(", ");
				System.out.print(upperBound);
				System.out.print(", ");
				System.out.print(approx);
				System.out.print(", ");
				System.out.print(asympt);
				System.out.print(", ");
				System.out.println(diff);
			}
			//prepare next iteration
			if (whichSide == boundary.LOWER) {
				lowerBound -= stepSize;
			}
			else {
				upperBound += stepSize;
			}
		}
		if (verbose) {
			System.out.println();
		}
		double variedBound = (whichSide == boundary.LOWER) ? lowerBound : upperBound;
		return variedBound;
	}
}
