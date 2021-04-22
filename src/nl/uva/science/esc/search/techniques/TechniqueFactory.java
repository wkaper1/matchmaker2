package nl.uva.science.esc.search.techniques;

import nl.uva.science.esc.search.views.Parameter;
import nl.uva.science.esc.reflection.myReflection;;

/**
 * The process of creating a Technique object via the GUI is routed through
 * this class. There are 3 tasks to perform in 3 phases:
 * 
 * phase 1: give the GUI an array of available Technique subtypes
 * (readable descriptions!). A selection within this list is made known via
 * index of the selected type.
 * - method: advertiseSubtypes();
 * 
 * phase 2: providing details of the selected subType to enable the GUI to
 * get the right data from the user
 * - method: advertiseParameters(int index);
 * 
 * phase 3: create the Technique object
 * - method: createTechnique(int index, Parameters[] p)
 * 
 * We need a list of subtypes that you can configure below.
 * To avoid configuration in lots of places, we use tricks from the java 
 * "reflection" package... A library is used to make this more convenient.
 * 
 * This class resembles the ProblemFacory class A LOT ! Biggest difference is that
 * an analogue of a ProblemConnector does not exist here.
 * 
 * @author kaper
 *
 */
public class TechniqueFactory {

	//in what package are we? this class needs to know its package because of introspection
	private static final String PACKAGE0 = "nl.uva.science.esc.search.techniques";
	//
	//configuration of Technique subtypes!
	private static final String[] typenames = { //Technique classname without "Technique"
		"BreadthFirstSearch", "DepthFirstSearch", "SimulatedAnnealing"
	};
	private static final String[] descriptions = {
		"Breadth-first search", "Depth-first search", "Simulated annealing"
	};
	//No configuration below this line !
	
	//Static methods

	/**
	 * Return readable descriptions of Technique subtypes in this package 
	 */
	public static String[] advertiseSubtypes() {
		return descriptions;
	}//end advertiseSubtypes
	
	/**
	 * Get an array of Technique parameters to be filled out in a GUI screen 
	 * @param typeindex, the Technique subtype from which we want the parameters
	 * @return parameters array, or null: indicates configuration problem
	 */
	public static Parameter[] advertiseParameters(int typeindex) {
		String classname = PACKAGE0 + "." + typenames[typeindex] + "Technique";
		return (Parameter[]) myReflection.invokeStaticMethod(
				classname, "advertiseParameters", new Object[] {} 
				);
	}//end advertiseParameters
	
	/**
	 * Create a Technique object
	 * @param typeindex, index of the Technique subtype to create
	 * @param p, array of GUI supplied parameter-values (Strings) for the Technique
	 * @return the Technique, or null: indicates configuration error
	 */
	public static Technique createTechnique(
		int typeindex, String[] p
	) {
		String classname = PACKAGE0 + "." + typenames[typeindex] + "Technique";
		return (Technique)myReflection.instantiate(
				classname, 
				new Class[] { String[].class }, 
				new Object[] { (Object)p }
		);
	}//end createTechnique	

}//end TechniqueFactory
