
/*
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS) Copyright (C)
 * 2009 Royal Institute of Technology (KTH)
 *
 * GVoD is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package manager.simulation;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Fault;
import se.sics.kompics.FaultHandler;
import se.sics.kompics.Kompics;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.core.SimulatorControlPort;
import se.sics.kompics.simulator.core.SimulatorPort;
import se.sics.kompics.simulator.core.impl.P2pSimulator;
import se.sics.kompics.simulator.core.impl.P2pSimulator.P2pSimulatorInit;
import se.sics.kompics.simulator.core.impl.SimulatorMngrComp;
import se.sics.kompics.simulator.network.NetworkModel;
import se.sics.kompics.simulator.scheduler.BasicSimulationScheduler;
import se.sics.kompics.timer.Timer;

/**
 * @author Alex Ormenisan <aaor@sics.se>
 */
public class LauncherComp extends ComponentDefinition {
    
    public static BasicSimulationScheduler simulatorScheduler = new BasicSimulationScheduler();
    
    public static NetworkModel networkModel = new SimNetworkModel();
    
    public static final FaultHandler NO_CRASH_HANDLER = new FaultHandler() {
        
        @Override
        public Fault.ResolveAction handle(Fault f) {
            System.err.println("Fault in Kompics component escalated to root:\n" + f);
            f.getCause().printStackTrace(System.err);
            return Fault.ResolveAction.DESTROY;
        }
    };
    
    public static void main(String[] args) {
        Kompics.setFaultHandler(NO_CRASH_HANDLER);
        Kompics.setScheduler(simulatorScheduler);
        Kompics.createAndStart(LauncherComp.class, 1);
    }
    
    private final SimulationScenario scenario = SimulationScenario.load(System.getProperty("scenario"));
    
    public LauncherComp() {
        Component simulator = create(P2pSimulator.class, new P2pSimulatorInit(simulatorScheduler, scenario, networkModel));
        Component simManager = create(SimulatorMngrComp.class, new SimulatorMngrComp.SimulatorMngrInit());
        connect(simManager.getNegative(Network.class), simulator.getPositive(Network.class), Channel.TWO_WAY);
        connect(simManager.getNegative(Timer.class), simulator.getPositive(Timer.class), Channel.TWO_WAY);
        connect(simManager.getNegative(SimulatorPort.class), simulator.getPositive(SimulatorPort.class), Channel.TWO_WAY);
        connect(simManager.getNegative(SimulatorControlPort.class), simulator.getPositive(SimulatorControlPort.class), Channel.TWO_WAY);
    }
}