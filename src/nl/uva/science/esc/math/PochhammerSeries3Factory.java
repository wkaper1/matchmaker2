package nl.uva.science.esc.math;

import java.math.BigInteger;
import java.util.*;

/**
 * Hands out PochhammerSeries instances.
 * Takes care that each one is cached and reused when the seed value fits.
 * 
 * The cache has three dimensions: 
 * - the first is the term number n, this part of the cache exists inside of the PochhammerSeries objects.
 *   It is implemented with an array that needs to be sized beforehand.
 * - the second is the seed numerator, the seed is a RationalNumber
 * - the third is the seed denominator.
 *   Second and third dimensions are implemented here using two nested HashMaps. Size is flexible. 
 * @author wkaper1
 */
public class PochhammerSeries3Factory {
	private int cacheByNSize;
	private Map<BigInteger, Map<BigInteger, PochhammerSeries3>> cacheBySeed = new HashMap<BigInteger, Map<BigInteger, PochhammerSeries3>>();
	
	public PochhammerSeries3Factory(int cacheByNSize) {
		this.cacheByNSize = cacheByNSize;
	}
	
	public PochhammerSeries3 createPochhammerSeries(RationalNumber seed) {
		Map<BigInteger, PochhammerSeries3> byEnumerator = cacheBySeed.get(seed.denominator());
		if (byEnumerator == null) {
			byEnumerator = new HashMap<BigInteger, PochhammerSeries3>();
			cacheBySeed.put(seed.denominator(), byEnumerator);
		}
		PochhammerSeries3 phs = byEnumerator.get(seed.numerator());
		if (phs == null) {
			phs = new PochhammerSeries3(seed, cacheByNSize);
			byEnumerator.put(seed.numerator(), phs);
		}
		return phs;
	}
	
}
