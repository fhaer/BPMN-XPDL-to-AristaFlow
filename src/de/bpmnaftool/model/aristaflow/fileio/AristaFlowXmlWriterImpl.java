package de.bpmnaftool.model.aristaflow.fileio;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import de.bpmnaftool.control.BpmnAfToolImpl;
import de.bpmnaftool.model.aristaflow.*;
import de.bpmnaftool.model.aristaflow.activity.*;
import de.bpmnaftool.model.aristaflow.data.*;
import de.bpmnaftool.model.aristaflow.graph.edge.*;
import de.bpmnaftool.model.aristaflow.graph.node.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * Implementation for writing AristaFlow XML files
 * 
 * @author Felix Härer
 */
public class AristaFlowXmlWriterImpl implements AristaFlowXmlWriter {

	/**
	 * File to be parsed
	 */
	private File file;
	/**
	 * resulting BPMN model
	 */
	private AristaFlowModel aristaFlowModel;

	/**
	 * A file is required to create a new writer
	 * 
	 * @param file
	 *            File to write
	 * @throws IOException
	 */
	public AristaFlowXmlWriterImpl(File file) throws IOException {
		setFile(file);
	}

	@Override
	public String getFile() {
		return file.toString();
	}

	@Override
	public void setFile(File file) throws IOException {
		this.file = file;
		if (!file.exists()) {
			file.createNewFile();
		}
		if (file.exists() && !file.canWrite()) {
			throw new IllegalArgumentException("Datei kann nicht geschrieben werden " + file);
		}
	}

	@Override
	public void write(AristaFlowModel aristaFlowModel) {
		if (aristaFlowModel == null) {
			throw new IllegalArgumentException("AristaFlowModel ist null");
		}
		this.aristaFlowModel = aristaFlowModel;
		Document document;
		try {
			document = generateXml();
			writeFile(document);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("ParserConfigurationException: " + e.getMessage());
		} catch (TransformerException e) {
			throw new RuntimeException("TransformerException: " + e.getMessage());
		}
	}

	/**
	 * Writes the given document to file
	 * 
	 * @param document
	 *            document which is written to file
	 * @throws TransformerException
	 */
	private void writeFile(Document document) throws TransformerException {
		BpmnAfToolImpl.getInstance().log("Formatiere XML", 1, 1);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

		BpmnAfToolImpl.getInstance().log("Schreibe XML-Datei \"" + file.getPath() + "\"", 1, 1);
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(file);
		transformer.transform(domSource, streamResult);
	}

	/**
	 * Generates an XML document (DOM tree)
	 * 
	 * @return XML Document
	 * @throws ParserConfigurationException
	 */
	private Document generateXml() throws ParserConfigurationException {
		BpmnAfToolImpl.getInstance().log("Erstelle XML-Datei für AristaFlow ...", 0, 1);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();

		Element root = generateRoot(document);

		BpmnAfToolImpl.getInstance().log("Generiere XML ...", 1, 1);
		generateHeader(root);
		generateNodes(root);
		generateDataElements(root);
		generateEdges(root);
		generateDataEdges(root);
		generateStartEndNode(root);
		generateStructuralNodeData(root);

		return document;
	}

	/**
	 * Generates the root Element with all attributes
	 * 
	 * @param document
	 *            Docoument (DOM) where root is added to
	 * @return root Element
	 */
	private Element generateRoot(Document document) {
		// <template id="5afa896a-c8af-3ed5-1893-8b7dfb97104a" version="16"
		// xmlns="http://www.aristaflow.de/adept2/processmodel"
		// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		// xsi:schemaLocation="http://www.aristaflow.de/adept2/processmodel template.xsd http://www.w3.org/2000/09/xmldsig# xmldsig-core-schema.xsd">
		Element root = document.createElement("template");
		root.setAttribute("id", aristaFlowModel.getId().toString());
		root.setAttribute("version", "16");
		root.setAttribute("xmlns", "http://www.aristaflow.de/adept2/processmodel");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root
				.setAttribute(
						"xsi:schemaLocation",
						"http://www.aristaflow.de/adept2/processmodel template.xsd http://www.w3.org/2000/09/xmldsig# xmldsig-core-schema.xsd");
		document.appendChild(root);
		return root;
	}

