package manager.simulation;

import manager.datacollection.DataCollector;
import manager.gateway.GatewayComp;
//Internal
import manager.node.ManagerNodeComp;
import manager.user.UserComp;
import manager.webdebug.WebDebugComp;

//Log4j
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Java
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

//Kompics
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.network.identifier.IdentifierExtractor;
import se.sics.kompics.simulator.util.GlobalView;

//KToolbox
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapServerComp;
import se.sics.ktoolbox.util.network.basic.BasicAddress;
import se.sics.ktoolbox.util.network.nat.NatAwareAddressImpl;
import se.sics.ktoolbox.util.overlays.id.OverlayIdRegistry;
import se.sics.ktoolbox.util.identifiable.basic.IntIdentifier;

public abstract class SimulationBase extends IScenario {

	
	private static final Logger LOG = LoggerFactory.getLogger(SimulationBase.class);
	
	private final SimulationSetup setup;
	

	
	public SimulationBase(SimulationSetup setup){
		this.setup = setup;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1881571606160177619L;

	
	
	
	protected Operation<SetupEvent> systemSetupOp = new Operation<SetupEvent>() {
        /**
		 * 
		 */
		private static final long serialVersionUID = -7981694034525400767L;

		@Override
        public SetupEvent generate() {
            return new SetupEvent() {
                @Override
                public void setupSystemContext() {
                    OverlayIdRegistry.registerPrefix("manager", setup.getOverlayPrefix());
                }

                @Override
                public IdentifierExtractor getIdentifierExtractor() {
                	
                	LOG.debug("Get identifier");
                	
                    return new SimNodeIdExtractor();
                }
                
                @Override
                public void setupGlobalView(GlobalView gv) {
                        gv.setValue("simulation.datacollector", new DataCollector("test.txt"));
                }
            };
        }
    };
    
    Operation<StartNodeEvent> startBootstrapServerOp = new Operation<StartNodeEvent>() {

        @Override
        public StartNodeEvent generate() {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                    selfAdr = setup.getBootstrapAddress();
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return BootstrapServerComp.class;
                }

                @Override
                public BootstrapServerComp.Init getComponentInit() {
                    return new BootstrapServerComp.Init(selfAdr);
                }
            };
        }
    };
    
    Operation<StartNodeEvent> startGatewayServerOp = new Operation<StartNodeEvent>() {

        @Override
        public StartNodeEvent generate() {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                    selfAdr = setup.getGatewayServer();
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return GatewayComp.class;
                }

                @Override
                public GatewayComp.Init getComponentInit() {
                    return new GatewayComp.Init(selfAdr, setup.getBootstrapAddress());
                }
            };
        }
    };
    
    Operation1<StartNodeEvent, Integer> startNodeOp = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer nodeId) {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                    selfAdr = setup.getNodeAdr(nodeId);
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return ManagerNodeComp.class;
                }

                @Override
                public ManagerNodeComp.Init getComponentInit() {
                    return new ManagerNodeComp.Init(selfAdr, setup.getBootstrapAddress(), setup.getOverlayId(), setup.getWebServerAdr());
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    Map<String, Object> nodeConfig = new HashMap<>();
                    nodeConfig.put("system.id", nodeId);
                    nodeConfig.put("system.seed", setup.getNodeSeed(nodeId));
                    nodeConfig.put("system.port", setup.getPort());
                    return nodeConfig;
                }
            };
        }
    };
    
    Operation1<StartNodeEvent, Integer> startWebServerNode = new Operation1<StartNodeEvent, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer port) {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                        selfAdr = setup.getWebServerAdr();
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<>();
                    config.put("system.id", 254);
                    config.put("system.seed", setup.getNodeSeed(254));
                    config.put("system.port", setup.getPort());
                    //config.put("pingpong.simulation.checktimeout", 2000);
                    return config;
                }
                
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return WebDebugComp.class;
                }

                @Override
                public WebDebugComp.Init getComponentInit() {
                    return new WebDebugComp.Init(selfAdr, "127.0.0.1", port);
                }
            };
        }
    };
    
    
    Operation1<StartNodeEvent, Integer> startUserNode = new Operation1<StartNodeEvent, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer nodeId) {
            return new StartNodeEvent() {
                KAddress selfAdr;

                {
                        selfAdr = setup.getNodeAdr(nodeId);
                }

                /*@Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<>();
                    config.put("system.id", 254);
                    config.put("system.seed", setup.getNodeSeed(254));
                    config.put("system.port", setup.getPort());
                    //config.put("pingpong.simulation.checktimeout", 2000);
                    return config;
                }*/
                
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return UserComp.class;
                }

                @Override
                public UserComp.Init getComponentInit() {
                    return new UserComp.Init(selfAdr, setup.getGatewayServer(), setup.getOverlayId());
                }
            };
        }
    };
	
}
