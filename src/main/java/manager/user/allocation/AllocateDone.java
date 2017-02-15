package manager.user.allocation;

import se.sics.kompics.KompicsEvent;

/***
 * Sent when the allocation has finished running
 * @author Alexander
 *
 */
public class AllocateDone implements KompicsEvent {

	public final int taskId;
	
	public AllocateDone(int taskid){
		this.taskId = taskid;
	}
}
