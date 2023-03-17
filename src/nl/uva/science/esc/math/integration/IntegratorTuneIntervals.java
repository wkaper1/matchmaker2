package nl.uva.science.esc.math.integration;

import java.util.function.DoubleUnaryOperator;

public class IntegratorTuneIntervals extends IntegratorTuner {
	private double lowerbound;
	private double upperbound; 
	private int initialIntervals;
	private int growFactor;
	private double convergenceCriterion;
	private int convergenceRepetitions;
	
	public IntegratorTuneIntervals(Integrator int1, double lowerbound, double upperbound, 
			int initialIntervals, int growFactor, double convergenceCriterion, int convergenceRepetitions) {
		super(int1);
		this.lowerbound = lowerbound;
		this.upperbound = upperbound;
		this.initialIntervals = initialIntervals;
		this.growFactor = growFactor;
		this.convergenceCriterion = convergenceCriterion;
		this.convergenceRepetitions = convergenceRepetitions;
	}

	@Override
	public double run(DoubleUnaryOperator f, double x) {
		return int1.tuneIntervals(f, lowerbound, upperbound, 
				initialIntervals, growFactor, convergenceCriterion, convergenceRepetitions, false);
	}

	@Override
	public boolean isConstant() {
		return true;
	}

}
