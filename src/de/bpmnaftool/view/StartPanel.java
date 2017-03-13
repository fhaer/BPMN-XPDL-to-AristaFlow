package de.bpmnaftool.view;

import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Rectangle;

import javax.swing.JTextField;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.bpmnaftool.control.BpmnAfToolImpl;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Panel to start the transformation
 * 
 * @author Felix Härer
 */
public class StartPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	/**
	 * input file label
	 */
	private JLabel jLabelInput = null;
	/**
	 * output file label
	 */
	private JLabel jLabelOutput = null;
	/**
	 * displays input file path
	 */
	private JTextField jTextFieldInput = null;
	/**
	 * displays output file path
	 */
	private JTextField jTextFieldOutput = null;
	/**
	 * label for schema valid
	 */
	private JLabel jLabelSchemaValid = null;
	/**
	 * label for schema well formed
	 */
	private JLabel jLabelXmlWellFormed = null;
	/**
	 * panel for well formed image
	 */
	private JPanel jPanelXmlWellFormed = null;
	/**
	 * panel for valid image
	 */
	private JPanel jPanelXmlValid = null;
	/**
	 * button to choose input file
	 */
	private JButton jButtonInput = null;
	/**
	 * button to choose output file
	 */
	private JButton jButtonOutput = null;

	/**
	 * This is the default constructor
	 */
	public StartPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes the StartPanel
	 * 
	 */
	private void initialize() {
		jLabelXmlWellFormed = new JLabel();
		jLabelXmlWellFormed.setBounds(new Rectangle(95, 105, 271, 31));
		jLabelXmlWellFormed.setFont(new Font("Dialog", Font.PLAIN, 13));
		jLabelXmlWellFormed.setText("XML-Datei wohlgeformt");
		jLabelSchemaValid = new JLabel();
		jLabelSchemaValid.setBounds(new Rectangle(95, 138, 430, 31));
		jLabelSchemaValid.setFont(new Font("Dialog", Font.PLAIN, 13));
		jLabelSchemaValid.setText("XML-Datei valide (XPDL-Schema 2.1 mit Einschränkungen)");
		jLabelInput = new JLabel();
		jLabelInput.setBounds(new Rectangle(45, 25, 69, 33));
		jLabelInput.setHorizontalAlignment(SwingConstants.LEFT);
		jLabelInput.setHorizontalTextPosition(SwingConstants.LEFT);
		jLabelInput.setFont(new Font("Dialog", Font.BOLD, 14));
		jLabelInput.setText("Eingabe");
		jLabelOutput = new JLabel();
		jLabelOutput.setBounds(new Rectangle(45, 190, 69, 33));
		jLabelOutput.setHorizontalAlignment(SwingConstants.LEFT);
		jLabelOutput.setHorizontalTextPosition(SwingConstants.LEFT);
		jLabelOutput.setFont(new Font("Dialog", Font.BOLD, 14));
		jLabelOutput.setText("Ausgabe");
		this.setSize(585, 370);
		this.setLayout(null);
		this.add(jLabelInput, null);
		this.add(jLabelOutput, null);
		this.add(getJTextFieldInput(), null);
		this.add(getJTextFieldOutput(), null);
		this.add(jLabelSchemaValid, null);
		this.add(jLabelXmlWellFormed, null);
		this.add(getJPanelXMLWellFormed(ViewImpl.inputImageNoInput), null);
		this.add(getJPanelXmlValid(ViewImpl.inputImageNoInput), null);
		this.add(getJButtonInput(), null);
		this.add(getJButtonOutput(), null);
	}

	/**
	 * This method initializes jTextFieldInput
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldInput() {
		if (jTextFieldInput == null) {
			jTextFieldInput = new JTextField();
			jTextFieldInput.setBounds(new Rectangle(45, 60, 361, 35));
			jTextFieldInput.setEditable(false);
		}
		return jTextFieldInput;
	}

	/**
	 * This method initializes jTextFieldOutput
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldOutput() {
		if (jTextFieldOutput == null) {
			jTextFieldOutput = new JTextField();
			jTextFieldOutput.setBounds(new Rectangle(45, 225, 361, 35));
			jTextFieldOutput.setEditable(false);
		}
		return jTextFieldOutput;
	}

	/**
	 * This method initializes jPanelXMLWellFormed
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelXMLWellFormed(final BufferedImage image) {
		if (jPanelXmlWellFormed == null) {
			jPanelXmlWellFormed = new JPanel() {
				private static final long serialVersionUID = 1L;

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, this);
				}
			};
			jPanelXmlWellFormed.setLayout(new GridBagLayout());
			jPanelXmlWellFormed.setBounds(new Rectangle(63, 112, 16, 16));
		}
		return jPanelXmlWellFormed;
	}

	/**
	 * This method initializes jPanelXmlValid
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelXmlValid(final BufferedImage image) {
		if (jPanelXmlValid == null) {
			jPanelXmlValid = new JPanel() {
				private static final long serialVersionUID = 1L;

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, this);
				}
			};
			jPanelXmlValid.setLayout(new GridBagLayout());
			jPanelXmlValid.setBounds(new Rectangle(63, 145, 16, 16));
		}
		return jPanelXmlValid;
	}

	/**
	 * This method initializes jButtonInput
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonInput() {
		if (jButtonInput == null) {
			jButtonInput = new JButton();

			jButtonInput.setBounds(new Rectangle(420, 64, 107, 27));
			jButtonInput.setText("Datei wählen");
			jButtonInput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					View view = ViewImpl.getInstance();
					File in = view.showFileDialogInputFile();
					setInputFile(in);
				}
			});
		}
		return jButtonInput;
	}

	/**
	 * This method initializes jButtonOutput
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOutput() {
		if (jButtonOutput == null) {
			jButtonOutput = new JButton();
			jButtonOutput.setBounds(new Rectangle(420, 228, 107, 27));
			jButtonOutput.setText("Datei wählen");
			jButtonOutput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					View view = ViewImpl.getInstance();
					File out = view.showFileDialogOutputFile();
					setOutputFile(out);
				}
			});
		}
		return jButtonOutput;
	}

	/**
	 * sets the output file in text field and calls controller
	 * 
	 * @param outputFile
	 *            file to set
	 */
	private void setOutputFile(File outputFile) {
		if (outputFile == null) {
			return;
		}
		if (!outputFile.isDirectory()) {
			// add file extension, if not already present
			String path = outputFile.getPath();
			outputFile = new File(path);
			jTextFieldOutput.setText(outputFile.getPath());
			BpmnAfToolImpl.getInstance().setOutputFile(outputFile);
		} else {
			throw new IllegalArgumentException("Ausgabedatei darf kein Ordner sein");
		}
	}

	/**
	 * sets the input file in text field and calls controller
	 * 
	 * @param inputFile
	 *            file to set
	 */
	private void setInputFile(File inputFile) {
		if (inputFile == null) {
			return;
		}
		if (inputFile.canRead() || !inputFile.isDirectory()) {
			jTextFieldInput.setText(inputFile.getPath());
			BpmnAfToolImpl.getInstance().setInputFile(inputFile);
			setOutputFile(new File(inputFile.getPath() + ".template"));
		} else {
			throw new IllegalArgumentException("Kein Zugriff auf Eingabedatei möglich");
		}
	}

	/**
	 * sets the validation image
	 * 
	 * @param valid
	 *            true if valid, false otherwise
	 */
	public void setVaildImage(boolean valid) {
		remove(jPanelXmlValid);
		jPanelXmlValid = null;
		if (valid)
			jPanelXmlValid = getJPanelXmlValid(ViewImpl.inputImageInputOk);
		else
			jPanelXmlValid = getJPanelXmlValid(ViewImpl.inputImageInputNotOk);
		add(jPanelXmlValid);
		jPanelXmlValid.paint(jPanelXmlValid.getGraphics());
	}

	/**
	 * sets the well-formed image
	 * 
	 * @param wellFormed
	 *            true if well-formed, false otherwise
	 */
	public void setWellFormedImage(boolean wellFormed) {
		remove(jPanelXmlWellFormed);
		jPanelXmlWellFormed = null;
		if (wellFormed)
			jPanelXmlWellFormed = getJPanelXMLWellFormed(ViewImpl.inputImageInputOk);
		else
			jPanelXmlWellFormed = getJPanelXMLWellFormed(ViewImpl.inputImageInputNotOk);
		add(jPanelXmlWellFormed);
		jPanelXmlWellFormed.paint(jPanelXmlWellFormed.getGraphics());
	}
}
