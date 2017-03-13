package de.bpmnaftool.model.bpmn;

import de.bpmnaftool.control.BpmnAfToolImpl;
import de.bpmnaftool.model.bpmn.connectingobject.MessageFlow;
import de.bpmnaftool.model.bpmn.connectingobject.SequenceFlow;
import de.bpmnaftool.model.bpmn.flowobject.Activity;
import de.bpmnaftool.model.bpmn.flowobject.Event;
import de.bpmnaftool.model.bpmn.flowobject.FlowObject;
import de.bpmnaftool.model.bpmn.flowobject.Gateway;
import de.bpmnaftool.model.bpmn.flowobject.GatewayJoin;
import de.bpmnaftool.model.bpmn.flowobject.GatewaySplit;
import de.bpmnaftool.model.bpmn.swimlane.Pool;
import de.bpmnaftool.model.transformation.NotTransformableException;
import de.bpmnaftool.model.transformation.NotTransformableException.CauseType;

/**
 * Implementation of a BPMN-Validator
 * 
 * @author Felix Härer
 */
public class BpmnValidatorImpl implements BpmnValidator {

	/**
	 * BPMN model to validate
	 */
	private final BpmnModel bpmnModel;

	/**
	 * Creates a validator for a specific BPMN model.
	 * 
	 * @param bpmnModel
	 */
	public BpmnValidatorImpl(BpmnModel bpmnModel) {
		if (bpmnModel == null)
			throw new IllegalArgumentException("Kein BPMN-Modell vorhanden (null)");
		this.bpmnModel = bpmnModel;
	}

	@Override
	public void validateModel() {
		BpmnAfToolImpl.getInstance().log("Validierung des BPMN-Workflowschemas gestartet ...", 0, 2);
		validateSwimLanes();
		for (Pool pool : bpmnModel.getPools()) {
			validateFlowObjects(pool);
			validateConnectingObjects(pool);
		}
		BpmnAfToolImpl.getInstance().log("BPMN-Workflowschema valide", 0, 2);
	}

	@Override
	public void validateSwimLanes() {
		BpmnAfToolImpl.getInstance().log("Validiere Swimlanes ...", 0, 1);
		Pool[] pools = bpmnModel.getPools();
		if (pools.length < 1)
			throw new NotTransformableException(CauseType.NoSwimLanes);
		boolean onlyBlackboxPools = true;
		for (Pool pool : pools) {
			BpmnAfToolImpl.getInstance().log("Pool: " + pool, 1, 1);
			boolean isBlackbox = true;
			Event[] startEndEvents = validateStartEndEvent(pool);
			// start/end events found, so not just black box pool
			if (startEndEvents[0] != null || startEndEvents[1] != null) {
				BpmnAfToolImpl.getInstance().log("Pool ist keine Black Box", 2, 1);
				isBlackbox = false;
				onlyBlackboxPools = false;
			} else {
				BpmnAfToolImpl.getInstance().log("Pool ist eine Black Box", 2, 1);
			}
			// if not black box: at least one lane
			if (!isBlackbox && bpmnModel.getLanes(pool).length < 1) {
				BpmnAfToolImpl.getInstance().log("Pool enthält keine Lane", 2, 1);
				throw new NotTransformableException(CauseType.NoSwimLanes, pool,
						"Jeder Pool, der keine Black Box ist, muss mindestens eine Lane enthalten");
			}
		}
		if (onlyBlackboxPools) {
			throw new NotTransformableException(CauseType.NoSwimLanes,
					"Keine Swimlane mit Start- oder End-Event gefunden");
		} else {
			BpmnAfToolImpl.getInstance().log("Swimlanes valide", 1, 1);
		}
	}

	@Override
	public void validateConnectingObjects(Pool pool) {
		BpmnAfToolImpl.getInstance().log("Validiere Connecting Objects in Pool " + pool + " ...", 0, 1);
		for (FlowObject flowObject : bpmnModel.getFlowObjectsByPool(pool)) {
			SequenceFlow[] out = bpmnModel.getSequenceFlows(flowObject, true);
			for (SequenceFlow sFlow : out) {
				validateSequenceFlow(flowObject, out, sFlow);
			}
			for (MessageFlow mFlow : bpmnModel.getMessageFlows()) {
				validateMessageFlow(mFlow);
			}
		}
	}

