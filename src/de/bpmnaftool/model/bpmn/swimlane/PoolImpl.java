package de.bpmnaftool.model.bpmn.swimlane;

/**
 * Implementation Pool interface. This class is for type definition only and
 * does not contain any methods.
 * 
 * @author Felix Härer
 */
public class PoolImpl extends SwimLaneImpl implements Pool {

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (object instanceof SwimLane) {
			if (id != null && ((SwimLane) object).getId().equals(id))
				return true;
		}
		return false;
	}
}
