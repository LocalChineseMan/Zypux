package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsDataFetcher {
  private static final Logger LOGGER = LogManager.getLogger();
  
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
  
  private static final int SERVER_UPDATE_INTERVAL = 60;
  
  private static final int PENDING_INVITES_INTERVAL = 10;
  
  private static final int TRIAL_UPDATE_INTERVAL = 60;
  
  private static final int LIVE_STATS_INTERVAL = 10;
  
  private volatile boolean stopped = true;
  
  private ServerListUpdateTask serverListUpdateTask = new ServerListUpdateTask();
  
  private PendingInviteUpdateTask pendingInviteUpdateTask = new PendingInviteUpdateTask();
  
  private TrialAvailabilityTask trialAvailabilityTask = new TrialAvailabilityTask();
  
  private LiveStatsTask liveStatsTask = new LiveStatsTask();
  
  private Set<RealmsServer> removedServers = Sets.newHashSet();
  
  private List<RealmsServer> servers = Lists.newArrayList();
  
  private RealmsServerPlayerLists livestats;
  
  private int pendingInvitesCount;
  
  private boolean trialAvailable = false;
  
  private ScheduledFuture<?> serverListScheduledFuture;
  
  private ScheduledFuture<?> pendingInviteScheduledFuture;
  
  private ScheduledFuture<?> trialAvailableScheduledFuture;
  
  private ScheduledFuture<?> liveStatsScheduledFuture;
  
  private Map<Task, Boolean> fetchStatus = new ConcurrentHashMap<Task, Boolean>((Task.values()).length);
  
  public boolean isStopped() {
    return this.stopped;
  }
  
  public synchronized void init() {
    if (this.stopped) {
      this.stopped = false;
      cancelTasks();
      scheduleTasks();
    } 
  }
  
  public synchronized void initWithSpecificTaskList(List<Task> tasks) {
    if (this.stopped) {
      this.stopped = false;
      cancelTasks();
      for (Task task : tasks) {
        this.fetchStatus.put(task, Boolean.valueOf(false));
        switch (task) {
          case SERVER_LIST:
            this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate(this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
          case PENDING_INVITE:
            this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
          case TRIAL_AVAILABLE:
            this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
          case LIVE_STATS:
            this.liveStatsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.liveStatsTask, 0L, 10L, TimeUnit.SECONDS);
        } 
      } 
    } 
  }
  
  public boolean isFetchedSinceLastTry(Task task) {
    Boolean result = this.fetchStatus.get(task);
    return (result == null) ? false : result.booleanValue();
  }
  
  public void markClean() {
    for (Task task : this.fetchStatus.keySet())
      this.fetchStatus.put(task, Boolean.valueOf(false)); 
  }
  
  public synchronized void forceUpdate() {
    stop();
    init();
  }
  
  public synchronized List<RealmsServer> getServers() {
    return Lists.newArrayList(this.servers);
  }
  
  public synchronized int getPendingInvitesCount() {
    return this.pendingInvitesCount;
  }
  
  public synchronized boolean isTrialAvailable() {
    return this.trialAvailable;
  }
  
  public synchronized RealmsServerPlayerLists getLivestats() {
    return this.livestats;
  }
  
  public synchronized void stop() {
    this.stopped = true;
    cancelTasks();
  }
  
  private void scheduleTasks() {
    for (Task task : Task.values())
      this.fetchStatus.put(task, Boolean.valueOf(false)); 
    this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate(this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
    this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
    this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
    this.liveStatsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.liveStatsTask, 0L, 10L, TimeUnit.SECONDS);
  }
  
  private void cancelTasks() {
    try {
      if (this.serverListScheduledFuture != null)
        this.serverListScheduledFuture.cancel(false); 
      if (this.pendingInviteScheduledFuture != null)
        this.pendingInviteScheduledFuture.cancel(false); 
      if (this.trialAvailableScheduledFuture != null)
        this.trialAvailableScheduledFuture.cancel(false); 
      if (this.liveStatsScheduledFuture != null)
        this.liveStatsScheduledFuture.cancel(false); 
    } catch (Exception e) {
      LOGGER.error("Failed to cancel Realms tasks", e);
    } 
  }
  
  private synchronized void setServers(List<RealmsServer> newServers) {
    int removedCnt = 0;
    for (RealmsServer server : this.removedServers) {
      if (newServers.remove(server))
        removedCnt++; 
    } 
    if (removedCnt == 0)
      this.removedServers.clear(); 
    this.servers = newServers;
  }
  
  public synchronized void removeItem(RealmsServer server) {
    this.servers.remove(server);
    this.removedServers.add(server);
  }
  
  private void sort(List<RealmsServer> servers) {
    Collections.sort(servers, (Comparator<? super RealmsServer>)new RealmsServer.McoServerComparator(Realms.getName()));
  }
  
  private boolean isActive() {
    return !this.stopped;
  }
  
  private class ServerListUpdateTask implements Runnable {
    private ServerListUpdateTask() {}
    
    public void run() {
      if (RealmsDataFetcher.this.isActive())
        updateServersList(); 
    }
    
    private void updateServersList() {
      try {
        RealmsClient client = RealmsClient.createRealmsClient();
        if (client != null) {
          List<RealmsServer> servers = (client.listWorlds()).servers;
          if (servers != null) {
            RealmsDataFetcher.this.sort(servers);
            RealmsDataFetcher.this.setServers(servers);
            RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, Boolean.valueOf(true));
          } else {
            RealmsDataFetcher.LOGGER.warn("Realms server list was null or empty");
          } 
        } 
      } catch (Exception e) {
        RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, Boolean.valueOf(true));
        RealmsDataFetcher.LOGGER.error("Couldn't get server list", e);
      } 
    }
  }
  
  private class PendingInviteUpdateTask implements Runnable {
    private PendingInviteUpdateTask() {}
    
    public void run() {
      if (RealmsDataFetcher.this.isActive())
        updatePendingInvites(); 
    }
    
    private void updatePendingInvites() {
      try {
        RealmsClient client = RealmsClient.createRealmsClient();
        if (client != null) {
          RealmsDataFetcher.this.pendingInvitesCount = client.pendingInvitesCount();
          RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.PENDING_INVITE, Boolean.valueOf(true));
        } 
      } catch (Exception e) {
        RealmsDataFetcher.LOGGER.error("Couldn't get pending invite count", e);
      } 
    }
  }
  
  private class TrialAvailabilityTask implements Runnable {
    private TrialAvailabilityTask() {}
    
    public void run() {
      if (RealmsDataFetcher.this.isActive())
        getTrialAvailable(); 
    }
    
    private void getTrialAvailable() {
      try {
        RealmsClient client = RealmsClient.createRealmsClient();
        if (client != null) {
          RealmsDataFetcher.this.trialAvailable = client.trialAvailable().booleanValue();
          RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, Boolean.valueOf(true));
        } 
      } catch (Exception e) {
        RealmsDataFetcher.LOGGER.error("Couldn't get trial availability", e);
      } 
    }
  }
  
  private class LiveStatsTask implements Runnable {
    private LiveStatsTask() {}
    
    public void run() {
      if (RealmsDataFetcher.this.isActive())
        getLiveStats(); 
    }
    
    private void getLiveStats() {
      try {
        RealmsClient client = RealmsClient.createRealmsClient();
        if (client != null) {
          RealmsDataFetcher.this.livestats = client.getLiveStats();
          RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.LIVE_STATS, Boolean.valueOf(true));
        } 
      } catch (Exception e) {
        RealmsDataFetcher.LOGGER.error("Couldn't get live stats", e);
      } 
    }
  }
  
  public enum Task {
    SERVER_LIST, PENDING_INVITE, TRIAL_AVAILABLE, LIVE_STATS;
  }
}
