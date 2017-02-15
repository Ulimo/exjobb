package manager.simulation;

import se.sics.kompics.network.Msg;
import se.sics.kompics.simulator.network.NetworkModel;

public class SimNetworkModel implements NetworkModel {

	@Override
	public long getLatencyMs(Msg message) {
		return 1;
	}

}
