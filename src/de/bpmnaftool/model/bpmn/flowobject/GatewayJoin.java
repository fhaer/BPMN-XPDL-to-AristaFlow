package de.bpmnaftool.model.bpmn.flowobject;

import de.bpmnaftool.model.bpmn.swimlane.Lane;

/**
 * A GatewayJoin is a Gateway which is allowed to have more than one incoming
 * sequence flows.
 * 
 * @author Felix Härer
 */
public class GatewayJoin extends GatewayImpl implements Gateway {

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
	public GatewayJoin(Lane lane, Type type) {
		super(lane, type);
	}

}
