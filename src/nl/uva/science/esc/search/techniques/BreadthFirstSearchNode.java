package nl.uva.science.esc.search.techniques;

import nl.uva.science.esc.search.problems.State;

/**
 * A node in a breadth-first search tree
 * Note: The full tree is not saved, we don't keep ancestor nodes.
 * If you expand a Node of level N, the resulting nodes will be
 * of level N+1.
 * 
 * The object is immutable
 * @author kaper
 *
 */
public class BreadthFirstSearchNode {
	private State s;   //a saved (cloned) state of the Problem
	private long cost;  //cost of the above state
	private int level; //level at which this node was generated in the tree. 
	
	public BreadthFirstSearchNode(State s, long cost, int level) {
		this.s = s;
		this.cost = cost;
		this.level = level;
	}//end constructor
	
	public State getState() {
		return s;
	}//end getState
	
	public long getCost() {
		return cost;
	}//end getCost
	
	public int getLevel() {
		return level;
	}//end getLevel
	
}//end class
