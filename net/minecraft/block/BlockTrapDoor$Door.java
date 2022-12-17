package net.minecraft.block;

import net.minecraft.util.IStringSerializable;

public enum DoorHalf implements IStringSerializable {
  TOP("top"),
  BOTTOM("bottom");
  
  private final String name;
  
  DoorHalf(String name) {
    this.name = name;
  }
  
  public String toString() {
    return this.name;
  }
  
  public String getName() {
    return this.name;
  }
}
