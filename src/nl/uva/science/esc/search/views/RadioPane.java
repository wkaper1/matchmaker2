package nl.uva.science.esc.search.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Panel that presents a radio buttongroup
 * Layout: FlowLayout
 * 
 * It needs to know and call its owner, which is usually the containing panel, 
 * to notify this owner about a change event. For this purpose we have the
 * RadioPaneListener interface that the owner should implement.
 * @author kaper
 *
 */
public class RadioPane extends JPanel {
	private RadioPaneListener owner;
	private JRadioButton[] buttons;
	private ButtonGroup grp;
	private String currentstate; //text of currently chosen button
	
	public RadioPane(
		RadioPaneListener owner, String[] buttontxts, String currentstate
	) {
		this.owner = owner;
		this.currentstate = currentstate;
		this.setLayout(new FlowLayout());
		for (int i=0; i<buttontxts.length; i++) {
			boolean selected = (currentstate.equals(buttontxts[i]));
			buttons[i] = new JRadioButton(buttontxts[i], selected);
			buttons[i].addActionListener(new RadioHandler());
			grp.add(buttons[i]);
			this.add(buttons[i]);
		}//next i
	}//end constructor
	
	public String getCurrentState() {
		return currentstate;
	}//end getCurrentState
	
	private class RadioHandler implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			currentstate = e.getActionCommand();
			owner.onradiochange(RadioPane.this); //Notify owner: zo pak je de 'this' van de omvattende klasse...
		}//end actionPerformed
	}//end private class RadioHandler
	
	//private void notifyowner() {
	//	owner.onradiochange(this);
	//}//end notifyowner

}//end class
