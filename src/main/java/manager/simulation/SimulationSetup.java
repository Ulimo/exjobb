package manager.simulation;

import java.io.Serializable;

import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;

public abstract class SimulationSetup implements Serializable {

	abstract KAddress getBootstrapAddress();
	
	abstract KAddress getGatewayServer();
	
	abstract KAddress getNodeAdr(int nodeId);
	
	abstract int getPort();
	
	abstract long getNodeSeed(int nodeId);
	
	abstract byte getOverlayPrefix();
	
	abstract Identifier getOverlayId();
	
	abstract long getSeed();
	
	abstract KAddress getWebServerAdr();
	
}
