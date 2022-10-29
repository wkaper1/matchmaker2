package nl.uva.science.esc.search.problems;

/**
 * This represents the State of a ManyToOne Matching Problem.
 * In this problem, a state is a (partial) filling of B-places by A's
 * 
 * Please note: the places field may contain a reference to a live array
 * being processed in the Problem class, so you may need to start by making a 
 * clone for safety.
 * 
 * Reporting is delegated to the "mother" Problem object of this State.
 * Such a decision is taken per Problem subclass, so the obligation to
 * report itself is in general with the State, it just MIGHT delegate it.
 * 
 * @author kaper
 *
 */
public class ManyToOneMatchingState implements State {
	
	//the state of a ManyToOneMatchingProblem:
	private int[] places;
	private boolean[] isAmatched;
	private int numAToMatch;
	
	//reference to live mother Problem object, for delegating the reporting
	private ManyToOneMatchingProblem p;  //it might still be a subclass!
	
	public ManyToOneMatchingState(int[] places, boolean[] isAmatched, int numAToMatch, ManyToOneMatchingProblem p) {
		this.places = places;
		this.isAmatched = isAmatched;
		this.numAToMatch = numAToMatch;
		this.p = p;
	}//end constructor
	
	public int[] getPlaces() {
		return places;
	}//end getPlaces
	
	public boolean[] isAmatched() {
		return isAmatched;
	}//end isAmatched
	
	public int getNumAToMatch() {
		return numAToMatch;
	}
	
	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.State#stateClone()
	 */
	@Override
	public State stateClone() {
		int[] placesCp = new int[places.length];
		boolean[] isAmatchedCp = new boolean[isAmatched.length];
		System.arraycopy(places, 0, placesCp, 0, places.length);
		System.arraycopy(isAmatched, 0, isAmatchedCp, 0, isAmatched.length);
		return new ManyToOneMatchingState(placesCp, isAmatchedCp, numAToMatch, p);
	}//end stateClone
	
	//Following 4 State-reports are all delegated to ManyToOneMatchingProblem

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.State#showDetails()
	 */
	@Override
	public String[][] showDetails() {
		return p.showStateDetails(this);
	}//end showDetails

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.State#showSummary()
	 */
	@Override
	public String[][] showSummary() {
		return p.showStateSummary(this);
	}//end showSummary

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.State#showDetailsHeadings()
	 */
	@Override
	public String[] showDetailsHeadings() {
		return p.showStateDetailsHeadings();
	}//end showDetailsHeadings

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.State#showSummaryHeadings()
	 */
	@Override
	public String[] showSummaryHeadings() {
		return p.showStateSummaryHeadings();
	}//end showSummaryHeadings

}//end class
