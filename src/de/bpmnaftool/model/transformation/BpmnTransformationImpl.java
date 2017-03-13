package de.bpmnaftool.model.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import de.bpmnaftool.control.*;
import de.bpmnaftool.model.aristaflow.*;
import de.bpmnaftool.model.aristaflow.graph.edge.*;
import de.bpmnaftool.model.aristaflow.graph.node.*;
import de.bpmnaftool.model.bpmn.*;
import de.bpmnaftool.model.bpmn.connectingobject.*;
import de.bpmnaftool.model.bpmn.flowobject.*;
import de.bpmnaftool.model.bpmn.swimlane.*;
import de.bpmnaftool.model.transformation.NotTransformableException.CauseType;


/**
 * Implementing base class for model transformation
 * 
 * @author Felix Härer
 */
public class BpmnTransformationImpl extends Observable implements BpmnTransformation {

	/**
	 * BPMN model to transform. @see BpmnModel
	 */
	protected BpmnModel bpmnModel;
	/**
	 * Resulting AristaFlow model. @see AristaFlowModel
	 */
	protected AristaFlowModel afModel;
	/**
	 * First node of Destination model
	 */
	protected StartFlowNode afStartNode;
	/**
	 * Assigns an AristaFlow node to every BPMN FlowObject
	 */
	protected Map<FlowObject, Node> transformationMapping;
	/**
	 * Instance (singleton) of main controller @see BpmnAfTool
	 */
	private BpmnAfTool controller = BpmnAfToolImpl.getInstance();

	/**
	 * Creates a new transformation object, source model is required. Also adds an observer.
	 */
	public BpmnTransformationImpl(BpmnModel bpmnModel) {
		this.bpmnModel = bpmnModel;
		// add view observer for updating transformation progress
		addObserver((Observer) BpmnAfToolImpl.getInstance().getView());
	}

	@Override
	public AristaFlowModel getDestinationModel() {
		if (afModel == null)
			transform();
		return afModel;
	}

	@Override
	public BpmnModel getSourceModel() {
		if (bpmnModel == null)
			throw new IllegalStateException("source model missing");
		return bpmnModel;
	}

	@Override
	public void transform() {
		ModelPreparation.prepareModel(bpmnModel);
		try {
			transformModel();
		} catch (NotTransformableException e) {
			controller.log("", 0, 1);
			String errMsg = "Transformation fehlgeschlagen, versuche überlappende Gateways aufzulösen";
			controller.log(errMsg, 0, 1);
			ModelPreparation.resolveOverlappingGateways(bpmnModel);
			try {
				transformModel();
			} catch (NotTransformableException e2) {
				throw e2;
			}
		}
	}

	/**
	 * Creates the destinationModel, a StartNode and if there is more than one pool, an AndSplitNode
	 * which is later connected to the transformed start events of each pool.
	 * 
	 * @return Node which starts the workflow, either StartNode or AndSplit
	 */
	private Node initialize() {
		controller.log("Transformation gestartet ...", 0, 2);
		notifyObservers(0);
		afModel = new AristaFlowModelImpl(bpmnModel.getName());
		afStartNode = afModel.getStartNode();
		transformationMapping = new HashMap<FlowObject, Node>();
		if (bpmnModel.getPools().length > 1) {
			Node andSplit = new AndSplitNode();
			afModel.setNodeIds(andSplit, afStartNode);
			afModel.addNode(andSplit);
			afModel.addEdge(new ControlEdge(afStartNode, andSplit));
			return andSplit;
		}
		return afStartNode;
	}

	/**
	 * Creates an EndNode an if there is more than one pool, an AndJoinNode which is used to connect to
	 * the transformed end events of each pool.
	 * 
	 * @param endEvents
	 *            end events of each pool
	 * @param firstNode
	 *            node that started the workflow, either of type StartNode or AndSplit (if more than one
	 *            pool)
	 */
	private void finalize(ArrayList<FlowObject> endEvents, Node firstNode) {
		// create join node if firstNode was a split
		Node andJoin = new AndJoinNode();
		if (firstNode instanceof AndSplitNode) {
			afModel.setNodeIds(andJoin, afStartNode, firstNode.getBranchId());
			andJoin.setCorrespondingBlockNode(firstNode);
			firstNode.setCorrespondingBlockNode(andJoin);
			afModel.addNode(andJoin);
		}
		// create end node and edges to it
		afModel.finalizedModel();
		Node endNode = afModel.getEndNode();
		Node lastNode = endNode;
		if (firstNode instanceof AndSplitNode) {
			afModel.addEdge(new ControlEdge(andJoin, endNode));
			lastNode = andJoin;
		}
		for (FlowObject endEvent : endEvents) {
			Node endEventAf = transformationMapping.get(endEvent);
			afModel.addEdge(new ControlEdge(endEventAf, lastNode));
		}
		controller.log("Modell erfolgreich transformiert", 0, 2);
		controller.getView().setTransformProgress(100);
	}

