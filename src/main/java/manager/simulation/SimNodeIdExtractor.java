package manager.simulation;

import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.network.identifier.Identifier;
import se.sics.kompics.simulator.network.identifier.IdentifierExtractor;
import se.sics.ktoolbox.util.identifiable.basic.IntIdentifier;
import se.sics.ktoolbox.util.network.KAddress;

public class SimNodeIdExtractor implements IdentifierExtractor {

	@Override
	public Identifier extract(Address adr) {
		KAddress usedAdr = (KAddress)adr;
        int nodeId = ((IntIdentifier)usedAdr.getId()).id;
        return new SimNodeIdentifier(nodeId);
	}

}
