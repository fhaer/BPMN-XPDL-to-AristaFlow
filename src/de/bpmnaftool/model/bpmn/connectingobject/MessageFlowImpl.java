package de.bpmnaftool.model.bpmn.connectingobject;

import de.bpmnaftool.model.bpmn.flowobject.FlowObject;
import de.bpmnaftool.model.bpmn.swimlane.Pool;

/**
 * Implementation of MessageFlow interface.
 * 
 * @author Felix Härer
 */
public class MessageFlowImpl extends ConnectingObjectImpl implements
		MessageFlow {

	/**
	 * source of message flow, where the flow object is outgoing
	 */
	public final Pool sourcePool;

	/**
	 * destination of message flow, where the flow object is incoming
	 */
	public final Pool destinationPool;

	/**
	 * source of message flow, where the flow object is outgoing
	 */
	public final FlowObject sourceFlowObject;

	/**
	 * destination of message flow, where the flow object is incoming
	 */
	public final FlowObject destinationFlowObject;

	/**
	 * Creates a MessageFlow between Pools or FlowObjects using source and
	 * destination.
	 * 
	 * @param source
	 *            source pool where message flow is outgoing
	 * @param destination
	 *            destination pool where the message flow is incoming
	 */
	public MessageFlowImpl(Pool source, Pool destination) {
		sourcePool = source;
		destinationPool = destination;
		sourceFlowObject = null;
		destinationFlowObject = null;
		assertDifferentPool(source, destination);
	}

	/**
	 * Creates a MessageFlow between Pools or FlowObjects using source and
	 * destination.
	 * 
	 * @param source
	 *            source pool where message flow is outgoing
	 * @param destination
	 *            destination FlowObject where the message flow is incoming
	 */
	public MessageFlowImpl(Pool source, FlowObject destination) {
		sourcePool = source;
		destinationPool = null;
		sourceFlowObject = null;
		destinationFlowObject = destination;
		assertDifferentPool(source, destination.getLane().getPool());
	}

	/**
	 * Creates a MessageFlow between Pools or FlowObjects using source and
	 * destination.
	 * 
	 * @param source
	 *            source FlowObject where message flow is outgoing
	 * @param destination
	 *            destination pool where the message flow is incoming
	 */
	public MessageFlowImpl(FlowObject source, Pool destination) {
		sourcePool = null;
		destinationPool = destination;
		sourceFlowObject = source;
		destinationFlowObject = null;
		assertDifferentPool(source.getLane().getPool(), destination);
	}

	/**
	 * Creates a MessageFlow between Pools or FlowObjects using source and
	 * destination.
	 * 
	 * @param source
	 *            source FlowObject where message flow is outgoing
	 * @param destination
	 *            destination FlowObject where the message flow is incoming
	 */
	public MessageFlowImpl(FlowObject source, FlowObject destination) {
		sourcePool = null;
		destinationPool = null;
		sourceFlowObject = source;
		destinationFlowObject = destination;
		assertDifferentPool(source.getLane().getPool(), destination.getLane()
				.getPool());
	}

	/**
	 * Throws an exception if poolA and poolB are equal
	 * 
	 * @param poolA
	 * @param poolB
	 */
	public void assertDifferentPool(Pool poolA, Pool poolB) {
		if (poolA.equals(poolB))
			throw new IllegalArgumentException(
					"a MessageFlow can only connect different Pools or FlowObjects " +
					"in different Pools");
	}

	@Override
	public Pool getSourcePool() {
		return sourcePool;
	}

	@Override
	public Pool getDestinationPool() {
		return destinationPool;
	}

	@Override
	public FlowObject getSourceFlowObject() {
		return sourceFlowObject;
	}

	@Override
	public FlowObject getDestinationFlowObject() {
		return destinationFlowObject;
	}

	@Override
	public boolean sourceIsPool() {
		return (sourcePool != null);
	}

	@Override
	public boolean destinationIsPool() {
		return (destinationPool != null);
	}

	@Override
	public String toString() {
		String source = "";
		String destination = "";
		if (sourceIsPool())
			source = "Pool " + getSourcePool();
		else
			source = "FlowObject " + getSourceFlowObject();
		if (destinationIsPool())
			destination = "Pool " + getDestinationPool();
		else
			destination = "FlowObject " + getDestinationFlowObject();
		return "MessageFlow " + getId() + ": [" + source + "] -> [" + destination + "]";
	}
}
