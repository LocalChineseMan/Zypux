package ir.lecer.uwu.events.events;

import ir.lecer.uwu.events.handler.Event;

public class PreClickGUIRenderEvent extends Event {
  private final int mouseX;
  
  private final int mouseY;
  
  public PreClickGUIRenderEvent(int mouseX, int mouseY) {
    this.mouseX = mouseX;
    this.mouseY = mouseY;
  }
  
  public int getMouseX() {
    return this.mouseX;
  }
  
  public int getMouseY() {
    return this.mouseY;
  }
}
