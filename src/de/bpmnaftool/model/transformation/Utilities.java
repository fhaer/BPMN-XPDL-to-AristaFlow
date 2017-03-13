package de.bpmnaftool.model.transformation;

import java.util.ArrayList;
import java.util.Arrays;

import de.bpmnaftool.control.BpmnAfToolImpl;
import de.bpmnaftool.model.aristaflow.graph.node.Node;
import de.bpmnaftool.model.bpmn.BpmnModel;
import de.bpmnaftool.model.bpmn.connectingobject.MessageFlow;
import de.bpmnaftool.model.bpmn.connectingobject.SequenceFlow;
import de.bpmnaftool.model.bpmn.flowobject.Event;
import de.bpmnaftool.model.bpmn.flowobject.FlowObject;
import de.bpmnaftool.model.bpmn.flowobject.Gateway;
import de.bpmnaftool.model.bpmn.flowobject.GatewaySplit;
import de.bpmnaftool.model.bpmn.swimlane.Lane;
import de.bpmnaftool.model.bpmn.swimlane.Pool;
import de.bpmnaftool.model.bpmn.swimlane.SwimLane;
import de.bpmnaftool.model.transformation.NotTransformableException.CauseType;


/**
 * This class contains static helper methods which are used for the transformation, but do not transform
 * anything by itself.
 * 
 * @author Felix Härer
 * 
 */
public class Utilities {

	/**
	 * Calculates current transformation progress and sets it in the View
	 * 
	 * @param model
	 *            Model that is being transformed
	 * @param currentPool
	 *            pool that is currently transformed
	 */
	static void setProgress(BpmnModel model, Pool currentPool) {
		int i = 0;
		for (Pool pool : model.getPools()) {
			i++;
			if (pool.equals(currentPool))
				break;
		}
		double progress = (double) i / (model.getPools().length + 1);
		int percentage = (int) (progress * 100);
		BpmnAfToolImpl.getInstance().getView().setTransformProgress(percentage);
	}

	/**
	 * Retrieves all conditions of the outgoing SequenceFlows of the given GatewaySplit and stores them
	 * in a string array. If outgoing SequenceFlows of an exclusive split have no conditions, it creates
	 * them (integer numbers).
	 * 
	 * @param bpmnModel
	 *            model where Gateway is located
	 * @param gateway
	 *            Gateway where conditions are to be retrieved
	 * @return string array with conditions
	 */
	static String[] getConditions(BpmnModel bpmnModel, GatewaySplit gateway) {
		FlowObject[] gatewayChildren = bpmnModel.getChildren(gateway);
		String[] conditions = new String[gatewayChildren.length];
		for (int i = 0; i < conditions.length; i++) {
			SequenceFlow sf = bpmnModel.getSequenceFlow(gateway, gatewayChildren[i]);
			if (sf.getCondition() == null || sf.getCondition().isEmpty())
				throw new IllegalArgumentException("Bedingung fehlt: " + sf);
			else
				conditions[i] = sf.getCondition();
		}
		return conditions;
	}

	/**
	 * Returns the prefix for a FlowObject. The prefix is a short version of the pool/lane name and
	 * optionally a prefix for the specific node type (e.g. special prefix for Event).
	 * 
	 * @param object
	 *            object to get prefix for
	 * @return short name of pool and lane
	 */
	static String getNodeName(BpmnModel model, FlowObject object) {
		Lane lane = object.getLane();
		Pool pool = lane.getPool();
		String name = pool.getName();
		String lanePrefix = lane.getName();
		// do not short name IF object is a StartEvent
		if (!(object instanceof Event && ((Event) object).getType() == Event.Type.StartEvent)) {
			name = getSwimLanePrefix(pool, model.getPools());
			lanePrefix = getSwimLanePrefix(lane, model.getLanes(pool));
		}
		if (!lanePrefix.isEmpty())
			name = name + "|" + lanePrefix;
		if (!name.isEmpty())
			name = "[" + name + "] ";
		if (object instanceof Event) {
			Event event = (Event) object;
			if (event.isThrowingEvent() != null) {
				if (event.isThrowingEvent())
					name += BpmnTransformation.throwingEventPrefix;
				else
					name += BpmnTransformation.catchingEventPrefix;
			}
			if (object.getName().isEmpty()) {
				if (event.getType().equals(Event.Type.StartEvent))
					name += "Start Event";
				else if (event.getType().equals(Event.Type.EndEvent))
					name += "End Event";
				else
					name += "Event";
			}
		}
		name += object.getName();
		return name;
	}

	/**
	 * Returns a shortened version of a Pool or lane name. A short name uses only the minimum number of
	 * characters needed to produce different names.
	 * 
	 * @param swimLane
	 *            SwimLane whose name is to be shortened
	 * @param swimLanes
	 *            Array of all existing SwimLanes for name comparison
	 * @return shortened name OR empty String if only one SwimLane
	 */
	static String getSwimLanePrefix(SwimLane swimLane, SwimLane[] swimLanes) {
		String name = swimLane.getName();
		String shortName = "";
		for (SwimLane swimLaneB : swimLanes) {
			if (swimLaneB == swimLane)
				continue;
			for (int c = shortName.length(); c < name.length(); c++) {
				int lenB = c;
				if (c > swimLaneB.getName().length())
					lenB = swimLaneB.getName().length();
				if (name.startsWith(swimLaneB.getName().substring(0, lenB)))
					shortName = name.substring(0, c + 1);
			}
		}
		return shortName;
	}

