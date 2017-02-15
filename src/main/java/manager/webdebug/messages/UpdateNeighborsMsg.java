package manager.webdebug.messages;

import java.util.List;

import se.sics.ktoolbox.util.identifiable.Identifier;

public class UpdateNeighborsMsg {

	public final static byte CROUPIER_UPDATE = 0;
	public final static byte GRADIENT_UPDATE = 1;
	
	public final int nodeId;
	public final List<Integer> neighbors;
	public final byte overlayType;
	
	public UpdateNeighborsMsg(int nodeId, List<Integer> neighbors, byte overlayType){
		this.nodeId = nodeId;
		this.neighbors = neighbors;
		this.overlayType = overlayType;
	}
	
}
