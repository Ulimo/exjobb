package manager.messages;

/***
 * Sent when an allocation has finished running in the executor
 * @author Alexander
 *
 */
public class AllocationDone {

	public final int taskId;
	
	public AllocationDone(int taskId){
		this.taskId = taskId;
	}
	
}
