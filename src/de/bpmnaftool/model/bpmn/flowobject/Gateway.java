package de.bpmnaftool.model.bpmn.flowobject;

/**
 * A Gateway is a FlowObject which is used to specify the outgoing SequeceFlow,
 * therefore it may have more than one outgoing SequenceFlow. Which outgoing
 * SequenceFlows are selected, depends on the type of the Gateway and on the
 * condition of the SequenceFlows - a gateway itself does not have a condition.
 * 
 * @author Felix Härer
 */
public interface Gateway extends FlowObject {

	/**
	 * Defines the split behavior of this Gateway. For an exclusive gateways,
	 * only one outgoing sequence flow is used (XOR). For an inclusive gateway,
	 * all are used which have a condition that is true (OR). For a parallel
	 * gateway, all outgoing sequence flows are executed in parallel with no
	 * conditions (AND). A Complex gateway defines a custom rule to select
	 * outgoing sequence flows.
	 * 
	 * @author Felix Härer
	 */
	public static enum Type {
		/**
		 * exclusive gateway (semantics: xor) 
		 */
		Exclusive, 
		/**
		 * inclusive gateway (semantics: or)
		 */
		Inclusive, 
		/**
		 * parallel gateway (semantics: and)
		 */
		Parallel, 
		/**
		 * complex gateway
		 */
		Complex,
		/**
		 * gateway type not known
		 */
		Unknown
	}

	/**
	 * Returns the type of an Event. It defines the split behavior of this
	 * Gateway. For an exclusive gateways, only one outgoing sequence flow is
	 * used (XOR). For an inclusive gateway, all are used which have a condition
	 * that is true (OR). For a parallel gateway, all outgoing sequence flows
	 * are executed in parallel with no conditions (AND). A Complex gateway
	 * defines a custom rule to select outgoing sequence flows.
	 * 
	 * @return type of this gateway
	 */
	public Type getType();

}
