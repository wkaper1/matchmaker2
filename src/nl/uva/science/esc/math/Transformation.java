package nl.uva.science.esc.math;

import java.util.function.DoubleUnaryOperator;

/**
 * A transformation works on a function to produce another function.
 * Example F(x') = Integral from 0 to x' of f(x)dx works on function f to produce F.
 * 
 * This is currently limited to transformations of functions of 1 variable.
 * The single variable must be a double and the output will also be a double.
 * @author Wolter2
 */
public interface Transformation {

	/**
	 * Get a return value for the transformed function F, given f and x'
	 * @param f, the function of one variable to transform
	 * @param x, the value of x for which to evaluate the big F
	 */
	public double run(DoubleUnaryOperator f, double x);
	
	/**
	 * Does the result of this Transformation depend on x?
	 * It could depend solely on the function but not on x if the Transformation is e.g. a
	 * definite integral from minus to plus infinity.
	 * @return
	 */
	public boolean isConstant();
}
