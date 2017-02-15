package manager.messages;

import se.sics.ktoolbox.util.network.KAddress;

/**
 * A proposal for an allocation
 * @author Alexander
 *
 */
public class AllocationProposal {
	
	public final KAddress sender;
	public final int taskId;
	
	
	public AllocationProposal(KAddress sender, int taskId){
		this.sender = sender;
		this.taskId = taskId;
	}
	
}
