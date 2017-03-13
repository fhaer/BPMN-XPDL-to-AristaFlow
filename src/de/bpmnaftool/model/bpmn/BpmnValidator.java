package de.bpmnaftool.model.bpmn;

import de.bpmnaftool.model.bpmn.swimlane.Pool;
import de.bpmnaftool.model.transformation.NotTransformableException;

/**
 * This interface provides methods to validate an existing BPMN model for
 * transformation. It can be used to ensure a correct input for the
 * transformation. 
 * 
 * @author Felix Härer
 */
public interface BpmnValidator {

	/**
	 * Validates a model by calling methods to validate SwimLanes, FlowObjects
	 * and ConnectingObjects
	 */
	void validateModel() throws NotTransformableException;

	/**
	 * Validates SwimLanes of the given model. There needs to be at least one
	 * pool which is not a block box pool. Each pool with start/end events (not
	 * black box) needs to have at least one lane.
	 * 
	 */
	void validateSwimLanes() throws NotTransformableException;

	/**
	 * Validates FlowObjects of the given model. Each Pool with FlowObjects (not
	 * black box) needs to have exactly one Event of type StartEvent and one of
	 * type EndEvent. This method also searches for unsupported elements like
	 * Gateways of type Complex. Cardinalities of all FlowObjects are checked as
	 * well, e.g. an activity may have only one incoming and one outgoing
	 * SequenceFlow.
	 * 
	 * @param pool
	 *            pool where objects to validate are located
	 */
	void validateFlowObjects(Pool pool) throws NotTransformableException;

	/**
	 * Validates ConnectingObjects of the given model. All SequenceFlows and
	 * MessageFlows are checked for source and destination.
	 * 
	 * @param pool
	 *            pool where objects to validate are located
	 */
	void validateConnectingObjects(Pool pool) throws NotTransformableException;

}
