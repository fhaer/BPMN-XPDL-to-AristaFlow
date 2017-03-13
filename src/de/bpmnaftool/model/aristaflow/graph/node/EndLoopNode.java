package de.bpmnaftool.model.aristaflow.graph.node;

import de.bpmnaftool.model.aristaflow.activity.ActivityTemplate;
import de.bpmnaftool.model.aristaflow.activity.XorActivity;

/**
 *This node may have an XOR Activity assigned which contains a condition for the do-until loop.
 * 
 * @author Felix Härer
 */
public class EndLoopNode extends NodeImpl {

	/**
	 * Some nodes may have an activity assigned, this depends on the sub-type. It can only be accessed
	 * through get/set methods to prevent setting activities when it is not allowed (e.g. AndSplitNode
	 * can not have an activity assigned).
	 */
	protected ActivityTemplate activity;

	/**
	 * String to describe the type of node. This should not be used for checking the type of an edge, the
	 * keyword instanceof can be used instead.
	 */
	public static final String nodeType = "NT_ENDLOOP";

	/**
	 * Constructor, sets node type
	 */
	public EndLoopNode() {
		super(nodeType);
		setStaffAssignment(defaultStaffAssignment);
	}

	/**
	 * Retrieves the assigned activity.
	 * 
	 * @return assigned activity OR null if no activity is assigned
	 */
	public ActivityTemplate getActivity() {
		return activity;
	}

	/**
	 * Assigns an activity to this node. Only XorActicity is allowed.
	 * 
	 * @param activity
	 *            an XorActivity to be assigned
	 */
	public void setActivity(ActivityTemplate activity) {
		if (activity instanceof XorActivity)
			this.activity = activity;
		else
			throw new IllegalArgumentException("an LoopStartNode can only have a XorActivity assigned");
	}

}
