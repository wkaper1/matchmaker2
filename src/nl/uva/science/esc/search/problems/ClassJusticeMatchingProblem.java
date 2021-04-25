package nl.uva.science.esc.search.problems;

import java.util.SortedMap;
import java.util.TreeMap;

import nl.uva.science.esc.matchmaker.Controller.InputType;
import nl.uva.science.esc.search.problems.transf.PreferenceTransformation;
import nl.uva.science.esc.search.views.Parameter;

/**
 * Each student is to be assigned to a single project.
 * A project can employ one or more students. For each project instance, a
 * different maximum number of students is set.
 * Students indicated preferences for projects, i.e. they indicated a 
 * project as their first, second, third, etc. preference.
 * 
 * Each student assigns preferences explicitly only for few of the possible 
 * projects: they are the high preferences. For all non-mentioned pairs an equal 
 * and very low default preference (high cost) is assumed.
 * Non-explicit preferences are temporarily indicated by zeroes in the prefs 
 * arrays.
 * 
 * The students are categorized into two or more categories, e.g. "first time" 
 * students, and "retry" students.
 * For each category of students a different transformation function is applied
 * to their preferences, e.g. "first time" students: multiply by 2, and "retry" 
 * students: "add 1". The effect in the example would be that the 10th 
 * preference of a "retry" student gets equal probability of being fulfilled as 
 * the 5th preference of an "first time" student. The motivation could be that 
 * we want to re-distribute the "tail" of the less wanted preferences away from 
 * the first time students, cause everybody deserves a great first time. So its
 * really not class justice at all! (in the example). 
 * 
 * The operator of this software can choose to disregard the higher numbered
 * (less desirable) explicit preferences of students, by choosing a 'cutoff' 
 * value. If the preference number that the student assigned to the project is 
 * equal or higher than the respective cutoff value, the preference of this 
 * student-project pair is set to the high default that also applies to 
 * unmentioned pairs.
 * 
 * After this transforming, and default assigning, the problem is reduced to 
 * a ManyToOneMatchingProblem
 * 
 * This class's knowledge of the un-reduced problem is also used in reporting.
 * Four reporting methods of the base class are overriden.
 * 
  * @author kaper
 */
