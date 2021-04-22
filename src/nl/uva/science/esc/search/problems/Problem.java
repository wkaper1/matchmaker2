package nl.uva.science.esc.search.problems;

/**
 * @author kaper
 *
 */
public interface Problem {

	/**
	 * Get the cost of the current state, which is the measure of the 
	 * desirableness of this state as a possible solution: the lower the better
	 * @return the cost
	 */
	public long getCost();
	
	/**
	 * Return the current state as one readable text, e.g. a table.
	 * Use it for debugging or showing the final (best) state. 
	 * Each concrete problem class will need problem-specific output mechanisms
	 * as well which we cannot declare here.
	 * 
	 * This does not seem to be general enough... We probably make it a method
	 * of the state itself, to be able to show states stored outside the problem
	 */
	public String showState();

	/**
	 * Return a reference to the current state of the running problem
	 * Be careful to use it only for reading
	 * @return reference to live problem state
	 */
	public State getState();
	
	/**
	 * Replace the state as it exists inside the problem object by the given
	 * State. States are problem-specific. As long as we are working on one
	 * problem at a time that should not present difficulties.
	 * @param s the given State
	 */
	public void setState(State s);
	
}//end interface
