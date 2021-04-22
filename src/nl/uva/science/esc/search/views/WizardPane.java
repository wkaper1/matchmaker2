package nl.uva.science.esc.search.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This creates a window that will contain a wizard.
 * 
 * A sequence of WizardPages can be shown in this Window. Below the large area
 * where a page is shown is a horizontal row of buttons: Previous, Next, and
 * Finish. Button-clicks are sent to a WizardController (see: interface
 * WizardController).
 * This controller, as one of its decisions can tell this class to move to
 * a given page. Also it can decide to enable /disable some of the 3 buttons.
 *  
 * @author kaper
 *
 */
public class WizardPane extends JFrame {
	//Names for the three buttons
	public static final String PREVIOUS = "Previous";
	public static final String NEXT = "Next";
	public static final String CANCEL = "Cancel";
	public static final String FINISH = "Finish"; //alternative name for cancel
	//private
	private WizardController c; //the controller of this view
	private JPanel[] pages;  //the pages between which we can jump
	private int currentpage; //index of current page
	private int length;      //current number of pages in the wizard
	private JButton prev; //the three Wizard buttons at the foot of the window 
	private JButton next;
	private JButton quit; //called "Cancel" in earlier screens and "Finish" finally

	/**
	 * Constructor
	 * @param c, the controller that we need to notify of button-clicks
	 * @param dim, how many pages can be expected as a maximum
	 */
	public WizardPane(WizardController c, int dim, String screentitle) {
		super(screentitle);
		this.c = c;
		this.pages = new JPanel[dim];
		this.currentpage = 0;
		this.length = 0; //there are currently no pages
		BorderLayout bdl = new BorderLayout(); 
		this.setLayout(bdl);
		//build the button row in the South region
		JPanel buttonpane = new JPanel();
		buttonpane.setLayout(new FlowLayout());
		this.prev = new JButton(PREVIOUS);
		this.next = new JButton(NEXT);
		this.quit = new JButton(CANCEL);
		prev.addActionListener(new WizardHandler());
		next.addActionListener(new WizardHandler());
		quit.addActionListener(new WizardHandler());
		buttonpane.add(prev);
		buttonpane.add(next);
		buttonpane.add(quit);
		this.add(buttonpane, BorderLayout.PAGE_END);
	}//end constructor
	
	/**
	 * Add a page to the Wizard. This is really the initializing phase.
	 * @param page
	 */
	public void addPage(JPanel page) {
		this.pages[this.length] = page;
		this.length++;
	}//end add
	
	/**
	 * Handler for the three Wizard buttons. Delegate action to the controller
	 * @author kaper
	 *
	 */
	private class WizardHandler implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			c.buttonPushed(e.getActionCommand(), currentpage);
		}//end actionPerformed
	}//end private class RefreshHandler

	/**
	 * Get the index of the currently shown page
	 * @return
	 */
	public int getCurrentPagenumber() {
		return currentpage;
	}//end getCurrentPagenumber
	
	/**
	 * Put the current page in the center of the BorderLayout,
	 * deleting anything that might be there
	 * @param i, new current page index
	 * @return i, the page we ended up on
	 */
	public int showPage(int i) {
		if (i>=0 && i<=length-1) {
			currentpage = i;
			this.add(pages[i], BorderLayout.CENTER);
			initButtons();
			return i;			
		}
		else return currentpage;
	}//end showPage
	
	public int showNext() {
		return showPage(currentpage++);
	}//end showNext
	
	public int showPrevious() {
		return showPage(currentpage--);
	}//end showPrevious
	
	/**
	 * You can configure the initial state of the buttons for each
	 * page here...!
	 */
	private void initButtons() {
		//These settings are so general that you won't change them
		if (currentpage==0) prev.setEnabled(false);
		if (currentpage==length-1) next.setEnabled(false);
		if (currentpage==length-1) quit.setText(FINISH); //on the last page, third button is traditionally renamed

		//Application specific settings:
		//Next will be always initially disabled, until controller changes it
		next.setEnabled(false);
		//For our third page we want all three buttons initially disabled
		if (currentpage==2) {
			prev.setEnabled(false);
			quit.setEnabled(false);
		}//end if
		//enabled is the default, so cases not mentioned here are enabled
	}//end initButtons

	/**
	 * Controller can change enabled statusses of each of the three buttons
	 * @param name, button name, equal to one of the three class constants
	 * @param enabled
	 */
	public void setButtonEnabled(String name, boolean enabled) {
		if (name.equals(PREVIOUS)) prev.setEnabled(enabled);
		if (name.equals(NEXT)) next.setEnabled(enabled);
		if (name.equals(CANCEL)) quit.setEnabled(enabled);
		//obviously bad commands are silently corrected...
		if (currentpage==0) prev.setEnabled(false);
		if (currentpage==length-1) next.setEnabled(false);
	}//end setButtonEnabled

}//end class
