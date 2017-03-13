package de.bpmnaftool.model.bpmn.swimlane;

/**
 * A lane is a SwimLane which is used to divide pools. A Lane is always inside a
 * pool.
 * 
 * @author Felix Härer
 */
public interface Lane extends SwimLane {

	/**
	 * Returns the pool, where this lane is located.
	 * 
	 * @return pool of this lane
	 */
	public Pool getPool();
}
