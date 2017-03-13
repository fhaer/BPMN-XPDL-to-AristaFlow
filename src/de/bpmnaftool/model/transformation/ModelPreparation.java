package de.bpmnaftool.model.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import de.bpmnaftool.control.BpmnAfToolImpl;
import de.bpmnaftool.model.bpmn.BpmnModel;
import de.bpmnaftool.model.bpmn.connectingobject.SequenceFlow;
import de.bpmnaftool.model.bpmn.connectingobject.SequenceFlowImpl;
import de.bpmnaftool.model.bpmn.flowobject.FlowObject;
import de.bpmnaftool.model.bpmn.flowobject.Gateway;
import de.bpmnaftool.model.bpmn.flowobject.GatewayJoin;
import de.bpmnaftool.model.bpmn.flowobject.GatewaySplit;
import de.bpmnaftool.model.bpmn.swimlane.Lane;
import de.bpmnaftool.model.bpmn.swimlane.Pool;


/**
 * Contains static methods to prepare a BPMN model for its transformation
 * 
 * @author Felix Härer
 */
public class ModelPreparation {

	/**
	 * Prepares the given model for transformation
	 * 
	 * @param model
	 *            model to prepare
	 */
	public static void prepareModel(BpmnModel model) {
		removeBlackBoxPools(model);
		removeUndefinedConditions(model);
	}

	/**
	 * Removes black box pools from a BPMN-Model
	 * 
	 * @param model
	 *            model to prepare
	 */
	public static void removeBlackBoxPools(BpmnModel model) {
		BpmnAfToolImpl.getInstance().log("BPMN-Workflowschema für die Transformation vorbereiten", 0, 1);

		for (Pool pool : model.getPools()) {
			FlowObject[] objects = model.getFlowObjectsByPool(pool);
			if (objects.length == 0) {
				BpmnAfToolImpl.getInstance().log("Entferne Black Box Pool " + pool, 1, 1);
				for (Lane lane : model.getLanes(pool))
					model.removeSwimLane(lane.getId());
				model.removeSwimLane(pool.getId());
			}
		}
		BpmnAfToolImpl.getInstance().log("", 1, 1);
	}

	/**
	 * Replaces all empty conditions with a number and creates conditions for default flows.
	 * 
	 * @param model
	 *            model to prepare
	 */
	public static void removeUndefinedConditions(BpmnModel model) {
		int conditionNr = 0;
		for (Pool pool : model.getPools()) {
			for (FlowObject object : model.getFlowObjectsByPool(pool)) {
				String defaultCondition = "";
				SequenceFlow defaultFlow = null;
				for (SequenceFlow sf : model.getSequenceFlows(object, true)) {
					String condition = sf.getCondition();
					if (sf.isDefaultFlow()) {
						defaultFlow = sf;
						continue;
					} else if (condition != null && condition.isEmpty()) {
						sf.setCondition(Integer.toString(conditionNr++));
					}
					defaultCondition += " && !(" + sf.getCondition() + ")";
				}
				if (defaultFlow != null) {
					defaultFlow.setCondition(defaultCondition.substring(4));
				}
			}
		}
	}

	/**
	 * In case overlapping Gateways are found, this method tries to resolve them. It removes the Gateways
	 * and creates new ones which do not overlap; also, it reduces the number of Gateways.
	 * 
	 * @param sourceModel
	 *            Model where overlapping Gateways are to be resolved
	 */
	public static BpmnModel resolveOverlappingGateways(BpmnModel sourceModel) {
		BpmnAfToolImpl.getInstance().log("Suche nach überlappenden Gateways ...", 1, 1);
		for (Pool pool : sourceModel.getPools()) {
			for (Gateway gateway : sourceModel.getGateways(pool)) {
				// find sequence of Gateways
				boolean isSequenceOfGateways = false;
				for (FlowObject fObject : sourceModel.getChildren(gateway)) {
					if (fObject instanceof Gateway) {
						isSequenceOfGateways = true;
					} else {
						isSequenceOfGateways = false;
						break;
					}
				}
				if (isSequenceOfGateways) {
					// try to resolve overlapping gateway
					if (resolveOverlappingGateway(sourceModel, gateway)) {
						BpmnAfToolImpl.getInstance().log(
								"überlappenden Gateways aufgelöst für \"" + gateway + "\"", 2, 1);
					}
				}
			}
		}
		BpmnAfToolImpl.getInstance().log("", 1, 1);
		return sourceModel;
	}

