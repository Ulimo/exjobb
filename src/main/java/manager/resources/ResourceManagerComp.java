package manager.resources;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import manager.datacollection.DataCollector;
import manager.executor.ExecutorAddTask;
import manager.executor.ExecutorComp;
import manager.executor.ExecutorPort;
import manager.executor.ExecutorTaskDone;
import manager.messages.AllocationAccept;
import manager.messages.AllocationDone;
import manager.messages.AllocationProposal;
import manager.messages.AllocationRequest;
import manager.webdebug.GlobalData;
import manager.webdebug.WebServer;
import manager.webdebug.messages.UpdateNeighborsMsg;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

public class ResourceManagerComp extends ComponentDefinition {
	
	private static final Logger LOG = LoggerFactory.getLogger(ResourceManagerComp.class);
	private String logPrefix = "";

	Positive<Timer> timerPort = requires(Timer.class);
	
	//Network port for communication to user nodes, and other resource nodes
    Positive<Network> networkPort = requires(Network.class);
    
    Positive<ExecutorPort> executorPort = requires(ExecutorPort.class);
    
    Queue<AllocationRequest> fifoQueue = new LinkedList<AllocationRequest>();
    
    HashMap<KAddress, HashMap<Integer, AllocationRequest>> allocations = new HashMap<KAddress, HashMap<Integer, AllocationRequest>>();
	
    ResourceVector resources;
    ResourceVector usedResources;
    
    private KAddress selfAdr;
    
	public ResourceManagerComp(Init init){
		this.resources = init.resources;
		this.selfAdr = init.selfAdr;
		this.logPrefix = "<nid:" + selfAdr.getId() + ">";
		
		//Create an empty vector for used resources
		this.usedResources = new ResourceVector(new double[this.resources.size()]);
		
		subscribe(handleStart, control);
		subscribe(handleAllocateRequest, networkPort);
		subscribe(handleAllocationAccept, networkPort);
		subscribe(handleTaskDone, executorPort);
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			// TODO Auto-generated method stub
			addStartUpNode();
		}
		
	};
	
	private void addStartUpNode(){
		GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
		DataCollector dataCollector = gv.getValue("simulation.datacollector", DataCollector.class);
		dataCollector.addToMaxResource(this.resources);
		
	}
	
	private void addUsedResources(ResourceVector toAdd){
		GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
		DataCollector dataCollector = gv.getValue("simulation.datacollector", DataCollector.class);
		dataCollector.addToUsedResources(toAdd);
	}
	
	private void removeUsedResources(ResourceVector toRemove){
		GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
		DataCollector dataCollector = gv.getValue("simulation.datacollector", DataCollector.class);
		dataCollector.removeUsedResources(toRemove);
	}
	
	ClassMatchedHandler handleAllocateRequest = new ClassMatchedHandler<AllocationRequest, KContentMsg<?, ?, AllocationRequest>>() {

		@Override
		public void handle(AllocationRequest content, KContentMsg<?, ?, AllocationRequest> context) {
			
			//Check if it has enough resources
			
			
			HashMap<Integer, AllocationRequest> map = allocations.getOrDefault(content.sender, new HashMap<Integer, AllocationRequest>());
			map.put(content.taskId, content);
			allocations.put(content.sender, map);
			
			
			if(!tryAllocateTask(content)){
				fifoQueue.add(content);
			}
			
			
			
			//Remaining resources on this machine
			/*ResourceVector remainingResources = resources.sub(usedResources);
			
			//Check if the machine has enough resources
			if(remainingResources.largerThan(content.demandVector)){
				
				//reserve resources
				
				
				sendProposal(content); //Send a proposal
			}else{
				LOG.info("{} server full!", logPrefix);
				
				fifoQueue.add(content);
			}*/
			
		}
	};
	
	private boolean tryAllocateTask(AllocationRequest req){
		if(req == null)
			return false;
		
		ResourceVector remainingResources = resources.sub(usedResources);
		
		//Check if the machine has enough resources
		if(remainingResources.largerThan(req.demandVector)){
			
			//reserve resources
			
			
			sendProposal(req); //Send a proposal
			return true;
		}
		return false;
	}
	
	ClassMatchedHandler handleAllocationAccept = new ClassMatchedHandler<AllocationAccept, KContentMsg<?, ?, AllocationAccept>>() {

		@Override
		public void handle(AllocationAccept content, KContentMsg<?, ?, AllocationAccept> context) {
			
			//Remove it from the waiting queue
			
			//Simulate task here
			runTask(content);
		}
	};
	
	Handler<ExecutorTaskDone> handleTaskDone = new Handler<ExecutorTaskDone>() {

		@Override
		public void handle(ExecutorTaskDone event) {
			
			
			//Remove the task from the resource manager cache
			HashMap<Integer, AllocationRequest> map =allocations.getOrDefault(event.sender, new HashMap<Integer, AllocationRequest>());
			AllocationRequest req = map.remove(event.taskId);
			usedResources = usedResources.sub(req.demandVector);
			
			removeUsedResources(req.demandVector);
			
			//Send message to the user that the task is done
			sendAllocateDone(event.taskId, event.sender);
			
			while(tryAllocateTask(fifoQueue.peek())){
				fifoQueue.remove();
			}
		}
		
	};
	
	private void runTask(AllocationAccept msg){
		
		HashMap<Integer, AllocationRequest> map =allocations.getOrDefault(msg.sender, new HashMap<Integer, AllocationRequest>());
		AllocationRequest req = map.get(msg.taskId);
		
		
		
		if(req != null){
			//Start running the task
			trigger(new ExecutorAddTask(msg.taskId, msg.sender, req.runtimeMilli), executorPort);
		}else{
			throw new RuntimeException("Allocation request does not exist!");
		}
	}
	
	private void sendAllocateDone(int taskid, KAddress sender){
		KHeader<KAddress> header = new BasicHeader<KAddress>(selfAdr, sender, Transport.UDT);
        KContentMsg<KAddress, KHeader<KAddress>, AllocationDone> msg = 
        		new BasicContentMsg<KAddress, KHeader<KAddress>, AllocationDone>(header, new AllocationDone(taskid));
        trigger(msg, networkPort);
	}
	
	/***
	 * Send a proposal to a scheduler
	 * @param req
	 */
	private void sendProposal(AllocationRequest req){
		
		usedResources = usedResources.add(req.demandVector);
		
		addUsedResources(req.demandVector);
		
		KHeader<KAddress> header = new BasicHeader<KAddress>(selfAdr, req.sender, Transport.UDT);
        KContentMsg<KAddress, KHeader<KAddress>, AllocationProposal> msg = 
        		new BasicContentMsg<KAddress, KHeader<KAddress>, AllocationProposal>(header, new AllocationProposal(selfAdr, req.taskId));
        trigger(msg, networkPort);
	}
	
	
	
	
	
	public static class Init extends se.sics.kompics.Init<ResourceManagerComp> {
		
		public final ResourceVector resources;
		public final KAddress selfAdr;
		
		public Init(KAddress selfAdr, ResourceVector resources){
			this.selfAdr = selfAdr;
			this.resources = resources;
		}
	}
	
}