	/**
	 * Validates a Message Flow, checks if source and destination pool differ
	 * 
	 * @param mFlow
	 *            Message Flow to validate
	 */
	private void validateMessageFlow(MessageFlow mFlow) {
		Pool sourcePool;
		Pool destinationPool;
		if (mFlow.sourceIsPool())
			sourcePool = mFlow.getSourcePool();
		else
			sourcePool = mFlow.getSourceFlowObject().getLane().getPool();
		if (mFlow.destinationIsPool())
			destinationPool = mFlow.getDestinationPool();
		else
			destinationPool = mFlow.getDestinationFlowObject().getLane().getPool();
		if (sourcePool.equals(destinationPool)) {
			throw new NotTransformableException("Message Flow mit gleichem Start/Ziel-Pool gefunden: "
					+ mFlow);
		}
	}

	/**
	 * Validates a Sequence Flow, checks conditions and if there are multiple flows between two objects.
	 * 
	 * @param flowObject
	 *            Flow Object which is the source of sFlow
	 * @param out
	 *            array with all outgoing sequence flows form flowObject
	 * @param sFlow
	 *            Sequence Flow to validate
	 */
	private void validateSequenceFlow(FlowObject flowObject, SequenceFlow[] out, SequenceFlow sFlow) {
		boolean conditionNeeded = false;
		if (flowObject instanceof GatewaySplit) {
			GatewaySplit gateway = (GatewaySplit) flowObject;
			if (gateway.getType() != Gateway.Type.Parallel)
				conditionNeeded = true;
		}
		if (conditionNeeded && sFlow.getCondition() == null)
			throw new NotTransformableException("Sequence Flow an Gateway ohne Bedingung " + sFlow);
		if (!conditionNeeded && sFlow.getCondition() != null)
			throw new NotTransformableException("Bedingung für Sequence Flow ohne Gateway " + sFlow);
		if (sFlow.getSourceFlowObject().equals(sFlow.getDestinationFlowObject()))
			throw new NotTransformableException("Sequence Flow besitzt gleiches Start/Ziel " + sFlow);

		FlowObject destination = sFlow.getDestinationFlowObject();
		for (SequenceFlow sFlowB : out) {
			if (sFlow.equals(sFlowB))
				continue;
			if (destination.equals(sFlowB.getDestinationFlowObject())) {
				throw new NotTransformableException(CauseType.Unknown,
						"Zwei Sequence Flows mit gleichem Start/Ziel ausgehend von " + destination
								+ "gefunden: " + sFlow + " und " + sFlowB);
			}
		}
	}

	@Override
	public void validateFlowObjects(Pool pool) {
		BpmnAfToolImpl.getInstance().log("Validiere Flow Objects in Pool " + pool + " ...", 0, 1);
		validateStartEndEvent(pool);
		// gateways: check for unsupported objects and cardinalities
		for (FlowObject flowObject : bpmnModel.getFlowObjectsByPool(pool)) {
			BpmnAfToolImpl.getInstance().log("Validiere Flow Object " + flowObject, 2, 1);
			if (flowObject instanceof Gateway) {
				validateGateway((Gateway) flowObject);
			} else if (flowObject instanceof Activity) {
				assertNSequenceFlows(flowObject, 1, 1, 1, 1);
			} else if (flowObject instanceof Event) {
				validateEvent((Event) flowObject);
			} else {
				throw new NotTransformableException(CauseType.UnsupportedObject, flowObject);
			}
		}
	}

	/**
	 * Validates an Event with checks for type and number of allowed incoming/outgoing sequence flows.
	 * 
	 * @param event
	 *            Event to validate
	 */
	private void validateEvent(Event event) {
		if (event.getType() == Event.Type.StartEvent)
			assertNSequenceFlows(event, 0, 0, 1, 1);
		else if (event.getType() == Event.Type.EndEvent)
			assertNSequenceFlows(event, 1, 1, 0, 0);
		else if (event.getType() == Event.Type.IntermediateEvent)
			assertNSequenceFlows(event, 1, 1, 1, 1);
		else
			throw new NotTransformableException(CauseType.UnsupportedObject, event);
	}

