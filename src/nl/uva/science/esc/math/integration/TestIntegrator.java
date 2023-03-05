package nl.uva.science.esc.math.integration;

import java.util.function.DoubleUnaryOperator;

import nl.uva.science.esc.math.integration.Integrator.boundary;
import nl.uva.science.esc.math.integration.Integrator.method;
import nl.uva.science.esc.statistics.distributionsequal.StudentizedRangeDistribution;

public class TestIntegrator {

	/**
	 * Test the Integrator with both subclasses, applied to the "inner integral" from the
	 * Studentized Range Distribution, from minus infinity up to x, variable upperbound.
	 * Tune the number of intervals for both methods, find the methods that needs the least
	 * intervals for a desired accuracy.
	 * 
	 * It's a test for the integrator. It's also an example of tuning a given integral.
	 */
	public static void main(String[] args) {
		DoubleUnaryOperator function = 
				(x) -> StudentizedRangeDistribution.CumulativeProbabilityNormalDistribution(x);
		Integrator int1 = Integrator.Create(method.TRAPEZOIDAL_RULE);
		Integrator int2 = Integrator.Create();  //should be Simpson's
		double points[] = new double[] {-10, -6, -5, -2, -0.5, -0.25, -0.1, 0, 0.1, 0.25, 0.5, 1, 2, 5, 10, 20};
		
		//Evaluate the function to integrate to check the curve
		System.out.println("The function to integrate: CumulativeProbabilityNormalDistribution");
		System.out.println("Explore integral from minus infinity (=-100), numIntervals high (320)");
		System.out.println();
		int2.tabulate(function, 320, points, boundary.UPPER, -100);
		
		System.out.println("We're accurate in 7 decimals, so it seems -5 to 5 is the interesting range.");
		System.out.println("To the left the integral seems near to zero.");
		System.out.println("To the tight, the line y = x appears as asymptote.");
		System.out.println("Let's first tune the number of intervals for a few points in the interesting range.");
		System.out.println();

		System.out.println("Tuning numIntervals for -0.5, Trapezoidal rule");
		int1.tuneIntervals(function, -6, -0.5, 8, 2, 1E-7, 2, true);
		System.out.println();
		
		System.out.println("Tuning numIntervals for -0.5, Simpson's rule");
		int2.tuneIntervals(function, -6, -0.5, 8, 2, 1E-7, 2, true);
		System.out.println();		

		System.out.println("Tuning numIntervals for +0.25, Trapezoidal rule");
		int1.tuneIntervals(function, -6, 0.25, 8, 2, 1E-7, 2, true);
		System.out.println();
		
		System.out.println("Tuning numIntervals for +0.25, Simpson's rule");
		int2.tuneIntervals(function, -6, 0.25, 8, 2, 1E-7, 2, true);
		System.out.println();
		
		System.out.println("Tuning numIntervals for +0.5, Simpson's rule");
		int2.tuneIntervals(function, -6, 0.5, 8, 2, 1E-7, 2, true);
		System.out.println();
		
		System.out.println("Tuning numIntervals for +0.1, Simpson's rule");
		int2.tuneIntervals(function, -6, 0.1, 8, 2, 1E-7, 2, true);
		System.out.println();		

		System.out.println("Decision: choose Simpson's with 256 intervals");
		System.out.println();		

		System.out.println("We want to integrate from minus infinity, how far to the left should we really start?");
		int2.vanishes(function, 256, boundary.LOWER, -100, -4.5, 0.5, 1E-7, true);

		System.out.println("At what point to the right can we safely replace the integral by the asymptote y = x?");
		int2.asymptoteReached(function, 256, (x) -> x, boundary.UPPER, 4.5, 0.5, -7, 1E-7, true);
		
		System.out.println("Decisions: we're accurate in 7 decimals, so -5 to 5 is the interesting range.");
		System.out.println("Minus infinity lowerbound can be approximated by -5.");
		System.out.println("To the left of -5 the integral is indistinguishable from 0");
		System.out.println("To the right of +5 the integral is indistinguishable from the line f(x)=x");
		System.out.println();
		
	}
}
