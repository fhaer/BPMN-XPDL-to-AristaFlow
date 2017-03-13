package de.bpmnaftool.model.aristaflow.graph.edge;

import de.bpmnaftool.model.aristaflow.graph.node.Node;

/**
 * Represents an abstract edge as it can not be instantiated. It defines
 * attributes which are needed for all edges, regardless of their type.
 * 
 * @author Felix Härer
 */
public abstract class AbstractEdge implements Edge {

	/**
	 * the source node where the edge is outgoing
	 */
	protected Node sourceNode;

	/**
	 * the destination node where the edge is incoming
	 */
	protected Node destinationNode;

	/**
	 * Used if other edges have the same sourceNodeId. For all edges with the
	 * same source node, the edgeCode must be unique. Edges are numbered with
	 * the edgeCode in sequence starting from 0.
	 */
	protected Integer edgeCode;

}
