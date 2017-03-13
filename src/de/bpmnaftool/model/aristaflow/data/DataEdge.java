package de.bpmnaftool.model.aristaflow.data;

import de.bpmnaftool.model.aristaflow.graph.node.Node;

/**
 * A DataEdge connects a DataElement to a Node. It indicates either a READ or WRITE access to the
 * DataElement.
 * 
 * @author Felix Härer
 */
public interface DataEdge {

	/**
	 * a DataEdge can either read or write a DataElement
	 */
	public static enum EdgeType {
		/**
		 * DataEdge reads DataElement 
		 */
		READ, 
		/**
		 * DataEdge writes DataElement
		 */
		WRITE
	}

	/**
	 * Returns "READ" or "WRITE" according to the type of the edge.
	 * 
	 * @return type of edge
	 */
	public EdgeType getEdgeType();

	/**
	 * Retrieves the connected DataElement
	 * 
	 * @return connected DataElement
	 */
	public DataElement getDataElement();

	/**
	 * Retrieves the connected Node
	 * 
	 * @return connected Node
	 */
	public Node getNode();

	/**
	 * If true, the DataEdge may not be used (optional) if the branch it is in, is not executed. Default
	 * is false.
	 * 
	 * @return True, if DataEdge is optional. False otherwise.
	 */
	public boolean isOptional();

	/**
	 * Unique identifier of the DataEdge. Starts with 0, incremented by 1 for each new DataEdge.
	 * 
	 * @return unique identifier of this DataEdge
	 */
	public int getConnectorId();

	/**
	 * Unique identifier of the DataEdge. Starts with 0, incremented by 1 for each new DataEdge.
	 * 
	 * @param connectorId
	 *            unique identifier of this DataEdge
	 */
	public void setConnectorId(int connectorId);
}
