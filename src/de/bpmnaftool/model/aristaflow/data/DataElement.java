package de.bpmnaftool.model.aristaflow.data;

import java.util.UUID;

/**
 * A DataElement holds one data attribute of any type, e.g. String or boolean.
 * For read and write access from nodes, DataEdges need to be created.
 * 
 * @author Felix Härer
 */
public interface DataElement {

	/**
	 * Sets a unique identifier of this DataElement. The ID is a String
	 * beginning with the letter d followed by an integer.
	 */
	public void setDataElementId(String dataElementId);

	/**
	 * Returns unique identifier of this DataElement as String
	 * 
	 * @return ID of this DataElement as String
	 */
	public String getDataElementId();

	/**
	 * Enumeration which holds all possible values for allowed data types for a
	 * DataElement.
	 */
	public static enum DataElementType {
		/**
		 * connected data element is of type boolean 
		 */
		BOOLEAN, 
		/**
		 * connected data element is of type integer
		 */
		INTEGER, 
		/**
		 * connected data element is of type string
		 */
		STRING, 
		/**
		 * type of connected data element is not known
		 */
		UNDEFINED
	}

	/**
	 * Returns the data type of a DataElement
	 * 
	 * @return data type of this DataElement
	 */
	public DataElementType getType();

	/**
	 * Return the name of this DataElement
	 * 
	 * @return name of DataElement
	 */
	public String getName();

	/**
	 * Sets the name of this DataElement
	 * 
	 * @param name
	 *            name of DataElement
	 */
	public void setName(String name);

	/**
	 * Returns description of this DataElement
	 * 
	 * @return description String
	 */
	public String getDescription();

	/**
	 * Returns an UUID which can be used to identify the DataElement. If the
	 * default identifier is set (see below), the name is used.
	 * 
	 * @return UUID of this DataElement
	 */
	public UUID getIdentifierId();

	/**
	 * If no UUID is set, this default identifier is set.
	 */
	static final UUID defaultIdentifier = UUID
			.fromString("ffffffff-ffff-fffe-8000-000000000000");

	/**
	 * Default identifier for DataElements that are used as output parameter of
	 * an XorActivity (Decision DataElement).
	 */
	static final UUID decisionIdentifier = UUID
			.fromString("f31b8592-f943-4911-847a-bdc65a8bdacd");

	/**
	 * Default name for DataElements that are used as output parameter of
	 * an XorActivity (Decision DataElement).
	 */
	static final String decisionName = "Decision";
	
	/**
	 * A public DataElement can be accessed form everywhere. Default (if not
	 * set) is true.
	 * 
	 * @return true if this DataElement is public
	 */
	public boolean isPublic();

	/**
	 * Sets name and identifier in order to represent a Decision DataElement
	 * which is used as output parameter of an xorActivity.
	 */
	public void setAsDecisionDataElement();
}
