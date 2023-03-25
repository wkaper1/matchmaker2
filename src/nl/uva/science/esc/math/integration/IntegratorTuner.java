package nl.uva.science.esc.math.integration;

import java.lang.reflect.Method;

import nl.uva.science.esc.math.Transformation2;

/**
 * The three "Integrator" tuning methods are wrapped to implement the Transformation interface
 * and to allow the Tabulator to tabulate it, including extra parameters not involved
 * in the function to integrate.
 * The tuning method itself also needs some extra parameters that are hidden
 * from the tabulator. They are kept constant during the repeated Tabulator calls
 * by these wrapper classes.
 * 
 * We do multiple-parameter-tuning so we need to use the IntegratorMultiTunable version of the 
 * Integrator. This points to implementing Transformation2 rather than 1.
 * @author Wolter2
 */
public abstract class IntegratorTuner implements Transformation2 {
	protected IntegratorMultiTunable int1;
	
	public IntegratorTuner(IntegratorMultiTunable int1) {
		this.int1 = int1;
	}
	
	@SuppressWarnings("rawtypes")
	public Object run(Method method, Object[] args) {
		Class[] argTypes = new Class[args.length];
		for (int i=0; i<args.length; i++) {
			argTypes[i] = args[i].getClass();
		}
		return run(method, args, argTypes);
	}
}
