package nl.uva.science.esc.search.views;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nl.uva.science.esc.search.problems.ProblemConnector;

/**
 * This is the WizardComponent that has a ProblemConector as its model object
 * 
 * The component can be initialised into two modes: (A) getting problem
 * data, or (B) sending problem solution.
 * If you need both modes: create two instances of this class.
 * 
 * Depending on the advertised features of the ProblemConnector we will
 * show various controls:
 * 
 * These features will be supported for the "PROBLEMGETTER" mode (A):
 * 0 - mocking: If mocking is on we will show a sentence that tells the user
 * about it. Also, this component will be "valid" immediately after construction,
 * while it will be invalid initially, if mocking is off.
 * 1 - file reading: we will show a browse button to open a file. The opening
 * will start the reading. Any parse errors will be presented next to the browse
 * tool. Only after a succesful parse, the component is valid.
 * 2 - URL reading: we will show a proposed preconfigured URL, together with
 * two "GET" buttons (TODO: plus 2 fields for login data). After the first GET
 * button is pushed, a list of matching problems appears from which you have to
 * select one. The second GET button then becomes active. Apart from parse errors 
 * also login errors and other http errors must be shown to the user. 
 * 
 * If various of the features 0, 1, 2 are simultaneously present, then the
 * component is valid if one of the features reaches success.
 * The last-used feature determines the data that will be delivered. So the 
 * mocking data will be overruled if one of the other methods reaches success.
 * 
 * These features will be supported for the "SOLUTIONSENDER" mode (B):
 * 3 - file writing: An input to type a filename with a Save button next to it.
 * The component will be valid after one succesful Save.
 * 4 - URL posting: An input that shows a suggested URL with a POST button
 * next to it (TODO: plus the login data that were previously recorded). We need 
 * to be able to show http-errors. The webservice must provide an explicit "success" 
 * response that we can recognise. Only after recognising the response will the 
 * component be valid.
 * 
 * The webservice is assumed to ask login data as GET paramaters on each request
 * The ProblemConnector object caches those, between PROBLEMGET-ting and 
 * SOLUTIONSEND-ing.
 * 
 * In B-mode, the component is always invalid initially because it assumes
 * that it helds precious unsaved solution data. If features 3 and 4 are 
 * present simultaneously, the component is valid after one of them reaches
 * success.
 * 
 * Big ToDo: == at the moment only the mock feature is supported! == 
 * 
 * @author kaper
 *
 */
public class ProblemConnectorPane extends JPanel implements WizardComponent {
	private WizardPagePane owner; //the page on which this component exists
	private ProblemConnector pc;  //the model object for this view
	private boolean[] pcfeatures; //proxy for a rather constant property...
	public enum Modes {PROBLEMGETTER, SOLUTIONSENDER};
	private final Modes mode;
	private BoxLayout boxl;
	private static final String MOCKMESSAGE = 
		"<html><div style='width:300px'>The mocking feature is on: <br>" + 
		"if no other data is provided the mock" +
		"data will be used. The component is valid.</div></html>";
	
	public ProblemConnectorPane(
		WizardPagePane owner, ProblemConnector pc, Modes mode
	) {
		this.owner = owner;
		this.pc = pc;
		this.pcfeatures = pc.advertiseFeatures();
		this.mode = mode; //it's final!
		this.boxl = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(boxl);
		if (this.mode == Modes.PROBLEMGETTER) {
			if (pcfeatures[0]) {
				JLabel l = new JLabel(MOCKMESSAGE);
				this.add(l);
			}
			if (false) {
				//ToDo: realise features 1 and 2
			}
		}//end if mode==PROBLEMGETTER
		else if (this.mode == Modes.SOLUTIONSENDER) {
			//ToDo: realise the other mode, features 3, 4
		}//end if mode = SOLUTIONSENDER
		this.validateAll(false);
	}//end constructor
	
	//the OR in the validation will make the notification idea work
	//really differently (compare ParameterFillinPane), I need to redesign it...
	
	private boolean validateMock() {
		if (pcfeatures[0]) {
			owner.notifyValid(true); //for this one feature it will be OK, but for the other ones...
			return true;
		}
		else return false;
	}//end alidateMockFeature
	
	private boolean validateFileReading() {
		return false; //ToDo !
	}//end validateFileReading
	
	private boolean validateURLReading() {
		return false; //ToDo !
	}//end validateURLReading

	@Override
	public boolean validateAll(boolean msg) {
		boolean valid = false;
		if (mode == Modes.PROBLEMGETTER) {
			valid = (
				validateMock() || validateFileReading() || validateURLReading()
			);
		}
		else {
			//ToDo: realise the other mode, features 3, 4
		}//end if
		return valid;
	}//end validateAll

}//end ProblemConnectorPane
