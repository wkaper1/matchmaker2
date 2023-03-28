package nl.uva.science.esc.math;

import java.math.*;

/**
 * Represents a rational number as two integers.
 * Enables postponing of rounding. E.g. you could cache the outcome of a calculation
 * under integer keys in a HashMap.
 * @author wkaper1
 */
public class RationalNumber {
	private BigInteger numerator;
	private BigInteger denominator;
	
	public RationalNumber(BigInteger numerator, BigInteger denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public RationalNumber(long numerator, long denominator) {
		this.numerator = BigInteger.valueOf(numerator);
		this.denominator = BigInteger.valueOf(denominator);
	}
	
	//operations that modify this object
	
	public void ToThisAddInt(long n) {
		numerator = numerator.add(denominator.multiply(BigInteger.valueOf(n)));
	}
	
	public void MultiplyThisBy(RationalNumber r) {
		numerator = numerator.multiply(r.numerator);
		denominator = denominator.multiply(r.denominator);
	}
	
	public void DivideThisBy(RationalNumber r) {
		numerator = numerator.multiply(r.denominator);
		denominator = denominator.multiply(r.numerator);
	}
	
	public void MultiplyThisByInt(long n) {
		numerator = numerator.multiply(BigInteger.valueOf(n));
	}
	
	public void DivideThisByInt(long n) {
		denominator = denominator.multiply(BigInteger.valueOf(n));
	}
	
	public void SimplifyThis() {
		BigInteger gcd = numerator.gcd(denominator);
		numerator = numerator.divide(gcd);
		denominator = denominator.divide(gcd);
	}
	
	//operations that do not modify 'this' but return a new Rational
	
	public RationalNumber AddInt(int n) {
		return new RationalNumber(numerator.add(denominator.multiply(BigInteger.valueOf(n))), denominator);
	}
	
	public RationalNumber MultiplyBy(RationalNumber r) {
		return new RationalNumber(numerator.multiply(r.numerator), denominator.multiply(r.denominator));
	}
	
	public RationalNumber DivideBy(RationalNumber r) {
		return new RationalNumber(numerator.multiply(r.denominator), denominator.multiply(r.numerator));
	}
	
	//operations that take a double and result in a double
	
	public double MultiplyGivenDoubleByThis(double d) {
		return d * ((new BigDecimal(numerator)).divide(new BigDecimal(denominator), 17, RoundingMode.HALF_EVEN)).doubleValue();
	}
	
	public double DivideGivenDoubleByThis(double d) {
		return d * ((new BigDecimal(denominator)).divide(new BigDecimal(numerator), 17, RoundingMode.HALF_EVEN)).doubleValue();
	}
	
	public double toDouble() {
		return ((new BigDecimal(numerator)).divide(new BigDecimal(denominator), 17, RoundingMode.HALF_EVEN)).doubleValue();
	}
	
	//integer logic operations
	
	/**
	 * Return simplified version of this rational number
	 */
	public RationalNumber simplify() {
		BigInteger gcd = numerator.gcd(denominator);
		return new RationalNumber(numerator.divide(gcd), denominator.divide(gcd));
	}

	/**
	 * Shortcut: denominator equals one?
	 */
	public boolean isInteger() {
		return denominator.equals(BigInteger.ONE);
	}
	
	private BigInteger[] integerAndRemainder = numerator.divideAndRemainder(denominator);
	
	/**
	 * Call this to initialize the integer quotient.
	 * After that, you can inquire into various of its properties. Call this method again after the instance been modified!
	 */
	public void integerQuotientInit() {
		integerAndRemainder = numerator.divideAndRemainder(denominator);
	}
	
	public long integerQuotientAsLong() {
		return integerAndRemainder[0].longValueExact();
	}
	
	public RationalNumber integerQuotientRemainingRational() {
		return new RationalNumber(integerAndRemainder[1], denominator);
	}
	
	public long integerQuotientRemainderAsLong() {
		return integerAndRemainder[1].longValueExact();
	}
	
	/**
	 * Check if the remainder is zero. Just a shortcut.
	 */
	public boolean integerQuotientIsInteger() {
		return integerAndRemainder[1].longValueExact() == 0;
	}
	
	//simple getters and other housekeeping methods
	
	public BigInteger numerator() {
		return this.numerator;
	}
	
	public BigInteger denominator() {
		return this.denominator;
	}
	
	public String toString() {
		return numerator.toString() + " / " + denominator.toString();
	}
	
	public RationalNumber clone() {
		return new RationalNumber(numerator, denominator);
	}
}