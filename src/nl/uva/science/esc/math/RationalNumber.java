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