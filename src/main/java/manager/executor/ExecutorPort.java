package manager.executor;

import se.sics.kompics.PortType;

public class ExecutorPort extends PortType {
	{
		indication(ExecutorAddTask.class);
		request(ExecutorAddTask.class);
		
		indication(ExecutorTaskDone.class);
		request(ExecutorTaskDone.class);
	}
}
