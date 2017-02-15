package manager.user.allocation;

import manager.resources.ResourceVector;
import se.sics.kompics.KompicsEvent;

public class AllocateRequest implements KompicsEvent {

	public final ResourceVector demandVector;
	public final int taskId;
	public final int finishTime;
	
	public AllocateRequest(int taskId, ResourceVector demandVector, int finishTime){
		this.demandVector = demandVector;
		this.taskId = taskId;
		this.finishTime = finishTime;
	}
}
