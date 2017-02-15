package manager.messages;

import java.util.List;

import se.sics.ktoolbox.util.network.KAddress;

public class GatewayNodeResponse {
	
	public final List<KAddress> node;
	public final int id;
	
	public GatewayNodeResponse(int id, List<KAddress> node){
		this.id = id;
		this.node = node;
	}
}
