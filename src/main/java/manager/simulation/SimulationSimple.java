package manager.simulation;

import se.sics.kompics.network.Address;
import se.sics.kompics.network.Msg;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.network.NetworkModel;
import se.sics.kompics.simulator.network.identifier.IdentifierExtractor;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.overlays.id.OverlayIdRegistry;
import se.sics.kompics.simulator.util.GlobalView;

public class SimulationSimple extends SimulationBase {

	public SimulationSimple(SimulationSetup setup) {
		super(setup);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 259891341398951882L;
	
	@SuppressWarnings("serial")
	@Override
	public SimulationScenario getScenario() {
		
		SimulationScenario scen = new SimulationScenario(){
			{
				
				StochasticProcess systemSetup = new StochasticProcess() {
	                {
	                    eventInterArrivalTime(constant(1000));
	                    raise(1, systemSetupOp);
	                }
	            };
	            StochasticProcess startBootstrapServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startBootstrapServerOp);
                    }
                };
                
                StochasticProcess startGatewayServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startGatewayServerOp);
                    }
                };
                
                StochasticProcess startPeers = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(10, startNodeOp, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startWebServer = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(1, startWebServerNode, new ConstantDistribution<Integer>(Integer.class, 1337));
                    }
                };
                StochasticProcess startUsers = new StochasticProcess() {
                    {
                        eventInterArrivalTime(uniform(1000, 1100));
                        raise(10, startUserNode, new BasicIntSequentialDistribution(100));
                        
                    }
                };
	            
                
                
	            systemSetup.start();
	            
	            //Start the bootstrap server
	            startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
	            
	            //Start the gateway server
	            startGatewayServer.startAfterTerminationOf(1000, startBootstrapServer);
	            
	            //Start the peers
	            startPeers.startAfterTerminationOf(1000, startGatewayServer);
	            
	            //Start web server
	            startWebServer.startAfterTerminationOf(1000, startPeers);
	            
	            //Start users
	            startUsers.startAfterTerminationOf(1000000, startWebServer);
	            
	            NetworkModel m = new NetworkModel() {
					
					@Override
					public long getLatencyMs(Msg message) {
						// TODO Auto-generated method stub
						return 0;
					}
				};
				
				
			}
		};
		
		
		
		return scen;
	}

	
	
	
}
