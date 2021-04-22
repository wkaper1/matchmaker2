package nl.uva.science.esc.search.views;

/**
 * A PropertyAdvertiser advertises properties that can be shown in a
 * PropertyMonitorPane
 * PropertyAdvertisers are the model objects for which the PropertyMonitorPane
 * is a view. Properties are limited to simple primitive values. All values
 * are converted to String before handing them to the view.
 * The controller will establish a direct relation between model and view, so
 * the view can directly query the possibly changing model in its own pace.
 * (The model can't push any changes)
 * 
 * @author kaper
 *
 */
public interface PropertyAdvertiser {
	
	/**
	 * Advertise simple properties meant for display in the UI
	 * @return array of names of properties
	 */
	public String[] advertiseSimpleProperties();

	/**
	 * Values corresponding to the advertised simple properties
	 * converted to Strings for easy display
	 * @return values array
	 */
	public String[] simplePropertyValues();
	
}//end interface
