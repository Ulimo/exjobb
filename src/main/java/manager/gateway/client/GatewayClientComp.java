package manager.gateway.client;

import java.util.HashMap;

import manager.bootstrap.BootstrapSimplePort;
import manager.bootstrap.BootstrapSimpleRequest;
import manager.messages.GatewayNodeResponse;
import manager.messages.GatewayRequestNode;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

public class GatewayClientComp extends ComponentDefinition {

	private KAddress selfAdr;
	private KAddress gatewayServer;
	
	Positive<Network> networkPort = requires(Network.class);
	Negative<GatewayClientPort> gatewayClientPort = provides(GatewayClientPort.class);
	
	private HashMap<Integer, Integer> serverToClientId = new HashMap<Integer, Integer>();
	
	public GatewayClientComp(Init init){
		this.selfAdr = init.selfAdr;
		this.gatewayServer = init.gatewayServer;
		
		subscribe(handleStart, control);
		subscribe(handleRequest, gatewayClientPort);
		subscribe(handleNodeResponse, networkPort);
	}
	
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			
		}
		
	};
	
	Handler<GatewayClientRequest> handleRequest = new Handler<GatewayClientRequest>(){

		@Override
		public void handle(GatewayClientRequest event) {
			requestNodeFromGateway(event.id); //Get a node from the gateway server
		}
		
	};
	
	ClassMatchedHandler<GatewayNodeResponse, KContentMsg<?, ?, GatewayNodeResponse>> handleNodeResponse
    = new ClassMatchedHandler<GatewayNodeResponse, KContentMsg<?, ?, GatewayNodeResponse>>() {

        @Override
        public void handle(GatewayNodeResponse content, KContentMsg<?, ?, GatewayNodeResponse> container) {
        	int clientId = serverToClientId.get(content.id);
        	trigger(new GatewayClientResponse(clientId, content.node), gatewayClientPort); //Send the response
        	serverToClientId.remove(content.id); //Remove cached id
        }
    };
	
	private void requestNodeFromGateway(int id){
		GatewayRequestNode req = new GatewayRequestNode();
		serverToClientId.put(req.id, id);
		KContentMsg container = new BasicContentMsg(new BasicHeader(selfAdr, gatewayServer, Transport.UDP), req);
		trigger(container, networkPort);
	}
	
	
	public static class Init extends se.sics.kompics.Init<GatewayClientComp> {
	
		public final KAddress selfAdr;
		public final KAddress gatewayServer;
		
		public Init(KAddress selfAdr, KAddress gatewayServer){
			this.selfAdr = selfAdr;
			this.gatewayServer = gatewayServer;
		}
	}
}
