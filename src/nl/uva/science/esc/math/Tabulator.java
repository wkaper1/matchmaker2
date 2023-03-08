package nl.uva.science.esc.math;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.DoubleUnaryOperator;
import nl.uva.science.esc.reflection.myReflection;

/**
 * Tabulate functions of one or more variables, given as operators, static methods, instance methods
 * The values of the independent variable(s) to tabulate are given as long[] or double[] arrays
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
	
	private Method method;          //the method to tabluate, as found by reflection, not yet attached to an instance
	private Object instance;        //the object instance we want to invoke the method for (ignored for static methods)
	private String[] variableNames; //variable names in order of appearance in method invocation
	private Class[] variableTypes;  //corresponding types, same order as the names
	//2 arays being phased out, together stating for each variable the array of values to use in tabulation
	//first dimension: variable index, same order as above; second dimension: value index; these arrays are ragged
	private long[][] valuesLong;
	private double[][] valuesDouble;
	//1 array, 2D, stating for each variable the array of values as Objects, each concealing a Long or a Double
	private Object[][] values;
	
	/**
	 * Load a static method for tabulation.
	 * @throws Exception 
	 */
	public Tabulator(String classname, String methodname) {
		this.method = myReflection.getMethod(classname, methodname);
		common();
	}
	
	/**
	 * Load an instance method for tabulation.
	 * @throws Exception 
	 */
	public Tabulator(Object instance, String methodname) {
		this.instance = instance;
		this.method = myReflection.getMethodFromClass(instance.getClass(), methodname);
		common();
	}
	
	private void common() {
		this.variableTypes = method.getParameterTypes();
		this.variableNames = new String[variableTypes.length];
		this.values = new Object[variableTypes.length][];
		//TODO: phase out next 2 if the above line is a success
		this.valuesLong = new long[variableTypes.length][];
		this.valuesDouble = new double[variableTypes.length][];
	}
	
	/**
	 * Declare a long variable, mentioning the set of values you want to tabulate for that variable, 
	 * and also the index position in the method invocation (0 = first)
	 * @throws Exception 
	 */
	public void declareVariableLong(int index, String name, long[] values) throws Exception {
		this.values[index] = new Object[values.length];
		for (int i=0; i<values.length; i++) {
			this.values[index][i] = values[i]; //implicit cast long to Object
		}
		finishDeclare(index, name, long.class);
	}

	/**
	 * Declare a double variable, mentioning the set of values you want to tabulate for that variable.
	 * and also the index position in the method invocation (0 = first)
	 * @throws Exception 
	 */
	public void declareVariableDouble(int index, String name, double[] values) throws Exception {
		this.values[index] = new Object[values.length];
		for (int i=0; i<values.length; i++) {
			this.values[index][i] = values[i]; //implicit cast double to Object
		}
		finishDeclare(index, name, double.class);
	}
	
	private void finishDeclare(int index, String name, Class cls) throws Exception {
		variableNames[index] = name;
		if (! variableTypes[index].equals(cls)) {
			throw new Exception("Type found by reflection for index " + index + " is not " + cls.getName());
		}
	}
	
	private void checkDeclare() throws Exception {
		for (int i=0; i<variableNames.length; i++) {
			if (! (variableNames[i].length() > 0)) {
				throw new Exception("Not all parameters found by reflection were declared. Missing declaration for index " + i);
			}
		}
	}

	/**
	 * Calculate and tabulate the combinations of variable values as dictated by the scheme and the given sets 
	 * of values for each variable. Tabulate the methods return value for each of these.
	 * @throws Exception 
	 */
	public void tabulate(VariationScheme scheme) throws Exception {
		checkDeclare();
		//print table headings
		for (int i=0; i<variableNames.length; i++) {
			System.out.print(variableNames[i]);
		}
		System.out.println("function value");
		switch (scheme) {
		case ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT: 
			tabulateOnePassPerVariable();
			break;
		case ALL_COMBINATIONS_ZIGZAG: 
			tabulateAllCombinationsZigzag();
		}
	}
	
	private void tabulateOnePassPerVariable() {
		//Object[] variables = new Object[variableTypes.length]; //TODO: get it directly form values, given the indices!
		int indices[] = new int[variableTypes.length];
		//Init the variables
		for (int i=1; i<variableTypes.length; i++) {
			indices[i] = (getVariableValuesLength(i) - 1) / 2; //find the mid point (uneven) or just below (even)
			//variables[i] = getVariableValue(i, mid);
		}
		//Do the looping
		for (int i=0; i<variableTypes.length; i++) {
			//Do the single pass for variable i, visiting each of its values once
			for (int j=0; j<getVariableValuesLength(i); j++) {
				//variables[i] = getVariableValue(i, j);
				invokeAndPrintline(indices); //Do it!
			}
			//put variable i back to the mid position
			indices[i] = (getVariableValuesLength(i) - 1) / 2;
			//variables[i] = getVariableValue(i, mid);
		}
	}
	
	private void tabulateAllCombinationsZigzag() {
		//Object[] variables = new Object[variableTypes.length]; -- This function is not yet adapted to the new philosophy
		int[] indices = new int[variableTypes.length];
		Arrays.fill(indices, 0);
		//Init the variables
		for (int i=0; i<variableTypes.length; i++) {
			//variables[i] = getVariableValue(i, 0);
		}
		//Do the looping
		boolean atEnd = false;
		while (!atEnd) {
			invokeAndPrintline(variables); //Do it!
			//get the next combination of indices and variable values
			boolean next = false;
			int varIndex = 0;
			while (!next) {
				if (indices[varIndex] < getVariableValuesLength(varIndex)) {
					indices[varIndex]++;
					//variables[varIndex] = getVariableValue(varIndex, indices[varIndex]);
					next = true;
				}
				else {
					next = false;
					if (varIndex < variableTypes.length) {
						//choose the next variables index for increasing, reset all lower numbered ones
						varIndex++;
						for (int i=0; i<varIndex; i++) {
							indices[i] = 0;
							//variables[i] = getVariableValue(i, 0);
						}
					}
					else {
						atEnd = true;
					}
				}
			}			
		}
	}

//	private Object getVariableValue(int variableIndex, int valueIndex) {
//		if (variableTypes[variableIndex] == long.class)	{
//			return (Object)valuesLong[variableIndex][valueIndex];
//		}
//		else {
//			return (Object)valuesDouble[variableIndex][valueIndex];
//		}
//	}
	
//	private int getVariableValuesLength(int variableIndex) {
//		if (variableTypes[variableIndex] == long.class)	{
//			return valuesLong[variableIndex].length;
//		}
//		else {
//			return valuesDouble[variableIndex].length;
//		}
//	}
	
	private int getVariableValuesLength(int variableIndex) {
		return values[variableIndex].length;  //TODO: replace calls to this function, then delete function
	}
	
	/**
	 * Invoke the method to tabulate, using for each variable the value pointed to
	 * by the array of valueIndices (variableIndex as key).
	 * Print a line showing the variable values used, as wel as the function value.
	 */
	private void invokeAndPrintline(int[] valueIndices) {
		Object[] args = new Object[valueIndices.length];
		for (int i=0; i<args.length; i++) {
			args[i] = values[i][valueIndices[i]];
		}
		double functionValue;
		if (instance != null) {
			functionValue = (double)myReflection.invokeInstanceMethod(instance, method, args);
		}
		else {
			functionValue = (double)myReflection.invokeStaticMethod(method, args);
		}
		for (int i=0; i<args.length; i++) {
			System.out.print(args[i]);
			System.out.print(", ");
		}
		System.out.println(functionValue);
	}
}
