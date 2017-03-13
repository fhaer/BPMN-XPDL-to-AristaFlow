package de.bpmnaftool.model.transformation;

import de.bpmnaftool.model.bpmn.flowobject.Activity;
import de.bpmnaftool.model.bpmn.flowobject.Event;
import de.bpmnaftool.model.bpmn.flowobject.FlowObject;
import de.bpmnaftool.model.bpmn.flowobject.Gateway;
import de.bpmnaftool.model.bpmn.flowobject.GatewayJoin;
import de.bpmnaftool.model.bpmn.flowobject.GatewaySplit;
import de.bpmnaftool.model.bpmn.swimlane.Pool;

/**
 * Exception class for model transformation. This Exception is thrown, if a model can not be transformed.
 * 
 * @author Felix Härer
 */
public class NotTransformableException extends RuntimeException {

	/**
	 * Enumeration of all possible causes for this exception to be thrown. See method
	 * "public static String getCauseString(CauseType type)".
	 */
	public static enum CauseType {
		/**
		 * swimlanes are missing
		 */
		NoSwimLanes,
		/**
		 * flow objects are missing
		 */
		NoFlowObjects,
		/**
		 * sequence flows are missing
		 */
		NoSequenceFlows, 
		/**
		 * the number of start events is not allowed
		 */
		StartEventCount, 
		/**
		 * the number of end events is not allowed
		 */
		EndEventCount, 
		/**
		 * the number of incoming sequence flows is not allowed
		 */
		IncomingSequenceFlows, 
		/**
		 * the number of outgoing sequence flows is not allowed
		 */
		OutgoingSequenceFlows, 
		/**
		 * a gateway join is missing for a gateway split that was found earlier
		 */
		NoGatewayJoin, 
		/**
		 * a gateway split is missing for a gateway join that was found earlier
		 */
		NoGatewaySplit, 
		/**
		 * an unsupported object was found that can not be transformed
		 */
		UnsupportedObject, 
		/**
		 * cause type not known
		 */
		Unknown
	}

	/**
	 * Cause for the exception
	 */
	final public CauseType causeType;

	/**
	 * random serial version UUID
	 */
	private static final long serialVersionUID = 6375139054419881466L;

	/**
	 * line separator
	 */
	private final static String newline = System.getProperty("line.separator");

	/**
	 * Standard constructor
	 */
	public NotTransformableException() {
		causeType = CauseType.Unknown;
	}

	/**
	 * Pass any string to super-class
	 * 
	 * @param string
	 *            information about the cause of the exception
	 */
	public NotTransformableException(String string) {
		super(string);
		causeType = CauseType.Unknown;
	}

	/**
	 * Pass any string to super-class and provide type
	 * 
	 * @param type
	 *            why the exception was thrown
	 * @param pool
	 *            pool whith object that caused the exception
	 */
	public NotTransformableException(CauseType type, Pool pool) {
		super(tryGetPoolInfo(pool));
		causeType = type;
	}

	/**
	 * Pass any string to super-class and provide type
	 * 
	 * @param type
	 *            why the exception was thrown
	 * @param pool
	 *            pool with object that caused the exception
	 * @param message
	 *            why the exception was thrown
	 */
	public NotTransformableException(CauseType type, Pool pool, String message) {
		super(tryGetPoolInfo(pool) + newline + message + newline + getCauseString(type));
		causeType = type;
	}

	/**
	 * Provide type only
	 * 
	 * @param type
	 *            why the exception was thrown
	 */
	public NotTransformableException(CauseType type) {
		super(getCauseString(type));
		causeType = type;
	}

	/**
	 * Provide flow object and type
	 * 
	 * @param type
	 *            why the exception was thrown
	 */
	public NotTransformableException(CauseType type, FlowObject object) {
		super(getCauseString(type) + newline + newline + tryGetObjectInfo(object));
		causeType = type;
	}

	/**
	 * Provide flow object and type
	 * 
	 * @param type
	 *            why the exception was thrown
	 */
	public NotTransformableException(CauseType type, String message, FlowObject object) {
		super(message + newline + newline + getCauseString(type) + newline + newline
				+ tryGetObjectInfo(object));
		causeType = type;
	}

	/**
	 * Provide type and message
	 * 
	 * @param type
	 *            why the exception was thrown
	 * @param message
	 *            error message
	 */
	public NotTransformableException(CauseType type, String message) {
		super(message + newline + newline + getCauseString(type));
		causeType = type;
	}

	/**
	 * Returns information about the erroneous object
	 * 
	 * @param object
	 *            erroneous object
	 * @return string with name, type, id, lane, pool
	 */
	private static String tryGetObjectInfo(FlowObject object) {
		String msg = "";
		try {
			msg += "FlowObject: " + object.getName() + newline;
		} catch (Exception e) {
		}
		try {
			msg += "Typ: " + getObjectType(object) + newline;
		} catch (Exception e) {
		}
		try {
			msg += "ID: " + object.getId() + newline;
		} catch (Exception e) {
		}
		try {
			msg += "Lane: " + object.getLane() + newline;
		} catch (Exception e) {
		}
		try {
			Pool pool = object.getLane().getPool();
			msg += tryGetPoolInfo(pool);
		} catch (Exception e) {
		}
		return msg;
	}

	/**
	 * 
	 * Returns information about the pool
	 * 
	 * @param pool
	 *            pool where exception was thrown
	 * @return information about pool
	 */
	public static String tryGetPoolInfo(Pool pool) {
		String info = "";
		try {
			info += "Pool: " + pool.getName();
			info += " (ID " + pool.getId() + ")" + newline;
		} catch (Exception e) {
		}
		return info;
	}

	/**
	 * Returns a human readable string for the cause of the exception
	 * 
	 * @param type
	 *            cause type
	 * @return human readable string
	 */
	public static String getCauseString(CauseType type) {
		if (type.equals(CauseType.EndEventCount))
			return "Es muss genau ein End Event vorhanden sein";
		if (type.equals(CauseType.IncomingSequenceFlows))
			return "Die Anzahl eingehender Sequence Flows ist nicht zulässig";
		if (type.equals(CauseType.NoFlowObjects))
			return "Keine Flow Objects gefunden";
		if (type.equals(CauseType.NoSequenceFlows))
			return "Keine Sequence Flows gefunden";
		if (type.equals(CauseType.NoSwimLanes))
			return "Keine Swimlanes gefunden";
		if (type.equals(CauseType.OutgoingSequenceFlows))
			return "Die Anzahl ausgehender Sequence Flows ist nicht zulässig";
		if (type.equals(CauseType.StartEventCount))
			return "Es muss genau ein Start Event vorhanden sein";
		if (type.equals(CauseType.NoGatewayJoin))
			return "Zu einer Verzweigung wurde keine passende Zusammenführung gefunden";
		if (type.equals(CauseType.NoGatewaySplit))
			return "Zu einer Zusammenführung wurde keine passende Verzweigung gefunden";
		if (type.equals(CauseType.UnsupportedObject))
			return "Unbekanntes Objekt gefunden";
		return "";
	}

	/**
	 * Returns the type of a FlowObject as readable string
	 * 
	 * @param o
	 *            FlowObject
	 * @return readable string
	 */
	public static String getObjectType(FlowObject o) {
		if (o instanceof GatewaySplit)
			return "Gateway (Verzweigung)";
		if (o instanceof GatewayJoin)
			return "Gateway (Zusammenführung)";
		if (o instanceof Gateway)
			return "Gateway";
		if (o instanceof Event)
			return "Event";
		if (o instanceof Activity)
			return "Activity";
		return "";
	}

}
