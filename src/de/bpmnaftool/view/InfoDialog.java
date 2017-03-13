package de.bpmnaftool.view;

import javax.swing.JPanel;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Info Dialog, displays a message
 * 
 * @author Felix Härer
 */
public class InfoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	/**
	 * main content panel
	 */
	private JPanel jContentPane = null;
	/**
	 * message to display
	 */
	private JLabel jLabel = null;

	/**
	 * @param owner
	 */
	public InfoDialog(Frame owner, String title, String text) {
		super(owner);
		initialize(title, text);
	}

	/**
	 * initializes the dialog with given title and text
	 * 
	 * @param title
	 *            title to display
	 * @param text
	 *            text of dialog
	 */
	private void initialize(String title, String text) {
		this.setContentPane(getJContentPane(text));
		this.setTitle(title);
		this.setResizable(false);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane(String text) {
		if (jContentPane == null) {
			jLabel = new JLabel();
			jLabel.setText(text);
			jLabel.setBounds(10, 10, 665, 155);
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel);
		}
		return jContentPane;
	}

}
