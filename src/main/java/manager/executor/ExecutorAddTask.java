package manager.executor;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

public class ExecutorAddTask implements KompicsEvent {

	public final int taskId;
	public final KAddress sender;
	public final int timeMilli;
	
	public ExecutorAddTask(int taskId, KAddress sender, int timeMilli){
		this.taskId = taskId;
		this.sender = sender;
		this.timeMilli = timeMilli;
	}
	
	public ExecutorTaskDone done(){
		return new ExecutorTaskDone(taskId, sender);
	}
	
}
