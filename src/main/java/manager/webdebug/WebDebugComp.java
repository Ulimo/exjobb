package manager.webdebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import manager.webdebug.messages.UpdateNeighborsMsg;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;

public class WebDebugComp extends ComponentDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(WebDebugComp.class);
	
	private KAddress selfAdr;
	private String webIp;
	private int webPort;
	
	Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
	
	private static WebServer webserver;
	
	
	public WebDebugComp(Init init){
		this.selfAdr = init.selfAdr;
		this.webIp = init.webIp;
		this.webPort = init.webPort;
		
		subscribe(handleStart, control);
		subscribe(handleNeighborUpdate, networkPort);
	}
	
	Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) {
			LOG.info("Starting web server.");
			webserver = new WebServer();
			
			WebServer.setData("globaldata", new GlobalData());
			startJetty(webPort);
		}
	};
	
	ClassMatchedHandler handleNeighborUpdate = new ClassMatchedHandler<UpdateNeighborsMsg, KContentMsg<?, ?, UpdateNeighborsMsg>>() {

		@Override
		public void handle(UpdateNeighborsMsg content, KContentMsg<?, ?, UpdateNeighborsMsg> context) {
			
			GlobalData data = WebServer.<GlobalData>getData("globaldata");
			
			if(content.overlayType == UpdateNeighborsMsg.CROUPIER_UPDATE){
				//data.InsertNodeNeighbors(content.nodeId, content.neighbors);
			}
			else if(content.overlayType == UpdateNeighborsMsg.GRADIENT_UPDATE){
				data.InsertNodeNeighbors(content.nodeId, content.neighbors);
			}
		}
	};
	
	
	
	private void startJetty(int port){
		webserver.StartJetty(port, "/");
	}
	
	
	public static class Init extends se.sics.kompics.Init<WebDebugComp>{
		
		public final KAddress selfAdr;
		public final String webIp;
		public final int webPort;
		
		public Init(KAddress selfAdr, String webIp, int webPort){
			this.selfAdr = selfAdr;
			this.webIp = webIp;
			this.webPort = webPort;
		}
	}
}
