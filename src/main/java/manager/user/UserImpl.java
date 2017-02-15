package manager.user;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import manager.datacollection.DataCollector;
import manager.resources.ResourceVector;
import manager.user.allocation.AllocateRequest;
import manager.user.allocation.AllocateSubmit;
import manager.user.allocation.AllocateDone;
import manager.user.allocation.AllocationPort;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.network.KAddress;

public class UserImpl extends ComponentDefinition {
	
	private static final int TASK_TIMEOUT = 1000;
	
	private static final Logger LOG = LoggerFactory.getLogger(UserImpl.class);
	
	Positive<AllocationPort> allocationPort = requires(AllocationPort.class);
	Positive<Timer> timerPort = requires(Timer.class);
	
	private UUID taskTimeout;
	
	private boolean taskBeingAllocated = false;

	private int taskCounter = 0;
	
	private KAddress selfAdr;
	
	private HashMap<Integer, Task> tasks = new HashMap<Integer,Task>();
	
	public UserImpl(Init init){
		this.selfAdr = init.selfAdr;
		
		subscribe(handleStart, control);
		subscribe(handleAllocateDone, allocationPort);
		subscribe(handleSubmit, allocationPort);
		subscribe(handleTaskTimeout, timerPort);
	}
	
	Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) {
			scheduleTaskTimeout();
			//addTask(new ResourceVector(2.0), 1, 1000); //Test to add one task
		}
	};
	
	//A response when the task have been submited to a node
	Handler<AllocateDone> handleAllocateDone = new Handler<AllocateDone>(){

		@Override
		public void handle(AllocateDone event) {
			LOG.info("Task done with IDzz: " + event.taskId);
			Task t = tasks.get(event.taskId);
			t.runnedTime = System.currentTimeMillis();
			addTask(t);
		}
		
	};
	
	private void addTask(Task t){
		GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
		DataCollector dataCollector = gv.getValue("simulation.datacollector", DataCollector.class);
		dataCollector.addAllocation(this.selfAdr, t.taskId, t.submitedTime - t.addedTime, t.runnedTime - t.submitedTime, t.demandVector, t.usedAtSubmit);
	}
	
	Handler<AllocateSubmit> handleSubmit = new Handler<AllocateSubmit>(){

		@Override
		public void handle(AllocateSubmit event) {
			taskBeingAllocated = false;
			LOG.info("Task submited with ID: " + event.taskId);
			
			Task t = tasks.get(event.taskId);
			t.submitedTime = System.currentTimeMillis();
			handleTaskAtSubmit(t);
		}
		
	};
	
	private void handleTaskAtSubmit(Task t){
		GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
		DataCollector dataCollector = gv.getValue("simulation.datacollector", DataCollector.class);
		t.usedAtSubmit = dataCollector.getUsedResources().copy();
	}
	
	Handler<TaskTimeout> handleTaskTimeout = new Handler<TaskTimeout>(){

		@Override
		public void handle(TaskTimeout event) {
			if(taskBeingAllocated)
				return;
			addTask(new ResourceVector(2.0), taskCounter, 1000000); //Test to add one task
			taskCounter++;
		}
		
	};
	
	private void scheduleTaskTimeout(){
		SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(TASK_TIMEOUT, TASK_TIMEOUT);
		
		TaskTimeout rt = new TaskTimeout(spt);
		spt.setTimeoutEvent(rt);
		
		taskTimeout = rt.getTimeoutId();
		trigger(spt, timerPort);
	}
	
	private class TaskTimeout extends Timeout {

		protected TaskTimeout(SchedulePeriodicTimeout request) {
			super(request);
		}
		
	}
	
	
	private void addTask(ResourceVector v, int taskId, int runtimeMilli){
		taskBeingAllocated = true;
		LOG.info("Task added with ID: " + taskId);
		Task t = new Task(taskId, v);
		t.addedTime = System.currentTimeMillis();
		tasks.put(taskId, t);
		trigger(new AllocateRequest(taskId, v, runtimeMilli), allocationPort);
	}
	
	
	public static class Init extends se.sics.kompics.Init<UserImpl> {
	
		public final KAddress selfAdr;
		
		public Init(KAddress selfAdr){
			this.selfAdr = selfAdr;
		}
		
	}
	
}
