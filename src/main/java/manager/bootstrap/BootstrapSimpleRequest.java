package manager.bootstrap;

import java.util.List;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

public class BootstrapSimpleRequest implements KompicsEvent {

	
	private static int currentId = 0;
	
	public final int id;
	
	public BootstrapSimpleRequest(){
		this.id = getNewId();
	}
	
	private static synchronized int getNewId(){
		return currentId++;
	}
	
	public BootstrapSimpleResponse answer(List<KAddress> sample){
		return new BootstrapSimpleResponse(id, sample);
	}
	
}
