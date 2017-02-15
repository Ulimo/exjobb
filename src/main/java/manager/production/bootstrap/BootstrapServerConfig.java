package manager.production.bootstrap;

import java.net.InetAddress;
import java.net.UnknownHostException;

import se.sics.ktoolbox.util.identifiable.basic.IntIdentifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.basic.BasicAddress;
import se.sics.ktoolbox.util.network.nat.NatAwareAddressImpl;

public class BootstrapServerConfig {

	private String IP;
	private int port;
	
	
	public BootstrapServerConfig setIP(String IP){
		this.IP = IP;
		return this;
	}
	
	public String getIP(){
		return this.IP;
	}
	
	public BootstrapServerConfig setPort(int port){
		this.port = port;
		return this;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public KAddress getAddress() throws UnknownHostException{
		return NatAwareAddressImpl.open(new BasicAddress(InetAddress.getByName(IP), port, new IntIdentifier(0)));
	}
	
}
