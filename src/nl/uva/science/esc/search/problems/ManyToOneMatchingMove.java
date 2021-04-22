package nl.uva.science.esc.search.problems;

/**
 * Deterministic Move while solving the ManyToOneMatching problem
 * In this problem, a Move consists of adding a single A to an empty place
 * @author kaper
 *
 */
public class ManyToOneMatchingMove implements Move {
	private int addableA;    //id of A to add to a place
	private int placetofill; //place to add this A to
	
	public ManyToOneMatchingMove(int addableA, int placetofill) {
		this.addableA = addableA;
		this.placetofill = placetofill;
	}//end ManyToOneMatchingMove
	
	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.Move#retreat(nl.uva.science.esc.search.problems.DeterministicSearchProblem)
	 */
	@Override
	public int[] getMoveInfo() {
		return new int[] {addableA, placetofill};
	}//end retreat
	
	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.Move#getLevel(nl.uva.science.esc.search.problems.DeterministicSearchProblem)
	 */
	@Override
	public int getLevel() {
		return addableA;
	}//end getLevel
	
	/**
	 * @return placetofill
	 */
	public int getPlacetofill() {
		return placetofill;
	}//end getPlacetofill
	
	/**
	 * Generic problem-unspecific UI elements could use this to display a current
	 * or best solution to a DeteministicSearchProblem
	 * @return readable representation of object
	 */
	public String toString() {
		return "addableA: "+addableA+", placetofill: "+placetofill;
	}//end toString
	
}//end class
