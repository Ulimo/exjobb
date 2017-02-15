package manager.test.bootstrapclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import manager.node.ManagerNodeComp;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapClientComp;
import se.sics.ktoolbox.util.network.KAddress;

public class BootstrapClientTest extends ComponentDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(BootstrapClientTest.class);
	private String logPrefix = " ";
	
	private KAddress selfAdr;
	private KAddress bootstrapServer;
	
	private Component bootstrapClientComp;
	
	Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
	
	public BootstrapClientTest(Init init){
		this.selfAdr = init.selfAdr;
		this.bootstrapServer = init.bootstrapServer;
		
		subscribe(handleStart, control);
	}
	
	Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}starting...", logPrefix);
            connectBootstrapClient();
            
            trigger(Start.event, bootstrapClientComp.control());
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
	
	
	public static class Init extends se.sics.kompics.Init<BootstrapClientTest> {
		public final KAddress selfAdr;
        public final KAddress bootstrapServer;
        
        public Init(KAddress selfAdr, KAddress bootstrapServer){
        	this.selfAdr = selfAdr;
        	this.bootstrapServer = bootstrapServer;
        }
	}
}
