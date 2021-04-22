package nl.uva.science.esc.search.views;

/**
 * A WizardComponent is a JPanel (name ending in "Pane") which can be placed on
 * a WizardPagePane.
 * 
 * It has methods to notify this containing Pane of changes in its validity
 * status, and must be able to identify itself in these push notifications.
 * See the WizardPagePane for the "notifyValid" method that one should call!
 * 
 * @author kaper
 *
 */
public interface WizardComponent {
	
	/**
	 * Validate all of the components inputs and return true only if all of them
	 * are valid
	 * @param msg, do we want to show validation messages on the component surface?
	 * @return OK, is the component valid as a whole?
	 */
	public boolean validateAll(boolean msg);
	
}//end interface
