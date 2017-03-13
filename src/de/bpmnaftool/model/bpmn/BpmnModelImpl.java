package de.bpmnaftool.model.bpmn;

import java.util.ArrayList;
import java.util.UUID;

import de.bpmnaftool.model.bpmn.connectingobject.*;
import de.bpmnaftool.model.bpmn.flowobject.*;
import de.bpmnaftool.model.bpmn.swimlane.*;

/**
 * Implementation of BpmnModel
 * 
 * @author Felix Härer
 */
public class BpmnModelImpl implements BpmnModel {

	/**
	 * @see BpmnModel
	 */
	protected final String id;
	/**
	 * @see BpmnModel
	 */
	protected String name;

	/**
	 * list of all pools in the model
	 */
	protected ArrayList<Pool> pools;
	/**
	 * list of all lanes in the model
	 */
	protected ArrayList<Lane> lanes;
	/**
	 * list of all flow objects in the model
	 */
	protected ArrayList<FlowObject> flowObjects;
	/**
	 * list of all sequence flows in the model
	 */
	protected ArrayList<SequenceFlow> sequenceFlows;
	/**
	 * list of all message flows in the model
	 */
	protected ArrayList<MessageFlow> messageFlows;

	/**
	 * Creates a new BpmnModel with random ID.
	 */
	public BpmnModelImpl() {
		this.id = UUID.randomUUID().toString();
		pools = new ArrayList<Pool>();
		lanes = new ArrayList<Lane>();
		flowObjects = new ArrayList<FlowObject>();
		sequenceFlows = new ArrayList<SequenceFlow>();
		messageFlows = new ArrayList<MessageFlow>();
	}

	@Override
	public void addConnectingObject(ConnectingObject connectingObject) {
		if (connectingObject == null)
			throw new IllegalArgumentException("connectingObject ist null");
		if (connectingObject.getId().isEmpty())
			throw new IllegalArgumentException("connectingObject hat keine ID " + connectingObject);
		if (connectingObject instanceof MessageFlow)
			addConnectingObject((MessageFlow) connectingObject);
		else if (connectingObject instanceof SequenceFlow)
			addConnectingObject((SequenceFlow) connectingObject);
		else
			throw new IllegalArgumentException("unbekanntes connecting object");
	}

	/**
	 * @see BpmnModel
	 * @param messageFlow
	 *            flow message flow to add
	 */
	public void addConnectingObject(MessageFlow messageFlow) {
		if (messageFlows == null)
			throw new IllegalStateException("message flows Datenstruktur nicht initialisiert");
		for (MessageFlow m : messageFlows)
			if (messageFlow.getId().equals(m.getId()))
				throw new IllegalArgumentException("ID nicht eindeutig");
		messageFlows.add(messageFlow);
	}

	/**
	 * @see BpmnModel
	 * @param sequenceFlow
	 *            sequence flow to add
	 */
	public void addConnectingObject(SequenceFlow sequenceFlow) {
		if (sequenceFlows == null)
			throw new IllegalStateException("sequence flow Datenstruktur nicht initialisiert");
		for (SequenceFlow s : sequenceFlows)
			if (sequenceFlow.getId().equals(s.getId()))
				throw new IllegalArgumentException("ID nicht eindeutig");
		sequenceFlows.add(sequenceFlow);
	}

	@Override
	public void addFlowObject(FlowObject flowObject) {
		if (flowObject == null)
			throw new IllegalArgumentException("flow object ist null");
		if (flowObjects == null)
			throw new IllegalStateException("flow object Datenstruktur nicht initialisiert");
		if (flowObject.getId().isEmpty())
			throw new IllegalArgumentException("flowObject hat keine ID " + flowObject);
		for (FlowObject f : flowObjects)
			if (flowObject.getId().equals(f.getId()))
				throw new IllegalArgumentException("ID nicht eindeutig");
		flowObjects.add(flowObject);
	}

	@Override
	public void addSwimLane(SwimLane swimLane) {
		if (swimLane == null)
			throw new IllegalArgumentException("swimLane ist null");
		if (swimLane.getId().isEmpty())
			throw new IllegalArgumentException("swimLane hat keine ID " + swimLane);
		if (swimLane instanceof Pool)
			addSwimLane((Pool) swimLane);
		else if (swimLane instanceof Lane)
			addSwimLane((Lane) swimLane);
		else
			throw new IllegalArgumentException("ubekannte swimLane");
	}

	/**
	 * adds a swimlane, see method public void addSwimLane(SwimLane swimLane)
	 * 
	 * @param pool
	 *            Pool where swimlane is contained
	 */
	public void addSwimLane(Pool pool) {
		if (pools == null)
			throw new IllegalStateException("pools Datenstruktur nicht initialisiert");
		for (Pool p : pools)
			if (pool.getId().equals(p.getId()))
				throw new IllegalArgumentException("ID nicht eindeutig");
		pools.add(pool);
	}

