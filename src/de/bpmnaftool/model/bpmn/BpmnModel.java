package de.bpmnaftool.model.bpmn;

import de.bpmnaftool.model.bpmn.connectingobject.*;
import de.bpmnaftool.model.bpmn.flowobject.*;
import de.bpmnaftool.model.bpmn.swimlane.*;

/**
 * Representation of a BPMN model. It constructs, stores and retrieves all model
 * elements.
 * 
 * @author Felix Härer
 */
public interface BpmnModel {

	/**
	 * returns the model ID
	 * 
	 * @return model ID
	 */
	String getId();

	/**
	 * returns the model name as String
	 * 
	 * @return name of model
	 */
	String getName();

	/**
	 * sets the model name
	 * 
	 * @param name
	 *            name of model
	 */
	void setName(String name);

	/**
	 * Adds a connecting object to the model if and object with 
	 * this ID does not exist.
	 * 
	 * @param connectingObject
	 *            connectingObject to add
	 */
	void addConnectingObject(ConnectingObject connectingObject);

	/**
	 * Adds a flow object to the model if and object with 
	 * this ID does not exist.
	 * 
	 * @param flowObject
	 *            flowObject to add
	 */
	void addFlowObject(FlowObject flowObject);

	/**
	 * Adds a lane or pool to the model if and swimLane with 
	 * this ID does not exist.
	 * 
	 * @param swimLane
	 *            swimLane to add
	 */
	void addSwimLane(SwimLane swimLane);

	/**
	 * Returns all child nodes of type FlowObject of a FlowObject, i.e. the
	 * destination of all SequenceFlows with the given source (flowObject).
	 * 
	 * @param flowObject
	 *            source of the sequence flow where children are the destination
	 * @return children of the given FlowObject
	 */
	FlowObject[] getChildren(FlowObject flowObject);

	/**
	 * Returns all parent nodes of type FlowObject of a FlowObject, i.e. the
	 * source of all SequenceFlows with the given destination (flowObject).
	 * 
	 * @param flowObject
	 *            destination of the sequence flow where parents are the source
	 * @return children of the given FlowObject
	 */
	FlowObject[] getParents(FlowObject flowObject);

	/**
	 * Returns the sequence flow which has the given source and destination
	 * FlowObject.
	 * 
	 * @param source
	 *            source of the sequence flow
	 * @param destination
	 *            destination of the sequence flow
	 * @return sequence flow between source and destination
	 */
	SequenceFlow getSequenceFlow(FlowObject source, FlowObject destination);

	/**
	 * Returns all sequence flows where the given node is source or destination
	 * node
	 * 
	 * @param node
	 *            source or destination of the sequence flows
	 * @param isSourceNode
	 *            if true, node is the source node, if false, node is the
	 *            destination node
	 * @return sequence flows with given source or destination node
	 */
	SequenceFlow[] getSequenceFlows(FlowObject node, boolean isSourceNode);

	/**
	 * Returns all message flows of this model
	 * 
	 * @return array with all message flows
	 */
	MessageFlow[] getMessageFlows();

	/**
	 * Returns all pools of this model
	 * 
	 * @return array of pools
	 */
	Pool[] getPools();

	/**
	 * Returns the pool with the given ID
	 * 
	 * @param poolId
	 *            ID of the pool to return
	 * @return pool with given ID
	 */
	Pool getPool(String poolId);

	/**
	 * Returns all lanes of this model
	 * 
	 * @param pool
	 *            pool where lanes are located
	 * @return array of lanes
	 */
	Lane[] getLanes(Pool pool);

	/**
	 * Returns the lane with the given ID
	 * 
	 * @param id
	 *            ID of the lane to return
	 * @return lane with given ID
	 */
	Lane getLane(String id);

	/**
	 * Returns all gateways which match the given criteria
	 * 
	 * @param pool
	 *            pool where the activities must be located
	 * @return array of gateways
	 */
	Gateway[] getGateways(Pool pool);

	/**
	 * Returns all events which match the given criteria
	 * 
	 * @param pool
	 *            pool where the activities must be located
	 * @return array of events
	 */
	Event[] getEvents(Pool pool);

	/**
	 * Returns all Activities which match the given criteria
	 * 
	 * @param pool
	 *            pool where the activities must be located
	 * @return array of activities
	 */
	Activity[] getActivities(Pool pool);

	/**
	 * Returns a FlowObject with the given ID
	 * 
	 * @param id
	 *            ID of the FlowObject to return
	 * @return FlowObject with the given ID
	 */
	FlowObject getFlowObject(String id);

	/**
	 * Returns all FlowObjects of the given pool
	 * 
	 * @param pool
	 *            Pool where FlowObjects are located
	 * @return FlowObjects
	 */
	FlowObject[] getFlowObjectsByPool(Pool pool);

	/**
	 * Removes a FlowObject from the model. Also searches for SequenceFlows
	 * where the removed FlowObject is source or destionation and removes those
	 * as well.
	 * 
	 * @param id
	 *            ID of the SequenceFlow to remove
	 */
	void removeFlowObject(String id);

	/**
	 * Removes a SwimLane including all Lanes and FlowObjects inside
	 * 
	 * @param id
	 *            ID of SwimLane to remove
	 */
	void removeSwimLane(String id);

	/**
	 * Removes a SequenceFlow from the model
	 * 
	 * @param id
	 *            ID of the SequenceFlow to remove
	 */
	void removeSequenceFlow(String id);

	/**
	 * Removes a MessageFlow from the model
	 * 
	 * @param id
	 *            ID of the MessageFlow to remove
	 */
	void removeMessageFlow(String id);
}