	/**
	 * Transforms the model in four steps: initialize, pool transformation, message flow transformation,
	 * finalize
	 */
	private void transformModel() {
		Node startNode = initialize();

		ArrayList<FlowObject> endEvents = new ArrayList<FlowObject>();
		for (Pool pool : bpmnModel.getPools()) {
			Utilities.setProgress(bpmnModel, pool);
			FlowObject end = transformPool(pool, startNode);
			endEvents.add(end);
		}
		transformMessageFlows();

		finalize(endEvents, startNode);
	}

	/**
	 * Transforms the given Pool
	 * 
	 * @param pool
	 *            Pools to transform
	 * @param andSplit
	 *            Node where transformed objects are connected to
	 * @return last object in Pool (EndEvent)
	 */
	private FlowObject transformPool(Pool pool, Node andSplit) {
		controller.log("Transformiere Pool " + pool, 0, 1);
		FlowObject startEvent = null, endEvent = null;
		for (Event event : bpmnModel.getEvents(pool)) {
			if (event.getType() == Event.Type.StartEvent) {
				controller.log("Start Event gefunden: " + event, 1, 1);
				startEvent = event;
			}
		}
		controller.log("Transformiere Flow Objects", 1, 1);
		endEvent = traverseFlowObjects(startEvent, andSplit, afModel.getNextBranchId(), andSplit, null);
		controller.log("Pool transformiert: " + pool, 0, 2);
		return endEvent;
	}

	/**
	 * Transforms all MessageFlows into SyncEdges
	 */
	private void transformMessageFlows() {
		controller.log("Transformiere Message Flows ... ", 0, 1);
		for (MessageFlow messageFlow : bpmnModel.getMessageFlows()) {
			controller.log("Message Flow " + messageFlow, 1, 1);
			// source or destination must be activities, not a black pool
			if (!messageFlow.sourceIsPool() && !messageFlow.destinationIsPool()) {
				controller.log("transformiert", 2, 1);
				FlowObject bpmnSource = messageFlow.getSourceFlowObject();
				Node afSource = transformationMapping.get(bpmnSource);
				FlowObject bpmnDestination = messageFlow.getDestinationFlowObject();
				Node afDestination = transformationMapping.get(bpmnDestination);
				afModel.addEdge(new SyncEdge(afSource, afDestination));
			} else {
				controller.log("ignoriert (Quelle oder Ziel ist ein Pool)", 2, 1);
			}
		}
		controller.log("Message Flows transformiert", 0, 2);
	}

	/**
	 * Creates a Node and adds it to the destination model (AristaFlow model).
	 * 
	 * @param bpmnObject
	 *            object to transform
	 * @return created object
	 */
	private Node transformFlowObject(FlowObject bpmnObject, int branchId, Node splitNode, Node lastAfNode) {
		Node afNode = new NormalNode();
		if (bpmnObject instanceof Gateway) {
			afNode = transformGateway((Gateway) bpmnObject);
		}
		String nodeName = Utilities.getNodeName(bpmnModel, bpmnObject);
		afNode.setName(nodeName);
		afModel.setNodeIds(afNode, splitNode, branchId);
		afModel.addNode(afNode);
		transformationMapping.put(bpmnObject, afNode);
		if (lastAfNode != null && afNode != null) {
			afModel.addEdge(new ControlEdge(lastAfNode, afNode));
		}
		transformActivity(bpmnObject, afNode);
		return afNode;
	}

