package de.bpmnaftool.model.aristaflow.graph.node;

/**
 * Abstract representation of a node. Instead of using this generalized node,
 * the specialized sub-types should be used. The purpose of this interface is to
 * define methods which are needed for all types of nodes. It is not possible to
 * create an instance of this class (protected constructor) - use the subclasses
 * instead.
 * 
 * @author Felix Härer
 */
public interface Node {

	/**
	 * Default value for StaffAssignment. This defines who is allowed to execute
	 * a node; default is anyone.
	 */
	final static String defaultStaffAssignment = "Agent()";

	/**
	 * Sets the ID of this Node. The ID of a node must begin with the letter n
	 * followed by an integer.
	 * 
	 * @param nodeId
	 *            ID to set
	 */
	void setNodeId(String nodeId);

	/**
	 * Returns the ID of this Node
	 * 
	 * @return ID of this node as string
	 */
	String getNodeId();

	/**
	 * Retrieves the type of the node in a string. This should not be used for
	 * checking the type of an edge, the keyword instanceof can be used instead.
	 * 
	 * @return type of node as string
	 */
	String getNodeType();

	/**
	 * topological position ID: AristaFlow orders nodes from left to right in
	 * sequence using this ID
	 * 
	 * @param topoId
	 *            topological position ID to set
	 */
	void setTopoId(int topoId);

	/**
	 * topological position ID: AristaFlow orders nodes from left to right in
	 * sequence using this ID
	 * 
	 * @return topological position ID
	 */
	int getTopoId();

	/**
	 * each branch (beginning with an AndNode or XorNode) has a unique
	 * identifier
	 * 
	 * @see AbstractNode
	 * 
	 * @param branchId
	 *            of the branch which is set to this node
	 */
	void setBranchId(int branchId);

	/**
	 * each branch (beginning with an AndNode or XorNode) has a unique
	 * identifier
	 * 
	 * @see AbstractNode
	 * 
	 * @return ID of the branch of this node
	 */
	int getBranchId();

	/**
	 * Sets the nodeId of the last split node
	 * 
	 * @see AbstractNode
	 * 
	 * @param splitNode
	 *            NodeId of the last split node
	 */
	void setSplitNode(Node splitNode);

	/**
	 * Retrieves the ID of the last split node
	 * 
	 * @see AbstractNode
	 * 
	 * @return NodeId of the last split node
	 */
	Node getSplitNode();

	/**
	 * Returns name of this node.
	 * 
	 * @return name of node OR if name not set, a white space
	 */
	String getName();

	/**
	 * Sets name of this node
	 * 
	 * @param name
	 *            name of node
	 */
	void setName(String name);

	/**
	 * Returns a description of the node
	 * 
	 * @return description of node
	 */
	String getDescription();

	/**
	 * Sets a description of the node
	 * 
	 * @param description
	 *            description of node
	 */
	void setDescription(String description);

	/**
	 * Returns, if a node is automatically started. Usually (if false) a user
	 * has to start a node explicitly.
	 * 
	 * @return true if auto start enabled, false otherwise
	 */
	boolean getAutoStart();

	/**
	 * Sets, if a node is automatically started. Usually (if set to false) a
	 * user has to start a node explicitly.
	 * 
	 * @param autoStart
	 *            if true, auto start is enabled, if false auto start is
	 *            disabled
	 */
	void setAutoStart(boolean autoStart);

	/**
	 * Returns the staff assignment. This contains a person, organizational unit
	 * or role who is allowed to execute the node.
	 * 
	 * @return staff assignment as string
	 */
	String getStaffAssignment();

	/**
	 * Sets the staff assignment. This can be a person, organizational unit or
	 * role who is allowed to execute the node.
	 * 
	 * @param staffAssignment
	 *            staff assignment as string
	 */
	void setStaffAssignment(String staffAssignment);

	/**
	 * Returns the corresponding block node of this node. A block consists of a
	 * split and a join Node, e.g. xor split and xor join. The corresponding
	 * block node for a split node is the join node and vice versa.
	 * 
	 * @return corresponding block node
	 */
	Node getCorrepondingBlockNode();

	/**
	 * Sets the corresponding block node of this node. A block consists of a
	 * split and a join Node, e.g. xor split and xor join. The corresponding
	 * block node for a split node is the join node and vice versa.
	 * 
	 * @param node corresponding block node
	 */
	void setCorrespondingBlockNode(Node node);

}
