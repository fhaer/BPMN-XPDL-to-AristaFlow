package de.bpmnaftool.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


import de.bpmnaftool.model.aristaflow.AristaFlowModel;
import de.bpmnaftool.model.aristaflow.graph.edge.Edge;
import de.bpmnaftool.model.aristaflow.graph.node.Node;
import de.bpmnaftool.model.bpmn.BpmnModel;
import de.bpmnaftool.model.bpmn.connectingobject.SequenceFlow;
import de.bpmnaftool.model.bpmn.flowobject.FlowObject;
import de.bpmnaftool.model.bpmn.swimlane.Pool;

import junit.framework.TestCase;

/**
 * High level system test for BpmnModel, AristaFlowModel and Transformation. Test data (XPDL files) are
 * used, the output is compared to expected files (AristaFlow template files).
 * 
 * @author Felix Härer
 */
public class BpmnAfToolImplTest extends TestCase {

	/**
	 * test input data (xpdl files)
	 */
	String testData = "test/testdata/";
	/**
	 * directory for output files (*.template, *.log)
	 */
	String testResult = "test/result/";
	/**
	 * directory for expected result - all files in here must be equal to the ones in the directory
	 * testResult
	 */
	String testExpected = "test/expected/";
	/**
	 * instance of controller
	 */
	BpmnAfTool controller;

	@Override
	public void setUp() {
		controller = BpmnAfToolImpl.getInstance();
		controller.initialize();
		assertNotNull(controller);
	}

