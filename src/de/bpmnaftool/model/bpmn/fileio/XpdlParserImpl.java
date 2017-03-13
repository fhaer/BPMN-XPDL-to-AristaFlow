package de.bpmnaftool.model.bpmn.fileio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.transform.stream.StreamSource;

import de.bpmnaftool.model.bpmn.*;
import de.bpmnaftool.model.bpmn.connectingobject.*;
import de.bpmnaftool.model.bpmn.flowobject.*;
import de.bpmnaftool.model.bpmn.swimlane.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implementation of XpdlParser
 * 
 * @author Felix Härer
 */
public class XpdlParserImpl implements XpdlParser {

	/**
	 * File to be parsed
	 */
	private File file;
	/**
	 * resulting BPMN model
	 */
	private BpmnModel bpmnModel;
	/**
	 * is the XML well formed
	 */
	private boolean wellFormed = false;
	/**
	 * is the XML valid against XPDL-Schema 2.1
	 */
	private boolean valid = false;

	/**
	 * A file is required to create a new parser
	 * 
	 * @param file
	 *            File to parse
	 */
	public XpdlParserImpl(File file) {
		setFile(file);
	}

	@Override
	public String getFile() {
		return file.toString();
	}

	@Override
	public void setFile(File file) {
		this.file = file;
		if (!file.canRead()) {
			throw new IllegalArgumentException("Kann Eingabedatei nicht lesen: " + file);
		}
	}

	@Override
	public BpmnModel parse() throws Exception {
		if (file == null) {
			throw new IllegalStateException("Keine Eingabedatei gesetzt");
		}
		BpmnModel bpmnModel = new BpmnModelImpl();
		this.bpmnModel = bpmnModel;
		try {
			Element documentRoot = parseXml();
			try {
				parseXpdl(documentRoot);
			} catch (XpdlParserException e) {
				throw e;
			} catch (Exception e) {
				validateXpdl();
				// if validator did not throw a (more specific) exception, throw e now
				throw e;
			}
			validateXpdl();
			validateBpmn();
		} catch (Exception e) {
			valid = false;
			throw e;
		}
		return bpmnModel;
	}

	/**
	 * Validate BPMN against transformation restrictions
	 */
	private void validateBpmn() {
		BpmnValidator validator = new BpmnValidatorImpl(bpmnModel);
		validator.validateModel();
	}

