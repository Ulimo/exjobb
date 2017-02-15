package manager.overlay;

import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.update.View;

public class OverlayView implements View {

	public final Identifier nodeId;
	
	public OverlayView(Identifier nodeId){
		this.nodeId = nodeId;
	}
	
	public OverlayView copy(){
		return new OverlayView(nodeId);
	}
}
