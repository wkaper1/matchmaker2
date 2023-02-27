package nl.uva.science.esc.statistics.distributionsequal;

/**
 * Calculate variance (and mean) from (double) data values according to the Shifted Data algorithm
 * @author Wolter2
 */
public class MeanAndVarianceAccumulatorShiftedDataAlgorithm extends MeanAndVarianceAccumulator  {
	int n;               //number of data-elements
	double sum;          //sum of the data-elements, used in calculating the mean
	double sumOfSquares; //sum of squares of shifted differences
	double sumToSquare;  //sum of shifted differences, to be squared
	double shiftingConst;//single shifting constant
	
	public MeanAndVarianceAccumulatorShiftedDataAlgorithm() {
		n = 0;
		sum = 0;
		shiftingConst = 0;  //not a useable value
	}
	
	public MeanAndVarianceAccumulatorShiftedDataAlgorithm(double shiftingConst) {
		n = 0;
		sum = 0;
		this.shiftingConst = shiftingConst;
	}

	/**
	 * Add a data element to the set, a double
	 */
	public void addDataElement(double d) {
		n++;
		sum += d;
		if (n == 1) {
			if (shiftingConst == 0) {
				shiftingConst = d; //arbitrarily choose first element as shifting Const.				
			}
			sumOfSquares = 0;
			sumToSquare = 0;
		}
		else {
			double diff = d - shiftingConst;
			sumOfSquares += Math.pow(diff, 2);
			sumToSquare += diff;
		}
	}

	/**
	 * Get the mean of the added elements
	 */
	public double mean() throws Exception {
		if (n > 0) {
			return sum / n;			
		}
		else
			throw new Exception("Number of data-elements is zero, mean is not available.");
	}
	
	/**
	 * Get the variance of the population added or an estimate of the population variance if
	 * we saw just a randomly chosen sample of it 
	 * @param fullPopulation, did we see the full population that interests us? 
	 *    Alternative: no, we just saw a random sample of that population.
	 */
	public double variance(boolean fullPopulation) throws Exception {
		if (n > 1) {
			int divisor = (fullPopulation) ? n : (n - 1);
			return (sumOfSquares - Math.pow(sumToSquare, 2) / n) / divisor;			
		}
		else 
			throw new Exception("Number of data-elements is: " + n + ", while variance has meaning only for n >= 2.");
	}

	/**
	 * The undividedVariance is the variance before being divided by either n or (n-1).
	 * It has application in at least the calculation of an ANOVA.
	 */
	public double undividedVariance() throws Exception {
		if (n > 1) {
			return sumOfSquares - Math.pow(sumToSquare, 2) / n;
		}
		else 
			throw new Exception("Number of data-elements is: " + n + ", while variance has meaning only for n >= 2.");	
	}
}
