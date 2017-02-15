package manager.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.identifiable.basic.OverlayIdFactory;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;
import se.sics.ktoolbox.omngr.bootstrap.event.Sample;

/***
 * A bootstrap client that does not register at the bootstrap server
 * Only sends requests for nodes in the network, used by a user node to get
 * the different servers in the network. Will send requests on an interval.
 * So different nodes will be used by the users.
 * @author Alexander
 *
 */
public class BootstrapSimpleClient extends ComponentDefinition {

	private final static long sampleRequestTimeout = 1000;
	
	private KAddress selfAdr;
	private KAddress bootstrapServer;
	private Identifier overlayId;
	
	Positive<Network> networkPort = requires(Network.class);
	Positive<Timer> timerPort = requires(Timer.class);
	Negative<BootstrapSimplePort> bootstrapPort = provides(BootstrapSimplePort.class);
	
	
	private UUID requestTimeout;
	
	private List<KAddress> currentSample = new ArrayList<KAddress>();
	
	private Stack<BootstrapSimpleRequest> requests = new Stack<BootstrapSimpleRequest>();
	
	
	public BootstrapSimpleClient(Init init){
		this.selfAdr = init.selfAdr;
		this.bootstrapServer = init.bootstrapServer;
		this.overlayId = OverlayIdFactory.getId((byte) 0x0E, OverlayIdFactory.Type.TGRADIENT, new byte[]{0, 0, 1});//init.overlayId;
		
		subscribe(handleStart, control);
		subscribe(handleSampleResponse, networkPort);
		subscribe(handleRequestTimeout, timerPort);
		subscribe(handleBootstrapRequest, bootstrapPort);
	}
	
	
	Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) { 
			scheduleSampleRequest();
		}
		
	};
	
	Handler<RequestTimeout> handleRequestTimeout = new Handler<RequestTimeout>(){

		@Override
		public void handle(RequestTimeout event) {
			sendSampleRequest();
		}
		
	};
	
	Handler<BootstrapSimpleRequest> handleBootstrapRequest = new Handler<BootstrapSimpleRequest>(){

		@Override
		public void handle(BootstrapSimpleRequest event) {
			
			if(currentSample.size() == 0){
				requests.push(event);
				//Add timeout here
				return;
			}
			
			trigger(event.answer(currentSample), bootstrapPort);
		}
		
	};
	
	
	private void scheduleSampleRequest(){
		sendSampleRequest(); //Start directly with getting a sample
		SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(sampleRequestTimeout, sampleRequestTimeout);
		
		RequestTimeout rt = new RequestTimeout(spt);
		spt.setTimeoutEvent(rt);
		
		requestTimeout = rt.getTimeoutId();
		trigger(spt, timerPort);
	}
	
	
	private class RequestTimeout extends Timeout {

		protected RequestTimeout(SchedulePeriodicTimeout request) {
			super(request);
		}
		
	}
	
	
	private void sendSampleRequest(){
		
		Sample.Request content = new Sample.Request(this.overlayId);
		KContentMsg container = new BasicContentMsg(new BasicHeader(selfAdr, bootstrapServer, Transport.UDP), content);
		trigger(container, networkPort);
		
	}
	
	ClassMatchedHandler<Sample.Response, KContentMsg<?, ?, Sample.Response>> handleSampleResponse
    = new ClassMatchedHandler<Sample.Response, KContentMsg<?, ?, Sample.Response>>() {

        @Override
        public void handle(Sample.Response content, KContentMsg<?, ?, Sample.Response> container) {
        	
        	if(content.sample.isEmpty())
        		return;
        	
        	
        	
			currentSample = content.sample;       
			
			while(!requests.isEmpty()){
        		trigger(requests.pop().answer(currentSample), bootstrapPort);
        	}
        }
    };
	
	
	
	
	
	public static class Init extends se.sics.kompics.Init<BootstrapSimpleClient> {
		
		public final KAddress selfAdr;
		public final KAddress bootstrapServer;
		public final Identifier overlayId;
		
		public Init(KAddress selfAdr, KAddress bootstrapServer, Identifier overlayId){
			this.selfAdr = selfAdr;
			this.bootstrapServer = bootstrapServer;
			this.overlayId = overlayId;
		}
		
		
	}
	
}