	/**
	 * Creates an Activity (Activity Template) and adds it to the destination model (AristaFlow model).
	 * It creates a "GeneratedForm activity" for any NormalNode and a "XorActivity" for XorSplitNodes or
	 * LoopEndNodes.
	 * 
	 * @param bpmnObject
	 *            Object where the node was created from
	 * @param node
	 *            Node to create an activity for
	 */
	private void transformActivity(FlowObject bpmnObject, Node node) {
		if (node instanceof XorSplitNode) {
			GatewaySplit gateway = (GatewaySplit) bpmnObject;
			String[] conditions = Utilities.getConditions(bpmnModel, gateway);
			afModel.createXorActivity(node, conditions);
		} else if (node instanceof EndLoopNode) {
			String[] conditions = new String[2];
			// Loop-Condition: 1st condition (do loop) is transformed, 2nd not
			for (FlowObject o : bpmnModel.getChildren(bpmnObject)) {
				SequenceFlow flow = bpmnModel.getSequenceFlow(bpmnObject, o);
				if (transformationMapping.get(o) != null)
					conditions[0] = flow.getCondition();
				else
					conditions[1] = flow.getCondition();
			}
			afModel.createXorActivity(node, conditions);
		} else if (node instanceof NormalNode) {
			afModel.createGeneratedFormActivity((NormalNode) node);
		}
	}

	/**
	 * Creates an AndSplitNode, AndJoinNode, XorSplitNode or XorJoinNode and returns it
	 * 
	 * @param gateway
	 *            Gateway to transform
	 * @return created Node
	 */
	private Node transformGateway(Gateway gateway) {
		if (gateway.getType() == Gateway.Type.Parallel) {
			if (gateway instanceof GatewaySplit) {
				return new AndSplitNode();
			} else if (gateway instanceof GatewayJoin) {
				return new AndJoinNode();
			}
			throw new NotTransformableException("Parallel Gateway neither Split nor Join");
		} else if (gateway.getType() == Gateway.Type.Exclusive) {
			if (gateway instanceof GatewaySplit) {
				return new XorSplitNode();
			} else if (gateway instanceof GatewayJoin) {
				return new XorJoinNode();
			}
			throw new NotTransformableException("Exclusive Gateway neither Split nor Join");
		} else if (gateway.getType() == Gateway.Type.Inclusive) {
			if (gateway instanceof GatewaySplit) {
				return new AndSplitNode();
			} else if (gateway instanceof GatewayJoin) {
				return new AndJoinNode();
			}
			throw new NotTransformableException("Inclusive Gateway neither Split nor Join");
		}
		throw new NotTransformableException("Gateway Type " + gateway.getType()
				+ " not supported, only Parallel and Exclusive are allowed");
	}

	private void transformGatewaySplit(FlowObject loopEnd, StartLoopNode afLoopStart) {
		controller.log("Flow Object " + loopEnd, 2, 1);
		controller.log("Gateway (Verzweigung) gefunden (Schleife)", 3, 1);
		Node afLoopEnd = new EndLoopNode();
		String endName = Utilities.getNodeName(bpmnModel, loopEnd);
		afLoopEnd.setName(endName);
		afModel.setNodeIds(afLoopEnd, afLoopStart.getSplitNode(), afLoopStart.getBranchId());
		afModel.addNode(afLoopEnd);
		transformationMapping.put(loopEnd, afLoopEnd);
		afLoopEnd.setCorrespondingBlockNode(afLoopStart);
		afLoopStart.setCorrespondingBlockNode(afLoopEnd);
		afModel.addEdge(new LoopEdge(afLoopEnd, afLoopStart));
		for (FlowObject parent : bpmnModel.getParents(loopEnd)) {
			Node afParent = transformationMapping.get(parent);
			afModel.addEdge(new ControlEdge(afParent, afLoopEnd));
		}
		transformActivity(loopEnd, afLoopEnd);
	}

	private void transformGatewayJoin(Node lastAfNode, Node afSplitNode, GatewayJoin gatewayJoin) {
		controller.log("Flow Object " + gatewayJoin, 2, 1);
		controller.log("Gateway (Zusammenführung) gefunden", 3, 1);
		ArrayList<Node> afJoinParents = new ArrayList<Node>();
		for (int i = 0; i < bpmnModel.getParents(gatewayJoin).length; i++) {
			FlowObject parent = bpmnModel.getParents(gatewayJoin)[i];
			Node afJoinParent = transformationMapping.get(parent);
			if (gatewayJoin.getType() == Gateway.Type.Inclusive) {
				Edge afXorEdge = afModel.getEdges(afSplitNode, true)[i];
				Node afXorSplit = afXorEdge.getDestinationNode();
				// create xor join
				XorJoinNode afXorJoin = new XorJoinNode();
				afModel.setNodeIds(afXorJoin, afXorSplit.getSplitNode(), afXorSplit.getBranchId());
				afXorSplit.setCorrespondingBlockNode(afXorJoin);
				afXorJoin.setCorrespondingBlockNode(afXorSplit);
				afModel.addNode(afXorJoin);
				if (!afJoinParent.equals(afSplitNode))
					afModel.addEdge(new ControlEdge(afJoinParent, afXorJoin));
				// insert empty branch
				afModel.addEdge(new ControlEdge(afXorSplit, afXorJoin));
				afJoinParents.add(afXorJoin);
			} else {
				afJoinParents.add(afJoinParent);
			}
		}

		int branchId = afSplitNode.getBranchId();
		Node splitNode = afSplitNode.getSplitNode();
		Node afJoin = transformFlowObject(gatewayJoin, branchId, splitNode, null);
		afJoin.setCorrespondingBlockNode(afSplitNode);
		afSplitNode.setCorrespondingBlockNode(afJoin);
		for (Node afParent : afJoinParents) {
			afModel.addEdge(new ControlEdge(afParent, afJoin));
		}
	}

