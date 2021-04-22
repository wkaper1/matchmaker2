package nl.uva.science.esc.search.views;

import java.awt.event.ActionEvent;

/**
 * The main controller for this application has to implement this
 * WizardController interface. It can then respond to the Wizard buttons:
 * Prvious, Next, Cancel / Finish.
 * @author kaper
 *
 */
public interface WizardController {
	
	/**
	 * The controller is notified by the currently shown page that it has
	 * become valid / invalid. (Use: e.g. to enable / disable the Next button)
	 * @param valid
	 */
	public void pageIsValid(boolean valid);
	
	/**
	 * React to one of the three Wizard buttons: Previous. Next, Cancel / Finish
	 * @param e, which button was pushed
	 */
	public void buttonPushed(String actioncommand, int currentpage);

}//end interface
