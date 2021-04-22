package nl.uva.science.esc.search.techniques;

import nl.uva.science.esc.search.problems.State;
import nl.uva.science.esc.search.views.PropertyAdvertiser;

/**
 * A Technique object manipulates a Problem, asking it to move from State
 * to State. Among the states, some are goal-states.
 * Task is to find the goal-state that has the least cost.
 * 
 * This interface defines the minimum methods that a Technique object needs
 * to communicate with the central controller and with the views layer.
 * 
 * A live Technique object is supposed to be involved in two threads:
 * (1) the solution thread which is working on the problem
 * (2) the GUI thread which intermittently asks for a peek into the kitchen
 * @author kaper
 *
 */
public interface Technique extends PropertyAdvertiser {
	
	/**
	 * Start the solution process, which is thread 1
	 */
	public void run();
	
	/**
	 * Stop prematurely, e.g. when the user gets restless
	 */
	public void stopRunning();
	
	/**
	 * @return Is it (still) running?
	 */
	public boolean isRunning();
	
	/**
	 * Return the State that we are currently inspecting, used for monitoring
	 * this typically runs in thread 2
	 * @return
	 */
	public State getCurrentState();
	
	/**
	 * Return the best State found up to now, for monitoring, so: thread 2
	 * @return
	 */
	public State getBestState();
	
}//end interface
