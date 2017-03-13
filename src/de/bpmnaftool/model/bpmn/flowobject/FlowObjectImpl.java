package de.bpmnaftool.model.bpmn.flowobject;

import de.bpmnaftool.model.bpmn.swimlane.Lane;

/**
 * Implementing base class for flow objects
 * 
 * @author Felix Härer
 */
public class FlowObjectImpl implements FlowObject, Comparable<FlowObject> {

	/**
	 * Unique identifier for this object, may be any String, but not an empty
	 * string.
	 */
	protected String id;

	/**
	 * Displayed name for this object, may be any String, but not an empty
	 * string.
	 */
	protected String name;
	
	/**
	 * Lane where this object is located.
	 */
	protected Lane lane;

	/**
	 * FlowObjects can only be created from inside the package, use sub-types to
	 * create FlowObjects.
	 * 
	 * @param lane
	 *            lane where the FlowObject is located
	 */
	protected FlowObjectImpl(Lane lane) {
		if (lane == null) {
			throw new IllegalArgumentException("Lane nicht gefunden (null)");
		}
		this.lane = lane;
	}

	@Override
	public String getId() {
		if (id == null)
			return "";
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Lane getLane() {
		if (lane == null)
			throw new IllegalStateException("lane unknown");
		return lane;
	}

	@Override
	public String getName() {
		if (name == null)
			return "";
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName() + " (ID " + getId() + ")";
	}

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (object instanceof FlowObject) {
			if (id != null && ((FlowObject) object).getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(FlowObject o) {
		return o.getId().compareTo(getId());
	}
	
}
