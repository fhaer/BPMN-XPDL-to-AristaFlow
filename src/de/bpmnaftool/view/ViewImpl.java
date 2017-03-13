package de.bpmnaftool.view;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.bpmnaftool.control.BpmnAfToolImpl;

/**
 * Implementation of the graphical user interface
 * 
 * @author Felix Härer
 */
public class ViewImpl implements View, Observer {

	/**
	 * instance to this class (singleton)
	 */
	private static View instance;

	/**
	 * main frame where panels are displays
	 */
	MainFrame mainFrame;

	/**
	 * header image for start of GUI
	 */
	static BufferedImage headerImageStart;
	/**
	 * header image while choosing files
	 */
	static BufferedImage headerImageChooseFiles;
	/**
	 * header image indicating that start of transformation is possible
	 */
	static BufferedImage headerImageClickStart;
	/**
	 * header image during transformation
	 */
	static BufferedImage headerImageClickBack;
	/**
	 * input ok (valid or well formed) image
	 */
	static BufferedImage inputImageInputOk;
	/**
	 * input not ok (not valid or well formed) image
	 */
	static BufferedImage inputImageInputNotOk;
	/**
	 * input image when no file is loaded
	 */
	static BufferedImage inputImageNoInput;
	/**
	 * image shown when an error occurs
	 */
	static BufferedImage errorImage;

	/**
	 * Get only instance to this (singleton)
	 * 
	 * @return instance to view
	 */
	public static View getInstance() {
		if (instance == null) {
			synchronized (ViewImpl.class) {
				if (instance == null) {
					instance = new ViewImpl();
				}
			}
		}
		return instance;
	}

	/**
	 * Private constructor (use getInstance method)
	 */
	private ViewImpl() {
		loadImages();
	}

	/**
	 * Creates main frame, sets window title
	 */
	public void initialize() {
		if (mainFrame == null) {
			setLookAndFeel();
			mainFrame = new MainFrame();
			setBoundsToCenter(mainFrame, View.windowWidth, View.windowHeight);
			mainFrame.showStartPanel();
			mainFrame.setVisible(true);
			addExitWindowListener(mainFrame);
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		// update either status text or progress
		if (data instanceof String) {
			addTransformStatusText((String) data);
		} else if (data instanceof Integer) {
			setTransformProgress((Integer) data);
		}
	}

	/**
	 * sets the bounds of a window the center of the screen and sets dimensions
	 * 
	 * @param frame
	 *            window to set bounds of
	 * @param width
	 *            set width to this value
	 * @param heigth
	 *            set height to this value
	 */
	private void setBoundsToCenter(Window frame, int width, int heigth) {
		if (frame != null) {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			int screenWidth = (int) dim.getWidth();
			int screenHeight = (int) dim.getHeight();
			int boundsX = (screenWidth - width) / 2;
			int boundsY = (screenHeight - heigth) / 2;
			frame.setBounds(boundsX, boundsY, width, heigth);
		}
	}

	@Override
	public void setValidationStatus(boolean isWellFormed, boolean isValid) {
		if (mainFrame != null) {
			mainFrame.setValidationStatus(isWellFormed, isValid);
		}
	}

	@Override
	public void showError(Exception e, String title) {
		String msg = getExceptionString(e);
		if (mainFrame != null) {
			ErrorDialog err = new ErrorDialog(mainFrame);
			err.showError(title, msg);
			setBoundsToCenter(err, 580, 340);
			err.setVisible(true);
		}
		BpmnAfToolImpl.getInstance().log(msg, 0, 1);
	}

	/**
	 * returns a string which contains information about the given exception
	 * 
	 * @param e
	 *            exception to return as string
	 * @return information string
	 */
	private String getExceptionString(Exception e) {
		String msg = "";
		String newline = System.getProperty("line.separator");
		msg += e.getMessage();
		msg += newline + newline;
		msg += "Exception: " + newline + e.getClass().toString();
		msg += newline + newline;
		msg += "Stack trace:" + newline;
		for (StackTraceElement el : e.getStackTrace()) {
			msg += el.getClassName() + "." + el.getMethodName();
			msg += "(line " + el.getLineNumber() + ")";
			msg += System.getProperty("line.separator");
		}
		return msg;
	}

	/**
	 * Try to set look and feel to system look and feel, sets it to cross-platform look and feel if it
	 * fails.
	 */
	private void setLookAndFeel() {
		try {
			String lfClassName;
			lfClassName = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lfClassName);
		} catch (Exception e) {
			setCrossPlattformLookAndFeel();
		}
	}

