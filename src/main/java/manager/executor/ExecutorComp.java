package manager.executor;

import java.util.PriorityQueue;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import manager.user.UserImpl;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.network.KAddress;

/***
 * This class simulates an executor, which runs different tasks
 * @author Alexander
 *
 */
public class ExecutorComp extends ComponentDefinition {
	
	private static final Logger LOG = LoggerFactory.getLogger(ExecutorComp.class);
	
	private static final long monitorTime = 10;

	Negative<ExecutorPort> executorPort = provides(ExecutorPort.class);
	Positive<Timer> timerPort = requires(Timer.class);
	
	private UUID monitorTimeout;
	private KAddress selfAdr;
	
	private String logPrefix = "";
	
	private long currentTime = 0;
	
	private PriorityQueue<RunningTask> queue = new PriorityQueue<RunningTask>(10, (a, b) -> Long.compare(a.finishTime, b.finishTime));
	
	public ExecutorComp(Init init){
		this.selfAdr = init.selfAdr;
		this.logPrefix = "<nid:" + selfAdr.getId() + ">";
		
		subscribe(handleStart, control);
		subscribe(handleMonitor, timerPort);
		subscribe(handleAddTask, executorPort);
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			scheduleMonitor();
		}
		
	};
	
	Handler<ExecutorAddTask> handleAddTask = new Handler<ExecutorAddTask>(){

		@Override
		public void handle(ExecutorAddTask event) {
			
			//Add the event to the priority queue to simulate it running
			queue.offer(new RunningTask(event, event.timeMilli + currentTime));
			LOG.info("{}Task {} running, user {}", logPrefix, event.taskId, event.sender.getId());
		}
		
	};
	
	Handler<MonitorTimeout> handleMonitor = new Handler<MonitorTimeout>(){

		@Override
		public void handle(MonitorTimeout event) {
			currentTime += monitorTime;
			
			//Remove all tasks that have finished executing
			while(!queue.isEmpty() && queue.peek().finishTime < currentTime){
				RunningTask t = queue.poll();
				trigger(t.task.done(), executorPort);
			}
		}
		
	};
	
	
	private void scheduleMonitor(){
		SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(monitorTime, monitorTime);
		MonitorTimeout mt = new MonitorTimeout(spt);
		spt.setTimeoutEvent(mt);
		monitorTimeout = mt.getTimeoutId();
		trigger(spt, timerPort);
	}
	
	private class MonitorTimeout extends Timeout {

		protected MonitorTimeout(SchedulePeriodicTimeout request) {
			super(request);
		}
		
	}
	
	
	public static class Init extends se.sics.kompics.Init<ExecutorComp> {
		public final KAddress selfAdr;
		
		public Init(KAddress selfAdr){
			this.selfAdr = selfAdr;
		}
	}
}
