package nl.uva.science.esc.matchmaker;

import nl.uva.science.esc.search.problems.ManyToOneMatchingState;
import nl.uva.science.esc.search.problems.ClassJusticeMatchingProblem;
import nl.uva.science.esc.search.problems.transf.*;
import nl.uva.science.esc.search.problems.StudentProjectMatchingProblemConnector;
import nl.uva.science.esc.search.techniques.SimulatedAnnealingTechnique;

/**
 * Test for ClassJusticetMatchingProblem, including its ProblemConnector
 * using data which we read from a file.
 * Also, the solution will be written to a file.
 * @author kaper
 */
public class TestClassJusticeMatchingRealData {
	//configure the input and output file here
	public static final String problemPath = "D:\\matching\\problem.txt";
	public static final String solutionPath = "D:\\matching\\solution.txt";
	
	//problem parameters that we later want to get from the GUI
	public static final String DEFAULT_PREF = "10000";  //pref. given to non-chosen projects
	//Transformation to do on all preference values, after the per class transformations
	public static final String tranformation2 = "identity";
	//Cutoff values for student-stated and project-stated preferences
	public static final String studentPrefCutoff = "11";
	
	//Separate transformations for two categories: first-time and return students
	//Preference multipliers, translations
	//Scheme 1: distribute the low-preference tail away from firsttimers
	public static final int firstTimeMultiplier = 7;
	public static final int returnMultiplier = 1;
	public static final int returnTranslation = 6;
	//Scheme 2: invert the scale, e.g. to 'punish' students that didn't make the required nr of choices 
//	public static final int firstTimeMultiplier = 1;  //it's NOT a great idea!
//	public static final int returnMultiplier = -1;
//	public static final int returnTranslation = 10;
	
	//technique parameters
	public static final double initialTemperature = 10;
	public static final int waittime = 100000;
	public static final float temperaturedrop = (float) 0.9997;
	public static final double closetozerotemp = 0.4;
	public static final int maxtriesinvain = 100000;
	
	//reporting parameters
	public static final int nhighestprefstoreport = 10; //highest N preferences to report on

	/**
	 * Run the test
	 * (No configuration here)
	 * @param args Currently so arguments supported
	 */
	public static void main(String[] args) throws Exception {
		//read problem from file
		StudentProjectMatchingProblemConnector pc = new StudentProjectMatchingProblemConnector();
		pc.getProblemFromFile(problemPath);
		//create the two transformation
		PreferenceTransformation[] transf = new PreferenceTransformation[2];
		transf[0] = new LinearTransformation(firstTimeMultiplier, 0);
		transf[1] = new LinearTransformation(returnMultiplier, returnTranslation);
		//make problem object
		ClassJusticeMatchingProblem p = new ClassJusticeMatchingProblem(
			pc, 
			new String[] { DEFAULT_PREF, tranformation2, studentPrefCutoff },
			transf
		);
		//tweak technique parameters in the call below
		SimulatedAnnealingTechnique t = new SimulatedAnnealingTechnique(
			p, initialTemperature, waittime, temperaturedrop, closetozerotemp, 
			maxtriesinvain
		);
		t.run();
		//show the final state on the console
		System.out.println( p.showState() ); //from parent ManyToOneMatching... (current, volgnrs ipv id's)
		//get the final state and work with it
		ManyToOneMatchingState s = (ManyToOneMatchingState) t.getCurrentState();
		//show statistics on console (how many 1st, 2nd,... preferences realized?)
		showStateSummary(p, s);
		//write final state to a file
		pc.setABMatches( p.showStateDetails(s)); 
		pc.writeSolutionToFile(solutionPath, true); //conversion to database ids!
	}//end main
	
	/**
	 * Write the summary of a ClassJusticeMatchingProblem state to the console
	 * in the form of a table
	 * @param p, the problem for which s is a solution state
	 * @param s, the state to summarize
	 */
	private static void showStateSummary(
		ClassJusticeMatchingProblem p, ManyToOneMatchingState s
	) {
		System.out.println("State Summary");
		//get the data to show
		String[] headings = p.showStateSummaryHeadings();
		String[][] body = p.showStateSummary(s, nhighestprefstoreport);
		//print the header row
		for (int j=0; j<headings.length; j++) {
			System.out.print(headings[j] + " ");
		}
		System.out.println();
		//print the table body
		for (int i=0; i<body.length; i++) {
			//print a single row
			for (int j=0; j<body[i].length; j++) {
				System.out.print(body[i][j] + " ");
			}//next j
			System.out.println(); //end the row
		}//next i
		System.out.println(); //white line
	}//end showStateSummary

}//end class
