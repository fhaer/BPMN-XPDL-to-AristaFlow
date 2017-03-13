package de.bpmnaftool.model.aristaflow.activity;

import java.util.UUID;

import de.bpmnaftool.model.aristaflow.data.DataEdge;


/**
 * Implementation of an ActivityTemplate
 * 
 * @author Felix Härer
 */
public class ActivityTemplateImpl implements ActivityTemplate {

	/**
	 * ID of this activity
	 */
	UUID id;
	/**
	 * Output parameters (DataEdges)
	 */
	DataEdge[] inputParams;
	/**
	 * Input parameters (DataEdges)
	 */
	DataEdge[] outputParams;

	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String ecName;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String guiContextID;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String implementationClass;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	boolean isColsable;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	boolean isResettable;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	boolean isSingleton;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	boolean isSuspensible;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String name;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String operationName;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String parameterChangePolicy;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	boolean supportestTestExecution;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	boolean supportsViewOnly;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String description;

	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String inParamAttribute;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String inParamAttributeValue;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String outParamAttribute;
	/**
	 * see getter method in ActivityTemplate @see ActivityTemplate
	 */
	String outParamAttributeValue;

	/**
	 * Creates an activity with the specified input and output parameters
	 * 
	 * @param inputParams
	 *            data edges from all input data elements
	 * @param outputParams
	 *            data edges to all output data elements
	 */
	public ActivityTemplateImpl(DataEdge[] inputParams, DataEdge[] outputParams) {
		id = UUID.randomUUID();
		this.inputParams = inputParams;
		this.outputParams = outputParams;
	}

	@Override
	public String getActivityId() {
		return id.toString();
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getEcName() {
		return ecName;
	}

	@Override
	public String getGuiContextID() {
		return guiContextID;
	}

	@Override
	public String getImplementationClass() {
		return implementationClass;
	}

	@Override
	public int getInParamConnectorId(int n) {
		return inputParams[n].getConnectorId();
	}

	@Override
	public String getInParamDescription(int n) {
		return "";
	}

	@Override
	public String getInParamIdentifierID(int n) {
		return defaultParamIdentifier;
	}

	@Override
	public boolean getInParamIsOptional(int n) {
		return false;
	}

	@Override
	public String getInParamName(int n) {
		return inputParams[n].getDataElement().getName();
	}

	@Override
	public String getInParamType(int n) {
		return inputParams[n].getDataElement().getType().toString();
	}

	@Override
	public boolean getIsClosable() {
		return isColsable;
	}

	@Override
	public boolean getIsResettable() {
		return isResettable;
	}

	@Override
	public boolean getIsSingleton() {
		return isSingleton;
	}

	@Override
	public boolean getIsSuspensible() {
		return isSuspensible;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getNumberOfInputParameters() {
		return inputParams.length;
	}

	@Override
	public int getNumberOfOutputParameters() {
		return outputParams.length;
	}

	@Override
	public String getOperationName() {
		return operationName;
	}

	@Override
	public int getOutParamConnectorId(int n) {
		return outputParams[n].getConnectorId();
	}

	@Override
	public String getParameterChangePolicy() {
		return parameterChangePolicy;
	}

	@Override
	public boolean getSupportsTestExecution() {
		return supportestTestExecution;
	}

	@Override
	public boolean getSupportsViewOnly() {
		return supportsViewOnly;
	}

	@Override
	public String getOutParamDescription(int n) {
		return outputParams[n].getDataElement().getDescription();
	}

	@Override
	public String getOutParamIdentifierID(int n) {
		return defaultParamIdentifier;
	}

	@Override
	public boolean getOutParamIsOptional(int n) {
		return false;
	}

	@Override
	public String getOutParamName(int n) {
		return outputParams[n].getDataElement().getName();
	}

	@Override
	public String getOutParamType(int n) {
		return outputParams[n].getDataElement().getType().toString();
	}

	@Override
	public String getInParamAttribute(int n) {
		return inParamAttribute;
	}

	@Override
	public String getInParamAttributeValue(int n) {
		return inParamAttributeValue;
	}

	@Override
	public String getOutParamAttribute(int n) {
		return outParamAttribute;
	}

	@Override
	public String getOutParamAttributeValue(int n) {
		return outParamAttributeValue;
	}

}
