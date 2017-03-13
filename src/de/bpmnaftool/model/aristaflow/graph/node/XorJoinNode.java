package de.bpmnaftool.model.aristaflow.graph.node;

/**
 * An XorJoinNode is used to close branches. In other words, this XorJoinNode
 * may have more than one incoming edge. This defines the end of the exclusive
 * execution of all the branches (incoming edges). This XorJoinNode can only be
 * reached, if one node connected through the incoming edges has been reached.
 * No activity can be assigned to this type of node.
 * 
 * @author Felix Härer
 */
public class XorJoinNode extends NodeImpl {

	/**
	 * String to describe the type of node. This should not be used for checking
	 * the type of an edge, the keyword instanceof can be used instead. 
	 */
	public static final String nodeType = "NT_XOR_JOIN";

	/**
	 * Constructor, sets node type 
	 */
	public XorJoinNode() {
		super(nodeType);
	}

}
