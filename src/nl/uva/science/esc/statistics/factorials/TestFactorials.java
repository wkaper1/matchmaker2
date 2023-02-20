package nl.uva.science.esc.statistics.factorials;

import java.io.Console;

public class TestFactorials {

	public static void main(String[] args) {
		long rs;
		for (int n=100; n>0; n--) {
			rs = Factorials.factorial(n);
			System.out.println("n=" + n + ", n!=" + rs);
		}
		
		for (int n=100; n>0; n--) {
			rs = Factorials.factorial2(n);
			System.out.println("n=" + n + ", n!=" + rs);
		}
		//We can conclude that long is not long enough!
		//  fixing that is outside the scope.
		
		double rs2;
		for (int n=100; n>0; n--) {
			rs2 = Factorials.gammaFactor(n);
			System.out.println("n=" + n + ", gammaFactor=" + rs2);
		}
		
		for (int n=100; n>0; n--) {
			rs2 = Factorials.gammaFactor2(n);
			System.out.println("n=" + n + ", gammaFactor=" + rs2);
		}
	}
}
