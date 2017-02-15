package manager.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import manager.executor.ExecutorComp;
import manager.executor.ExecutorPort;
import manager.overlay.OverlayGradientFilter;
import manager.overlay.OverlayView;
import manager.overlay.OverlayViewComparator;
import manager.resources.ResourceManagerComp;
import manager.resources.ResourceVector;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.gradient.GradientPort;
import se.sics.ktoolbox.overlaymngr.OverlayMngrPort;
import se.sics.ktoolbox.overlaymngr.events.OMngrTGradient;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.overlays.view.OverlayViewUpdate;
import se.sics.ktoolbox.util.overlays.view.OverlayViewUpdatePort;

public class ManagerNodeImpl extends ComponentDefinition {
	private static final Logger LOG = LoggerFactory.getLogger(ManagerNodeImpl.class);
	
	private String logPrefix = "";
	
	//*****************************CONNECTIONS**********************************
    Positive<OverlayMngrPort> omngrPort = requires(OverlayMngrPort.class);
	//***************************EXTERNAL_STATE*********************************
    private ExtPort extPorts;
    private KAddress selfAdr;
    private KAddress webServerAdr;
    private Identifier gradientId;
    
    private OMngrTGradient.ConnectRequest pendingGradientConnReq;
    
    private Component webUpdate;
    private Component resourceManagerComp;
    private Component executorComp;
	
	public ManagerNodeImpl(Init init){
		this.logPrefix = "<nid:" + init.selfAdr.getId() + ">";
		
		this.extPorts = init.extPorts;
		this.selfAdr = init.selfAdr;
		this.webServerAdr = init.webServerAdr;
		this.gradientId = init.gradientOId;
		
		subscribe(handleStart, control);
		subscribe(handleGradientConnected, omngrPort);
		//subscribe(handleCroupierSample, this.extPorts.croupierPort);
	}
	
	Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}starting...", logPrefix);
            
            pendingGradientConnReq = new OMngrTGradient.ConnectRequest(gradientId,
                    new OverlayViewComparator(), new OverlayGradientFilter());
            trigger(pendingGradientConnReq, omngrPort);
        }
    };
    
    Handler<OMngrTGradient.ConnectResponse> handleGradientConnected = new Handler<OMngrTGradient.ConnectResponse>() {
        @Override
        public void handle(OMngrTGradient.ConnectResponse event) {
            LOG.info("{}overlays connected", logPrefix);
            
            updateView();
            connectWebNeighborUpdate();
            connectExecutor();
            connectResourceManager();
            
            trigger(Start.event, webUpdate.control());
            trigger(Start.event, executorComp.control());
            trigger(Start.event, resourceManagerComp.control());
           
        }
    };
    
    private void updateView(){
		OverlayView v = new OverlayView(selfAdr.getId());
		trigger(new OverlayViewUpdate.Indication<OverlayView>(gradientId, false, v.copy()), extPorts.viewUpdatePort);
	}
    
    Handler<CroupierSample<OverlayView>> handleCroupierSample = new Handler<CroupierSample<OverlayView>>() {
        @Override
        public void handle(CroupierSample<OverlayView> castSample) {
        	
        	LOG.info("Recieved croupier!");
        	
            if (castSample.publicSample.isEmpty()) {
                return;
            }
            
        }
    };
    
    private void connectWebNeighborUpdate(){
    	
    	WebNeighborUpdateComp.Init init = new WebNeighborUpdateComp.Init(selfAdr, this.webServerAdr);
    	webUpdate = create(WebNeighborUpdateComp.class, init);
    	connect(webUpdate.getNegative(Timer.class), extPorts.timerPort, Channel.TWO_WAY);
    	connect(webUpdate.getNegative(Network.class), extPorts.networkPort, Channel.TWO_WAY);
    	connect(webUpdate.getNegative(CroupierPort.class), extPorts.croupierPort, Channel.TWO_WAY);
    	connect(webUpdate.getNegative(GradientPort.class), extPorts.gradientPort, Channel.TWO_WAY);
    }
    
    private void connectResourceManager(){
    	resourceManagerComp = create(ResourceManagerComp.class, new ResourceManagerComp.Init(selfAdr, new ResourceVector(10.0)));
    	connect(resourceManagerComp.getNegative(Timer.class), extPorts.timerPort, Channel.TWO_WAY);
    	connect(resourceManagerComp.getNegative(Network.class), extPorts.networkPort, Channel.TWO_WAY);
    	connect(resourceManagerComp.getNegative(ExecutorPort.class), executorComp.getPositive(ExecutorPort.class), Channel.TWO_WAY);
    }
	
    private void connectExecutor(){
    	executorComp = create(ExecutorComp.class, new ExecutorComp.Init(selfAdr));
    	connect(executorComp.getNegative(Timer.class), extPorts.timerPort, Channel.TWO_WAY);
    	
    }
    
	public static class ExtPort {

        public final Positive<Timer> timerPort;
        public final Positive<Network> networkPort;
        public final Positive<CroupierPort> croupierPort;
        public final Positive<GradientPort> gradientPort;
        public final Negative<OverlayViewUpdatePort> viewUpdatePort;

        public ExtPort(Positive<Timer> timerPort, Positive<Network> networkPort, Positive<CroupierPort> croupierPort,
                Positive<GradientPort> gradientPort, Negative<OverlayViewUpdatePort> viewUpdatePort) {
            this.networkPort = networkPort;
            this.timerPort = timerPort;
            this.croupierPort = croupierPort;
            this.gradientPort = gradientPort;
            this.viewUpdatePort = viewUpdatePort;
        }
    }
	
	public static class Init extends se.sics.kompics.Init<ManagerNodeImpl> {

        public final ExtPort extPorts;
        public final KAddress selfAdr;
        public final Identifier gradientOId;
        public final KAddress webServerAdr;

        public Init(ExtPort extPorts, KAddress selfAdr, Identifier gradientOId, KAddress webServerAdr) {
            this.extPorts = extPorts;
            this.selfAdr = selfAdr;
            this.gradientOId = gradientOId;
            this.webServerAdr = webServerAdr;
        }
    }
	
}
