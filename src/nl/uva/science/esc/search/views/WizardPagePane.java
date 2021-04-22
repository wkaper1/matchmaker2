package nl.uva.science.esc.search.views;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JPanel;

import nl.uva.science.esc.matchmaker.Controller;

/**
 * There will be four pages in the wizard and this will be their superclass.
 * The superclass registers components in an agnostic way in addition to
 * the more specific registering that the subclass does.
 * The agnostic registering is used for validating the components contents and'
 * for receiving their "I am valid" push notifications.
 * 
 * It keeps a count of invalid components. It assumes each new component
 * to be invalid until it is notified otherwise. The numinvalid count is the
 * only measure.
 * 
 * As soon as this count reaches zero (again) (while the number of components
 * is not zero) the page as a whole is reported valid. This is used e.g. 
 * to enable the NEXT button.
 * @author kaper
 *
 */
public abstract class WizardPagePane extends JPanel {
	//all pages must be able to talk to the controller
	protected Controller controller;
	//uniform layout for the wizard pages
	private GridBagLayout pagelayout;   //2 columns: label + component
	private GridBagConstraints constr;  //defaults: copy and change them
	private static final int labelwidth = 100; //intended width of label column 
	//fields used for validation
	private List<WizardComponent> components; //the set of components on the page
	private int numinvalid; //number of invalid components
	
	/**
	 * Constructor
	 */
	public WizardPagePane(Controller controller) {
		this.controller = controller;
		//layout
		this.pagelayout = new GridBagLayout();
		this.setLayout(pagelayout);
		this.constr = new GridBagConstraints();
			//build the default here, then copy/clone and change it in addComp
		//validation
		this.components = new LinkedList<WizardComponent>();
		this.numinvalid = 0;
	}//end WizardPagePane
	
	//the next few methods are used by the page subclasses to arrange their
	//components into a standard layout
	
	/**
	 * Low-level method to place one component into the grid
	 * The next two methods are more suitable (they in turn use this one) 
	 */
	private void addComponent(Component c, int col, int row, int colspan) {
		GridBagConstraints gc = (GridBagConstraints) constr.clone();
		gc.gridx = col;
		gc.gridy = row;
		gc.gridwidth = colspan;
		pagelayout.setConstraints(c, gc);
		this.add(c);
	}//end addComponent
	
	/**
	 * Fill a row with a label plus a component 
	 * @param txt, text for the label
	 * @param c, component
	 * @param row, layout row number to fill
	 */
	protected void addLabelContentPair(String txt, WizardComponent wc, int row) {
		txt = "<html><div style='width:" + labelwidth + "px;'>" + txt + 
		"</div></html>" ;
		JLabel lbl = new JLabel(txt);
		Component c = (Component) wc;
		lbl.setLabelFor(c);
		//add them to the JPanel GridBagLayout
		addComponent(lbl, 0, row, 1);
		addComponent(c, 1, row, 1);
		//add the wc to this class for validating!
		numinvalid++;      //assume it's invalid
		components.add(wc); //the component might immediately notify a change to valid
	}//end addLabelContentPair
	
	/**
	 * Fill a row with a label that spans both columns
	 * @param txt, text for the label
	 * @param row, layout row number to fill
	 */
	protected void add2ColLabel(String txt, int row) {
		txt = "<html>" + txt + "</html>";
		JLabel lbl = new JLabel(txt);
		lbl.setLabelFor(null);  //it's not a label for something
		addComponent(lbl, 0, row, 2);
	}//end add2ColLabel
	
	
	//the next methods realize validation of the page
	
	/**
	 * Validate all of the components from scratch, assuming them not valid
	 * I don't know if we need  this one, but it seems safe to have it!
	 * 
	 * @param msg, do we want to show messages?
	 * @return true, only if all components are valid
	 */
	public boolean validateAll(boolean msg) {
		numinvalid = components.size();
		Iterator<WizardComponent> i = components.iterator();
		while (i.hasNext()) {
			i.next().validateAll(msg);  //it generates pushes to lower numinvalid!
		}//end while
		return (numinvalid==0);
	}//end validateAll
	
	/**
	 * Components will push their status changes here
	 * @param valid, true means: change from invalid to valid, false means the reverse
	 */
	public void notifyValid(boolean valid) {
		int oldnuminvalid = numinvalid;
		if (valid) numinvalid--;
		else numinvalid++;
		//Did the page as a whole become valid / invalid? Notify WizardController
		if (numinvalid==0 && oldnuminvalid!=0) {
			((WizardController)controller).pageIsValid(true);			
		}
		else if (numinvalid!=0 && oldnuminvalid==0) {
			((WizardController)controller).pageIsValid(false);			
		}//end if
	}//end notifyValid

}//end class
