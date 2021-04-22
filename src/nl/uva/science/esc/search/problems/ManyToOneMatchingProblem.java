package nl.uva.science.esc.search.problems;

import java.lang.Math;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.uva.science.esc.matchmaker.Controller.InputType;
import nl.uva.science.esc.search.views.Parameter;

/**
 * Each A is to be matched to exactly one B
 * Reversely, a B can be matched to zero or more A's.
 * ...up to a maximum which is different per individual B.
 * The sum of these maxima has to be larger than or equal to the number of A's,
 * otherwise a solution does not exist.
 * 
 * Each A-B pair has a preference number assigned to it (with the lowest
 * numbers indicating the best preferences).
 * As every A has to find a match, we have a known number of matches to make.
 * The problem is to find the set of matches, where the sum of the realized
 * preferences is minimal.
 * 
 * This is a base class that has at least one extension. 
 * @author kaper
 */
public class ManyToOneMatchingProblem 
		implements DeterministicSearchProblem, StochasticOptimisationProblem 
		{
	
	//Each B has a number of places to offer for A's.
	//The places array contains a filling of places by A-ids.
	//This represents the current problem state.
	private int places[];
	
	//Move: plan for the next change
	//Plan for a stochastic move: swap the contents of two places
	private int swapplace1;
	private int swapplace2;
	//Plan for a deterministic move: add an A to an empty place
	private int addableA;
	private int placetofill;
	
	//Fixed problem data
	protected int BPlaces[]; //For each place: to which B does it belong.
	protected int ABPreferences[][]; //For each A,B pair the original preference is given: for reporting.
	protected long ABPreferencesT[][]; //For each A,B pair the transformed preference is given: for use in optimizing
	protected int numberOfAs;  //How many As are there
	
	//constants
	public static final int EMPTYPLACE = 9999; //should not be an A-id
	private static final int NOPLANYET = 9999; //should not be an A-id and
	  //not a place number either

	/**
	 * Specific constructor
	 * @param BMax, for each B: number of As that can be related to it
	 *    a.k.a. "number of places"
	 * @param ABPreferences, preference of each A,B pair
	 * @param transformprefs, transformation to do on the preference values
	 *    use it e.g. to increase the penalty for relatively unwanted combi's
	 * @param numberOfAs, How many As are there
	 */
	public ManyToOneMatchingProblem(
		int[] BMax, int[][] ABPreferences, String transformprefs, int numberOfAs
	) {
		initPlacesAndPlans(BMax);
		this.ABPreferences = ABPreferences;
		this.ABPreferencesT = this.transformPrefs(ABPreferences, transformprefs);
		this.numberOfAs = numberOfAs;
	}//end constructor
	
	/**
	 * No-argument constructor
	 */
	protected ManyToOneMatchingProblem() {
	}//end constructor
	
	/**
	 * Normalised constructor for this class, used by the GUI via the ProblemFactory
	 * @param c, a specific Connector for this problem type
	 * @param parametervalues, parameters we want from the GUI, as advertised below
	 * @return the object
	 */
	public ManyToOneMatchingProblem(
		ProblemConnector c, String[] parametervalues
	) {
		ManyToOneMatchingProblemConnector sc = (ManyToOneMatchingProblemConnector) c;
		initPlacesAndPlans( sc.getBMax() );
		this.ABPreferences = sc.getABPreferences();
		this.ABPreferencesT = transformPrefs(ABPreferences, parametervalues[0]);
		this.numberOfAs = sc.getnumberOfAs();
	}//end StudentProjectMatchingProblemFactory
	
	/**
	 * This protected method is really part of the "shared constructor"
	 * @param BMax, maximum number of A's for each B
	 */
	protected void initPlacesAndPlans(int[] BMax) {
		//Find out the number of places as well as to which B they belong
		int i=0;
		for(int j=0; j<BMax.length; j++) {
			i += BMax[j];
		}//end for
		this.places = new int[i]; //we know the number of places...!
		this.BPlaces = new int[i];
		i = 0;
		for(int j=0; j<BMax.length; j++) {
			for(int k=0; k<BMax[j]; k++) {
				this.BPlaces[i] = j; //register B-j as owner of place i
				i++;
			}//end for
		}//end for
		//Plans: innocent defaults
		swapplace1 = NOPLANYET;
		swapplace2 = NOPLANYET;
		addableA = NOPLANYET;
		placetofill = NOPLANYET;		
	}//end initPlacesAndPlans
	
	
	//problem specific getters
	
	public int getNumberOfAs() {
		return numberOfAs;
	}//end getNumberOfAs
	

	//shared public / private methods between both technique-families

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.DeterministicSearchProblem#getCost()
	 * 
	 * As the cost, calculate the sum of the realized preference values
	 */
	@Override
	public long getCost() {
		long cost =0;
		for(int i=0; i<places.length; i++) {
			cost += preferenceOfPlacedStudent(i);				
		}//end for
		return cost;
	}//end getCost
	
	/**
	 * Given a position p in the places array: find the preference of the
	 * student placed there for that place
	 * @param p index in places array
	 * @return preference, or zero if place p is unfilled
	 */
	private long preferenceOfPlacedStudent(int p) {
		if (places[p]==EMPTYPLACE)
			return 0;
		else return ABPreferencesT[places[p]][BPlaces[p]];
	}//end getPreferenceOfPlacedStudent
	
	/**
	 * What is the preference of student at place p1 for place p2?
	 * @param p1, current place of the student
	 * @param p2, alternative place, for which to find the preference
	 * @return the preference for p2 of the student at p1
	 */
	private long preferenceOfPlacedStudentForOtherPlace(int p1, int p2) {
		if (places[p1]==EMPTYPLACE)
			return 0;
		else return ABPreferencesT[places[p1]][BPlaces[p2]];		
	}//end preferenceOfPlacedStudentForOtherPlace
	
	/**
	 * Return a packaged reference to the live State inside this Problem
	 * @return the State
	 */
	public State getState() {
		return new ManyToOneMatchingState(places, this);
	}//end getState
	
	/**
	 * Exchange the live State of this problem for another State of the
	 * appropriate subtype
	 */
	public void setState(State s) {
		ManyToOneMatchingState s1 = (ManyToOneMatchingState) s;
		this.places = s1.getPlaces();
		this.placetofill = -1;  //we should start without search history
	}//end setState
	
	/**
	 * Describe the current state - this one has been used for debugging
	 * == take care it shows transformed preferences ==
	 * See bottom for reporting methods called by the GUI
	 */
	public String showState() {
		String txt = "place, B-id, A-id, Pref\n";
		for (int i=0; i<places.length; i++) {
			txt += 	String.valueOf(i)+",  "+
					String.valueOf(BPlaces[i])+",  "+
					String.valueOf(places[i])+",  "+
					String.valueOf(preferenceOfPlacedStudent(i))+"\n";
		}//end for
		txt += "EOF\n";
		return txt;
	}//end showState

	/**
	 * Transform the single AB-pair preferences according to a chosen function
	 * Useful functions are monotonous.
	 * @param ABPreferences, the preferences array whose values must be transformed
	 * @param transformation, name of transformation: "identity", "square",
	 *    "thirdpower", "faculteit" (ToDo), "n^n"
	 * @return ABPreferencesT with values transformed
	 */
	protected long[][] transformPrefs(int[][] ABPreferences, String transformation) {
		long[][ ] ABPreferencesT = new long[ABPreferences.length][ABPreferences[0].length];
		for (int i=0; i<ABPreferences.length; i++) {
			for (int j=0; j<ABPreferences[i].length; j++) {
				switch (transformation) {
				case "identity":
					ABPreferencesT[i][j] = ABPreferences[i][j];
					break;
				case "square":
					ABPreferencesT[i][j] = ABPreferences[i][j] * ABPreferences[i][j];
					break;
				case "thirdpower":
					ABPreferencesT[i][j] = ABPreferences[i][j] * ABPreferences[i][j] * ABPreferences[i][j];
					break;
				case "faculteit": {
					int val = ABPreferences[i][j];
					int valT = 1;
					for (int k=1; k<=val; k++) {
						valT *= k;
					}
					ABPreferencesT[i][j] = valT;
					break; }
				case "n^n": {
					int val = ABPreferences[i][j];
					int valT = 1;
					for (int k=1; k<=val; k++) {
						valT *= val;
					}
					ABPreferencesT[i][j] = valT;
					break; }
				default:
					try {
						throw new Exception("Undefined transformation for ABPreferences given");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}//end switch
			}//next j
		}//next i
		return ABPreferencesT;
	}//end transformPrefs
	
	
	//methods needed by the stochastic techniques
	//There is no goalTest method here because the initial state should be a 
	//goals state already, and the moves should take us from one goal state
	//to another.
	
	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.StochasticOptimisationProblem#initGoalState()
	 * 
	 * We create a goal state by arbitrarily filling the first numberOfAs
	 * places just in order: student 0 in place 0, etcetera.
	 * We could later try to make a better initial guess.
	 */
	@Override
	public void initGoalState() throws Exception {
		if (numberOfAs <= places.length) {
			for (int i=0; i<numberOfAs; i++) {
				places[i] = i;
			}
			for (int i=numberOfAs; i<places.length; i++) {
				places[i] = EMPTYPLACE;
			}
		}
		else throw new Exception("Not enough places for all of the As offered by the Bs.");
	}//end initGoalState

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.StochasticOptimisationProblem#generateRandomMove()
	 * 
	 * Choose two places to swap their contents - even if one of them is empty
	 * If both are empty: short circuit and generate another move.
	 */
	@Override
	public void generateRandomMove() {
		do { //we do it at least once...
			swapplace1 = (int) Math.floor(Math.random()*places.length);
			swapplace2 = (int) Math.floor(Math.random()*places.length);						
		} while (
			places[swapplace1] == EMPTYPLACE && places[swapplace2] == EMPTYPLACE
		);
	}//end generateRandomMove

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.StochasticOptimisationProblem#getDeltaCostRandomMove()
	 */
	@Override
	public long getDeltaCostRandomMove() {
		//Lookup the 4 preferences involved
		//a1, the A initially at place 1, is: places[swapplace1]
		//the move is not made yet so we keep looking up A's at their old places
		long a1place1 = preferenceOfPlacedStudent(swapplace1);
		long a2place2 = preferenceOfPlacedStudent(swapplace2);
		long a1place2 = preferenceOfPlacedStudentForOtherPlace(swapplace1, swapplace2);
		long a2place1 = preferenceOfPlacedStudentForOtherPlace(swapplace2, swapplace1);
		//cost at the two new locations, minus cost at the old ones
		return a1place2 + a2place1 - (a1place1 + a2place2);
	}//end getDeltaCostRandomMove

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.StochasticOptimisationProblem#acceptMove(void)
	 * 
	 * The planned swap is done: two A-ids change places
	 */
	@Override
	public void acceptMove() {
		int temp = places[swapplace1];
		places[swapplace1] = places[swapplace2];
		places[swapplace2] = temp;
	}//end acceptMove

	
	
	//methods needed by the deterministic depthfirst techniques
	//We start with all places empty. In each move a single A is given
	//a place. The depth of the tree is equal to the number of As and each
	//A is uniquely linked to a level in the tree, in which he gets his places.
	//Places are tried in order, from low to high.
	//When we retreat to an earlier visited node, the same places are open as
	//on earlier visits, so we are safe in looking for the first higher open 
	//place. We do need to remember the previous move we did from this node.

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.DeterministicSearchProblem#initState()
	 * 
	 * We want to start with an empty places array! It is needed to mark them EMPTY
	 */
	@Override
	public void initState() {
		for (int i=0; i<places.length; i++) {
			places[i]=EMPTYPLACE;
		}//end for
	}//end initState

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.DeterministicSearchProblem#generateDeterministicMove()
	 * 
	 * For A-id equal to level: try new place.
	 * A new place is a place that we didn't yet visit in this same tree-node.
	 * For keeping track, the previous move is stored with the node and restored
	 * by the retreat method together with the rest of the state.
	 * So given the previous try, we need to find the next higher free place
	 * 
	 * @return success, was a new place found?
	 * @param, level, the level of the tree node for which we make a suggestion
	 */
	@Override
	public boolean generateDeterministicMove(int level) {
		addableA = level;
		if (placetofill==NOPLANYET) placetofill = -1;
		do {
			placetofill++;			
		} while ( //we need lazy evaluation here...
				placetofill<places.length && places[placetofill]!=EMPTYPLACE 
		);
		boolean rs = (placetofill<places.length)? (true) : (false);
		return rs;
	}//end generateDeterministicMove
	
	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.DeterministicSearchProblem#getDeltaCostDeterministicMove()
	 * 
	 * These moves make one relation at a time and break no relations
	 * (that happens on retreat! we will use a minus sign then and 
	 * we just call this same function - see the techniques packages)
	 */
	@Override
	public long getDeltaCostDeterministicMove() {
		return ABPreferencesT[addableA][BPlaces[placetofill]];
	}//end getDeltaCostDeterministicMove

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.DeterministicSearchProblem#doPlannedMove()
	 */
	@Override
	public Move doForwardMove() {
		//add the A to the planned place
		places[placetofill] = addableA;
		//package the move for putting it on the stack
		Move mv = new ManyToOneMatchingMove(addableA, placetofill);
		//fresh node: we should start without search history
		placetofill = -1;
		return mv;
	}//end doPlannedMove
	
	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.DeterministicSearchProblem# goalTest(int level)
	 */
	@Override
	public boolean goalTest(int level) {
		return (level == this.numberOfAs);  //level is size! en moet echt gelijk zijn aan aantal
	}//end goalTest

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.DeterministicSearchProblem#retreatMove(nl.uva.science.esc.search.problems.Move)
	 */
	@Override
	public void retreatMove(Move m1) {
		ManyToOneMatchingMove m = (ManyToOneMatchingMove) m1;
		//the move should fit: it should lead back to the original level
		assert (this.addableA == m.getLevel()+1); 
		//restore the move
		this.addableA = m.getLevel();
		this.placetofill = m.getPlacetofill();
		//do the move in reverse, to restore the original state
		places[placetofill] = EMPTYPLACE;
		//note: we keep the history of the node by not deleting placetofill
	}//end retreatMove
	
	
	//Methods used to describe a state to the GUI
	//A State is expected to be able to describe itself (see State interface)
	//However for the ManyToOneMatchingState it is advantageous to delegate
	//this responsibility back to this Problem object.
	//Reason: we need BPlaces and ABPreferences (without transformation) to make 
	//a more meaningful report.
	
	/**
	 * Describe a state in full
	 * @param s, the state to describe
	 * @return an n x m array to be shown as a table, n is rows
	 */
	public String[][] showStateDetails(ManyToOneMatchingState s) {
		int[] stateplaces = s.getPlaces(); //the thing we're describing!
		String[][] rs = new String[stateplaces.length][3]; 
		for (int i=0; i<stateplaces.length; i++) {
			rs[i][0] = Integer.toString( BPlaces[i] ); //column 0: B-id
			rs[i][1] = Integer.toString( stateplaces[i] );  //column 1: A-id
			rs[i][2] = Integer.toString( ABPreferences[BPlaces[i]][stateplaces[i]] );
			    //column 2: The preference of this B and this A being linked
		}//next i
		return rs;
	}//end showStateDetails
	
	/**
	 * Advertise column headings for the above detailed State report
	 * @return headings
	 */
	public String[] showStateDetailsHeadings() {
		return new String[] {"B-id", "A-id", "AB-pref."};
	}//end showStateDetailsHeadings
	
	/**
	 * Give a summary description of a state
	 * We will tell how many lowest, next lowest,... up to 5th lowest
	 * as well as how many higher preferences were realised
	 * @param s, the state to describe
	 * @return an n x m array to be shown as a table, n rows
	 */
	public String[][] showStateSummary(ManyToOneMatchingState s) {
		int[] stateplaces = s.getPlaces(); //the thing we're describing!
		IntegerCategoriesSummarizer m = new IntegerCategoriesSummarizer();
		for (int i=0; i<stateplaces.length; i++) {
			m.addItem(ABPreferences[BPlaces[i]][stateplaces[i]]);
		}//next i
		//Use it to see how often the lowest 5 preferences were realized
		String[] RankWords = new String[] 
		    {"Lowest", "Next lowest", "Third lowest", "Fourth lowest", "Fifth lowest"};
		int maxrows = Math.min(5, m.size());
		String[][] rs = new String[maxrows+1][3]; //a place for building the output
		for (int j=0; j<maxrows; j++) {
			rs[j][0] = RankWords[j];                //column 0: rank word
			rs[j][1] = Integer.toString(m.removeLowest());   //column 1: preference number
			rs[j][2] = Integer.toString(m.getRomevedCount());//column 2: times realised
		}//next j
		if (m.size()>0) { //Fill a final row with a total of remaining cats.
			maxrows = maxrows + 1;		
			rs[maxrows-1][0] = "Higher prefs.";        //column 0: rank word
			rs[maxrows-1][1] = "up to " + 
				Integer.toString(m.highestCategory()); //column 1: preference number
			rs[maxrows-1][2] = 
				Integer.toString(m.removeAndCountRest());//column 2: times realised
		}//end if
		return rs;
	}//end showStateSummary

	/**
	 * Advertise column headings for the above summary State report
	 * @return headings
	 */
	public String[] showStateSummaryHeadings() {
		return new String[] {"Pref-rank", "Pref", "Times realised"};
	}//end showStateSummaryHeadings

	/**
	 * Advertise parameters that the GUI must get from the user before calling
	 * the below Factory. The Factory expects them in the order given here!
	 */
	public static Parameter[] advertiseParameters() {
		return new Parameter[] {
				new Parameter("transformpreferences", true, InputType.STRING) 
				//This inputtype is not really good.
				//It's really an enum type whose possible values should be
				//shown in a selectbox, instead of being typed in. ToDo!
		};
	}//end advertiseParameters
	
}//end class
