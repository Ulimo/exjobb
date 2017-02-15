package manager.production.bootstrap;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapServerComp;
import se.sics.ktoolbox.util.network.KAddress;

public class BootstrapServerHost extends ComponentDefinition {

	private final Component network;
	private final Component timer;
	private final Component bootstrapParent;
	
	public BootstrapServerHost(Init init){
		network = create(NettyNetwork.class, new NettyInit(init.selfAdr));
		timer = create(JavaTimer.class, Init.NONE);
		bootstrapParent = create(BootstrapServerComp.class, new BootstrapServerComp.Init(init.selfAdr));
		
		connect(bootstrapParent.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
		connect(bootstrapParent.getNegative(Timer.class), timer.getPositive(Timer.class), Channel.TWO_WAY);
	}
	
	
	public static class Init extends se.sics.kompics.Init<BootstrapServerHost> {

        public final KAddress selfAdr;

        public Init(KAddress selfAdr) {
            this.selfAdr = selfAdr;
        }
    }
	
}
