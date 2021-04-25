package nl.uva.science.esc.matchmaker;

import nl.uva.science.esc.search.problems.ManyToOneMatchingProblem;
import nl.uva.science.esc.search.techniques.*;

/**
 * Tests for ManyToOneMatchingProblem
 * including three Techniques: DepthFirstSearch, Breadthfirstsearch and SimulatedAnnealing
 * @author kaper
 *
 */
public class TestManyToOneMatching {
	private static ManyToOneMatchingProblem[] p;

	/**
	 * Run the tests
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//a place to store some instances of the problem
		p = new ManyToOneMatchingProblem[10];
		
		//write the problems
		p[1] = new ManyToOneMatchingProblem(
				new int[] {0, 0, 0, 0, 0}, 
				new int[] {1, 1, 1, 1, 1}, 
				new int[][] {
						{5, 4, 3, 2, 1},
						{4, 3, 2, 1, 2},
						{3, 2, 1, 2, 3},
						{2, 1, 2, 3, 4},
						{1, 2, 3, 4, 5}
				},
				"identity",
				5
		);
		p[2] = new ManyToOneMatchingProblem(
				new int[] {0, 0, 0, 0}, 
				new int[] {1, 1, 1, 1},
				new int[][] {
						{4, 3, 2, 1},
						{3, 2, 1, 2},
						{2, 1, 2, 3},
						{1, 2, 3, 4}
				},
				"identity",
				4
		);
		p[3] = new ManyToOneMatchingProblem(
				new int[] {0, 0, 0, 0, 0, 0, 0}, 
				new int[] {1, 1, 1, 1, 1, 1, 1},
				new int[][] {
						{7, 6, 5, 4, 3, 2, 1},
						{6, 5, 4, 3, 2, 1, 2},
						{5, 4, 3, 2, 1, 2, 3},
						{4, 3, 2, 1, 2, 3, 4},
						{3, 2, 1, 2, 3, 4, 5},
						{2, 1, 2, 3, 4, 5, 6},
						{1, 2, 3, 4, 5, 6, 7},
				},
				"identity",
				7
		);
		p[4] = new ManyToOneMatchingProblem(
				new int[] {0, 0, 0, 0, 0, 0, 0, 0}, 
				new int[] {1, 1, 1, 1, 1, 1, 1, 1},
				new int[][] {
						{8, 7, 6, 5, 4, 3, 2, 1},
						{7, 6, 5, 4, 3, 2, 1, 2},
						{6, 5, 4, 3, 2, 1, 2, 3},
						{5, 4, 3, 2, 1, 2, 3, 4},
						{4, 3, 2, 1, 2, 3, 4, 5},
						{3, 2, 1, 2, 3, 4, 5, 6},
						{2, 1, 2, 3, 4, 5, 6, 7},
						{1, 2, 3, 4, 5, 6, 7, 8},
				},
				"identity",
				8
		);
		p[5] = new ManyToOneMatchingProblem(
				new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0}, 
				new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1},
				new int[][] {
						{9, 8, 7, 6, 5, 4, 3, 2, 1},
						{8, 7, 6, 5, 4, 3, 2, 1, 2},
						{7, 6, 5, 4, 3, 2, 1, 2, 3},
						{6, 5, 4, 3, 2, 1, 2, 3, 4},
						{5, 4, 3, 2, 1, 2, 3, 4, 5},
						{4, 3, 2, 1, 2, 3, 4, 5, 6},
						{3, 2, 1, 2, 3, 4, 5, 6, 7},
						{2, 1, 2, 3, 4, 5, 6, 7, 8},
						{1, 2, 3, 4, 5, 6, 7, 8, 9},
				},
				"identity",
				9
		);
		p[6] = new ManyToOneMatchingProblem(
				new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
				new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				new int[][] {
						{10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
						{9, 8, 7, 6, 5, 4, 3, 2, 1, 2},
						{8, 7, 6, 5, 4, 3, 2, 1, 2, 3},
						{7, 6, 5, 4, 3, 2, 1, 2, 3, 4},
						{6, 5, 4, 3, 2, 1, 2, 3, 4, 5},
						{5, 4, 3, 2, 1, 2, 3, 4, 5, 6},
						{4, 3, 2, 1, 2, 3, 4, 5, 6, 7},
						{3, 2, 1, 2, 3, 4, 5, 6, 7, 8},
						{2, 1, 2, 3, 4, 5, 6, 7, 8, 9},
						{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
				},
				"identity",
				10
		);
		p[9] = new ManyToOneMatchingProblem(
				new int[] {0, 0, 0, 0},
				new int[] {2, 2, 1, 1},
				new int[][] {
						{4, 3, 2, 1},
						{3, 2, 1, 2},
						{2, 1, 2, 3},
						{2, 1, 2, 3},
						{1, 2, 3, 4},
						{1, 2, 3, 4}
				},
				"identity",
				6
		);
		
		//choose a problem and a technique
		ManyToOneMatchingProblem px = p[6];
		String tx = "breadthfirstsearch";
		
		if (tx=="simulatedannealing") {
			//tweak parameters in the call below
			SimulatedAnnealingTechnique t = new SimulatedAnnealingTechnique(
					px, 1, 10, (float) 0.90, 1, 300
			);
			t.run();
			//ask for the solution
			System.out.println( px.showState() );			
		}//end if
		if (tx=="depthfirstsearch") {
			//there is not much to tweak in this one...
			DepthFirstSearchTechnique t = new DepthFirstSearchTechnique(
					px, px.getNumberOfAs()
			);
			t.run();
			//the best solution is not kept in the problem, now...
			System.out.println(""); //showbestmoves does no longer exist... it was only for testing...
		}//end if
		if (tx=="breadthfirstsearch") {
			//again a simple one without interesting parameters
			BreadthFirstSearchTechnique t = new BreadthFirstSearchTechnique(
					px, px.getNumberOfAs()
			);
			t.run();
			System.out.println( t.showBestState() ); //need to standardize this a bit more
		}//end if
	}//end main

}//end class
