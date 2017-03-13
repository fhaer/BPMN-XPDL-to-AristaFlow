package de.bpmnaftool.model.bpmn.swimlane;

/**
 * Implementing base class for SwimLanes. This class can not be instantiated,
 * use the sub-types instead.
 * 
 * @author Felix Härer
 */
public class SwimLaneImpl implements SwimLane, Comparable<SwimLane> {

	/**
	 * ID of this Swimlane
	 */
	protected String id;
	/**
	 * name of this Swimlane
	 */
	protected String name;
	/**
	 * Protected constructor to prevent initialization from outside the package.
	 */
	protected SwimLaneImpl() {
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
	public int compareTo(SwimLane o) {
		return o.getId().compareTo(getId());
	}
}
