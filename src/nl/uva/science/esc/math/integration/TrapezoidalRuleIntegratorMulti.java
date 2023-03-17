package nl.uva.science.esc.math.integration;

import nl.uva.science.esc.math.SingleParameterMask;

/**
 * Implements the extended trapezoidal rule for numerical integration
 * See: https://en.wikipedia.org/wiki/Numerical_integration
 * @author wkaper1
 */
public class TrapezoidalRuleIntegratorMulti extends IntegratorMultiTunable {
	
	protected TrapezoidalRuleIntegratorMulti() {
	}

	@Override
	public double integrate(SingleParameterMask function, double lowerbound, double upperbound, int numIntervals) {
		double intervalLength = (upperbound - lowerbound) / numIntervals;
		double sum = 0;
		sum += function.applyAsDouble(lowerbound) / 2;
		for (int k=1; k<=numIntervals-1; k++) {
			sum += function.applyAsDouble(lowerbound + k * intervalLength);
		}
		sum += function.applyAsDouble(upperbound) / 2;
		return intervalLength * sum;
	}
}
