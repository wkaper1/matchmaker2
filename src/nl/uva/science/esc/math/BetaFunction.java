package nl.uva.science.esc.math;

import nl.uva.science.esc.math.factorials.Factorials;

/**
 * Variants of the BetaFunction.
 * It is related to the GammaFunction which in the factorials subpackage
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
	 * We aim at a computation that directly gives us the RegularizedIncompleteBetaFunction, therefore
	 * we define the (non-regularized) Incomplete BetaFunction as a product of RegularizedIncomplete and
	 * Complete BetaFunction.
	 */
	public static double IncompleteBetaFunction(double x, RationalNumber a, RationalNumber b) throws Exception {
		return RegularizedIncompleteBetaFunction(x, a, b) * CompleteBetaFunction(a, b);
	}

	/**
	 * The Regularized Incomplete BetaFunction.
	 */
	public static double RegularizedIncompleteBetaFunction(double x, RationalNumber a, RationalNumber b) {
		//TODO: this is the difficult part. It needs a cache of sums of polynomials
		//  if we're using the recurrence relations and that's my intention.
		return 0;
	}

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
		int[][] maxA; //2 x 2 array showing the index a up to which the respective subcache is filled
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
		 * Determine suitable starting point, then use the recurrence relations till the required cell (a,b)
		 * is filled. We follow a rectangular path, first piece parallel to the a-axis, then upwards to (a,b).
		 * @return the developed polynomial for (a,b)
		 */
		public PolynomialRationalCoefficients fillCacheAimingAt(int a, int b) {
			//TODO
		}

		/**
		 * Given a cache cell that's already filled: use recurrence relation to calculate cell for next a (+= 2) 
		 * @throws Exception 
		 */
		private void useRelationForA(int a, int b) throws Exception {
			PolynomialRationalCoefficients nw = newTermForRelation(a, b, a);
			cache[a + 2][b] = cache[a][b].Add(nw);
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
			//   leaving out irrational factors sqrt(x) and srt(1-x) to be collected at the end
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
	 * Simplified values of the complete Betafunction, for halves (rational numbers having denominator 2)
	 * The value is a product rational * irrational, where the irrational part is either Pi or 1
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
}
