package de.bpmnaftool.view;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.Graphics;

import javax.swing.JDialog;
import javax.swing.JLabel;

import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 * Error dialog, displays error messages and exceptions
 * 
 * @author Felix Härer
 */
public class ErrorDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	/**
	 * main content panel
	 */
	private JPanel jContentPane = null;
	/**
	 * error label
	 */
	private JLabel jLabelError = null;
	/**
	 * error scroll pane
	 */
	private JScrollPane jScrollPaneErrorText = null;
	/**
	 * error image
	 */
	private JPanel jPanelGraphics = null;
	/**
	 * error message (text)
	 */
	private JTextArea jTextAreaError = null;

	/**
	 * @param owner
	 */
	public ErrorDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes the error dialog
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Fehler");
		this.setResizable(false);
	}

	/**
	 * displays an error message
	 * 
	 * @param title
	 *            title of the message
	 * @param message
	 *            message contents
	 */
	public void showError(String title, String message) {
		initialize();
		setErrorTitle(title);
		if (message != null && !message.isEmpty()) {
			jTextAreaError.setText(message);
			jTextAreaError.setCaretPosition(0);
			this.validate();
			this.repaint();
		}
	}

	/**
	 * sets the title of the error
	 * 
	 * @param title
	 *            title
	 */
	private void setErrorTitle(String title) {
		jLabelError.setText(title);
		this.validate();
		this.repaint();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelError = new JLabel();
			jLabelError.setBounds(new Rectangle(65, 14, 541, 32));
			jLabelError.setFont(new Font("Dialog", Font.BOLD, 14));
			jLabelError.setText("Unbekannter Fehler");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabelError, null);
			jContentPane.add(getJScrollPaneErrorText(), null);
			jContentPane.add(getJPanelGraphics(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPaneErrorText
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneErrorText() {
		if (jScrollPaneErrorText == null) {
			jScrollPaneErrorText = new JScrollPane();
			jScrollPaneErrorText.setBounds(new Rectangle(11, 60, 552, 236));
			jScrollPaneErrorText.setViewportView(getJTextAreaError());
		}
		return jScrollPaneErrorText;
	}

	/**
	 * This method initializes jPanelGraphics with a default image
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelGraphics() {
		if (jPanelGraphics == null) {
			final BufferedImage image = ViewImpl.errorImage;
			jPanelGraphics = new JPanel() {
				private static final long serialVersionUID = 1L;

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, this);
				}
			};
			jPanelGraphics.setLayout(new GridBagLayout());
			jPanelGraphics.setBounds(new Rectangle(20, 14, 90, 90));
		}
		return jPanelGraphics;
	}

	/**
	 * This method initializes jTextFieldError
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextArea getJTextAreaError() {
		if (jTextAreaError == null) {
			jTextAreaError = new JTextArea();
			jTextAreaError.setEditable(false);
			jTextAreaError.setText("Für diese Fehler liegt keine Beschreibung vor");
			jTextAreaError.setBorder(null);
			Color background = new Color(240, 240, 240);
			jTextAreaError.setBackground(background);
		}
		return jTextAreaError;
	}

} 