	/**
	 * Validate XPDL against XPDL-Schema 2.1
	 * 
	 * @throws SAXException
	 * @throws IOException
	 */
	private void validateXpdl() throws SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaLanguage);
		java.net.URL schemaLocation = getClass().getResource(xpdlSchemaLocal);
		Schema schema = schemaFactory.newSchema(schemaLocation);
		Validator validator = schema.newValidator();
		Source source = new StreamSource(file);
		// validate against schema, throws exception if fails
		validator.validate(source);
		valid = true;
	}

	/**
	 * Parse XML and return root element
	 * 
	 * @return root element
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private Element parseXml() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// use name spaces, so the attribute xmlns in root element can be used
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		Element root = document.getDocumentElement();

		// if there was no exception, XML must be well formed
		wellFormed = true;
		return root;
	}

	/**
	 * Parses an XPDL file
	 */
	private void parseXpdl(Element root) {
		String name = root.getAttribute("Name");
		bpmnModel.setName(name);
		parseHeader(root);
		parsePools(root);
		parseWorkflowProcesses(root);
		parseMessageFlows(root);
		valid = true;
	}

	/**
	 * Parses XPDL Pools
	 * 
	 * @param root
	 *            Root of DOM document where elements are added to
	 */
	private void parsePools(Element root) {
		Element pools = getSingleElement(root, "Pools");
		for (Element pool : getElements(pools, "Pool")) {
			String[] nameAndId = getNameAndId(pool);
			Pool bpmnPool = new PoolImpl();
			bpmnPool.setName(nameAndId[0]);
			bpmnPool.setId(nameAndId[1]);
			bpmnModel.addSwimLane(bpmnPool);

			if (elementExists(pool, "Lanes")) {
				Element lanes = getSingleElement(pool, "Lanes");
				for (Element lane : getElements(lanes, "Lane")) {
					String[] nameAndIdLane = getNameAndId(lane);
					Lane bpmnLane = new LaneImpl(bpmnPool);
					bpmnLane.setName(nameAndIdLane[0]);
					bpmnLane.setId(nameAndIdLane[1]);
					bpmnModel.addSwimLane(bpmnLane);
				}
			}

		}
	}

	/**
	 * Parses XPDL WorkflowProcesses, sets Name and calls methods to parse activities and transitions.
	 * 
	 * @param root
	 *            Root of DOM document where elements are added to
	 */
	private void parseWorkflowProcesses(Element root) {
		ArrayList<Element> workflows = getElements(root, "WorkflowProcess");
		// parse workflows for each pool
		Element pools = getSingleElement(root, "Pools");
		for (Element pool : getElements(pools, "Pool")) {
			String poolId = pool.getAttribute("Id");
			String procId = pool.getAttribute("Process");
			for (Element workflow : workflows) {
				String workflowId = workflow.getAttribute("Id");
				if (workflowId.equals(procId)) {
					Element activities = getSingleElement(workflow, "Activities");
					parseActivities(activities, poolId);
				}
			}
		}
		// parse transitions
		for (Element workflow : workflows) {
			Element transitions = getSingleElement(workflow, "Transitions");
			parseTransitions(transitions);
		}
	}

	/**
	 * Parses XPDL Activities. An Activity element is either an Activity, a Gateway or an Event.
	 * 
	 * @param activities
	 *            Element in DOM document
	 * @param poolId
	 *            Pool in which workflow processes are located
	 */
	private void parseActivities(Element activities, String poolId) {
		for (Element activity : getElements(activities, "Activity")) {
			Lane lane = getLane(poolId, activity);
			// skip if activity is not part of the given pool
			if (lane == null) {
				continue;
			}

			FlowObject bpmnFlowObject = new ActivityImpl(lane);
			// Event or Gateway can be inside an Activity element
			if (elementExists(activity, "Event")) {
				Element event = getSingleElement(activity, "Event");
				bpmnFlowObject = parseEvent(event, lane);
			} else if (elementExists(activity, "Route")) {
				bpmnFlowObject = parseGateway(activity, lane);
			}
			String[] nameAndId = getNameAndId(activity);
			bpmnFlowObject.setName(nameAndId[0]);
			bpmnFlowObject.setId(nameAndId[1]);
			bpmnModel.addFlowObject(bpmnFlowObject);
		}
	}

	/**
	 * Returns the lane of an activity to create. An activity can have a lane assigned; if no lane is
	 * assigned, a default lane is created.
	 * 
	 * @param poolId
	 *            pool where activity is located
	 * @param activity
	 *            activity element in DOM
	 * @return lane object (BPMN). Null if lane is not part of given pool
	 */
	private Lane getLane(String poolId, Element activity) {
		Lane lane = null;
		Pool pool = bpmnModel.getPool(poolId);
		Element graphicsInfo = getSingleElement(activity, "NodeGraphicsInfo");
		String laneId = graphicsInfo.getAttribute("LaneId");
		if (laneId.isEmpty()) {
			// no lane => place in default lane (create if not exist)
			laneId = poolId + "_DEFAULT-LANE";
			for (Lane l : bpmnModel.getLanes(pool)) {
				if (l.getId().equals(laneId)) {
					return l;
				}
			}
			lane = new LaneImpl(pool);
			lane.setId(laneId);
			bpmnModel.addSwimLane(lane);
			return lane;
		}
		if (bpmnModel.getLane(laneId).getPool().equals(pool)) {
			lane = bpmnModel.getLane(laneId);
		}
		return lane;
	}

	/**
	 * Parses XPDL Transitions, creates SequenceFlow objects.
	 * 
	 * @param transitions
	 *            XML Element where transitions are
	 */
	private void parseTransitions(Element transitions) {
		for (Element transition : getElements(transitions, "Transition")) {
			String id = transition.getAttribute("Id");
			String from = transition.getAttribute("From");
			String to = transition.getAttribute("To");

			FlowObject fromObject = bpmnModel.getFlowObject(from);
			FlowObject toObject = bpmnModel.getFlowObject(to);
			SequenceFlow sequenceFlow = new SequenceFlowImpl(fromObject, toObject);
			sequenceFlow.setId(id);

			// <xpdl2:Condition Type="OTHERWISE"/>
			if (elementExists(transition, "Condition")) {
				Element c = getSingleElement(transition, "Condition");
				String type = c.getAttribute("Type");
				String condition = "";
				if (type.equals("CONDITION")) {
					if (elementExists(c, "Expression")) {
						Element e = getSingleElement(c, "Expression");
						condition = e.getTextContent();
					}
				} else if (type.equals("OTHERWISE")) {
					sequenceFlow.setDefaultFlow();
				}
				sequenceFlow.setCondition(condition);
			}
			bpmnModel.addConnectingObject(sequenceFlow);
		}
	}

	/**
	 * Parses an XPDL Event which is part of an Activity Element.
	 * 
	 * @param event
	 *            Element in DOM document
	 * @param lane
	 *            lane where Event is added to
	 */
	private Event parseEvent(Element event, Lane lane) {
		Event.Trigger trigger = Event.Trigger.Unknown;
		Event.Type type = Event.Type.Unknown;
		String triggerString = "";

		if (elementExists(event, "StartEvent")) {
			type = Event.Type.StartEvent;
			Element startEvent = getSingleElement(event, "StartEvent");
			triggerString = startEvent.getAttribute("Trigger");
		} else if (elementExists(event, "EndEvent")) {
			type = Event.Type.EndEvent;
		} else if (elementExists(event, "IntermediateEvent")) {
			type = Event.Type.IntermediateEvent;
			Element interEvent = getSingleElement(event, "IntermediateEvent");
			triggerString = interEvent.getAttribute("Trigger");
		}
		trigger = setEventTrigger(trigger, triggerString);

		Event bpmnEvent = new EventImpl(lane, type, trigger);
		if (trigger == Event.Trigger.Message) {
			Element triggerResult = getSingleElement(event, "TriggerResultMessage");
			if (triggerResult.getAttribute("CatchThrow").equals("CATCH"))
				bpmnEvent.setCatchingEvent();
			if (triggerResult.getAttribute("CatchThrow").equals("THROW"))
				bpmnEvent.setThrowingEvent();
		}
		return bpmnEvent;
	}

	/**
	 * returns the trigger type for an event
	 * 
	 * @param trigger
	 *            trigger of event
	 * @param triggerString
	 *            string representation from XML
	 * @return trigger type
	 */
	private Event.Trigger setEventTrigger(Event.Trigger trigger, String triggerString) {
		if (triggerString.equals("Message")) {
			trigger = Event.Trigger.Message;
		} else if (triggerString.equals("Link")) {
			trigger = Event.Trigger.Link;
		} else if (triggerString.equals("Rule")) {
			trigger = Event.Trigger.Rule;
		} else if (triggerString.equals("Timer")) {
			trigger = Event.Trigger.Timer;
		}
		return trigger;
	}

	/**
	 * Parses an XPDL Gateway element which is part of an Activity element.
	 * 
	 * @param activity
	 *            Element in DOM document
	 * @param lane
	 *            lane where Gateway is added to
	 * @return the created Gateway
	 */
	private Gateway parseGateway(Element activity, Lane lane) {
		Element gateway = getSingleElement(activity, "Route");
		Gateway.Type type = Gateway.Type.Unknown;
		String typeString = gateway.getAttribute("GatewayType");
		if (typeString.equals("Exclusive")) {
			type = Gateway.Type.Exclusive;
		} else if (typeString.equals("Parallel")) {
			type = Gateway.Type.Parallel;
		} else if (typeString.equals("Inclusive")) {
			type = Gateway.Type.Inclusive;
		} else if (typeString.equals("Complex")) {
			type = Gateway.Type.Complex;
		}
		if (elementExists(activity, "Split")) {
			return new GatewaySplit(lane, type);
		} else if (elementExists(activity, "Join")) {
			return new GatewayJoin(lane, type);
		}
		throw new XpdlParserException("Gateway ohne enthaltenes Split oder Join Element gefunden: "
				+ activity.getAttribute("Id") + " " + gateway);
	}

	/**
	 * Parses XPDL MessageFlow elements and creates BPMN MessgageFlow objects.F
	 * 
	 * @param root
	 *            Root of DOM document where elements are added to
	 */
	private void parseMessageFlows(Element root) {
		if (!elementExists(root, "MessageFlows"))
			return;
		Element messageFlows = getSingleElement(root, "MessageFlows");
		for (Element messageFlow : getElements(messageFlows, "MessageFlow")) {
			MessageFlow bpmnFlow = null;
			Pool sourcePool = null;
			Pool targetPool = null;
			bpmnFlow = generateMessageFlow(messageFlow, bpmnFlow, sourcePool, targetPool);
			String id = messageFlow.getAttribute("Id");
			bpmnFlow.setId(id);
			bpmnModel.addConnectingObject(bpmnFlow);
		}
	}

	/**
	 * generates the given message flow in the BPMN model
	 * 
	 * @param messageFlow
	 *            message flow XML element
	 * @param bpmnFlow
	 *            BPMN message flow
	 * @param sourcePool
	 *            source pool
	 * @param targetPool
	 *            target pool
	 * @return message flow
	 */
	private MessageFlow generateMessageFlow(Element messageFlow, MessageFlow bpmnFlow, Pool sourcePool,
			Pool targetPool) {
		String sourceId = messageFlow.getAttribute("Source");
		String targetId = messageFlow.getAttribute("Target");
		for (Pool pool : bpmnModel.getPools()) {
			String poolId = pool.getId();
			if (sourceId.equals(poolId))
				sourcePool = pool;
			if (targetId.equals(poolId))
				targetPool = pool;
		}
		if (sourcePool != null && targetPool != null) {
			bpmnFlow = new MessageFlowImpl(sourcePool, targetPool);
		} else if (sourcePool == null && targetPool != null) {
			FlowObject source = bpmnModel.getFlowObject(sourceId);
			bpmnFlow = new MessageFlowImpl(source, targetPool);
		} else if (sourcePool != null && targetPool == null) {
			FlowObject target = bpmnModel.getFlowObject(targetId);
			bpmnFlow = new MessageFlowImpl(sourcePool, target);
		} else if (sourcePool == null && targetPool == null) {
			FlowObject source = bpmnModel.getFlowObject(sourceId);
			FlowObject target = bpmnModel.getFlowObject(targetId);
			bpmnFlow = new MessageFlowImpl(source, target);
		} else {
			throw new XpdlParserException("Von dem Message Flow \"" + bpmnFlow + "\" wurde "
					+ "ein Flow Object oder ein Pool referenziert, welcher nicht existiert.");
		}
		return bpmnFlow;
	}

	/**
	 * Parses the header of an XPDL file to check the XPDL version.
	 * 
	 * @param root
	 *            Root of DOM document where elements are added to
	 */
	private void parseHeader(Element root) {
		Element header = getSingleElement(root, "PackageHeader");
		Element version = getSingleElement(header, "XPDLVersion");
		if (!version.getTextContent().equals("2.1")) {
			throw new XpdlParserException("XPDL-Version ist nicht 2.1");
		}
	}

	/**
	 * Checks if an element with the given name exists as a child of the given element.
	 * 
	 * @param element
	 *            Element which is the parent of the element to check for
	 * @param tagName
	 *            name of the Element to check
	 * @return true if element exists, false otherwise
	 */
	private boolean elementExists(Element element, String tagName) {
		NodeList nodes = element.getElementsByTagNameNS(xpdlNamespace, tagName);
		return (nodes.getLength() > 0);
	}

	/**
	 * Checks if an element with the given name exists as a child of the given element. If exactly one
	 * element is found it is returned, otherwise an Exception is thrown.
	 * 
	 * @param element
	 *            Element which is the parent of the single element to return
	 * @param tagName
	 *            name of the single element to return
	 * @return single element
	 */
	private Element getSingleElement(Element element, String tagName) {
		NodeList nodes = element.getElementsByTagNameNS(xpdlNamespace, tagName);
		int length = nodes.getLength();
		if (length != 1)
			throw new XpdlParserException("Es muss genau ein Element \"" + tagName
					+ "\" enthalten sein, es sind aber " + length + " enthalten.");
		return (Element) nodes.item(0);
	}

	/**
	 * Returns all elements with the given name which are child elements of the given element.
	 * 
	 * @param element
	 *            Element which is the parent of the elements to return
	 * @param tagName
	 *            name of the elements to return
	 * @return ArrayList of Elements
	 */
	private ArrayList<Element> getElements(Element element, String tagName) {
		ArrayList<Element> elements = new ArrayList<Element>();
		NodeList nodes = element.getElementsByTagNameNS(xpdlNamespace, tagName);
		for (int i = 0; i < nodes.getLength(); i++) {
			elements.add((Element) nodes.item(i));
		}
		return elements;
	}

	/**
	 * Checks if the given Element has attributes Name and Id. If they exist, an array of two strings is
	 * returned, where the first string in index 0 is the Name and the second string in index 1 is the
	 * Id. If Name or Id attribute does not exist, an Exception is thrown.
	 * 
	 * @param element
	 *            Element where attributes are retrieved
	 * @return String array where index 0 is Name and index 1 is Id
	 */
	private String[] getNameAndId(Element element) {
		String[] nameAndId = new String[2];
		nameAndId[0] = element.getAttribute("Name");
		nameAndId[1] = element.getAttribute("Id");
		if (nameAndId[0] == null || nameAndId[1] == null)
			throw new XpdlParserException("Element ohne Namen oder ID gefunden: " + element.toString());
		return nameAndId;
	}

	@Override
	public boolean isXmlWellFormed() {
		return wellFormed;
	}

	@Override
	public boolean isXpdlValid() {
		return valid;
	}
}

/**
 * Simple exception for XPDL parsing
 * 
 * @author Felix Härer
 */
class XpdlParserException extends RuntimeException {
	public XpdlParserException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -2899192894215776832L;
}