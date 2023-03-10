package nl.uva.science.esc.math;

import nl.uva.science.esc.math.Tabulator.VariationScheme;

public class TestTabulator {

	public static void main(String[] args) throws Exception {
		Tabulator tab1 = new Tabulator(
				"nl.uva.science.esc.statistics.distributionsequal.StudentizedRangeDistribution", 
				"MiddleIntegrand", 3);
		tab1.declareVariableInt(0, "n", new int[] { 2, 3, 5 });
		tab1.declareVariableDouble(1, "t", new double[] { 2, 10, 25 });
		tab1.declareVariableDouble(2, "u", new double[] { 3, 11, 27 });
		
		tab1.tabulate(VariationScheme.ONE_PASS_PER_VARIABLE_OTHERS_AT_MIDPOINT);
		tab1.tabulate(VariationScheme.ALL_COMBINATIONS_ZIGZAG);
	}

}
