package de.bpmnaftool.view;

import java.io.File;

/**
 * Interface for the Graphical User Interface. Defines methods to show various frames, dialogs and provides methods to update the view.
 * @author Felix Härer
 */
public interface View {

	/**
	 * title of main window
	 */
	final String windowTitle = "Vom BPMN-WFS zum ausführbaren WFS für AristaFlow";
	/**
	 * height of main window
	 */
	final int windowHeight = 477;
	/**
	 * width of main window
	 */
	final int windowWidth = 585;

	/**
	 * path to header image used when starting the view
	 */
	final String headerImageStart = "/de/bpmnaftool/resources/images/header.png";
	/**
	 * path to header image used to choose files
	 */
	final String headerImageChooseFiles = "/de/bpmnaftool/resources/images/header1.png";
	/**
	 * path to header image after files are chosen
	 */
	final String headerImageClickStart = "/de/bpmnaftool/resources/images/header2.png";
	/**
	 * path to header image during transformation
	 */
	final String headerImageClickBack = "/de/bpmnaftool/resources/images/header3.png";

	/**
	 * path to input ok image (valid or well formed)
	 */
	final String inputImageInputOk = "/de/bpmnaftool/resources/images/inputOk.gif";
	/**
	 * path to input not ok image (not valid or not well formed)
	 */
	final String inputImageInputNotOk = "/de/bpmnaftool/resources/images/inputNotOk.gif";
	/**
	 * path to input image when no file is chosen
	 */
	final String inputImageNoInput = "/de/bpmnaftool/resources/images/noInput.gif";

	/**
	 * path to error image
	 */
	final String errorImage = "/de/bpmnaftool/resources/images/error.png";

	/**
	 * initializes the graphical user interface by starting the main frame and loading the start panel
	 */
	public void initialize();

	/**
	 * Shows an error dialog which contains the message of the exception and the given title
	 * 
	 * @param e
	 *            exception to show
	 * @param title
	 *            title of error
	 */
	public void showError(Exception e, String title);

	/**
	 * Sets the validation and well-formed status. When set to true, the inputOk image is shown, when set
	 * to false, the inputNotOk image is shown.
	 * 
	 * @param isWellFormed
	 *            true if input is well formed, false otherwise
	 * @param isValid
	 *            true if input is valid, false otherwise
	 */
	public void setValidationStatus(boolean isWellFormed, boolean isValid);

	/**
	 * Shows an info dialog with the given title and text
	 * 
	 * @param title
	 *            title of dialog
	 * @param text
	 *            text to display in dialog
	 */
	public void showInfo(String title, String text);

	/**
	 * shows an about window with information about the tool
	 */
	public void showAbout();

	/**
	 * adds transformation status text
	 * 
	 * @param message
	 *            text to add
	 */
	public void addTransformStatusText(String message);

	/**
	 * sets transformation progress to the given percentage
	 * 
	 * @param percentage
	 *            value from 0 to 100 indicating the progress
	 */
	public void setTransformProgress(int percentage);

	/**
	 * shows the file input dialog
	 * 
	 * @return the chosen file
	 */
	public File showFileDialogInputFile();

	/**
	 * shows the output file dialog
	 * 
	 * @return the chosen file
	 */
	public File showFileDialogOutputFile();
}
