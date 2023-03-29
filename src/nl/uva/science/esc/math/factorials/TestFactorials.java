package nl.uva.science.esc.math.factorials;

import java.math.BigInteger;
import nl.uva.science.esc.math.RationalNumber;

public class TestFactorials {

	public static void main(String[] args) throws Exception {
		//factorial, two versions
		BigInteger rs;
		for (int n=100; n>0; n--) {
			rs = Factorials.factorial(n);
			System.out.println("n=" + n + ", n!=" + rs);
		}
		System.out.println();
		
		for (int n=100; n>0; n--) {
			rs = Factorials.factorial2(n);
			System.out.println("n=" + n + ", n!=" + rs);
		}
		System.out.println();
		//We can conclude that long is not long enough!
		//  fixing that is outside the scope.
		
		//gammaFactor, two versions
		double rs2;
		for (int n=100; n>0; n--) {
			rs2 = Factorials.gammaFactor(n);
			System.out.println("n=" + n + ", gammaFactor=" + rs2);
		}
		System.out.println();
		
		for (int n=100; n>0; n--) {
			rs2 = Factorials.gammaFactor2(n);
			System.out.println("n=" + n + ", gammaFactor=" + rs2);
		}
		System.out.println();
		
		//gammaOfRational, only the smart-caching version
		//test 1: build up the cache slowly, restarting the series after each term
		RationalNumber r0 = new RationalNumber(3, 2);
		double gamma0 = Factorials.gammaOfRational(r0);
		System.out.println("n/2=3/2, gamma=" + gamma0);
		r0 = new RationalNumber(5, 2);
		gamma0 = Factorials.gammaOfRational(r0);
		System.out.println("n/2=5/2, gamma=" + gamma0);
		r0 = new RationalNumber(7, 2);
		gamma0 = Factorials.gammaOfRational(r0);
		System.out.println("n/2=7/2, gamma=" + gamma0);
		System.out.println();
		
		//Show Gamma(n/2) series
		//test 2: from high to low, all the work is done to get the first term
		//  after that, the lower terms require just a lookup plus something.
		for (int n=12; n>0; n--) {
			RationalNumber r = new RationalNumber(n, 2);
			double gamma = Factorials.gammaOfRational(r);
			System.out.println("n/2=" + n + "/2, gamma=" + gamma);
		}
		System.out.println();
		
		//Gamma(n/3):
		for (int n=18; n>0; n--) {
			RationalNumber r = new RationalNumber(n, 3);
			double gamma = Factorials.gammaOfRational(r);
			System.out.println("n/3=" + n + "/3, gamma=" + gamma);
		}
		System.out.println();
		
		//Now ask for 13/2, a member of the n/2 series
		//Check in debug mode that he uses the cache and enlarges it by just 1 step, no more.
		RationalNumber r = new RationalNumber(13, 2);
		double gamma = Factorials.gammaOfRational(r);
		System.out.println("n/2=13/2, gamma=" + gamma);
		r = new RationalNumber(14, 2);
		gamma = Factorials.gammaOfRational(r);
		System.out.println("n/2=14/2, gamma=" + gamma);
	}
}
