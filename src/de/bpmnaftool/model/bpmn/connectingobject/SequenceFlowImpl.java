package de.bpmnaftool.model.bpmn.connectingobject;

import de.bpmnaftool.model.bpmn.flowobject.FlowObject;
import de.bpmnaftool.model.bpmn.swimlane.Pool;

/**
 * Implementation of SequenceFlow interface.
 * 
 * @author Felix Härer
 */
public class SequenceFlowImpl extends ConnectingObjectImpl implements SequenceFlow {

	/**
	 * source of sequence flow, where the flow object is outgoing
	 */
	public final FlowObject source;

	/**
	 * destination of sequence flow, where the flow object is incoming
	 */
	public final FlowObject destination;

	/**
	 * Condition which must be true to execute the sequence flow.
	 */
	protected String condition;

	protected boolean isDefaultFlow;

	/**
	 * Creates a sequence flow without an ID. The ID can be set using the setId method.
	 * 
	 * @param source
	 *            source flow object, where the sequence flow is outgoing
	 * @param destination
	 *            destination flow object, where the sequence flow is incoming
	 */
	public SequenceFlowImpl(FlowObject source, FlowObject destination) {
		Pool sourcePool = source.getLane().getPool();
		Pool destinationPool = destination.getLane().getPool();
		if (!sourcePool.equals(destinationPool))
			throw new IllegalArgumentException("source and destination must be in the same pool");
		this.source = source;
		this.destination = destination;
		this.isDefaultFlow = false;
	}

	/**
	 * Creates a sequence flow using a given ID.
	 * 
	 * @param source
	 *            source flow object, where the sequence flow is outgoing
	 * @param destination
	 *            destination flow object, where the sequence flow is incoming
	 * @param id
	 *            unique identifier of the sequence flow, can be any string, but not an empty string.
	 */
	public SequenceFlowImpl(FlowObject source, FlowObject destination, String id) {
		super(id);
		this.source = source;
		this.destination = destination;
	}

	@Override
	public String getCondition() {
		return condition;
	}

	@Override
	public void setCondition(String condition) {
		if (condition == null)
			throw new IllegalArgumentException("attempt to set a condition to null");
		this.condition = condition;
	}

	@Override
	public FlowObject getSourceFlowObject() {
		if (source == null)
			throw new IllegalStateException("unknown source");
		return source;
	}

	@Override
	public FlowObject getDestinationFlowObject() {
		if (destination == null)
			throw new IllegalStateException("unknwon destination");
		return destination;
	}

	@Override
	public String toString() {
		return "SequenceFlow " + getId() + ": [" + source + "] -> [" + destination + "]";
	}

	@Override
	public boolean isDefaultFlow() {
		return isDefaultFlow;
	}

	@Override
	public void setDefaultFlow() {
		isDefaultFlow = true;
	}
}
