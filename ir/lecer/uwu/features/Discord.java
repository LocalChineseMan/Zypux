package ir.lecer.uwu.features;

import ir.lecer.uwu.tools.tasks.TaskManager;
import ir.lecer.uwu.tools.tasks.TaskRunnable;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;

public class Discord implements Runnable {
  public static Discord getInstance() {
    return instance;
  }
  
  public static Discord instance = new Discord();
  
  public static String username = "";
  
  public static String tag = "";
  
  public static String id = "";
  
  public static boolean started;
  
  public static TaskRunnable task;
  
  public static Long startTime;
  
  public static void setupDiscord() {
    startTime = Long.valueOf(System.currentTimeMillis());
    DiscordEventHandlers handlers = (new DiscordEventHandlers.Builder()).setReadyEventHandler(user -> {
          username = user.username;
          tag = user.discriminator;
          id = user.userId;
          update("Loading...", "");
        }).build();
    DiscordRPC.discordInitialize("1040628345290891296", handlers, true);
    task = TaskManager.syncRepeat(getInstance(), 0L, 20L);
    started = true;
  }
  
  public static void shutdown() {
    started = false;
    TaskManager.cancel(task);
    DiscordRPC.discordShutdown();
  }
  
  public static void update(String firsline, String secondline) {
    DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(secondline);
    builder.setBigImage("logo", "");
    builder.setDetails(firsline);
    builder.setStartTimestamps(startTime.longValue());
    DiscordRPC.discordUpdatePresence(builder.build());
  }
  
  public void run() {
    if (started)
      DiscordRPC.discordRunCallbacks(); 
  }
}
