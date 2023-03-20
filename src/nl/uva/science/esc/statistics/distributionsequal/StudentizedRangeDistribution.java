package nl.uva.science.esc.statistics.distributionsequal;

import java.util.function.DoubleUnaryOperator;

import nl.uva.science.esc.math.SingleParameterMask;
import nl.uva.science.esc.math.Tabulator;
import nl.uva.science.esc.math.Tabulator.VariationScheme;
import nl.uva.science.esc.math.integration.*;
import nl.uva.science.esc.math.integration.Integrator.method;
import nl.uva.science.esc.math.integration.Integrator.boundary;

/**
 * Static functions involved in calculating the cumulative probability for the Studentized
 * Range Distribution as used in Tukey's Honestly Significant Difference test.
 * 
 * See: https://en.wikipedia.org/wiki/Studentized_range_distribution
 * And: Dunlap, W.P.; Powell, R.S.; Konnerth, T.K. (1977). "A FORTRAN IV function for calculating 
 * probabilities associated with the studentized range statistic". Behavior Research Methods 
 * & Instrumentation. 9 (4): 373–375. doi:10.3758/BF03202264
 * 
 * We follow Dunlap at al. in their description of 3 nested integrals which they call the inner,
 * the middle and the outer integral.
 * The inner integral is the cumulative probability for the normal distribution, which they '
 * approximate by a polynomial taken from Abramowith & Stegun (1964) Handbook of Mathematical 
 * Functions, 0931-932, formula's 26.2.2 and 26.2.17.
 * @author wkaper1
 */
public class StudentizedRangeDistribution {
	
	//Constants used in the "inner integral":
	public static final double b1 =  .319381530;
	public static final double b2 = -.356563782;
	public static final double b3 = 1.781477937;
	public static final double b4 =-1.821255978;
	public static final double b5 = 1.330274429;
	public static final double p  =  .2316419;
	public static final double sqrtTwoPi = Math.sqrt(2 * Math.PI);
	private static Integrator int1 = Integrator.Create();
	
	/**
	 * Inner integral: The cumulative probability for the normal distribution
	 * from minus infinity up to x. It's approximated by a polynomial.
	 * @param x, normalized variable for normal distribution
	 */
	public static double CumulativeProbabilityNormalDistribution(double x) {
		boolean positive = (x >= 0);
		//The given polynomial is only valid for positive x
		if (!positive) {
			x = -x;
		}
		double z = Math.exp(-x * x /2) / sqrtTwoPi;
		double t = 1 / (1 + p * x);
		double Px = 1 - z * (b1*t + b2*t*t + b3*t*t*t + b4*t*t*t*t + b5*t*t*t*t*t);
		//But we can find the negative part of the curve by symmetry!
		if (positive) {
			return Px;
		}
		else {
			return 1 - Px;
		}
	}
	
	/**
	 * Integrand of "middle integral", it calls the inner integral.
	 * [The method needs to be public when tuning it! Make it private afterwards?]
	 * @param n, number of treatments compared
	 * @param t, integration variable for the outer integral (see next method)
	 * @param u, integration variable for this middle integral
	 */
	public static double MiddleIntegrand(int n, double t, double u) {
		double z = Math.exp(-u * u /2) / sqrtTwoPi;
		double diff = CumulativeProbabilityNormalDistribution(u + t)
				- CumulativeProbabilityNormalDistribution(u);
		return z * Math.pow(diff, n - 1);
	}

	/**
	 * Integrand of the "outer integral", it contains calculation of the middle integral
	 * [The method needs to be public when tuning it! Make it private afterwards?]
	 * @param n, number of treatments compared
	 * @param df, degrees of freedom of the error variance
	 * @param q, studentized range statistic
	 * @param t, integration variable for this outer integral
	 */
	public static double OuterIntegrand(int n, int df, double q, double t) {
		DoubleUnaryOperator f = (u) -> MiddleIntegrand(n, t, u);
		double Pnt = n * int1.integrate(f, -7, 4, 256);    //TODO: tune this integral!
		double tq = t / q;
		double z = Math.exp(-tq * tq /2) / sqrtTwoPi;
		return Math.pow(4*tq*z, df) * (1 - Pnt) / t;
	}
	
	/**
	 * Calculate the probability for the given n, df and q this large, or larger
	 * @param n, number of treatments compared
	 * @param df, degrees of freedom of the error variance
	 * @param q, studentized range statistic
	 */
	public static double Distribution(int n, int df, double q) {
		DoubleUnaryOperator f = (t) -> OuterIntegrand(n, df, q, t);
		//TODO: tune and decide boundaries as functions of q and df, see Dunlop et al.
		double Int = int1.integrate(f, n, n, n);
		double Gamma = 1;  //TODO: find gamma as function of df / 2, i.e. fraction with 2 as denominator
		double cv = 2 * Math.pow(Math.sqrt(df * Math.PI) / 4, df) / Gamma;
		return cv * Int;
	}

