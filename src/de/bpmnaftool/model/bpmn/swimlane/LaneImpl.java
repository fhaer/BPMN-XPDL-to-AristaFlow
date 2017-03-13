package de.bpmnaftool.model.bpmn.swimlane;

/**
 * Implementation of a Lane
 * 
 * @author Felix Härer
 */
public class LaneImpl extends SwimLaneImpl implements Lane {

	/**
	 * Pool where this lane is located
	 */
	Pool pool;

	/**
	 * Creates a Lane. It is required to specify the pool where the lane is
	 * located.
	 * 
	 * @param pool
	 */
	public LaneImpl(Pool pool) {
		this.pool = pool;
	}

	@Override
	public Pool getPool() {
		if (pool == null)
			throw new IllegalStateException("lane without pool");
		return pool;
	}

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
