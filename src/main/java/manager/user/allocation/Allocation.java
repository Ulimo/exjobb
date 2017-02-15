package manager.user.allocation;

import manager.resources.ResourceVector;

public class Allocation {
	
	//private int nodeSelectId;
	private ResourceVector demandVector;
	private int taskId;
	private int finishTime;

	public ResourceVector getDemandVector(){
		return this.demandVector;
	}
	
	public int getTaskId(){
		return this.taskId;
	}
	
	public int getFinishTime(){
		return this.finishTime;
	}
	/*
	public int getNodeSelectId(){
		return this.nodeSelectId;
	}*/
	
	/*
	public Allocation(int nodeSelectId){
		this.nodeSelectId = nodeSelectId;
	}*/
	
	public Allocation(int taskId, ResourceVector demandVector, int finishTime){
		this.demandVector = demandVector;
		this.taskId = taskId;
		this.finishTime = finishTime;
	}
	
}