	/**
	 * Run the tuning tests for the middle and outer integrals and show the results.
	 * After incorporating the tuning results into the middle integral, repeat the tests
	 * and finally incorporate the results into the outer integral.
	 * @throws Exception 
	 */
	public static void tuningReport() throws Exception {
		Integrator int1 = Integrator.Create(method.TRAPEZOIDAL_RULE);
		Integrator int2 = Integrator.Create();  //should be Simpson's
		double points[] = new double[] {-10, -6, -5, -2, -0.5, -0.25, -0.1, 0, 0.1, 0.25, 0.5, 1, 2, 5, 10, 20};
		
		System.out.println("Tuning tests for: the middle integral");
		System.out.println();
		System.out.println("Explore integral from minus infinity (=-10), numIntervals high (516)");
		System.out.println();
		int n = 3;     //number of treatments
		double t = 1;  //variable to integrate away in the outer integral...
		   //0 or a big value for "t" make the integrand vanish...
		   //what is the influence of these two? I'm relying on the Fortran guys saying they make no difference, mm...
		DoubleUnaryOperator function = (u) -> MiddleIntegrand(n, t, u);
		int2.tabulate(function, 516, points, boundary.UPPER, -10);
		System.out.println("The integral rises from zero on the left up to a horizontal asymptote on the right.");
		System.out.println("Determine boundaries on both sides that fit our accuracy of 7 decimals.");
		System.out.println();
		int2.vanishes(function, 516, boundary.LOWER, -10, -2.5, 0.5, 1E-8, true);
		int2.vanishes(function, 516, boundary.UPPER, +6, +1.5, 0.5, 1E-8, true);
		System.out.println("Conclusion: integration from -3.5 to 3.0 can replace minus to plus infinity.");
		System.out.println("Determine optimal method and number of intervals for our accuracy of 7 digits.");
		int1.tuneIntervals(function, -3.5, 3.0, 8, 2, 1E-8, 2, true);
		int2.tuneIntervals(function, -3.5, 3.0, 8, 2, 1E-8, 2, true);
		System.out.println("Conclusion: trapezoidal rule gives quickest convergence, 64 intervals is more than adequate.");
		System.out.println();
		
		System.out.println("Investigate influence of parameters (n, t) on results like te above.");
		System.out.println("All the below done with trapezoidal rule - should we try the other too?");
		IntegratorMultiTunable int3 = IntegratorMultiTunable.Create(
				IntegratorMultiTunable.method.TRAPEZOIDAL_RULE);
		System.out.println("Left boundary (approx. for minus infinity), dependence on n and t.");
		IntegratorTuner findVanishPL = int3.CreateFindVanishPointMulti(1024, 
				IntegratorMultiTunable.boundary.LOWER, -10.0, -2.5, 0.5, 1E-8);
		Tabulator tab1 = new Tabulator(StudentizedRangeDistribution.class, "MiddleIntegrand", 3, false);
		tab1.declareVariableInt(0, "n", new int[] {2, 3, 10, 100});
		tab1.declareVariableDouble(1, "t", new double[] {0.2, 1, 5, 50, 500});
		tab1.declareVariableDouble(2, "u", new double[] { 1 });  //dummy integration variable, value not used
		tab1.setTransformation(findVanishPL, 2);                  //tell Tabulator that variable 2 is involved in the transformation
		tab1.tabulate(VariationScheme.ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT);
		
		System.out.println("Right boundary (approx. for plus infinity), dependence on n and t.");
		IntegratorTuner findVanishPH = int3.CreateFindVanishPointMulti(1024, 
				IntegratorMultiTunable.boundary.UPPER, +6.0, +1.5, 0.5, 1E-8);
		tab1.setTransformation(findVanishPH, 2);
		tab1.tabulate(VariationScheme.ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT);
		System.out.println("Conclusion: enlarge boundaries, from -6.5 to +3.5 (or +4.5 if you want n=2");
		System.out.println();

		System.out.println("Number of intervals needed for 7 decimals accuracy, dependence on n and t.");
		System.out.println("Scheme: one pass through the middle for each variable.");
		IntegratorTuner tuneIntervals = int3.CreateTuneIntervalsMulti(-6.5, 4.5, 8, 2, 1E-8, 2);
		tab1.setTransformation(tuneIntervals, 2);                //tell Tabulator that variable 2 is involved in the transformation
		tab1.tabulate(VariationScheme.ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT);
		System.out.println("Scheme: cartesian product.");
		tab1.tabulate(VariationScheme.ALL_COMBINATIONS_ZIGZAG);
		
		//TODO 2: on to the outer integral where the parameters DO play a role according to the Fortran authors. But they do not state which role.
	}
}
