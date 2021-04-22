package nl.uva.science.esc.search.problems;

import nl.uva.science.esc.search.views.Parameter;
import nl.uva.science.esc.reflection.myReflection;;

/**
 * The process of creating a Problem object via the GUI is routed through
 * this class. There are 4 tasks to perform in 3 phases:
 * 
 * phase 1: give the GUI an array of available Problem subtypes
 * (readable descriptions!). A selection within this list is made known via
 * index of the selected type.
 * - method: advertiseSubtypes();
 * 
 * phase 2: providing details of the selected subType to enable the GUI to
 * get the right data from the user (partly by selecting a data source)
 * - method: advertiseParameters(int index);
 * - method: createProblemConnector(int index);
 * 
 * phase 3: create the Problem object
 * - method: createProblem(int index, ProblemConnector c, Parameters[] p)
 * 
 * We need a list of subtypes that you can configure below.
 * To avoid configuration in lots of places, we use tricks from the java 
 * "reflection" package... A "myReflection" library is used to factor out the
 * common parts of this ProblemFactory and the TechniqueFactory.
 *  
 * @author kaper
 *
 */
public class ProblemFactory {
	//In what package are we? this class needs to know its package because of introspection
	private static final String PACKAGE0 = "nl.uva.science.esc.search.problems";
	//
	//Configuration of Problem subtypes!
	//
	//   Problem classname without "Problem"
	private static final String[] typenames = { 
		"ManyToOneMatching", "StudentProjectMatching"
	};
	//   Does the problem subtype have a connector?
	private static final boolean[] hasConnector = { 
		true, true
	};
	//   Descriptions of the subtypes
	private static final String[] descriptions = { 
		"Many-to-one matching", "Student-project matching"
	};
	//No configuration below this line !
	
	//Static methods

	/**
	 * Return readable descriptions of Problem subtypes in this package 
	 */
	public static String[] advertiseSubtypes() {
		return descriptions;
	}//end advertiseSubtypes
	
	/**
	 * Get an array of Problem parameters to be filled out in a GUI screen 
	 * @param typeindex, the Problem subtype from which we want the parameters
	 * @return parameters array, or null: indicates configuration problem
	 */
	public static Parameter[] advertiseParameters(int typeindex) {
		String classname = PACKAGE0 + "." + typenames[typeindex] + "Problem";
		return (Parameter[]) myReflection.invokeStaticMethod(
				classname, "advertiseParameters", new Object[] {} 
				);
	}//end advertiseParameters
	
	/**
	 * Instatiate a ProblemConnector for the chosen Problem subtype
	 * @param typeindex, index of chosen subtype
	 * @return, new ProblemConnector, or null if connector does not exist
	 */
	public static ProblemConnector createProblemConnector(int typeindex) {
		if (hasConnector[typeindex]) {
			String classname = PACKAGE0 + "." + typenames[typeindex] + 
					"ProblemConnector";
			return (ProblemConnector)myReflection.instantiateNoArgs(classname);
		}
		else return null;
	}//end getProblemConnector
	
	/**
	 * Create a Problem object
	 * @param typeindex, index of the Problem subtype to create
	 * @param pc, ProblemConnector that has data arrays for the Problem
	 * @param p, array of GUI supplied parameter-values (Strings) for the Problem
	 * @return the Problem, or null: indicates configuration error
	 */
	public static Problem createProblem(
		int typeindex, ProblemConnector pc, String[] p
	) {
		String classname = PACKAGE0 + "." + typenames[typeindex] + "Problem";
		return (Problem)myReflection.instantiate(
				classname, 
				new Class[] { ProblemConnector.class, String[].class }, 
				new Object[] { (Object)pc, (Object)p }
		);
	}//end createProblem
	
}//end class
