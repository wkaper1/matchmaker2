package nl.uva.science.esc.statistics.factorials;

import java.util.HashMap;

/**
 * Ab initio calculation of factorials and functions involving factorials and caching the results.
 * (Check if caching is relevant)
 * @author wkaper1
 */
public class Factorials {
	private static HashMap<Integer, Long> factorial = new HashMap<Integer, Long>();
	private static HashMap<Integer, Double> gammaFactor = new HashMap<Integer, Double>();
	
	//The below version 1 methods use ordinary caching like you would do in any long running deterministic method
	//  they are still wasteful: if we ask for 100! and then 99! still many earlier done calculations are repeated
	
	/**
	 * The wellknown factorial function
	 */
	public static long factorial(int n) {
		Long fact0 = factorial.get((Integer)n);
		if (fact0 == null) {
			long fact = (long)n;
			while (n > 1) {
				n--;
				fact = fact * n;
			}
			factorial.put((Integer)n, (Long)fact);
			return fact;
		}
		else 
			return (long)fact0;
	}

	/**
	 * The gamma-factor that repeatedly appears in expressions for Student's t-distribution
	 * It's not the "Gamma function" itself, a complex-valued function, however for integer
	 * inputs a certain transformation of the gamma function acts much like a factorial.
	 * It's that transformation we calculate here. 
	 * It should be ready for use in t-distribution calculation!
	 */
	public static double gammaFactor(int n) {
		Double gammaF0 = gammaFactor.get((Integer)n);
		if (gammaF0 == null) {
			boolean even = (n % 2 == 0);
			n--;
			double gammaF = n;
			boolean upper = false;
			while (n > 1) {
				n--;
				if (upper) {
					gammaF = gammaF * n;
					upper = false;
				}
				else {
					gammaF = gammaF / n;
					upper = true;
				}
			}
			if (even) {
				gammaF = gammaF / (2 * Math.sqrt(n));
			}
			else {
				gammaF = gammaF / (Math.PI * Math.sqrt(n)); 
			}
			gammaFactor.put((Integer)n, (Double)gammaF);
			return gammaF;
		}
		else
			return (double)gammaF0;
	}
	
	//Version 2 methods and the private properties they need
	//  These versions should be more time efficient in the long run if you need many values closely together but often not identical
	//  They consume memory more quickly but after many calls the difference gets less.
	//  The method is not as general as the above: it exploits the quite special 'factorial' property.
	
	private static long[] factorial2 = new long[1000];   //array instead of hashmap, index 0 remains unused
	private static int nmax = 0;  //to what n is the factorial2 array currently filled?
	
	/**
	 * The wellknown factorial function, version 2
	 */
	public static long factorial2(int n) {
		if (n > nmax) {
			int i;
			long fact;
			//fill the array up to the required point, starting from the highest n already filled
			if (nmax == 0) { //invalid starting point
				i = 1;
				factorial2[1] = 1;
				fact = 1;
			}
			else { //valid starting point
				i = nmax;
				fact = factorial2[nmax];
			}
			while (i < n) { //fill it up!
				i++;
				fact = fact * i;
				factorial2[i] = fact;
			}
			nmax = i;
			return fact;
		}
		else 
			return factorial2[n];
	}

	private static double[] gammaFactor2 = new double[1000];
	private static double preGFactorEven;         //uncorrected gFactor, highest numbered even, fitting to gmax
	private static double preGFactorOdd;          //uncorrected gFactor, highest numbered odd, fitting to gmax
	private static int gmax = 0;                  //to what max index is the gammaFactor array filled?

	/**
	 * The gammaFactor function, explained above, version 2
	 * @param n
	 * @return
	 */
	public static double gammaFactor2(int n) {
		if (n > gmax) {
			int g;
			double gFOdd;
			double gFEven;
			boolean even;
			if (gmax == 0) { //start with nothing
				gammaFactor2[1] = 0;  //undefined?
				gammaFactor2[2] = 0;  //undefined?
				gFOdd = 2;
				gFEven = 3.0/2;
				gammaFactor2[3] = correctGFactor(gFOdd, 3);
				gammaFactor2[4] = correctGFactor(gFEven, 4);
				gFOdd = gFOdd / 3;
				g = 4;
				even = true;
			}
			else { //start from previous valid gmax
				gFOdd = preGFactorOdd;
				gFEven = preGFactorEven;
				g = gmax;
				even = (gmax % 2 == 0);
			}
			while (g < n) { //fill it up!
				g++;
				even = !even;
				if (even) {
					gFOdd = gFOdd / (g - 1);
					gFEven = gFEven * (g - 1);
					gammaFactor2[g] = correctGFactor(gFEven, g);
				}
				else {
					gFOdd = gFOdd * (g - 1);
					gFEven = gFEven / (g - 1);
					gammaFactor2[g] = correctGFactor(gFOdd, g);
				}
			}
			preGFactorOdd = gFOdd;
			preGFactorEven = gFEven;
			gmax = n;
		}
		return gammaFactor2[n];
	}
	
	private static double correctGFactor(double preGFactor, int n) {
		if (n % 2 == 0) { //n is even
			return preGFactor / (2 * Math.sqrt(n));
		}
		else { //n is odd
			return preGFactor / (Math.PI * Math.sqrt(n));
		}
	}
}