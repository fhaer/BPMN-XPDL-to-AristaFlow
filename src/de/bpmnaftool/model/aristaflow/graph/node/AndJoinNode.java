package de.bpmnaftool.model.aristaflow.graph.node;

/**
 * An AndJoinNode is used to close branches. In other words, this AndJoinNode
 * may have more than one incoming edge. This defines the end of the parallel
 * execution of all the branches (incoming edges). This AndJoinNode can only be
 * reached, if all nodes connected through the incoming edges have been reached.
 * No activity can be assigned to this type of node.
 * 
 * @author Felix Härer
 */
public class AndJoinNode extends NodeImpl {
	
	/**
	 * String to describe the type of node. This should not be used for checking
	 * the type of an edge, the keyword instanceof can be used instead. 
	 */
	public static final String nodeType = "NT_AND_JOIN";
	
	/**
	 * Constructor, sets node type 
	 */
	public AndJoinNode() {
		super(nodeType);
	}
	
}
