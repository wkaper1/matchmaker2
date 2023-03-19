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
@SuppressWarnings("rawtypes")
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
	 * Scheme for generating the combinations of variable values to tabulate, in case of 2 or more variables
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
	private boolean[] variableVisible;//for each var: include it in table or not
	private boolean showConstantVars; //shall we tabulate variables for which just 1 value was provided?
	//2D array, stating for each variable the array of values to use in tabulation
	//the type is Object[] because we use reflection, however each variable SHOULD have the type required by variableTypes
	//first dimension: variable index, same order as above; second dimension: value index; the array is ragged
	private Object[][] values;
	
	//optional feature: Transformation to perform on the function before tabulating it.
	private Transformation2 transformation; //we need the slower multi-parameter version here! 
	private int transformVariable;  //index of the single variable involved in the transformation
	
	/**
	 * Load a static method for tabulation.
	 * @throws Exception 
	 */
	public Tabulator(String classname, String methodname, int numvars, boolean showConstantVars) {
		this.cls = myReflection.getClass(classname);
		common(methodname, numvars, showConstantVars);
	}

	/**
	 * Load a static method for tabulation.
	 */
	public Tabulator(Class cls, String methodname, int numvars, boolean showConstantVars) {
		this.cls = cls;
		common(methodname, numvars, showConstantVars);
	}
	
	/**
	 * Load an instance method for tabulation.
	 * @throws Exception 
	 */
	public Tabulator(Object instance, String methodname, int numvars, boolean showConstantVars) {
		this.instance = instance;
		this.cls = instance.getClass();
		common(methodname, numvars, showConstantVars);
	}
	
	private void common(String methodname, int numvars, boolean showConstantVars) {
		this.methodname = methodname;
		this.variableNames = new String[numvars];
		this.variableTypes = new Class[numvars];
		this.variableVisible = new boolean[numvars];
		this.values = new Object[numvars][];
		this.transformation = null;
		this.showConstantVars = showConstantVars;
	}
	
	/**
	 * Declare an int variable, mentioning the set of values you want to tabulate for that variable, 
	 * and also the index position in the method invocation (0 = first)
	 * @throws Exception 
	 */
	public void declareVariableInt(int index, String name, int[] values) throws Exception {
		this.values[index] = new Object[values.length];
		for (int i=0; i<values.length; i++) {
			this.values[index][i] = (Object)values[i];
		}
		finishDeclare(index, name, int.class);
	}

	/**
	 * Declare a long variable, mentioning the set of values you want to tabulate for that variable, 
	 * and also the index position in the method invocation (0 = first)
	 * @throws Exception 
	 */
	public void declareVariableLong(int index, String name, long[] values) throws Exception {
		this.values[index] = new Object[values.length];
		for (int i=0; i<values.length; i++) {
			this.values[index][i] = (Object)values[i];
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
			this.values[index][i] = (Object)values[i];
		}
		finishDeclare(index, name, double.class);
	}
	
	private void finishDeclare(int index, String name, Class cls) throws Exception {
		variableNames[index] = name;
		variableTypes[index] = cls;
		variableVisible[index] = (showConstantVars || values[index].length > 1);
		if (values[index].length == 0) {
			throw new Exception("Variable '" + name + "' was declared with zero values, please provide at least one.");
		}
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
	 * Set a transformation to perform on the function before tabulating
	 */
	public void setTransformation(Transformation2 transformation, int transformVariable) {
		this.transformation = transformation;
		this.transformVariable = transformVariable;
		if (transformation.isConstant() && values[transformVariable].length > 1) {
			System.out.println("Warning: transformation is constant with respect to variable " + transformVariable + ", while multiple values were provided. Consider providing just 1 value, and not showing constant variables.");
			System.out.println();
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
			if (variableVisible[i]) {
				System.out.print(variableNames[i]);
				System.out.print(", ");				
			}
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
	
	private void tabulateOnePassPerVariable() throws Exception {
		Object[] variables = new Object[variableTypes.length];
		//Init the variables, including the invisible ones
		for (int i=1; i<variableTypes.length; i++) {
			int mid = (values[i].length - 1) / 2; //find the mid point (uneven) or just below (even)
			variables[i] = values[i][mid];
		}
		//Do the looping: do the single pass for each visible variable
		for (int i=0; i<variableTypes.length; i++) {
			if (variableVisible[i]) {
				//Do the single pass for variable i, visiting each of its values once
				for (int j=0; j<values[i].length; j++) {
					variables[i] = values[i][j];
					invokeAndPrintline(variables); //Do it!
				}
				//put variable i back to the mid position
				int mid = (values[i].length - 1) / 2;
				variables[i] = values[i][mid];				
			}
		}
	}
	
	private void tabulateAllCombinationsZigzag() throws Exception {
		//It is assumed that invisible variables are single valued
		//  under that assumption we don't need to do anything special here, Cartesian product takes care of itself
		Object[] variables = new Object[variableTypes.length];
		int[] indices = new int[variableTypes.length];
		Arrays.fill(indices, 0);
		//Init the variables
		for (int i=0; i<variableTypes.length; i++) {
			variables[i] = values[i][0];
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
				if (indices[varIndex] < values[varIndex].length) {
					variables[varIndex] = values[varIndex][indices[varIndex]];
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
							variables[i] = values[i][0];
						}
					}
					else {
						atEnd = true; //there are no more variable indices to increase
					}
				}
			}
		}
	}

	private void invokeAndPrintline(Object[] args) throws Exception {
		double functionValue;
		if (transformation == null) {
			if (instance != null) {
				functionValue = (double)myReflection.invokeInstanceMethod(instance, method, args);
			}
			else {
				functionValue = (double)myReflection.invokeStaticMethod(method, args);
			}			
		}
		else {
			functionValue = invokeWithTransformation(args);
		}
		
		//print line
		for (int i=0; i<args.length; i++) {
			if (variableVisible[i]) {
				System.out.print(args[i]);
				System.out.print(", ");				
			}
		}
		System.out.println(functionValue);
	}
	
	private double invokeWithTransformation(Object[] args) throws Exception {
		//Mask the method to tabulate as a single-variable function, using (temporarily) fixed values
		SingleParameterMask fx = new SingleParameterMask(this.method, this.variableTypes);
		fx.setCurrentVariable(transformVariable);
		fx.setValues(args);
		//Provide the single variable function to the Transformation, let it return a value
		return transformation.run(fx, (double)args[transformVariable]);
	}
}
