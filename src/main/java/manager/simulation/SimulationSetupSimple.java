package manager.simulation;

import java.net.InetAddress;
import java.net.UnknownHostException;

import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.identifiable.basic.IntIdentifier;
import se.sics.ktoolbox.util.identifiable.basic.OverlayIdFactory;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.basic.BasicAddress;
import se.sics.ktoolbox.util.network.nat.NatAwareAddressImpl;

public class SimulationSetupSimple extends SimulationSetup {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4556217431220971022L;
	public final int appPort;
	public final String bootstrapServerIP;
	public final long seed;
	public SimulationSetupSimple(String bootstrapServerIP, int appPort, long seed) {
		this.appPort = appPort;
		this.bootstrapServerIP = bootstrapServerIP;
		this.seed = seed;
	}
	
	@Override
	KAddress getBootstrapAddress() {
		try {
            return NatAwareAddressImpl.open(new BasicAddress(InetAddress.getByName(bootstrapServerIP), appPort, new IntIdentifier(0)));
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
	}

	@Override
	KAddress getNodeAdr(int nodeId) {
		try {
            int firstLady = nodeId / 253;
            int secondLady = nodeId % 253;
            return NatAwareAddressImpl.open(new BasicAddress(InetAddress.getByName("193.0." + firstLady + "." + secondLady), appPort, new IntIdentifier(nodeId)));
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
	}

	@Override
	byte getOverlayPrefix() {
		return 0x10;
	}

	@Override
	Identifier getOverlayId() {
		return OverlayIdFactory.getId(getOverlayPrefix(), OverlayIdFactory.Type.TGRADIENT, new byte[]{0, 0, 1});
	}

	@Override
	long getNodeSeed(int nodeId) {
		return this.seed + nodeId;
	}

	@Override
	long getSeed() {
		return this.seed;
	}

	@Override
	int getPort() {
		return this.appPort;
	}

	@Override
	KAddress getWebServerAdr() {
		try {
            return NatAwareAddressImpl.open(new BasicAddress(InetAddress.getByName("193.1.0.2"), appPort, new IntIdentifier(0)));
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
	}

	@Override
	KAddress getGatewayServer() {
		try {
            return NatAwareAddressImpl.open(new BasicAddress(InetAddress.getByName("193.1.0.3"), appPort, new IntIdentifier(0)));
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
	}
	
	

}
