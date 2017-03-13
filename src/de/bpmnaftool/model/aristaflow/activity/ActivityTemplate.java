package de.bpmnaftool.model.aristaflow.activity;

/**
 * Represents an abstract activity as it can not be instantiated. It defines attributes which are needed
 * for all activities, regardless of their type.
 * 
 * @author Felix Härer
 */
public interface ActivityTemplate {

	/**
	 * ID for the default Identifier for parameters of an activity
	 */
	final String defaultParamIdentifier = "ffffffff-ffff-ffff-8000-000000000000";

	/**
	 * returns the ID of an activity
	 * 
	 * @return ID
	 */
	String getActivityId();

	/**
	 * name of the activity
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * name of class e.g. "de.aristaflow.rules.XOR"
	 * 
	 * @return ecName
	 */
	String getEcName();

	/**
	 * name for operation, usually equal to getName()
	 * 
	 * @return operation name
	 */
	String getOperationName();

	/**
	 * description for the activity
	 * 
	 * @return description
	 */
	String getDescription();

	/**
	 * class name, where the activity is implemented
	 * 
	 * @return implementation class
	 */
	String getImplementationClass();

	/**
	 * if true, only one instance of the activity at a time is possible
	 * 
	 * @return isSingleton
	 */
	boolean getIsSingleton();

	/**
	 * ID for GUI Context, e.g. NullContext
	 * 
	 * @return GuiContextID
	 */
	String getGuiContextID();

	/**
	 * If true, an activity can be suspended (paused) and resumed later
	 * 
	 * @return isSuspensible
	 */
	boolean getIsSuspensible();

	/**
	 * If true, Activity can be reseted
	 * 
	 * @return isResettable
	 */
	boolean getIsResettable();

	/**
	 * If true, Activity can be closed
	 * 
	 * @return isClosable
	 */
	boolean getIsClosable();

	/**
	 * If true, no parameters can be modified after creation
	 * 
	 * @return supportsViewOnly
	 */
	boolean getSupportsViewOnly();

	/**
	 * If true, execution in test client is supported
	 * 
	 * @return supportsTestExecution
	 */
	boolean getSupportsTestExecution();

	/**
	 * Defines whether parameters can be changed after creation
	 * 
	 * @return change policy
	 */
	String getParameterChangePolicy();

	/**
	 * Returns the number of input parameters for this activity
	 * 
	 * @return number of input parameters
	 */
	int getNumberOfInputParameters();

	/**
	 * Returns the number of output parameters for this activity
	 * 
	 * @return number of output parameters
	 */
	int getNumberOfOutputParameters();

	/**
	 * Returns the ID of the data edge for incoming parameter n
	 * 
	 * @param n
	 *            parameter number
	 * @return ID of data edge
	 */
	int getInParamConnectorId(int n);

	/**
	 * Returns the ID of the data edge for outgoing parameter n
	 * 
	 * @param n
	 *            paramter number
	 * @return ID of data edge
	 */
	int getOutParamConnectorId(int n);

	/**
	 * return parameter name of input parameter n
	 * 
	 * @param n
	 *            parameter number
	 * @return name
	 */
	String getInParamName(int n);

	/**
	 * return parameter name of output parameter n
	 * 
	 * @param n
	 *            parameter number
	 * @return name
	 */
	String getInParamType(int n);

	/**
	 * returns description of input parameter n
	 * 
	 * @param n
	 *            parameter number
	 * @return description
	 */
	String getInParamDescription(int n);

	/**
	 * ID of input parameter n
	 * 
	 * @param n
	 *            parameter number
	 * @return ID
	 */
	String getInParamIdentifierID(int n);

	/**
	 * If true, input parameter n is optional
	 * 
	 * @param n
	 *            parameter number
	 * @return isOptional
	 */
	boolean getInParamIsOptional(int n);

	/**
	 * returns name of output parameter n
	 * 
	 * @param n
	 *            parameter number
	 * @return name
	 */
	String getOutParamName(int n);

	/**
	 * returns type of output parameter n
	 * 
	 * @param n
	 *            parameter number
	 * @return type
	 */
	String getOutParamType(int n);

	/**
	 * returns description of output parameter n
	 * 
	 * @param n
	 *            parameter number
	 * @return description
	 */
	String getOutParamDescription(int n);

	/**
	 * returns ID of output parameter n
	 * 
	 * @param n
	 *            parameter number
	 * @return ID
	 */
	String getOutParamIdentifierID(int n);

	/**
	 * If true, input parameter n is optional
	 * 
	 * @param n
	 *            parameter number
	 * @return isOptional
	 */
	boolean getOutParamIsOptional(int n);

	/**
	 * returns input parameter attribute n
	 * 
	 * @param n
	 *            parameter number
	 * @return attribute for n
	 */
	String getInParamAttribute(int n);

	/**
	 * returns input parameter attribute value for n
	 * 
	 * @param n
	 *            parameter number
	 * @return attribute value for n
	 */
	String getInParamAttributeValue(int n);

	/**
	 * returns output parameter attribute for n
	 * 
	 * @param n
	 *            parameter number
	 * @return attribute for n
	 */
	String getOutParamAttribute(int n);

	/**
	 * returns output parameter attribute value for n
	 * 
	 * @param n
	 *            parameter number
	 * @return attribute value for n
	 */
	String getOutParamAttributeValue(int n);
}
