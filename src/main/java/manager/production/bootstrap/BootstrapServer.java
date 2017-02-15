package manager.production.bootstrap;

import java.net.UnknownHostException;

import se.sics.kompics.Kompics;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapServerComp;

public class BootstrapServer {

	public static void main(String[] args) throws UnknownHostException, InterruptedException{
		
		BootstrapServerConfig config = (new BootstrapServerCLI()).parseArgs(args);
		
		Kompics.createAndStart(BootstrapServerHost.class, new BootstrapServerHost.Init(config.getAddress()));
		
		
		Kompics.waitForTermination();
	}
	
}
