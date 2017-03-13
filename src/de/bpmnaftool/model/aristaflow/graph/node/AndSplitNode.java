package de.bpmnaftool.model.aristaflow.graph.node;

/**
 * An AndJoinSplit is used to start a new branch. In other words, from this
 * AndJoinSplit on, there may be more than one outgoing edge. This allows all
 * branches (outgoing edges) to be executed parallel.
 * No activity can be assigned to this type of node.
 * 
 * @author Felix Härer
 */
public class AndSplitNode extends NodeImpl {
	
	/**
	 * String to describe the type of node. This should not be used for checking
	 * the type of an edge, the keyword instanceof can be used instead. 
	 */
	public static final String nodeType = "NT_AND_SPLIT";

	/**
	 * Constructor, sets node type 
	 */
	public AndSplitNode() {
		super(nodeType);
	}

}