	/**
	 * Traverses FlowObjects using backtracking. If a FlowObject is a Gateway, all branches are
	 * traversed, until the matching Gateway (join/split) is found. recursively starting with the given
	 * FlowObject. Traversal ends, if an end event is found.
	 * 
	 * @param bpmnObject
	 *            FlowObject to transform
	 * @param lastAfNode
	 *            Last node that was transformed (new node is connected here)
	 * @param branchId
	 *            ID (counter) for opened branches
	 * @param splitNode
	 *            last opened split node
	 * @param searchForGateway
	 *            If true, the method returns if bpmnObject is a split node. If false, it returns it is a
	 *            join node. If null, it does not return on split or join.
	 * @return last transformed FlowObject
	 */
	private FlowObject traverseFlowObjects(FlowObject bpmnObject, Node lastAfNode, int branchId,
			Node splitNode, Boolean searchForGateway) {
		controller.log("Flow Object " + bpmnObject, 2, 1);

		if (isMatchingGatewayFound(bpmnObject, searchForGateway)) {
			return bpmnObject;
		}
		Node afNode;
		FlowObject nextObject;

		transformFlowObject(bpmnObject, branchId, splitNode, lastAfNode);
		bpmnObject = detectGateway(bpmnObject, lastAfNode);
		afNode = transformationMapping.get(bpmnObject);
		nextObject = getNextFlowObject(bpmnObject);

		// return if bpmnObject is last object (end event)
		if (nextObject == null) {
			return bpmnObject;
		}
		return traverseFlowObjects(nextObject, afNode, branchId, splitNode, searchForGateway);
	}

	/**
	 * Traverses a Gateway and starts the transformation for it. It traverses all branches and after
	 * that, creates the gateway join.
	 * 
	 * @param gatewaySplit
	 *            Gateway to traverse and transform
	 * @param lastAfNode
	 *            previous node where new node is connected to
	 * @return gateway join
	 */
	private GatewayJoin traverseGateway(GatewaySplit gatewaySplit, Node lastAfNode) {
		controller.log("Gateway (Verzweigung) gefunden", 3, 1);
		Node afSplitNode = transformationMapping.get(gatewaySplit);
		GatewayJoin gatewayJoin = null;
		gatewayJoin = traverseGatewayBranches(gatewaySplit, afSplitNode, gatewayJoin);
		transformGatewayJoin(lastAfNode, afSplitNode, gatewayJoin);
		if (afSplitNode instanceof XorSplitNode) {
			int edgeCode = 0;
			for (FlowObject child : bpmnModel.getChildren(gatewaySplit)) {
				Node afChild = transformationMapping.get(child);
				Edge edge = afModel.getEdge(afSplitNode, afChild);
				edge.setEdgeCode(edgeCode++);
			}
		}
		return (GatewayJoin) gatewayJoin;
	}

