package de.bpmnaftool.model.aristaflow.data;

import de.bpmnaftool.model.aristaflow.graph.node.Node;

/**
 * Implementation of a DataEdge
 * 
 * @author Felix Härer
 */
public class DataEdgeImpl implements DataEdge {

	/**
	 * see DataEdge @see DataEdge
	 */
	protected DataElement dataElement;

	/**
	 * see DataEdge @see DataEdge
	 */
	protected Node node;

	/**
	 * see DataEdge @see DataEdge
	 */
	protected EdgeType type;

	/**
	 * see DataEdge @see DataEdge
	 */
	protected boolean isOptional;

	/**
	 * see DataEdge @see DataEdge
	 */
	protected int connectorId;

	/**
	 * To create a new DataEdge, it's type, DataElement and Node must be given
	 * 
	 * @param type
	 *            type of the new DataEdge (READ or WRITE)
	 * @param dataElement
	 *            element which is connected to the data edge
	 * @param node
	 *            node which is connected to the data edge
	 * @param isOptional
	 *            if true, the DataEdge may not be used (optional) if the branch it is in, is not
	 *            executed
	 */
	public DataEdgeImpl(EdgeType type, DataElement dataElement, Node node, boolean isOptional) {
		this.type = type;
		this.dataElement = dataElement;
		this.node = node;
		this.isOptional = isOptional;
	}

	/**
	 * To create a new DataEdge, it's type, DataElement and Node must be given
	 * 
	 * @param type
	 *            type of the new DataEdge (READ or WRITE)
	 * @param dataElement
	 *            element which is connected to the data edge
	 * @param node
	 *            node which is connected to the data edge
	 */
	public DataEdgeImpl(EdgeType type, DataElement dataElement, Node node) {
		this.type = type;
		this.dataElement = dataElement;
		this.node = node;
		this.isOptional = false;
	}

	@Override
	public int getConnectorId() {
		return connectorId;
	}

	@Override
	public DataElement getDataElement() {
		return dataElement;
	}

	@Override
	public EdgeType getEdgeType() {
		return type;
	}

	@Override
	public Node getNode() {
		return node;
	}

	@Override
	public boolean isOptional() {
		return isOptional;
	}

	@Override
	public void setConnectorId(int connectorId) {
		if (connectorId < 0)
			throw new IllegalArgumentException("a connector ID has to be at least 0");
		this.connectorId = connectorId;
	}

	@Override
	public boolean equals(Object object) {
		boolean equals = false;
		if (object == this) {
			equals = true;
		} else if (object instanceof DataEdge) {
			DataEdge dEdge = (DataEdge) object;
			equals = true;
			equals &= dEdge.getNode().equals(node);
			equals &= dEdge.getDataElement().equals(dataElement);
			equals &= dEdge.getEdgeType().equals(type);
		}
		return equals;
	}

}
