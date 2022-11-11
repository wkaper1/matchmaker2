package nl.uva.science.esc.search.problems;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.*;

/**
 * Connector for the StudentProjectMatchingProblem
 * 
 * It can read a JSON object describing the problem data. The object can be 
 * retrieved from a file or from a webservice (superclass takes care of that).
 * 
 * Parsing the JSON object results in two things:
 * - The arrays (BMin[], BMax[], ABPreferencesStud[][], ABPreferencesProj[][], 
 *   ACategory[], numberOfAs), which are needed to construct the problem;
 * - Translation arrays: from actual database ids to temporary consequtive
 *   counting ids. Two such translation arrays will arise: StudId[], and ProjId[]
 * 
 * After solving the problem, this class can write the solution to a JSON hash
 * or object, which can then be written to a file, or posted to a webservice.
 * 
 * While constructing the JSON solution, the above mentioned translation arrays 
 * will be needed to reverse the translation that was done when constructing the 
 * problem.
 * 
 * Needed JSON structures in this particular problem
 * 
 * The JSON problem description object has these properties:
 * - hash projectPlaces
 *   { String projectid => [ int minPlaces,  maxPlaces ] }
 * - hash studentCategory
 *   { String studentid => int category }
 * - hash studentProjectPreferences
 *   preferences per student-project combination as given by both parties
 *   { String studentid => { String projectid => 
 *   			[ int prefStud, int prefProj ] 
 *   }}
 * - bool allowPlacesShortage, is it a valid problem numberOfStudents > numberOfPlaces ?
 * - int numberOfProjects, number of projectids in projectPlaces
 * - int numberOfPlaces, sum of numbers of places in projectPlaces
 * - int numberOfStudents, number of different studentids in studentProjectPreferences
 * The three int properties are for checking
 * All students appearing in studentProjectPreferences should also have a
 * category in studentCategory, we throw an error otherwise.
 * 
 * The JSON problem solution hash has this structure:
 * { String studentid : String projectid }
 * 
 * The StudentProjectMatchingProblem works by starting from an initial situation where all
 * students are given a place. This is not possible when numberOfStudents > numberOfPlaces.
 * But this connector can easily allow it by creating 1 extra bogus project that has just 
 * the missing places. This project by necessity was chosen by none. The matchmaking will
 * proceed as usual, and the bogus-places will remain filled, resulting in a minimum 
 * penalty that cannot be avoided.
 * However, the distribution of students will be such that students that chose impopular
 * projects always get what they want while students choosing popular projects have a
 * higher (but fairly distributed) chance of ending up in one of the bogus places.
 * In reporting the solution the bogus project is simply omitted by this connector.
 * 
 * The underlying Problem-class does not know that 1 project is bogus, so its reporting
 * will involve that bogus project! It will be recognisable by a suitably weird ID.
 * 
 * @author kaper
 *
 */
public class StudentProjectMatchingProblemConnector extends ProblemConnector {
	//data that we should deliver after reading the problem description
	private int numberOfAs;  //see constructor of Problem
	private int[] BMin;
	private int[] BMax;
	private int[][] ABPreferencesStud;
	private int[][] ABPreferencesProj;
	private int[] ACategory;
	private boolean HasPlacesShortage; //shortage allowed && actual shortage => bogus project added?
	//translation arrays that we need to keep between reading and writing
	private String[] StudId;
	private String[] ProjId;
	//solution data like the Problem natively delivers it
	private String[][] BAMatches; //an array of untranslated (B-id, A-id) pairs
		//see showStateDetails: it gives the same data for viewing in GUI.
	//webservice control data (see also parent class)
	private final String WEBSERVICEBASEURL="https://www.science.uva.nl/onderwijs/database/stages/modules/services/matching2/";
	private final String PROBLEMIDNAME = "vak_id";
	//other config data
	private final String BOGUSPROJECTID = "99999";

	/**
	 * Constructor 
	 */
	public StudentProjectMatchingProblemConnector() {
		if (advertiseFeatures()[0]) {
			createMockupData();
		}
		numberOfAs = -1;  //invalid value
		BAMatches = new String[0][0];  //invalid (empty) solution
	}//end StudentProjectMatchingProblemConnector
	
	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.ProblemConnector#advertiseFeatures()
	 */
	@Override
	public boolean[] advertiseFeatures() {
		return new boolean[] {
				true,  //Mockup: we deliver configured problem data 
				true,  //File reading/writing: yes
				false, //Webservice get/post: not yet
		};
	}//end advertiseFeatures
	
	public String getWebserviceBaseUrl() {
		return this.WEBSERVICEBASEURL;
	}//end getWebserviceBaseUrl
	
	public String getProblemIdName() {
		return this.PROBLEMIDNAME;
	}//end getProblemIdName
	
