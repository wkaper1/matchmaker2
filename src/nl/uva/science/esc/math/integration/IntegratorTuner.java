package nl.uva.science.esc.math.integration;

import java.lang.reflect.Method;

import nl.uva.science.esc.math.SingleParameterMask;
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
@SuppressWarnings("rawtypes")
public abstract class IntegratorTuner extends Transformation2 {
	protected IntegratorMultiTunable int1;
	protected int integrationVarIndex;

	/**
	 * Constructor
	 * @param argTypes, Class[] array describing the argument types of the integrand whose integral we are tuning
	 * @param typeChecking, should the arguments be typeChecked at each call of the run method?
	 * @param int1, the Integrator instance that will do the integrations
	 */
	public IntegratorTuner(Class[] argTypes, int integrationVarIndex, boolean typeChecking, IntegratorMultiTunable int1) {
		super(argTypes, typeChecking);
		this.int1 = int1;
		this.integrationVarIndex = integrationVarIndex;
	}

	/**
	 * Common pre-processing before a run.
	 * All 3 IntegratorTuners need a single-variable "masked" version of the integrand.
	 * And, we can just as well include the optional call to the typeCheck here.
	 */
	protected SingleParameterMask preRun(Method method, Object[] args) throws Exception {
		if (typeChecking) {
			typeCheckProtected(args);
		}
		SingleParameterMask f = new SingleParameterMask(method, argTypes, integrationVarIndex);
		f.setValues(args);
		return f;
	}
}
