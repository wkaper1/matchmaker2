package nl.uva.science.esc.statistics.distributionsequal;

/**
 * Add data-elements and then calculate mean and estimated variance for the elements added up to now
 */
public abstract class MeanAndVarianceAccumulator {
	
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
}
