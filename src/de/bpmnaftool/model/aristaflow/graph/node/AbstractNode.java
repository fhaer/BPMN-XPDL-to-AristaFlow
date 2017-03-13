package de.bpmnaftool.model.aristaflow.graph.node;

/**
 * Represents an abstract node as it can not be instantiated. It defines
 * attributes which are needed for all edges, regardless of their type.
 * 
 * @author Felix Härer
 */
public abstract class AbstractNode {

	/**
	 * unique identifier of the node
	 */
	protected String nodeId;

	/**
	 * name of the node
	 */
	protected String name;

	/**
	 * description of the node and its activity
	 */
	protected String description;

	/**
	 * if true, the node is started automatically without user interaction
	 */
	protected boolean autoStart;

	/**
	 * staffAssignment contains a person or an organizational unit which is
	 * allowed to execute the current node
	 */
	protected String staffAssignment;

	/*
	 * STRUCTURAL NODE DATA: contains IDs for position, branch, corresponding
	 * nodes
	 */

	/**
	 * topological position ID: AristaFlow orders nodes from left to right in
	 * sequence using this ID
	 */
	protected int topoId;

	/**
	 * each branch (beginning with an AndNode or XorNode) has a unique
	 * identifier
	 */
	protected int branchId;

	/**
	 * Holds the Node of the corresponding block node. A block consists of a
	 * split and a join Node, e.g. xor split and xor join. The corresponding
	 * block node for a split node is the join node and vice versa.
	 */
	protected Node correspondingBlockNode;

	/**
	 * Holds the NodeId of the last split node. If there is no split node before
	 * the current node, this holds the NodeId of StartNode. StartNode and
	 * EndNode do not use this value. It is protected to prevent it to be set
	 * for StartNode or EndNode.
	 */
	protected Node splitNode;
}
