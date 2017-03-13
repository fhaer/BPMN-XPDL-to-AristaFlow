package de.bpmnaftool.model.bpmn.flowobject;

import de.bpmnaftool.model.bpmn.swimlane.Lane;

/**
 * Implementing class for an Event.
 * 
 * @author Felix Härer
 */
public class EventImpl extends FlowObjectImpl implements Event {

	/**
	 * trigger of this Event
	 * 
	 * @see Event
	 */
	final protected Trigger trigger;

	/**
	 * type of this Event
	 * 
	 * @see Event
	 */
	final protected Type type;

	/**
	 * true if throwing, false if catching
	 * 
	 * @see Event
	 */
	protected Boolean isThrowing;

	/**
	 * To create an Event, the lane, type is required and trigger are required.
	 * 
	 * @see Event
	 * 
	 * @param lane
	 *            lane where gateway is located
	 * @param type
	 *            type of event (see Event interface)
	 * @param trigger
	 *            trigger of event (see Event interface)
	 */
	public EventImpl(Lane lane, Type type, Trigger trigger) {
		super(lane);
		this.type = type;
		this.trigger = trigger;
	}

	@Override
	public Trigger getTrigger() {
		return trigger;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Boolean isThrowingEvent() {
		return isThrowing;
	}

	@Override
	public void setCatchingEvent() {
		isThrowing = false;
	}

	@Override
	public void setThrowingEvent() {
		isThrowing = true;
	}
}
