package nl.uva.science.esc.statistics.distributionsequal;

public class MeanAndVarianceAccumulatorWelfordsAlgorithm extends MeanAndVarianceAccumulator {
	int n;               //number of data-elements
	double currentMean;
	double sumOfSquares; //the variance before dividing by n or (n-1)
	
	public MeanAndVarianceAccumulatorWelfordsAlgorithm() {
		n = 0;
		currentMean = 0;
		sumOfSquares = 0;
	}

	@Override
	public void addDataElement(double d) {
		double prevMean = currentMean;
		n++;
		currentMean = currentMean + (d - currentMean) / n;
		sumOfSquares = sumOfSquares + (d - prevMean) * (d - currentMean);
	}

	@Override
	public double mean() throws Exception {
		if (n > 0)
			return currentMean;
		else
			throw new Exception("Number of data-elements is zero, mean is not available.");
	}

	@Override
	public double variance(boolean fullPopulation) throws Exception {
		if (n > 1) {
			int divisor = (fullPopulation) ? n : (n - 1);
			return sumOfSquares / divisor;			
		}
		else
			throw new Exception("Number of data-elements is: " + n + ", while variance has meaning only for n >= 2.");	
	}

	@Override
	public double undividedVariance() throws Exception {
		if (n > 1)
			return sumOfSquares;
		else
			throw new Exception("Number of data-elements is: " + n + ", while variance has meaning only for n >= 2.");	
	}
}
