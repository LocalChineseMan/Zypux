package ir.lecer.uwu.events.events;

import ir.lecer.uwu.events.handler.Event;

public class Render2DEvent extends Event {
  private final float width;
  
  private final float height;
  
  public Render2DEvent(float width, float height) {
    this.width = width;
    this.height = height;
  }
  
  public float getHeight() {
    return this.height;
  }
  
  public float getWidth() {
    return this.width;
  }
}
