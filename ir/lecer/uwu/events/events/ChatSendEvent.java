package ir.lecer.uwu.events.events;

import ir.lecer.uwu.events.handler.Event;

public class ChatSendEvent extends Event {
  private String message;
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public ChatSendEvent(String message) {
    this.message = message;
  }
  
  public String getMessage() {
    return this.message;
  }
}
