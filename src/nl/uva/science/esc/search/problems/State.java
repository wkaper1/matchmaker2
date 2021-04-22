package nl.uva.science.esc.search.problems;

/**
 * Interface State
 * A State object contains the state of a problem.
 * A state is a partial and not necessarily optimal solution.
 * Moves (in comparison) are State-changes, while State objects contain the
 * totality of a state.
 *
 * States have internals that are Problem-specific, but this interface
 * contains the general methods that the Technique and View layers need
 * @author kaper
 *
 */
public interface State {
	
	/**
	 * Returns a full independent copy of this State, 
	 * used by: Techniques and Views
	 * @return the copy;
	 */
	public State stateClone();
	
	/**
	 * Fully describe the state in a n x m table format. Used by: Views
	 * @return
	 */
	public String[][] showDetails();
	
	/**
	 * Summarize the state in an n x m table format. Used by: Views
	 * @return
	 */
	public String[][] showSummary();
	
	/**
	 * Show headings for the details table - should be constant when running
	 * @return
	 */
	public String[] showDetailsHeadings();
	
	/**
	 * Show headings for the summary table - constant during a run
	 * @return
	 */
	public String[] showSummaryHeadings();

}//end interface
