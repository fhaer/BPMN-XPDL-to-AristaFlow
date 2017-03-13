package de.bpmnaftool.model.aristaflow.fileio;

import java.io.File;
import java.io.IOException;

import de.bpmnaftool.model.aristaflow.AristaFlowModel;

/**
 * This interface provides methods to parse XPDL 2.1 files
 * 
 * @author Felix Härer
 */
public interface AristaFlowXmlWriter {

	/**
	 * Sets a new file and checks its existence
	 * 
	 * @param file
	 *            where to write the XML
	 * @throws IOException
	 */
	void setFile(File file) throws IOException;

	/**
	 * Returns the file name
	 * 
	 * @return name of file
	 */
	String getFile();

	/**
	 * Writes the given AristaFlowModel to the file which is set using setFile(...)
	 * 
	 * @param aristaFlowModel
	 *            model to write to file
	 */
	void write(AristaFlowModel aristaFlowModel);
}
