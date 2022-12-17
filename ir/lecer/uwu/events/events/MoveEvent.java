package ir.lecer.uwu.events.events;

import ir.lecer.uwu.events.handler.Event;

public class MoveEvent extends Event {
  private double x;
  
  private double y;
  
  private double z;
  
  private boolean safeWalk;
  
  private boolean onGround;
  
  public void setX(double x) {
    this.x = x;
  }
  
  public void setY(double y) {
    this.y = y;
  }
  
  public void setZ(double z) {
    this.z = z;
  }
  
  public void setSafeWalk(boolean safeWalk) {
    this.safeWalk = safeWalk;
  }
  
  public void setOnGround(boolean onGround) {
    this.onGround = onGround;
  }
  
  public double getX() {
    return this.x;
  }
  
  public double getY() {
    return this.y;
  }
  
  public double getZ() {
    return this.z;
  }
  
  public boolean isSafeWalk() {
    return this.safeWalk;
  }
  
  public boolean isOnGround() {
    return this.onGround;
  }
  
  public MoveEvent(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
