package manager.user.nodeselection;

import java.util.List;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

public class NodeSelectRequest implements KompicsEvent {

	private static int counter = 0;
	
	private int id;
	
	public NodeSelectRequest(){
		this.id = getNewId();
	}
	
	private NodeSelectRequest(int id){
		this.id = id;
	}
	
	private static synchronized int getNewId(){
		return counter++;
	}
	
	public NodeSelectRequest copy(){
		return new NodeSelectRequest(this.id);
	}
	
	public int getId(){
		return this.id;
	}
	
	
	public NodeSelectResponse answer(List<KAddress> sample){
		return new NodeSelectResponse(this.id, sample);
	}
	
}
