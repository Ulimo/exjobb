package manager.user;

import manager.bootstrap.BootstrapSimpleClient;
import manager.bootstrap.BootstrapSimplePort;
import manager.gateway.client.GatewayClientComp;
import manager.gateway.client.GatewayClientPort;
import manager.user.allocation.AllocationComp;
import manager.user.allocation.AllocationPort;
import manager.user.nodeselection.NodeSelectComp;
import manager.user.nodeselection.NodeSelectPort;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;

public class UserComp extends ComponentDefinition {

	private KAddress selfAdr;
    private KAddress gatewayServer;
    private Identifier overlayId;
    
    //private Component bootstrapClient;
    //private Component nodeSelectComp;
    private Component gatewayClientComp;
    private Component userImpl;
    private Component allocationComp;
    
    Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
	
	public UserComp(Init init){
		this.selfAdr = init.selfAdr;
		this.gatewayServer = init.gatewayServer;
		this.overlayId = init.overlayId;
		
		subscribe(handleStart, control);
	}
	
	Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) {
			//connectBootstrapClient();
			//connectNodeSelectComp();
			connectGatewayClient();
			connectAllocationComp();
			connectUserImpl();
			
			//trigger(Start.event, bootstrapClient.control());
			//trigger(Start.event, nodeSelectComp.control());
			trigger(Start.event, gatewayClientComp.control());
			trigger(Start.event, allocationComp.control());
			trigger(Start.event, userImpl.control());
		}
	};
	
	/*private void connectBootstrapClient(){
		bootstrapClient = create(BootstrapSimpleClient.class, new BootstrapSimpleClient.Init(selfAdr, bootstrapServer, overlayId));
		connect(bootstrapClient.getNegative(Timer.class), timerPort, Channel.TWO_WAY);
        connect(bootstrapClient.getNegative(Network.class), networkPort, Channel.TWO_WAY);
	}
	
	private void connectNodeSelectComp(){
		nodeSelectComp = create(NodeSelectComp.class, new NodeSelectComp.Init());
		connect(nodeSelectComp.getNegative(BootstrapSimplePort.class), bootstrapClient.getPositive(BootstrapSimplePort.class), Channel.TWO_WAY);
	}*/
	
	private void connectGatewayClient(){
		gatewayClientComp = create(GatewayClientComp.class, new GatewayClientComp.Init(selfAdr, gatewayServer));
		connect(gatewayClientComp.getNegative(Network.class), networkPort, Channel.TWO_WAY);
	}
	
	private void connectUserImpl(){
		userImpl = create(UserImpl.class, new UserImpl.Init(selfAdr));
		connect(userImpl.getNegative(AllocationPort.class), allocationComp.getPositive(AllocationPort.class), Channel.TWO_WAY);
		connect(userImpl.getNegative(Timer.class), timerPort, Channel.TWO_WAY);
	}
	
	private void connectAllocationComp(){
		allocationComp = create(AllocationComp.class, new AllocationComp.Init(selfAdr));
		connect(allocationComp.getNegative(Timer.class), timerPort, Channel.TWO_WAY);
		connect(allocationComp.getNegative(Network.class), networkPort, Channel.TWO_WAY);
		connect(allocationComp.getNegative(GatewayClientPort.class), gatewayClientComp.getPositive(GatewayClientPort.class), Channel.TWO_WAY);
		//connect(allocationComp.getNegative(NodeSelectPort.class), nodeSelectComp.getPositive(NodeSelectPort.class), Channel.TWO_WAY);
	}
	
	public static class Init extends se.sics.kompics.Init<UserComp> {

        public final KAddress selfAdr;
        public final KAddress gatewayServer;
        public final Identifier overlayId;

        public Init(KAddress selfAdr, KAddress gatewayServer, Identifier overlayId) {
            this.selfAdr = selfAdr;
            this.gatewayServer = gatewayServer;
            this.overlayId = overlayId;
        }
    }
	
}
