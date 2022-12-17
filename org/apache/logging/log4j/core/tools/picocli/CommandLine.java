package org.apache.logging.log4j.core.tools.picocli;

enum TraceLevel {
  OFF, WARN, INFO, DEBUG;
  
  public boolean isEnabled(TraceLevel other) {
    return (ordinal() >= other.ordinal());
  }
  
  private void print(CommandLine.Tracer tracer, String msg, Object... params) {
    if (tracer.level.isEnabled(this))
      tracer.stream.printf(prefix(msg), params); 
  }
  
  private String prefix(String msg) {
    return "[picocli " + this + "] " + msg;
  }
  
  static TraceLevel lookup(String key) {
    return (key == null) ? WARN : ((CommandLine.access$2400(key) || "true".equalsIgnoreCase(key)) ? INFO : valueOf(key));
  }
  
  private enum CommandLine {
  
  }
  
  private static class CommandLine {}
}
