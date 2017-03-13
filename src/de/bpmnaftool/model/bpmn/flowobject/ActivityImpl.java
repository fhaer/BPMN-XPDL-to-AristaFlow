package de.bpmnaftool.model.bpmn.flowobject;

import de.bpmnaftool.model.bpmn.swimlane.Lane;

/**
 * Implementing class for an Activity.
 * 
 * @author Felix Härer
 */
public class ActivityImpl extends FlowObjectImpl implements Activity {

	/**
	 * To create an Activity, the lane which it is located in is required.
	 * 
	 * @param lane
	 *            lane, where this activity is located in
	 */
	public ActivityImpl(Lane lane) {
		super(lane);
	}

}
