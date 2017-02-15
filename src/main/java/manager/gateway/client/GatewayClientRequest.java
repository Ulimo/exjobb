package manager.gateway.client;

import se.sics.kompics.KompicsEvent;

public class GatewayClientRequest implements KompicsEvent {

private static int idcounter = 0;
	
	public final int id;
	
	public GatewayClientRequest(){
		this.id = getNewId();
	}
	
	private static synchronized int getNewId(){
		return idcounter++;
	}
	
}
