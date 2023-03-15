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

	private Class cls;              //the class that contains the method to tabulate
	private String methodname;      //the name of the method to tabulate... needed till we find the Method itself
	private Method method;          //the method to tabulate, as found by reflection, not yet attached to an instance
	private Object instance;        //the object instance we want to invoke the method for (ignored for static methods)
	private String[] variableNames; //variable names in order of appearance in method invocation
	private Class[] variableTypes;  //corresponding types, same order as the names
	//3 arrays, together stating for each variable the array of values to use in tabulation
	//first dimension: variable index, same order as above; second dimension: value index; these arrays are ragged
	private int[][] valuesInt;
	private long[][] valuesLong;
	private double[][] valuesDouble;

	/**
	 * Load a static method for tabulation.
	 * @throws Exception 
	 */
	public Tabulator(String classname, String methodname, int numvars) {
		this.cls = myReflection.getClass(classname);
		this.methodname = methodname;
		common(numvars);
	}
	
	/**
	 * Load an instance method for tabulation.
	 * @throws Exception 
	 */
	public Tabulator(Object instance, String methodname, int numvars) {
		this.instance = instance;
		this.cls = instance.getClass();
		this.methodname = methodname;
		common(numvars);
	}
	
	private void common(int numvars) {
		this.variableNames = new String[numvars];
		this.variableTypes = new Class[numvars];
		this.valuesInt = new int[numvars][];
		this.valuesLong = new long[numvars][];
		this.valuesDouble = new double[numvars][];
	}
	
	/**
	 * Declare an int variable, mentioning the set of values you want to tabulate for that variable, 
	 * and also the index position in the method invocation (0 = first)
	 * @throws Exception 
	 */
	public void declareVariableInt(int index, String name, int[] values) throws Exception {
		valuesInt[index] = values;
		finishDeclare(index, name, int.class);
	}

	/**
	 * Declare a long variable, mentioning the set of values you want to tabulate for that variable, 
	 * and also the index position in the method invocation (0 = first)
	 * @throws Exception 
	 */
	public void declareVariableLong(int index, String name, long[] values) throws Exception {
		valuesLong[index] = values;
		finishDeclare(index, name, long.class);
	}

	/**
	 * Declare a double variable, mentioning the set of values you want to tabulate for that variable.
	 * and also the index position in the method invocation (0 = first)
	 * @throws Exception 
	 */
	public void declareVariableDouble(int index, String name, double[] values) throws Exception {
		valuesDouble[index] = values;
		finishDeclare(index, name, double.class);
	}
	
	private void finishDeclare(int index, String name, Class cls) throws Exception {
		variableNames[index] = name;
		variableTypes[index] = cls;
	}
	
	private void checkDeclare() throws Exception {
		for (int i=0; i<variableNames.length; i++) {
			if (! (variableNames[i].length() > 0)) {
				throw new Exception("The variables array has a gap at position " + i);
			}
		}
		this.method = myReflection.getMethodFromClass(cls, methodname, variableTypes);
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
			System.out.print(", ");
		}
		System.out.println("function value");
		switch (scheme) {
		case ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT: 
			tabulateOnePassPerVariable();
			break;
		case ALL_COMBINATIONS_ZIGZAG: 
			tabulateAllCombinationsZigzag();
		}
		System.out.println(); //white line to signal end-of-table
	}
	
	private void tabulateOnePassPerVariable() {
		Object[] variables = new Object[variableTypes.length];
		//Init the variables
		for (int i=1; i<variableTypes.length; i++) {
			int mid = (getVariableValuesLength(i) - 1) / 2; //find the mid point (uneven) or just below (even)
			variables[i] = getVariableValue(i, mid);
		}
		//Do the looping
		for (int i=0; i<variableTypes.length; i++) {
			//Do the single pass for variable i, visiting each of its values once
			for (int j=0; j<getVariableValuesLength(i); j++) {
				variables[i] = getVariableValue(i, j);
				invokeAndPrintline(variables); //Do it!
			}
			//put variable i back to the mid position
			int mid = (getVariableValuesLength(i) - 1) / 2;
			variables[i] = getVariableValue(i, mid);
		}
	}
	
	private void tabulateAllCombinationsZigzag() {
		Object[] variables = new Object[variableTypes.length];
		int[] indices = new int[variableTypes.length];
		Arrays.fill(indices, 0);
		//Init the variables
		for (int i=0; i<variableTypes.length; i++) {
			variables[i] = getVariableValue(i, 0);
		}
		//Do the looping
		boolean atEnd = false;
		while (!atEnd) {
			invokeAndPrintline(variables); //Do it!
			//get the next combination of indices and variable values
			boolean next = false;
			int varIndex = 0;
			while (!next && !atEnd) {
				indices[varIndex]++;
				if (indices[varIndex] < getVariableValuesLength(varIndex)) {
					variables[varIndex] = getVariableValue(varIndex, indices[varIndex]);
					next = true;
				}
				else {
					next = false;
					//Try choose the next variables index for increasing
					varIndex++;
					if (varIndex < variableTypes.length) {
						//success: reset all lower numbered variable indices
						for (int i=0; i<varIndex; i++) {
							indices[i] = 0;
							variables[i] = getVariableValue(i, 0);
						}
					}
					else {
						atEnd = true; //there are no more variable indices to increase
					}
				}
			}
		}
	}

	private Object getVariableValue(int variableIndex, int valueIndex) {
		if (variableTypes[variableIndex] == int.class) {
			return (Object) valuesInt[variableIndex][valueIndex];
		}
		else if (variableTypes[variableIndex] == long.class)	{
			return (Object)valuesLong[variableIndex][valueIndex];
		}
		else {
			return (Object)valuesDouble[variableIndex][valueIndex];
		}
	}
	
	private int getVariableValuesLength(int variableIndex) {
		if (variableTypes[variableIndex] == int.class) {
			return valuesInt[variableIndex].length;
		}
		else if (variableTypes[variableIndex] == long.class)	{
			return valuesLong[variableIndex].length;
		}
		else {
			return valuesDouble[variableIndex].length;
		}
	}
	
	private void invokeAndPrintline(Object[] args) {
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
