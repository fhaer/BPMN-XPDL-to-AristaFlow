package de.bpmnaftool.model.aristaflow.graph.node;

/**
 * This node may have an XOR Activity assigned which contains a condition for the do-until loop.
 * 
 * @author Felix Härer
 */
public class StartLoopNode extends NodeImpl {

	/**
	 * String to describe the type of node. This should not be used for checking the type of an edge, the
	 * keyword instanceof can be used instead.
	 */
	public static final String nodeType = "NT_STARTLOOP";

	/**
	 * Constructor, sets node type
	 */
	public StartLoopNode() {
		super(nodeType);
	}
}
