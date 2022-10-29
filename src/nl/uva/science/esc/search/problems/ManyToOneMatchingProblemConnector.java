/**
 * 
 */
package nl.uva.science.esc.search.problems;

import org.json.JSONObject;

/**
 * @author kaper
 *
 */
public class ManyToOneMatchingProblemConnector extends ProblemConnector {

	/**
	 * 
	 */
	public ManyToOneMatchingProblemConnector() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.ProblemConnector#isReadyReading()
	 */
	@Override
	public boolean isReadyReading() {
		// TODO Auto-generated method stub
		return false;
	}//end isReadyReading
	
	//after reading the following getters should provide all the stuff!
	
	public int getnumberOfAs() {
		return 0;  //ToDo !
	}//end getnumberOfAs
	
	public int[] getBMax() {
		return new int[] {}; //ToDo !
	}//end getBMax
	
	public int[] getBMin() {
		return new int[] {}; //ToDo!
	}
	
	public int[][] getABPreferences() {
		return new int[][] {}; //ToDo !
	}//end getABPreferencesStud
	

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.ProblemConnector#isReadyForWriting()
	 */
	@Override
	public boolean isReadyForWriting() {
		// TODO Auto-generated method stub
		return false;
	}//end isReadyForWriting

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.problems.ProblemConnector#advertiseFeatures()
	 */
	@Override
	public boolean[] advertiseFeatures() {
		return new boolean[] {
				false, //Mockup: not yet 
				false, //File reading: not yet
				false, //URL reading: not yet
				false, //File writing: not yet
				false  //URL posting: not yet
		};
	}//end advertiseFeatures

	@Override
	public String getWebserviceBaseUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProblemIdName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void interpretJsonProblem(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected JSONObject solution2JSON(boolean showtranslation) {
		// TODO Auto-generated method stub
		return null;
	}
	
}//end class