	/**
	 * adds a swimlane, see method public void addSwimLane(SwimLane swimLane)
	 * 
	 * @param lane
	 *            lane where swimlane is contained
	 */
	public void addSwimLane(Lane lane) {
		if (lanes == null)
			throw new IllegalStateException("lanes Datenstruktur nicht initialisiert");
		for (Lane l : lanes)
			if (lane.getId().equals(l.getId()))
				throw new IllegalArgumentException("ID nicht eindeutig");
		lanes.add(lane);
	}

	@Override
	public FlowObject[] getChildren(FlowObject flowObject) {
		if (flowObject == null)
			throw new IllegalArgumentException("flow object ist null");
		if (flowObjects == null)
			throw new IllegalStateException("flow objects Datenstruktur nicht initialisiert");

		ArrayList<FlowObject> children = new ArrayList<FlowObject>();

		// return default flow as last child
		SequenceFlow defaultFlow = null;
		for (SequenceFlow sFlow : sequenceFlows) {
			if (sFlow.getSourceFlowObject().equals(flowObject)) {
				if (sFlow.isDefaultFlow()) {
					defaultFlow = sFlow;
				} else {
					children.add(sFlow.getDestinationFlowObject());
				}
			}
		}
		if (defaultFlow != null) {
			children.add(defaultFlow.getDestinationFlowObject());
		}

		return (FlowObject[]) children.toArray(new FlowObject[0]);
	}

	@Override
	public FlowObject[] getParents(FlowObject flowObject) {
		if (flowObject == null)
			throw new IllegalArgumentException("flow object ist null");
		if (flowObjects == null)
			throw new IllegalStateException("flow objects Datenstruktur nicht initialisiert");

		ArrayList<FlowObject> parents = new ArrayList<FlowObject>();

		for (SequenceFlow sFlow : sequenceFlows) {
			if (sFlow.getDestinationFlowObject().equals(flowObject)) {
				parents.add(sFlow.getSourceFlowObject());
			}
		}

		return (FlowObject[]) parents.toArray(new FlowObject[0]);
	}

	@Override
	public SequenceFlow getSequenceFlow(FlowObject source, FlowObject destination) {
		if (source == null)
			throw new IllegalArgumentException("Quelle des SequenceFlow ist null");
		if (destination == null)
			throw new IllegalArgumentException("Ziel des SequenceFlow ist null");
		if (sequenceFlows == null)
			throw new IllegalStateException("sequence flows Datenstruktur nicht initialisiert");

		for (SequenceFlow sFlow : sequenceFlows) {
			if (sFlow.getSourceFlowObject().equals(source)
					&& sFlow.getDestinationFlowObject().equals(destination)) {
				return sFlow;
			}
		}
		return null;
	}

	@Override
	public Pool[] getPools() {
		if (pools == null) {
			throw new IllegalStateException("pool data structure nicht initialisiert");
		}
		return (Pool[]) pools.toArray(new Pool[0]);
	}

	@Override
	public Pool getPool(String id) {
		for (Pool pool : pools) {
			if (pool.getId().equals(id)) {
				return pool;
			}
		}
		throw new IllegalArgumentException("Pool mit angegebener ID nicht gefunden: " + id);
	}

	@Override
	public Lane[] getLanes(Pool pool) {
		if (lanes == null) {
			throw new IllegalStateException("lanes data structure nicht initialisiert");
		}
		ArrayList<Lane> lanesInPool = new ArrayList<Lane>();
		for (Lane lane : lanes) {
			if (lane.getPool().equals(pool)) {
				lanesInPool.add(lane);
			}
		}
		return (Lane[]) lanesInPool.toArray(new Lane[0]);
	}

	@Override
	public Lane getLane(String id) {
		for (Lane lane : lanes) {
			if (lane.getId().equals(id)) {
				return lane;
			}
		}
		throw new IllegalArgumentException("Lane mit angegebener ID nicht gefunden: " + id);
	}

	@Override
	public String getId() {
		if (id == null)
			return "";
		return id;
	}

	@Override
	public String getName() {
		if (name == null)
			return "";
		return name;
	}

