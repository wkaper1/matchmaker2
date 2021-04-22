package nl.uva.science.esc.search.views;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A TablePane is a rectangular area that shows a formatted table
 * ToDo: If the table is too large for the area, one or two scroll bars 
 * should appear!
 * 
 * The table body has N columns and M rows.
 * There is one extra table header row with separate formatting, whose
 * contents are declared separately from the body.
 * There might be an optional footer row with separate formatting, whose
 * contents are also declared separately.
 * 
 * The separate formatting of header, body and footer is hardcoded into the
 * class. If you want different formatting, edit the class.
 * We choose as body layout: white background, black text, black gridlines
 * Header and footer layout: black background, white text, no gridlines
 * 
 * Contents for the various cells have to be provided as Strings
 * @author kaper
 *
 */
public class TablePane extends JPanel {
	private int cols; //number of columns of the table
	private String[] headerrow;
	private String[] footerrow;
	private String[][] body;
	private GridLayout layout;
	private JLabel[] headercells;
	private JLabel[] footercells;
	private JLabel[][] bodycells; //it makes them adressable, but we don't use it... yet

	/**
	 * There should be a header row, therefore we ask it in the constructor
	 * It determines the width (number of columns) of the table as well as 
	 * the header row texts
	 * @param headerrow
	 */
	public TablePane(String[] headerrow) {
		this.cols = headerrow.length;
		this.headerrow = headerrow;
	}//end constructor
	
	/**
	 * Set the header row texts
	 * @param headerrow
	 * @throws Exception when the row does not fit
	 */
	public void setHeader(String[] headerrow) throws Exception {
		if (headerrow.length==this.cols) {
			this.headerrow = headerrow;
		}
		else {
			throw new Exception("Does not fit table width.");
		}//end if
		if (isrenderable()) rerenderheader();
	}//end setHeader
	
	/**
	 * Set footer row texts
	 * @param footerrow
	 * @throws Exception when the row does not fit
	 */
	public void setFooter(String[] footerrow)throws Exception {
		boolean isnew = (this.footerrow==null);
		if (footerrow.length==this.cols) {
			this.footerrow = footerrow;
		}
		else {
			throw new Exception("Does not fit table width.");
		}//end if
		if (isrenderable()) {
			if (isnew) render();
			else rerenderfooter();
		}//end if
	}//end setFooter

	/**
	 * Set the table body
	 * @param body a 2D array of strings
	 * @throws Exception when the body does not fit into the defined table width
	 */
	public void setBody(String[][] body) throws Exception {
		if (body[0].length==this.cols) {
			this.body = body;
		}
		else {
			throw new Exception("Does not fit table width.");
		}//end if
		if (isrenderable()) render();
	}//end setBody
	
	/**
	 * Do we have enough information to show the table ? 
	 * @return
	 */
	public boolean isrenderable() {
		return (headerrow!=null && cols>0 && body!=null);
	}//end isrenderable
	
	/**
	 * Total rendering of the table, this is necessary after a new body
	 * has been set, possibly with a different number of rows. The
	 * GridLayout then must be replaced and we need to fill the new one.
	 */
	private void render() {
		//build the frame
		int rows = body.length + 1;   //1 extra voor de header
		if (footerrow!=null) rows++;  //nog 1 extra
		layout = new GridLayout(rows, cols, 1, 1); //gridlines door achtergrond te kleuren?
		this.setLayout(layout);
		this.setBackground(new Color(1, 1, 1)); //achtergrond zwart ?

		//put the contents in
		//first the header row: background color black, text color white
		for (int j=0; j<cols; j++) {
			headercells[j] = new JLabel(headerrow[j]);
			headercells[j].setBackground(new Color(1,1,1));
			headercells[j].setForeground(new Color(0,0,0));
			this.add(headercells[j]);
		}// next j

		//then the body cells: bacground white, text color black
		for (int i=0; i<body.length; i++) {
			for (int j=0; j<cols; j++) {
				bodycells[i][j] = new JLabel(body[i][j]);
				this.add(bodycells[i][j]);
			}// next j
		}// next i

		//then the footer tow if it was given
		if (footerrow!=null) {
			for (int j=0; j<cols; j++) {
				footercells[j] = new JLabel(footerrow[j]);
				footercells[j].setBackground(new Color(1,1,1));
				footercells[j].setForeground(new Color(0,0,0));
				this.add(footercells[j]);				
			}// next j
		}//end if
	}//end render
	
	//beetje rare kopieeractiviteiten hier onder!... kan het anders?
	//Je zou die hele Strings arrays kunnen weglaten en vervangen door 
	//JLabel arrays... maar dan gaan inhoud en vormgeving door elkaar lopen!
	
	/**
	 * If we only want to show a changed header... (we don't use it yet)
	 * It is just to show the adressability of the JLabels
	 */
	private void rerenderheader() {
		for (int j=0; j<cols; j++) {
			headercells[j].setText(headerrow[j]);
		}//next j
	}//end rerenderheader
	
	/**
	 * If we only wanted to show a changed footer...
	 */
	private void rerenderfooter() {
		for (int j=0; j<cols; j++) {
			footercells[j].setText(footerrow[j]);
		}//next j
	}//end rerenderfooter

}//end class
