package de.bpmnaftool.view;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Rectangle;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * Panel used during the transformation, show status messages and progress.
 * 
 * @author Felix Härer
 */
public class TransformPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	/**
	 * scroll pane for transformation status text
	 */
	private JScrollPane jScrollPane = null;
	/**
	 * shows transformation status text
	 */
	private JTextArea jTextAreaStatus = null;
	/**
	 * shows transformation progress
	 */
	private JProgressBar jProgressBar = null;
	/**
	 * status text
	 */
	private String statusText = "";
	/**
	 * transformation progress
	 */
	private double progress = 0.0;
	/**
	 * worker for background refresh of progress bar
	 */
	@SuppressWarnings("rawtypes")
	private SwingWorker worker;

	/**
	 * This is the default constructor
	 */
	public TransformPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes the TransformPanel
	 */
	private void initialize() {
		this.setSize(585, 370);
		this.setLayout(null);
		this.add(getJScrollPane(), null);
		this.add(getJProgressBar(), null);
		this.worker = getSwingWorker();
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(10, 10, 560, 262));
			jScrollPane.setViewportView(getJTextAreaStatus());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextAreaStatus
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaStatus() {
		if (jTextAreaStatus == null) {
			jTextAreaStatus = new JTextArea();
			jTextAreaStatus.setEditable(false);
			;
			jTextAreaStatus.setText("");
		}
		return jTextAreaStatus;
	}

	/**
	 * adds status text for transformation
	 * 
	 * @param text
	 *            text to add
	 */
	public void addStatusText(String text) {
		statusText += text;
		worker.execute();
	}

	/**
	 * sets progress for the transformation
	 * 
	 * @param progress
	 *            new progress
	 */
	public void setProgress(double progress) {
		if (jProgressBar != null) {
			this.progress = progress;
			worker.execute();
		}
	}

	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(77, 279, 426, 28));
			jProgressBar.setMinimum(0);
			jProgressBar.setMaximum(100);
			jProgressBar.setValue(0);
			Color color = new Color(85, 135, 185);
			jProgressBar.setForeground(color);
			jProgressBar.setStringPainted(true);
		}
		return jProgressBar;
	}

	/**
	 * Update text and progress bar in background
	 * 
	 * @return swing worker for background work
	 */
	@SuppressWarnings("rawtypes")
	private SwingWorker getSwingWorker() {
		SwingWorker worker = new SwingWorker() {

			public String doInBackground() {
				try {
					jTextAreaStatus.setText(statusText);
					jProgressBar.setValue((int) (progress * 100));
				} catch (Exception e) {
				}
				return null;
			}

			public void done() {
				jTextAreaStatus.setText(statusText);
				jProgressBar.setValue((int) (progress * 100));
			}
		};
		return worker;
	}
}
