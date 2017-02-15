package manager.bootstrap;

import se.sics.kompics.PortType;

public class BootstrapSimplePort extends PortType {
	{
		indication(BootstrapSimpleRequest.class);
		request(BootstrapSimpleRequest.class);
		
		indication(BootstrapSimpleResponse.class);
		request(BootstrapSimpleResponse.class);
	}
}
