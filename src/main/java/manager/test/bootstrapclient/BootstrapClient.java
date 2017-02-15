package manager.test.bootstrapclient;

import java.net.UnknownHostException;

import manager.production.bootstrap.BootstrapServerHost;
import se.sics.kompics.Kompics;

public class BootstrapClient {

	
	public static void main(String[] args) throws UnknownHostException, InterruptedException{
		BootstrapClientConfig config = (new BootstrapClientCLI()).parseArgs(args);
		
		Kompics.createAndStart(BootstrapClientHost.class, new BootstrapClientHost.Init(config.getSelfAddress(), config.getServerAddress()));
		
		Kompics.waitForTermination();
	}
	
}