	@Override
	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Name des Modells ist null");
		this.name = name;
	}

	@Override
	public FlowObject[] getFlowObjectsByPool(Pool pool) {
		if (flowObjects == null)
			throw new IllegalStateException("flow objects data structure null");
		ArrayList<FlowObject> poolFlowObjects = new ArrayList<FlowObject>();
		for (FlowObject flowObject : flowObjects) {
			if (flowObject.getLane().getPool().equals(pool)) {
				poolFlowObjects.add(flowObject);
			}
		}
		return (FlowObject[]) poolFlowObjects.toArray(new FlowObject[0]);
	}

	@Override
	public Activity[] getActivities(Pool pool) {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		FlowObject[] poolObjects = getFlowObjectsByPool(pool);
		for (int i = 0; i < poolObjects.length; i++) {
			if (poolObjects[i] instanceof Activity) {
				activities.add((Activity) poolObjects[i]);
			}
		}
		return (Activity[]) activities.toArray(new Activity[0]);
	}

	@Override
	public Event[] getEvents(Pool pool) {
		ArrayList<Event> activities = new ArrayList<Event>();
		FlowObject[] poolObjects = getFlowObjectsByPool(pool);
		for (int i = 0; i < poolObjects.length; i++) {
			if (poolObjects[i] instanceof Event) {
				activities.add((Event) poolObjects[i]);
			}
		}
		return (Event[]) activities.toArray(new Event[0]);
	}

	@Override
	public Gateway[] getGateways(Pool pool) {
		ArrayList<Gateway> gateways = new ArrayList<Gateway>();
		FlowObject[] poolObjects = getFlowObjectsByPool(pool);
		for (int i = 0; i < poolObjects.length; i++) {
			if (poolObjects[i] instanceof Gateway) {
				gateways.add((Gateway) poolObjects[i]);
			}
		}
		return (Gateway[]) gateways.toArray(new Gateway[0]);
	}

	@Override
	public SequenceFlow[] getSequenceFlows(FlowObject node, boolean isSourceNode) {
		if (node == null)
			throw new IllegalArgumentException("node ist null");
		if (sequenceFlows == null)
			throw new IllegalStateException("sequence flows Datenstruktur nicht initialisiert");

		ArrayList<SequenceFlow> sFlowsNode = new ArrayList<SequenceFlow>();

		for (SequenceFlow sFlow : sequenceFlows) {
			FlowObject compareTo = sFlow.getDestinationFlowObject();
			if (isSourceNode) {
				compareTo = sFlow.getSourceFlowObject();
			}
			if (node.equals(compareTo)) {
				sFlowsNode.add(sFlow);
			}
		}
		return (SequenceFlow[]) sFlowsNode.toArray(new SequenceFlow[0]);
	}

	@Override
	public MessageFlow[] getMessageFlows() {
		if (messageFlows == null)
			throw new IllegalStateException("message flows Datenstruktur nicht initialisiert");
		return (MessageFlow[]) messageFlows.toArray(new MessageFlow[0]);
	}

	@Override
	public FlowObject getFlowObject(String id) {
		for (FlowObject flowObject : flowObjects) {
			if (flowObject.getId().equals(id)) {
				return flowObject;
			}
		}
		throw new IllegalArgumentException("Ein FlowObject mit der gesuchten ID existiert nicht: " + id);
	}

	@Override
	public void removeFlowObject(String id) {
		for (FlowObject flowObject : flowObjects) {
			if (flowObject.getId().equals(id)) {
				// remove sequenceflows where flowObject is source or destination
				SequenceFlow[] sourceFlows = getSequenceFlows(flowObject, true);
				SequenceFlow[] destFlows = getSequenceFlows(flowObject, false);
				for (SequenceFlow sFlow : sourceFlows) {
					removeSequenceFlow(sFlow.getId());
				}
				for (SequenceFlow sFlow : destFlows) {
					removeSequenceFlow(sFlow.getId());
				}
				flowObjects.remove(flowObject);
				return;
			}
		}
		throw new IllegalArgumentException("Ein FlowObject mit der gesuchten ID existiert nicht: " + id);
	}

	@Override
	public void removeSwimLane(String id) {
		for (Pool pool : pools) {
			if (pool.getId().equals(id)) {
				if (getLanes(pool).length > 0) {
					throw new IllegalArgumentException("Der zu entfernende Pool ist nicht leer");
				}
				pools.remove(pool);
				return;
			}
		}
		for (Lane lane : lanes) {
			if (lane.getId().equals(id)) {
				for (FlowObject flowObject : flowObjects) {
					if (flowObject.getLane().equals(lane)) {
						throw new IllegalArgumentException("Die zu entfernende Lane ist nicht leer");
					}
				}
				lanes.remove(lane);
				return;
			}
		}
		throw new IllegalArgumentException("Eine SwimLane mit der angegebenen ID existiert nicht " + id);
	}

	@Override
	public void removeMessageFlow(String id) {
		for (MessageFlow mFlow : messageFlows) {
			if (mFlow.getId().equals(id)) {
				messageFlows.remove(mFlow);
				return;
			}
		}
		throw new IllegalArgumentException("Ein SequenceFlow mit der angegebenen ID existiert nicht "
				+ id);
	}

	@Override
	public void removeSequenceFlow(String id) {
		for (SequenceFlow sFlow : sequenceFlows) {
			if (sFlow.getId().equals(id)) {
				sequenceFlows.remove(sFlow);
				return;
			}
		}
		throw new IllegalArgumentException("Ein MessageFlow mit der angegebenen ID existiert nicht "
				+ id);
	}

}
