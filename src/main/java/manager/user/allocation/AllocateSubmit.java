package manager.user.allocation;

import se.sics.kompics.KompicsEvent;

public class AllocateSubmit implements KompicsEvent {

	public final int taskId;
	
	public AllocateSubmit(int taskid){
		this.taskId = taskid;
	}
}
