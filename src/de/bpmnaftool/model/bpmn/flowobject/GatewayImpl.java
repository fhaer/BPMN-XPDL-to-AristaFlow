package de.bpmnaftool.model.bpmn.flowobject;

import de.bpmnaftool.model.bpmn.swimlane.Lane;

/**
 * Implementing base class for Gateway.
 * 
 * @author Felix Härer
 */
public class GatewayImpl extends FlowObjectImpl implements Gateway {

	/**
	 * type of this Gateway
	 * @see Gateway 
	 */
	final protected Type type;
	
	/**
	 * To create a Gateway, the lane and type is reqired. A Gateway must be created using
	 * the sub-types, so this constructor is protected.
	 * @see Gateway
	 * 
	 * @param lane lane where gatway is located
	 * @param type type of gateway (see Gateway interface)
	 */
	protected GatewayImpl(Lane lane, Type type) {
		super(lane);
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}

}
