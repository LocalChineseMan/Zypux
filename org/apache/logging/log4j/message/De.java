package org.apache.logging.log4j.message;

class AbstractFlowMessage implements FlowMessage {
  private static final long serialVersionUID = 1L;
  
  private final Message message;
  
  private final String text;
  
  AbstractFlowMessage(String text, Message message) {
    this.message = message;
    this.text = text;
  }
  
  public String getFormattedMessage() {
    if (this.message != null)
      return this.text + " " + this.message.getFormattedMessage(); 
    return this.text;
  }
  
  public String getFormat() {
    if (this.message != null)
      return this.text + ": " + this.message.getFormat(); 
    return this.text;
  }
  
  public Object[] getParameters() {
    if (this.message != null)
      return this.message.getParameters(); 
    return null;
  }
  
  public Throwable getThrowable() {
    if (this.message != null)
      return this.message.getThrowable(); 
    return null;
  }
  
  public Message getMessage() {
    return this.message;
  }
  
  public String getText() {
    return this.text;
  }
}
