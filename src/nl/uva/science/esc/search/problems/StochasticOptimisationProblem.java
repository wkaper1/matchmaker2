package nl.uva.science.esc.search.problems;

/**
 * Problem stated to make it fit for stochastic optimisation techniques:
 * simulated annealing, local beam search, genetic technique
 * 
 * In a stochastic optimisation problem, each state is a goal state, so no
 * partial solutions are considered.
 * The aim is to find a state with the lowest cost.
 * @author kaper
 *
 */
public interface StochasticOptimisationProblem extends Problem {
	
	/**
	 * Initialise the class to a valid state
	 * A valid state of an optimisation problem is an acceptable but not
	 * necessarily optimal solution.
	 * (In terms of deterministic search: it would pass the goal test)
	 * @throws Exception 
	 */
	public void initGoalState() throws Exception;
	
	/**
	 * Generate a proposal for a random change in the current state
	 */
	public void generateRandomMove();
	
	/**
	 * Get the change in cost that the proposed move would cause, if accepted
	 * @return change in cost
	 */
	public long getDeltaCostRandomMove();
	
	/**
	 * Accept the proposed move
	 * If the move is accepted, the current state is changed, not earlier
	 * In case of non-acceptance nothing is called ...(so no cleanup assumed needed)
	 */
	public void acceptMove();
	
}//end interface
