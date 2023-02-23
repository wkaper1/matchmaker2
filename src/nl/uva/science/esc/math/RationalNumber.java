package nl.uva.science.esc.math;

/**
 * Represents a rational number as two integers.
 * Enables postponing of rounding. E.g. you could cache the outcome of a calculation
 * under integer keys in a HashMap.
 * @author wkaper1
 */
public class RationalNumber {
	private int numerator;
	private int denominator;
	
	public RationalNumber(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	//operations that modify this object
	
	public void ToThisAddInt(int n) {
		numerator += n * denominator;
	}
	
	public void MultiplyThisBy(RationalNumber r) {
		numerator = numerator * r.numerator;
		denominator = denominator * r.denominator;
	}
	
	public void DivideThisBy(RationalNumber r) {
		numerator = numerator * r.denominator;
		denominator = denominator * r.numerator;
	}
	
	//operations that do not modify 'this' but return a new Rational
	
	public RationalNumber AddInt(int n) {
		return new RationalNumber(numerator + n * denominator, denominator);
	}
	
	public RationalNumber MultiplyBy(RationalNumber r) {
		return new RationalNumber(numerator * r.numerator, denominator * r.denominator);
	}
	
	public RationalNumber DivideBy(RationalNumber r) {
		return new RationalNumber(numerator * r.denominator, denominator * r.numerator);
	}
	
	//operations that take a double and result in a double
	
	public double MultiplyGivenDoubleByThis(double d) {
		return d * numerator / denominator;
	}
	
	public double DivideGivenDoubleByThis(double d) {
		return d * denominator / numerator;
	}
	
	public double toDouble() {
		return ((double)numerator) / denominator;
	}
	
	//simple getters and other housekeeping methods
	
	public int numerator() {
		return this.numerator;
	}
	
	public int denominator() {
		return this.denominator;
	}
	
	public RationalNumber clone() {
		return new RationalNumber(numerator, denominator);
	}
}