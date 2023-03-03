package nl.uva.science.esc.math.integration;

import java.util.function.DoubleUnaryOperator;

import nl.uva.science.esc.math.integration.Integrator.method;
import nl.uva.science.esc.statistics.distributionsequal.StudentizedRangeDistribution;

public class TestIntegrator {

	/**
	 * Test the Integrator with both subclasses, applied to the "inner integral" from the
	 * Studentized Range Distribution.
	 * Tune the number of intervals for both methods, find the methods that needs the least
	 * intervals for a desired accuracy.
	 */
	public static void main(String[] args) {
		DoubleUnaryOperator function = 
				(x) -> StudentizedRangeDistribution.CumulativeProbabilityNormalDistribution(x);
		Integrator int1 = Integrator.Create(method.TRAPEZOIDAL_RULE);
		Integrator int2 = Integrator.Create();  //should be Simpson's
		double points[] = new double[] {-20, -2, -0.5, -0.25, -0.1, 0, 0.1, 0.25, 0.5, 1, 2, 20};
		
		//Evaluate the function to integrate to check the curve
		System.out.println("x, P(x)");
		for(int i=0; i<points.length; i++) {
			double Px = function.applyAsDouble(points[i]);
			System.out.print(points[i]);
			System.out.print(", ");
			System.out.println(Px);
		}
		System.out.println();
		
		//Quick evaluation of the integral for all of the chosen points to check the curve
		System.out.println("x, Integral(P(x), a, b)");
		for(int i=0; i<points.length; i++) {
			double approx = int2.integrate(function, -100, points[i], 320);
			System.out.print(points[i]);
			System.out.print(", ");
			System.out.println(approx);			
		}
		System.out.println();
		
		//Try the tuning on a chosen point for both methods
	}
}
