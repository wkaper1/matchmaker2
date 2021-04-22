package nl.uva.science.esc.search.problems;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nl.uva.science.esc.search.views.Parameter;

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
 * To avoid configuration in lots of places, we use tricks from the "inspection"
 * package...! They look a bit dirty because of all the possible Exceptions...
 *  
 * @author kaper
 *
 */
public class ProblemFactory_bk1 {
	//in what package are we? this class needs to know its package because of introspection
	private static final String PACKAGE0 = "nl.uva.science.esc.search.problems";
	//
	//configuration of Problem subtypes!
	private static final String[] typenames = { //Problem clasname without "Problem"
		"ManyToOneMatching", "StudentProjectMatching"
	};
	private static final boolean[] hasConnector = {
		true, true
	};
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
		//Get the class...
		String classname = PACKAGE0 + "." + typenames[typeindex] + "Problem";
		Class cls = null;
		try {
			cls = Class.forName(classname);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}//end try
		//Get the static method
		Method m = null;
		try {
			m = cls.getMethod("advertiseParameters", null);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}//end try
		//Invoke the method and get the result
		Object o = null;
		try {
			o = m.invoke(null, null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}//end try
		return (Parameter[])o;
	}//end advertiseParameters
	
	/**
	 * Instatiate a ProblemConnector for the chosen Problem subtype
	 * @param typeindex, index of chosen subtype
	 * @return, new ProblemConnector, or null if connector does not exist
	 */
	public static ProblemConnector createProblemConnector(int typeindex) {
		if (hasConnector[typeindex]) {
			String classname = PACKAGE0 + "." + typenames[typeindex] + "ProblemConnector";
			//Get the Connector class
			Class cls = null;
			try {
				cls = Class.forName(classname);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}//end try
			//Instantiate it via the no-argument constructor
			ProblemConnector pc = null;
			try {
				pc = (ProblemConnector) cls.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}//end try
			return pc;			
		}
		else return null;
	}//end getProblemConnector
	
	/**
	 * Create a Problem object
	 * @param typeindex, index of the Problem subtye to create
	 * @param pc, ProblemConnector that has data arrays for the Problem
	 * @param p, array of GUI supplied parameter-values (Strings) for the Problem
	 * @return the Problem, or null: indicates configuration error
	 */
	public static Problem createProblem(
		int typeindex, ProblemConnector pc, String[] p
	) {
		//Get the class...
		String classname = PACKAGE0 + "." + typenames[typeindex] + "Problem";
		Class cls = null;
		try {
			cls = Class.forName(classname);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}//end try
		//Get the constructor of the right type
		Constructor c = null;
		try {
			c = cls.getConstructor(new Class[] {
					ProblemConnector.class, String[].class
			});
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}//end try
		//Invoke the constructor and return Problem
		Problem pp = null;
		try {
			pp = (Problem) c.newInstance(new Object[] {
					(Object)pc, (Object)p
			});
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}//end try
		return pp;
	}//end createProblem
	
}//end class
