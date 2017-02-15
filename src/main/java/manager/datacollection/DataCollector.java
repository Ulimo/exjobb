package manager.datacollection;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import manager.resources.ResourceVector;
import se.sics.ktoolbox.util.network.KAddress;

public class DataCollector {

	public static class allocationtimeLog{
		
		public ResourceVector demandVector;
		public long timeMilli;
		public long taskLength;
		public KAddress scheduler;
		public int taskId;
		
		//Current load?
		public double loadPercentage;
		public ResourceVector usedResources;
		public ResourceVector maximumResources;
		
		
		public allocationtimeLog(KAddress scheduler, int taskId, long timeMilli, long taskLength, ResourceVector demand, ResourceVector used, ResourceVector max){
			this.scheduler = scheduler;
			this.taskId = taskId;
			this.timeMilli = timeMilli;
			this.taskLength = taskLength;
			this.demandVector = demand;
			this.usedResources = used;
			this.maximumResources = max;
		}
		
		@Override
		public String toString(){
			String s = "";
			
			s += scheduler.getId() + ", ";
			
			s += taskId + ", ";
			
			s += timeMilli + ", ";
			
			for(int i = 0; i < demandVector.size(); i++){
				s += demandVector.getResource(i) + ", ";
			}
			
			for(int i = 0; i < usedResources.size(); i++){
				s += usedResources.getResource(i) + ", ";
			}
			
			for(int i = 0; i < maximumResources.size(); i++){
				s += maximumResources.getResource(i) + ", ";
			}
			
			s = s.substring(0, s.length() - 2);
			
			return s;
			
		}
	}
	
	
	
	PrintWriter writer;
	//Allocation times
	
	ArrayList<allocationtimeLog> allocationLogs = new ArrayList<allocationtimeLog>();
	
	
	ResourceVector currentMaximumResources ;
	
	ResourceVector usedResources;
	
	
	public ResourceVector getUsedResources(){
		return usedResources;
	}
	
	
	
	public void addToMaxResource(ResourceVector vector){
		
		if(currentMaximumResources == null){
			currentMaximumResources = new ResourceVector(new double[vector.size()]);
		}
		
		currentMaximumResources = currentMaximumResources.add(vector);
	}
	
	public void addToUsedResources(ResourceVector vector){
		
		if(usedResources == null){
			usedResources = new ResourceVector(new double[vector.size()]);
		}
		
		usedResources = usedResources.add(vector);
	}
	
	public void removeUsedResources(ResourceVector vector){
		if(usedResources == null){
			usedResources = new ResourceVector(new double[vector.size()]);
		}
		
		usedResources = usedResources.sub(vector);
	}
	
	public void addAllocation(KAddress scheduler, int taskId, long timeMilli, long taskLength, ResourceVector demandVector, ResourceVector used){

		allocationtimeLog l = new allocationtimeLog(scheduler, taskId, timeMilli, taskLength, demandVector, used, currentMaximumResources);
		writer.println(l.toString());
		allocationLogs.add(l);
		writer.flush();
		//writer.close();
	}
	
	
	public DataCollector(String dataFileName){

		try {
			writer = new PrintWriter(dataFileName, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
