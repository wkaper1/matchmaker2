package nl.uva.science.esc.math;

import java.lang.reflect.Method;

/**
 * A transformation works on a function to produce another function.
 * Example F(x') = Integral from 0 to x' of f(x)dx works on function f to produce F.
 * Or generally: T(x, y, z, ...) : f(x, y, z, ...) --> g(x, y, z, ...)
 * 
 * This version takes a function of many variables and produces a function of the same (or less) variables: 
 * the output function can be constant with respect to some of the variables.
 * The transformation itself can also use these same variables (e.g. for deciding integration boundaries). 
 * @author Wolter2
 */
@SuppressWarnings("rawtypes")
public abstract class Transformation2 {
	protected Class[] argTypes;
	protected boolean typeChecking;
	protected boolean[] isConstant;
	
	public Transformation2(Class[] argTypes, boolean typeChecking) {
		this.argTypes = argTypes;
		this.typeChecking = typeChecking;
		this.isConstant = new boolean[argTypes.length]; //default value false, can be changed by subclasses
	}

	/**
	 * Get a function value for the transformed function g, given f and (x, y, z,..)
	 * @param method, the function to transform
	 * @param args, array of function and transformation arguments (both take what they want)
	 * @throws Exception 
	 */
	public abstract Object run(Method method, Object[] args) throws Exception;
	
	/**
	 * Is the result of this Transformation constant with respect to x, y, z, ...?
	 * @return an array of same length as the expected args array
	 */
	public boolean[] isConstant() {
		return isConstant;
	}
	
	/**
	 * Subclasses can ask the superclass to check the full args array received against the known argTypes.
	 * They should check the typeChecking boolean property before asking!
	 */
	protected void typeCheckProtected(Object[] args) throws Exception {
		if (args.length != argTypes.length) {
			throw new Exception(this.getClass().getSimpleName() + ".run received " + args.length + " arguments, expected: " + argTypes.length);
		}
		for (int i=0; i<argTypes.length; i++) {
			if (! args[i].getClass().equals(argTypes[i])) {
				throw new Exception(this.getClass().getSimpleName() + ".run received " + args[i].getClass().getSimpleName()
						+ " as argument " + i + " while expecting " + argTypes[i].getSimpleName());
			}
		}
	}
	
	/**
	 * A receiver of a fresh Transformation instance could have ideas about the types of arguments it wants
	 * to send and can check beforehand if they will be welcome.
	 */
	public void typeCheck(Class[] argTypes) throws Exception {
		if (argTypes.length != this.argTypes.length) {
			throw new Exception("Number of arguments in type array unequal, received " + argTypes.length + ", expected: " + this.argTypes.length + " by this " + this.getClass().getSimpleName());
		}
		for (int i=0; i<argTypes.length; i++) {
			if (! argTypes[i].equals(this.argTypes[i])) {
				throw new Exception("Unexpected type " + argTypes[i].getSimpleName()
						+ " as argument " + i + " while " + this.argTypes[i].getSimpleName() + " is expected by this " + this.getClass().getSimpleName());
			}
		}
	}

	/**
	 * Return the argument types (call signature) of the functions that this Transformation accepts 
	 */
	public Class[] getArgTypes() {
		return this.getArgTypes();
	}
}
