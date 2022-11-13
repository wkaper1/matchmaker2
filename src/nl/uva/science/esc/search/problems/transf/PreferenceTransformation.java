package nl.uva.science.esc.search.problems.transf;

/**
 * Transforms (regular) preference numbers, for the student-project matching problem, 
 * and provides values for the known high penalties
 * 
 * @author kaper
 */
public interface PreferenceTransformation {

	/**
	 * Transform a (regular) preference number
	 * @param preference, the preference to transform
	 * @return
	 */
	public int Transform(int preference);
	
	/**
	 * The penalty for placing a student on a project not mentioned as one of her preferences
	 * @return a constant
	 */
	public int NonChosenPlacePenalty();
	
	/**
	 * The penalty for not placing a student at all
	 * @return
	 */
	public int NonPlacementPenalty();
}
