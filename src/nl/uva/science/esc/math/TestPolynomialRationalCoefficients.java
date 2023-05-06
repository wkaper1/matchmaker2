package nl.uva.science.esc.math;

public class TestPolynomialRationalCoefficients {

	/**
	 * Tests for the PolynomialRationalCoefficients class
	 * The tests currently are aimed quite a bit at the use that this class will get in the BetaFunction class.
	 */
	public static void main(String[] args) {
		//Use the binomium-factory methods to create some versions of y = (1 - x)^b
		PolynomialRationalCoefficients p2 = PolynomialRationalCoefficients.CreateFromBinomialInts(1, -1, 2);
		System.out.println("(1 - x)^2 =>  " + p2.toString());
		PolynomialRationalCoefficients p3 = PolynomialRationalCoefficients.CreateFromBinomialInts(1, -1, 3);
		System.out.println("(1 - x)^3 =>  " + p3.toString());
		PolynomialRationalCoefficients p4 = PolynomialRationalCoefficients.CreateFromBinomialInts(1, -1, 4);
		System.out.println("(1 - x)^4 =>  " + p4.toString());
		//Try binomium with rationals just a little bit
		PolynomialRationalCoefficients pHalves4 = PolynomialRationalCoefficients.CreateFromBinomial(
				new RationalNumber(1, 2), new RationalNumber(-1, 2), 4);
		System.out.println("(1/2 - x/2)^4 =>  " + pHalves4.toString());
		//Try division by a rational
		p3 = p3.DivideBy(new RationalNumber(2, 1));
		p4 = p4.DivideBy(new RationalNumber(2, 1));
		System.out.println("((1 - x)^4) /2 =>  " + p4.toString());
		//Adding or subtracting two polynomials
		PolynomialRationalCoefficients p3Plus4 = p3.Add(p4);
		System.out.println("p3 + p4 =>  " + p3Plus4.toString());
		PolynomialRationalCoefficients p3Minus4 = p3.Subtract(p4);
		System.out.println("p3 - p4 =>  " + p3Minus4.toString());
	}

}
