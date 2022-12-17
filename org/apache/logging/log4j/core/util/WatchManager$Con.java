package org.apache.logging.log4j.core.util;

final class ConfigurationMonitor {
  private final Watcher watcher;
  
  private volatile long lastModifiedMillis;
  
  public ConfigurationMonitor(long lastModifiedMillis, Watcher watcher) {
    this.watcher = watcher;
    this.lastModifiedMillis = lastModifiedMillis;
  }
  
  public Watcher getWatcher() {
    return this.watcher;
  }
  
  private void setLastModifiedMillis(long lastModifiedMillis) {
    this.lastModifiedMillis = lastModifiedMillis;
  }
  
  public String toString() {
    return "ConfigurationMonitor [watcher=" + this.watcher + ", lastModifiedMillis=" + this.lastModifiedMillis + "]";
  }
}
