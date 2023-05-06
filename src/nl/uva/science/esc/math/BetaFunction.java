package nl.uva.science.esc.math;

import nl.uva.science.esc.math.factorials.Factorials;

/**
 * Variants of the BetaFunction.
 * It is related to the GammaFunction which in the Factorial subpackage
 * @author Wolter2
 *
 */
public class BetaFunction {
	
	/**
	 * The Complete BetaFunction
	 * We evaluate it for RationalNumbers only as this is the use that occurs in statistics.
	 */
	public static double CompleteBetaFunction(RationalNumber a, RationalNumber b) throws Exception {
		double GammaAPlusB = Factorials.gammaOfRational(a.Add(b, true));
		return Factorials.gammaOfRational(a) * Factorials.gammaOfRational(b) / GammaAPlusB;
	}

	/**
	 * The complete BetaFunction, evaluated for halve only, i.e. for aa/2 and bb/2 where aa and bb are ints.
	 * In these cases we can return an exact result, consisting of a RationalNumber and an irrational factor
	 * equal to Pi that can be present or absent!
	 */
	private static CompleteBetaValueSimplified CompleteBetaFunctionSimplified (int aa, int bb) throws Exception {
		int i = aa % 2; //i tests whether a is odd
		int j = bb % 2;
		Factorials.SimplifiedGammaOfRational g1 = Factorials.gammaOfRationalSimplified(new RationalNumber(aa, 2));
		Factorials.SimplifiedGammaOfRational g2 = Factorials.gammaOfRationalSimplified(new RationalNumber(bb, 2));
		Factorials.SimplifiedGammaOfRational g3 = Factorials.gammaOfRationalSimplified(new RationalNumber(aa + bb, 2));
		
		boolean hasPiFactor = (i == 1 && j == 1);  //only if both are odd
		return new CompleteBetaValueSimplified(hasPiFactor, 
				g1.getRationalFactor().MultiplyBy(g2.getRationalFactor()).DivideBy(g3.getRationalFactor()) );
	}

	/**
	 * The incomplete BetaFunction has one more parameter x, in comparison to the complete betafunction.
	 * For x=1 the two should be equal.
	 * Usually the RegularizedIncompleteBetaFunction is defined as the quotient of incomplete and
	 * complete BetaFunction, but we do it the other way around.
	 * a and b are RationalNumbers with denominator equal to 2, x is a double.
	 *
	 */
	public static double IncompleteBetaFunction(double x, RationalNumber a, RationalNumber b) throws Exception {
		return RegularizedIncompleteBetaFunction(x, a, b) * CompleteBetaFunction(a, b);
	}

	/**
	 * The Regularized Incomplete BetaFunction
	 * a and b are RationalNumbers with denominator equal to 2, x is a double.
	 */
	public static double RegularizedIncompleteBetaFunction(double x, RationalNumber a, RationalNumber b) throws Exception {
		RegularizedIncompleteBetaExpressionInX expr = get(a, b);
		return expr.evalIx(x);
	}

	/**
	 * The F cumulative distribution
	 * d1 and d2 are the degress of freedom (integers) of the statistical variables being compared
	 */
	public static double FCumulativeDistribution(double x, int d1, int d2) throws Exception {
		RegularizedIncompleteBetaExpressionInX expr = get(new RationalNumber(d1, 2), new RationalNumber(d2, 2));
		return expr.evalFx(x);
	}

	/**
	 * Get an object that contains an expression in X for the Regularized Incomplete Beta Function, for a fixed
	 * combination (a, b) where a and b are RationalNumbers with denominator equal to 2.
	 * The same object also gives an expression in F (the F statistic) for the cumulative
	 * F distribution for given degrees of freedom d1 and d2, where d1 = 2*a and d2 = 2*b.
	 */
	public static RegularizedIncompleteBetaExpressionInX get(RationalNumber a, RationalNumber b) throws Exception {
		if (cache == null) {
			cache = new BetaPolynomialsCache();			
		}
		if (a.denominator().intValueExact() != 2 || b.denominator().intValueExact() != 2) {
			throw new Exception("RegularizedIncompleteBetafunction only accepts RationalNumbers with 2 as denominator!");
		}
		return cache.getBetaExpressionInX(a.numerator().intValueExact(), b.numerator().intValueExact());
	}

	private static BetaPolynomialsCache cache;
	
