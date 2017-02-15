package manager.user;

import manager.resources.ResourceVector;

public class Task {

	public int taskId;
	
	public long addedTime;
	
	public long submitedTime;
	
	public long runnedTime;
	
	public ResourceVector demandVector;
	
	public ResourceVector usedAtSubmit;
	
	public Task(int taskid, ResourceVector demandVector){
		this.taskId = taskid;
		this.demandVector = demandVector;
	}
}
