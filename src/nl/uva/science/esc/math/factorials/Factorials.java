package nl.uva.science.esc.math.factorials;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import nl.uva.science.esc.math.RationalNumber;

/**
 * Ab initio calculation of factorials and functions involving factorials and caching the results.
 * (Check if caching is relevant)
 * @author wkaper1
 */
public class Factorials {
	private static HashMap<Integer, BigInteger> factorial = new HashMap<Integer, BigInteger>();
	private static HashMap<Integer, Double> gammaFactor = new HashMap<Integer, Double>();
	
	//The below version 1 methods use ordinary caching like you would do in any long running deterministic method
	//  they are still wasteful: if we ask for 100! and then 99! still many earlier done calculations are repeated
	
	/**
	 * The wellknown factorial function
	 */
	public static BigInteger factorial(int n) {
		BigInteger fact = factorial.get((Integer)n);
		if (fact == null) {
			fact = BigInteger.valueOf(n);
			while (n > 1) {
				n--;
				fact = fact.multiply(BigInteger.valueOf(n));
			}
			factorial.put((Integer)n, (BigInteger)fact);
			return fact;
		}
		else 
			return (BigInteger)fact;
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
			int i = n;
			i--;
			RationalNumber gammaF = new RationalNumber(i, 1);
			boolean upper = false;
			while (i > 1) {
				i--;
				if (upper) {
					gammaF.MultiplyThisByInt(i);
					upper = false;
				}
				else {
					gammaF.DivideThisByInt(i);
					upper = true;
				}
			}
			double gammaF2;
			if (even) {
				gammaF2 = gammaF.toDouble() / (2 * Math.sqrt(n));
			}
			else {
				gammaF2 = gammaF.toDouble() / (Math.PI * Math.sqrt(n)); 
			}
			gammaFactor.put((Integer)n, (Double)gammaF2);
			return gammaF2;
		}
		else
			return (double)gammaF0;
	}
	
	//Version 2 methods and the private properties they need
	//  These versions should be more time efficient in the long run if you need many values closely together but often not identical
	//  They consume memory more quickly but after many calls the difference gets less.
	//  The method is not as general as the above: it exploits the quite special 'factorial' property.
	
	private static BigInteger[] factorial2 = new BigInteger[1000];   //array instead of hashmap
	private static int nmax = 0;  //to what n is the factorial2 array currently filled?
	
	/**
	 * The wellknown factorial function, version 2
	 */
	public static BigInteger factorial2(int n) {
		if (n > nmax) {
			int i;
			BigInteger fact;
			//fill the array up to the required point, starting from the highest n already filled
			if (nmax == 0) { //invalid starting point
				factorial2[0] = new BigInteger("1"); //according to the convention for an empty product
				i = 1;
				factorial2[1] = new BigInteger("1");
				fact = new BigInteger("1");
			}
			else { //valid starting point
				i = nmax;
				fact = factorial2[nmax];
			}
			while (i < n) { //fill it up!
				i++;
				fact = fact.multiply(BigInteger.valueOf(i));
				factorial2[i] = fact;
			}
			nmax = i;
			return fact;
		}
		else 
			return factorial2[n];
	}

	private static double[] gammaFactor2 = new double[1000];
	private static RationalNumber preGFactorEven;         //uncorrected gFactor, highest numbered even, fitting to gmax
	private static RationalNumber preGFactorOdd;          //uncorrected gFactor, highest numbered odd, fitting to gmax
	private static int gmax = 0;                  //to what max index is the gammaFactor array filled?

	/**
	 * The gammaFactor function, explained above, version 2
	 */
	public static double gammaFactor2(int n) {
		if (n > gmax) {
			int g;
			RationalNumber gFOdd;
			RationalNumber gFEven;
			boolean even;
			if (gmax == 0) { //start with nothing
				gammaFactor2[1] = 0;  //undefined?
				gFEven = new RationalNumber(1, 1);
				gFOdd = new RationalNumber(2, 1);
				gammaFactor2[2] = correctGFactor(gFEven, 2);
				gammaFactor2[3] = correctGFactor(gFOdd, 3);
				g = 3;
				gFEven.DivideThisByInt(g - 1);
				even = false;
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
					gFOdd.DivideThisByInt(g - 1);
					gFEven.MultiplyThisByInt(g - 1);
					gammaFactor2[g] = correctGFactor(gFEven, g);
				}
				else {
					gFOdd.MultiplyThisByInt(g - 1);
					gFEven.DivideThisByInt(g - 1);
					gammaFactor2[g] = correctGFactor(gFOdd, g);
				}
			}
			preGFactorOdd = gFOdd;
			preGFactorEven = gFEven;
			gmax = n;
		}
		return gammaFactor2[n];
	}
	
	private static double correctGFactor(RationalNumber preGFactor, int n) {
		if (n % 2 == 0) { //n is even
			return preGFactor.toDouble() / (2 * Math.sqrt(n));
		}
		else { //n is odd
			return preGFactor.toDouble() / (Math.PI * Math.sqrt(n));
		}
	}
	
	private static GammaOfRationalsLibrary gammaLib;
	private static GammaOfRationalsCache gammaCache;

	/**
	 * Return the Gamma function value of a RationalNumber.
	 * This is currently limited to rational numbers having small denominators: 1, 2, 3, 4.
	 * The number should be already simplified, or having a small-enough denominator already.
	 * (RationalNumbers that simplify to integers are however handled without protest)
	 */
	public static double gammaOfRational(RationalNumber r) throws Exception {
		if (gammaLib == null) {
			gammaLib = new GammaOfRationalsLibrary();
			gammaCache = new GammaOfRationalsCache();
		}
		//The following 2 if's can be replaced by one if we call "simplify"...
		//But how fast is simplification? (finding the gcd...). Another option: trying to treat integers as not at all a special case.
		if (r.isInteger()) {
			int i = r.numerator().intValueExact();
			return (factorial2(i - 1)).doubleValue();
		}
		r.integerQuotientInit();
		int integerPart = (int)r.integerQuotientAsLong();
		if (r.integerQuotientIsInteger()) { //remainder zero
			return (factorial2(integerPart - 1)).doubleValue();
		}
		else {
			RationalNumber remainingRational = r.integerQuotientRemainingRational();
			double gammaRemaining = gammaLib.getGamma(remainingRational);
			if (integerPart == 0) {
				return gammaRemaining;
			}
			int filledUpTo = gammaCache.setCurrentSeries(remainingRational);
			if (integerPart < filledUpTo) {
				return gammaCache.get(integerPart - 1).MultiplyGivenDoubleByThis(gammaRemaining);
			}
			else {
				RationalNumber product;
				RationalNumber factor;
				if (filledUpTo == 0) { //add the very first element
					factor = remainingRational;
					product = factor;
					gammaCache.add(product);
				}
				else {
					factor = remainingRational.AddInt(filledUpTo - 1);
					product = gammaCache.get(filledUpTo - 1);
				}
				while (filledUpTo < integerPart - 1) {
					factor = factor.AddInt(1);
					product = product.MultiplyBy(factor);
					filledUpTo = gammaCache.add(product);
				}
				return product.MultiplyGivenDoubleByThis(gammaRemaining);
			}
		}
	}
	
	/**
	 * Library of often-needed Gamma-values for Rational numbers < 1 having small denominators: 2, 3, 4.
	 * Denominator 1 should not be needed, because we have the ordinary factorial function for that!
	 * You can extend it to bigger denominators but it might require work.
	 * (Like reading in a file that you found somewhere or calculating the needed integrals)
	 */
	private static class GammaOfRationalsLibrary {
		//2D map of gamma values of rational numbers:
		//     denominator -> ( numerator -> gamma)
		private Map<Long, Map<Long, Double>> library = new HashMap<Long, Map<Long, Double>>();
		
		/**
		 * Constructor, takes care that the library is filled
		 */
		public GammaOfRationalsLibrary() {
			//denominator = 2
			setGamma(1, 2, Math.sqrt(Math.PI));
			//denominator = 3
			setGamma(1, 3, 2.678938534707747633656);
			setGamma(2, 3, 1.354117939426400416945);
			//denominator = 4
			setGamma(1, 4, 3.625609908221908311931);
			setGamma(2, 4, Math.sqrt(Math.PI)); //the client software can't simplify Rationals yet...
			setGamma(3, 4, 1.225416702465177645129);
		}
		
		private void setGamma(long numerator, long denominator, double gamma) {
			Map<Long, Double> inner = library.getOrDefault(denominator, null);
			if (inner == null) {
				inner = new HashMap<Long, Double>();
				library.put(denominator, inner);
			}
			inner.put(numerator, gamma);
		}
		
		public double getGamma(long numerator, long denominator ) throws Exception {
			Map<Long, Double> inner = library.getOrDefault(denominator, null);
			if (inner == null) {
				throw new Exception("Denominators larger than 4 are not supported yet.");
			}
			Double gamma = inner.getOrDefault(numerator, null);
			if (gamma == null) {
				throw new Exception("Gamma for nominator " + numerator + " and denominator " + denominator + " not found in library.");
			}
			return gamma;
		}
		
		public double getGamma(RationalNumber r) throws Exception {
			return getGamma(r.numerator().longValueExact(), r.denominator().longValueExact());
		}
	}
	
	/**
	 * Cache for the factorial-like products of Rationals, needed to calculate the Gamma function of a Rational
	 * The products are indexed by the integerPart of the current Rational being added to the product.
	 * So the element at index 0 is the one and only Rational < 1. The next Rational added is 1 bigger, so it's at index 1, etc.
	 */
	private static class GammaOfRationalsCache {
		//Each series is stored in a 2D map, keyed by the remainderRational
		//     denominator -> ( numerator -> product-array)
		private Map<Long, Map<Long, RationalProductSeries>> cache = new HashMap<Long, Map<Long, RationalProductSeries>>();
		private RationalProductSeries currentSeries;
		
		/**
		 * Choose the series we are going to create, enlarge or interrogate, by providing the remainingRational < 1
		 * (having small denominator, 2, 3, 4) that is the key to the series.
		 * @param remainingRational, RationalNumber < 1 that will be the starting point of the current series
		 * @return up to what index (not including) is the series developed already? Index of first empty slot!
		 * @throws Exception 
		 */
		public int setCurrentSeries(RationalNumber remainingRational) throws Exception {
			long denominator = remainingRational.denominator().longValueExact();
			Map<Long, RationalProductSeries> inner = cache.getOrDefault(denominator, null);
			if (inner == null) {
				inner = new HashMap<Long, RationalProductSeries>();
				cache.put(denominator, inner);
			}
			long numerator = remainingRational.numerator().longValueExact();
			currentSeries = inner.getOrDefault(numerator, null);
			if (currentSeries == null) {
				currentSeries = new RationalProductSeries();
				inner.put(numerator, currentSeries);
			}
			return currentSeries.filledUpTo;
		}

		/**
		 * Add a rational to the current series
		 * @return index of next slot to fill
		 */
		public int add(RationalNumber r) {
			currentSeries.series[currentSeries.filledUpTo] = r;
			return currentSeries.filledUpTo++;
		}

		/**
		 * Return a RationalNumber from the current series
		 */
		public RationalNumber get(int index) {
			return currentSeries.series[index];
		}
		
		//Products of Rationals, cached by integerPart as the array index number.
		private class RationalProductSeries {
			public RationalNumber[] series = new RationalNumber[1000];
			public int filledUpTo = 0;  //up to which index is the series filled?
		}
	}//end private static class GammaOfRationalsCache
}
