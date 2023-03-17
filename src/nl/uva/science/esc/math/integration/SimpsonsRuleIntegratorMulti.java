package nl.uva.science.esc.math.integration;

import nl.uva.science.esc.math.SingleParameterMask;

/**
 * Implements Simpsons 1/3 rule in extended form, numerical integration
 * See: https://en.wikipedia.org/wiki/Simpson%27s_rule
 * @author wkaper1
 */
public class SimpsonsRuleIntegratorMulti extends IntegratorMultiTunable {
	
	protected SimpsonsRuleIntegratorMulti() {
	}

	@Override
	public double integrate(SingleParameterMask function, double lowerbound, double upperbound, int numIntervals) {
		double intervalLength = (upperbound - lowerbound) / numIntervals;
		double sum = 0;
		sum += function.applyAsDouble(lowerbound);
		boolean even = false;
		for (int k=1; k<=numIntervals-1; k++) {
			if (even) {
				sum += 2 * function.applyAsDouble(lowerbound + k * intervalLength);
				even = false;
			}
			else {
				sum += 4 * function.applyAsDouble(lowerbound + k * intervalLength);
				even = true;
			}
		}
		sum += function.applyAsDouble(upperbound);
		return sum * intervalLength / 3;
	}
}
