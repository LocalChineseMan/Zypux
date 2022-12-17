package ir.lecer.uwu.events.events;

import ir.lecer.uwu.events.handler.Event;

public class GuiContainerRenderEvent extends Event {
  private boolean background;
  
  public void setBackground(boolean background) {
    this.background = background;
  }
  
  public boolean isBackground() {
    return this.background;
  }
}
