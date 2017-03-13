package de.bpmnaftool.model.aristaflow.graph.node;

import de.bpmnaftool.model.aristaflow.activity.ActivityTemplate;

/**
 * A normal node is a typical workflow activity, however this is just a node in a graph - it may or may
 * not have an activity assigned. This node may have an activity assigned.
 * 
 * @author Felix Härer
 */
public class NormalNode extends NodeImpl {

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
	public static final String nodeType = "NT_NORMAL";

	/**
	 * creates new normal node with default staff assignment
	 */
	public NormalNode() {
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
	 * Assigns an activity to this node.
	 * 
	 * @param activity
	 *            to be assigned
	 */
	public void setActivity(ActivityTemplate activity) {
		this.activity = activity;
	}
}
