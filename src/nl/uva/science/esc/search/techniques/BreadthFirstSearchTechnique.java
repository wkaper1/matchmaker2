package nl.uva.science.esc.search.techniques;

import java.util.LinkedList;
import java.util.Queue;

import nl.uva.science.esc.search.problems.*;
import nl.uva.science.esc.search.views.PropertyAdvertiser;

/**
 * The simple breadth-first exhaustive search technique.
 * We are not looking for the first goal state but for the goal state that has 
 * the absolute least cost, within a certain maximum depth of the searchtree.
 * See Russell and Norvig for more.
 * @author kaper
 *
 */
public class BreadthFirstSearchTechnique implements Technique {
	
	private Queue<BreadthFirstSearchNode> fringe; //fringe of unexpanded Nodes 
		//kept in a FIFO queue
	private int levels;        //number of levels to develop in the search tree
	private int nodecount;     //how many Nodes were generated
	private int goalcount;     //hoe many goal States were examined
	private long bestcost;      //best cost found up to now
	private static final long UNKNOWNCOST = 99999;
	private BreadthFirstSearchNode bestNode; //remember the best solution
	private int bestnodenumber; //compare it to nodecount to guess how old it is
	private DeterministicSearchProblem p;   //the problem to solve !
	private boolean running;   //we can stop the process by setting this to no
	
	public BreadthFirstSearchTechnique(DeterministicSearchProblem p, int levels) {
		this.fringe = new LinkedList<BreadthFirstSearchNode>(); 
		this.levels = levels;
		this.nodecount = 0;
		this.goalcount = 0;
		this.bestcost = UNKNOWNCOST;
		this.bestNode = null;
		this.bestnodenumber = 0;
		this.p = p;
		this.running = false;
	}//end constructor
	
	public void run() {
		running = true;
		p.initState(); //different from stochastic which needs a GOAL state for starters!
		State s = p.getState().stateClone();
		fringe.add(new BreadthFirstSearchNode(s, 0, 0));
		while(running && fringe.size()>0 && fringe.peek().getLevel()<=levels-1) {
			//Pop one Node from the beginning of the fringe-queue.
			//Expand it, and put the resulting Nodes at the end of that queue
			BreadthFirstSearchNode n = fringe.poll(); //Node to expand
			int level = n.getLevel();
			long cost = n.getCost();
			p.setState(n.getState());
			boolean ok = p.generateDeterministicMove(level);
			while (ok) {   //while the Node is not out of Moves
				long newcost = cost + p.getDeltaCostDeterministicMove();
				Move m = p.doForwardMove();
				BreadthFirstSearchNode newnode = new BreadthFirstSearchNode(
						p.getState().stateClone(), newcost, level + 1
				);
				fringe.add(newnode); //save result at the end of the queue
				nodecount++;
				if (p.goalTest(level + 1)) {
					goalcount++;
					if (newcost < bestcost || bestcost==UNKNOWNCOST) {
						bestcost = newcost;
						bestNode = newnode;
						bestnodenumber = nodecount;
					}//end if
				}//end if
				//for debugging, comment out in production
				if (nodecount % 1000 ==0) {
					System.out.println("nodes generated: "+nodecount);
					System.out.println("goals states: "+goalcount);
					System.out.println("best cost: "+bestcost);
					System.out.println();
				}//end if
				p.retreatMove(m);  //backtrack to try next move
				ok = p.generateDeterministicMove(level);			
			}//end while			
		}//end while
		//In the fringe we now have only nodes of level N
		//If we were sure the solution is in level N we could postpone looking
		//for goal states till we are here. (But that is less than general)
	}//end run
	
	public int getNodeCount() {
		return nodecount;
	}//end getNodeCount
	
	public int getGoalCount() {
		return goalcount;
	}//end getGoalCount
	
	public State getCurrentState() {
		return p.getState(); //take care to only read it
	}//end getCurrentState
	
	public long getBestCost() {
		return bestcost;
	}//end getBestCost
	
	public State getBestState() {
		return bestNode.getState();
	}//end getBestState
	
	public int getBestNodeNumber() {
		return bestnodenumber;
	}//end getBestNodeNumber
	
	/**
	 * Return a readable description of the best goal state found
	 * @return the text
	 */
	public String showBestState() {
		String txt = "";
		txt += "bestcost: "+bestcost+"\n";
		p.setState(bestNode.getState()); //the problem box will show it
		txt += p.showState();
		return txt;
	}//end showBestMoves
	
	/**
	 * Send the run method a stop signal
	 */
	public void stopRunning() {
		running = false;
	}//end stopRunning
	
	/**
	 * Is it running?
	 */
	public boolean isRunning() {
		return running;
	}//end isRunning
	
	/**
	 * Advertise simple properties meant for display in the UI
	 * @return array of names of properties
	 */
	public String[] advertiseSimpleProperties() {
		return new String[] {"nodecount", "goalcount", "bestcost", "bestnodenumber"};
	}//end advertiseSimpleProperties
	
	/**
	 * Values corresponding to the advertised simple properties
	 * converted to Strings for easy display
	 * @return values array
	 */
	public String[] simplePropertyValues() {
		return new String[] {
			String.valueOf(nodecount), 
			String.valueOf(goalcount), 
			String.valueOf(bestcost), 
			String.valueOf(bestnodenumber)
		};
	}//end simplePropertyValues
	
	/**
	 * Advertise parameters that the GUI must get from the user before calling
	 * the constructor
	 * @return parametersnames array
	 */
	public static String[] advertiseParameters() {
		return new String[] {"level"};
	}//end advertiseParameters
	
}//end class
