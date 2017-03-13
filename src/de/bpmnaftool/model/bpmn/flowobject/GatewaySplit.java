package de.bpmnaftool.model.bpmn.flowobject;

import de.bpmnaftool.model.bpmn.swimlane.Lane;

/**
 * A GatewaySplit is Gateway which is allowed to have more than one outgoing
 * sequence flows.
 * 
 * @author Felix Härer
 */
public class GatewaySplit extends GatewayImpl implements Gateway {

	/**
	 * To create a Gateway, the lane and type is required.
	 * 
	 * @see Gateway
	 * 
	 * @param lane
	 *            lane where gatway is located
	 * @param type
	 *            type of gateway (see Gateway interface)
	 */
	public GatewaySplit(Lane lane, Type type) {
		super(lane, type);
	}

}
