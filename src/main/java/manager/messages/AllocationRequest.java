package manager.messages;

import manager.resources.ResourceVector;
import se.sics.ktoolbox.util.network.KAddress;

public class AllocationRequest {

	public final KAddress sender;
	public final ResourceVector demandVector;
	public final int taskId;
	public final int runtimeMilli;
	
	public AllocationRequest(KAddress sender, ResourceVector vector, int taskId, int runtimeMilli){
		this.demandVector = vector;
		this.sender = sender;
		this.taskId = taskId;
		this.runtimeMilli = runtimeMilli;
	}
}
