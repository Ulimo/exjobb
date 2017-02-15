package manager.gateway;

import manager.bootstrap.BootstrapSimpleClient;
import manager.bootstrap.BootstrapSimplePort;
import manager.messages.GatewayRequestNode;
import se.sics.kompics.Channel;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.omngr.bootstrap.event.Sample;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.identifiable.basic.OverlayIdFactory;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;

public class GatewayComp extends ComponentDefinition {

	private KAddress selfAdr;
	private KAddress bootstrap;
	
	private Component bootstrapSimpleComp;
	private Component gatewayImpl;
	private Identifier overlayId;
	
	Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
	
	
	public GatewayComp(Init init){
		this.selfAdr = init.selfAdr;
		this.bootstrap = init.bootstrap;
		this.overlayId = OverlayIdFactory.getId((byte) 0x0E, OverlayIdFactory.Type.TGRADIENT, new byte[]{0, 0, 1});
		
		subscribe(handleStart, control);
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			connectBootstrapClient(); //Connect to the bootstrap server
			connectGatewayImpl();
			
			trigger(Start.event, bootstrapSimpleComp.control());
			trigger(Start.event, gatewayImpl.control());
		}
		
	};
	
	private void connectBootstrapClient(){
		bootstrapSimpleComp = create(BootstrapSimpleClient.class, new BootstrapSimpleClient.Init(selfAdr, bootstrap, this.overlayId));
		connect(bootstrapSimpleComp.getNegative(Timer.class), timerPort, Channel.TWO_WAY);
        connect(bootstrapSimpleComp.getNegative(Network.class), networkPort, Channel.TWO_WAY);
	}
	
	private void connectGatewayImpl(){
		gatewayImpl = create(GatewayImpl.class, new GatewayImpl.Init(selfAdr));
		connect(gatewayImpl.getNegative(Network.class), networkPort, Channel.TWO_WAY);
		connect(gatewayImpl.getNegative(BootstrapSimplePort.class), bootstrapSimpleComp.getPositive(BootstrapSimplePort.class), Channel.TWO_WAY);
	}
	
	
	public static class Init extends se.sics.kompics.Init<GatewayComp> {
		
		public final KAddress selfAdr;
		public final KAddress bootstrap;
		
		public Init(KAddress selfAdr, KAddress bootstrap){
			this.selfAdr = selfAdr;
			this.bootstrap = bootstrap;
		}
	}
	
}
