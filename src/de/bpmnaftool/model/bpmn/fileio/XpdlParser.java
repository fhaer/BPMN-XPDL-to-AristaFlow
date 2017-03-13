package de.bpmnaftool.model.bpmn.fileio;

import java.io.File;

import de.bpmnaftool.model.bpmn.BpmnModel;

/**
 * This interface provides methods to parse XPDL 2.1 files
 * 
 * @author Felix Härer
 */
public interface XpdlParser {

	/**
	 * Namespace for XPDL files. This namespace needs to be set using the xmlns attribute in the root
	 * element. E.g. for the root element "Package": <Package xmlns="http://www.wfmc.org/someNamespace">
	 */
	final String xpdlNamespace = "http://www.wfmc.org/2008/XPDL2.1";

	/**
	 * W3C XML Schema language of the XPDL-Schema
	 */
	final String schemaLanguage = "http://www.w3.org/2001/XMLSchema";

	/**
	 * XPDL-Schema 2.1 for validation
	 */
	final String xpdlSchema = "http://www.wfmc.org/standards/docs/bpmnxpdl_31.xsd";

	/**
	 * XPDL-Schema 2.1 for validation (local file)
	 */
	final String xpdlSchemaLocal = "/de/bpmnaftool/resources/xmlvalidation/bpmnxpdl_31.xsd";

	/**
	 * Sets a new file and checks its existence
	 * 
	 * @param file
	 *            file
	 */
	void setFile(File file);

	/**
	 * Returns the file name
	 * 
	 * @return file name as string
	 */
	String getFile();

	/**
	 * Parses the file which was set using the setFile method. Also checks if input is well formed and
	 * valid.
	 * 
	 * @return parsed BPMN model
	 * @throws Exception
	 *             various exceptions from the parsing process (e.g. if XML not well fomred)
	 */
	BpmnModel parse() throws Exception;

	/**
	 * If XML is well formed
	 * 
	 * @return true if well formed, false otherwise
	 */
	boolean isXmlWellFormed();

	/**
	 * If XPDL is valid for XPDL-Schema 2.1
	 * 
	 * @return true if valid, false otherwise
	 */
	boolean isXpdlValid();
}
