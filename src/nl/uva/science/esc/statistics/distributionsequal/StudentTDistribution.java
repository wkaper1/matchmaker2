package nl.uva.science.esc.statistics.distributionsequal;

import java.util.HashMap;
import nl.uva.science.esc.math.*;
import nl.uva.science.esc.math.factorials.Factorials;

/**
 * Static methods involved with Student's T-distribution
 * @author wkaper1
 */
public class StudentTDistribution {
	
	public static final double minimumTermSize = 0.000000001;

	/**
	 * Calculate the cumulative probability that t is between minus infinity and t'
	 * @param t, the wellknown t statistic from Student's t-distribution
	 * @param df, degrees of freedom (number of observations minus 1)
	 * @return, the cumulative probability
	 */
	public static double CumulativeProbability(double t, int df) {
		PochhammerSeries a = new PochhammerSeries(new RationalNumber(1, 2));
		PochhammerSeries c = new PochhammerSeries(new RationalNumber(3, 2));
		PochhammerSeries b = new PochhammerSeries(new RationalNumber(df + 1, 2));
		PochhammerSeries fac = new PochhammerSeries(new RationalNumber(0, 1));
		double powerSeriesVar = - Math.pow(t, 2) / df;
		
		//Calculate the hypergeometric function until next term smaller than minimumTermsize
		double sum = 0;
		double nextTerm = minimumTermSize + 1;
		int n = 0;  //term number, starts at 0
		while (nextTerm > minimumTermSize) {
			//build the next term from 5 factors
			nextTerm = 1;
			nextTerm = a.NextTerm().MultiplyGivenDoubleByThis(nextTerm);
			nextTerm = b.NextTerm().MultiplyGivenDoubleByThis(nextTerm);
			nextTerm = c.NextTerm().DivideGivenDoubleByThis(nextTerm);
			nextTerm = fac.NextTerm().DivideGivenDoubleByThis(nextTerm);
			nextTerm = nextTerm * Math.pow(powerSeriesVar, n);
			//add the term
			sum = sum + nextTerm;
			n++;
		}
		//Make a probability out of that
		double prob = (t / 2) * Factorials.gammaFactor(df) * sum;
		return prob;
	}

	/**
	 * Calculate the probability that t is between -t' and +t',
	 * that is: the probability that |t| is as large as given, or smaller
	 * @param t, the wellknown t statistic from Student's t-distribution
	 * @param df, degrees of freedom (number of observations minus 1)
	 * @return, the said probability
	 */
	public static double ProbabilityOfAbsTOrSmaller(double t, int df) {
		return CumulativeProbability(t, df) - CumulativeProbability(-t, df);
	}
	
	//version 2, using caching of the hyperGeometricFactors (df, n)
	//  df is the HashMap key, n is the array index
	//  The downside of this caching is we calculate way more terms than we need, to avoid upgrading the PochhammerSeries type
	private static HashMap<Integer, double[]> hyperGeometricFactors = new HashMap<Integer, double[]>();
	
	//Choose it really large, you can't get past it, you can only enlarge it.
	private static final int nMax = 1000;
	
	/**
	 * Calculate the cumulative probability that t is between minus infinity and t'
	 * @param t, the wellknown t statistic from Student's t-distribution
	 * @param df, degrees of freedom (number of observations minus 1)
	 * @return, the cumulative probability
	 */
	public static double CumulativeProbability2(double t, int df) {
		//Calculate the hypergeometric function until next term smaller than minimumTermsize
		double powerSeriesVar = - Math.pow(t, 2) / df;
		double[] hgFactors = developHyperGeometricFactors(df);
		double sum = 0;
		double nextTerm = minimumTermSize + 1;
		int n = 0;  //term number, starts at 0
		while (nextTerm > minimumTermSize) {
			nextTerm = hgFactors[n] * Math.pow(powerSeriesVar, n);
			sum = sum + nextTerm;
			n++;
		}
		//Make a probability out of that
		double prob = (t / 2) * Factorials.gammaFactor(df) * sum;
		return prob;
	}

	/**
	 * Fill the Hypergeometric factors cache for the requested value of df, up to nmax
	 * if it does not exist, othrewise return that row immediately
	 * @param df, degrees of freedom
	 */
	private static double[] developHyperGeometricFactors(int df) {
		double[] hgFactors = hyperGeometricFactors.get(df); 
		if (hgFactors == null) {
			PochhammerSeries a = new PochhammerSeries(new RationalNumber(1, 2));
			PochhammerSeries c = new PochhammerSeries(new RationalNumber(3, 2));
			PochhammerSeries b = new PochhammerSeries(new RationalNumber(df + 1, 2));
			PochhammerSeries fac = new PochhammerSeries(new RationalNumber(0, 1));

			hgFactors = new double[nMax];
			for (int n=0; n<=nMax; n++) {
				//build the next hypergeometric factor from the next 4 Pochhammer terms
				double fact = 1;
				fact = a.NextTerm().MultiplyGivenDoubleByThis(fact);
				fact = b.NextTerm().MultiplyGivenDoubleByThis(fact);
				fact = c.NextTerm().DivideGivenDoubleByThis(fact);
				fact = fac.NextTerm().DivideGivenDoubleByThis(fact);
				hgFactors[n] = fact;
			}
			hyperGeometricFactors.put(df, hgFactors);			
		}
		return hgFactors;
	}
	
	/**
	 * Calculate the probability that t is between -t' and +t',
	 * that is: the probability that |t| is as large as given, or smaller
	 * @param t, the wellknown t statistic from Student's t-distribution
	 * @param df, degrees of freedom (number of observations minus 1)
	 * @return, the said probability
	 */
	public static double ProbabilityOfAbsTOrSmaller2(double t, int df) {
		return CumulativeProbability2(t, df) - CumulativeProbability2(-t, df);
	}
	
	//Version 3 cacching, using the new PochhammerSeries3 type, it can restart a series "in the middle".
}
