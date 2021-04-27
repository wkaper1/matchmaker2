package nl.uva.science.esc.search.techniques;

import java.util.ArrayDeque;
import java.util.Deque;

import nl.uva.science.esc.search.problems.*;
import nl.uva.science.esc.search.views.PropertyAdvertiser;

/**
 * The simple depth-first exhaustive search technique.
 * See Russell and Norvig for more.
 * @author kaper
 */
public class DepthFirstSearchTechnique implements Technique {
	
	private Deque<Move> movestack;  //stack of moves that make up a solution
	private long cost;         //cost of the current state
	private int leafcount;    //how many goal states were investigated
	//private Move[] bestmoves; //solution with lowest cost up to now
	private State beststate;  //solution with lowest cost up to now
	private long bestcost;     //lowest cost found up to now
	private int bestleaf;     //leaf number where best cost was found
	   //compare it to leafcount to see how long ago progress was made
	private static final long UNKNOWNCOST = 99999;
	private DeterministicSearchProblem p;   //the problem to solve !
	private boolean running;   //we can stop the process by setting this to no

	/**
	 * Constructor of the thechnique
	 * @param p, the problem to solve
	 * @param levels, number of levels to develop in the search tree
	 */
	public DepthFirstSearchTechnique(DeterministicSearchProblem p, int levels) {
		this.p = p;
		movestack = new ArrayDeque<Move>(levels);
		cost = 0;  //the cost of an empty solution branch is zero
		leafcount = 0;
		beststate = null;  //initially there is no solution known
		//bestmoves = null;
		bestcost = UNKNOWNCOST;
		bestleaf = 0;
		running = false;
	}//end constructor
	
	public void run() {
		running = true;
		p.initState(); //different from stochastic which needs a GOAL state for starters!
		boolean ok = p.generateDeterministicMove(movestack.size());
		assert ok==true; //first move should succeed
		do { 
			//start new goal seek
			while (!p.goalTest(movestack.size()) && ok ) {
				//use generated move 
				cost += p.getDeltaCostDeterministicMove();
				Move m = p.doForwardMove();
				movestack.push(m);
				ok = p.generateDeterministicMove(movestack.size()); //may return false, but...
			}//end while
			leafcount++;
			//we have a leaf, but is it a valid leaf, is it a goalstate?
			if (p.goalTest(movestack.size()) && (cost<bestcost || beststate==null)) {
				//bestmoves = movestack.toArray(new Move[0]);
				beststate = p.getState().stateClone();
				bestcost = cost;
				bestleaf = leafcount;
			}//end if
			//for debugging, comment out in production
			if (leafcount % 1000 ==0) {
				System.out.println("cost: "+cost);
				System.out.println("leafcount: "+leafcount);
				System.out.println("bestcost: "+bestcost);
				System.out.println("bestleaf: "+bestleaf);
				System.out.println();
			}//end if
			//after getting to a leaf we need to retreat
			//at least once and maybe more, to get rid of nodes which
			//are out of moves (all have been tried)
			do { //at least once
				Move m = movestack.pop();
				p.retreatMove(m);
				cost -= p.getDeltaCostDeterministicMove();
				ok = p.generateDeterministicMove(movestack.size());
			} while (!ok && movestack.size()>0);
		} while (running && movestack.size()>0 || (movestack.size()==0 && ok) );
	}//end run
	
	//getters and setters for interaction with the UI
	//take care, no threadsafety! 
	//The run method is running in a different thread than the UI
	
	public Move[] getMoves() {
		return movestack.toArray(new Move[0]);
	}//end getMoves
	
	public long getCost() {
		return cost;
	}//end getCost
	
	public int getLeafCount() {
		return leafcount;
	}//end getLeafCount
	
	public long getBestCost() {
		return bestcost;
	}//end getBestCost
	
	public int getBestLeaf() {
		return bestleaf;
	}//end getBestLeaf
	
	public State getCurrentState() {
		return p.getState(); //careful, reference to live object
	}//end getCurrentState
	
	public State getBestState() {
		return beststate;    //it's a clone and has only one goal: being read
	}//end getBestState
	
	/**
	 * Take care, reference to live object returned! You can look but not touch
	 * @return the problem object
	 */
	public DeterministicSearchProblem getProblem() {
		return p;
	}//end getProblem
	
	/**
	 * Return a readable description of the best goal state found
	 * @return the text
	 */
	public String showBestState() {
		String txt = "";
		txt += "bestcost: "+bestcost+"\n";
		p.setState(beststate);
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
		return new String[] {"cost", "leafcount", "bestcost", "bestleaf"};
	}//end advertiseSimpleProperties
	
	/**
	 * Values corresponding to the advertised simple properties
	 * converted to Strings for easy display
	 * @return values array
	 */
	public String[] simplePropertyValues() {
		return new String[] {
			String.valueOf(cost), 
			String.valueOf(leafcount), 
			String.valueOf(bestcost), 
			String.valueOf(bestleaf)
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