	/**
	 * Try to set look and feel to cross-platform, exits if it fails.
	 */
	private void setCrossPlattformLookAndFeel() {

		try {
			String lfClassName;
			lfClassName = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lfClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			unsupportedPlattformExit();
		} catch (InstantiationException e) {
			e.printStackTrace();
			unsupportedPlattformExit();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			unsupportedPlattformExit();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
			unsupportedPlattformExit();
		}
	}

	/**
	 * exits application if UI can not be created
	 */
	private void unsupportedPlattformExit() {
		String message;
		message = "LookAndFeel class not found - plattform not supported.";
		System.err.println(message);
		System.exit(1);
	}

	/**
	 * Adds a window listener to the given frame which exits system if the window is closed
	 * 
	 * @param frame
	 *            window where listener is added to
	 */
	private void addExitWindowListener(JFrame frame) {
		if (frame != null) {
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				}
			});
		}
	}

	@Override
	public void showInfo(String title, String text) {
		if (mainFrame != null) {
			String html = "<html>" + text + "</html>";
			InfoDialog info = new InfoDialog(mainFrame, title, html);
			setBoundsToCenter(info, 675, 210);
			info.setVisible(true);
		}
	}

	@Override
	public void showAbout() {
		String headline;
		String text;
		String date;
		String author;
		String br;
		headline = "<b>Werkzeug für die Transformation von BPMN-Workflowschemata (XPDL 2.1) nach AristaFlow (v1.0.1)</b>";
		text = "Vom hochflexiblen Geschäftsprozess zum ausführbaren Workflowschema für AristaFlow";
		date = "24. März 2012";
		author = "Felix Härer";
		br = "<br><br>";
		showInfo("Über", headline + br + text + br + date + br + author);
	}

	@Override
	public void addTransformStatusText(String message) {
		if (mainFrame != null) {
			TransformPanel panel = mainFrame.getTransformPanel();
			if (panel != null) {
				panel.addStatusText(message);
			}
		}
	}

	@Override
	public void setTransformProgress(int percentage) {
		if (mainFrame != null) {
			TransformPanel panel = mainFrame.getTransformPanel();
			if (panel != null) {
				panel.setProgress(percentage * 0.01);
			}
		}
	}

	@Override
	public File showFileDialogInputFile() {
		String title = "XPDL-Datei auswählen";
		return showFileDialog(title, FileDialog.LOAD);
	}

	@Override
	public File showFileDialogOutputFile() {
		String title = "Template-Datei für AristaFlow auswählen";
		return showFileDialog(title, FileDialog.SAVE);
	}

	/**
	 * shows a file dialog
	 * 
	 * @param title
	 *            title of dialog
	 * @param mode
	 *            FileDialog mode, determines if a load-file or save-file dialog is shown
	 * @return chosen file
	 */
	private File showFileDialog(String title, int mode) {
		File choosenFile = null;
		FileDialog fileDialog = new FileDialog(mainFrame, title, mode);
		fileDialog.setLocationRelativeTo(mainFrame);
		fileDialog.setVisible(true);
		if (fileDialog.getFile() != null) {
			String path = fileDialog.getDirectory() + fileDialog.getFile();
			choosenFile = new File(path);
		}
		fileDialog.dispose();
		return choosenFile;
	}

	/**
	 * loads images
	 */
	private void loadImages() {
		headerImageStart = getImage(View.headerImageStart);
		headerImageClickStart = getImage(View.headerImageClickStart);
		headerImageClickBack = getImage(View.headerImageClickBack);
		headerImageChooseFiles = getImage(View.headerImageChooseFiles);
		inputImageInputNotOk = getImage(View.inputImageInputNotOk);
		inputImageInputOk = getImage(View.inputImageInputOk);
		inputImageNoInput = getImage(View.inputImageNoInput);
		errorImage = getImage(View.errorImage);
	}

	/**
	 * loads an image from the given path
	 * 
	 * @param imgPath
	 *            path of image
	 * @return loaded iamge
	 */
	static BufferedImage getImage(String imgPath) {
		java.net.URL imgUrl;
		imgUrl = BpmnAfToolImpl.getInstance().getClass().getResource(imgPath);
		try {
			return ImageIO.read(imgUrl);
		} catch (IOException e) {
			throw new IllegalStateException("Image not found: " + imgPath);
		}

	}
}