	/**
	 * Generates the header including name, description, processType, supervisorAgent and version
	 * 
	 * @param root
	 *            Element where generated Header is added to
	 */
	private void generateHeader(Element root) {
		BpmnAfToolImpl.getInstance().log("Generiere Header", 2, 1);
		// <name>ProcessPackageProcess</name>
		// <description/>
		// <processType/>
		// <supervisorAgent>
		// <agentID>-1</agentID>
		// <agentName>supervisor</agentName>
		// <orgPositionID>-1</orgPositionID>
		// <orgPositionName>supervisor</orgPositionName>
		// </supervisorAgent>
		// <version/>
		addElement(root, "name", aristaFlowModel.getName());
		addElement(root, "description");
		addElement(root, "processType");

		Element agent;
		String agentName = aristaFlowModel.getSupervisoryAgent();
		agent = addElement(root, "supervisorAgent");
		addElement(agent, "agentID", "-1");
		addElement(agent, "agentName", agentName);
		addElement(agent, "orgPositionID", "-1");
		addElement(agent, "orgPositionName", agentName);

		addElement(root, "version");
	}

	/**
	 * Generates the nodes Element including all node Elements
	 * 
	 * @param root
	 *            Element where generated Nodes are added to
	 */
	private void generateNodes(Element root) {
		BpmnAfToolImpl.getInstance().log("Generiere Nodes", 2, 1);
		// <node id="n0">
		// <name>Start</name>
		// <description></description>
		// <staffAssignmentRule></staffAssignmentRule>
		// <autoStart>false</autoStart>
		// </node>
		Element nodes = addElement(root, "nodes");
		for (Node node : aristaFlowModel.getAllNodes()) {
			Element xmlNode = addElement(nodes, "node");
			xmlNode.setAttribute("id", node.getNodeId());
			addElement(xmlNode, "name", node.getName());
			addElement(xmlNode, "description", node.getDescription());
			addElement(xmlNode, "staffAssignmentRule", node.getStaffAssignment());
			generateExecutableBusinessProcessElement(node, xmlNode);
			addElement(xmlNode, "autoStart", node.getAutoStart());
			generateConnectedParameterMappingsElement(node, xmlNode);
		}
	}

	/**
	 * Generates the element executableBusinessProcess (an activity) if a node has an activity assigned
	 * 
	 * @param node
	 *            activity of this node is used to generate the element
	 * @param xmlNode
	 *            Element where generated element is added to
	 */
	private void generateExecutableBusinessProcessElement(Node node, Element xmlNode) {
		ActivityTemplate activity = getActivity(node);
		if (activity == null)
			return;

		Element execElem = addElement(xmlNode, "executableBusinessProcess");
		Element actElem = addElement(execElem, "activity");
		setActivityAttributes(activity, actElem);

		if (activity instanceof XorActivity) {
			generateXorActivity((XorActivity) activity, actElem);
		} else {
			addElement(actElem, "parameterChangePolicy", activity.getParameterChangePolicy());
			generateInputParams(activity, actElem);
			generateOutputParams(activity, actElem);
		}
	}

	/**
	 * Sets attributes inside a executableBusinessProcess element (activity)
	 * 
	 * @param activity
	 *            activity template
	 * @param actElem
	 *            Element where attributes are set
	 */
	private void setActivityAttributes(ActivityTemplate activity, Element actElem) {
		actElem.setAttribute("id", activity.getActivityId());
		addElement(actElem, "ecName", activity.getEcName());
		addElement(actElem, "operationName", activity.getOperationName());
		addElement(actElem, "name", activity.getName());
		addElement(actElem, "description", activity.getDescription())
				.setAttribute("origin", "OPERATION");
		addElement(actElem, "implementationClass", activity.getImplementationClass()).setAttribute(
				"origin", "ECD");
		addElement(actElem, "isSingleton", activity.getIsSingleton()).setAttribute("origin", "ECD");
		addElement(actElem, "guiContextID", activity.getGuiContextID()).setAttribute("origin", "ECD");

		Element execProp = addElement(actElem, "executionControlProperties");
		execProp.setAttribute("origin", "ECD");
		addElement(execProp, "isSuspensible", activity.getIsSuspensible());
		addElement(execProp, "isResettable", activity.getIsResettable());
		addElement(execProp, "isClosable", activity.getIsClosable());
		addElement(actElem, "supportsViewOnly", activity.getSupportsViewOnly()).setAttribute("origin",
				"OPERATION");
		addElement(actElem, "supportsTestExecution", activity.getSupportsTestExecution()).setAttribute(
				"origin", "OPERATION");
	}

