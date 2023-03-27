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
	
	private static BigInteger[] factorial2 = new BigInteger[1000];   //array instead of hashmap, index 0 remains unused
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
	
	public static double GammaOfRational(RationalNumber r) throws Exception {
		if (gammaLib == null) {
			gammaLib = new GammaOfRationalsLibrary();
		}
		r.integerQuotientInit();
		if (r.integerQuotientIsInteger()) {
			int i = (int)r.integerQuotientAsLong();
			return (factorial2(i - 1)).doubleValue();
		}
		else {
			RationalNumber remainingRational = r.integerQuotientRemainingRational();
			double gammaR = gammaLib.getGamma(remainingRational);
			RationalNumber product = remainingRational.clone();
			for (int i=1; i<r.integerQuotientAsLong(); i++) {
				remainingRational.AddInt(1);
				//Nu heb ik nog helemaal geen caching van tussenresultaten, en dat is ook best pittig
				//  want dat moet denk ik apart per remainingRational! Een HashMap met dezelfde dimensies als de "library"!
				product.MultiplyThisBy(remainingRational);
			}
			return product.MultiplyGivenDoubleByThis(gammaR);			
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
		private Map<Integer, Map<Integer, Double>> library = new HashMap<Integer, Map<Integer, Double>>();
		
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
		
		private void setGamma(int numerator, int denominator, double gamma) {
			Map<Integer, Double> inner = library.getOrDefault(denominator, null);
			if (inner == null) {
				inner = new HashMap<Integer, Double>();
				library.put(denominator, inner);
			}
			inner.put(numerator, gamma);
		}
		
		public double getGamma(long numerator, long denominator ) throws Exception {
			Map<Integer, Double> inner = library.getOrDefault(denominator, null);
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
}
