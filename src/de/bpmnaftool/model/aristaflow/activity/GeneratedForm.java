package de.bpmnaftool.model.aristaflow.activity;

import de.bpmnaftool.model.aristaflow.data.DataEdge;

/**
 * Implementation of Generated Form Activity. This activity is used to automatically generate forms for
 * data input and output. The created form contains a field for each input and output parameter.
 * 
 * @author Felix Härer
 */
public class GeneratedForm extends ActivityTemplateImpl {

	/**
	 * Creates an activity with the specified input and output parameters
	 * 
	 * @param inputParams
	 *            data edges from all input data elements
	 * @param outputParams
	 *            data edges to all output data elements
	 */
	public GeneratedForm(DataEdge[] inputParams, DataEdge[] outputParams) {
		super(inputParams, outputParams);

		this.name = "Generated Form";
		this.operationName = "Generated Form";
		this.description = "";
		this.ecName = "de.aristaflow.form.GeneratedForm";
		this.implementationClass = "de.aristaflow.adept2.extensions.generatedforms2.FormToolkitDataBindingEnvironment";
		this.guiContextID = "SWTContext";
		this.isSingleton = false;
		this.isColsable = true;
		this.isResettable = true;
		this.isSuspensible = true;
		this.supportestTestExecution = false;
		this.supportsViewOnly = true;
		this.parameterChangePolicy = "GENERIC";
		this.inParamAttribute = "fromParameterTemplate";
		this.inParamAttributeValue = "Default";
		this.outParamAttribute = "fromParameterTemplate";
		this.outParamAttributeValue = "Default";
	}
}