	/**
	 * Checks if Gateways in the given path list have incoming or outgoing flows to other nodes than the
	 * Gateways in the given list. If this is the case, the Gateways can not be used resolved if the
	 * overlap.
	 * 
	 * @param sourceModel
	 *            BpmnModel where paths are checked
	 * @param pathList
	 *            List of Paths with Gateways to check
	 * @return true if no external incoming/outgoing flows, false otherwise
	 */
	private static boolean checkGatewayClusterForExternalFlows(BpmnModel sourceModel,
			ArrayList<SequenceFlow[]> pathList) {

		// get all Gateways in the given list of paths
		ArrayList<FlowObject> gatewayList = new ArrayList<FlowObject>();
		for (SequenceFlow[] path : pathList) {
			for (SequenceFlow sFlow : path) {
				gatewayList.add(sFlow.getSourceFlowObject());
			}
		}
		// check inclusive gateways for incoming SequenceFlows
		for (FlowObject f : gatewayList) {
			if (f instanceof GatewayJoin) {
				// are all parents of this object part of pathList?
				for (FlowObject parent : sourceModel.getParents(f)) {
					if (!gatewayList.contains(parent)) {
						// resolve not possible, gateway join with
						// incoming SequenceFlow from somewhere else found
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * In case overlapping Gateways are found, this method tries to resolve one Gateway. It removes all
	 * overlapping Gateway and creates new ones which do not overlap; also, it reduces the number of
	 * Gateways.
	 * 
	 * @param sourceModel
	 *            Model where overlapping Gateway are to be resolved
	 * @param splitGateway
	 *            Gateway to resolve
	 * @return True if Gateway Overlap has been resolved, false otherwise
	 */
	static boolean resolveOverlappingGateway(BpmnModel sourceModel, Gateway splitGateway) {

		if (!Utilities.checkGatewayParents(sourceModel, splitGateway))
			return false;

		// list of paths for cluster 1, 2 and parents of the clusers
		ArrayList<SequenceFlow[]> cluster1PathList = new ArrayList<SequenceFlow[]>();
		ArrayList<SequenceFlow[]> cluster2PathList = new ArrayList<SequenceFlow[]>();
		ArrayList<FlowObject> cluster2ParentNode = new ArrayList<FlowObject>();

		// find all possible paths to all activities starting from gateway split
		cluster1PathList = Utilities.getGatewayCluster1Paths(sourceModel, splitGateway);

		if (!checkGatewayClusterForExternalFlows(sourceModel, cluster1PathList))
			return false;

		boolean foundCluster2 = false;
		foundCluster2 = getCluster2(sourceModel, cluster1PathList, cluster2ParentNode, cluster2PathList);
		if (!foundCluster2)
			return false;

		// check if all cluster2pahts end in the same object
		SequenceFlow lastFlow = null;
		Gateway cluster2LastGateway = null;
		FlowObject cluster2Child = null;
		lastFlow = checkCluster2PathsEndObject(cluster2PathList);
		if (lastFlow == null)
			return false;
		cluster2LastGateway = (Gateway) lastFlow.getSourceFlowObject();
		cluster2Child = lastFlow.getDestinationFlowObject();

		// get conditions for each path of consolidated gateway
		ArrayList<String> cluster1PathConditions;
		cluster1PathConditions = new ArrayList<String>();
		getCluster1PathConditions(sourceModel, cluster1PathList, cluster1PathConditions);

		// get cluster1Children and cluster2Parents
		ArrayList<FlowObject> cluster1Children = new ArrayList<FlowObject>();
		ArrayList<FlowObject> cluster2Parents = new ArrayList<FlowObject>();
		getCluster1ChildrenCluster2Parents(cluster1PathList, cluster2ParentNode, cluster1Children,
				cluster2Parents);

		// get conditions for each activity
		ArrayList<String> activityConditions;
		activityConditions = getConditionsForAllActivities(cluster1PathList, cluster1PathConditions,
				cluster1Children);

		// are some activities executed for all conditions?
		ArrayList<FlowObject> activitiesAlwaysExecuted;
		activitiesAlwaysExecuted = getAlwaysExecutedActivities(cluster1PathList, cluster1PathConditions,
				cluster1Children);

		createNonOverlappingGateways(sourceModel, splitGateway, cluster2LastGateway, cluster2Child,
				cluster1Children, cluster2Parents, activityConditions, activitiesAlwaysExecuted);

		removeOverlappingGateways(sourceModel, cluster1PathList, cluster2PathList);

		return true;
	}

	/**
	 * Creates new Gateways for the overlapping ones. A parallel split is created which is followed by
	 * xor split nodes for each activity (cluster 1 children). Cluster 2 is replaced by xor join nodes
	 * which connect to an and join.
	 * 
	 * @param sourceModel
	 *            BPMN model
	 * @param splitGateway
	 *            Overlapping gateway (start of cluster 1)
	 * @param cluster2LastGateway
	 *            end of cluster 2
	 * @param cluster2Child
	 *            first activity after cluster 2
	 * @param cluster1Children
	 *            first activity after cluster 1 (between cluster 1 and 2)
	 * @param cluster2Parents
	 *            last activity before cluster 2 (between cluster 1 and 2)
	 * @param activityConditions
	 *            conditions for each activity
	 * @param activitiesAlwaysExecuted
	 *            list of activities that are always executed, regardless of condition
	 */
	private static void createNonOverlappingGateways(BpmnModel sourceModel, Gateway splitGateway,
			Gateway cluster2LastGateway, FlowObject cluster2Child,
			ArrayList<FlowObject> cluster1Children, ArrayList<FlowObject> cluster2Parents,
			ArrayList<String> activityConditions, ArrayList<FlowObject> activitiesAlwaysExecuted) {

		// create parallel split/join
		Gateway newSplit = new GatewaySplit(splitGateway.getLane(), Gateway.Type.Parallel);
		Gateway newJoin = createNewParallelSplit(sourceModel, splitGateway, cluster2LastGateway,
				cluster2Child, newSplit);
		String newId = splitGateway.getId() + "_" + cluster2LastGateway.getId();

		// create new, resolved gateway
		for (int i = 0; i < cluster1Children.size(); i++) {
			FlowObject cluster1Child = cluster1Children.get(i);
			FlowObject cluster2Parent = cluster2Parents.get(i);
			String condition = activityConditions.get(i);
			// create an xor split if there is a condition for this child
			if (!condition.isEmpty() && !activitiesAlwaysExecuted.contains(cluster1Child)) {
				createNewXorSplit(sourceModel, splitGateway, newSplit, newJoin, newId, cluster1Child,
						cluster2Parent, condition);
			} else {
				// connect branchStart/branchEnd to parSplit/parJoin
				SequenceFlow branchStartFlow = new SequenceFlowImpl(newSplit, cluster1Child);
				SequenceFlow branchEndFlow = new SequenceFlowImpl(cluster2Parent, newJoin);
				// set IDs
				branchStartFlow.setId(newId + 5);
				branchEndFlow.setId(newId + 6);
				// add to model
				sourceModel.addConnectingObject(branchStartFlow);
				sourceModel.addConnectingObject(branchEndFlow);
			}
		}
	}

	/**
	 * Creates a new XOR split node and sets the conditions
	 * 
	 * @param sourceModel
	 *            BPMN model
	 * @param splitGateway
	 *            overlapping gateway (start of cluster 1)
	 * @param newSplit
	 *            newly created parallel split node
	 * @param newJoin
	 *            newly created parallel join node
	 * @param newId
	 *            ID for new nodes
	 * @param cluster1Child
	 *            child of cluster 1 (activity)
	 * @param cluster2Parent
	 *            parent of cluster 2 (activity)
	 * @param condition
	 *            condition for the new xor split
	 */
	private static void createNewXorSplit(BpmnModel sourceModel, Gateway splitGateway, Gateway newSplit,
			Gateway newJoin, String newId, FlowObject cluster1Child, FlowObject cluster2Parent,
			String condition) {
		// create excl.split/join and connect to new par.split/join
		Gateway exclSplit = new GatewaySplit(cluster1Child.getLane(), Gateway.Type.Exclusive);
		Gateway exclJoin = new GatewayJoin(cluster1Child.getLane(), Gateway.Type.Exclusive);
		SequenceFlow startFlow = new SequenceFlowImpl(newSplit, exclSplit);
		SequenceFlow endFlow = new SequenceFlowImpl(exclJoin, newJoin);

		// connect branchStart/branchEnd to exclSplit/exclJoin
		SequenceFlow branchStartFlow = new SequenceFlowImpl(exclSplit, cluster1Child);
		SequenceFlow branchEndFlow = new SequenceFlowImpl(cluster2Parent, exclJoin);
		branchStartFlow.setCondition(condition);

		// insert empty branch
		SequenceFlow emptyBranch = new SequenceFlowImpl(exclSplit, exclJoin);
		emptyBranch.setCondition("!(" + condition + ")");

		// set IDs
		exclSplit.setName(splitGateway.getName());
		exclSplit.setId(newId + 5);
		exclJoin.setId(newId + 6);
		startFlow.setId(newId + 7);
		endFlow.setId(newId + 8);
		branchStartFlow.setId(newId + 9);
		branchEndFlow.setId(newId + 10);
		emptyBranch.setId(newId + 11);

		// add to model
		sourceModel.addFlowObject(exclSplit);
		sourceModel.addFlowObject(exclJoin);
		sourceModel.addConnectingObject(startFlow);
		sourceModel.addConnectingObject(endFlow);
		sourceModel.addConnectingObject(branchStartFlow);
		sourceModel.addConnectingObject(branchEndFlow);
		sourceModel.addConnectingObject(emptyBranch);
	}

	/**
	 * Creates a new parallel split node for a resolved gateway
	 * 
	 * @param sourceModel
	 *            BPMN model
	 * @param splitGateway
	 *            overlapping gateway (star of cluster 1)
	 * @param cluster2LastGateway
	 *            end of cluster 2
	 * @param cluster2Child
	 *            first activity after cluster 2
	 * @param newSplit
	 *            new split node
	 * @return new join node
	 */
	private static Gateway createNewParallelSplit(BpmnModel sourceModel, Gateway splitGateway,
			Gateway cluster2LastGateway, FlowObject cluster2Child, Gateway newSplit) {
		Gateway newJoin = new GatewayJoin(cluster2LastGateway.getLane(), Gateway.Type.Parallel);
		String id = splitGateway.getId() + "_" + cluster2LastGateway.getId();
		FlowObject splitGatewayParent = sourceModel.getParents(splitGateway)[0];
		SequenceFlow newSplitFlow = new SequenceFlowImpl(splitGatewayParent, newSplit);
		SequenceFlow newJoinFlow = new SequenceFlowImpl(newJoin, cluster2Child);
		newSplit.setId(id + 0);
		newJoin.setId(id + 1);
		newSplitFlow.setId(id + 2);
		newJoinFlow.setId(id + 3);
		sourceModel.addFlowObject(newSplit);
		sourceModel.addFlowObject(newJoin);
		sourceModel.addConnectingObject(newSplitFlow);
		sourceModel.addConnectingObject(newJoinFlow);
		return newJoin;
	}

	/**
	 * Removes resolved overlapping gateways
	 * 
	 * @param sourceModel
	 *            BPMN model
	 * @param cluster1PathList
	 *            list of paths in cluster 1
	 * @param cluster2PathList
	 *            list of paths in cluster 2
	 */
	private static void removeOverlappingGateways(BpmnModel sourceModel,
			ArrayList<SequenceFlow[]> cluster1PathList, ArrayList<SequenceFlow[]> cluster2PathList) {
		Set<FlowObject> objectsToRemove = new LinkedHashSet<FlowObject>();
		for (int i = 0; i < cluster1PathList.size(); i++) {
			SequenceFlow[] cluster1Path = cluster1PathList.get(i);
			SequenceFlow[] cluster2Path = cluster2PathList.get(i);
			for (SequenceFlow sFlow : cluster1Path) {
				objectsToRemove.add(sFlow.getSourceFlowObject());
			}
			for (SequenceFlow sFlow : cluster2Path) {
				objectsToRemove.add(sFlow.getSourceFlowObject());
			}
		}
		for (FlowObject f : objectsToRemove) {
			sourceModel.removeFlowObject(f.getId());
		}
	}

	/**
	 * Are some activities executed for all conditions?
	 * 
	 * @param cluster1PathList
	 *            list of paths in cluster 1
	 * @param cluster1PathConditions
	 *            list of conditions for each path in cluster 1
	 * @param cluster1Children
	 *            children (activities) of cluster 1
	 * @return list of flow objects that are executed regardless of conditions (always true)
	 */
	private static ArrayList<FlowObject> getAlwaysExecutedActivities(
			ArrayList<SequenceFlow[]> cluster1PathList, ArrayList<String> cluster1PathConditions,
			ArrayList<FlowObject> cluster1Children) {
		ArrayList<FlowObject> cluster1ChildrenTrue = new ArrayList<FlowObject>();
		Set<String> allCluster1PathConditions = new LinkedHashSet<String>();
		for (int i = 0; i < cluster1PathConditions.size(); i++) {
			allCluster1PathConditions.add(cluster1PathConditions.get(i));
		}
		for (int c = 0; c < cluster1Children.size(); c++) {

			Set<String> childConditions = new LinkedHashSet<String>();

			FlowObject cluster1Child = cluster1Children.get(c);
			for (int i = 0; i < cluster1PathConditions.size(); i++) {
				SequenceFlow[] cluster1Path = cluster1PathList.get(i);
				int last = cluster1Path.length - 1;
				FlowObject child = cluster1Path[last].getDestinationFlowObject();
				if (!cluster1Child.equals(child)) {
					continue;
				}
				String pathCondition = cluster1PathConditions.get(i);
				childConditions.add(pathCondition);
			}
			Boolean childAlwaysTrue = null;
			for (String cluster1PathCondition : allCluster1PathConditions) {
				if (childConditions.contains(cluster1PathCondition)) {
					if (childAlwaysTrue == null) {
						childAlwaysTrue = true;
					}
				} else {
					childAlwaysTrue = false;
				}
			}
			if (childAlwaysTrue != null && childAlwaysTrue) {
				cluster1ChildrenTrue.add(cluster1Child);
			}
		}
		return cluster1ChildrenTrue;
	}

	/**
	 * returns a list of conditions, one for each activity
	 * 
	 * @param cluster1PathList
	 *            list of paths in cluster 1
	 * @param cluster1PathConditions
	 *            list of conditions for each path in cluster 1
	 * @param cluster1Children
	 *            child nodes (activities) of cluster 1
	 * @return list of conditions for each activity
	 */
	private static ArrayList<String> getConditionsForAllActivities(
			ArrayList<SequenceFlow[]> cluster1PathList, ArrayList<String> cluster1PathConditions,
			ArrayList<FlowObject> cluster1Children) {
		ArrayList<String> cluster1ChildrenConditions;
		cluster1ChildrenConditions = new ArrayList<String>();
		for (int c = 0; c < cluster1Children.size(); c++) {
			FlowObject cluster1Child = cluster1Children.get(c);
			for (int i = 0; i < cluster1PathConditions.size(); i++) {
				SequenceFlow[] cluster1Path = cluster1PathList.get(i);
				int last = cluster1Path.length - 1;
				FlowObject child = cluster1Path[last].getDestinationFlowObject();
				if (!cluster1Child.equals(child)) {
					continue;
				}
				String pathCondition = cluster1PathConditions.get(i);
				String childCondition = "";

				if (pathCondition.isEmpty()) {
					childCondition = "";
				} else if (cluster1ChildrenConditions.size() <= c) {
					childCondition = pathCondition;
				} else if (!cluster1ChildrenConditions.get(c).isEmpty()) {
					childCondition = cluster1ChildrenConditions.get(c);
					if (!pathCondition.equals(childCondition)) {
						if (!childCondition.startsWith("(")) {
							childCondition = "(" + childCondition + ")";
						}
						childCondition += " || (" + pathCondition + ")";
					}
				} else if (cluster1ChildrenConditions.get(c).isEmpty()) {
					childCondition = "";
				}
				cluster1ChildrenConditions.add(c, childCondition);
			}
		}
		return cluster1ChildrenConditions;
	}

	/**
	 * Get all child objects of cluster 1 and all parent objects of cluster two. All these objects are
	 * activities between the two clusters.
	 * 
	 * @param cluster1PathList
	 *            list of all paths in cluster 1
	 * @param cluster2ParentNode
	 *            list of parent nodes for cluster 2
	 * @param cluster1Children
	 *            list of child nodes for cluster 1
	 * @param cluster2Parents
	 *            list of parent nodes for cluster 2
	 */
	private static void getCluster1ChildrenCluster2Parents(ArrayList<SequenceFlow[]> cluster1PathList,
			ArrayList<FlowObject> cluster2ParentNode, ArrayList<FlowObject> cluster1Children,
			ArrayList<FlowObject> cluster2Parents) {
		for (int i = 0; i < cluster1PathList.size(); i++) {
			SequenceFlow[] cluster1Path = cluster1PathList.get(i);
			// SequenceFlow[] cluster2Path = cluster2PathList.get(i);
			FlowObject cluster1Child;
			FlowObject cluster2Parent;
			int last = cluster1Path.length - 1;
			cluster1Child = cluster1Path[last].getDestinationFlowObject();
			cluster2Parent = cluster2ParentNode.get(i);
			if (!cluster1Children.contains(cluster1Child)) {
				cluster1Children.add(cluster1Child);
				cluster2Parents.add(cluster2Parent);
			}
		}
	}

	/**
	 * Get conditions for all paths in cluster 1
	 * 
	 * @param sourceModel
	 *            BPMN model
	 * @param cluster1PathList
	 *            list of paths in cluster 1
	 * @param cluster1PathConditions
	 *            list of conditions for each path in cluster 1
	 */
	private static void getCluster1PathConditions(BpmnModel sourceModel,
			ArrayList<SequenceFlow[]> cluster1PathList, ArrayList<String> cluster1PathConditions) {
		for (int i = 0; i < cluster1PathList.size(); i++) {
			SequenceFlow[] cluster1Path = cluster1PathList.get(i);
			String condition = "";
			for (SequenceFlow sFlow : cluster1Path) {
				String sFlowCondition = "";
				if (sFlow.isDefaultFlow()) {
					FlowObject source = sFlow.getSourceFlowObject();
					for (SequenceFlow sameSourceFlow : sourceModel.getSequenceFlows(source, true)) {
						if (sFlow.equals(sameSourceFlow))
							continue;
						if (sameSourceFlow.getCondition() != null) {
							if (!sFlowCondition.equals(""))
								sFlowCondition += " && ";
							sFlowCondition += "!(" + sameSourceFlow.getCondition() + ")";
						}
					}
				} else {
					sFlowCondition = sFlow.getCondition();
				}
				if (sFlowCondition == null)
					continue;
				if (!condition.trim().isEmpty()) {
					if (!condition.startsWith("(")) {
						condition = "(" + condition + ")";
					}
					condition += " && (" + sFlowCondition + ")";
				} else {
					condition += sFlowCondition;
				}
			}
			cluster1PathConditions.add(i, condition);
		}
	}

	/**
	 * Check if all cluster 2 paths end in the same object. In this case, the last sequence flow of
	 * cluster 2 is returned. If this is not the case, return null.
	 * 
	 * @param cluster2PathList
	 *            list of paths in cluster 2
	 * @return last sequence flow of cluster 2 or null if cluster 2 end undefined
	 */
	private static SequenceFlow checkCluster2PathsEndObject(ArrayList<SequenceFlow[]> cluster2PathList) {
		SequenceFlow lastFlow = null;
		Gateway cluster2LastGateway = null;
		for (int i = 0; i < cluster2PathList.size(); i++) {
			SequenceFlow[] cluster2Path = cluster2PathList.get(i);
			lastFlow = cluster2Path[cluster2Path.length - 1];
			if (cluster2LastGateway != null && cluster2LastGateway != lastFlow.getSourceFlowObject()) {
				return null;
			}
		}
		return lastFlow;
	}

	/**
	 * Find join gateway(s) with equal types for a given list of paths that consist of Gateways. Get
	 * parent nodes for each possible path (join).
	 * 
	 * @param sourceModel
	 *            BPMN model
	 * @param cluster1PathList
	 *            list of paths with gateways - search matching gateways for these paths
	 * @param cluster2ParentNode
	 *            parent nodes for all paths in second cluster
	 * @param cluster2PathList
	 *            list of paths with gateways from second cluster
	 */
	private static boolean getCluster2(BpmnModel sourceModel,
			ArrayList<SequenceFlow[]> cluster1PathList, ArrayList<FlowObject> cluster2ParentNode,
			ArrayList<SequenceFlow[]> cluster2PathList) {
		for (int i = 0; i < cluster1PathList.size(); i++) {
			SequenceFlow[] cluster1Path = cluster1PathList.get(i);
			FlowObject activity = cluster1Path[cluster1Path.length - 1].getDestinationFlowObject();
			// find a the next gateway and look for a join gateway
			SequenceFlow[] cluster2Path = null;
			FlowObject[] nextGatewayt = Utilities.getNextGateway(sourceModel, activity);
			Gateway nextGateway;
			FlowObject nextGatewayParent = null;
			ArrayList<Gateway> visitedGateways = new ArrayList<Gateway>();
			while (nextGatewayt != null && cluster2Path == null) {
				nextGateway = (Gateway) nextGatewayt[0];
				nextGatewayParent = nextGatewayt[1];
				if (visitedGateways.contains(nextGateway)) {
					// loop detected, abort.
					return false;
				}
				visitedGateways.add(nextGateway);
				// check if the structure is the same as the Split
				ArrayList<SequenceFlow[]> joinPathList;
				joinPathList = Utilities.getGatewayCluster1Paths(sourceModel, nextGateway);
				cluster2Path = matchGatewayJoin(sourceModel, cluster1Path, joinPathList);
				nextGatewayt = Utilities.getNextGateway(sourceModel, nextGateway);
			}
			if (cluster2Path != null) {
				cluster2Path = Arrays.copyOf(cluster2Path, cluster1Path.length);
				cluster2PathList.add(i, cluster2Path);
				cluster2ParentNode.add(i, nextGatewayParent);
			} else {
				// no cluster2Path found, abort.
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares (matches) the given gateway and its children with the given path. Used to check if the
	 * given Gateway (and children) can be consolidated to a Join.
	 * 
	 * @param sourceModel
	 *            BPMN model
	 * @param cluster1Path
	 *            path in cluster 1 where matching path is searched
	 * @param cluster2PathList
	 *            list of all paths in cluster 2
	 * @return path in cluster 2 if path matches, otherwise null
	 */
	private static SequenceFlow[] matchGatewayJoin(BpmnModel sourceModel, SequenceFlow[] cluster1Path,
			ArrayList<SequenceFlow[]> cluster2PathList) {
		for (SequenceFlow[] joinPath : cluster2PathList) {
			boolean pathMatches = false;
			for (int i = 1; i <= cluster1Path.length; i++) {
				SequenceFlow splitFlow = cluster1Path[cluster1Path.length - i];
				if (joinPath.length < i) {
					pathMatches = false;
					break;
				}
				SequenceFlow joinFlow = joinPath[i - 1];
				Gateway gatewaySplit = (Gateway) splitFlow.getSourceFlowObject();
				Gateway gatewayJoin = (Gateway) joinFlow.getSourceFlowObject();
				// the type (exclusive, parallel) of gatewaySplit gatewayJoin
				// must be equal
				pathMatches = true;
				if (!gatewaySplit.getType().equals(gatewayJoin.getType())) {
					pathMatches = false;
					break;
				}
				if (gatewaySplit instanceof GatewaySplit && gatewayJoin instanceof GatewaySplit) {
					pathMatches = false;
					break;
				}
				if (gatewaySplit instanceof GatewayJoin && gatewayJoin instanceof GatewayJoin) {
					pathMatches = false;
					break;
				}
			}
			if (pathMatches) {
				return joinPath;
			}
		}
		return null;
	}

}
