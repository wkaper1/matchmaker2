package nl.uva.science.esc.search.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import nl.uva.science.esc.search.problems.State;
import nl.uva.science.esc.search.techniques.Technique;

/**
 * The SolutionMonitorPane gives a peek into the developing solution.
 * 
 * It is a view that has a Technique object as its model-object (data-supplier)
 * This view can switch between:
 * - Current solution, the solution currently being inspected by the Technique
 * - Best solution, the solution with lowest cost found up to now
 *   it depends on the Technique whether this second option is available
 * This view can also switch between:
 * - Detailed view of the solution (showing it all)
 * - Summary view, showing properties of the solution that enable one to 
 *   estimate how good the solution is
 *   
 * There is a refresh button, there are 2 x 2 radio buttons for the 2 binary
 * choices, and there is a big TablePane to show the results.
 * 
 * The Technique object, when asked for the current or best solution responds
 * with a solution State. This State is then enquired for a detailed or a
 * summary description, which is fed into the TablePane.
 * @author kaper
 *
 */
public class SolutionMonitorPane extends JPanel implements RadioPaneListener {
	private Technique t; //the live Technique object that we should watch
	private State s; //the latest State (current or best) that we asked for
	private RadioPane curbest; //choice between current and best solution
	private RadioPane sumdet;  //choice between summary and details
	private TablePane sum;  //TablePane to show summaries in
	private TablePane det;  //TablePane to show details in
	private TablePane active; //reference to the active one (sum or det)
	private static final String TITLE = "Solution Monitor";
	private BoxLayout layout;
	
	/**
	 * Constructor
	 * @param t A Technique object to watch, it should be in a running state
	 *    so this means that another thread than ours is letting stuff happen
	 * @throws Exception 
	 */
	public SolutionMonitorPane(Technique t) throws Exception {
		this.t = t;
		this.s = t.getCurrentState();
		//Initialise the tables with the right headings
		//Headings as well as number of columns will be Problem-specific (but we don't care here!)
		this.sum = new TablePane(s.showSummaryHeadings());
		this.det = new TablePane(s.showDetailsHeadings());
		//Build the GUI
		this.layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(layout);
		//refresh button
		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new RefreshHandler());
		this.add(refresh);
		//radiobuttons current / best, and summary / details
		curbest = new RadioPane(this, 
				new String[] {"current", "best"}, "current");
		this.add(curbest);
		sumdet = new RadioPane(this,
				new String[] {"summary", "details"}, "summary");
		this.add(sumdet);
		//show one of the two TablePanes
		refresh();
	}//end SolutionMonitorPane
	
	//event handlers
	
	/**
	 * Refresh button handler
	 */
	private class RefreshHandler implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			try {
				refresh();
			} catch (Exception e1) {
				e1.printStackTrace();
			}//end try
		}//end actionPerformed
	}//end private class RefreshHandler
	
	/**
	 * Collective handler for all the RadioPanes 
	 */
	public void onradiochange(RadioPane rp) {
		try {
			if (rp==curbest) refresh();
			if (rp==sumdet) refreshview();			
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}//end try
	}//end onradiochange
	
	/**
	 * Ask the Technique object for a new State and show the result in a table
	 * @throws Exception if table contents do not fit table headings
	 */
	public void refresh() throws Exception {
		//Ask for new State; Let the curbest RadioPane decide abouut which one
		this.s = (curbest.getCurrentState()=="best") ? 
				(t.getBestState()) : (t.getCurrentState());
		//Now we decide on the view and make the table
		refreshview();
	}//end refresh
	
	/**
	 * Ask the State s for a description, and show the result in a table
	 * @throws Exception 
	 */
	private void refreshview() throws Exception {
		//First remove the currently active TablePane from the view
		this.remove(active);
		//Get a new description of the State, let the sumdet RadioPane decide 
		//Then feed it to the right TablePane
		if (sumdet.getCurrentState()=="summary") {
			this.sum.setBody(s.showSummary());
			this.active = this.sum;
		}
		else {
			this.det.setBody(s.showDetails());
			this.active = this.det;
		}//end if
		//Now show the new active TablePane 
		this.add(active);
		//ToDo: Repaint... or something like it
	}//end refreshview
	
}//end class
