package de.bpmnaftool.control;
        
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.*;


import de.bpmnaftool.model.aristaflow.*;
import de.bpmnaftool.model.aristaflow.fileio.*;
import de.bpmnaftool.model.bpmn.*;
import de.bpmnaftool.model.bpmn.fileio.*;
import de.bpmnaftool.model.transformation.*;
import de.bpmnaftool.view.*;


/**
 * Implementation of BpmnAfTool which is used to initialize and control the
 * application
 * 
 * @author Felix Härer
 */
public class BpmnAfToolImpl extends Observable implements BpmnAfTool {

	/**
	 * instance to this class (singleton)
	 */
	private static BpmnAfTool instance;
	/**
	 * Instance of view (singleton)
	 */
	private View view;
	/**
	 * BPMN-Model used as input for transformation
	 */
	private BpmnModel bpmnModel;
	/**
	 * AristaFlow-Model used as output for the transformation
	 */
	private AristaFlowModel aristaFlowModel;
	/**
	 * Output file where AristaFlow-Model will be stored
	 */
	private File outputFile;
	/**
	 * Input file where BPMN-Model is stored
	 */
	private File inputFile;
	/**
	 * Path to Log file for messages during transformation
	 */
	private File logFile;
	/**
	 * Logger instance
	 */
	private static Logger log;

	/**
	 * Get the (only) instance to this (singleton)
	 * 
	 * @return instance to this (controller object)
	 */
	public static BpmnAfTool getInstance() {
		if (instance == null) {
			synchronized (BpmnAfToolImpl.class) {
				if (instance == null) {
					instance = new BpmnAfToolImpl();
				}
			}
		}
		return instance;
	}

	/**
	 * Private constructor to prevent the creation of instances from outside the
	 * class (use getInstance method instead)
	 */
	private BpmnAfToolImpl() {
	}

	/**
	 * Initializes the BpmnAfTool and starts the view
	 * 
	 * @param args
	 *            arguments are ignored
	 */
	public static void main(String[] args) {
		BpmnAfTool controller = BpmnAfToolImpl.getInstance();
		controller.initialize();
		controller.initializeView();
	}

	@Override
	public void initializeView() {
		View view = getView();
		try {
			view.initialize();
			// observer for updating transformation status
			addObserver((Observer) view);
		} catch (Exception e) {
			view.showError(e, "Initialisierung fehlgeschlagen");
		}
	}

	@Override
	public void initialize() {
		bpmnModel = null;
		aristaFlowModel = null;
		outputFile = null;
		inputFile = null;
	}

	@Override
	public View getView() {
		if (view == null) {
			view = ViewImpl.getInstance();
		}
		return view;
	}

	@Override
	public BpmnModel getBpmnModel() {
		if (bpmnModel == null) {
			bpmnModel = new BpmnModelImpl();
		}
		return bpmnModel;
	}

	@Override
	public AristaFlowModel getAristaFlowModel() {
		if (aristaFlowModel == null) {
			throw new IllegalAccessError("Modell nicht transformiert");
		}
		return aristaFlowModel;
	}

	@Override
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
		boolean isValid, isWellFormed;
		XpdlParser xpdlParser = new XpdlParserImpl(this.inputFile);
		try {
			bpmnModel = xpdlParser.parse();
		} catch (Exception e) {
			getView().showError(e, "Fehler während des Parsens");
		} finally {
			isWellFormed = xpdlParser.isXmlWellFormed();
			isValid = xpdlParser.isXpdlValid();
			getView().setValidationStatus(isWellFormed, isValid);
		}
	}

	@Override
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
		this.logFile = new File(outputFile.getPath() + ".log");
		// close all open log and initialize new log
		if (log != null) {
			for (Handler logHandler : log.getHandlers()) {
				logHandler.close();
			}
		}
		try {
			initializeLogger();
		} catch (IOException e) {
			view.showError(e, "Kann Log-Datei nicht erstellen");
		}
	}

	@Override
	public void transformModel() {
		try {
			BpmnTransformation bpmnTransform;
			bpmnTransform = new BpmnTransformationImpl(getBpmnModel());
			bpmnTransform.transform();
			setAndWriteTransformedModel(bpmnTransform.getDestinationModel());
			log("Transformation erfolgreich beendet", 0, 2);
		} catch (Exception e) {
			getView().showError(e, "Fehler bei der Transformation");
		}
		log("Schreibe Log-Datei \"" + logFile + "\"", 0, 1);
		for (Handler logHandler : log.getHandlers())
			logHandler.close();
	}

	/**
	 * Sets AristaFlow model and writes it to file
	 * 
	 * @param model
	 *            AristaFlow model to set and write
	 * @throws IOException 
	 */
	private void setAndWriteTransformedModel(AristaFlowModel model) throws IOException
		 {
		aristaFlowModel = model;
		AristaFlowXmlWriter aristaFlowXmlWriter;
		aristaFlowXmlWriter = new AristaFlowXmlWriterImpl(outputFile);
		aristaFlowXmlWriter.write(aristaFlowModel);
	}

	@Override
	public void log(String message, int indent, int lineBreaks) {
		for (int i = 0; i < indent; i++)
			message = "  " + message;
		for (int i = 0; i < lineBreaks; i++)
			message += " " + System.getProperty("line.separator");
		if (log != null)
			log.info(message);
		notifyObservers(message);
		setChanged();
		System.out.print(message);
	}

	/**
	 * Initializes a new logger with a custom format
	 * 
	 * @throws IOException
	 *             thrown if log can not be created
	 */
	private void initializeLogger() throws IOException {
		log = Logger.getLogger("BpmnAfTool " + System.currentTimeMillis());
		log.setUseParentHandlers(false);
		FileHandler handler = new FileHandler(logFile.getPath(), true);
		handler.setFormatter(new Formatter() {
			public String format(LogRecord rec) {
				DateFormat dateFormat = new SimpleDateFormat("yyMMdd-HHmmss");
				String date = dateFormat.format(new Date(rec.getMillis()));
				String log = "", newline = System.getProperty("line.separator");
				for (String line : rec.getMessage().split(newline))
					log += String.format("%-14s %s %s", date, line, newline);
				return log;
			}
		});
		log.addHandler(handler);
	}
}
