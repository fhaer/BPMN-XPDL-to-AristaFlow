package de.bpmnaftool.model.aristaflow.graph.edge;

import de.bpmnaftool.model.aristaflow.graph.node.Node;

/**
 * Represents a loop edge which is needed to return to the beginning of a loop.
 * E.g., for three nodes in sequence n1->n2->n3, a loop can be defined using a
 * LoopEdge with source node n3 and destination node n1.
 * 
 * @author Felix Härer
 */
public class LoopEdge extends EdgeImpl {
	
	/**
	 * String to describe the type of edge
	 */
	public static final String edgeType = "ET_LOOP";
	
	/**
	 * Constructor, sets edge type 
	 * 
	 * @param sourceNode the connected source node
	 * @param destinationNode the connected destination node
	 */
	public LoopEdge(Node sourceNode, Node destinationNode) {
		super(edgeType, sourceNode, destinationNode);
	}

}
