package manager.test.bootstrapclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import manager.production.bootstrap.BootstrapServerHost.Init;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Start;
import se.sics.kompics.config.Config;
import se.sics.kompics.config.ConfigUpdate;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapServerComp;
import se.sics.ktoolbox.util.network.KAddress;

public class BootstrapClientHost extends ComponentDefinition {

	
	private static final Logger LOG = LoggerFactory.getLogger(BootstrapClientHost.class);
	
	private KAddress selfAdr;
	private KAddress bootstrapServer;
	
	private final Component network;
	private final Component timer;
	private final Component bootstrapParent;
	
	public BootstrapClientHost(Init init){
		this.selfAdr = init.selfAdr;
		this.bootstrapServer = init.bootstrapServer;
		
		LOG.info("Starting host!");
		
		network = create(NettyNetwork.class, new NettyInit(init.selfAdr));
		timer = create(JavaTimer.class, Init.NONE);
		
		Config.Builder cb = config().modify(id());
		
		cb.setValue("system.seed", 0);
		cb.setValue("system.port", this.selfAdr.getPort());
		
		bootstrapParent = create(BootstrapClientTest.class, new BootstrapClientTest.Init(selfAdr, bootstrapServer), cb.finalise());
		
		
		//ConfigUpdate update = new ConfigUpdate(null, 0, null);
		connect(bootstrapParent.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
		connect(bootstrapParent.getNegative(Timer.class), timer.getPositive(Timer.class), Channel.TWO_WAY);
		
		
		
		//trigger(Start.event, bootstrapParent.control());
	}
	
	/*
	Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("starting...");
            trigger(Start.event, bootstrapParent.control());
        }
    };*/
	
	
	
	
	public static class Init extends se.sics.kompics.Init<BootstrapClientHost> {
		
		public final KAddress selfAdr;
		public final KAddress bootstrapServer;
		
		public Init(KAddress selfAdr, KAddress bootstrapServer){
			this.selfAdr = selfAdr;
			this.bootstrapServer = bootstrapServer;
		}
		
	}
	
}
