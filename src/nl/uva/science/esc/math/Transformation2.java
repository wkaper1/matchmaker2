package nl.uva.science.esc.math;

import java.lang.reflect.Method;

/**
 * A transformation works on a function to produce another function.
 * Example F(x') = Integral from 0 to x' of f(x)dx works on function f to produce F.
 * Or generally: T(x, y, z, ...) : f(x, y, z, ...) --> g(x, y, z, ...)
 * 
 * This version takes a function of many variables and produces of function of the
 * same (or less) variables: the output function can be constant with respect to of the variables.
 * The transformation itself can also use some of the same variables (e.g. for deciding integration boundaries). 
 * @author Wolter2
 */
@SuppressWarnings("rawtypes")
public interface Transformation2 {

	/**
	 * Get a function value for the transformed function g, given f and (x, y, z,..)
	 * @param method, the function to transform
	 * @param args, array of function and transformation arguments (both take what they want)
	 */
	public Object run(Method method, Object[] args);

	/**
	 * Get a function value for the transformed function g, given f and (x, y, z,..)
	 * Overload to provide the argument types if you have them, so the receiver does not need to hunt for them 
	 * @param method, the function to transform
	 * @param args, array of function and transformation arguments (both take what they want)
	 * @param argTypes, Class objects describing the argument types
	 */
	public Object run(Method method, Object[] args, Class[] argTypes);
	
	/**
	 * Does the result of this Transformation depend on x, y, z, ...?
	 * @return an array of same length as the expected args array
	 */
	public boolean[] isConstant();
}
