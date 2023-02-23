package nl.uva.science.esc.math;

/**
 * Object represents a Pochhammer series, it consequtively returns each term and caches them.
 * Pochhammer series (u)_n = 1, u, u*(u+1), u*(u+1)(u+2), ...
 * where (in our application) u is a rational number, e.g. 1/2
 * 
 * The main use of the caching is being able to restart the series somewhere "in the middle"
 * @author wkaper1
 */
public class PochhammerSeries3 {
	private RationalNumber[] growingFactor; //the seed number u, with n times 1 added to it
	private RationalNumber[] term;          //a term in the series
	private int n;                          //the current term's number, starting at 0
	private int nmax;                       //cache currently filled up to...
	
	
	public PochhammerSeries3(RationalNumber seed, int cacheSize) {
		growingFactor = new RationalNumber[cacheSize];
		term = new RationalNumber[cacheSize];
		growingFactor[0] = seed.clone();   //don't modify the seed that we got from outside
		growingFactor[0].ToThisAddInt(-1);
		term[0] = null;                    //we didn't start yet
		n = -1;                            //we didn't start yet
		nmax = -1;                         //not relevant on first run
	}

	/**
	 * Returns the next term from the series, from cache if possible, 
	 * otherwise calculate it from the previous term and growingFactor
	 */
	public RationalNumber NextTerm() {
		if (n == -1) {
			n = 0;
			return term[0] = new RationalNumber(1, 1); //zero'th term, n starts at 0
		}
		else {
			n++;
			if (term[n] == null) {    //if the cache is not filled yet from a previous run
				growingFactor[n] = growingFactor[n-1].AddInt(1);    //'first' term will be the seed
				term[n] = term[n-1].MultiplyBy(growingFactor[n-1]);				
			}
			return term[n];
		}
	}

	/**
	 * Restart the series at a given index position.
	 * The maximum possible position for restart is the one after the highest numbered cached term.
	 * @param n, the index position at which to restart
	 * @throws Exception
	 */
	public void restartAt(int n) throws Exception {
		if (term[n-1] != null) {
			if (this.n > nmax) {
				nmax = this.n;
			}
			this.n = n;
		}
		else {
			throw new Exception("Cannot restart at n=" + n);
		}
	}

	/**
	 * To what index position (term number) is the cache filled?
	 * This determines restarting possibilities.
	 */
	public int filledUpTo() {
		return Math.max(nmax, n);
	}
}
