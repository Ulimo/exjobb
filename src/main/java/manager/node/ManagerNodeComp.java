package manager.node;


//Log4j
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import manager.messages.KillMsg;
//Kompics
import se.sics.kompics.Start;
import se.sics.kompics.Channel;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.Handler;
import se.sics.kompics.Kill;
import se.sics.kompics.network.Network;

//Ktoolbox
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.nat.NatAwareAddress;
import se.sics.ktoolbox.util.overlays.view.OverlayViewUpdatePort;
import se.sics.ktoolbox.cc.heartbeat.CCHeartbeatPort;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.gradient.GradientPort;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapClientComp;
import se.sics.ktoolbox.overlaymngr.OverlayMngrComp;
import se.sics.ktoolbox.overlaymngr.OverlayMngrPort;
import se.sics.ktoolbox.util.identifiable.Identifier;


public class ManagerNodeComp extends ComponentDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(ManagerNodeComp.class);
	private String logPrefix = " ";
	
	
	Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
	
    private KAddress selfAdr;
    private KAddress bootstrapServer;
    private KAddress webServerAdr;
    private Identifier gradientId;
    
    private Component bootstrapClientComp;
    private Component overlayMngrComp;
    private Component managerNodeImpl;
    
    public ManagerNodeComp(Init init){
    	selfAdr = init.selfAdr;
    	this.bootstrapServer = init.bootstrapServer;
    	this.gradientId = init.overlayId;
    	this.webServerAdr = init.webServerAdr;
        logPrefix = "<nid:" + selfAdr.getId() + ">";
        LOG.info("{}initiating...", logPrefix);
        
        subscribe(handleStart, control);
    }
    
    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}starting...", logPrefix);
            connectBootstrapClient();
            connectOverlayMngr();
            connectApp();

            trigger(Start.event, bootstrapClientComp.control());
            trigger(Start.event, overlayMngrComp.control());
            trigger(Start.event, managerNodeImpl.control());
        }
    };
    
    /***
     * Connect to the bootstrap client which connects to the bootstrap server
     */
    private void connectBootstrapClient() {
        bootstrapClientComp = create(BootstrapClientComp.class, new BootstrapClientComp.Init(selfAdr, bootstrapServer));
        connect(bootstrapClientComp.getNegative(Timer.class), timerPort, Channel.TWO_WAY);
        connect(bootstrapClientComp.getNegative(Network.class), networkPort, Channel.TWO_WAY);
    }
    
    /***
     * Create connection to the overlay manager
     */
    private void connectOverlayMngr() {
        OverlayMngrComp.ExtPort extPorts = new OverlayMngrComp.ExtPort(timerPort, networkPort,
                bootstrapClientComp.getPositive(CCHeartbeatPort.class));
        overlayMngrComp = create(OverlayMngrComp.class, new OverlayMngrComp.Init((NatAwareAddress) selfAdr, extPorts));
    }
    
    private void connectApp() {
    	ManagerNodeImpl.ExtPort extPorts = new ManagerNodeImpl.ExtPort(timerPort, networkPort, 
    			overlayMngrComp.getPositive(CroupierPort.class), overlayMngrComp.getPositive(GradientPort.class)
    			, overlayMngrComp.getNegative(OverlayViewUpdatePort.class));
    	
    	managerNodeImpl = create(ManagerNodeImpl.class, new ManagerNodeImpl.Init(extPorts, selfAdr, this.gradientId, this.webServerAdr));
    	connect(managerNodeImpl.getNegative(OverlayMngrPort.class), overlayMngrComp.getPositive(OverlayMngrPort.class), Channel.TWO_WAY);
    }
    
    public static class Init extends se.sics.kompics.Init<ManagerNodeComp> {

        public final KAddress selfAdr;
        public final KAddress bootstrapServer;
        public final Identifier overlayId;
        public final KAddress webServerAdr;
        
        public Init(KAddress selfAdr, KAddress bootstrapServer, Identifier overlayId, KAddress webServerAdr) {
            this.selfAdr = selfAdr;
            this.bootstrapServer = bootstrapServer;
            this.overlayId = overlayId;
            this.webServerAdr = webServerAdr;
        }
    }
}
