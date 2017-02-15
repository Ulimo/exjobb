package manager.node;

import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import manager.overlay.OverlayView;
import manager.webdebug.messages.UpdateNeighborsMsg;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.gradient.GradientPort;
import se.sics.ktoolbox.gradient.event.TGradientSample;
import se.sics.ktoolbox.gradient.util.GradientContainer;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

public class WebNeighborUpdateComp extends ComponentDefinition {
	
	private static final Logger LOG = LoggerFactory.getLogger(WebNeighborUpdateComp.class);

	private KAddress selfAdr;
	private KAddress webServerAdr;
	
	Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
    Positive<CroupierPort> croupierPort = requires(CroupierPort.class);
    Positive<GradientPort> gradientPort = requires(GradientPort.class);
	
	public WebNeighborUpdateComp(Init init){
		this.selfAdr = init.selfAdr;
		this.webServerAdr = init.webServerAdr;
		
		subscribe(handleStart, control);
		subscribe(handleCroupierSample, croupierPort);
		subscribe(handleGradientSample, gradientPort);
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			LOG.info("Web neighbor update started...");
		}
		
	};
	
	
	
	Handler<CroupierSample<OverlayView>> handleCroupierSample = new Handler<CroupierSample<OverlayView>>() {
        @Override
        public void handle(CroupierSample<OverlayView> castSample) {
        	
            if (castSample.publicSample.isEmpty()) {
                return;
            }
            
            ArrayList<Integer> neighbors = new ArrayList<>();
            Iterator<Identifier> it = castSample.publicSample.keySet().iterator();
            
            while(it.hasNext())
            {
                Identifier identifier = it.next();
                KAddress address = castSample.publicSample.get(identifier).getSource();
                neighbors.add(Integer.parseInt(address.getId().toString()));
            }
            KHeader<KAddress> header = new BasicHeader<KAddress>(selfAdr, webServerAdr, Transport.UDT);
            KContentMsg<KAddress, KHeader<KAddress>, UpdateNeighborsMsg> msg = new BasicContentMsg<KAddress, KHeader<KAddress>, UpdateNeighborsMsg>(header, new UpdateNeighborsMsg(Integer.parseInt(selfAdr.getId().toString()), neighbors, UpdateNeighborsMsg.CROUPIER_UPDATE));
            trigger(msg, networkPort);
        }
    };
    
    Handler<TGradientSample> handleGradientSample = new Handler<TGradientSample>() {
        @Override
        public void handle(TGradientSample sample) {
            
        	ArrayList<Integer> neighbors = new ArrayList<>();
            Iterator<Object> it = sample.getGradientNeighbours().iterator();
            
            while(it.hasNext())
            {
                Object identifier = it.next();
                GradientContainer cont = (GradientContainer)identifier;
                neighbors.add(Integer.parseInt(cont.getSource().getId().toString()));
            }
            
            KHeader<KAddress> header = new BasicHeader<KAddress>(selfAdr, webServerAdr, Transport.UDT);
            KContentMsg<KAddress, KHeader<KAddress>, UpdateNeighborsMsg> msg = new BasicContentMsg<KAddress, KHeader<KAddress>, UpdateNeighborsMsg>(header, new UpdateNeighborsMsg(Integer.parseInt(selfAdr.getId().toString()), neighbors, UpdateNeighborsMsg.GRADIENT_UPDATE));
            trigger(msg, networkPort);
        }
    };
	
	
	public static class Init extends se.sics.kompics.Init<WebNeighborUpdateComp> {
		
		public final KAddress selfAdr;
		public final KAddress webServerAdr;
		
		public Init(KAddress selfAdr, KAddress webServerAdr){
			this.selfAdr = selfAdr;
			this.webServerAdr = webServerAdr;
		}
	}
	
}
