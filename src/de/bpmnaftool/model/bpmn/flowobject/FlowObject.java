package de.bpmnaftool.model.bpmn.flowobject;

import de.bpmnaftool.model.bpmn.swimlane.Lane;

/**
 * Representation of a Flow Object which is a Activity, Event or Gateway.
 * 
 * @author Felix Härer
 */
public interface FlowObject {

	/**
	 * Returns the ID of this flow object. If no ID is set, an empty
	 * string is returned.
	 * 
	 * @return ID as String
	 */
	public String getId();

	/**
	 * Sets the ID of this flow object. The ID may be any non-empty
	 * String.
	 * 
	 * @param id
	 *            ID to set
	 */
	void setId(String id);

	/**
	 * Returns the name of this flow object. If no name is set, an empty
	 * string is returned.
	 * 
	 * @return name as String
	 */
	public String getName();

	/**
	 * Sets the name of this flow object. The name may be any non-empty
	 * String.
	 * 
	 * @param name
	 *            name to set
	 */
	void setName(String name);
	
	/**
	 * Returns the lane, the object is located in.
	 * 
	 * @return lane of this object
	 */
	public Lane getLane();
}
