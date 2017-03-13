package de.bpmnaftool.model.transformation;

import de.bpmnaftool.model.aristaflow.AristaFlowModel;
import de.bpmnaftool.model.bpmn.BpmnModel;

/**
 * This interface controls the transformation from BPMN to AristaFlow. It calls
 * methods to perform the model transformation where new objects in the
 * AristaFlow model are created and assigned to existing objects in the BPMN
 * model.
 * 
 * @author Felix Härer
 */
public interface BpmnTransformation {

	/**
	 * Prefix for the name of transformed events of type "catching"
	 */
	final String catchingEventPrefix = "Warte auf Ereignis: ";
	
	/**
	 * Prefix for the name of transformed events of type "throwing" 
	 */
	final String throwingEventPrefix = "Löse Ereignis aus: ";

	/**
	 * Returns the source of the model transformation
	 * 
	 * @return BPMN model
	 */
	BpmnModel getSourceModel();

	/**
	 * Returns the result of the model transformation. If destination model does
	 * not exist, a transformation is performed first.
	 * 
	 * @return AristaFlow model
	 */
	AristaFlowModel getDestinationModel();

	/**
	 * Performs model transformation
	 */
	void transform();

}
