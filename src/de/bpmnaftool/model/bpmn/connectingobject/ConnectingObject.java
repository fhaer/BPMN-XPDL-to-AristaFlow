package de.bpmnaftool.model.bpmn.connectingobject;

/**
 * Representation of a Connecting Object which is either a MessageFlow or a
 * SequenceFlow
 * 
 * @author Felix Härer
 */
public interface ConnectingObject {

	/**
	 * Returns the ID of this connecting object. If no ID is set, an empty
	 * string is returned.
	 * 
	 * @return ID as String
	 */
	public String getId();

	/**
	 * Sets the ID of this connecting object. The ID may be any non-empty
	 * String.
	 * 
	 * @param id
	 *            ID to set
	 */
	void setId(String id);

}
