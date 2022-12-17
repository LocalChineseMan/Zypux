package io.netty.util.concurrent;

import java.util.concurrent.ThreadFactory;

final class DefaultEventExecutor extends SingleThreadEventExecutor {
  DefaultEventExecutor(DefaultEventExecutorGroup parent, ThreadFactory threadFactory) {
    super(parent, threadFactory, true);
  }
  
  protected void run() {
    do {
      Runnable task = takeTask();
      if (task == null)
        continue; 
      task.run();
      updateLastExecutionTime();
    } while (!confirmShutdown());
  }
}
