package manager.bootstrap;

import java.util.List;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

public class BootstrapSimpleResponse implements KompicsEvent {

	
	public final int id;
	
	public final List<KAddress> sample;
	
	public BootstrapSimpleResponse(int id, List<KAddress> sample){
		this.id = id;
		this.sample = sample;
	}
	
}
