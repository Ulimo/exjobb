package manager.simulation;

import java.lang.reflect.InvocationTargetException;

import se.sics.kompics.simulator.SimulationScenario;
//import se.sics.kompics.simulator.run.LauncherComp;
//import se.sics.kompics.simulator.run.
public class SimulationLauncher {
	

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		SimulationSetup setup = new SimulationSetupSimple("193.0.0.1", 12345, 0);
		
		SimulationScenario.setSeed(setup.getSeed());
		SimulationScenario scenario = Simulation.getScenario("manager.simulation.SimulationSimple", setup);//Simulation.<SimulationSimple>getScenario();
		
		scenario.simulate(LauncherComp.class);
		
		// TODO Auto-generated method stub
		System.out.println("hej");
	}

}
