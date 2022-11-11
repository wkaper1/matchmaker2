package nl.uva.science.esc.matchmaker;

import nl.uva.science.esc.search.problems.ManyToOneMatchingState;
import nl.uva.science.esc.search.problems.StudentProjectMatchingProblem;
import nl.uva.science.esc.search.problems.StudentProjectMatchingProblemConnector;
import nl.uva.science.esc.search.techniques.SimulatedAnnealingTechnique;

/**
 * Test for StudentProjectMatchingProblem, including its ProblemConnector
 * using data which we read from a file.
 * Also, the solution will be written to a file.
 * So we only test the file-feature, not the webservice or mock feature of the
 * ProblemConnector. However this includes a test of the JSONObject reading and
 * writing that the webservice feature also will be using. 
 * @author kaper
 */
public class TestStudentProjectMatchingPlusConnectorRealData {
	//configure the input and output file here
	public static final String problemPath = "c:\\matching\\problem.txt";
	public static final String solutionPath = "c:\\matching\\solution.txt";
	
	//problem parameters that we later want to get from the GUI
	public static final String studentPrefWeight = "1";
	public static final String projectPrefWeight = "0";
	public static final String DEFAULT_PREF = "10000";  //pref. given to non-chosen projects
	//Transformation to do on all preference values
	public static final String tranformation = "identity";
	//Cutoff values for student-stated and project-stated preferences
	public static final String studentPrefCutoff = "4";
	public static final String projectPrefCutoff = "3";
	
	//technique parameters
	public static final double initialTemperature = 50000;
	public static final int waittime = 100000;
	public static final float temperaturedrop = (float) 0.997;
	public static final double closetozerotemp = 0.1;
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
		//make problem object
		StudentProjectMatchingProblem p = new StudentProjectMatchingProblem(
			pc, new String[] {
				studentPrefWeight, projectPrefWeight, DEFAULT_PREF, tranformation,
				studentPrefCutoff, projectPrefCutoff
			}
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
	 * Write the summary of a StudentProjectMatchingProblem state to the console
	 * in the form of a table
	 * @param p, the problem for which s is a solution state
	 * @param s, the state to summarize
	 */
	private static void showStateSummary(
		StudentProjectMatchingProblem p, ManyToOneMatchingState s
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
