package manager.simulation;


import java.util.UUID;

import manager.datacollection.DataCollector;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

public class SimulationTimeComp extends ComponentDefinition {

	private static long timeIncrease = 1;
	
	private long currentTime = 0;
	
	private UUID timeTimeout;
	
	Positive<Timer> timerPort = requires(Timer.class);
	
	public SimulationTimeComp(Init init){
		
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			setTime(currentTime);
		}
		
	};
	
	
	private void scheduleTimeout(){
		SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(timeIncrease, timeIncrease);
		
		timeTimeout rt = new timeTimeout(spt);
		spt.setTimeoutEvent(rt);
		
		timeTimeout = rt.getTimeoutId();
		trigger(spt, timerPort);
		
	}
	
	private class timeTimeout extends Timeout {

		protected timeTimeout(SchedulePeriodicTimeout request) {
			super(request);
		}
		
	}
	
	private void setTime(long time){
		GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
		gv.setValue("simulation.currenttime", time);
		//Long dataCollector = gv.getValue("simulation.currentTime", Long.class);
		//dataCollector.addToMaxResource(this.resources);
		
	}
	
	public static class Init extends se.sics.kompics.Init<SimulationTimeComp>{
		public Init(){
			
		}
	}
}
