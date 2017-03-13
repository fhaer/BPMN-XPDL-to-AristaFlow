package de.bpmnaftool.model.aristaflow;

import java.util.ArrayList;
import java.util.UUID;


import de.bpmnaftool.control.BpmnAfToolImpl;
import de.bpmnaftool.model.aristaflow.activity.*;
import de.bpmnaftool.model.aristaflow.data.*;
import de.bpmnaftool.model.aristaflow.data.DataEdge.EdgeType;
import de.bpmnaftool.model.aristaflow.data.DataElement.*;
import de.bpmnaftool.model.aristaflow.graph.edge.*;
import de.bpmnaftool.model.aristaflow.graph.node.*;

/**
 * Implementation of AristaFlow model
 * 
 * @author Felix Härer
 */
public class AristaFlowModelImpl implements AristaFlowModel {

	/**
	 * see AristaFlowModel @see AristaFlowModel
	 */
	private final UUID id;
	/**
	 * see AristaFlowModel @see AristaFlowModel
	 */
	private final String name;
	/**
	 * see AristaFlowModel @see AristaFlowModel
	 */
	private String supervisoryAgent;
	/**
	 * Stores all Activities of the model
	 */
	private ArrayList<ActivityTemplate> activities;
	/**
	 * Stores all nodes of the model
	 */
	private ArrayList<Node> nodes;
	/**
	 * Stores all edges of the model
	 */
	private ArrayList<Edge> edges;
	/**
	 * Stores all data elements of the model
	 */
	private ArrayList<DataElement> dataElements;
	/**
	 * stores all data edges of the model
	 */
	private ArrayList<DataEdge> dataEdges;
	/**
	 * First Node of this workflow
	 */
	private StartFlowNode startNode;
	/**
	 * Last Node of this workflow
	 */
	private EndFlowNode endNode;
	/**
	 * stores the next id which is used when a new node is added
	 */
	private int nextNodeId = 0;
	/**
	 * Unique ID that defines the topological order of nodes in the graph.
	 * 
	 * @see AristaFlowModel
	 */
	private int nextTopoId = 0;
	/**
	 * Unique ID for a Branch, this value is the next ID to be set. @see AristaFlowModel
	 */
	private int nextBranchId = 0;
	/**
	 * stores the next id which is used when a new data element is added
	 */
	private int nextDataElementId = 0;
	/**
	 * stores the next id which is used when a new data edge is added
	 */
	private int nextConnectorId = 0;

	/**
	 * Creates an initial workflow model with a new random ID.
	 * 
	 * @param name
	 *            name of the new model
	 */
	public AristaFlowModelImpl(String name) {
		this.id = UUID.randomUUID();
		this.name = name;
		initializeModel();
	}

	@Override
	public void initializeModel() {
		BpmnAfToolImpl.getInstance().log("initialisiere leeres AristaFlow-Workflowschema", 0, 2);
		supervisoryAgent = defaultSupervisoryAgent;
		activities = new ArrayList<ActivityTemplate>();
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
		dataElements = new ArrayList<DataElement>();
		dataEdges = new ArrayList<DataEdge>();
		// create start node and set IDs
		startNode = new StartFlowNode();
		setNodeIds(startNode, startNode, nextBranchId++);
		addNode(startNode);
	}