	/**
	 * We use the recurrence relation: Ix(a+1,b) = Ix(a,b) - (x^a(1-x)^b) / a*B(a,b)
	 * and a similar rule for b:       Ix(a,b+1) = Ix(a,b) + (x^a(1-x)^b) / b*B(a,b)
	 * ...to build up expressions for Ix(a,b) where a and b are positive multiples of 1/2.
	 * Four expressions for Ix(1,1), Ix(1, 1/2), Ix(1/2, 1) and Ix(1/2, 1/2) serve as starting points.
	 * These starting points are expressions involving square roots and pi, they have irrational values.
	 * The remaining sum of terms turns out to be a (big) polynomial, each term multiplied by one or two
	 * square roots that are the same for all terms of the series so they can be factored out.
	 * (which square roots they are, depends on the starting point).
	 * 
	 *  Below we change notation and take a and b twice as large as above so they become integers and we use them
	 *  as indexes of the below BetaPolynomialsCache. When using the recurrence relations to fill the cache a and
	 *  b both will grow in steps of 2. So when a starts even it will stay even, and when it start uneven it stays
	 *  uneven, and similar for b. This makes 4 possibilities, one for each starting point.
	 *  The cache consists of 4 sub-caches that need to be filled separately.
	 *  
	 *  There exists a symmetry relation Ix(a,b) = I(1-x)(b,a) that allows us to fill only half of a quadrant,
	 *  we fill the quadrant where a >= b.
	 */
	private static class BetaPolynomialsCache {
		//the cache as a 2D array, having a and b as respective indexes
		PolynomialRationalCoefficients cache[][];
		int[][] maxA; //2 x 2 array showing the index a up to which the respective sub-cache is filled
		int[] maxB;   //shows for each index a, to what extent the b dimension is filled
		
		public BetaPolynomialsCache() {
			this.cache = new PolynomialRationalCoefficients[100][100];
			this.maxA = new int[2][2];
			this.maxB = new int[100]; //size should be kept equal with the first dimension of the cache.
			
			//Index position 0 is not used. The 4 starting points are initialized as 0, because their values
			//  are not polynomials and therefore kept out of the summation. They are added afterwards.
			cache[1][1] = new PolynomialRationalCoefficients(new RationalNumber[] { new RationalNumber(0, 1) } );
			maxA[1][1]  = 1;
			cache[1][2] = new PolynomialRationalCoefficients(new RationalNumber[] { new RationalNumber(0, 1) } );
			maxA[1][2]  = 1;
			cache[2][1] = new PolynomialRationalCoefficients(new RationalNumber[] { new RationalNumber(0, 1) } );
			maxA[2][1]  = 2;
			cache[2][2] = new PolynomialRationalCoefficients(new RationalNumber[] { new RationalNumber(0, 1) } );
			maxA[2][2]  = 2;
			
			maxB[1] = 2;
			maxB[2] = 2;
			for (int i=3; i<maxB.length; i++) {
				maxB[i] = 0;
			}
		}

		/**
		 * Get an expression in x for the Regularized Incomplete Beta function for a given a and b, where a and b
		 * are integers - and twice as large as the usual a and b (to fit the conventions of this cache!)
		 * @throws Exception 
		 */
		public RegularizedIncompleteBetaExpressionInX getBetaExpressionInX(int a, int b) throws Exception {
			//check if we need to apply mirror symmetry
			boolean mirrored = false;
			if (a < b) {
				mirrored = true;
				int b0 = a;
				a = b;
				b = b0; //swapped a and b
			}
			//find the coordinates of the starting point (i, j) for the given (a, b)
			int i = a % 2;
			int j = b % 2;
			//if the needed polynomial is not available in cache, grow the cache!
			if (cache[a][b] == null) {
				fillCacheAimingAt(a, b, i, j);
			}
			return new RegularizedIncompleteBetaExpressionInX(a, b, i, j, mirrored, cache[a][b]);
		}

		/**
		 * Determine suitable starting point, then use the recurrence relations till the required cell (a,b)
		 * is filled. We follow a rectangular path, first piece parallel to the a-axis, then upwards to (a,b).
		 * @throws Exception 
		 */
		private void fillCacheAimingAt(int a, int b, int i, int j) throws Exception {
			//for the starting point (i, j) to what extent is a series of a's already developed?
			int maxA = this.maxA[i][j];
			while (a > maxA) {
				//move along the line b = j by increasing maxA till it equals a
				useRelationForA(maxA, j);
				maxA += 2;
			}
			//to what extent is a fitting series of b's already developed? (it is j at the least)
			int maxB = this.maxB[a];
			while (b > maxB) {
				//move upward from (a, maxB) till we meet (a, b)
				useRelationForB(a, maxB);
				maxB += 2;
			}
			//register new cache state
			this.maxA[i][j] = a;
			this.maxB[a] = b;
		}

		/**
		 * Given a cache cell that's already filled: use recurrence relation to calculate cell for next a (+= 2) 
		 * @throws Exception 
		 */
		private void useRelationForA(int a, int b) throws Exception {
			PolynomialRationalCoefficients nw = newTermForRelation(a, b, a);
			cache[a + 2][b] = cache[a][b].Subtract(nw);
		}
		
		/**
		 * Given a cache cell that's already filled: use recurrence relation to calculate cell for next b (+= 2) 
		 * @throws Exception 
		 */
		private void useRelationForB(int a, int b) throws Exception {
			PolynomialRationalCoefficients nw = newTermForRelation(a, b, b);
			cache[a][b + 2] = cache[a][b].Add(nw);
		}