public class ClassJusticeMatchingProblem extends ManyToOneMatchingProblem
		implements StochasticOptimisationProblem, DeterministicSearchProblem {
	
	//preferences above the respective cutoff will be treated in the same way
	//as nonmentioned preferences
	private int studPrefCutoff; //cutoff value for preferences stated by students
	private int defaultPref;             //Preference for all nonmentioned pairs
	  //as well as for pairs where the students preference is above the cutoff!
	private PreferenceTransformation[] transforms;
	private int[] ACategory;    //for each A his category number (0, 1, 2,...)
	private int[][] ABPreferencesStud;   //Preference of student A for project B
	private int numCategories;  //how many categories?

	/**
	 * Constructor does preprosessing to turn the specific problem into
	 * the more general one: 
	 *  ** The preferences of the categories of students are transformed **
	 * @param numberOfAs, how many students to link to projects
	 * @param BMax, maximum number of students for each project
	 * @param ABPreferencesStud, preference of student A for project B
	 */
	public ClassJusticeMatchingProblem(
			int numberOfAs, int[] BMax, int defaultPref,
			int[][] ABPreferencesStud,
			PreferenceTransformation[] transforms, int[] ACategory,  
			String transformprefs, int studPrefCutoff
	) {
		this.defaultPref = defaultPref;
		this.ABPreferencesStud = ABPreferencesStud;
		this.transforms = transforms;
		this.ACategory = ACategory;
		this.studPrefCutoff = studPrefCutoff;
		this.ABPreferences = calcABPreferences(); //protected property of base class
		this.ABPreferencesT = this.transformPrefs(ABPreferences, transformprefs);
		initPlacesAndPlans(BMax, BMax);     //protected method in base class      //TODO: BMin
		this.numberOfAs = numberOfAs;  //protected property of base class
		this.numCategories = Max(this.ACategory)+1;
	}//end constructor
	
	/**
	 * Normalised constructor for this class, used by the GUI via the ProblemFactory
	 * Mmm... the transforms parameter does not fit this philosophy, sadly...
	 * 
	 * @param c, a specific Connector for this problem type
	 * @param parametervalues, parameters we want from the GUI, as advertised (see below)
	 * @param transforms, array of preferencetransformation objects, one for each 
	 * 		student category
	 * @return the object
	 */
	public ClassJusticeMatchingProblem(
		ProblemConnector c, String[] parametervalues, PreferenceTransformation[] transforms
	) {
		StudentProjectMatchingProblemConnector sc = (StudentProjectMatchingProblemConnector) c;
		this.defaultPref = Integer.parseInt( parametervalues[0] );
		this.ABPreferencesStud = sc.getABPreferencesStud();
		this.transforms = transforms;
		this.ACategory = sc.getACategory();
		this.studPrefCutoff = Integer.parseInt( parametervalues[2] );
		this.ABPreferences = calcABPreferences(); //protected property of base class
		this.ABPreferencesT = this.transformPrefs(ABPreferences, parametervalues[1]);
		initPlacesAndPlans( sc.getBMax(), sc.getBMax() );      //protected method in base class   //TODO: sc.getBMin()
		this.numberOfAs = sc.getnumberOfAs();    //protected property of base class
		this.numCategories = Max(this.ACategory)+1;
	}//end StudentProjectMatchingProblemFactory
	
	/**
	 * Transform the preference numbers of the students, each according to its 
	 * category.
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
					ABPreferencesStud[i][j]==0 ||
					ABPreferencesStud[i][j]>=studPrefCutoff 
				) {
					ABPreferences[i][j] = defaultPref; 
				}
				else {
					ABPreferences[i][j] = transforms[ACategory[i]]
							.Transform( ABPreferencesStud[i][j] );					
				}//end if
			}//next j
		}//next i
		return ABPreferences;
	}//end factory
	
	
	
	//After constructing the object, the base class does all the work.
	//We will however override the methods for reporting a solution.
	//The overridden methods will show specifically how many students first
	//choices were granted... split by student category! 

	/**
	 * Describe a state in full
	 * @param s, the state to describe
	 * @return an n x m array to be shown as a table, n is rows
	 */
	public String[][] showStateDetails(ManyToOneMatchingState s) {
		int[] stateplaces = s.getPlaces(); //the thing we're describing!
		String[][] rs = new String[stateplaces.length][3]; 
		for (int i=0; i<stateplaces.length; i++) {
			rs[i][0] = Integer.toString( BPlaces[i] ); //column 0: Project-id
			rs[i][1] = Integer.toString( stateplaces[i] );  //column 1: Student-id
			if (stateplaces[i] != EMPTYPLACE) {
				//column 2 Preference of student for project
				rs[i][2] = Integer.toString( ABPreferencesStud[stateplaces[i]][BPlaces[i]] );
			}
			else {
				//not a student, so no preferences known
				rs[i][2] = Integer.toString(EMPTYPLACE);
			}
		}//next i
		return rs;
	}//end showStateDetails
	
	/**
	 * Advertise column headings for the above detailed State report
	 * @return headings
	 */
	public String[] showStateDetailsHeadings() {
		return new String[] {"Project", "Student", "PrefStud."};
	}//end showStateDetailsHeadings
	
	/**
	 * Give a summary description of a state
	 * We will tell how many lowest, next lowest,... up to 5th lowest
	 * as well as how many higher preferences were realized
	 * @param s, the state to describe
	 * @param maxrow, highest preference to show, this determines the size of the summary
	 * @return an n x m array to be shown as a table, n rows
	 */
	public String[][] showStateSummary(ManyToOneMatchingState s, int maxrow) {
		int[] stateplaces = s.getPlaces(); //the thing we're describing!
		//we need Summarizers for each of the student categories
		int m = numCategories;
		IntegerCategoriesSummarizer[] ms = new IntegerCategoriesSummarizer[m];
		for (int c=0; c<m; c++) {
			ms[c] = new IntegerCategoriesSummarizer();
		}
		//Count the realized preferences, to determine the 5 lowest, and how often they were realized
		for (int i=0; i<stateplaces.length; i++) {
			if (stateplaces[i] != EMPTYPLACE) {
				int c = ACategory[stateplaces[i]];
				ms[c].addItem(ABPreferencesStud[stateplaces[i]][BPlaces[i]]);
			}//end if
		}//next i
		//Determine the maximum number of rows
		int maxrow0 = 0;
		for (int c=0; c<m; c++) {
			int maxs = (int)ms[c].highestCategory();
			maxrow0 = Math.max(maxrow0, maxs);
		}
		//Apply chosen limitation
		maxrow = Math.min(maxrow, maxrow0);
		//a place for building the output
		String[][] rs = (maxrow < maxrow0) ? 
				new String[maxrow+2][m+2] : //one more for the summary row!
				new String[maxrow+1][m+2] ; //from 0 up to and including maxrow
		//Use it to show counts of the lowest 5 preferences
		//Preferences are consequtive integers in this application
		//Preference 0 is used for non-chosen courses
		for (int j=0; j<=maxrow; j++) {
			rs[j][0] = Integer.toString(j);     //column 0: preference number
			//colums 1 to m: realized numbers for this pref per student category
			int tot = 0;
			for (int c=0; c<m; c++) {
				rs[j][c+1] = Integer.toString(ms[c].getCount(j));
				tot += ms[c].getCount(j);
			}
			rs[j][m+1] = Integer.toString(tot); //column m+1: total for this pref 	
		}//next j
		//Fill a final row with totals of the remaining prefs
		if (maxrow < maxrow0) {
			maxrow = maxrow + 1;
			rs[maxrow][0] = "Higher"; //column 0: preference number
			int tot = 0;
			for (int c=0; c<m; c++) {
				int count = ms[c].addCountsForRange(maxrow, maxrow0);
				rs[maxrow][1] = Integer.toString(count); //mumber realized per cat.
				tot += count;
			}
			rs[maxrow][2] = Integer.toString(tot);//column m+1: total for remaining pref-range			
		}//end if
		return rs;
	}//end showStateSummary

	/**
	 * Advertise column headings for the above summary State report
	 * @return headings
	 */
	public String[] showStateSummaryHeadings() {
		int m = numCategories;
		String[] hs = new String[m+2];
		hs[0] = "Preference";
		for (int c=0; c<m; c++) {
			hs[c+1] = "c" + c;
		}
		hs[m+1] = "Total";
		return hs;
	}//end showStateSummaryHeadings
	
	/**
	 * Get the max value from an array of positive integers
	 * If the values are categories and meant to be consecutive this gives us
	 * the number of categories (add 1, if 0 is also a category).
	 * @param intarr
	 * @return the max, or 0 if array empty
	 */
	public int Max(int[] intarr) {
		int max = 0; //default if empty array
		for (int i=0; i<intarr.length; i++) {
			if (intarr[i]>max) max = intarr[i];
		}
		return max;
	}

	/**
	 * Advertise parameters that the GUI must get from the user before calling
	 * the below Factory. The Factory expects them in the order given here!
	 *  ** TODO: design the GUI part for the transformations per ACategory **
	 * The below is just a shortened copy from StudentProjectMachingProblem
	 */
	public static Parameter[] advertiseParameters() {
		return new Parameter[] {
				new Parameter("notchosenpenalty", true, InputType.POSITIVEINT), 
				new Parameter("transformpreferences", true, InputType.STRING), 
				//This 4e inputtype is not really good.
				//It's really an enum type whose possible values should be
				//shown in a selectbox, instead of being typed in. ToDo!
				new Parameter("studentprefcutoff", true, InputType.POSITIVEINT),
		};
	}//end advertiseParameters
	
}//end class
