package nl.uva.science.esc.matchmaker;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import nl.uva.science.esc.search.problems.Problem;
import nl.uva.science.esc.search.problems.ProblemConnector;
import nl.uva.science.esc.search.problems.ProblemFactory;
import nl.uva.science.esc.search.techniques.Technique;
import nl.uva.science.esc.search.views.*;
import nl.uva.science.esc.search.views.ProblemConnectorPane.Modes;

/**
 * This is the main controller of the ProblemSolver application.
 * It connects to the WizardPane as its view.
 * It handles one Problem object and one Technique object per run as its
 * main model objects.
 * 
 * A single run of the ProblemSolver has 4 phases which coincide with the
 * pages of the wizard: (0) construct the Problem object from user input plus a
 * file, (1) construct the Technique object that will solve the problem
 * (2) put both of them to work and watch the solution get better and better,
 * (3) after a manual or automatic stop: save the solution.
 * 
 * @author kaper
 *
 */
public class Controller implements WizardController {
	//Application level constants:
	//Constants for declaring datatypes of data which is input in GUI screens
	public enum InputType { STRING, INT, FLOAT, POSITIVEINT, FRACTION };
	//
	//Application level model objects
	private Problem problem;
	private ProblemConnector problemconnector;
	private Technique technique;
	//
	//Our all containing view object
	private WizardPane wp;
	//
	//Wizard pages appearing on the WizardPane
	private WizardProblemPagePane wp1; //wp2, wp3, wp4 will follow
	//
	//GUI components on various wizard pages
	//wp1:
	private TypeChooserPane problemTypeChooser;
	private ProblemConnectorPane problemReader;
	private ParameterFillinPane problemParams;
	//wp2:
	private TypeChooserPane techniqueTypeChooser;
	private ParameterFillinPane techniqueParams;
	//wp3
	private PropertyMonitorPane techniqueMon;
	private SolutionMonitorPane solutionMon;
	private StopRunningPane stopIt;
	//wp4
	private ProblemConnectorPane solutionWriter;
	
	/**
	 * Constructor of this controller
	 */
	public Controller() {
		wp = new WizardPane(this, 4, "Problem Solver");
		wp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		wp.setSize(640, 480);
		wp.setVisible(true);
		//construct and add un-initialised stubs of all 4 pages
		wp1 = new WizardProblemPagePane(this);
		wp.addPage(wp1);  
		initWP0();
	}//end constructor
	
	/**
	 * The first page is started up by this controller
	 */
	public void initWP0() {
		//publish Problem subtype selectbox
		String[] types = ProblemFactory.advertiseSubtypes();
		problemTypeChooser = new TypeChooserPane(wp1, types);
		wp1.initPhase1(problemTypeChooser);
		wp.showPage(0);
	}//end initWP0
	
	/**
	 * Phase 2 of page 1 is initialised as soon as the page says it is
	 * ready for it. The page sends us the index of the chosen problem subtype
	 * @param typeindex
	 */
	public void phase2WP0(int typeindex) {
		//create problemconnector for chosen problem type
		problemconnector = ProblemFactory.createProblemConnector(typeindex);
		//present it to user to e.g. load a file
		problemReader = new ProblemConnectorPane(
				wp1, problemconnector, Modes.PROBLEMGETTER);
		//Problem parameters window
		Parameter[] p = ProblemFactory.advertiseParameters(typeindex);
		problemParams = new ParameterFillinPane(wp1, "", "", p);
		wp1.initPhase2(problemReader, problemParams);
	}//end phase2WP0
	
	/**
	 * Post-processing of the results of page 0, happens after pressing Next.
	 * The aim of page 0 is to formulate the Problem.
	 */
	public void afterWP0() {
		this.problem = ProblemFactory.createProblem(
			problemTypeChooser.getSelectedIndex(), 
			problemconnector, 
			problemParams.getParameterValues()
		);
	}//afterWP0
	
	public void initWP1() {
	}//end initWP1
	
	public void phase2WP1() {
	}//end phase2WP1
	
	public void afterWP1() {
	}//afterWP1
	
	public void initWP2() {
	}//end initWP2
	
	public void afterWP2() {
	}//afterWP2
	
	public void initWP3() {
	}//end initWP3
	
	public void restart() {
	}//end restart
	
	public void quit() {
	}//end quit
	
	/**
	 * The currently shown page says it's become valid / invalid.
	 * We react by enabling / disabling the Next button
	 */
	public void pageIsValid(boolean valid) {
		wp.setButtonEnabled(WizardPane.NEXT, valid);
	}//pageIsValid
	
	/**
	 * Process buttonpush on the WizardPane: Previous, Next, Quit
	 * We must initiate page-specific processing for Next:
	 * - Post-process the finished page
	 * - Initialize the new page based on results of earlier pages
	 * For Previous we can show the page as it already exists (in filled state)
	 * The Quit button is handled independent of the page
	 */
	@Override
	public void buttonPushed(String actioncommand, int currentpage) {
		int goalpage = currentpage;
		if (actioncommand.equals(WizardPane.NEXT)) goalpage++;
		if (actioncommand.equals(WizardPane.PREVIOUS)) goalpage--;
		if (actioncommand.equals(WizardPane.NEXT)) {
			//post-processing of the finished page!
			if (currentpage==0) this.afterWP0();
			if (currentpage==1) this.afterWP1();
			if (currentpage==2) this.afterWP2();
			if (currentpage==3) this.quit();
			//initialising the new page based on results of earlier pages
			if (goalpage==1) this.initWP1(); //init includes showing
			if (goalpage==2) this.initWP2();
			if (goalpage==3) this.initWP3();
		}//end if actioncommand==NEXT
		if (actioncommand.equals(WizardPane.PREVIOUS)) {
			wp.showPage(goalpage); //simply show the already filled page
		}//end if
		if (actioncommand.equals(WizardPane.CANCEL)) {
			//ToDo: warn and quit
		}//end if
		if (actioncommand.equals(WizardPane.FINISH)) {
			//ToDo; warn, save and quit, or restart from zero 
		}//end if
	}//end buttonPushed

}//end class
