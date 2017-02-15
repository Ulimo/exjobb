package manager.gateway.client;

import java.util.List;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

public class GatewayClientResponse implements KompicsEvent {

	public final int id;
	public final List<KAddress> node;
	
	public GatewayClientResponse(int id, List<KAddress> node){
		this.id = id;
		this.node = node;
	}
}
