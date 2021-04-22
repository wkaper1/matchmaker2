package nl.uva.science.esc.search.problems;

import java.net.*;
import java.io.*;

import org.json.*;

/**
 * A ProblemConnector provides the (typically) large arrays which are needed
 * in defining a Problem.
 * 
 * It gets those arrays from reading a file, from requesting data
 * from a webservice, or (by way of mockup) from constant arrays stored in
 * the "connector".
 * 
 * Reversely, a ProblemConnector can write the solution of a Problem to a file
 * or can post it back to the webservice.
 * 
 * Problem Connectors are either ready or not yet ready to provide data for a
 * new problem. If not ready, buttons should be enabled to open a file, or to
 * fetch a problem from a webservice. For this purpose, ProblemConnectors 
 * have their own GUI component: the ProblemConnectorPane. It communicates 
 * with this abstract ProblemConnector superclass.
 * 
 * Reversely, problem connectors are either ready or not ready to send a solution 
 * somewhere.
 * 
 * This superclass has corresponding subclasses for each Problem subtype.
 * Typically, each problem type needs another filestructure, which must be trans-
 * lated into a different set of arrays.
 * 
 * The webservice to communicate with is expected to offer 3 functions
 * with the following specifications:
 * - GetProblemList
 * GET-Parameters: username, password, for logging in
 * Returns: JSON hash { String problemid : String description }
 * - GetProblem
 * GET-Parameters: username, password, problemid
 * Returns: JSON object containing a description of the problem
 * (The same object could instead be read from a file)
 * - PostSolution
 * POST-parameters: solution, username, password, problemid, numrows
 * solution: JSON hash or array containing the solution
 * username, password for logging in
 * problemid, for making sure the solution is matched with the right problem
 * numrows, number of rows in JSON hash or array, to check transfer errors
 * (Instead, just the solution JSON could be written to a file)
 * 

 * @author kaper
 *
 */
public abstract class ProblemConnector {
	//webservice control data
	public static final String GETPROBLEMLIST = "GetProblemList.php";
	public static final String GETPROBLEM = "GetProblem.php";
	public static final String POSTSOLUTION = "PostSolution.php";
	private String username; //login data, ask once, use 3 times
	private String password;
	private String problemid; //remember it between GET and POST
	
	//informative methods
	
	/**
	 * Return a boolean array that indicates the presence of absence 
	 * of these possible ProblemConnector features:
	 * 0 - mockup feature: serve preconfigured problem data
	 * 1 - file reading / writing
	 * 2 - webservice get / post
	 * @return
	 */
	public abstract boolean[] advertiseFeatures();
	
	/**
	 * Is the connector ready to provide the data for a new Problem?
	 * @return
	 */
	public abstract boolean isReadyReading();
	
	/**
	 * Are all data set (by subclass setters) to start writing a solution?
	 * @return
	 */
	public abstract boolean isReadyForWriting();
	
	/**
	 * @return the webservice base URL
	 */
	public abstract String getWebserviceBaseUrl();
	
	/**
	 * @return name of the get-parameter that the problem-specific webservce
	 * uses to distinguish the problems
	 */
	public abstract String getProblemIdName();
	
	//setters
	
	/**
	 * Set the username and password for connetcting to the webservice 
	 * @param username
	 * @param password
	 */
	public void setLogindata(String username, String password) {
		this.username = username;
		this.password = password;
	}//end setLogindata
	
	/**
	 * Only web service connector actions from this class should set the problemid
	 * @param problemid
	 */
	private void setProblemid(String problemid) {
		this.problemid = problemid;
	}//end setProblemid
	
	//connector actions
	
	/**
	 * Phase 1 of the webservice choreography:
	 * Get a list of problems that are available for download
	 * @return JSON hash { problemid : description }
	 */
	public JSONObject getProblemList() {
		//Do the http download and read it into JSON
		JSONObject json = null;
		try {
			URL url = new URL(getWebserviceBaseUrl()+"/"+GETPROBLEMLIST);
			URLConnection con = url.openConnection();
			con.addRequestProperty("username", username);
			con.addRequestProperty("password", password);
			BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream() ));
			json = new JSONObject(new JSONTokener(in)); //zo stream je!
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return json; //leak JSON dependency...
	}//end getProblemList
	
	/**
	 * Phase 2: download a specific problem 
	 * and store it in problem-specific subclass of this class
	 * @param problemid to identify the problem
	 * @throws InvalidProblemException 
	 */
	public void getProblemFromWeb(String problemid) throws InvalidProblemException {
		//Do the http download and read it into JSON
		JSONObject json = null;
		try {
			URL url = new URL(getWebserviceBaseUrl()+"/"+GETPROBLEM);
			URLConnection con = url.openConnection();
			con.addRequestProperty("username", username);
			con.addRequestProperty("password", password);
			con.addRequestProperty(getProblemIdName(), problemid); //extra property...
			BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream() ));
			json = new JSONObject(new JSONTokener(in)); //zo stream je!
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//Decode the json object in problem specific way
		interpretJsonProblem(json);
		//Remember which problem we downloaded
		setProblemid(problemid);
	}//end getProblemFromWeb
	
	/**
	 * Instead of using the webservice the problem can also be read directly
	 * from a file. In that case we should take care not to upload the
	 * solution to any webservice
	 * @param pathname, of the file decribing the problem
	 * @throws InvalidProblemException 
	 */
	public void getProblemFromFile(String pathname) throws InvalidProblemException {
		File f = new File(pathname);
		JSONObject json = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(f) ));
			json = new JSONObject(new JSONTokener(in)); //zo stream je!
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Decode the json object in problem specific way
		interpretJsonProblem(json);
		//Remember that we didn't get the problem from the web
		setProblemid(""); 
	}//end getProblemFromFile
	
	/**
	 * Interpret a JSONObject that contains the description of a problem
	 * and store the results in the problem specific ProblemConnector subclass
	 * @param json
	 * @throws InvalidProblemException 
	 */
	protected abstract void interpretJsonProblem(JSONObject json) throws InvalidProblemException;
	
	/**
	 * Package the solution as found in the problems specific subclass into a 
	 * JSONObject
	 * @param showtranslation, dump the translated solution also to the console
	 * @return json
	 */
	protected abstract JSONObject solution2JSON(boolean showtranslation);

	/**
	 * Write the solution to a file, using the JSONObject that the subclass
	 * is able to provide 
	 * @param showtranslation, dump the translated solution also to the console
	 * @param pathname, path to write the file to
	 */
	public void writeSolutionToFile(String pathname, boolean showtranslation) {
		JSONObject json = solution2JSON(showtranslation);
		try {
			PrintWriter out = new PrintWriter(pathname);
			json.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}//end writeSolutionToFile
	
	/**
	 * Post the solution to a preconfigured web service, using the JSONObject
	 * that the subclass is able to provide
	 */
	public void writeSolutionToWeb() {
		//ToDo, use stored problemid, error if problemid==""
	}//end writeSolutionToWeb

}//end class
