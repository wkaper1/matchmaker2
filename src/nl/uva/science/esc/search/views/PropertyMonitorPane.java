package nl.uva.science.esc.search.views;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * A pane for showing properties of model objects, as name-value pairs.
 * Properties are limited to simple ones. Values must be supplied as Strings.
 * 
 * The pane will have a refresh button. On click the property values will be
 * refreshed from the object, e.g. to show progress in an ongoing calculation.
 * 
 * There will be a 2-column table with possibly a text above and below it.
 * The refresh button will be at the top right.
 * 
 * @author kaper
 *
 */
public class PropertyMonitorPane extends JPanel {
	
	PropertyAdvertiser pa; //the model object that we need to show
	
	private String abovetext;
	private String belowtext;
	private String[] propertynames; //they are supposed not to change, unlike the values
	private BorderLayout layout1;
	private GridLayout layout2; //layout for inner panel
	private JPanel innerpanel; //panel inside this panel that has the table
	private JPanel toppanel; //north panel, it will have the button and the "above" text
	private JButton refresh; //refresh button
	private JLabel[] propertyvalues; //JLabels that will contain the property values
	
	public PropertyMonitorPane(
		String abovetext, String belowtext, PropertyAdvertiser pa
	) {
		this.abovetext = abovetext;
		this.belowtext = belowtext;
		this.pa = pa;
		this.propertynames = pa.advertiseSimpleProperties();
		//build the view, with the button in it
		this.layout1 = new BorderLayout(5, 5);
		this.setLayout(layout1);
		//north area
		this.toppanel = new JPanel();
		toppanel.setLayout(new FlowLayout(5));
		if (abovetext.length()>0) {
			toppanel.add(new JLabel(this.abovetext));
		}//end if
		this.refresh = new JButton("Refresh");
		RefreshHandler h = new RefreshHandler();
		refresh.addActionListener(h);
		toppanel.add(refresh);
		this.add(toppanel, BorderLayout.NORTH);
		//south area
		if (belowtext.length()>0) {
			this.add(new JLabel(this.belowtext), BorderLayout.SOUTH);			
		}//end if
		//center area
		if (propertynames.length>0) {
			this.innerpanel = new JPanel();
			this.layout2 = new GridLayout(propertynames.length, 2, 5, 5);
			innerpanel.setLayout(layout2);
			for (int i=0; i<propertynames.length; i++) {
				//put the property name in the first column
				innerpanel.add(new JLabel(propertynames[i]));
				//put an (empty) label for the value in the second column
				propertyvalues[i] = new JLabel("");
				innerpanel.add(propertyvalues[i]);
			}//next i
		}//end if
		//run once the refresh method to fill the values JLabels
		refresh();
	}//end constructor
	
	private class RefreshHandler implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			refresh();
		}//end actionPerformed
	}//end private class RefreshHandler

	//It is not inside the handler class because we want to call it separately too
	//for instance by a timer, which could be inside this class
	//...or we could decide to make it public...
	private void refresh() {
		String[] values = pa.simplePropertyValues();
		for (int i=0; i<propertyvalues.length; i++) {
			propertyvalues[i].setText(values[i]);
		}//next i			
	}//end refresh
	
}//end class