	/**
	 * For use during testing: configure fixed problem data which is
	 * ready for "getting".
	 */
	private void createMockupData() {
		switch (0) {
		case 0:
			this.numberOfAs = 4;
			this.BMin = new int[] {0, 0, 0, 0};
			this.BMax = new int[] {1, 1, 1, 1};
			this.ABPreferencesProj = new int[][] {
					{4, 3, 2, 1},
					{3, 2, 1, 2},
					{2, 1, 2, 3},
					{1, 2, 3, 4}
			};
			this.ABPreferencesStud = new int[][] {
					{4, 3, 2, 1},
					{3, 2, 1, 2},
					{2, 1, 2, 3},
					{1, 2, 3, 4}
			};
		case 1:
			this.numberOfAs = 10;
			this.BMin = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			this.BMax = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
			this.ABPreferencesProj = new int[][] {
					{10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
					{9, 8, 7, 6, 5, 4, 3, 2, 1, 2},
					{8, 7, 6, 5, 4, 3, 2, 1, 2, 3},
					{7, 6, 5, 4, 3, 2, 1, 2, 3, 4},
					{6, 5, 4, 3, 2, 1, 2, 3, 4, 5},
					{5, 4, 3, 2, 1, 2, 3, 4, 5, 6},
					{4, 3, 2, 1, 2, 3, 4, 5, 6, 7},
					{3, 2, 1, 2, 3, 4, 5, 6, 7, 8},
					{2, 1, 2, 3, 4, 5, 6, 7, 8, 9},
					{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
			};
			this.ABPreferencesStud = new int[][] {
					{10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
					{9, 8, 7, 6, 5, 4, 3, 2, 1, 2},
					{8, 7, 6, 5, 4, 3, 2, 1, 2, 3},
					{7, 6, 5, 4, 3, 2, 1, 2, 3, 4},
					{6, 5, 4, 3, 2, 1, 2, 3, 4, 5},
					{5, 4, 3, 2, 1, 2, 3, 4, 5, 6},
					{4, 3, 2, 1, 2, 3, 4, 5, 6, 7},
					{3, 2, 1, 2, 3, 4, 5, 6, 7, 8},
					{2, 1, 2, 3, 4, 5, 6, 7, 8, 9},
					{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
			};
		case 2:
			this.numberOfAs = 5;
			this.BMin = new int[] {2, 1, 1, 0};
			this.BMax = new int[] {2, 2, 1, 1};
			this.ABPreferencesProj = new int[][] {
					{4, 3, 2, 1},
					{3, 2, 1, 2},
					{2, 1, 2, 3},
					{2, 1, 2, 3},
					{1, 2, 3, 4},
					{1, 2, 3, 4}
			};
			this.ABPreferencesStud = new int[][] {
					{4, 3, 2, 1},
					{3, 2, 1, 2},
					{2, 1, 2, 3},
					{2, 1, 2, 3},
					{1, 2, 3, 4},
					{1, 2, 3, 4}
			};
		}//end switch
	}//end createMockupData
	
	/*
	 * (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.ProblemConnector#interpretJsonProblem(org.json.JSONObject)
	 */
	protected void interpretJsonProblem(JSONObject json) throws InvalidProblemException {
		//read the checking numbers, and other simple problem settings
		int numberOfProjects = json.getInt("numberOfProjects");
		int numberOfPlaces = json.getInt("numberOfPlaces");
		numberOfAs = json.getInt("numberOfStudents");
		boolean allowPlacesShortage = json.getBoolean("allowPlacesShortage");
		boolean placesShortage = (numberOfAs > numberOfPlaces);
		if (placesShortage && !allowPlacesShortage) {
			throw new InvalidProblemException("The number of students, as declared at the bottom of the file, is larger than the number of available project-places, while it was not allowed.");
		}
		//read projectPlaces and create BMax plus ProjId translation array
		JSONObject pp = json.getJSONObject("projectPlaces");
		BMin = new int[placesShortage ? pp.length() + 1 : pp.length()]; //add 1 bogus project if needed
		BMax = new int[placesShortage ? pp.length() + 1 : pp.length()];
		ProjId = new String[placesShortage ? pp.length() + 1 : pp.length()];
		Map<String, Integer> projNr = new HashMap<String, Integer>(); //reverse of ProjId[]
		Iterator<String> k = pp.keys();
		int sumplaces = 0;
		for (int i=0; i<pp.length(); i++) {
			ProjId[i] = k.next();
			JSONArray minmax = pp.getJSONArray(ProjId[i]);
			if (minmax.length() != 2)
				throw new InvalidProblemException("projectPlaces hash: array value length 2 expected, found: " + minmax.length());
			BMin[i] = minmax.getInt(0);
			BMax[i] = minmax.getInt(1);
			projNr.put(ProjId[i], Integer.valueOf(i)); //enable reverse lookups
			sumplaces += BMax[i];
		}//end for
		//checks
		if (!(numberOfProjects==pp.length() && numberOfPlaces==sumplaces)) {
			throw new InvalidProblemException("The number of projects, or the  number of available places, does not fit the declared value at the bottom of the file.");
		}//end if
		if (placesShortage) { 
			HasPlacesShortage = true;
			//add the bogus project, offering just the missing places
			int i = pp.length();
			ProjId[i] = BOGUSPROJECTID;
			BMin[i] = 0;
			BMax[i] = numberOfAs - numberOfPlaces;
			projNr.put(ProjId[i], Integer.valueOf(i)); //treat it like a normal project unless noted otherwise
			sumplaces += BMax[i];
		}
		//read studentProjectPreferences and studentCategory
		//create ABPreferencesStud and -Proj, ACategory, plus StudId translation array
		JSONObject spp = json.getJSONObject("studentProjectPreferences");
		JSONObject sc = json.getJSONObject("studentCategory");
		ABPreferencesStud = new int[numberOfAs][BMax.length];
		ABPreferencesProj = new int[numberOfAs][BMax.length];
		ACategory = new int[numberOfAs];
		StudId = new String[numberOfAs];
		Iterator<String> kstud = spp.keys();
		if ( numberOfAs != spp.length() ) {
			throw new InvalidProblemException("The number of students does not fit the value declared at the bottom of the file.");
		}
		for (int i=0; i<numberOfAs; i++) {
			StudId[i] = kstud.next();
			//fill ACategory[i] by lookup in sc JSONobject, a hash really
			ACategory[i] = sc.optInt(StudId[i], -1); //-1:invalid default
			if (ACategory[i]== -1) {
				throw new InvalidProblemException("Student " + StudId[i] + " has no category assigned.");
			}
			//fill preferences arrays with default values
			for (int j=0; j<BMax.length; j++) {
				ABPreferencesStud[i][j] = 0;
				ABPreferencesProj[i][j] = 0;				
			}//end for
			//overwrite defaults with given preferences
			//only part of the cells will be revisited
			JSONObject spp1 = spp.getJSONObject(StudId[i]);
			Iterator<String> kproj = spp1.keys();
			while (kproj.hasNext()) {
				String proj = kproj.next();
				if (!projNr.containsKey(proj)) {
					throw new InvalidProblemException("Project "+proj+" chosen by student "+StudId[i]+", but not declared in ProjectPlaces hash.");
				}
				int j = projNr.get(proj); //what cell are we revisiting?
				JSONArray a = spp1.getJSONArray(proj);
				ABPreferencesStud[i][j] = a.getInt(0);
				ABPreferencesProj[i][j] = a.getInt(1);
			}//next proj
		}//next stud
	}//end interpretJsonProblem

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.ProblemConnector#isReadyReading()
	 */
	@Override
	public boolean isReadyReading() {
		if (advertiseFeatures()[0]) {
			return true;  //if mocking is on, then we're ready as soon as the constructor finishes
		}
		else {
			return (numberOfAs >= 1);  //sign that we've read something (but is it a valid problem?)			
		}
	}//end isReadyReading
		
	//after reading the following getters should provide all the stuff!
	
	public int getnumberOfAs() {
		return numberOfAs;
	}//end getnumberOfAs
	
	public int[] getBMax() {
		return BMax;
	}//end getBMax
	
	public int[] getBMin() {
		return BMin;
	}//end getBMin
	
	public int[][] getABPreferencesStud() {
		return ABPreferencesStud;
	}//end getABPreferencesStud
	
	public int[][] getABPreferencesProj() {
		return ABPreferencesProj;		
	}//end getABPreferencesProj
	
	public int[] getACategory() {
		return ACategory;
	}//end getACategory
	
	public boolean hasPlacesShortage() {
		return HasPlacesShortage;
	}
	
	
	//setters for the solution before we can start writing...!
	
	public void setABMatches(String[][] BAMatches) {
		this.BAMatches = BAMatches;
	}//end setABMatches

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.ProblemConnector#isReadyForWriting()
	 */
	@Override
	public boolean isReadyForWriting() {
		return (this.BAMatches.length > 0);
	}//end isReadyForWriting

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.ProblemConnector#solution2JSON()
	 */
	@Override
	protected JSONObject solution2JSON(boolean showtranslation) {
		JSONObject json = new JSONObject();
		if (showtranslation) {
			System.out.println("Solution translated to original db-ids");
			System.out.println("proj-id, stud-id, studpref, projpref");
		}
		for (int i=0; i<this.BAMatches.length; i++) {
			//translate consecutive numbers back to the original db ids
			//transplate project id
			String projid = this.ProjId[Integer.parseInt( this.BAMatches[i][0] )];
			String studid = "0"; //default in case place is empty
			if (
				Integer.parseInt( this.BAMatches[i][1] ) != ManyToOneMatchingProblem.EMPTYPLACE
			) {
				//translate student id
				studid = this.StudId[Integer.parseInt( this.BAMatches[i][1] )];
				if (projid != BOGUSPROJECTID) {
					//store both ids in the json
					json.put(studid, projid); //student-id is key					
				}
			}
			//We can't store the "0" for empty places because: not unique
			//but we can show them on the console.
			//If everything works well it is redundant information, however.
			//Also the bogus project appears below while NOT being included in the json.
			if (showtranslation) {
				System.out.println(projid + " " + studid + " " + this.BAMatches[i][2] + 
						" " + 
						(this.BAMatches[i].length == 4 ? this.BAMatches[i][3]:"")
				);
			}
		}
		return json;
	}//end solution2JSON

}//end class
