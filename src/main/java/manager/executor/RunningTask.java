package manager.executor;

public class RunningTask {
	
	public final ExecutorAddTask task;
	public final long finishTime;

	public RunningTask(ExecutorAddTask task, long finishTime){
		this.task = task;
		this.finishTime = finishTime;
	}
	
}
