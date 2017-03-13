package de.bpmnaftool.model.aristaflow.graph.edge;

import de.bpmnaftool.model.aristaflow.graph.node.Node;

/**
 * Represents a sync edge which defines the order of execution between two nodes
 * in different parallel branches. E.g., if nodes n1 and n2 are in different
 * parallel branches, they could be executed at the same time. However, if a
 * SyncEdge exists with source node n1 and destination node n2, a parallel
 * execution is not possible anymore. In this case, n2 can not start execution
 * before n1 is completed.
 * 
 * @author Felix Härer
 */
public class SyncEdge extends EdgeImpl {
	
	/**
	 * String to describe the type of edge
	 */
	public static final String edgeType = "ET_SYNC";
	
	/**
	 * Constructor, sets edge type 
	 * 
	 * @param sourceNode the connected source node
	 * @param destinationNode the connected destination node
	 */
	public SyncEdge(Node sourceNode, Node destinationNode) {
		super(edgeType, sourceNode, destinationNode);
	}

}
