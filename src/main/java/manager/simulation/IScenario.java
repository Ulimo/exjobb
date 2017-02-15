package manager.simulation;

import java.io.Serializable;

import se.sics.kompics.simulator.SimulationScenario;

public abstract class IScenario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2921966352887641184L;

	abstract SimulationScenario getScenario();
}
