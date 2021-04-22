package nl.uva.science.esc.search.problems;

/**
 * A move made in a DeterministicSearchProblem
 * When it's time to retreat back to a previous state, the stored
 * move is consulted to realise the retreat.
 * In depth-first search, such moves are stored as a solution path in the 
 * MovesStack. In breadth-first search, each move is reversed immediately
 * after it was done.
 * @author kaper
 *
 */
public interface Move {
	
	/**
	 * Return the move data as an array
	 * Implementing classes might offer more specific getters...
	 */
	public int[] getMoveInfo();
	
	/**
	 * Get the level at which the move started
	 * @return level
	 */
	public int getLevel();
	
	/**
	 * Generic problem-unspecific UI elements could use this to display a current
	 * or best solution to a DeteministicSearchProblem
	 * @return readable representation of object
	 */
	public String toString();

}//end interface
