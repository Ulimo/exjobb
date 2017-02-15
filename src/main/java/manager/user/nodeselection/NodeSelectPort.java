package manager.user.nodeselection;

import se.sics.kompics.PortType;

public class NodeSelectPort extends PortType {
	{
		indication(NodeSelectRequest.class);
		request(NodeSelectRequest.class);
		
		indication(NodeSelectResponse.class);
		request(NodeSelectResponse.class);
	}
}
