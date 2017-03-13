package de.bpmnaftool.model.aristaflow.graph.node;

/**
 * Special node which is used as the root of the tree graph (first node).
 * AristaFlow starts to execute the workflow with this node.
 * No activity can be assigned to this type of node.
 * 
 * @author Felix Härer
 */
public class StartFlowNode extends NodeImpl {

	/**
	 * String to describe the type of node. This should not be used for checking
	 * the type of an edge, the keyword instanceof can be used instead. 
	 */
	public static final String nodeType = "NT_STARTFLOW";

	/**
	 * Node name of EndNode, can not be changed.
	 */
	public static final String startNodeName = "Start";
	
	/**
	 * Constructor, sets node type 
	 */
	public StartFlowNode() {
		super(nodeType);
		name = startNodeName;
	}

	@Override
	public void setSplitNode(Node splitNode) {
		throw new IllegalAccessError("a StartNode can not have a SplitNode assigned");
	}
	
	@Override
	public void setName(String name) {
		throw new IllegalAccessError("Name of StartNode not can not be changed");
	}
}
