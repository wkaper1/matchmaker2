package nl.uva.science.esc.search.views;

/**
 * A RadioPaneListener is usually a JPanel that contains a RadioPane
 * and needs to listen for changes 
 * @author kaper
 *
 */
public interface RadioPaneListener {

	/**
	 * The changed RadioPane will send a reference to itself for identification
	 * @param rp
	 * @throws Exception 
	 */
	public void onradiochange(RadioPane rp);

}//end interface
