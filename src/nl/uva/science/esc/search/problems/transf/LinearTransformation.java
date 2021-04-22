package nl.uva.science.esc.search.problems.transf;

/**
 * Linear transformation y = a.x + b
 * @author kaper
 */
public class LinearTransformation implements PreferenceTransformation {
	int steepness;
	int intercept;

	/**
	 * Constructor of a linear transformation
	 * @param steepness, multiplier for the variable
	 * @param intercept, translation added to the multiplied variable
	 */
	public LinearTransformation(int steepness, int intercept) {
		this.steepness = steepness;
		this.intercept = intercept;
	}

	@Override
	public int Transform(int preference) {
		return preference * steepness + intercept;
	}

}