	/**
	 * Generates the dataElements node with all dataElement nodes.
	 * 
	 * @param root
	 *            Element where generated dataElement nodes are added to
	 */
	private void generateDataElements(Element root) {
		BpmnAfToolImpl.getInstance().log("Generiere Data Elements", 2, 1);
		// <dataElement id="d0">
		// <type>BOOLEAN</type>
		// <name>A&amp;&amp;B</name>
		// <description></description>
		// <identifierID>ffffffff-ffff-fffe-8000-000000000000</identifierID>
		// <isPublic>false</isPublic>
		// </dataElement>
		Element dataElements = addElement(root, "dataElements");
		for (DataElement dataElement : aristaFlowModel.getDataElements()) {
			Element xmlNode = addElement(dataElements, "dataElement");
			xmlNode.setAttribute("id", dataElement.getDataElementId());
			addElement(xmlNode, "type", dataElement.getType().toString());
			addElement(xmlNode, "name", dataElement.getName());
			addElement(xmlNode, "description", dataElement.getDescription());
			addElement(xmlNode, "identifierID", dataElement.getIdentifierId().toString());
			addElement(xmlNode, "isPublic", dataElement.isPublic());
		}
	}

	/**
	 * Generates the edge node with all edges nodes.
	 * 
	 * @param root
	 *            Element where generated edge nodes are added to
	 */
	private void generateEdges(Element root) {
		BpmnAfToolImpl.getInstance().log("Generiere Edges", 2, 1);
		// <edge destinationNodeID="n15" edgeType="ET_CONTROL"
		// sourceNodeID="n7">
		// <edgeType>ET_CONTROL</edgeType>
		// <edgeCode>1</edgeCode>
		// </edge>
		Element edges = addElement(root, "edges");
		for (Edge edge : aristaFlowModel.getEdges()) {
			Element xmlNode = addElement(edges, "edge");
			xmlNode.setAttribute("destinationNodeID", edge.getDestinationNode().getNodeId());
			xmlNode.setAttribute("edgeType", edge.getEdgeType());
			xmlNode.setAttribute("sourceNodeID", edge.getSourceNode().getNodeId());
			addElement(xmlNode, "edgeType", edge.getEdgeType());
			if (edge.getEdgeCode() != null)
				addElement(xmlNode, "edgeCode", Integer.toString(edge.getEdgeCode()));
		}
	}

	/**
	 * Generates the dataEdges node with all dataEdge nodes.
	 * 
	 * @param root
	 *            Element where generated dataEdge nodes are added to
	 */
	private void generateDataEdges(Element root) {
		BpmnAfToolImpl.getInstance().log("Generiere Data Edges", 2, 1);
		// <dataEdge connectorID="0" dataEdgeType="READ" dataElementID="d0"
		// nodeID="n14">
		// <dataEdgeType>READ</dataEdgeType>
		// <isOptional>false</isOptional>
		// </dataEdge>
		Element dataEdges = addElement(root, "dataEdges");
		for (DataEdge dataEdge : aristaFlowModel.getDataEdges()) {
			Element xmlNode = addElement(dataEdges, "dataEdge");
			xmlNode.setAttribute("connectorID", Integer.toString(dataEdge.getConnectorId()));
			xmlNode.setAttribute("dataEdgeType", dataEdge.getEdgeType().toString());
			xmlNode.setAttribute("dataElementID", dataEdge.getDataElement().getDataElementId());
			xmlNode.setAttribute("nodeID", dataEdge.getNode().getNodeId());
			addElement(xmlNode, "dataEdgeType", dataEdge.getEdgeType().toString());
			addElement(xmlNode, "isOptional", dataEdge.isOptional());
		}
	}

	/**
	 * Generates two elements with start and end node of the workflow.
	 * 
	 * @param root
	 *            Element where generated start/end node is added to
	 */
	private void generateStartEndNode(Element root) {
		// <startNode>n0</startNode>
		// <endNode>n1</endNode>
		addElement(root, "startNode", aristaFlowModel.getStartNode().getNodeId());
		addElement(root, "endNode", aristaFlowModel.getEndNode().getNodeId());
	}

