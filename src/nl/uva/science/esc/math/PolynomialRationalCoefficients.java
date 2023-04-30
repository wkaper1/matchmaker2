package nl.uva.science.esc.math;

import nl.uva.science.esc.math.factorials.Factorials;

/**
 * Represents a polynomial with RationalNumbers as coefficients.
 * This can be useful e.g. if you want to add many of such polynomials and postpone rounding
 * till after the additions are done with exact math. Take care of the possibly
 * big memory consumption!
 * @author Wolter2
 *
 */
public class PolynomialRationalCoefficients {
	private RationalNumber[] coefficients;
	
	/**
	 * Constructor, requires an array of coefficients
	 */
	public PolynomialRationalCoefficients(RationalNumber[] coefficients) {
		this.coefficients = coefficients;
	}
	
	/**
	 * Constructor, uses the binomial expansion for (A + X)^N to construct this polynomial
	 * @param BinomialA, the binomial term A from (A + B)^N
	 * @param BinomialB, the binomial term A from (A + B)^N
	 * @param N, the integer power N from (A + B)^N
	 */
	public PolynomialRationalCoefficients(RationalNumber BinomialA, RationalNumber BinomialB, int N) {
		this.coefficients = new RationalNumber[N+1];
		for (int i=0;i<coefficients.length;i++) {
			RationalNumber powerfactor = BinomialA.ToThePowerOf(N - i).MultiplyBy(BinomialB.ToThePowerOf(i));
			coefficients[i] = new RationalNumber(
					Factorials.factorial2(N), 
					Factorials.factorial2(N - i).multiply(Factorials.factorial2(i)));
			coefficients[i].MultiplyThisBy(powerfactor);
		}
	}
	
	/**
	 * Add this polynomial and the given polynomial, return result as new polynomial
	 */
	public PolynomialRationalCoefficients Add(PolynomialRationalCoefficients p) {
		return new PolynomialRationalCoefficients(AddCoeffs(p));
	}

	/**
	 * Let this polynomial become the sum of this and the given polynomial
	 */
	public void ToThisAdd(PolynomialRationalCoefficients p) {
		this.coefficients = AddCoeffs(p);
	}

	private RationalNumber[] AddCoeffs(PolynomialRationalCoefficients p) {
		RationalNumber[] newCoeffs = new RationalNumber[Math.max(this.coefficients.length, p.coefficients.length)];
		int commonPart = Math.min(this.coefficients.length, p.coefficients.length);
		for (int i=0;i<=commonPart;i++) {
			newCoeffs[i] = this.coefficients[i].Add(p.coefficients[i], false);
		}
		if (newCoeffs.length > commonPart) {
			if (this.coefficients.length > commonPart) {
				for (int i=commonPart + 1; i<newCoeffs.length; i++) {
					newCoeffs[i] = this.coefficients[i];
				}
			}
			else {
				for (int i=commonPart + 1; i<newCoeffs.length; i++) {
					newCoeffs[i] = p.coefficients[i];
				}				
			}
		}
		return newCoeffs;
	}
	
	public PolynomialRationalCoefficients MultiplyBy(RationalNumber r) {
		RationalNumber[] newCoeffs = new RationalNumber[coefficients.length];
		for (int i=0;i<coefficients.length;i++) {
			newCoeffs[i] = coefficients[i].MultiplyBy(r);
		}
		return new PolynomialRationalCoefficients(newCoeffs);
	}
	
	public void MultiplyThisBy(RationalNumber r) {
		for (int i=0;i<coefficients.length;i++) {
			coefficients[i] = coefficients[i].MultiplyBy(r);
		}		
	}
	
	public PolynomialRationalCoefficients DivideBy(RationalNumber r) {
		return this.MultiplyBy(r.inverse());
	}
	
	public void DivideThisBy(RationalNumber r) {
		this.MultiplyThisBy(r.inverse());
	}
	
	/**
	 * Evaluate this polynomial for the given double value of the independent variable 
	 * @param x, the independent variable
	 * @return, the value of the polynomial, as a double
	 */
	public double eval(double x) {
		double val = 0;
		if (coefficients.length>0) {
			val = coefficients[0].toDouble();
		}
		if (coefficients.length>1) {
			val += coefficients[1].MultiplyGivenDoubleByThis(x);
		}
		for (int i=2;i<coefficients.length;i++) {
			val += coefficients[1].MultiplyGivenDoubleByThis(Math.pow(x, i));
		}
		return val;
	}
	
	/**
	 * Evaluate this polynomial for the given RationalNumber value of the independent variable 
	 * @param x, the independent variable
	 * @return, the value of the polynomial, as a RationalNumber
	 */
	public RationalNumber eval(RationalNumber x) {
		RationalNumber val;
		if (coefficients.length>0) {
			val = coefficients[0].clone();
		}
		else {
			val = new RationalNumber(0, 1);
		}
		if (coefficients.length>1) {
			val.Add(x.MultiplyBy(coefficients[1]), false);
		}
		for (int i=2;i<coefficients.length;i++) {
			val.Add(x.ToThePowerOf(i).MultiplyBy(coefficients[1]), false); 
		}
		return val;
	}
	
	/**
	 * Returns a readable representation of the polynomial.
	 */
	public String toString() {
		String str = "";
		if (coefficients.length>0) {
			str += coefficients[0].toString();
		}
		if (coefficients.length>1) {
			str += " + " + coefficients[1] + ".x";
		}
		for (int i=2;i<coefficients.length;i++) {
			str += " + " + coefficients[i] + ".x^" + i;
		}
		return str;
	}
}