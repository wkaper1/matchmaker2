package nl.uva.science.esc.search.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import nl.uva.science.esc.matchmaker.Controller.InputType;

/**
 * A parameter fillin pane has a list of parameters that it must ask to the 
 * user. Parameter-names become label texts. Parameter values are to be
 * retrieved from JTextField's. Labels and boxes are in a 2-column layout.
 * Above and below the parameter table there is room for a sentence of text
 * 
 * For each parameter it is known whether it is required (true/false) and
 * of what type it is (see the Controller.InputType enum for possible types).
 * 
 * After a field looses focus, a TypeCheck is done on the contents of that 
 * field (and of the others), and a validation error displayed if necessary.
 * Results of typechecks are cached. As soon as all fields validate, the 
 * containing WizardPage (or its Controller?) is notified.
 * If one or more fields become invalid again, an opposing notification is done.
 * 
 * @author kaper
 *
 */
public class ParameterFillinPane extends JPanel implements WizardComponent {
	private WizardPagePane owner; //the page on which this component exists
	private String abovetext;
	private String belowtext;
	private Parameter[] parameters;
	private GridLayout gridLayout1;
	private GridLayout gridLayout2;
	private JPanel innerpanel; //panel inside this panel that has the table
	private JTextField[] fields;
	//private ChangeHandler[] handlers; //one handler for each field (not necessary to store them!)
	//cached validation results
	private boolean valid[]; //for each field: does it have a valid value?
	private JLabel[] messages; //validation messages for each field
	private int numinvalid;  //how many invalid fields remain?
	//validation message texts
	public static final String REQUIRED = "Required field";
	public static final String INT0 = "An integer number is required";
	public static final String POSITIVEINT0 = "A positive integer is required";
	public static final String FLOAT0 = "A decimal number is required";
	public static final String FRACTION0 = "A number in [0,1], like: 0.99";
	//possible validation errors
	private enum ErrorType {NO_ERROR, REQUIRED_ERROR, INT_ERROR, POSITIVEINT_ERROR, FLOAT_ERROR, FRACTION_ERROR};
	
	/**
	 * The constructor builds the view and takes care of initial contents 
	 * @param abovetext
	 * @param belowtext
	 * @param parameternames
	 */
	public ParameterFillinPane(
		WizardPagePane owner,
		String abovetext, String belowtext, Parameter[] parameters
	) {
		this.owner = owner;
		this.abovetext = abovetext;
		this.belowtext = belowtext;
		this.parameters = parameters;
		this.valid = new boolean[parameters.length];
		this.messages = new JLabel[parameters.length];
		numinvalid = parameters.length; //assume all invalid before test
		//decide on the overall layout: which pieces do we really have?
		int i=0;
		if (abovetext!="") i++;
		if (parameters.length > 0) i++;
		if (belowtext!="") i++;
		gridLayout1 = new GridLayout(i, 1, 5, 5);
		setLayout(gridLayout1);
		//fillin the pieces, max. 3
		if (abovetext!="") {
			add(new JLabel(abovetext));
		}//end if
		if (parameters.length > 0) {
			innerpanel = new JPanel();
			fields = new JTextField[parameters.length];
			gridLayout2 = new GridLayout(parameters.length, 3, 5, 5);
			innerpanel.setLayout(gridLayout2);
			//fill the innerpanels grid: the table of parameters
			for (int j=0; j<parameters.length; j++) {
				//first column: the label with the name
				innerpanel.add(new JLabel(parameters[j].name));
				//second column: the JTextField for the value
				fields[j] = new JTextField(10);
				fields[j].addActionListener(new ChangeHandler(j));
				innerpanel.add(fields[j]);
				//third column: room for the error message
				messages[j] = new JLabel();
				innerpanel.add(messages[j]);
			}//end for
			add(innerpanel);
		}//end if
		if (belowtext!="") {
			add(new JLabel(belowtext));
		}//end if
		//now that we have all the JTextFields, we can validate:
		validateAll(false); //establish baseline! no messages yet
	}//end constructor
	
	/**
	 * Listen for change or lostFocus events on the JTextField objects 
	 */
	private class ChangeHandler implements ActionListener {
		int i; //index of listened field in fields array
		public ChangeHandler(int i) {
			this.i = i;
		}//end constructor
		public void actionPerformed(ActionEvent e) {
			//ToDo: kritisch het event filteren... hebben we 't juiste event?
			validate(i, true);
		}//end actionPerformed
	}//end private class RefreshHandler
	
