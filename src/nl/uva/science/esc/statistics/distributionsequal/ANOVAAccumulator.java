package nl.uva.science.esc.statistics.distributionsequal;

import java.util.*;

/**
 * Mean and Variance are accumulated on two levels, as preparation for ANOVA testing:
 * - individual measurements (double) are accumulated at the group level
 * - group results are summarized in preparation for ANOVA
 * Aim is to find out whether one or more of the groups have a mean that differs 
 * significantly from the overall mean.
 * The groups are variously referred to as 'treatments' or 'factors', the groups may have 
 * received different preparation. The within group-variation however, should be independent.
 * (E.g. you can't make two 'groups' by observing the same people at 2 different points
 * in time)
 * @author wkaper1
 *
 */
public class ANOVAAccumulator {
	//probably unnecessary?
	private List<MeanAndVarianceAccumulator> accus = new ArrayList<MeanAndVarianceAccumulator>();
	//results at the ANOVA-level
	private int numGroups = 0;
	private int numElements = 0;
	private double betweenGroupsSumOfSquares = 0; //before subtraction of term involving overall mean
	private double overallSum = 0;                //numerator of the overall mean!
	private double withinGroupsUndividedVariance = 0;
	
	public void addGroup(Collection<Double> group) throws Exception {
		MeanAndVarianceAccumulator accu = MeanAndVarianceAccumulator.Create();
		accu.addRange(group);
		numGroups++;
		int count = accu.count();
		double mean = accu.mean();
		numElements += count;
		betweenGroupsSumOfSquares += count * Math.pow(mean, 2);
		overallSum += count * mean;
		withinGroupsUndividedVariance += accu.undividedVariance();
		
		//needed? yes! not for ANOVA but for follow-up: which ones deviate significantly?
		accus.add(accu);
	}
	
	//used in main ANOVA test
	
	public double betweenGroupsMeanSquares() {
		//the second term is equal to, and usually formulated as: numElements * overallMean^2
		return (betweenGroupsSumOfSquares - Math.pow(overallSum, 2) / numElements) / (numGroups - 1);
	}
	
	public double betweenGroupsDegreesOfFreedom() {
		return numGroups - 1;
	}
	
	public double withinGroupsMeanSquares() {
		return withinGroupsUndividedVariance / (numElements - numGroups);
	}
	
	public double withinGroupsDegreesOfFreedom() {
		return numElements - numGroups;
	}
	
	public double FStatistic() {
		return betweenGroupsMeanSquares() / withinGroupsMeanSquares();
	}
	
	//used in follow-up Tukey's HSD-tests
}
