package ir.lecer.uwu.events.events;

import ir.lecer.uwu.events.handler.Event;

public class Render3DEvent extends Event {
  private final float partialTicks;
  
  public Render3DEvent(float partialTicks) {
    this.partialTicks = partialTicks;
  }
  
  public float getPartialTicks() {
    return this.partialTicks;
  }
}