		/**
		 * The common part of both rules! For 'ab' fill in either a or b, depending on which of the two is changing
		 */
		private PolynomialRationalCoefficients newTermForRelation(int a, int b, int ab) throws Exception {
			//Build the polynomial x^k (1 - x)^l, where k and l are integer counterparts of a and b
			//   leaving out irrational factors sqrt(x) and sqrt(1-x) to be collected at the end
			int k = (int)Math.floor(a/2);
			int l = (int)Math.floor(b/2);
			PolynomialRationalCoefficients nw = PolynomialRationalCoefficients.CreateFromBinomialInts(1, -1, l);
			nw.MultiplyThisByPowerOfX(k);
			//Divide polynomial by (a/2) * B(a/2, b/2), leaving out the irrational Pi factor in B(...)
			RationalNumber cBeta = CompleteBetaFunctionSimplified(a, b).rational();
			RationalNumber divider = cBeta.MultiplyBy(new RationalNumber(ab, 2));
			return nw.DivideBy(divider);
		}

	}// end BetaPolynomialsCache

	/**
	 * Simplified value of the complete Beta function, for halves (rational numbers having denominator 2)
	 * The value is a product (rational * irrational), where the irrational part is either Pi or 1
	 */
	private static class CompleteBetaValueSimplified {
		private boolean hasPiFactor0;
		private RationalNumber r0;
		public CompleteBetaValueSimplified(boolean hasPiFactor, RationalNumber r) {
			this.hasPiFactor0 = hasPiFactor;
			this.r0 = r;
		}
		public boolean hasPifactor() {
			return hasPiFactor0;
		}
		public RationalNumber rational() {
			return r0;
		}
	}
	
	/**
	 * Simplified expression in x for the Regularized Incomplete Beta function, for one particular
	 * value of (a, b), where a and b are limited to multiples of one half.
	 * Also: simplified expression in x for the F-cumulative distribution, for one particular combination
	 * of (d1, d2) where d1 and d2 are the degrees of freedom for the two independent random variables being
	 * compared, x is the F-statistic in this interpretation.
	 * It consists internally of a polynomial with rational coefficients in X, that has to be multiplied by 1 or 2 
	 * square root expressions involving x, and to which an initial term has to be added (also irrational)
	 */
	public static class RegularizedIncompleteBetaExpressionInX {
		//the conventional a and b (multiples of one half) that this expression is valid for
		int aa;
		int bb;
		//the coordinates (i, j) of the starting point, as integers
		int i; //1 if a is odd, 0 otherwise
		int j; //1 if b is odd
		//did we use mirroring to get at the polynomial? then we have to reverse it!
		boolean mirrored;
		//the polynomial!
		PolynomialRationalCoefficients p;
		
		private RegularizedIncompleteBetaExpressionInX(int aa, int bb, int i, int j, 
				boolean mirrored, PolynomialRationalCoefficients p) {
			this.aa = aa;
			this.bb = bb;
			this.i = i;
			this.j = j;
			this.mirrored = mirrored;
			this.p = p;
		}

		/**
		 * Evaluate this expression interpreted as a cumulative F-distribution for given
		 * degrees of freedom of the two statistical variables d1 and d2; x is the F-statistic
		 */
		public double evalFx(double x) {
			//transform x, using d1 and d2 (really aa and bb)
			x = aa * x / (aa * x + bb);
			return evalIx(x);
		}
		
		/**
		 * Evaluate this regularized incomplete beta-expression for a given double x-value
		 */
		public double evalIx(double x) {
			//mirroring rule requires transformation of x
			if (mirrored) {
				x = 1 - x;
			}
			
			//evaluate the polynomial
			double out = p.eval(x);
			
			//multiply by max 2 square roots in x, depending on (i, j), and by 1/Pi, also depending on (i, j)
			if (i == 1) {
				out *= Math.sqrt(x);
			}
			if (j == 1) {
				out *= Math.sqrt(1 - x);
			}
			if (i == 1 && j == 1) {
				out *= 1/Math.PI;
			}
			
			//add the irrational initial term for Ix(i, j)
			if (i == 0 && j == 0) {
				out += x;
			}
			if (i == 0 && j == 1) {
				out += Math.sqrt(x);
			}
			if (i == 1 && j == 0) {
				out += 1 - Math.sqrt(1 - x);
			}
			if (i == 1 && j == 1) {
				out += (-2/Math.PI) * Math.asin(Math.sqrt(1 - x));
			}
			return out;
		}

		/**
		 * Return the conventional value of 'a' that this expression is valid for, as a multiple of 1/2
		 */
		public RationalNumber getA() {
			return mirrored ? new RationalNumber(bb, 2) : new RationalNumber(aa, 2); //reverse the swap we did
		}

		/**
		 * Return the conventional value of 'b' that this expression is valid for, as a multiple of 1/2
		 */
		public RationalNumber getB() {
			return mirrored ? new RationalNumber(aa, 2) : new RationalNumber(bb, 2);  //reverse the swap we did
		}

		/**
		 * Return the degrees of freedom 'd1' of the first statistic variable that this expression is valid for
		 */
		public int getD1() {
			return mirrored ? bb : aa;
		}
		
		/**
		 * Return the degrees of freedom 'd2' of the second statistic variable that this expression is valid for
		 */
		public int getD2() {
			return mirrored ? aa : bb;
		}
	}
}
