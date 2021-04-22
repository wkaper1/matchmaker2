package nl.uva.science.esc.search.techniques;

import java.lang.Math;

import nl.uva.science.esc.matchmaker.Controller;
import nl.uva.science.esc.matchmaker.Controller.InputType;
import nl.uva.science.esc.search.problems.State;
import nl.uva.science.esc.search.problems.StochasticOptimisationProblem;

import nl.uva.science.esc.search.views.Parameter;
import nl.uva.science.esc.search.views.PropertyAdvertiser;

/**
 * Well known technique. The minimum of a function is sought in a way that
 * is taken from physics: you can find the energy of a crystal by
 * simulating a slow cooling of a liquid until it solidifies.
 * 
 * For details see Russell and Norvig.
 * @author kaper
 */
public class SimulatedAnnealingTechnique implements Technique {
	
	private long energy; //The cost function is by tradition called energy
		//when using this technique. It is the function to minimize. 
		//This is the energy of the current state.
	private int iteration;     //counter of tries
	private long bestenergy;    //lowest energy seen in this run up to now
	private int bestiteration; //iteration in which best energy was seen
	private double temperature;   //current temperature
	private double initialtemperature; //starting value for temperature
	private int waittime;      //number of iterations before temperature change
	private float temperaturedrop; //new temperature as fraction of previous one
	private double closetozerotemp; //temperature where we expect the minimum
		//energy to be stably reached
	private int maxtriesinvain;//max of attempts after best iteration
	private StochasticOptimisationProblem p;   //the problem to solve !
	private boolean running;   //we can stop the process by setting this to no
	
	/**
	 * Constructor
	 * @param p, problem to solve
	 * @param initialtemperature
	 * @param waittime, between temperature drops
	 * @param temperaturedrop, size of a temperature drop as a fraction
	 * @param closetozerotemp, temperature at which to stop trying
	 * @param maxtriesinvain, stop after how many unsuccesful tries 
	 * @throws Exception
	 */
	public SimulatedAnnealingTechnique(
		StochasticOptimisationProblem p, double initialtemperature, int waittime, 
		float temperaturedrop, double closetozerotemp, int maxtriesinvain
	) throws Exception {
		this.p = p;
		this.p.initGoalState();
		iteration = 0;
		bestiteration = 0;
		energy = this.p.getCost();
		bestenergy = energy;
		this.initialtemperature = initialtemperature;
		this.temperature = initialtemperature;
		this.waittime = waittime;
		this.temperaturedrop = temperaturedrop;
		this.closetozerotemp = closetozerotemp;
		this.maxtriesinvain = maxtriesinvain;
		running=false;
	}//end SimulatedAnnealingTechnique
	
	/**
	 * Run the technique on the problem
	 * This could best be run in a separate thread, as it involves lengthy
	 * work.
	 */
	public void run() {
		running=true;
		while (
			running && 
			(temperature > closetozerotemp || iteration < bestiteration + maxtriesinvain)
		) {
			//get a proposal for a state change
			p.generateRandomMove();
			long energychange = p.getDeltaCostRandomMove();
			//decide whether it will happen
			double p1 = Math.exp(((double)-energychange) / temperature);
			double p2 = Math.random(); 
			if (p1 > p2) { //accept the change
				p.acceptMove();
				energy += energychange;
				if (energy < bestenergy) {
					bestenergy = energy;
					bestiteration = iteration;
				}//end if
			}//end if
			//for debugging, comment out in production
			if (iteration % 50000 ==0) {
				System.out.println("energy: "+energy);
				System.out.println("iteration: "+iteration);
				System.out.println("bestenergy: "+bestenergy);
				System.out.println("bestiteration: "+bestiteration);
				System.out.println("temperature: "+temperature);
				System.out.println();
			}//end if
			iteration++;
			if ((iteration % waittime)==0) {
				//it is time to change the temperature
				temperature = temperature * temperaturedrop;
			}//end if
		}//end while
	}//end run
	
	//Below are functions for interacting with the running process
	//They could be called by the user interface
	//There is no threadsafety, so just reading is the safest!
	
	public long getEnergy() {
		return energy;
	}//end getEnergy
	
	public int getIteration() {
		return iteration;
	}//end getIteration
	
	public long getBestenergy() {
		return bestenergy;
	}//end getBestenergy
	
	public int getBestiteration() {
		return bestiteration;
	}//end getBestiteration
	
	public double getTemperature() {
		return temperature;
	}//end getTemperature
	
	public State getCurrentState() {
		return p.getState();
	}//end getCurrentState
	
	/**
	 * In this Technique, the best state is not stored, but the current state
	 * should be a good approximation... so ask for that one instead!
	 */
	public State getBestState() {
		return null; 
	}//end getBestState
	
	/**
	 * This returns a reference to the live problem object
	 * Reading (calling getters) is okay but other actions may messup the work
	 * @return problem object
	 */
	public StochasticOptimisationProblem getProblem() {
		return this.p;
	}//end getProblem
	
	/**
	 * The only interference which is deemed safe
	 */
	public void stopRunning() {
		this.running = false;
	}//end setRunning
	
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
		return new String[] {
			"energy", "iteration", "bestenergy", "bestiteration", "temperature"
		};
	}//end advertiseSimpleProperties
	
	/**
	 * Values corresponding to the advertised simple properties
	 * converted to Strings for easy display
	 * @return values array
	 */
	public String[] simplePropertyValues() {
		return new String[] {
			String.valueOf(energy), 
			String.valueOf(iteration), 
			String.valueOf(bestenergy), 
			String.valueOf(bestiteration),
			String.valueOf(temperature)
		};
	}//end simplePropertyValues
	
	/**
	 * Advertise parameters that the GUI must get from the user before calling
	 * the constructor
	 * @return parametersnames array
	 */
	public static Parameter[] advertiseParameters() {
		return new Parameter[] {
				new Parameter("initialtemperature", true, InputType.POSITIVEINT), 
				new Parameter("waittime", true, InputType.POSITIVEINT), 
				new Parameter("temperaturedrop", true, InputType.FRACTION),
				new Parameter("closetozerotemp", true, InputType.FLOAT),
				new Parameter("maxtriesinvain", true, InputType.POSITIVEINT)
				};
	}//end advertiseParameters
	
}//end SimulatedAnnealingTechnique
