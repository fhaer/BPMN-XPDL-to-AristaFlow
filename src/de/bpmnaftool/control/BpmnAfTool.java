package de.bpmnaftool.control;

import java.io.File;

import de.bpmnaftool.model.aristaflow.AristaFlowModel;
import de.bpmnaftool.model.bpmn.BpmnModel;
import de.bpmnaftool.view.View;


/**
 * This interface and its underlying implementation are used to start the BpmnAfTool, manage all of its
 * functions and delegate user interaction from the view to other controllers.
 * 
 * @author Felix Härer
 */
public interface BpmnAfTool {

	/**
	 * Initializes the BpmnAfTool
	 */
	public void initialize();

	/**
	 * Starts the graphical user interface and adds an observer
	 */
	public void initializeView();

	/**
	 * Returns only instance of the view (singleton)
	 * 
	 * @return View-Instance
	 */
	public View getView();

	/**
	 * Returns the input model. If no model is set, a new one is created
	 * 
	 * @return BPMN-Model
	 */
	public BpmnModel getBpmnModel();

	/**
	 * Returns the transformed model.
	 * 
	 * @return AristaFlow-Model
	 */
	public AristaFlowModel getAristaFlowModel();

	/**
	 * Sets the given file as input, parses and validates it
	 * 
	 * @param inputFile
	 *            file to set as input
	 */
	public void setInputFile(File inputFile);

	/**
	 * Sets the given file as output, sets the log file to outputFile.log
	 * 
	 * @param outputFile
	 *            file to set as output
	 */
	public void setOutputFile(File outputFile);

	/**
	 * Transforms the model which was set as input and writes it to the file set as output.
	 */
	public void transformModel();

	/**
	 * Logs the given message with a simple logger, notifies the view (observer) and prints to stout.
	 * Indentation and line breaks are added as specified.
	 * 
	 * @param message
	 *            message to log
	 * @param indent
	 *            level of indentation, from 0 on
	 * @param lineBreaks
	 *            insert given number of line breaks after message
	 */
	public void log(String message, int indent, int lineBreaks);
}
