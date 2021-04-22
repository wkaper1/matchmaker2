package nl.uva.science.esc.search.views;

import nl.uva.science.esc.matchmaker.Controller;

/**
 * Two of the wizards pages each have to construct an object.
 * 
 * They start with a selectbox for choosing a subtype.
 * After a choice is made, the rest of the page is filled with components
 * whose contents are tailored to the chosen subtype.
 * 
 * This is both pages superclass, it prescribes a method that listens for
 * changes in the subtype selectbox.
 * @author kaper
 *
 */
public abstract class WizardTypeChooserPagePane extends WizardPagePane {
	
	public WizardTypeChooserPagePane(Controller controller) {
		super(controller);
	}//end constructor

	/**
	 * A contained TypeChooserPane will call this method to notify that a
	 * new type choice has been made
	 * @param selectedindex, the index of the chosen option
	 */
	public abstract void notifyChange(int selectedindex);

}//end class
