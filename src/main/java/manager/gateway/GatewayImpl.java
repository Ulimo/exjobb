package manager.gateway;

import java.util.HashMap;
import java.util.List;

import manager.bootstrap.BootstrapSimplePort;
import manager.bootstrap.BootstrapSimpleRequest;
import manager.bootstrap.BootstrapSimpleResponse;
import manager.messages.GatewayNodeResponse;
import manager.messages.GatewayRequestNode;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.ktoolbox.omngr.bootstrap.event.Sample;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

public class GatewayImpl extends ComponentDefinition {

	private KAddress selfAdr;
	
	
	Positive<Network> networkPort = requires(Network.class);
	Positive<BootstrapSimplePort> bootstrapPort = requires(BootstrapSimplePort.class);
	
	//private List<KAddress> sample;
	
	private HashMap<Integer, SenderContainer> bootstrapIdToSender = new HashMap<Integer, SenderContainer>();
	
	public GatewayImpl(Init init){
		this.selfAdr = init.selfAdr;
		
		subscribe(handleStart, control);
		subscribe(handleBootstrapResponse, bootstrapPort);
		subscribe(handleNodeRequest, networkPort);
	}
	
	Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) {
			//trigger(new BootstrapSimpleRequest(), bootstrapPort);
		}
	};
	
	Handler<BootstrapSimpleResponse> handleBootstrapResponse = new Handler<BootstrapSimpleResponse>() {

		@Override
		public void handle(BootstrapSimpleResponse event) {
			SenderContainer sender = bootstrapIdToSender.get(event.id);
		
			KContentMsg container = new BasicContentMsg(new BasicHeader(selfAdr, sender.sender, Transport.UDP), new GatewayNodeResponse(sender.id, event.sample));
			trigger(container, networkPort);
			bootstrapIdToSender.remove(event.id); //Remove the cached id to sender
		}
		
	};
	
	ClassMatchedHandler<GatewayRequestNode, KContentMsg<?, ?, GatewayRequestNode>> handleNodeRequest
    = new ClassMatchedHandler<GatewayRequestNode, KContentMsg<?, ?, GatewayRequestNode>>() {

        @Override
        public void handle(GatewayRequestNode content, KContentMsg<?, ?, GatewayRequestNode> container) {
        	
        	BootstrapSimpleRequest req = new BootstrapSimpleRequest();
        	bootstrapIdToSender.put(req.id, new SenderContainer(content.id, container.getHeader().getSource()));
        	trigger(req, bootstrapPort);
        }
    };
	
    private static class SenderContainer {
    	
    	public int id;
    	public KAddress sender;
    	
    	public SenderContainer(int id, KAddress sender){
    		this.id = id;
    		this.sender = sender;
    	}
    	
    }
	
	public static class Init extends se.sics.kompics.Init<GatewayImpl> {
		
		public final KAddress selfAdr;
		
		public Init(KAddress selfAdr){
			this.selfAdr = selfAdr;
		}
	}
}
