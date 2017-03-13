package de.bpmnaftool.model.bpmn.flowobject;

/**
 * An Event is a FlowObject which can be triggered for various reasons (see trigger attribute). Every
 * pool starts and ends with an event, but events can also occur in between (see type attribute).
 * 
 * @author Felix Härer
 */
public interface Event extends FlowObject {

	/**
	 * Type of the event. A StartEvent is the event of a pool, an EndEvent is the last. Events in between
	 * are called IntermediateEvents.
	 * 
	 * @author Felix Härer
	 */
	public static enum Type {
		/**
		 * first event of a pool
		 */
		StartEvent,
		/**
		 * event between StartEvent and EndEvent
		 */
		IntermediateEvent,
		/**
		 * last event of a pool
		 */
		EndEvent,
		/**
		 * type not known
		 */
		Unknown
	}

	/**
	 * Returns the type of this event. A StartEvent is the event of a pool, an EndEvent is the last.
	 * Events in between are called IntermediateEvents.
	 * 
	 * @return type of this event
	 */
	public Type getType();

	/**
	 * Defines how the event is triggered. E.g. through a Message or Timer.
	 * 
	 * @author Felix Härer
	 */
	public static enum Trigger {
		/**
		 * event triggered by a message
		 */
		Message,
		/**
		 * event triggered by a timer
		 */
		Timer,
		/**
		 * event triggered by a rule
		 */
		Rule,
		/**
		 * links the trigger to another event
		 */
		Link,
		/**
		 * trigger not known
		 */
		Unknown
	}

	/**
	 * Returns how the event is triggered, e.g. through a Message or a Timer.
	 * 
	 * @return trigger of this event
	 */
	public Trigger getTrigger();

	/**
	 * Returns if an event is throwing. E.g. a message event is throwing if it sends messages and
	 * catching if it receives messages. Null if undefined.
	 * 
	 * @return true, if the event is throwing, false if the event is catching, null if undefined.
	 */
	public Boolean isThrowingEvent();

	/**
	 * Sets this event to a throwing event. E.g. a message event is throwing if it sends messages and
	 * catching if it receives messages.
	 */
	public void setThrowingEvent();

	/**
	 * Sets this event to a catching event. E.g. a message event is throwing if it sends messages and
	 * catching if it receives messages.
	 */
	public void setCatchingEvent();

}
