package nl.uva.science.esc.search.views;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This WizardComponent contains a single selectbox.
 * 
 * Initially, no choice is made and the component is invalid. When the user
 * makes a choice the component goes to a valid state once.
 * When the choice is changed, the state remains valid (no new valid notification)
 * 
 * This WizardComponent will be used on page1 of the wizard for selecting a
 * Problem subtype, and on page2 to select a Technique subtype. It is a
 * separate component because the rest of the containing page can be initialised
 * only after a choice has been made.
 * 
 * The owner WizardPage needs to expect two types of notifications: 
 * - notifyValid (see WizardPagePane superclass)
 * - notifyChange (see SelectboxListener interface)
 * @author kaper
 *
 */
public class TypeChooserPane extends JPanel implements WizardComponent {
	private static final String REQUIREDMSG = "A choice is required" ;
	private WizardTypeChooserPagePane owner;  //the wizardpage where this component is on
	private JList<String> chooser; //the selectbox
	private int selectedindex;     //index of selected option
		//is also used for recognising which one was chosen
	private JLabel errormsg;       //room to display an error message
	private boolean valid;         //is this component valid
	
	/**
	 * Constructor
	 * @param owner, the containing wizard page
	 * @param options, texts options to choose between
	 */
	public TypeChooserPane(WizardTypeChooserPagePane owner, String[] options) {
		this.owner = owner;
		BoxLayout boxl = new BoxLayout(this, BoxLayout.LINE_AXIS);
		this.setLayout(boxl);
		//first box: the selection list
		chooser = new JList<String>(options);
		chooser.setVisibleRowCount(options.length);
		chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chooser.addListSelectionListener(new ChoiceHandler());
		add(new JScrollPane(chooser));
		//second box: error message if user forgets to choose
		errormsg = new JLabel("");  //no error yet!
		add(errormsg);
		//validation duties
		this.valid = false; //invalid while no choice has been made
	}//end constructor
	
	/**
	 * Listener, it sends the two notifications
	 */
	private class ChoiceHandler implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			boolean oldvalid = valid;
			selectedindex = e.getFirstIndex();
			valid = true;
			if (!oldvalid) owner.notifyValid(true);
			owner.notifyChange(selectedindex);
		}//end valueChanged
	}//end inner class choiceHandler
	
	public int getSelectedIndex() {
		return selectedindex;
	}//end getSelectedIndex

	/* (non-Javadoc)
	 * @see nl.uva.science.esc.search.views.WizardComponent#validateAll(boolean)
	 */
	@Override
	public boolean validateAll(boolean msg) {
		if (valid) {
			owner.notifyValid(true);  //act like we validated from scratch			
		}
		else { //not valid!
			if (msg) errormsg.setText(REQUIREDMSG);
		}//end if
		return valid;
	}//end validateAll

}//end class