	/**
	 * Generates the structuralNodeData node with one structuralData node for each AristaFlow node. They
	 * include type and IDs of the AristaFlow node.
	 * 
	 * @param root
	 *            Element where generated structuralNodeData nodes are added to
	 */
	private void generateStructuralNodeData(Element root) {
		BpmnAfToolImpl.getInstance().log("Generiere strukturelle Daten für Nodes ", 2, 1);
		// <structuralNodeData nodeID="n0">
		// <type>NT_STARTFLOW</type>
		// <topologicalID>0</topologicalID>
		// <branchID>0</branchID>
		// <splitNodeID>n14</splitNodeID>
		// <correspondingBlockNodeID>n1</correspondingBlockNodeID>
		// </structuralNodeData>
		Element structuralData = addElement(root, "structuralData");
		for (Node node : aristaFlowModel.getAllNodes()) {
			Element xmlNode = addElement(structuralData, "structuralNodeData");
			xmlNode.setAttribute("nodeID", node.getNodeId());
			addElement(xmlNode, "type", node.getNodeType());
			addElement(xmlNode, "topologicalID", Integer.toString(node.getTopoId()));
			addElement(xmlNode, "branchID", Integer.toString(node.getBranchId()));
			// StartNode and EndNode do not include the SplitNode attribute
			if (!(node instanceof StartFlowNode || node instanceof EndFlowNode)) {
				addElement(xmlNode, "splitNodeID", node.getSplitNode().getNodeId());
			}
			addElement(xmlNode, "correspondingBlockNodeID", node.getCorrepondingBlockNode().getNodeId());
		}
	}

	/**
	 * Generates XML for an XorActivity
	 * 
	 * @param activity
	 *            XorActivity to generate
	 * @param xmlNode
	 *            add activity to this element
	 */
	private void generateXorActivity(XorActivity activity, Element xmlNode) {
		generateConfigEntries(activity, xmlNode);
		addElement(xmlNode, "parameterChangePolicy", activity.getParameterChangePolicy());
		addElement(xmlNode, "decisionParameter", activity.getOutParamName(0));
		generateDecisionStatements(activity, xmlNode);
		generateInputParams(activity, xmlNode);
		generateOutputParams(activity, xmlNode);
		Element a = addElement(xmlNode, "userAttributes");
		Element val = addElement(a, "userAttribute", "true");
		val.setAttribute("name", "decision");
	}

	/**
	 * Generates configuration entries of an activity
	 * 
	 * @param activity
	 *            generate configuration entries for this activity
	 * @param xmlNode
	 *            add configuration entries to this element
	 */
	private void generateConfigEntries(XorActivity activity, Element xmlNode) {
		Element config = addElement(xmlNode, "configuration");
		Element decBytecode = addElement(config, "configurationEntry", activity.getDecisionBytecode());
		decBytecode.setAttribute("name", "DECISION_BYTECODE");
		decBytecode.setAttribute("fixed", "false");
		Element decXml = addElement(config, "configurationEntry", activity.getDecisionXml());
		decXml.setAttribute("name", "DECISION_XML");
		decXml.setAttribute("fixed", "false");
	}

	/**
	 * Generates decision statements of an activity
	 * 
	 * @param activity
	 *            generate statements for this activity
	 * @param xmlNode
	 *            add generated statements to this element
	 */
	private void generateDecisionStatements(XorActivity activity, Element xmlNode) {
		Element decisions = addElement(xmlNode, "decisionStatements");
		for (int i = 0; i < activity.getNumberOfDecisions(); i++) {
			Element decision = addElement(decisions, "decisionStatement");
			decision.setAttribute("edgeCode", Integer.toString(i));
			addElement(decision, "decisionLabel", activity.getDecisionLabel(i));
			addElement(decision, "decisionStatement", activity.getDecisionStatement(i));
			addElement(decision, "decisionID", Integer.toString(i));
		}
	}

	/**
	 * Generates input parameter an activity
	 * 
	 * @param activity
	 *            generate parameter for this activity
	 * @param xmlNode
	 *            add generated parameter to this element
	 */
	private void generateInputParams(ActivityTemplate activity, Element xmlNode) {
		if (activity.getNumberOfInputParameters() < 1)
			return;
		Element in = addElement(xmlNode, "inputParameters");
		for (int i = 0; i < activity.getNumberOfInputParameters(); i++) {
			Element p = addElement(in, "inputParameter");
			addElement(p, "name", activity.getInParamName(i));
			addElement(p, "description", activity.getInParamDescription(i));
			addElement(p, "type", activity.getInParamType(i));
			addElement(p, "identifierID", activity.getInParamIdentifierID(i));
			addElement(p, "isOptional", activity.getInParamIsOptional(i));
			Element a = addElement(p, "userAttributes");
			Element val = addElement(a, "userAttribute", activity.getInParamAttribute(i));
			val.setAttribute("name", activity.getInParamAttributeValue(i));
		}
	}

