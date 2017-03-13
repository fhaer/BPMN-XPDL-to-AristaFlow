package de.bpmnaftool.model.bpmn.connectingobject;

import de.bpmnaftool.model.bpmn.flowobject.FlowObject;
import de.bpmnaftool.model.bpmn.swimlane.Pool;

/**
 * A MessageFlow is a directed edge between Pools or FlowObjects in different
 * pools.
 * 
 * @author Felix Härer
 */
public interface MessageFlow extends ConnectingObject {

	/**
	 * Returns whether the source is a pool or a FlowObject.
	 * 
	 * @return true if source is a pool, false otherwise.
	 */
	public boolean sourceIsPool();

	/**
	 * Returns, whether the destination is a pool or a FlowObject.
	 * 
	 * @return true if destination is a pool, false otherwise.
	 */
	public boolean destinationIsPool();

	/**
	 * Returns the source, where the object is outgoing
	 * 
	 * @return source Pool or null if source is not a pool
	 */
	public Pool getSourcePool();

	/**
	 * Returns the destination, where the object is incoming
	 * 
	 * @return destination Pool or null if destination is not a pool
	 */
	public Pool getDestinationPool();

	/**
	 * Returns the source, where the object is outgoing
	 * 
	 * @return source FlowObject or null if source is not a FlowObject.
	 */
	public FlowObject getSourceFlowObject();

	/**
	 * Returns the destination, where the object is incoming
	 * 
	 * @return destination FlowObject or null if destination is not a FlowObject
	 */
	public FlowObject getDestinationFlowObject();
}
