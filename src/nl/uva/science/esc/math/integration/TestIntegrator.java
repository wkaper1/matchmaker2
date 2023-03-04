package nl.uva.science.esc.math.integration;

import java.util.function.DoubleUnaryOperator;

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
		System.out.println("x, P(x)");
		for(int i=0; i<points.length; i++) {
			double Px = function.applyAsDouble(points[i]);
			System.out.print(points[i]);
			System.out.print(", ");
			System.out.println(Px);
		}
		System.out.println();
		
		System.out.println("Explore integral from minus infinity (=-100), numIntervals high (320)");
		System.out.println("x, Integral(P(x), a, b)");
		for(int i=0; i<points.length; i++) {
			double approx = int2.integrate(function, -100, points[i], 320);
			System.out.print(points[i]);
			System.out.print(", ");
			System.out.println(approx);			
		}
		System.out.println();
		
		System.out.println("Decisions: we're accurate in 7 decimals, so -6 to 6 is the interesting range.");
		System.out.println("Minus infinity lowerbound can be approximated by -6.");
		System.out.println("To the left of -6 the integral is indistinguishable from 0");
		System.out.println("To the right of +6 the integral is indistinguishable from the line f(x)=x");
		System.out.println();
		
		//Try the tuning on a chosen point for both methods
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
	}
}
