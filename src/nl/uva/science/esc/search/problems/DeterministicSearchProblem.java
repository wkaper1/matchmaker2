package nl.uva.science.esc.search.problems;

/**
 * A DeterministicSearchProblem is a Problem stated such that it can be solved
 * by all of the deterministic tree-search techniques: 
 * breadth-first, depth-first and A-star. 
 * @author kaper
 *
 */
public interface DeterministicSearchProblem extends Problem {
	
	/**
	 * Generate the initial state, which is the start of all search paths
	 */
	public void initState();
	
	/**
	 * Generate the next state
	 * Russell and Norwig call it the "successor function"
	 * If all possibilities have been tried at the current node it returns false
	 * We then need a retreat to continue the search.
	 * @return success
	 */
	public boolean generateDeterministicMove(int level);	

	/**
	 * Get the change in cost that the proposed move causes
	 * This method might be redundant, but it is used for efficiency's sake
	 * @return change in cost
	 */
	public long getDeltaCostDeterministicMove();
	
	/**
	 * Execute the move planned earlier, i.e. move this problem to the new state
	 * This move leads to a new node in the search tree, 
	 * i.e. it should not have a search history.
	 * Return the Move made, to be recorded in the MoveStack
	 * @return the Move
	 */
	public Move doForwardMove();
	
	/**
	 * Takes an earlier stored Move and execute it in reverse
	 * This returns us to an existing node, including its previous tries
	 * (to enable us not to repeat those)
	 * @param m move to reverse
	 */
	public void retreatMove(Move m);
	
	/**
	 * Is the current state acceptable as a (not necessarily optimal) solution?
	 * @param level, number of Moves that make up the solution (length of MoveStack)
	 * @return goal?
	 */
	public boolean goalTest(int level);
	
}//end interface
