package nl.uva.science.esc.statistics.distributionsequal;

import java.util.Collection;

/**
 * Add data-elements and then calculate mean and estimated variance for the elements added up to now
 */
public abstract class MeanAndVarianceAccumulator {

	/**
	 * Algorithm to use if you call this class' factory method
	 */
	public static final String currentAlgorithm = "ShiftedData";
	
	/**
	 * Add a data-element
	 * @param d,  the element to be added
	 */
	public abstract void addDataElement(double d);
	
	/**
	 * Get the mean of the population added, or an estimate of that mean
	 */
	public abstract double mean() throws Exception;
	
	/**
	 * Get the variance of the population added or an estimate of the population variance if
	 * we saw just a randomly chosen sample of it 
	 * @param fullPopulation, did we see the full population? Alternative: we saw a random sample.
	 */	
	public abstract double variance(boolean fullPopulation) throws Exception;
	
	/**
	 * The undividedVariance is the variance before being divided by either n or (n-1).
	 * It has application in at least the calculation of an ANOVA.
	 */
	public abstract double undividedVariance() throws Exception;

	/**
	 * Returns the number of elements accumulated
	 */
	public abstract int count();

	/**
	 * Add all the Doubles in the given Collection to this accumulator
	 */
	public void addRange(Collection<Double> elements) {
		elements.forEach(element -> { addDataElement(element); });
	}
	
	/**
	 * Factory method, to implement the current default algorithm
	 */
	public static MeanAndVarianceAccumulator Create() throws Exception {
		switch (currentAlgorithm) {
			case "ShiftedData": 
				return new MeanAndVarianceAccumulatorShiftedDataAlgorithm();
			case "Welfords": 
				return new MeanAndVarianceAccumulatorWelfordsAlgorithm();
			default: 
				throw new Exception("Unknown algorithm " + currentAlgorithm);
		}
	}
}
