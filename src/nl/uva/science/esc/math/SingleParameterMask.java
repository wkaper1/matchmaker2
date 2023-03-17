package nl.uva.science.esc.math;

import java.lang.reflect.Method;
import nl.uva.science.esc.reflection.myReflection;

/**
 * Replaces java.util.function.DoubleUnaryOperator for a specific case: you have a method that accepts multiple
 * parameters, returns double, and to want masquerade it as a function of one (double) variable, for the
 * purpose of not burdening one-variable procedures with having to know all the parameters in order to make
 * the call. We replace all parameters except one (the "variable") by constant values, for a given period.
 * 
 * This method works using reflection!
 * java.util.function.DoubleUnaryOperator can serve the same purpose and is probably faster (and safer).
 * But it can decide what will be the variable and when to switch parameter values only at compile time
 * while for some purposes it gives much shorter code if you can decide those things at runtime.
 * Our use case at the moment is the writing of tuning scripts for numeric integrals... 
 * @author Wolter2
 */
@SuppressWarnings("rawtypes")
public class SingleParameterMask {
	private Method method;   //the method to tabulate, as found by reflection
	private Class[] parameterTypes; //in invocation order
	private Object[] values;  //values for all of the parameters, also the current dummy (value is ignored)
	private int variableIndex; //which of the parameters is currently THE variable?
	
	/**
	 * Describe the method we'll be masking: class name fully qualified, method name, parameter types
	 * given as an array of Class objects.
	 */
	public SingleParameterMask(String classname, String methodname, Class[] parameterTypes) {
		Class cls = myReflection.getClass(classname);
		this.method = myReflection.getMethodFromClass(cls, methodname, parameterTypes);
		this.parameterTypes = parameterTypes;
		this.values = new Object[parameterTypes.length];
		this.variableIndex = 0;
	}
	
	public SingleParameterMask(Method method, Class[] parameterTypes) {
		this.method = method;
		this.parameterTypes = parameterTypes;
		this.values = new Object[parameterTypes.length];
		this.variableIndex = 0;
	}
	
	/**
	 * State which of the parameters is currently acting as the single variable, by stating its index
	 * (in invocation order)
	 */
	public void setCurrentVariable(int index) {
		this.variableIndex = index;
	}
	
	/**
	 * Set values for all parameters at once. Type-checking should be done by the client who knows the types
	 * if all is well! Provide a value for the current variable as well, just to be sure.
	 */
	public void setValues(Object[] values) throws Exception {
		if (values.length == this.values.length) {
			this.values = values;
		}
		else
			throw new Exception("Expected parameters " + this.parameterTypes.length + ", received: " + values.length);
	}

	/**
	 * Set a value for the parameter at the given index position
	 */
	public void setValue(int index, int value) throws Exception {
		finishSetValue(index, value, int.class);
	}
	
	/**
	 * Set a value for the parameter at the given index position
	 */
	public void setValue(int index, long value) throws Exception {
		finishSetValue(index, value, long.class);
	}
	
	/**
	 * Set a value for the parameter at the given index position
	 */
	public void setValue(int index, double value) throws Exception {
		finishSetValue(index, value, double.class);
	}
	
	private void finishSetValue(int index, Object value, Class type) throws Exception {
		if (type == parameterTypes[index]) {
			this.values[index] = value;					
		}
		else
			throw new Exception("Expected type at index " + index + " is " + parameterTypes[index] + ", received: " + type);
	}

	/**
	 * The single-variable function that we pretend to be is this one.
	 * Methods that expect a single variable function: look here!
	 */
	public double applyAsDouble(double x) {
		Object[] values2 = this.values.clone();
		values2[variableIndex] = x;
		return (double)myReflection.invokeStaticMethod(method, values2);
	}
}
