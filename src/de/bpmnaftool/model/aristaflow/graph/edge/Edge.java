package de.bpmnaftool.model.aristaflow.graph.edge;

import de.bpmnaftool.model.aristaflow.graph.node.Node;

/**
 * Abstract representation of a directed edge. Instead of using this generalized
 * edge, the specialized sub-types should be used. The purpose of this interface
 * is to define methods which are needed for all types of edges. It is not
 * possible to create an instance of this class (protected constructor) - use
 * the subclasses instead.
 * 
 * @author Felix Härer
 */
public abstract interface Edge {

	/**
	 * Retrieves the source node where the edge is outgoing
	 * 
	 * @return Node source node
	 */
	Node getSourceNode();

	/**
	 * Retrieves the destination node where the edge is incoming
	 * 
	 * @return Node destination node
	 */
	Node getDestinationNode();

	/**
	 * Retrieves the edge code. It is used if other edges have the same
	 * sourceNodeId. For all edges with the same source node, the edgeCode must
	 * be unique. Edges are numbered with the edgeCode in sequence starting from
	 * 0. 
	 * 
	 * @return edge code, at least 0. Null if no edge code is set.
	 */
	Integer getEdgeCode();

	/**
	 * Sets an edge code. It is used if other edges have the same sourceNodeId.
	 * For all edges with the same source node, the edgeCode must be unique.
	 * Edges are numbered with the edgeCode in sequence starting from 0.
	 * 
	 * @param edgeCode
	 */
	void setEdgeCode(int edgeCode);

	/**
	 * Retrieves the type of the edge in a string. This should not be used for
	 * checking the type of an edge, the keyword instanceof can be used instead.
	 * 
	 * @return type of edge as string
	 */
	String getEdgeType();
}
