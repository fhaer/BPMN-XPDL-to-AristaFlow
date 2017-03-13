package de.bpmnaftool.model.aristaflow;

import java.util.UUID;

import de.bpmnaftool.model.aristaflow.activity.*;
import de.bpmnaftool.model.aristaflow.data.*;
import de.bpmnaftool.model.aristaflow.graph.edge.*;
import de.bpmnaftool.model.aristaflow.graph.node.*;


/**
 * Representation of an AristaFlow model. It constructs, stores and retrieves all model elements.
 * 
 * @author Felix Härer
 */
public interface AristaFlowModel {

	/**
	 * Default supervisory agent for the model. A role, person, organizational unit who is allowed to
	 * execute the model.
	 */
	final String defaultSupervisoryAgent = "supervisor";

	/**
	 * returns the model ID
	 * 
	 * @return model ID
	 */
	UUID getId();

	/**
	 * returns the model name as String
	 * 
	 * @return name of model
	 */
	String getName();

	/**
	 * Creates an initial workflow graph with start node, sets a supervisor who is allowed to execute the
	 * workflow.
	 */
	void initializeModel();

	/**
	 * Creates an end node and set's IDs for it.
	 */
	void finalizedModel();

	/**
	 * Adds a node to the model and assigns an ID.
	 * 
	 * @param node
	 *            node to add
	 */
	void addNode(Node node);

	/**
	 * Adds an edge to the model. No ID is set, edges are identified using source and destination node.
	 * 
	 * @param edge
	 *            edge to add
	 */
	void addEdge(Edge edge);

	/**
	 * Adds a data element to the model and assigns an ID. DataElements need to have a unique name,
	 * therefore a number is added to the name, if an element with the same name already exists.
	 * 
	 * @param dataElemenet
	 *            data element to add
	 */
	void addDataElement(DataElement dataElemenet);

	/**
	 * Adds a data edge to the model and assigns a connector ID.
	 * 
	 * @param dataEdge
	 *            data edge to add
	 */
	void addDataEdge(DataEdge dataEdge);

	/**
	 * Adds an activity to the model.
	 * 
	 * @param activity
	 *            activity to add
	 */
	void addActivity(ActivityTemplate activity);

	/**
	 * Removes a node from the model
	 * 
	 * @param node
	 *            node to add
	 */
	void removeNode(Node node);

	/**
	 * Removes the given edge from the model
	 * 
	 * @param edge
	 *            edge to remove
	 */
	void removeEdge(Edge edge);

	/**
	 * Creates an XorActivity for an existing XorSplitNode. Also creates DataElements and DataEdges for
	 * the conditions.
	 * 
	 * @param node
	 *            Node where XorActivity is assigned to
	 * @param conditions
	 *            Array with conditions
	 */
	public void createXorActivity(Node node, String[] conditions);

	/**
	 * Creates a GeneratedFormActivity and assigns it to the given node
	 * 
	 * @param node
	 *            node where activity is assigned to
	 */
	public void createGeneratedFormActivity(NormalNode node);

	/**
	 * Replaces the given node with a StartLoopNode node.
	 * 
	 * @param nodeToReplace
	 *            node to be replaced
	 * @param previousNode
	 *            previous node, where control edge from previousNode to nodeToReplace exists
	 * @param name
	 *            name for the new node
	 * @return The new StartLoopNode
	 */
	public StartLoopNode insertLoopStart(Node nodeToReplace, Node previousNode, String name);

	/**
	 * Returns an array with all nodes in the model.
	 * 
	 * @return all nodes of this model
	 */
	Node[] getAllNodes();

	/**
	 * Returns all Edges of this model
	 * 
	 * @return Edges in array
	 */
	Edge[] getEdges();

	/**
	 * Retrieves an edge between two given nodes.
	 * 
	 * @param sourceNode
	 *            node where the edge is outgoing
	 * @param destinationNode
	 *            node where the edge is incoming
	 * @return edge between sourceNode and destinationNode
	 */
	Edge getEdge(Node sourceNode, Node destinationNode);

	/**
	 * Returns all sequence flows where the given node is source or destination node
	 * 
	 * @param node
	 *            source or destination of the sequence flows
	 * @param isSourceNode
	 *            if true, node is the source node, if false, node is the destination node
	 * @return sequence flows with given source or destination node
	 */
	Edge[] getEdges(Node node, boolean isSourceNode);

	/**
	 * Returns all DataElements of this model
	 * 
	 * @return DataElements in array
	 */
	DataElement[] getDataElements();

	/**
	 * Returns the DataElement with the given name (the name of a DataElement is unique)
	 * 
	 * @return DataElement with given name, null if not found
	 */
	DataElement getDataElement(String name);

	/**
	 * Retrieves a data edge between a node and a data element.
	 * 
	 * @param node
	 *            node the data edge is connected to
	 * @param dataElement
	 *            data element the data edge is connected to
	 * @param type
	 *            type of data access, can be READ or WRITE
	 * @return the retrieved data edge
	 */
	DataEdge getDataEdge(Node node, DataElement dataElement, DataElement.DataElementType type);

	/**
	 * Returns all DataEdges of this model
	 * 
	 * @return DataEdges in array
	 */
	DataEdge[] getDataEdges();

	/**
	 * Returns the Node which is the first Node of the workflow
	 * 
	 * @return start Node
	 */
	StartFlowNode getStartNode();

	/**
	 * Returns the Node which is the last Node of the workflow
	 * 
	 * @return end Node
	 */
	EndFlowNode getEndNode();

	/**
	 * Returns the supervisory agent. A role, person, organizational unit who is allowed to execute the
	 * model.
	 * 
	 * @return supervisory agent
	 */
	String getSupervisoryAgent();

	/**
	 * Sets the supervisory agent. A role, person, organizational unit who is allowed to execute the
	 * model.
	 * 
	 * @param agent
	 *            supervisory agent to set
	 */
	void setSupervisoryAgent(String agent);

	/**
	 * Returns the next unused BranchId
	 * 
	 * @return unused branch ID
	 */
	int getNextBranchId();

	/**
	 * Sets the IDs of the given node. IDs are topoId, branchId, SplitNode and CorrespondingBlockNode.
	 * TopoId is incremented and does not need to be passed to this method, CorrespondingBlockNode is set
	 * to the start node. CorrespondingBlockNode and SplitNode are not set for StartNode or EndNode.
	 * 
	 * @param node
	 *            Node where ID's need to be set
	 * @param splitNode
	 *            Last AndNode or XorNode, @see AristaFlowModel
	 * @param branchId
	 *            ID of the branch, @see AristaFlowModel
	 */
	public void setNodeIds(Node node, Node splitNode, int branchId);

	/**
	 * Sets the IDs of the given node. IDs are topoId, branchId, SplitNode and CorrespondingBlockNode.
	 * TopoId is incremented and does not need to be passed to this method, CorrespondingBlockNode is set
	 * to the start node. CorrespondingBlockNode and SplitNode are not set for StartNode or EndNode.
	 * BranchId is incremented automatically.
	 * 
	 * @param node
	 *            Node where ID's need to be set
	 * @param splitNode
	 *            Last AndNode or XorNode, @see AristaFlowModel
	 */
	public void setNodeIds(Node node, Node splitNode);
}
