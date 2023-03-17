package nl.uva.science.esc.math.integration;

import nl.uva.science.esc.math.Transformation;

/**
 * The three Integrator tuning methods wrapped to implement Transformation
 * and allow the Tabulator to tabulate it, including extra parameters not involved
 * in the function to integrate.
 * The tuning method itself also needs some extra parameters that are hidden
 * from the tabulator. They are kept constant during the repeated Tabulater calls
 * by these wrapper classes.
 * @author Wolter2
 */
public abstract class IntegratorTuner implements Transformation {
	protected Integrator int1;
	
	public IntegratorTuner(Integrator int1) {
		this.int1 = int1;
	}
}
