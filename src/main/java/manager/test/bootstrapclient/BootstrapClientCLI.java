package manager.test.bootstrapclient;


import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class BootstrapClientCLI {

	
	@Option(name = "-help", usage = "Prints usages.")
	private boolean HELP = false;
	
	@Option(name = "-IP", usage = "Sets the IP of the client, default 127.0.0.1:12346.")
	private String IP;
	
	@Option(name = "-server", usage = "Sets the bootstrap server to connect to, default 127.0.0.1:12345")
	private String server;
	
	public BootstrapClientConfig parseArgs(String[] args){
		String selfIp = "127.0.0.1";
		int port = 12346;
		
		String serverIp = "127.0.0.1";
		int serverPort = 12345;
		
		CmdLineParser parser = new CmdLineParser(this);
		
		try{
			parser.parseArgument(args);
			
			String[] ipAndPortSelf = IP.split(":");
			if(ipAndPortSelf.length != 2){
				throw new Exception("Wrong IP and Port format");
			}
			selfIp = ipAndPortSelf[0];
			port = Integer.parseInt(ipAndPortSelf[1]);
			
			String[] ipAndPortServer = server.split(":");
			if(ipAndPortServer.length != 2){
				throw new Exception("Wrong IP and Port format");
			}
			serverIp = ipAndPortServer[0];
			serverPort = Integer.parseInt(ipAndPortServer[1]);
			
		}catch(Exception e){
			parser.printUsage(System.out);
		}
		
		if(HELP){
			parser.printUsage(System.out);
			System.exit(0);
		}
		
		return new BootstrapClientConfig()
				.setSelfIp(selfIp)
				.setSelfPort(port)
				.setServerIp(serverIp)
				.setServerPort(serverPort);
	}
	
}
