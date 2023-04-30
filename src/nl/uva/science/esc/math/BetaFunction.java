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

}
