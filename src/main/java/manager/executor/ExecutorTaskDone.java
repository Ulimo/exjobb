package manager.executor;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

public class ExecutorTaskDone implements KompicsEvent {

	public final int taskId;
	public final KAddress sender;
	
	public ExecutorTaskDone(int taskId, KAddress sender){
		this.taskId = taskId;
		this.sender = sender;
	}
	
}