	/**
	 * Generates output parameter an activity
	 * 
	 * @param activity
	 *            generate parameter for this activity
	 * @param xmlNode
	 *            add generated parameter to this element
	 */
	private void generateOutputParams(ActivityTemplate activity, Element xmlNode) {
		if (activity.getNumberOfOutputParameters() < 1)
			return;
		Element out = addElement(xmlNode, "outputParameters");
		for (int i = 0; i < activity.getNumberOfOutputParameters(); i++) {
			Element p = addElement(out, "outputParameter");
			addElement(p, "name", activity.getOutParamName(i));
			addElement(p, "description", activity.getOutParamDescription(i));
			addElement(p, "type", activity.getOutParamType(i));
			addElement(p, "identifierID", activity.getOutParamIdentifierID(i));
			addElement(p, "isOptional", activity.getOutParamIsOptional(i));
			Element a = addElement(p, "userAttributes");
			Element val = addElement(a, "userAttribute", activity.getOutParamAttribute(i));
			val.setAttribute("name", activity.getOutParamAttributeValue(i));
		}
	}

	/**
	 * Generates the element connectedParameterMappings if a node has an activity assigned
	 * 
	 * @param node
	 *            activity of this node is used to generate the element
	 * @param xmlNode
	 *            Element where generated element is added to
	 */
	private void generateConnectedParameterMappingsElement(Node node, Element xmlNode) {
		ActivityTemplate activity = getActivity(node);
		if (activity == null)
			return;
		int nParams = activity.getNumberOfInputParameters() + activity.getNumberOfOutputParameters();
		if (nParams < 1)
			return;

		Element mappings = addElement(xmlNode, "connectorParameterMappings");
		for (int i = 0; i < activity.getNumberOfInputParameters(); i++) {
			Element mappingIn = addElement(mappings, "connectorParameterMapping");
			mappingIn.setAttribute("accessType", "READ");
			int id = activity.getInParamConnectorId(i);
			mappingIn.setAttribute("connector", Integer.toString(id));
			mappingIn.setAttribute("parameterName", activity.getInParamName(i));
		}
		for (int i = 0; i < activity.getNumberOfOutputParameters(); i++) {
			Element mappingIn = addElement(mappings, "connectorParameterMapping");
			mappingIn.setAttribute("accessType", "WRITE");
			int id = activity.getOutParamConnectorId(i);
			mappingIn.setAttribute("connector", Integer.toString(id));
			mappingIn.setAttribute("parameterName", activity.getOutParamName(i));
		}
	}

	/**
	 * Returns the activity of a node, returns null if it has none
	 * 
	 * @param node
	 *            node where the activity is returned
	 * @return activity
	 */
	private ActivityTemplate getActivity(Node node) {
		ActivityTemplate activity = null;
		if (node instanceof NormalNode)
			activity = ((NormalNode) node).getActivity();
		if (node instanceof XorSplitNode)
			activity = ((XorSplitNode) node).getActivity();
		if (node instanceof EndLoopNode)
			activity = ((EndLoopNode) node).getActivity();
		return activity;
	}

	/**
	 * Creates a new Element and adds it to the given element. The new Element will have the given name
	 * and contain the given text.
	 * 
	 * @param element
	 *            Element where a new Element is added as child
	 * @param name
	 *            name of the Element to add
	 * @param text
	 *            text of the new Element
	 * @return the new Element
	 */
	private Element addElement(Element element, String name, String text) {
		Document document = element.getOwnerDocument();
		Element newElement = document.createElement(name);
		Text newElementText = document.createTextNode(text);
		newElement.appendChild(newElementText);
		element.appendChild(newElement);
		return newElement;
	}

	/**
	 * Creates a new Element and adds it to the given element. The new Element will have the given name
	 * and contain the given text.
	 * 
	 * @param element
	 *            Element where a new Element is added as child
	 * @param name
	 *            name of the Element to add
	 * @param text
	 *            text of the element (boolean, text is true or false)
	 * @return the new Element
	 */
	private Element addElement(Element element, String name, boolean text) {
		return addElement(element, name, Boolean.toString(text));
	}

	/**
	 * Creates a new Element and adds it to the given element. The new Element will have the given name
	 * and will not contain anything.
	 * 
	 * @param element
	 *            Element where a new Element is added as child
	 * @param name
	 *            name of the Element to add
	 * @return the new Element
	 */
	private Element addElement(Element element, String name) {
		Document document = element.getOwnerDocument();
		Element newElement = document.createElement(name);
		element.appendChild(newElement);
		return newElement;
	}
}