	@Override
	public void finalizedModel() {
		BpmnAfToolImpl.getInstance().log("Stelle AristaFlow-Wokflowschema fertig ...", 0, 2);
		Node endNode = new EndFlowNode();
		setNodeIds(endNode, startNode, startNode.getBranchId());
		endNode.setCorrespondingBlockNode(startNode);
		startNode.setCorrespondingBlockNode(endNode);
		addNode(endNode);
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSupervisoryAgent() {
		return supervisoryAgent;
	}

	@Override
	public void setSupervisoryAgent(String agent) {
		if (agent == null)
			throw new IllegalArgumentException("supervisoryAgent set to null");
		supervisoryAgent = agent;
	}

	@Override
	public void addActivity(ActivityTemplate activity) {
		if (activities == null)
			throw new IllegalStateException("Modell nicht initialisiert");
		if (activity == null)
			throw new IllegalArgumentException("Activity ist null");
		if (activity.getActivityId().isEmpty())
			throw new IllegalArgumentException("Activity hat keine ID " + activity);
		activities.add(activity);
	}

	@Override
	public void addDataEdge(DataEdge dataEdge) {
		if (dataEdges == null)
			throw new IllegalStateException("Modell nicht initialisiert");
		if (dataEdge == null)
			throw new IllegalArgumentException("dataEdge ist null");
		dataEdge.setConnectorId(nextConnectorId++);
		dataEdges.add(dataEdge);
	}

	@Override
	public void addDataElement(DataElement dataElement) {
		if (dataElements == null)
			throw new IllegalStateException("Modell nicht initialisiert");
		if (dataElement == null)
			throw new IllegalArgumentException("dataElement ist null");
		dataElement.setDataElementId("d" + nextDataElementId++);
		// name of a data element must be unique
		String name = dataElement.getName();
		int id = 2;
		for (DataElement e : dataElements) {
			while (e.getName().equals(dataElement.getName()))
				dataElement.setName(name + " (" + id++ + ")");
		}
		dataElements.add(dataElement);
	}

	@Override
	public void addEdge(Edge edge) {
		if (edges == null)
			throw new IllegalStateException("Modell nicht initialisiert");
		if (edge == null)
			throw new IllegalArgumentException("edge ist null");
		edges.add(edge);
		setEdgeCode(edge.getSourceNode());
	}

	/**
	 * Sets the edge code for all edges which have the given node as source node if it is an XorSplitNode
	 * or LoopEndNode. Otherwise, no edge code is needed. The edge code is an integer starting from 0
	 * which numbers all edges from the same source node.
	 * 
	 * @param sourceNode
	 *            set edge code for all edges with this node as source
	 */
	private void setEdgeCode(Node sourceNode) {
		int edgeCode = 0;
		for (Edge e : getEdges(sourceNode, true)) {
			boolean isXorSplit = (sourceNode instanceof XorSplitNode);
			boolean isLoopEnd = (sourceNode instanceof EndLoopNode);
			if (isXorSplit || isLoopEnd) {
				e.setEdgeCode(edgeCode++);
			}
		}
	}

	@Override
	public void addNode(Node node) {
		if (nodes == null)
			throw new IllegalStateException("Modell nicht initialisiert");
		if (node == null)
			throw new IllegalArgumentException("node ist null");
		node.setNodeId("n" + nextNodeId++);
		nodes.add(node);
		if (node instanceof StartFlowNode)
			startNode = (StartFlowNode) node;
		if (node instanceof EndFlowNode)
			endNode = (EndFlowNode) node;
	}

	@Override
	public Node[] getAllNodes() {
		Node[] nodesArray = new Node[0];
		if (nodes != null)
			nodesArray = (Node[]) nodes.toArray(new Node[0]);
		return nodesArray;
	}

	@Override
	public DataEdge getDataEdge(Node node, DataElement dataElement, DataElement.DataElementType type) {

		for (DataEdge dEdge : dataEdges) {
			if (dEdge.getDataElement().equals(dataElement) && dEdge.getNode().equals(node)
					&& dEdge.getEdgeType().equals(type)) {
				return dEdge;
			}
		}

		throw new IllegalArgumentException("data edge existiert nicht für data element "
				+ dataElement.toString() + ", node " + node.toString() + ", typ " + type.toString());
	}

	@Override
	public Edge getEdge(Node sourceNode, Node destinationNode) {

		for (Edge edge : edges) {
			if (edge.getSourceNode().equals(sourceNode)
					&& edge.getDestinationNode().equals(destinationNode)) {
				return edge;
			}
		}

		throw new IllegalArgumentException("edge existiert nicht für Quelle " + sourceNode.toString()
				+ ", und Ziel " + destinationNode.toString());
	}

	@Override
	public DataEdge[] getDataEdges() {
		return (DataEdge[]) dataEdges.toArray(new DataEdge[0]);
	}

	@Override
	public DataElement[] getDataElements() {
		return (DataElement[]) dataElements.toArray(new DataElement[0]);
	}

	@Override
	public DataElement getDataElement(String name) {
		for (DataElement e : getDataElements()) {
			if (e.getName().equals(name))
				return e;
		}
		return null;
	}

	@Override
	public Edge[] getEdges() {
		return (Edge[]) edges.toArray(new Edge[0]);
	}

	@Override
	public Edge[] getEdges(Node node, boolean isSourceNode) {
		ArrayList<Edge> foundEdges = new ArrayList<Edge>();
		for (Edge edge : edges) {
			if (isSourceNode && edge.getSourceNode() == node)
				foundEdges.add(edge);
			if (!isSourceNode && edge.getDestinationNode() == node)
				foundEdges.add(edge);
		}
		return (Edge[]) foundEdges.toArray(new Edge[0]);
	}

	@Override
	public EndFlowNode getEndNode() {
		if (endNode == null)
			throw new IllegalStateException("Kein EndNode gesetzt");
		return endNode;
	}

	@Override
	public StartFlowNode getStartNode() {
		if (startNode == null)
			throw new IllegalStateException("Kein StartNode gesetzt");
		return startNode;
	}

	@Override
	public void removeEdge(Edge edge) {
		if (edge == null)
			throw new IllegalArgumentException("Zu löschende Edge ist null");
		if (!edges.remove(edge))
			throw new IllegalArgumentException("Zu löschende Edge ist nicht Teil des Modells");
		// edge code might have changed
		Node sourceNode = edge.getSourceNode();
		setEdgeCode(sourceNode);
	}

	@Override
	public void removeNode(Node node) {
		if (node == null)
			throw new IllegalArgumentException("Zu löschender Node ist null");
		if (!nodes.remove(node))
			throw new IllegalArgumentException("Zu löschender Node ist nicht Teil des Modells");
		int max = nodes.size();
		if (nextTopoId > max)
			max = nextTopoId;
		Node[] orderedNodes = new Node[max];
		for (Node n : nodes) {
			int topoId = n.getTopoId();
			orderedNodes[topoId] = n;
		}
		int topoId = 0;
		for (Node n : orderedNodes) {
			if (n != null)
				n.setTopoId(topoId++);
		}
		nextTopoId = topoId;
	}

	@Override
	public void setNodeIds(Node node, Node splitNode, int branchId) {
		node.setTopoId(nextTopoId++);
		node.setBranchId(branchId);
		if (!(node instanceof StartFlowNode || node instanceof EndFlowNode)) {
			node.setSplitNode(splitNode);
			node.setCorrespondingBlockNode(startNode);
		}
	}

	@Override
	public void setNodeIds(Node node, Node splitNode) {
		int nextBranchId = getNextBranchId();
		setNodeIds(node, splitNode, nextBranchId);
	}

	@Override
	public int getNextBranchId() {
		return nextBranchId++;
	}

	@Override
	public void createXorActivity(Node node, String[] conditions) {
		// find decisionNode where the DataElement can be connected
		NormalNode decisionNode = getNormalNodeBeforeSplit(node);
		if (decisionNode == null) {
			decisionNode = createDecisionNode(node);
		}
		DataEdge[] data;
		data = createDataElementsForXorActivity(node, decisionNode, conditions);

		// create decision data element (needed by arista flow)
		DataElement decision = new DataElementImpl(DataElementType.INTEGER);
		addDataElement(decision);
		decision.setAsDecisionDataElement();
		DataEdge decEdge;
		decEdge = new DataEdgeImpl(EdgeType.WRITE, decision, node);
		addDataEdge(decEdge);

		// create activity
		ActivityTemplate xorActivity = new XorActivity(conditions, data, decEdge);
		if (node instanceof XorSplitNode)
			((XorSplitNode) node).setActivity(xorActivity);
		if (node instanceof EndLoopNode)
			((EndLoopNode) node).setActivity(xorActivity);
	}

	@Override
	public void createGeneratedFormActivity(NormalNode node) {
		ArrayList<DataEdge> inputs, outputs;
		inputs = new ArrayList<DataEdge>();
		outputs = new ArrayList<DataEdge>();
		for (DataEdge d : dataEdges) {
			EdgeType t = d.getEdgeType();
			if (d.getNode().equals(node) && t.equals(EdgeType.WRITE))
				outputs.add(d);
			if (d.getNode().equals(node) && t.equals(EdgeType.READ))
				inputs.add(d);
		}
		DataEdge[] inputA = inputs.toArray(new DataEdge[0]);
		DataEdge[] outputA = outputs.toArray(new DataEdge[0]);
		ActivityTemplate activity = new GeneratedForm(inputA, outputA);
		node.setActivity(activity);
	}

	@Override
	public StartLoopNode insertLoopStart(Node nodeToReplace, Node previousNode, String name) {
		StartLoopNode afLoopStart = new StartLoopNode();
		afLoopStart.setName(name);
		setNodeIds(afLoopStart, nodeToReplace.getSplitNode(), nodeToReplace.getBranchId());
		addNode(afLoopStart);
		addEdge(new ControlEdge(previousNode, afLoopStart));
		removeEdge(getEdge(previousNode, nodeToReplace));
		removeNode(nodeToReplace);
		return afLoopStart;
	}

	/**
	 * Returns the NormalNode before the given SplitNode. If there is no NormalNode before the SplitNode,
	 * it returns null.
	 * 
	 * @param xorSplitNode
	 *            SplitNode behind returned node
	 * @return NormalNode before given SplitNode
	 */
	private NormalNode getNormalNodeBeforeSplit(Node xorSplitNode) {
		Node prevNode = xorSplitNode;
		while (true) {
			Edge[] edges = getEdges(prevNode, false);
			if (edges.length != 1) {
				return null;
			}
			prevNode = edges[0].getSourceNode();
			if (prevNode instanceof NormalNode) {
				return (NormalNode) prevNode;
			}
			// return if prevNode is not a split
			if (!(prevNode instanceof XorSplitNode || prevNode instanceof AndSplitNode)) {
				return null;
			}
		}
	}

	/**
	 * Creates a new NormalNode to be used for making decisions before XorActivities
	 * 
	 * @return NormalNode for decisions
	 */
	private NormalNode createDecisionNode(Node node) {
		// create decision node and exchange topo ID with xorSplitNode
		NormalNode dec = new NormalNode();
		dec.setName(node.getName());
		if (node instanceof EndLoopNode) {
			Node pre = getEdges(node, false)[0].getSourceNode();
			setNodeIds(dec, pre.getSplitNode(), pre.getBranchId());
		} else {
			setNodeIds(dec, node.getSplitNode(), node.getBranchId());
		}
		int xorTopoId = node.getTopoId();
		node.setTopoId(dec.getTopoId());
		dec.setTopoId(xorTopoId);
		addNode(dec);
		// connect edge to new node
		Edge[] toXorEdge = getEdges(node, false);
		Edge edgeA = new ControlEdge(toXorEdge[0].getSourceNode(), dec);
		Edge edgeB = new ControlEdge(dec, node);
		addEdge(edgeA);
		addEdge(edgeB);
		removeEdge(toXorEdge[0]);
		return dec;
	}

	/**
	 * Creates DataElements and DataEdges needed for XorActivities. DataElements store the conditions and
	 * DataEdges connect them to the node where the condition is set (decisionNode).
	 * 
	 * @param splitNode
	 *            Node where XorActivity is assigned to
	 * @param decisionNode
	 *            Node which is used to set conditions (decision node)
	 * @return array with created data edges
	 */
	private DataEdge[] createDataElementsForXorActivity(Node splitNode, NormalNode decisionNode,
			String[] conditions) {
		// create a data element for each condition (except last), at least one
		int numberOfElements = conditions.length - 1;
		if (numberOfElements == 0)
			numberOfElements = 1;
		DataEdge[] data = new DataEdge[numberOfElements];
		for (int i = 0; i < numberOfElements; i++) {
			DataElement elem = new DataElementImpl(DataElementType.BOOLEAN);
			elem.setName(conditions[i]);
			addDataElement(elem);
			data[i] = new DataEdgeImpl(EdgeType.READ, elem, splitNode);
			addDataEdge(data[i]);
			addDataEdge(new DataEdgeImpl(EdgeType.WRITE, elem, decisionNode));
		}
		createGeneratedFormActivity(decisionNode);
		return data;
	}

}