	/**
	 * Traverses a branch of a Gateway. Each child of gatewaySplit represents the start of a branch.
	 * Branches are transformed completely before the next branch is traversed (backtracking).
	 * 
	 * @param gatewaySplit
	 *            traverse branches of this gateway
	 * @param afSplitNode
	 *            transformed AristaFlow node of gatewaySplit
	 * @param gatewayJoin
	 *            join (end of gateway branch)
	 * @return join gateway
	 */
	private GatewayJoin traverseGatewayBranches(GatewaySplit gatewaySplit, Node afSplitNode,
			GatewayJoin gatewayJoin) {
		for (FlowObject child : bpmnModel.getChildren(gatewaySplit)) {
			Node afBranchStart = afSplitNode;
			if (gatewaySplit.getType() == Gateway.Type.Inclusive) {
				afBranchStart = transformInclusiveGatewaySplit(gatewaySplit, afSplitNode, child);
			}
			GatewayJoin prevGatewayJoin = gatewayJoin;
			int branchId = afModel.getNextBranchId();
			FlowObject join = traverseFlowObjects(child, afBranchStart, branchId, afBranchStart, false);
			if (join == null || !(join instanceof GatewayJoin)) {
				throw new NotTransformableException(CauseType.NoGatewayJoin,
						"Zusammenführung nicht gefunden ", gatewaySplit);
			}
			gatewayJoin = (GatewayJoin) join;
			if (prevGatewayJoin != null && prevGatewayJoin != gatewayJoin) {
				throw new NotTransformableException(CauseType.NoGatewayJoin,
						"Verschiedene Gateways für die Zusammenführung gefunden: " + prevGatewayJoin
								+ " vs. " + gatewayJoin, gatewaySplit);
			}
		}
		return gatewayJoin;
	}

	/**
	 * Creates XOR Split Nodes for each branch of the gateway
	 * 
	 * @param gatewaySplit
	 *            Inclusive Gateway
	 * @param afSplitNode
	 *            transformed AristaFlow node of Gateway
	 * @param child
	 *            child of the Gateway
	 * @return XOR Split Node
	 */
	private Node transformInclusiveGatewaySplit(GatewaySplit gatewaySplit, Node afSplitNode,
			FlowObject child) {
		Node afBranchStart;
		// create xor split
		XorSplitNode afXorSplit = new XorSplitNode();
		afModel.setNodeIds(afXorSplit, afSplitNode, afModel.getNextBranchId());
		afModel.addNode(afXorSplit);
		afModel.addEdge(new ControlEdge(afSplitNode, afXorSplit));

		boolean isEmptyBranch = false;
		if (child instanceof GatewayJoin)
			if (((GatewayJoin) child).getType() == Gateway.Type.Inclusive)
				isEmptyBranch = true;

		String[] condition = new String[1];
		condition[0] = bpmnModel.getSequenceFlow(gatewaySplit, child).getCondition();
		if (!isEmptyBranch) {
			condition = Arrays.copyOf(condition, 2);
			condition[1] = "!(" + condition[0] + ")";
		}
		afModel.createXorActivity(afXorSplit, condition);

		afBranchStart = afXorSplit;
		return afBranchStart;
	}

	/**
	 * Traverses a loop and transforms it by starting methods to traverse the loops branches and insert
	 * it into the current AristaFlow model.
	 * 
	 * @param loopStart
	 *            start of loop (gateway join)
	 * @param lastAfNode
	 *            previous AristaFlow node where next node is connected to
	 * @return end of loop (gateway split)
	 */
	private GatewaySplit traverseLoop(GatewayJoin loopStart, Node lastAfNode) {
		controller.log("Gateway (Zusammenführung) gefunden (Schleife)", 3, 1);

		Node afJoin = transformationMapping.get(loopStart);

		FlowObject loopEnd = traverseLoopBranches(loopStart, lastAfNode);

		StartLoopNode afLoopStart;
		String startName = Utilities.getNodeName(bpmnModel, loopStart);
		afLoopStart = afModel.insertLoopStart(afJoin, lastAfNode, startName);
		transformationMapping.put(loopStart, afLoopStart);

		detectLoopStart(loopStart, afLoopStart);

		transformGatewaySplit(loopEnd, afLoopStart);
		return (GatewaySplit) loopEnd;
	}

	/**
	 * Traverses the branches of a loop and figures out which branch goes to the loop end.
	 * 
	 * @param loopStart
	 *            start of a loop (gateway join)
	 * @param lastAfNode
	 *            previous AristaFlow node where next node is connected to
	 * @return end of loop
	 */
	private FlowObject traverseLoopBranches(GatewayJoin loopStart, Node lastAfNode) {
		FlowObject[] parents = bpmnModel.getParents(loopStart);
		if (parents.length != 2) {
			throw new NotTransformableException(CauseType.NoGatewaySplit, "Verzweigung nicht gefunden",
					loopStart);
		}
		Node nodeA = transformationMapping.get(parents[0]);
		Node nodeB = transformationMapping.get(parents[1]);
		FlowObject loopEnd = Utilities.getLoopEnd(parents, nodeA, nodeB, lastAfNode, loopStart);
		return loopEnd;
	}

