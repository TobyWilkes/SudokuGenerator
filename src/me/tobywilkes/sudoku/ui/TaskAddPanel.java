package me.tobywilkes.sudoku.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class TaskAddPanel extends JPanel {
	Logger logger = Logger.getLogger(TaskAddPanel.class.getSimpleName());
	JLabel introText = new JLabel("Add requests to SudokuBuilder below");
	JSpinner taskQuantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
	JButton submitButton = new JButton("Make Requests");
	
	Semaphore userRequests;
	
	public TaskAddPanel(Semaphore userRequests) {
		this.userRequests = userRequests;
		
		this.setPreferredSize(new Dimension(250, 200));
		add(introText);
		add(taskQuantitySpinner);
		add(submitButton);
		
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Added " + ((int) taskQuantitySpinner.getValue()) + " tasks.");
				userRequests.release((int) taskQuantitySpinner.getValue());
			}
		});
	}
}
