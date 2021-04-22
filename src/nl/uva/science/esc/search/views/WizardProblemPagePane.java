package nl.uva.science.esc.search.views;

import nl.uva.science.esc.matchmaker.Controller;

/**
 * This Wizard page collects all data needed to construct a Problem. 
 * It contains 3 components which are added in 2 phases:
 * 
 * phase 1:
 * - TypeChooserPane is visible and user chooses a Problem subtype
 * 
 * phase 2:
 * - ProblemConnectorPane with subtype-specific connector
 * - ParameterFillinPane with subtype-specific parameters
 * are filled by the user
 * 
 * final phase, after pressing next:
 * The Controller collects the data (bypassing this class) and constructs the Problem
 * 
 * @author kaper
 */
public class WizardProblemPagePane extends WizardTypeChooserPagePane {

	/**
	 * Constructor
	 */
	public WizardProblemPagePane(Controller c) {
		super(c);
	}//end WizardProblemPagePane
	
	/**
	 * Initialize page for phase 1
	 * @param chooser
	 */
	public void initPhase1(TypeChooserPane chooser) {
		addLabelContentPair("Choose a problem type", chooser, 0);
	}//end addProblemChooser
	
	/**
	 * Listener for change in the "chooser"
	 * On each change, we ask the controller to re-initialize phase 2
	 * @see nl.uva.science.esc.search.views.WizardTypeChooserPagePane#notifyChange(int)
	 */
	@Override
	public void notifyChange(int selectedindex) {
		controller.phase2WP0(selectedindex);
	}//end notifyChange

	/**
	 * Initialize page for phase 2
	 * @param reader
	 * @param params
	 */
	public void initPhase2(ProblemConnectorPane reader, ParameterFillinPane params) {
		addLabelContentPair("Get data for problem", reader, 1);		
		addLabelContentPair("Problem parameters", params, 2);		
	}//end initPhase2

}//end class
