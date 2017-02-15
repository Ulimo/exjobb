package manager.gateway.client;


import se.sics.kompics.PortType;

public class GatewayClientPort extends PortType {
	{
		indication(GatewayClientRequest.class);
		request(GatewayClientRequest.class);
		
		indication(GatewayClientResponse.class);
		request(GatewayClientResponse.class);
	}
}
