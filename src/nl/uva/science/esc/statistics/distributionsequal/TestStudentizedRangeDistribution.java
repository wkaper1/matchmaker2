package nl.uva.science.esc.statistics.distributionsequal;

public class TestStudentizedRangeDistribution {

	public static void main(String[] args) throws Exception {
		StudentizedRangeDistribution.tuningReport();
		StudentizedRangeDistribution.tabulateDistribution(true); //ask complement? for reference with Harter et al.
		testForDifferencesWithHarter();
	}
	
	public static void testForDifferencesWithHarter() throws Exception {
		double P;
		System.out.println("Expected P = 0.9000, in 4 to 6 decimals");
		P = StudentizedRangeDistribution.Distribution(2, 1, 8.929, true); //Expected P = 0.90
		System.out.println("(n, df, q) = (2, 1, 8.929) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(2, 10, 2.563, true); //Expected P = 0.90
		System.out.println("(n, df, q) = (2, 10, 2.563) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(2, 120, 2.344, true); //Expected P = 0.90
		System.out.println("(n, df, q) = (2, 120, 2.344) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(10, 1, 24.48, true); //Expected P = 0.90
		System.out.println("(n, df, q) = (10, 1, 24.48) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(10, 10, 4.913, true); //Expected P = 0.90
		System.out.println("(n, df, q) = (10, 10, 4.913) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(10, 120, 4.191, true); //Expected P = 0.90
		System.out.println("(n, df, q) = (10, 120, 4.191) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(100, 1, 39.91, true); //Expected P = 0.90
		System.out.println("(n, df, q) = (100, 1, 39.91) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(100, 10, 7.396, true); //Expected P = 0.90
		System.out.println("(n, df, q) = (100, 10, 7.396) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(100, 120, 5.960, true); //Expected P = 0.90
		System.out.println("(n, df, q) = (100, 120, 5.960) => P=" + P);
		System.out.println();
		System.out.println("Expected P = 0.9990, in 4 to 6 decimals");
		P = StudentizedRangeDistribution.Distribution(2, 10, 6.487, true); //Expected P = 0.999
		System.out.println("(n, df, q) = (2, 10, 6.487) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(2, 120, 4.771, true); //Expected P = 0.999
		System.out.println("(n, df, q) = (2, 120, 4.771) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(10, 10, 9.769, true); //Expected P = 0.999
		System.out.println("(n, df, q) = (10, 10, 9.769) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(10, 120, 6.206, true); //Expected P = 0.999
		System.out.println("(n, df, q) = (10, 120, 6.206) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(50, 60, 7.671, true); //Expected P = 0.999
		System.out.println("(n, df, q) = (50, 60, 7.671) => P=" + P);
		P = StudentizedRangeDistribution.Distribution(50, 120, 7.296, true); //Expected P = 0.999
		System.out.println("(n, df, q) = (50, 120, 7.296) => P=" + P);
	}

}
