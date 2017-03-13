package de.bpmnaftool.model.aristaflow.data;

import java.util.UUID;

/**
 * implementation of DataElelment
 * 
 * @author Felix Härer
 */
public class DataElementImpl implements DataElement {

	/**
	 * @see DataElement
	 */
	protected String dataElementId;

	/**
	 * @see DataElement
	 */
	protected String name;

	/**
	 * @see DataElement
	 */
	protected String description;

	/**
	 * @see DataElement
	 */
	protected UUID identifier;

	/**
	 * @see DataElement
	 */
	protected DataElementType type;

	/**
	 * @see DataElement
	 */
	protected boolean isPublic;

	/**
	 * Constructor, requires a data type to be set
	 * 
	 * @param type
	 *            type of the DataElement, e.g. String
	 */
	public DataElementImpl(DataElementType type) {
		identifier = defaultIdentifier;
		this.type = type;
		isPublic = true;
	}

	@Override
	public void setDataElementId(String dataElementId) {
		if (dataElementId == null)
			throw new IllegalArgumentException("a data element ID may not be null");
		if (!dataElementId.matches("^d[0-9]+$"))
			throw new IllegalArgumentException(
					"a data element ID must begin with the letter d, followed by an integer");
		this.dataElementId = dataElementId;
	}

	@Override
	public String getDataElementId() {
		if (dataElementId == null)
			throw new IllegalStateException("a data element ID may not be null");
		if (!dataElementId.matches("^d[0-9]+$"))
			throw new IllegalStateException(
					"a data element ID must begin with the letter d, followed by an integer");
		return dataElementId;
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
	public String getDescription() {
		if (description == null)
			return "";
		return description;
	}

	@Override
	public UUID getIdentifierId() {
		return identifier;
	}

	@Override
	public DataElementType getType() {
		return type;
	}

	@Override
	public boolean isPublic() {
		return isPublic;
	}

	@Override
	public boolean equals(Object object) {
		boolean equals = false;
		if (object == this) {
			equals = true;
		} else if (object instanceof DataElement) {
			DataElement dElement = (DataElement) object;
			equals = true;
			equals &= dElement.getDataElementId().equals(dataElementId);
			equals &= dElement.getIdentifierId().equals(identifier);
			equals &= dElement.getType().equals(type);
			equals &= dElement.getName().equals(name);
		}
		return equals;
	}

	@Override
	public void setAsDecisionDataElement() {
		identifier = decisionIdentifier;
		name = decisionName + getDataElementId();
	}
}