	/**
	 * Returns the next FlowObject to transform. Searches through all children of the given bpmnObject
	 * and returns a child, which has not been transformed, yet. Returns null if there is no next object
	 * (end event reached.
	 * 
	 * @param bpmnObject
	 *            last transformed FlowObject
	 * @return next FlowObject to transform
	 */
	private FlowObject getNextFlowObject(FlowObject bpmnObject) {
		if (Utilities.isEndEvent(bpmnObject))
			return null;
		FlowObject nextObject = null;
		for (FlowObject o : bpmnModel.getChildren(bpmnObject)) {
			if (transformationMapping.get(o) == null)
				nextObject = o;
		}
		if (Utilities.isEndEventWithoutMessageFlows(bpmnModel, nextObject))
			return null;
		return nextObject;
	}

	/**
	 * Detects, if the given FlowObject is a Gateway. If this is the case, it also detects if the gateway
	 * is the start of a Loop (JOIN gateway) or a split gateway. In both cases, methods for
	 * transformation are called.
	 * 
	 * @param bpmnObject
	 *            A FlowObject where a Gateway is to detect
	 * @param lastAfNode
	 *            Previous (last transformed) AristaFlow node
	 * @return returns the given bpmnObject if no Gateway was detected. If a Gateway was detected and
	 *         transformed, the matching join (if loop: split) is returned.
	 */
	private FlowObject detectGateway(FlowObject bpmnObject, Node lastAfNode) {
		if (bpmnObject instanceof GatewaySplit) {
			GatewayJoin join = traverseGateway((GatewaySplit) bpmnObject, lastAfNode);
			bpmnObject = join;
		} else if (bpmnObject instanceof GatewayJoin) {
			GatewaySplit split = traverseLoop((GatewayJoin) bpmnObject, lastAfNode);
			bpmnObject = split;
		}
		return bpmnObject;
	}

	/**
	 * Checks if the given loopStart is actually the start of a loop by traversing its children.
	 * 
	 * @param loopStart
	 *            start of loop (gateway join)
	 * @param afLoopStart
	 *            AristaFlow node of loop start
	 */
	private void detectLoopStart(GatewayJoin loopStart, StartLoopNode afLoopStart) {
		FlowObject[] children = bpmnModel.getChildren(loopStart);
		int loopBranchId = afModel.getNextBranchId();
		FlowObject loopEnd2 = traverseFlowObjects(children[0], afLoopStart, loopBranchId, afLoopStart,
				true);

		if (loopEnd2 == null || !(loopEnd2 instanceof GatewaySplit)) {
			throw new NotTransformableException(CauseType.NoGatewaySplit,
					"Verzweigung nicht gefunden  ", loopStart);
		}
	}

	/**
	 * Checks if the given FlowObject is a Gateway (if searchForGateway is enabled).
	 * 
	 * @param bpmnObject
	 *            FlowObject to check
	 * @param searchSplit
	 *            if true, search is for Gateways of type split. If false, search is for type join.
	 * @return true if the searched Gateway is found, false otherwise
	 */
	private boolean isMatchingGatewayFound(FlowObject bpmnObject, Boolean searchSplit) {
		if (searchSplit != null) {
			if (!searchSplit && bpmnObject instanceof GatewayJoin) {
				return detectLoopEnd(bpmnObject);
			}
			if (searchSplit) {
				for (FlowObject o : bpmnModel.getChildren(bpmnObject)) {
					// return if child is already transformed
					if (transformationMapping.get(o) != null)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks, if the given bpmnObject is the end of a loop (or a new gateway)
	 * 
	 * @param bpmnObject
	 *            object to check
	 * @return true if bpmnObject is end of loop, false otherwise
	 */
	private boolean detectLoopEnd(FlowObject bpmnObject) {
		FlowObject child = bpmnObject;
		ArrayList<FlowObject> visitedObjects = new ArrayList<FlowObject>();
		while (child != null) {
			visitedObjects.add(child);
			FlowObject newChild = null;
			for (FlowObject o : bpmnModel.getChildren(child))
				if (transformationMapping.get(o) == null && !visitedObjects.contains(o))
					newChild = o;
			child = newChild;
			for (FlowObject parent : bpmnModel.getParents(bpmnObject)) {
				if (parent.equals(child))
					return false;
			}
		}
		return true;
	}

}
