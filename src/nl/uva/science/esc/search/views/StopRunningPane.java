package nl.uva.science.esc.search.views;

import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

import nl.uva.science.esc.search.techniques.Technique;

/**
 * This pane shows the run-status (running, stop requested, or stopped) of a
 * Technique object. Also it contains a Stop button which sends the Technique
 * object a Stop signal.
 * 
 * To update the runstatus, and to see the effect of a stop signal, an outside
 * controller should call the refresh method regularly. There is no refresh
 * button on this small pane. 
 * @author kaper
 *
 */
public class StopRunningPane extends JPanel {
	private Technique t; //the live Technique object that we should connect to
	private JLabel runstatus;
	private JButton stop;

	/**
	 * Constructor
	 * @param t, The Technique object to connect to, it should be in running state
	 *   when offered here as input (otherwise you can't stop it).
	 */
	public StopRunningPane(Technique t) {
		this.t = t;
		this.setLayout(new FlowLayout());
		runstatus = new JLabel("Runstatus...");
		this.add(runstatus);
		stop = new JButton("Stop running");
		stop.addActionListener(new StopHandler());
		this.add(stop);
		this.refresh();
	}//end StopRunningPane
	
	/**
	 * Stop button handler
	 */
	private class StopHandler implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			int choice = JOptionPane.showConfirmDialog(
				StopRunningPane.this, "Stop searching...?", "Stop", 
				JOptionPane.YES_NO_OPTION
				);
			if (choice==JOptionPane.YES_OPTION) {
				t.stopRunning();
				runstatus.setText("Runstatus: Stop requested");				
			}//end if
		}//end actionPerformed
	}//end private class StopHandler
	
	/**
	 * Refresh the run status, call it regularly to see the current status
	 */
	public void refresh() {
		String status = (t.isRunning()) ? ("running") : ("stopped");
		runstatus.setText("Runstatus: " + status);
	}//end refresh
	
}//end class
