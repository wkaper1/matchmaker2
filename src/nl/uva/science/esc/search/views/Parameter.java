package nl.uva.science.esc.search.views;

import nl.uva.science.esc.matchmaker.Controller.InputType;

/**
 * A parameter object is used by model objects to advertise their parameters
 * to the GUI layer. There are 3 properties:
 * - (String) parametername
 * - (boolean) required
 * - (InputType) type
 * 
 * Possible InputTypes can be found on the main Controller
 * This class is just a messenger so we'll not take the trouble of data 
 * encapsulation.
 * 
 * @author kaper
 *
 */
public class Parameter {
	public String name;
	public boolean required;
	public InputType type;
	
	public Parameter(String name, boolean required, InputType type) {
		this.name=name;
		this.required=required;
		this.type=type;
	}//end constructor

}//end class