	/**
	 * Validate one of the JTextField objects according to the rules expressed
	 * in the parameters array and store the result in the valid array.
	 * @param i, index of the field to validate
	 * @param msg, do we want an error message?
	 */
	private boolean validate(int i, boolean msg) {
		String value = fields[i].getText();
		ErrorType err = ErrorType.NO_ERROR;
		if (value.length()>0) {
			switch (parameters[i].type) {
			case INT: 
				if (!isInt(value)) {
					err = ErrorType.INT_ERROR;
				}
				break;
			case POSITIVEINT:
				if (isInt(value)) {
					if (Integer.parseInt(value) < 0) {
						err = ErrorType.POSITIVEINT_ERROR;											
					}
				}
				else {
					err = ErrorType.POSITIVEINT_ERROR;					
				}
				break;
			case FLOAT:
				if (!isFloat(value)) {
					err = ErrorType.FLOAT_ERROR;					
				}
				break;
			case FRACTION:
				if (isFloat(value)) {
					float f = Float.parseFloat(value);
					if (f<0 || f>1) {
						err = ErrorType.FRACTION_ERROR;											
					}
				}
				else {
					err = ErrorType.FRACTION_ERROR;					
				}
			case STRING:
				break;
			}//end switch
		}//end if
		else {
			if (parameters[i].required) {
				err = ErrorType.REQUIRED_ERROR;
			}//end if
		}//end if
		if (msg) { //create message for error, if there is one
			switch (err) {
			case REQUIRED_ERROR: messages[i].setText(REQUIRED);
			case INT_ERROR: messages[i].setText(INT0);
			case POSITIVEINT_ERROR: messages[i].setText(POSITIVEINT0);
			case FLOAT_ERROR: messages[i].setText(FLOAT0);
			case FRACTION_ERROR: messages[i].setText(FRACTION0);
			case NO_ERROR: messages[i].setText("");
			}//end switch
		}//end if
		boolean oldvalid = valid[i];
		int oldnuminvalid = numinvalid;
		valid[i] = (err == ErrorType.NO_ERROR) ? (true) : (false) ;
		//register a change
		if (oldvalid && ! valid[i]) numinvalid++;
		if (valid[i] && ! oldvalid) numinvalid--;
		//if all is well while previously it wasn't: shout horay!
		//the reverse is also a possibility
		if (numinvalid==0) {
			if (oldnuminvalid!=0) { //change from invalid to valid
				owner.notifyValid(true);				
			}
			return true;
		}
		else {
			if (oldnuminvalid==0) { //change from valid to invalid
				owner.notifyValid(false);								
			}
			return false;			
		}
	}//end validate
	
	/**
	 * Validate all of the fields
	 * Use it (1) to establish the initial validation state (baseline)
	 * (2) for a final validation round from scratch
	 * 
	 * @param msg, do we want error messages?
	 * @return
	 */
	public boolean validateAll(boolean msg) {
		//assume invalid before we have proof of validity
		for (int i=0; i<parameters.length; i++) {
			valid[i] = false;
		}//next i
		numinvalid = parameters.length;
		boolean valid1 = false;
		for (int i=0; i<parameters.length && valid1==false; i++) {
			valid1 = validate(i, msg);
		}//next i
		return valid1;
	}//end validateAll
	
	/**
	 * Determine if a String s can be parsed as an int
	 * @param s, string to test
	 * @return
	 */
	private boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			return false;
		}//end try
		return true;
	}//end isInt
	
	/**
	 * Determine if a String s can be parsed as a float
	 * @param s, string to test
	 * @return
	 */
	private boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
		}
		catch (NumberFormatException e) {
			return false;
		}//end try
		return true;		
	}//end isFloat

	/**
	 * At a certain time we want to ask for the values.
	 * @return an array with the values, in same order as names array
	 */
	public String[] getParameterValues() {
		String[] values = new String[parameters.length];
		for (int j=0; j<parameters.length; j++) {
			values[j] = fields[j].getText();
		}//end for
		//casting the parameters to the right type must be done in the
		//controller?
		return values;
	}//end getParameterValues
	
}//end class ParameterFillinPane
