package manager.test.bootstrapclient;

import java.net.InetAddress;
import java.net.UnknownHostException;

import se.sics.ktoolbox.util.identifiable.basic.IntIdentifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.basic.BasicAddress;
import se.sics.ktoolbox.util.network.nat.NatAwareAddressImpl;

public class BootstrapClientConfig {

	private String selfIp;
	private int selfPort;
	
	private String serverIp;
	private int serverPort;
	
	public BootstrapClientConfig setSelfIp(String ip){
		this.selfIp = ip;
		return this;
	}
	
	public BootstrapClientConfig setSelfPort(int port){
		this.selfPort = port;
		return this;
	}
	
	public BootstrapClientConfig setServerIp(String ip){
		this.serverIp = ip;
		return this;
	}
	
	public BootstrapClientConfig setServerPort(int port){
		this.serverPort = port;
		return this;
	}
	
	public String getSelfIp(){
		return this.selfIp;
	}
	
	public int getSelfPort(){
		return this.selfPort;
	}
	
	public String getServerIp(){
		return this.serverIp;
	}
	
	public int getServerPort(){
		return this.serverPort;
	}
	
	public KAddress getSelfAddress() throws UnknownHostException{
		return NatAwareAddressImpl.open(new BasicAddress(InetAddress.getByName(selfIp), selfPort, new IntIdentifier(0)));
	}
	
	public KAddress getServerAddress() throws UnknownHostException{
		return NatAwareAddressImpl.open(new BasicAddress(InetAddress.getByName(serverIp), serverPort, new IntIdentifier(0)));
	}
}