	/**
	 * Returns true if the given object is and EndEvent and does not have any incoming or outgoing
	 * MessageFlows.
	 * 
	 * @param model
	 *            Model where object is located
	 * @param object
	 *            object to check
	 * @return true if object is an EndEvent without MessageFlows
	 */
	static boolean isEndEventWithoutMessageFlows(BpmnModel model, FlowObject object) {
		if (!isEndEvent(object))
			return false;
		for (MessageFlow mf : model.getMessageFlows()) {
			FlowObject dest = mf.getDestinationFlowObject();
			FlowObject src = mf.getSourceFlowObject();
			if (dest != null && dest.equals(object))
				return false;
			if (src != null && src.equals(object))
				return false;
		}
		return true;
	}

	/**
	 * Returns true if the given object is an EndEvent
	 * 
	 * @param object
	 *            object to check
	 * @return true if object is EndEvent
	 */
	static boolean isEndEvent(FlowObject object) {
		if (object instanceof Event) {
			Event.Type type = ((Event) object).getType();
			if (type.equals(Event.Type.EndEvent)) {
				return true;
			}
		}
		return false;
	}

	static FlowObject getLoopEnd(FlowObject[] parents, Node parentA, Node parentB, Node lastAfNode,
			FlowObject loopStart) {

		// one parent is already transformed, other one is loop end

		if (parentA != null && parentA.equals(lastAfNode)) {
			return parents[1];
		}
		if (parentB != null && parentB.equals(lastAfNode)) {
			return parents[0];
		}
		throw new NotTransformableException(CauseType.NoGatewaySplit, "Split nicht gefunden", loopStart);
	}

	/**
	 * Traverses the graph beginning with the given object and returns the next Gateway and its parent
	 * 
	 * @param sourceModel
	 *            model where traversal happens
	 * @param fObject
	 *            object where traversal starts
	 * @return array where index 0 is the Gateway and index 1 its parent
	 */
	static FlowObject[] getNextGateway(BpmnModel sourceModel, FlowObject fObject) {
		FlowObject[] gatewayAndParent = null;
		for (FlowObject child : sourceModel.getChildren(fObject)) {
			if (child instanceof Gateway) {
				gatewayAndParent = new FlowObject[2];
				gatewayAndParent[0] = child;
				gatewayAndParent[1] = fObject;
				break;
			} else {
				return getNextGateway(sourceModel, child);
			}
		}
		return gatewayAndParent;
	}

	/**
	 * This method traverses all possible paths starting from Gateway and ending with an activity (first
	 * one found). During the traversal, all sequence flows are stored (the Path from Gateway to any
	 * activity). All Paths are stored in a list.
	 * 
	 * @param sourceModel
	 *            BpmnModel where traversal happens (graph)
	 * @param gateway
	 *            Gateway to start with (start node)
	 * @return All possible paths to all activities (path as list of Sequence Flows)
	 */
	static ArrayList<SequenceFlow[]> getGatewayCluster1Paths(BpmnModel sourceModel, Gateway gateway) {
		ArrayList<SequenceFlow[]> pathList = new ArrayList<SequenceFlow[]>();
		SequenceFlow[] currentPath = new SequenceFlow[0];
		getPathsToAllActivities(sourceModel, gateway, currentPath, pathList);
		return pathList;
	}

	/**
	 * This is a helper method to find all paths to activities starting from a Gateway. For further
	 * information see the method getPathsToAllActivities(BpmnModel sourceModel, Gateway gateway)
	 * 
	 * @param sourceModel
	 *            BpmnModel where traversal happens (graph)
	 * @param gateway
	 *            Gateway to start with (start node)
	 * @param path
	 *            Current Path (recursion)
	 * @param pathList
	 *            All possible paths to all activities (path as list of Sequence Flows)
	 */
	private static void getPathsToAllActivities(BpmnModel sourceModel, Gateway gateway,
			SequenceFlow[] path, ArrayList<SequenceFlow[]> pathList) {
		FlowObject[] children = sourceModel.getChildren(gateway);
		// for each child: copy previous path and create new list
		for (FlowObject fObject : children) {
			SequenceFlow sequenceFlow = sourceModel.getSequenceFlow(gateway, fObject);
			// loop detection: stop if sequence flow is already in list
			for (SequenceFlow s : path) {
				if (s.equals(sequenceFlow))
					return;
			}
			// add sequence flow to path list
			SequenceFlow[] newPath = Arrays.copyOf(path, path.length + 1);
			newPath[path.length] = sequenceFlow;
			// create a new path if this is a split
			if (fObject instanceof Gateway) {
				getPathsToAllActivities(sourceModel, (Gateway) fObject, newPath, pathList);
			} else {
				pathList.add(newPath);
			}
		}
	}

	/**
	 * Check if the parent of Gateway is a gateway itself. If this is the case, it returns false,
	 * otherwise true.
	 * 
	 * @param sourceModel
	 *            Model where overlapping Gateway are to be resolved
	 * @param splitGateway
	 *            Gateway to resolve
	 * @return true if parent it not a gateway, false otherwise
	 */
	static boolean checkGatewayParents(BpmnModel sourceModel, Gateway splitGateway) {

		boolean hasParent = false;
		for (FlowObject parent : sourceModel.getParents(splitGateway)) {
			if (parent instanceof Gateway) {
				return false;
			} else {
				hasParent = true;
			}
		}
		if (!hasParent) {
			return false;
		}
		return true;
	}

}
