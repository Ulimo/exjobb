package manager.messages;

public class GatewayRequestNode {

	private static int idcounter = 0;
	
	public final int id;
	
	public GatewayRequestNode(){
		this.id = getNewId();
	}
	
	private static synchronized int getNewId(){
		return idcounter++;
	}
}
