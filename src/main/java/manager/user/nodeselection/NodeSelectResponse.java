package manager.user.nodeselection;

import java.util.List;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

public class NodeSelectResponse implements KompicsEvent {

	public final List<KAddress> sample;
	public final int id;
	
	public NodeSelectResponse(int id, List<KAddress> sample){
		this.sample =sample;
		this.id = id;
	}
}
