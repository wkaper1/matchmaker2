package nl.uva.science.esc.search.views;

import java.awt.*;
import javax.swing.*;

/**
 * Represents the main application window of the problem solving application.
 * 
 * ** This class is obsolete, it just contains a few ideas I need to copy **
 * 
 * @author kaper
 *
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1050835250869933150L;
	//Properties: components to show on the window
	Container container;    //frame container
	GridLayout gridLayout1;
	private JLabel label1;
	private JList<String> problemList;
	private static final String[] PROBLEMS = {
		"Student-project matching problem", "Many-to-one matching problem"
		};
	private JLabel label2;
	private JLabel label3;
	
	public MainWindow() {
		super("Problem Solver");
		//create layout
		gridLayout1 = new GridLayout(3, 2, 5, 5); //3 rows, 2 cols, 5px gaps
		container = getContentPane();
		setLayout(gridLayout1);
		//fill the layout: 
		//label in first column, contents in second
		//
		//Problem type selection list
		label1 = new JLabel("Problem type");
		add(label1);
		problemList = new JList<String>(PROBLEMS);
		problemList.setVisibleRowCount(PROBLEMS.length);
		problemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(problemList));
		//
		//Problem data file selection and / or URL retriever
		label2 = new JLabel("Get problem data");
		add(label2);
		//ToDo: create the data retrievers
		//
		//Problem parameters  fill-in subwindow
		label3 = new JLabel("Set problem parameters");
		add(label3);
		//Todo: add the subwindow
		
	}//end constructor
}//end class
