package nl.uva.science.esc.math;

import java.util.function.DoubleUnaryOperator;

/**
 * Tabulate functions of one or more variables, given as operators, static methods, instance methods
 * The values of the independent variable(s) to tabulate are given as double[] arrays
 * @author Wolter2
 */
public class Tabulator {
	
	/**
	 * Tabulate the given unary function at the given values of the independent variable.
	 * The function is given as a java "Operator", a DoubleUnaryOperator specifically.
	 * @param function, the function to tabulate
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

	/**
	 * Scheme for generating the combinations of variable values to tabulate
	 * @author Wolter2
	 */
	public enum VariationScheme {
		/**
		 * Do one pass for each of the variables while keeping the others at their midpoint 
		 * (if number of values is uneven) or close below the midpoint (if even)
		 */
		ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT,
		/**
		 * Generate all possible combinations in the systematic way known from (old fashioned) television screens
		 */
		ALL_COMBINATIONS_ZIGZAG
	}

	/**
	 * Load an instance method for tabulation.
	 * Next, call addVariable to declare each of the variables plus the values of that variable to tabulate.
	 */
	public Tabulator(Object obj, String methodname) {
		//TODO
	}
	
	/**
	 * Load a static method for tabulation
	 * Next, call addVariable to declare each of the variables plus the values of that variable to tabulate.
	 */
	public Tabulator(String classname, String methodname) {
		//TODO
	}
	
	/**
	 * Declare a variable, mentioning the set of values you want to tabulate for that variable.
	 * All variables that appear in the method should get declared.
	 */
	public void addVariable(String name, double[] values) {
		//TODO
	}

	/**
	 * Calculate and tabulate the combinations of variable values as dictated by the scheme and the given sets 
	 * of values for each variable. Tabulate the methods return value for each of these.
	 */
	public void tabulate(VariationScheme scheme) {
		//TODO
	}
}