	/**
	 * test for very basic model
	 * 
	 * @throws FileNotFoundException
	 */
	public void testSimpleModel() throws FileNotFoundException {
		String file = "Basic Test.xpdl";
		String[] flowObjects = { "Start Event", "Task", "Task 2", "End Event" };
		String[] nodes = { "[Pool|Lane] Start Event", "Task", "Task 2" };
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * Example 2-24
	 * 
	 * @throws FileNotFoundException
	 */
	public void testBeispiel2_24() throws FileNotFoundException {
		String file = "Beispiel 2-24.xpdl";
		String[] flowObjects = { "Auftrag", "Empfange Auftrag", "Sende Lieferung", "Sende Meldung" };
		String[] nodes = { "[Lager|Lane] Warte auf Ereignis: Auftrag", "[L] Empfange Auftrag",
				"[L] Sende Lieferung", "[L] Sende Meldung" };
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * Example 2-24 with modification
	 * 
	 * @throws FileNotFoundException
	 */
	public void testBeispiel2_24_AEnderung() throws FileNotFoundException {
		String file = "Beispiel 2-24 Änderung.xpdl";
		String[] flowObjects = { "Auftrag", "Empfange Auftrag", "Sende Ware", "Sende Meldung" };
		String[] nodes = { "[Lager|Lane] Warte auf Ereignis: Auftrag", "[L] Empfange Auftrag",
				"[L] Sende Ware", "[L] Sende Meldung" };
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * test for loop transformation
	 * 
	 * @throws FileNotFoundException
	 */
	public void testLoop1() throws FileNotFoundException {
		String file = "Test loop 1.xpdl";
		String[] flowObjects = {};
		String[] nodes = {};
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * test for loop transformation
	 * 
	 * @throws FileNotFoundException
	 */
	public void testLoop2() throws FileNotFoundException {
		String file = "Test loop 2.xpdl";
		String[] flowObjects = {};
		String[] nodes = {};
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * test for overlapping gateways
	 * 
	 * @throws FileNotFoundException
	 */
	public void testGatewayOverlap() throws FileNotFoundException {
		String file = "Test gateway overlap.xpdl";
		String[] flowObjects = {};
		String[] nodes = {};
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * test for even more overlapping gateways
	 * 
	 * @throws FileNotFoundException
	 */
	public void testGatewayOverlap2() throws FileNotFoundException {
		String file = "Test gateway overlap 2.xpdl";
		String[] flowObjects = {};
		String[] nodes = {};
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * test for inclusive gateways
	 * 
	 * @throws FileNotFoundException
	 */
	public void testGatewayInclusive() throws FileNotFoundException {
		String file = "Test gateway inclusive.xpdl";
		String[] flowObjects = {};
		String[] nodes = {};
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * more than one workflow process in one input xpdl file
	 * 
	 * @throws FileNotFoundException
	 */
	public void testMultipleProcesses() throws FileNotFoundException {
		String file = "Test multiple workflow processes.xpdl";
		String[] flowObjects = {};
		String[] nodes1 = { "[Process 1] Start Event", "[Process 1] Task 1" };
		String[] nodes2 = { "[Process 2] Start Event", "[Process 2] Task 2" };
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes1, afModel);
		testAristaFlowModel(nodes2, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * Test where many pools contain various lanes in two processes
	 * 
	 * @throws FileNotFoundException
	 */
	public void testMultiplePoolsProcessesLanes() throws FileNotFoundException {
		String file = "Test multiple pools, processes and lanes.xpdl";
		String[] flowObjects = {};
		String[] nodes1 = {};
		AristaFlowModel afModel;
		afModel = testFile(file, false, flowObjects);
		testAristaFlowModel(nodes1, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * e-Car without overlapping gateways
	 * 
	 * @throws FileNotFoundException
	 */
	public void testECarNoOverlap() throws FileNotFoundException {
		String file = "Test e-Car No Overlap.xpdl";
		String[] flowObjects = { "Sende Bedarfe", "GW1" };
		String[] nodes1 = { "[S] Empfange Bedarfe", "[S] GW3", "[S] Sende Individualteilebestellung",
				"[S] GW4", "[S] Sende Zahlungsfreigabe" };
		String[] nodes2 = { "[S] Empfange Bedarfe", "[S] GW3",
				"[S] Sende Meld. Individualteile bestellt", "[S] Empf. Meld. Individualteile erhalten",
				"[S] GW4", "[S] Sende Zahlungsfreigabe" };
		String[] nodes3 = { "[S] Empfange Bedarfe", "[S] GW3", "[S] Sende Gleichteilebestellung",
				"[S] Empfange Gleichteilebelieferung", "[S] Sende Gleichteilebereitstellung" };
		String[] nodes4 = { "[P] GW1", "[P] Sende Meld. Individualteile erhalten", "[P] GW2" };
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes1, afModel);
		testAristaFlowModel(nodes2, afModel);
		testAristaFlowModel(nodes3, afModel);
		testAristaFlowModel(nodes4, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * e-Car, example 4-09
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFallstudie4_09_ECar() throws FileNotFoundException {
		String file = "Fallstudie 4-09 e-Car Produktion.xpdl";
		String[] flowObjects = {};
		String[] nodes1 = { "[P] Empf. Meld. Individualteile bestellt",
				"[P] Empfange Individualteilebelieferung", "[P] Sende Meld. Individualteile erhalten" };
		String[] nodes2 = { "[S] Sende Gleichteilebestellung", "[S] Empfange Gleichteilebelieferung" };
		String[] nodes3 = { "[F] Empfange Zahlungsfreigabe", "[F] Zahlung" };
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes1, afModel);
		testAristaFlowModel(nodes2, afModel);
		testAristaFlowModel(nodes3, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * e-Car modification 1, example 4-14
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFallstudie4_14_ECar_AEnderung1() throws FileNotFoundException {
		String file = "Fallstudie 4-14 e-Car Produktion Änderung 1.xpdl";
		String[] flowObjects = {};
		String[] nodes1 = { "[P] Sende e-Car Bereitstellung", "[P] Sende Meldung abholbereit" };
		String[] nodes2 = { "[S] Sende Meldung Montage" };
		String[] nodes3 = { "[S] Sende Zahlungsfreigabe" };
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes1, afModel);
		testAristaFlowModel(nodes2, afModel);
		testAristaFlowModel(nodes3, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * e-Car modification 2, example 4-19 (error, no output expected)
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFallstudie4_19_ECar_AEnderung2_1() throws FileNotFoundException {
		String file = "Fallstudie 4-19 e-Car Produktion Änderung 2-1.xpdl";
		testFile(file, false, null);
		assertTrue(compareResultToExpected(file + ".template.log"));
	}

	/**
	 * e-Car modification 2, example 4-20 (no error here, but in AristaFlow)
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFallstudie4_20_ECar_AEnderung2_2() throws FileNotFoundException {
		String file = "Fallstudie 4-20 e-Car Produktion Änderung 2-2.xpdl";
		String[] flowObjects = {};
		String[] nodes1 = { "[P] Empfange Bestellung", "[P] 1", "[P] Sende Bedarfe" };
		String[] nodes2 = { "[S] Sende Meld. Individualteile bestellt",
				"[S] Empf. Meld. Individualteile erhalten" };
		String[] nodes3 = { "[P] Individualteile i.O.?" };
		AristaFlowModel afModel;
		afModel = testFile(file, true, flowObjects);
		testAristaFlowModel(nodes1, afModel);
		testAristaFlowModel(nodes2, afModel);
		testAristaFlowModel(nodes3, afModel);
		assertTrue(compareResultToExpected(file + ".template"));
	}

	/**
	 * Tests an input file and returns the transformed aristaFlowModel. The test consists of setting
	 * input, output, transformation, input validation check, basic input model test.
	 * 
	 * @param filename
	 *            input (xpdl) file to test
	 * @param inputValid
	 *            set true if input is valid (test expects transformation to work)
	 * @param flowObjects
	 *            array of flow objects which have to exist in the BpmnModel. Between two flow objects, a
	 *            sequence flow is expected.
	 * @return the transformed AristaFlow model
	 * @throws FileNotFoundException
	 */
	private AristaFlowModel testFile(String filename, boolean inputValid, String[] flowObjects)
			throws FileNotFoundException {
		File input = new File(testData + filename);
		File output = new File(testResult + filename + ".template");
		File log = new File(testResult + filename + ".template.log");
		outputFileCheck(output, log);
		controller.setInputFile(input);
		controller.setOutputFile(output);
		controller.transformModel();
		BpmnModel bpmnModel = controller.getBpmnModel();
		AristaFlowModel afModel = null;
		if (inputValid) {
			afModel = controller.getAristaFlowModel();
			testBpmnModel(flowObjects, bpmnModel);
		}
		return afModel;
	}

	/**
	 * deletes old result, checks if output is writable
	 * 
	 * @param output
	 *            output file
	 * @param log
	 *            log file
	 */
	private void outputFileCheck(File output, File log) {
		output.delete();
		log.delete();
		try {
			if (!output.createNewFile() && !output.createNewFile()) {
				throw new RuntimeException("Keine Schreibrechte unter " + output.getAbsolutePath());
			}
			if (!log.createNewFile() && !log.createNewFile()) {
				throw new RuntimeException("Keine Schreibrechte unter " + output.getAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("IOException beim Schreibrechte unter " + testResult + ": "
					+ e.getMessage());
		}
		output.delete();
		log.delete();
		if (output.exists())
			throw new RuntimeException("Kann Datei nicht löschen " + output.getAbsolutePath());
		if (log.exists())
			throw new RuntimeException("Kann Datei nicht löschen " + log.getAbsolutePath());
	}

	/**
	 * Basic test for a BMPN-Model where the existence of all objects with the names in flowObjects are
	 * checked. Sequence Flows between to subsequent flow objects are expected and checked.
	 * 
	 * @param flowObjects
	 *            array with names of flow objects that have to exist
	 * @param bpmnModel
	 *            model to check
	 */
	private void testBpmnModel(String[] flowObjects, BpmnModel bpmnModel) {
		assertNotNull(bpmnModel);
		FlowObject previousObject = null;
		for (int i = 0; i < flowObjects.length; i++) {
			boolean found = false;
			System.out.println("Flow Object check: " + flowObjects[i]);
			for (Pool pool : bpmnModel.getPools()) {
				for (FlowObject object : bpmnModel.getFlowObjectsByPool(pool)) {
					if (object.getName().equals(flowObjects[i])) {
						if (i > 0) {
							SequenceFlow flow;
							flow = bpmnModel.getSequenceFlow(previousObject, object);
							assertNotNull(flow);
							assertEquals(flow.getSourceFlowObject(), previousObject);
							assertEquals(flow.getDestinationFlowObject(), object);
						}
						previousObject = object;
						found = true;
						break;
					}
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Basic test for an AristaFlow-Model where the existence of all nodes with the names in nodes-array
	 * are checked. Control Edges between to subsequent nodes are expected and checked.
	 * 
	 * @param nodes
	 *            array with node names to check
	 * @param afModel
	 *            model to check
	 */
	private void testAristaFlowModel(String[] nodes, AristaFlowModel afModel) {
		Node previousNode = null;
		for (int i = 0; i < nodes.length; i++) {
			boolean found = false;
			System.out.println("Node check: " + nodes[i]);
			for (Node n : afModel.getAllNodes()) {
				if (n.getName().equals(nodes[i])) {
					if (i > 0) {
						Edge e = afModel.getEdge(previousNode, n);
						assertNotNull(e);
						assertEquals(e.getSourceNode(), previousNode);
						assertEquals(e.getDestinationNode(), n);
					}
					previousNode = n;
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Checks if a test result is equal to the expected one
	 * 
	 * @param filename
	 *            file name in expected and result directory to compare
	 * @return true if equal, false otherwise
	 * @throws FileNotFoundException
	 */
	private boolean compareResultToExpected(String filename) throws FileNotFoundException {
		File result = new File(testResult + filename);
		File expected = new File(testExpected + filename);
		boolean filesEqual = true;
		System.out.println("COMPARE: " + expected + " vs. " + result);
		Scanner scanExp = new Scanner(new FileReader(expected));
		Scanner scanRes = new Scanner(new FileReader(result));
		filesEqual = compareLines(filesEqual, scanExp, scanRes);
		scanExp.close();
		scanRes.close();
		return filesEqual;
	}

	/**
	 * Compares lines, @see compareResultToExpected(String filename)
	 */
	private boolean compareLines(boolean filesEqual, Scanner scanExp, Scanner scanRes) {
		while (scanExp.hasNextLine()) {
			if (!scanRes.hasNextLine()) {
				filesEqual = false;
				System.out.println("result file too short");
				break;
			}
			String lineExp = scanExp.nextLine();
			String lineRes = scanRes.nextLine();
			lineExp = filterString(lineExp);
			lineRes = filterString(lineRes);
			if (!lineExp.equals(lineRes)) {
				System.out.println("lines do not match (exp./res.):");
				System.out.println(lineExp);
				System.out.println(lineRes);
				filesEqual = false;
				break;
			}
		}
		return filesEqual;
	}

	/**
	 * Removes time stamps, random UUIDs, line numbers from a string
	 * 
	 * @param line
	 *            string where removal is applied to
	 * @return filtered line
	 */
	private static String filterString(String line) {
		String idExp;
		String timeExp;
		String lineExp;
		// remove random UUID (e.g. 42c36c93-f531-4b83-bbc6-5314bdeaaac8)
		idExp = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
		// remove time stamp (e.g. 120310-140732)
		timeExp = "^\\d{6}-\\d{6}\\s+";
		// remove line number
		lineExp = "(line \\d+)";
		String filteredString;
		filteredString = line.replaceAll(idExp, "");
		filteredString = filteredString.replaceAll(timeExp, "");
		filteredString = filteredString.replaceAll(lineExp, "");
		return filteredString.trim();
	}
}
