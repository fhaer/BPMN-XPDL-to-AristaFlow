package de.bpmnaftool.model.aristaflow.graph.edge;

import de.bpmnaftool.model.aristaflow.graph.node.Node;

/**
 * Representation of a control edge, a directed edge which
 * defines the control flow between two nodes.
 * 
 * @author Felix Härer
 */
public class ControlEdge extends EdgeImpl {
	
	/**
	 * String to describe the type of edge
	 */
	public static final String edgeType = "ET_CONTROL";
	
	/**
	 * Constructor, sets edge type 
	 * 
	 * @param sourceNode the connected source node
	 * @param destinationNode the connected destination node
	 */
	public ControlEdge(Node sourceNode, Node destinationNode) {
		super(edgeType, sourceNode, destinationNode);
	}
	
}
