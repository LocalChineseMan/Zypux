package net.minecraft.block;

import net.minecraft.util.IStringSerializable;

public enum EnumDoorHalf implements IStringSerializable {
  UPPER, LOWER;
  
  public String toString() {
    return getName();
  }
  
  public String getName() {
    return (this == UPPER) ? "upper" : "lower";
  }
}
