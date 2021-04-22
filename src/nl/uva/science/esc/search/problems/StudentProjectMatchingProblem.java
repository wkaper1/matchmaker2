package nl.uva.science.esc.search.problems;

import java.util.SortedMap;
import java.util.TreeMap;

import nl.uva.science.esc.matchmaker.Controller.InputType;
import nl.uva.science.esc.search.views.Parameter;

/**
 * Each student is to be assigned to a single project.
 * A project can employ one or more students. For each project instance, a
 * different maximum number of students is set.
 * Both parties (students and projects) indicated preferences for eachother
 * So for each student-project combination we have two preferences numbers,
 * Pstudent[s,p] and PProject[s,p], that are to be weighted into a single
 * preference for the [s,p] combination as a whole.
 * 
 * Each student assigns preferences explicitly only for few of the possible 
 * pairs: they are the high preferences. For all non-mentioned pairs an equal 
 * and very low default preference (high cost) is assumed.
 * Projects assign explicit preferences only for candidates that expressed
 * explicit preference for them! (Explicitness is organised to be mutual).
 * Non-explicit preferences are temporarily indicated by zeroes in the prefs 
 * arrays.
 * 
 * The operator of this software can choose to disregard the higher numbered
 * (less desirable) explicit preferences of students, of projects, or of both
 * by choosing two 'cutoff' values. If the preference number that either the
 * student or the project assigned to the other party is equal or higher than
 * the respective cutoff value, the preference of this student-project pair is
 * set to the high default that also applies to unmentioned pairs.
 * We will have a 'studentprefcutoff', as well as a 'projectprefcutoff' parameter
 * for this purpose.
 * 
 * After this weighting, and default assigning, the problem is reduced to 
 * a ManyToOneMatchingProblem
 * 
 * This class's knowledge of the un-reduced problem is also used in reporting
 * Four reporting methods of the base class are overriden
 * 
 * @author kaper
 *
 */
