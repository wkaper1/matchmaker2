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
		double Pnt = n * int1.integrate(f, -6.5, 4.5, 256);
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
		System.out.println("Explore integrand and integral (n=3, t=1) from minus infinity (=-10), numIntervals high (516)");
		System.out.println();
		int n = 3;     //number of treatments
		double t = 1;  //variable to integrate away in the outer integral...
		   //0 or a big value for "t" make the integrand vanish...
		   //what is the influence of these two? I'm relying on the Fortran guys saying they make no difference, mm...
		DoubleUnaryOperator function = (u) -> MiddleIntegrand(n, t, u);
		int2.tabulate(function, 516, points, boundary.UPPER, -10);
		System.out.println("The integral rises from zero on the left up to a horizontal asymptote on the right.");
		System.out.println("How does it look for n=3, t=50?");
		DoubleUnaryOperator function2 = (u) -> MiddleIntegrand(n, 50, u);
		int2.tabulate(function2, 516, points, boundary.UPPER, -10);
		System.out.println("It seems like for high values of t, the integral is approacging 1/3 = 0.3333...");
		System.out.println("We return to n=3, t=1.");
		System.out.println();
		
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
		System.out.println("All the below done with Simpson's rule - we tried the other one too");
		IntegratorMultiTunable int3 = IntegratorMultiTunable.Create(
				IntegratorMultiTunable.method.SIMPSONS_RULE);
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
		System.out.println("Conclusion: enlarge boundaries, from -6.5 to +4.5 (or +3.5 if you do not want n=2");
		System.out.println();

		System.out.println("Number of intervals needed for 7 decimals accuracy, dependence on n and t.");
		System.out.println("Scheme: one pass through the middle for each variable.");
		IntegratorTuner tuneIntervals = int3.CreateTuneIntervalsMulti(-6.5, 4.5, 8, 2, 1E-8, 2);
		tab1.setTransformation(tuneIntervals, 2);                //tell Tabulator that variable 2 is involved in the transformation
		tab1.tabulate(VariationScheme.ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT);
		System.out.println("Scheme: cartesian product.");
		tab1.tabulate(VariationScheme.ALL_COMBINATIONS_ZIGZAG);

		System.out.println("Strange phenomenon if we keep the boundaries -3.5 to +3, like originally found");
		System.out.println("This is a *smaller* stretch and it says we need *more* intervals, can that be true?");
		IntegratorTuner tuneIntervals2 = int3.CreateTuneIntervalsMulti(-3.5, 3.0, 8, 2, 1E-8, 2);
		tab1.setTransformation(tuneIntervals2, 2);                //tell Tabulator that variable 2 is involved in the transformation
		tab1.tabulate(VariationScheme.ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT);
		tab1.tabulate(VariationScheme.ALL_COMBINATIONS_ZIGZAG);
		
		System.out.println("Let's see it in verbose mode, for n=3, t=50, on the smaller stretch -3.5 to +3.");
		double t3 = 50;
		DoubleUnaryOperator function3 = (u) -> MiddleIntegrand(n, t3, u);
		int1.tuneIntervals(function3, -3.5, 3.0, 8, 2, 1E-8, 2, true);
		System.out.println("And compare it with how it behaves on the bigger stretch, -6.5 to +4.5");
		int1.tuneIntervals(function3, -6.5, 4.5, 8, 2, 1E-8, 2, true);

		System.out.println();
		System.out.println("***************************");
		System.out.println("Tuning the 'outer' integral");
		System.out.println("***************************");
		System.out.println();
		System.out.println("Explore integrand and integral (n=3, df=10, q=2.0), numIntervals high (516)");
		System.out.println();
		int df = 10;
		double q = 2.0;
		DoubleUnaryOperator function4 = (t2) -> OuterIntegrand(n, df, q, t2);
		points = new double[] {1E-8, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0, 6.5 };
		int2.tabulate(function4, 516, points, boundary.UPPER, 1E-8);
		System.out.println("The integrand is undefined at t = 0, but we can come really close and integrand is very small near 0.");
		System.out.println("The integral rises from 0,0 to a horizontal asymptote, that's reached at t = 3 within 3 digits.");
		System.out.println("We now try a more extreme value for each of the three parameters, while keeping the others 'in the middle'");
		System.out.println("Well if n goes up, then obviously df has to go up too to keep df > n... we keep their ratio at 1 : 3 currently.");
		System.out.println("Try: n=10, df=50, q = 2.0");
		DoubleUnaryOperator function5 = (t2) -> OuterIntegrand(10, 50, 2.0, t2);
		int2.tabulate(function5, 516, points, boundary.UPPER, 1E-8);
		System.out.println("Try: n=3, df=10, q = 5.0");
		DoubleUnaryOperator function6 = (t2) -> OuterIntegrand(3, 10, 5.0, t2);
		int2.tabulate(function6, 516, points, boundary.UPPER, 1E-8);
		System.out.println("Conclusions: n and/or df have an influence on the hight of the asymptote but not so much on when it is reached.");
		System.out.println("q = 5.0 versus 2.0 leads to the asymptote being reached later, near 6.0 instead of near 3.0 (in 3 digits).");
		System.out.println();
		
		System.out.println("Determine boundary on the upper side that fits our accuracy of 7 decimals (n=3, df=10, q=2.0).");
		int2.vanishes(function4, 516, boundary.UPPER, +8, +1.5, 0.5, 1E-8, true);
		System.out.println("Determine optimal method and number of intervals for our accuracy of 7 digits.");
		int1.tuneIntervals(function4, 1E-8, 4.5, 8, 2, 1E-8, 2, true);
		int2.tuneIntervals(function4, 1E-8, 4.5, 8, 2, 1E-8, 2, true);
		System.out.println("Conclusion: trapezoidal rule gives quickest convergence, 32 intervals is enough. Simpson needs 64.");
		System.out.println();
		System.out.println("Investigate influence of parameters (n, df, q) on results like te above.");
		System.out.println("All the below done with Trapezoid rule - TODO: try the other one too");
		System.out.println("Upper boundary");
		findVanishPL = int3.CreateFindVanishPointMulti(1024, 
				IntegratorMultiTunable.boundary.UPPER, +8, +1.5, 0.5, 1E-8);
		tab1 = new Tabulator(StudentizedRangeDistribution.class, "OuterIntegrand", 4, false);
		tab1.declareVariableInt(0, "n", new int[] { 2, 3, 5, 10});
		tab1.declareVariableInt(1, "df", new int[] { 10, 25, 50, 100});
		tab1.declareVariableDouble(2, "q", new double[] { 0.2, 1.5, 3.0, 4.5, 6.0 });
		tab1.declareVariableDouble(3, "t", new double[] { 1 });  //dummy integration variable, value not used
		tab1.setTransformation(findVanishPL, 3);                 //tell Tabulator that variable 2 is involved in the transformation
		tab1.tabulate(VariationScheme.ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT);
		System.out.println("Upper boundary seems an almost linear function of q: y = 2 + x");
		System.out.println("Upper boundary does not depend on n");
		System.out.println("Upper boundary depends a bit on df, it goes down (-2) when df goes up (*10).");

		//TODO 2: on to the outer integral where the parameters DO play a role according to the Fortran authors. But they do not state which role.
	}
}
