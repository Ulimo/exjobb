package manager.production.bootstrap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class BootstrapServerCLI {

	private static final Logger LOG = LoggerFactory.getLogger(BootstrapServerCLI.class);
	
	@Option(name = "-help", usage = "Print usages.")
	private boolean HELP = false;
	
	@Option(name = "-IP", usage = "Set the IP address of the bootstrap server. Default: 193.0.0.1:12345")
	private String IP = "127.0.0.1:12345";
	
	public BootstrapServerConfig parseArgs(String[] args){
		
		String ip = "193.0.0.1";
		int port = 12345;
		
		CmdLineParser parser = new CmdLineParser(this);
		try{
		
			parser.parseArgument(args);
		
			String[] ipAndPort = IP.split(":");
			if(ipAndPort.length != 2){
				throw new Exception("Wrong IP and Port format");
			}
			ip = ipAndPort[0];
			port = Integer.parseInt(ipAndPort[1]);
			
		}catch(Exception e){
			LOG.error(e.getMessage());
			parser.printUsage(System.out);
		}
		
		if(HELP){
			parser.printUsage(System.out);
			System.exit(0);
		}
		
		return new BootstrapServerConfig()
				.setIP(ip)
				.setPort(port);
	}
	
}
