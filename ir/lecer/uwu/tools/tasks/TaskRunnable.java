package ir.lecer.uwu.tools.tasks;

import ir.lecer.uwu.tools.utilities.MathUtils;
import java.util.concurrent.Future;

public class TaskRunnable {
  private final int taskID;
  
  private final Future<?> object;
  
  public int getTaskID() {
    return this.taskID;
  }
  
  public Future<?> getObject() {
    return this.object;
  }
  
  public TaskRunnable(Future<?> object) {
    this.object = object;
    this.taskID = MathUtils.randInt(2147483647);
    TaskManager.taskMap.put(Integer.valueOf(getTaskID()), this);
  }
  
  public void cancel() {
    this.object.cancel(false);
  }
}
