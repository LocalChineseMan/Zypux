package org.apache.logging.log4j.message;

class StatusMessage implements Message {
  private static final long serialVersionUID = 4199272162767841280L;
  
  private final String formattedMessage;
  
  private final Throwable throwable;
  
  public StatusMessage(String formattedMessage, Throwable throwable) {
    this.formattedMessage = formattedMessage;
    this.throwable = throwable;
  }
  
  public String getFormattedMessage() {
    return this.formattedMessage;
  }
  
  public String getFormat() {
    return this.formattedMessage;
  }
  
  public Object[] getParameters() {
    return null;
  }
  
  public Throwable getThrowable() {
    return this.throwable;
  }
  
  static class ParameterizedNoReferenceMessageFactory {}
}
