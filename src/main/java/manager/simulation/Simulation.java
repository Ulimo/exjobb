package manager.simulation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import se.sics.kompics.simulator.SimulationScenario;

public class Simulation {
	
	static class helper <T extends IScenario> {
		@SuppressWarnings("unchecked")
		public Class<T> returnClass(){
			ParameterizedType parameterizedType =
	        (ParameterizedType) getClass().getGenericSuperclass(); //getGenericSuperClass();
			return (Class<T>) parameterizedType.getActualTypeArguments()[0];
		}
	}
	
	public static <T extends IScenario> SimulationScenario getScenario(SimulationSetup setup) throws InstantiationException, IllegalAccessException{
		Class<T> classOfT = new helper<T>().returnClass();
		return classOfT.newInstance().getScenario();
	}
	
	public static SimulationScenario getScenario(Class<?> c, SimulationSetup setup) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		
		Object o = c.getDeclaredConstructor(SimulationSetup.class).newInstance(setup);
		
		if (o instanceof IScenario) {
			IScenario scenario = (IScenario) o;
			return scenario.getScenario();
		}else{
			return null;
		}
	}
	
	public static SimulationScenario getScenario(String className, SimulationSetup setup) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		return getScenario(Class.forName(className), setup);
	}
	
}
