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
		//Show Gamma(n/2) series
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
	}
}
