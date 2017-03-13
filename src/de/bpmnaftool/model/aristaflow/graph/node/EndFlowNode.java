package de.bpmnaftool.model.aristaflow.graph.node;

/**
 * Special node which is used as the only leave of the tree graph (last node).
 * AristaFlow stops to execute the workflow with this node.
 * No activity can be assigned to this type of node.
 * 
 * @author Felix Härer
 */
public class EndFlowNode extends NodeImpl {

	/**
	 * String to describe the type of node. This should not be used for checking
	 * the type of an edge, the keyword instanceof can be used instead. 
	 */
	public static final String nodeType = "NT_ENDFLOW";
	
	/**
	 * Node name of EndNode, can not be changed.
	 */
	public static final String endNodeName = "End";

	/**
	 * Constructor, sets node type 
	 */
	public EndFlowNode() {
		super(nodeType);
		name = endNodeName;
	}

	@Override
	public void setSplitNode(Node splitNode) {
		throw new IllegalAccessError("an EndNode can not have a SplitNode assigned");
	}

	@Override
	public void setName(String name) {
		throw new IllegalAccessError("Name of EndNode not can not be changed");
	}
}
