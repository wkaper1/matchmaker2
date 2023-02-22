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
	
	public double MultiplyGivenDoubleByThis(double d) {
		return d * numerator / denominator;
	}
	
	public double DivideGivenDoubleByThis(double d) {
		return d * denominator / numerator;
	}
	
	public double toDouble() {
		return ((double)numerator) / denominator;
	}
	
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