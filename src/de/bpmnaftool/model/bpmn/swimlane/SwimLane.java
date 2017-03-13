package de.bpmnaftool.model.bpmn.swimlane;

/**
 * A SwimLane is either a Pool or a Lane used to separate FlowObjects from another.
 * 
 * @author Felix Härer
 */
public interface SwimLane {

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
	
}
