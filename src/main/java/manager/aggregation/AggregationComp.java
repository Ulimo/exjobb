package manager.aggregation;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Start;
import se.sics.ktoolbox.util.network.KAddress;

public class AggregationComp extends ComponentDefinition {

	private KAddress selfAdr;
	
	
	public AggregationComp(Init init){
		this.selfAdr = init.selfAdr;
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			
		}
		
	};
	
	
	
	public static class Init extends se.sics.kompics.Init<AggregationComp> {
		
		public final KAddress selfAdr;
		
		public Init(KAddress selfAdr){
			this.selfAdr = selfAdr;
		}
		
	}
	
}
