package manager.messages;

import se.sics.ktoolbox.util.network.KAddress;

/***
 * Accepts an allocation from a proposal
 * @author Alexander
 *
 */
public class AllocationAccept {

	public final KAddress sender;
	public final int taskId;
	
	public AllocationAccept(KAddress sender, int taskId){
		this.sender = sender;
		this.taskId = taskId;
	}
	
}
