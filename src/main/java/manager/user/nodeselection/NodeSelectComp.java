package manager.user.nodeselection;

import java.util.List;
import java.util.Stack;

import manager.bootstrap.BootstrapSimplePort;
import manager.bootstrap.BootstrapSimpleRequest;
import manager.bootstrap.BootstrapSimpleResponse;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.ktoolbox.util.network.KAddress;

/***
 * Node selection component, this component handles finding which
 * nodes that should be contacted when asking for resources
 * @author Alexander
 *
 */
public class NodeSelectComp extends ComponentDefinition {

	Positive<BootstrapSimplePort> bootstrapPort = requires(BootstrapSimplePort.class);
	Negative<NodeSelectPort> nodeSelectPort = provides(NodeSelectPort.class);
	
	boolean bootstrapping = false;
	boolean nodeSelectRequest = false;
	
	private Stack<NodeSelectRequest> requests = new Stack<>();
	
	public NodeSelectComp(Init init){
		
		subscribe(handleStart, control);
		subscribe(handleBootstrapResponse, bootstrapPort);
		subscribe(handleNodeSelectRequest, nodeSelectPort);
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			sendBootstrapRequest();
		}
		
	};
	
	private void sendBootstrapRequest(){
		bootstrapping = true;
		trigger(new BootstrapSimpleRequest(), bootstrapPort);
	}
	
	Handler<BootstrapSimpleResponse> handleBootstrapResponse = new Handler<BootstrapSimpleResponse>(){

		@Override
		public void handle(BootstrapSimpleResponse event) {
			
			@SuppressWarnings("unused")
			List<KAddress> sample = event.sample;
			bootstrapping = false;
			if(nodeSelectRequest){
				
				while(!requests.isEmpty()){
					trigger(requests.pop().answer(event.sample), nodeSelectPort);
				}
				nodeSelectRequest = false;
			}
		}
		
	};
	
	Handler<NodeSelectRequest> handleNodeSelectRequest = new Handler<NodeSelectRequest>(){

		@Override
		public void handle(NodeSelectRequest event) {
			nodeSelectRequest = true;
			requests.push(event);
			if(!bootstrapping){
				sendBootstrapRequest();
			}
		}
		
	};
	
	
	
	
	
	public static class Init extends se.sics.kompics.Init<NodeSelectComp> {
		
		public Init(){
			
		}
		
	}
	
}
