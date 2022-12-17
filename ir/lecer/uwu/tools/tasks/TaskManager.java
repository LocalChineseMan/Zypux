package ir.lecer.uwu.tools.tasks;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public final class TaskManager {
  private TaskManager() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  private static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
  
  private static final OperatingSystemMXBean osBean = ManagementFactory.<OperatingSystemMXBean>getPlatformMXBean(OperatingSystemMXBean.class);
  
  private static final Map<Long, Thread> threadMap = Maps.newConcurrentMap();
  
  public static final Map<Integer, TaskRunnable> taskMap = Maps.newConcurrentMap();
  
  private static final ThreadFactory factory = (new ThreadFactoryBuilder())
    .setDaemon(true)
    .setNameFormat("TaskManager-%d")
    .build();
  
  private static final ExecutorService asyncExecutor = Executors.newFixedThreadPool(8, factory);
  
  private static final EventLoop eventLoop = (new LocalEventLoopGroup(1, factory)).next();
  
  private static final BlockingQueue<FutureTask<?>> bsyncQueue = new ArrayBlockingQueue<>(1024);
  
  private static final Thread thread;
  
  private static boolean bsyncisAlive() {
    return thread.isAlive();
  }
  
  public static int async(@NotNull Runnable runnable) {
    return (new TaskRunnable(
        
        CompletableFuture.runAsync(runnable, asyncExecutor)
        .exceptionally(throwable -> {
            if (!(throwable instanceof java.util.concurrent.CancellationException))
              throwable.printStackTrace(); 
            return null;
          }))).getTaskID();
  }
  
  public static <T extends Future<?>> Future<?> async(@NotNull FutureTask<T> runnable) {
    return (new TaskRunnable(
        CompletableFuture.runAsync(runnable, asyncExecutor)
        .exceptionally(throwable -> {
            if (!(throwable instanceof java.util.concurrent.CancellationException))
              throwable.printStackTrace(); 
            return null;
          }))).getObject();
  }
  
  public static int sync(@NotNull Runnable runnable) {
    return (new TaskRunnable((Future<?>)eventLoop.submit(runnable).addListener(errorLogger()))).getTaskID();
  }
  
  static {
    thread = factory.newThread(() -> {
          while (bsyncisAlive()) {
            FutureTask<?> poll = bsyncQueue.poll();
            if (poll == null)
              continue; 
            poll.run();
          } 
        });
  }
  
  public static int bsync(@NotNull Runnable runnable) {
    FutureTask<?> task = new FutureTask(runnable, null);
    boolean add = bsyncQueue.add(task);
    return (new TaskRunnable(task)).getTaskID();
  }
  
  private static <T extends Future<?>> GenericFutureListener<T> errorLogger() {
    return future -> {
        if (!future.isCancelled() && future.cause() != null)
          future.cause().printStackTrace(); 
      };
  }
  
  public static <T extends Future<?>> Future<?> bsync(@NotNull FutureTask<T> runnable) {
    boolean add = bsyncQueue.add(runnable);
    return (new TaskRunnable(runnable)).getObject();
  }
  
  public static <T extends Future<?>> Future<?> sync(@NotNull FutureTask<T> runnable) {
    return (new TaskRunnable((Future<?>)eventLoop.submit(runnable).addListener(errorLogger()))).getObject();
  }
  
  public static int syncLater(@NotNull Runnable runnable, long delay) {
    try {
      return (new TaskRunnable((Future<?>)eventLoop.schedule(() -> Integer.valueOf(sync(runnable)), delay * 50L, TimeUnit.MILLISECONDS)
          .addListener(errorLogger()))).getTaskID();
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static int asyncLater(@NotNull Runnable runnable, long delay) {
    try {
      return (new TaskRunnable((Future<?>)eventLoop.schedule(() -> Integer.valueOf(async(runnable)), delay * 50L, TimeUnit.MILLISECONDS)
          .addListener(errorLogger()))).getTaskID();
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static int bsyncLater(@NotNull Runnable runnable, long delay) {
    try {
      return (new TaskRunnable((Future<?>)eventLoop.schedule(() -> Integer.valueOf(bsync(runnable)), delay * 50L, TimeUnit.MILLISECONDS)
          .addListener(errorLogger()))).getTaskID();
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void cancelAll() {
    taskMap.values().forEach(TaskManager::cancel);
    threadMap.values().forEach(TaskManager::cancel);
  }
  
  public static void cancel(Thread thread) {
    cancel(new long[] { thread.getId() });
  }
  
  public static void cancel(TaskRunnable runnable) {
    cancel(new int[] { runnable.getTaskID() });
  }
  
  public static void cancel(int... taskIds) {
    for (int id : taskIds) {
      if (taskMap.containsKey(Integer.valueOf(id))) {
        TaskRunnable z = taskMap.get(Integer.valueOf(id));
        taskMap.remove(Integer.valueOf(id));
        if (z.getObject().isCancelled() || z.getObject().isDone())
          return; 
        z.cancel();
      } 
    } 
  }
  
  public static int syncRepeat(Runnable runnable, int period) {
    return syncRepeat(runnable, 0L, period).getTaskID();
  }
  
  public static int asyncRepeat(Runnable runnable, int period) {
    return asyncRepeat(runnable, 0, period);
  }
  
  public static long threadRepeat(Runnable runnable, int period) {
    return threadRepeat(runnable, 0L, period);
  }
  
  public static int bsyncRepeat(Runnable runnable, int period) {
    return bsyncRepeat(runnable, 0, period);
  }
  
  public static int asyncRepeat(Runnable runnable, int delay, int period) {
    return (new TaskRunnable((Future<?>)eventLoop.scheduleAtFixedRate(() -> async(runnable), delay * 50L, 
          Math.max(period * 50L, 1L), TimeUnit.MILLISECONDS).addListener(errorLogger()))).getTaskID();
  }
  
  public static int bsyncRepeat(Runnable runnable, int delay, int period) {
    return (new TaskRunnable((Future<?>)eventLoop.scheduleAtFixedRate(() -> bsync(runnable), delay * 50L, 
          Math.max(period * 50L, 1L), TimeUnit.MILLISECONDS).addListener(errorLogger()))).getTaskID();
  }
  
  public static TaskRunnable bsyncRepeat(@NotNull Runnable runnable, long delay, long period) {
    return new TaskRunnable((Future<?>)eventLoop.scheduleAtFixedRate(() -> bsync(runnable), delay * 50L, 
          Math.max(period * 50L, 1L), TimeUnit.MILLISECONDS).addListener(errorLogger()));
  }
  
  public static TaskRunnable syncRepeat(@NotNull Runnable runnable, long delay, long period) {
    return new TaskRunnable((Future<?>)eventLoop.scheduleAtFixedRate(() -> sync(runnable), delay * 50L, 
          Math.max(period * 50L, 1L), TimeUnit.MILLISECONDS).addListener(errorLogger()));
  }
  
  public static TaskRunnable syncMRepeat(@NotNull Runnable runnable, long milisPeriod) {
    return new TaskRunnable((Future<?>)eventLoop.scheduleAtFixedRate(() -> sync(runnable), 0L, 
          Math.max(milisPeriod, 1L), TimeUnit.MILLISECONDS).addListener(errorLogger()));
  }
  
  public static long thread(Runnable runnable) {
    Thread thread = factory.newThread(runnable);
    thread.start();
    return ((Thread)threadMap.compute(Long.valueOf(thread.getId()), (k, v) -> thread)).getId();
  }
  
  public static <T extends Future<?>> Future<?> thread(@NotNull FutureTask<T> future) {
    Thread thread = factory.newThread(future);
    return future;
  }
  
  public static long threadRepeat(Runnable runnable, long delay, long period) {
    Thread thread = factory.newThread(() -> {
          try {
            Thread.sleep(delay * 50L);
            while (true) {
              Thread.sleep(period * 50L);
              runnable.run();
            } 
          } catch (InterruptedException interruptedException) {
            return;
          } 
        });
    thread.start();
    return ((Thread)threadMap.compute(Long.valueOf(thread.getId()), (k, v) -> thread)).getId();
  }
  
  public static Future<Long> threadLater(Runnable runnable, long delay) {
    return eventLoop.schedule(() -> {
          Thread thread = factory.newThread(runnable);
          thread.start();
          return Long.valueOf(((Thread)threadMap.compute(Long.valueOf(thread.getId()), ())).getId());
        }delay * 50L, TimeUnit.MILLISECONDS)
      
      .addListener(errorLogger());
  }
  
  public static void cancel(long... threads) {
    Arrays.stream(threads)
      .filter(threadMap::containsKey)
      .mapToObj(threadMap::get)
      .filter(Objects::nonNull)
      .forEach(thread1 -> {
          thread1.stop();
          threadMap.remove(Long.valueOf(thread1.getId()));
        });
  }
  
  public static void cancel(ArrayList<Integer> tasks) {
    tasks.forEach(xva$0 -> cancel(new int[] { xva$0 }));
  }
  
  public static boolean isDone(int id) {
    if (!taskMap.containsKey(Integer.valueOf(id)))
      return true; 
    TaskRunnable z = taskMap.get(Integer.valueOf(id));
    return (z.getObject().isCancelled() || z.getObject().isDone());
  }
  
  public static boolean isCannceled(int id) {
    if (!taskMap.containsKey(Integer.valueOf(id)))
      return true; 
    TaskRunnable z = taskMap.get(Integer.valueOf(id));
    return z.getObject().isCancelled();
  }
}
