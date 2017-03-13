package de.bpmnaftool.model.aristaflow.graph.node;

/**
 * Implementation base class for all types of nodes
 * 
 * @author Felix Härer
 */
public class NodeImpl extends AbstractNode implements Node {

	/**
	 * String to describe the type of node. This should not be used for checking
	 * the type of an edge, the keyword instanceof can be used instead.
	 */
	public final String nodeType;

	/**
	 * Constructor, node type is required. Not used from outside the package. To
	 * create a Node, use the public constructor of the specific type (e.g.
	 * NormalNode).
	 */
	protected NodeImpl(final String nodeType) {
		this.nodeType = nodeType;
	}

	@Override
	public String getNodeType() {
		if (nodeType == null)
			throw new IllegalStateException("Node with unknown type");
		return nodeType;
	}

	@Override
	public void setNodeId(String nodeId) {
		if (nodeId == null)
			throw new IllegalArgumentException("a node ID may not be null");
		if (!nodeId.matches("^n[0-9]+$"))
			throw new IllegalArgumentException(
					"a node ID must begin with the letter n, followed by an integer");
		this.nodeId = nodeId;
	}

	@Override
	public String getNodeId() {
		if (nodeId == null)
			throw new IllegalStateException("a node ID may not be null");
		if (!nodeId.matches("^n[0-9]+$"))
			throw new IllegalStateException(
					"a node ID must begin with the letter n, followed by an integer");
		return nodeId;
	}

	@Override
	public Node getSplitNode() {
		return splitNode;
	}

	@Override
	public void setSplitNode(Node splitNode) {
		this.splitNode = splitNode;
	}

	@Override
	public int getBranchId() {
		return branchId;
	}

	@Override
	public int getTopoId() {
		return topoId;
	}

	@Override
	public void setBranchId(int branchId) {
		if (branchId < 0)
			throw new IllegalArgumentException(
					"branch ID needs to be at least 0");
		this.branchId = branchId;
	}

	@Override
	public void setTopoId(int topoId) {
		if (topoId < 0)
			throw new IllegalArgumentException("topo ID needs to be at least 0");
		this.topoId = topoId;
	}

	@Override
	public boolean getAutoStart() {
		return autoStart;
	}

	@Override
	public Node getCorrepondingBlockNode() {
		if (correspondingBlockNode == null)
			throw new IllegalStateException("no corresponding node set");
		return correspondingBlockNode;
	}

	@Override
	public String getDescription() {
		if (description == null)
			return "";
		return description;
	}

	@Override
	public String getName() {
		if (name == null)
			return " ";
		return name;
	}

	@Override
	public String getStaffAssignment() {
		if (staffAssignment == null)
			return "";
		return staffAssignment;
	}

	@Override
	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}

	@Override
	public void setCorrespondingBlockNode(Node node) {
		if (node == this)
			throw new IllegalStateException(
					"corresponding block node must not be the node itself");
		if (node == null)
			throw new IllegalArgumentException(
					"attempt to set corresponding block node to null");
		correspondingBlockNode = node;
	}

	@Override
	public void setDescription(String description) {
		if (description == null)
			throw new IllegalArgumentException(
					"attempt to set description to null");
		this.description = description;
	}

	@Override
	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("attempt to set name to null");
		if (name.equals(""))
			this.name = " ";
		else
			this.name = name;

	}

	@Override
	public void setStaffAssignment(String staffAssignment) {
		if (staffAssignment == null)
			throw new IllegalArgumentException(
					"attempt to set staff assignment to null");
		this.staffAssignment = staffAssignment;
	}

	@Override
	public boolean equals(Object object) {
		boolean equals = false;
		if (object == this) {
			equals = true;
		} else if (object instanceof Node) {
			Node node = (Node) object;
			equals = true;
			equals &= node.getNodeId().equals(nodeId);
			equals &= node.getNodeType().equals(nodeType);
			equals &= node.getName().equals(name);
		}
		return equals;
	}

	@Override
	public String toString() {
		return getName() + " (Typ " + getNodeType() + ", ID " + getNodeId() + ")"; 
	}
}
