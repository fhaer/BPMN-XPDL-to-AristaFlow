package de.bpmnaftool.view;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.JButton;

import de.bpmnaftool.control.BpmnAfToolImpl;

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * Main frame of the program where panels are loaded
 * 
 * @author Felix Härer
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	/**
	 * main content panel
	 */
	private JPanel jContentPane = null;
	/**
	 * header panel for images
	 */
	private JPanel jPanelHeader = null;
	/**
	 * footer panel for buttons
	 */
	private JPanel jPanelFooter = null;
	/**
	 * panel for starting transformation
	 */
	private JPanel jPanelStart = null;
	/**
	 * panel for the transformation
	 */
	private JPanel jPanelTransform = null;
	/**
	 * button to start transformation
	 */
	private JButton jButtonStart = null;
	/**
	 * button to go back after transformation
	 */
	private JButton jButtonBack = null;
	/**
	 * button go get info about program
	 */
	private JButton jButtonInfo = null;
	/**
	 * true if start panel is shown
	 */
	private boolean startPanelShown = false;

	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes the main frame
	 */
	private void initialize() {
		this.setTitle(View.windowTitle);
		this.setContentPane(getJContentPane());
		this.setResizable(false);
	}

	/**
	 * show panel where transformation can be started
	 */
	public void showStartPanel() {
		if (jContentPane == null)
			return;
		if (startPanelShown)
			return;
		jButtonBack.setEnabled(false);
		if (jPanelTransform != null) {
			jButtonStart.setEnabled(false);
			jContentPane.remove(jPanelTransform);
		}
		jContentPane.add(getJPanelStart());
		setHeaderImage(ViewImpl.headerImageChooseFiles);
		jContentPane.paint(jContentPane.getGraphics());
		startPanelShown = true;
	}

	/**
	 * show panel for transformation
	 */
	public void showTransformPanel() {
		if (jContentPane == null)
			return;
		if (!startPanelShown)
			return;
		jButtonBack.setEnabled(true);
		jButtonStart.setEnabled(false);
		if (jPanelStart != null) {
			jContentPane.remove(jPanelStart);
			jPanelStart = null;
		}
		jContentPane.add(getJPanelTransform());
		setHeaderImage(ViewImpl.headerImageClickBack);
		jContentPane.paint(jContentPane.getGraphics());
		startPanelShown = false;
	}

	/**
	 * returns transformation panel
	 * 
	 * @return TransformPanel
	 */
	public TransformPanel getTransformPanel() {
		if (jPanelTransform == null) {
			return null;
		}
		return (TransformPanel) jPanelTransform;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJPanelHeader(ViewImpl.headerImageStart), null);
			jContentPane.add(getJPanelFooter(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes getJPanelStart
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelStart() {
		if (jPanelStart == null) {
			jPanelStart = new StartPanel();
			jPanelStart.setBounds(new Rectangle(0, 72, getSize().width, 310));
		}
		return jPanelStart;
	}

	/**
	 * This method initializes getJPanelStart
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelTransform() {
		// always get new panel
		if (jPanelTransform != null) {
			jPanelTransform = null;
		}
		jPanelTransform = new TransformPanel();
		jPanelTransform.setBounds(new Rectangle(0, 72, getSize().width, 310));
		return jPanelTransform;
	}

	/**
	 * This method initializes jPanelHeader
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelHeader(final BufferedImage image) {
		if (jPanelHeader == null) {
			jPanelHeader = new JPanel() {
				private static final long serialVersionUID = 1L;

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, this);
				}
			};
			jPanelHeader.setLayout(null);
			jPanelHeader.setBounds(new Rectangle(0, 0, getSize().width, 71));
			jPanelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
		}
		return jPanelHeader;
	}

	/**
	 * Sets the header image to the given mage
	 * 
	 * @param image
	 *            image to set
	 */
	private void setHeaderImage(BufferedImage image) {
		if (jContentPane != null && jPanelHeader != null) {
			jContentPane.remove(jPanelHeader);
			jPanelHeader = null;
			jContentPane.add(getJPanelHeader(image), null);
		}
	}

	/**
	 * This method initializes jPanelFooter
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelFooter() {
		if (jPanelFooter == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jPanelFooter = new JPanel();
			jPanelFooter.setLayout(null);
			jPanelFooter.setBounds(new Rectangle(15, 386, 549, 52));
			jPanelFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
			jPanelFooter.add(getJButtonStart(), gridBagConstraints);
			jPanelFooter.add(getJButtonBack(), gridBagConstraints);
			jPanelFooter.add(getJButtonInfo(), gridBagConstraints);
		}
		return jPanelFooter;
	}

	/**
	 * This method initializes jButtonStart
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setSize(new Dimension(100, 27));
			jButtonStart.setLocation(new Point(430, 17));
			jButtonStart.setText("Start >");
			jButtonStart.setEnabled(false);
			jButtonStart.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showTransformPanel();
					BpmnAfToolImpl.getInstance().transformModel();
				}
			});
		}
		return jButtonStart;
	}

	/**
	 * This method initializes jButtonStart
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonBack() {
		if (jButtonBack == null) {
			jButtonBack = new JButton();
			jButtonBack.setSize(new Dimension(100, 27));
			jButtonBack.setLocation(new Point(325, 17));
			jButtonBack.setText("< Zurück");
			jButtonBack.setEnabled(false);
			jButtonBack.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showStartPanel();
				}
			});
		}
		return jButtonBack;
	}

	/**
	 * This method initializes jButtonStart
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonInfo() {
		if (jButtonInfo == null) {
			jButtonInfo = new JButton();
			jButtonInfo.setSize(new Dimension(100, 27));
			jButtonInfo.setLocation(new Point(17, 17));
			jButtonInfo.setText("Über");
			jButtonInfo.setEnabled(true);
			jButtonInfo.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					BpmnAfToolImpl.getInstance().getView().showAbout();
				}
			});
		}
		return jButtonInfo;
	}

	/**
	 * sets the validation status images
	 * 
	 * @param isWellFormed
	 *            true well formed, false otherwise
	 * @param isValid
	 *            true for valid, false otherwise
	 */
	public void setValidationStatus(boolean isWellFormed, boolean isValid) {
		showStartPanel();
		if (jPanelStart != null) {
			((StartPanel) jPanelStart).setWellFormedImage(isWellFormed);
			((StartPanel) jPanelStart).setVaildImage(isValid);
			if (isWellFormed && isValid) {
				setHeaderImage(ViewImpl.headerImageClickStart);
			}
		}
		jButtonStart.setEnabled(isValid && isWellFormed);
	}

}
