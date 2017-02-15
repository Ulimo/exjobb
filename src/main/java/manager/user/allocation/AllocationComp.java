package manager.user.allocation;

import java.util.HashMap;
import java.util.List;

import manager.gateway.client.GatewayClientPort;
import manager.gateway.client.GatewayClientRequest;
import manager.gateway.client.GatewayClientResponse;
import manager.messages.AllocationAccept;
import manager.messages.AllocationDone;
import manager.messages.AllocationProposal;
import manager.messages.AllocationRequest;
import manager.resources.ResourceVector;
import manager.user.nodeselection.NodeSelectPort;
import manager.user.nodeselection.NodeSelectRequest;
import manager.user.nodeselection.NodeSelectResponse;
import manager.webdebug.messages.UpdateNeighborsMsg;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

/***
 * This class handles the allocations for a user
 * It handles the communication to resource manager nodes.
 * @author Alexander
 *
 */
public class AllocationComp extends ComponentDefinition {

	
	Positive<Timer> timerPort = requires(Timer.class);
	
	//Network port for communication to resource nodes
    Positive<Network> networkPort = requires(Network.class);
	
    //Node select port for communication with the node selector
    //Positive<NodeSelectPort> nodeSelectPort = requires(NodeSelectPort.class);
    Positive<GatewayClientPort> gatewayPort = requires(GatewayClientPort.class);
    
    //The allocation port for the user to request allocations from
    Negative<AllocationPort> allocationPort = provides(AllocationPort.class);
    
    //Hashmap to store current allocations
    HashMap<Integer, Allocation> allocations = new HashMap<>();
    
    private KAddress selfAdr;
    
    
	public AllocationComp(Init init){
		this.selfAdr = init.selfAdr;
		
		subscribe(handleStart, control);
		//subscribe(handleNodeSelect, nodeSelectPort);
		subscribe(handleNodeResponse, gatewayPort);
		subscribe(handleAllocateRequest, allocationPort);
		subscribe(handleAllocationProposal, networkPort);
		subscribe(handleAllocationDone, networkPort);
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			
		}
		
	};
	
	Handler<AllocateRequest> handleAllocateRequest = new Handler<AllocateRequest>() {

		@Override
		public void handle(AllocateRequest event) {
			
			//Create a new allocation object here, add the demand vector
			Allocation alloc = new Allocation(event.taskId, event.demandVector, event.finishTime);
			//requestNodesForAllocation(alloc);
			getNodeForAllocation(alloc);
		}
		
	};
	
	Handler<GatewayClientResponse> handleNodeResponse = new Handler<GatewayClientResponse>() {

		@Override
		public void handle(GatewayClientResponse event) {
			//Get the allocation info
			Allocation alloc = allocations.get(event.id);
			
			//Get the sample of nodes that one can send allocation to
			List<KAddress> sample = event.node;
			
			KHeader<KAddress> header = new BasicHeader<KAddress>(selfAdr, sample.get(0), Transport.UDT);
            KContentMsg<KAddress, KHeader<KAddress>, AllocationRequest> msg = 
            		new BasicContentMsg<KAddress, KHeader<KAddress>, AllocationRequest>(header, new AllocationRequest(selfAdr, alloc.getDemandVector(), alloc.getTaskId(), alloc.getFinishTime()));
            trigger(msg, networkPort);
		}
		
	};
	
	/*Handler<NodeSelectResponse> handleNodeSelect = new Handler<NodeSelectResponse>(){

		@Override
		public void handle(NodeSelectResponse event) {
			if(allocations.containsKey(event.id)){
				
				//Get the allocation info
				Allocation alloc = allocations.get(event.id);
				
				//Get the sample of nodes that one can send allocation to
				List<KAddress> sample = event.sample;
				
				KHeader<KAddress> header = new BasicHeader<KAddress>(selfAdr, sample.get(0), Transport.UDT);
	            KContentMsg<KAddress, KHeader<KAddress>, AllocationRequest> msg = 
	            		new BasicContentMsg<KAddress, KHeader<KAddress>, AllocationRequest>(header, new AllocationRequest(selfAdr, alloc.getDemandVector(), alloc.getTaskId(), alloc.getFinishTime()));
	            trigger(msg, networkPort);
			}
		}
		
	};*/
	
	ClassMatchedHandler handleAllocationProposal = new ClassMatchedHandler<AllocationProposal, KContentMsg<?, ?, AllocationProposal>>() {

		@Override
		public void handle(AllocationProposal content, KContentMsg<?, ?, AllocationProposal> context) {
			
			
			sendAllocateSubmit(content.taskId);
			sendAllocationAccept(context.getHeader().getSource(), content);
			
		}
	};
	
	ClassMatchedHandler handleAllocationDone = new ClassMatchedHandler<AllocationDone, KContentMsg<?, ?, AllocationDone>>() {

		@Override
		public void handle(AllocationDone content, KContentMsg<?, ?, AllocationDone> context) {
			sendAllocateDone(content.taskId);
		}
	};
	
	private void sendAllocateDone(int taskId){
		trigger(new AllocateDone(taskId), allocationPort);
	}
	
	
	private void sendAllocateSubmit(int taskId){
		trigger(new AllocateSubmit(taskId), allocationPort);
	}
	
	private void sendAllocationAccept(KAddress reciever, AllocationProposal proposal){
		KHeader<KAddress> header = new BasicHeader<KAddress>(selfAdr, reciever, Transport.UDT);
        KContentMsg<KAddress, KHeader<KAddress>, AllocationAccept> msg = 
        		new BasicContentMsg<KAddress, KHeader<KAddress>, AllocationAccept>(header, new AllocationAccept(selfAdr, proposal.taskId));
        trigger(msg, networkPort);
	}
	
	
	private void getNodeForAllocation(Allocation alloc){
		GatewayClientRequest req = new GatewayClientRequest();
		allocations.put(req.id, alloc);
		trigger(req, gatewayPort);
	}
	
	/***
	 * Requests nodes from the node selector.
	 * It will match the allocation to an ID.
	 */
	/*private void requestNodesForAllocation(Allocation alloc){
		NodeSelectRequest req = new NodeSelectRequest();
		allocations.put(req.getId(), alloc);
		trigger(req, nodeSelectPort);
	}*/
	
	public static class Init extends se.sics.kompics.Init<AllocationComp> {
		
		public final KAddress selfAdr;
		
		public Init(KAddress selfAdr){
			this.selfAdr = selfAdr;
		}
		
	}
	
}