public class StudentProjectMatchingProblem extends ManyToOneMatchingProblem
		implements StochasticOptimisationProblem, DeterministicSearchProblem {
	
	private int WStud; //Weight for students preferences
	private int WProj; //Weight for the project supervisors preferences
	//preferences above the respective cutoff will be treated in the same way
	//as nonmentioned preferences
	private int studPrefCutoff; //cutoff value for preferences stated by students
	private int projPrefCutoff; //cutoff value for preferences stated by projects
	private int defaultPref;             //Preference for all nonmentioned pairs
	  //as well as for pairs where one of the two preferences is above the cutoff!
	private int[][] ABPreferencesStud;   //Preference of student A for project B
	private int[][] ABPreferencesProj;	 //Preference of project B for student A

	/**
	 * Constructor does preprosessing to turn the specific problem into
	 * the more general one: ** The preferences of both parties are weighted **
	 * @param numberOfAs, how many students to link to projects
	 * @param WStud, weight for students preferences
	 * @param WProj, weight for the project supervisors preferences
	 * @param BMax, maximum number of students for each project
	 * @param ABPreferencesStud, preference of student A for project B
	 * @param ABPreferencesProj, preference of project B for student A
	 */
	public StudentProjectMatchingProblem(
			int numberOfAs, int WStud, int WProj, int defaultPref,
			int[] BMax, int[][] ABPreferencesStud, int[][] ABPreferencesProj,
			String transformprefs, int studPrefCutoff, int projPrefCutoff
	) {
		this.WStud = WStud;
		this.WProj = WProj;
		this.defaultPref = defaultPref;
		this.ABPreferencesStud = ABPreferencesStud;
		this.ABPreferencesProj = ABPreferencesProj;
		this.studPrefCutoff = studPrefCutoff;
		this.projPrefCutoff = projPrefCutoff;
		this.ABPreferences = calcABPreferences(); //protected property of base class
		this.ABPreferencesT = this.transformPrefs(ABPreferences, transformprefs);
		initPlacesAndPlans(BMax);     //protected method in base class
		this.numberOfAs = numberOfAs;  //protected property of base class
	}//end constructor
	
	/**
	 * Normalised constructor for this class, used by the GUI via the ProblemFactory
	 * @param c, a specific Connector for this problem type
	 * @param parametervalues, parameters we want from the GUI, as advertised (see below)
	 * @return the object
	 */
	public StudentProjectMatchingProblem(
		ProblemConnector c, String[] parametervalues
	) {
		StudentProjectMatchingProblemConnector sc = (StudentProjectMatchingProblemConnector) c;
		this.WStud = Integer.parseInt( parametervalues[0] );
		this.WProj = Integer.parseInt( parametervalues[1] );
		this.defaultPref = Integer.parseInt( parametervalues[2] );
		this.ABPreferencesStud = sc.getABPreferencesStud();
		this.ABPreferencesProj = sc.getABPreferencesProj();
		this.studPrefCutoff = Integer.parseInt( parametervalues[4] );
		this.projPrefCutoff = Integer.parseInt( parametervalues[5] );
		this.ABPreferences = calcABPreferences(); //protected property of base class
		this.ABPreferencesT = this.transformPrefs(ABPreferences, parametervalues[3]);
		initPlacesAndPlans( sc.getBMax() );      //protected method in base class
		this.numberOfAs = sc.getnumberOfAs();    //protected property of base class
	}//end StudentProjectMatchingProblemFactory
	
	/**
	 * Do the weighting of student-stated and project-stated preferences
	 * Assign a default (high, unwanted) preference to all nonmentioned pairs
	 * as well as to the pairs where one of both stated preferences is equal or
	 * above the respective (operator-chosen) cutoff value
	 * @return ABPreferences
	 */
	private int[][] calcABPreferences() {
		int ABPreferences[][] = new int[ABPreferencesStud.length][ABPreferencesStud[0].length];
		for (int i=0; i<ABPreferences.length; i++) {
			for (int j=0; j<ABPreferences[0].length; j++) {
				if (
					ABPreferencesStud[i][j]==0 || ABPreferencesProj[i][j]==0 ||
					ABPreferencesStud[i][j]>=studPrefCutoff ||
					ABPreferencesProj[i][j]>=projPrefCutoff
				) {
					ABPreferences[i][j] = defaultPref; 
				}
				else {
					ABPreferences[i][j] = WStud * ABPreferencesStud[i][j] + 
						WProj * ABPreferencesProj[i][j];					
				}//end if
			}//next j
		}//next i
		return ABPreferences;
	}//end factory
	
	
	
	//After constructing the object, the base class does all the work.
	//We will however override the methods for reporting a solution.
	//The overridden methods will show specifically how many students first
	//choices were granted, and how many supervisor first choices...! 

	/**
	 * Describe a state in full
	 * @param s, the state to describe
	 * @return an n x m array to be shown as a table, n is rows
	 */
	public String[][] showStateDetails(ManyToOneMatchingState s) {
		int[] stateplaces = s.getPlaces(); //the thing we're describing!
		String[][] rs = new String[stateplaces.length][4]; 
		for (int i=0; i<stateplaces.length; i++) {
			rs[i][0] = Integer.toString( BPlaces[i] ); //column 0: Project-id
			rs[i][1] = Integer.toString( stateplaces[i] );  //column 1: Student-id
			if (stateplaces[i] != EMPTYPLACE) {
				//columns 2 and 3: Preferences of student and project for eachother
				rs[i][2] = Integer.toString( ABPreferencesStud[stateplaces[i]][BPlaces[i]] );
				rs[i][3] = Integer.toString( ABPreferencesProj[stateplaces[i]][BPlaces[i]] );				
			}
			else {
				//not a student, so no preferences known
				rs[i][2] = Integer.toString(EMPTYPLACE);
				rs[i][3] = Integer.toString(EMPTYPLACE);
			}
		}//next i
		return rs;
	}//end showStateDetails
	
	/**
	 * Advertise column headings for the above detailed State report
	 * @return headings
	 */
	public String[] showStateDetailsHeadings() {
		return new String[] {"Project", "Student", "PrefStud.", "PrefProj."};
	}//end showStateDetailsHeadings
	
	/**
	 * Give a summary description of a state
	 * We will tell how many lowest, next lowest,... up to 5th lowest
	 * as well as how many higher preferences were realised
	 * @param s, the state to describe
	 * @param maxrow, highest preference to show, this determines the size of the summary
	 * @return an n x m array to be shown as a table, n rows
	 */
	public String[][] showStateSummary(ManyToOneMatchingState s, int maxrow) {
		//...it contains two or three ugly repetitions... not quite DRY
		int[] stateplaces = s.getPlaces(); //the thing we're describing!
		//we need two Summarizers for s-prefs and p-prefs respectively
		IntegerCategoriesSummarizer ms = new IntegerCategoriesSummarizer();
		IntegerCategoriesSummarizer mp = new IntegerCategoriesSummarizer();
		//Count the preferences as categories, to determine the 5 lowest, and how often they were realized
		for (int i=0; i<stateplaces.length; i++) {
			if (stateplaces[i] != EMPTYPLACE) {
				ms.addItem(ABPreferencesStud[stateplaces[i]][BPlaces[i]]);
				mp.addItem(ABPreferencesProj[stateplaces[i]][BPlaces[i]]);				
			}//end if
		}//next i
		//Determine the maximum number of rows
		int maxs = (int)ms.highestCategory();
		int maxp = (int)mp.highestCategory();
		int maxrow0 = Math.max(maxs, maxp);
		maxrow = Math.min(maxrow, maxrow0);
		//a place for building the output
		String[][] rs = (maxrow < maxs || maxrow < maxp) ? 
				new String[maxrow+2][3] : //one more for the summary row!
				new String[maxrow+1][3]	; //from 0 up to and including maxrow
		//Use it to show counts of the lowest 5 categories
		//Preference categories are consequtive integers in this application
		//Preference 0 is used for non-chosen courses
		for (int j=0; j<=maxrow; j++) {
			rs[j][0] = Integer.toString(j);         //column 0: preference number
			rs[j][1] = Integer.toString(ms.getCount(j)); //column 1: students pref realized
			rs[j][2] = Integer.toString(mp.getCount(j)); //column 2: projects pref realized	
		}//next j
		//Fill a final row with totals of the remaining categories
		if (maxrow < maxs || maxrow < maxp) {
			maxrow = maxrow + 1;
			rs[maxrow][0] = "Higher"; //column 0: preference number
			rs[maxrow][1] = 
				Integer.toString(ms.addCountsForRange(maxrow, maxs)); 
				//column 1: students pref realized
			rs[maxrow][2] = 
				Integer.toString(mp.addCountsForRange(maxrow, maxp)); 
				//column 2: projects pref realized			
		}//end if
		return rs;
	}//end showStateSummary

	/**
	 * Advertise column headings for the above summary State report
	 * @return headings
	 */
	public String[] showStateSummaryHeadings() {
		return new String[] {"Preference", "Students", "Supervisors"};
	}//end showStateSummaryHeadings

	/**
	 * Advertise parameters that the GUI must get from the user before calling
	 * the below Factory. The Factory expects them in the order given here!
	 */
	public static Parameter[] advertiseParameters() {
		return new Parameter[] {
				new Parameter("studentsprefweight", true, InputType.POSITIVEINT), 
				new Parameter("projectsprefweight", true, InputType.POSITIVEINT), 
				new Parameter("notchosenpenalty", true, InputType.POSITIVEINT), 
				new Parameter("transformpreferences", true, InputType.STRING), 
				//This 4e inputtype is not really good.
				//It's really an enum type whose possible values should be
				//shown in a selectbox, instead of being typed in. ToDo!
				new Parameter("studentprefcutoff", true, InputType.POSITIVEINT),
				new Parameter("projectprefcutoff", true, InputType.POSITIVEINT)
		};
	}//end advertiseParameters
	
}//end class
