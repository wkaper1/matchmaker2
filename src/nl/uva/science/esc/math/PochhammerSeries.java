package nl.uva.science.esc.math;

/**
 * Object represents a Pochhammer series, it consequtively returns each element.
 * Pochhammer series (u)_n = 1, u, u*(u+1), u*(u+1)(u+2), ...
 * where (in our application) u is a rational number, e.g. 1/2
 * @author wkaper1
 */
public class PochhammerSeries {
	private RationalNumber growingFactor; //the seed number u, with n times 1 added to it
	private RationalNumber term;          //a term in the series
	
	public PochhammerSeries(RationalNumber seed) {
		growingFactor = seed.clone();   //don't modify the seed that we got from outside
		growingFactor.ToThisAddInt(-1);
		term = null;                    //we didn't start yet
	}
	
	public RationalNumber NextTerm() {
		if (term == null) {
			return term = new RationalNumber(1, 1); //zero'th term, n starts at 0
		}
		else {
			growingFactor.ToThisAddInt(1);          //'first' term will be the seed
			term.MultiplyThisBy(growingFactor);
			return term;
		}
	}
}