	/**
	 * Validates a Gateway with checks for type and number of allowed incoming/outgoing sequence flows.
	 * 
	 * @param gateway
	 *            gateway to validate
	 */
	private void validateGateway(Gateway gateway) {
		if (gateway instanceof GatewaySplit)
			assertNSequenceFlows(gateway, 1, 1, 2, Integer.MAX_VALUE);
		else if (gateway instanceof GatewayJoin)
			assertNSequenceFlows(gateway, 2, Integer.MAX_VALUE, 1, 1);
		else
			throw new NotTransformableException(CauseType.UnsupportedObject,
					"Gateway weder Verzweigung noch Zusammenführung", gateway);
		if (gateway.getType() == Gateway.Type.Complex)
			throw new NotTransformableException(CauseType.UnsupportedObject, "Complex Gateway gefunden",
					gateway);
		if (gateway.getType() == Gateway.Type.Unknown)
			throw new NotTransformableException(CauseType.UnsupportedObject, "Gateway-Typ unbekannt",
					gateway);
	}

	/**
	 * Throws an exception if the number of incoming/outgoing sequence flows of a FlowObject is not as
	 * specified.
	 * 
	 * @param flowObject
	 *            FlowObject to be checked for SequenceFlows
	 * @param minIn
	 *            minimum number of incoming sequence flows
	 * @param maxIn
	 *            maximum number of incoming sequence flows
	 * @param minOut
	 *            minimum number of outgoing sequence flows
	 * @param maxOut
	 *            maximum number of outgoing sequence flows
	 */
	private void assertNSequenceFlows(FlowObject flowObject, int minIn, int maxIn, int minOut, int maxOut) {
		int nIncoming = bpmnModel.getSequenceFlows(flowObject, false).length;
		int nOutgoing = bpmnModel.getSequenceFlows(flowObject, true).length;
		String in = nIncoming + " gefunden, aber nur " + minIn + "-" + maxIn + " erlaubt";
		if (nIncoming < minIn || nIncoming > maxIn)
			throw new NotTransformableException(CauseType.IncomingSequenceFlows, in, flowObject);
		String out = nOutgoing + " gefunden, aber nur " + minOut + "-" + maxOut + " erlaubt";
		if (nOutgoing < minOut || nOutgoing > maxOut)
			throw new NotTransformableException(CauseType.OutgoingSequenceFlows, out, flowObject);
	}

	/**
	 * Returns start and end event of a pool. Also checks if there is one and only one start end end
	 * event.
	 * 
	 * @param pool
	 *            pool where to look for evens
	 * @return array with start event at index 0, end event at index 1
	 */
	private Event[] validateStartEndEvent(Pool pool) {
		BpmnAfToolImpl.getInstance().log("Suche nach Start Events und End Events ...", 2, 1);
		int startEvents = 0, endEvents = 0;
		Event[] events = new Event[2];
		for (Event event : bpmnModel.getEvents(pool)) {
			if (event.getType() == Event.Type.StartEvent) {
				BpmnAfToolImpl.getInstance().log("Start Event gefunden " + event, 2, 1);
				events[0] = event;
				startEvents++;
			}
			if (event.getType() == Event.Type.EndEvent) {
				BpmnAfToolImpl.getInstance().log("End Event gefunden " + event, 2, 1);
				events[1] = event;
				endEvents++;
			}
		}
		// check for number of start/end events (0 is allowed => black box)
		if (startEvents > 1)
			throw new NotTransformableException(CauseType.StartEventCount, pool,
					"Mehr als ein Start Event gefunden");
		if (endEvents > 1)
			throw new NotTransformableException(CauseType.EndEventCount, pool,
					"Mehr als ein End Event gefunden");
		if (startEvents == 0 && endEvents > 0)
			throw new NotTransformableException(CauseType.EndEventCount, pool,
					"Kein Start Event, aber mindestens ein End Event gefunden");
		if (startEvents > 0 && endEvents == 0)
			throw new NotTransformableException(CauseType.EndEventCount, pool,
					"Kein End Event, aber mindestens ein Start Event gefunden");
		return events;
	}
}
