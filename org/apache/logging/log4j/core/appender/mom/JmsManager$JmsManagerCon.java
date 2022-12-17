package org.apache.logging.log4j.core.appender.mom;

import java.util.Properties;
import org.apache.logging.log4j.core.net.JndiManager;

public class JmsManagerConfiguration {
  private final Properties jndiProperties;
  
  private final String connectionFactoryName;
  
  private final String destinationName;
  
  private final String userName;
  
  private final char[] password;
  
  private final boolean immediateFail;
  
  private final boolean retry;
  
  private final long reconnectIntervalMillis;
  
  JmsManagerConfiguration(Properties jndiProperties, String connectionFactoryName, String destinationName, String userName, char[] password, boolean immediateFail, long reconnectIntervalMillis) {
    this.jndiProperties = jndiProperties;
    this.connectionFactoryName = connectionFactoryName;
    this.destinationName = destinationName;
    this.userName = userName;
    this.password = password;
    this.immediateFail = immediateFail;
    this.reconnectIntervalMillis = reconnectIntervalMillis;
    this.retry = (reconnectIntervalMillis > 0L);
  }
  
  public String getConnectionFactoryName() {
    return this.connectionFactoryName;
  }
  
  public String getDestinationName() {
    return this.destinationName;
  }
  
  public JndiManager getJndiManager() {
    return JndiManager.getJndiManager(getJndiProperties());
  }
  
  public Properties getJndiProperties() {
    return this.jndiProperties;
  }
  
  public char[] getPassword() {
    return this.password;
  }
  
  public long getReconnectIntervalMillis() {
    return this.reconnectIntervalMillis;
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public boolean isImmediateFail() {
    return this.immediateFail;
  }
  
  public boolean isRetry() {
    return this.retry;
  }
  
  public String toString() {
    return "JmsManagerConfiguration [jndiProperties=" + this.jndiProperties + ", connectionFactoryName=" + this.connectionFactoryName + ", destinationName=" + this.destinationName + ", userName=" + this.userName + ", immediateFail=" + this.immediateFail + ", retry=" + this.retry + ", reconnectIntervalMillis=" + this.reconnectIntervalMillis + "]";
  }
}
