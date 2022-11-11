package nl.uva.science.esc.search.problems.transf;

/**
 * Linear transformation y = a.x + b
 * @author kaper
 */
public class LinearTransformation implements PreferenceTransformation {
	int steepness;
	int intercept;
	int nonChosenPlacePenalty;
	int nonPlacementPenalty;

	/**
	 * Constructor of a linear transformation PreferenceTransformation
	 * @param steepness, multiplier for the variable
	 * @param intercept, translation added to the multiplied variable
	 * @param nonChosenPlacePenalty, the penalty for placing a student on a project not mentioned as one of her preferences
	 * @param nonPlacementPenalty, the penalty for not placing a student at all
	 */
	public LinearTransformation(int steepness, int intercept, int nonChosenPlacePenalty, int nonPlacementPenalty) {
		this.steepness = steepness;
		this.intercept = intercept;
		this.nonChosenPlacePenalty = nonChosenPlacePenalty;
		this.nonPlacementPenalty = nonPlacementPenalty;
	}

	@Override
	public int Transform(int preference) {
		return preference * steepness + intercept;
	}

	@Override
	public int NonChosenPlacePenalty() {
		return nonChosenPlacePenalty;
	}

	@Override
	public int NonPlacementPenalty() {
		return nonPlacementPenalty;
	}

}
