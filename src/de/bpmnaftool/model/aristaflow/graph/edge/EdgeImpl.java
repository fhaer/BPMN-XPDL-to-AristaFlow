package de.bpmnaftool.model.aristaflow.graph.edge;

import de.bpmnaftool.model.aristaflow.graph.node.Node;

/**
 * Implementation base class for all types of edges
 * 
 * @author Felix Härer
 */
public class EdgeImpl extends AbstractEdge implements Edge {

	/**
	 * String to describe the type of edge. This should not be used for checking
	 * the type of an edge, the keyword instanceof can be used instead.
	 */
	public final String edgeType;

	/**
	 * Constructor, not used from outside the package. To create an edge, use
	 * the public constructor of the specific type (e.g. ControlEdge).
	 * 
	 * @param edgeType
	 *            String representing the type of edge
	 */
	protected EdgeImpl(final String edgeType, Node sourceNode,
			Node destinationNode) {
		this.edgeType = edgeType;
		if (sourceNode == destinationNode) {
			throw new IllegalArgumentException(
					"Source and Destination Node of an Edge can not be the same Node: "
							+ this);
		}
		this.sourceNode = sourceNode;
		this.destinationNode = destinationNode;
	}

	@Override
	public Node getDestinationNode() {
		if (destinationNode == null)
			throw new IllegalStateException("Edge without destination node");
		return destinationNode;
	}

	@Override
	public Node getSourceNode() {
		if (sourceNode == null)
			throw new IllegalStateException("Edge without source node");
		return sourceNode;
	}

	@Override
	public Integer getEdgeCode() {
		return edgeCode;
	}

	@Override
	public void setEdgeCode(int edgeCode) {
		if (edgeCode < 0)
			throw new IllegalArgumentException(
					"edgeCode has to be at least 0, but is " + edgeCode);
		this.edgeCode = edgeCode;
	}

	@Override
	public String getEdgeType() {
		if (edgeType == null)
			throw new IllegalStateException("Edge with unknown type");
		return edgeType;
	}

	@Override
	public boolean equals(Object object) {
		boolean equals = false;
		if (object == this) {
			equals = true;
		} else if (object instanceof Edge) {
			Edge edge = (Edge) object;
			equals = true;
			equals &= edge.getEdgeType().equals(edgeType);
			equals &= edge.getSourceNode().equals(sourceNode);
			equals &= edge.getDestinationNode().equals(destinationNode);
		}
		return equals;
	}
}
