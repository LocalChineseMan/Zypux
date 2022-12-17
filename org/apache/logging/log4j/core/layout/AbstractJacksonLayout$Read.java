package org.apache.logging.log4j.core.layout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

class ReadOnlyLogEventWrapper implements LogEvent {
  @JsonIgnore
  private final LogEvent event;
  
  public ReadOnlyLogEventWrapper(LogEvent event) {
    this.event = event;
  }
  
  public LogEvent toImmutable() {
    return this.event.toImmutable();
  }
  
  public Map<String, String> getContextMap() {
    return this.event.getContextMap();
  }
  
  public ReadOnlyStringMap getContextData() {
    return this.event.getContextData();
  }
  
  public ThreadContext.ContextStack getContextStack() {
    return this.event.getContextStack();
  }
  
  public String getLoggerFqcn() {
    return this.event.getLoggerFqcn();
  }
  
  public Level getLevel() {
    return this.event.getLevel();
  }
  
  public String getLoggerName() {
    return this.event.getLoggerName();
  }
  
  public Marker getMarker() {
    return this.event.getMarker();
  }
  
  public Message getMessage() {
    return this.event.getMessage();
  }
  
  public long getTimeMillis() {
    return this.event.getTimeMillis();
  }
  
  public Instant getInstant() {
    return this.event.getInstant();
  }
  
  public StackTraceElement getSource() {
    return this.event.getSource();
  }
  
  public String getThreadName() {
    return this.event.getThreadName();
  }
  
  public long getThreadId() {
    return this.event.getThreadId();
  }
  
  public int getThreadPriority() {
    return this.event.getThreadPriority();
  }
  
  public Throwable getThrown() {
    return this.event.getThrown();
  }
  
  public ThrowableProxy getThrownProxy() {
    return this.event.getThrownProxy();
  }
  
  public boolean isEndOfBatch() {
    return this.event.isEndOfBatch();
  }
  
  public boolean isIncludeLocation() {
    return this.event.isIncludeLocation();
  }
  
  public void setEndOfBatch(boolean endOfBatch) {}
  
  public void setIncludeLocation(boolean locationRequired) {}
  
  public long getNanoTime() {
    return this.event.getNanoTime();
  }
}
