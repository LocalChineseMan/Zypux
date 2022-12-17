package ir.lecer.uwu.events.events;

import ir.lecer.uwu.events.handler.Event;

public class PressedKeyEvent extends Event {
  private final int key;
  
  public PressedKeyEvent(int key) {
    this.key = key;
  }
  
  public int getKey() {
    return this.key;
  }
}
