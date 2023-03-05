package nl.uva.science.esc.math;

import java.util.function.DoubleUnaryOperator;

public class Tabulator {
	
	/**
	 * Tabulate the given function at the given values of the independent variable
	 * @param function, the function top tabulate
	 * @param points, an array of values of the independent variable, points on the x-axis
	 */
	public static void tabulate(DoubleUnaryOperator function, double[] points) {
		System.out.println("x, f(x)");
		for(int i=0; i<points.length; i++) {
			double fx = function.applyAsDouble(points[i]);
			System.out.print(points[i]);
			System.out.print(", ");
			System.out.println(fx);
		}
		System.out.println();
	}

}
