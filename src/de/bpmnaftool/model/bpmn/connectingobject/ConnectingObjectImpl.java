package de.bpmnaftool.model.bpmn.connectingobject;

/**
 * Implementing base class for connecting objects. It is not possible to create
 * an instance of this class (protected constructor) - use the subclasses
 * instead.
 * 
 * @author Felix Härer
 */
public class ConnectingObjectImpl implements ConnectingObject {

	/**
	 * unique identifier of the node
	 */
	protected String id;

	/**
	 * Constructor, not used from outside the package. To create a
	 * ConnectingObject, use the public constructor of the specific type (e.g.
	 * SequenceFlow).
	 */
	protected ConnectingObjectImpl() {
	}

	/**
	 * Constructor, where an ID is required. Not used from outside the package.
	 * To create a ConnectingObject, use the public constructor of the specific
	 * type (e.g. SequenceFlow).
	 * 
	 * @param id
	 */
	protected ConnectingObjectImpl(String id) {
		setId(id);
	}

	@Override
	public String getId() {
		if (id == null)
			return "";
		return id;
	}

	@Override
	public void setId(String id) {
		if (id == null || id.isEmpty())
			throw new IllegalArgumentException(
					"an ID may not be an empty String or null");
		this.id = id;
	}

	@Override
	public String toString() {
		return "ID " + getId();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (object instanceof ConnectingObject) {
			if (id != null && ((ConnectingObject) object).getId().equals(id))
				return true;
		}
		return false;
	}
}
