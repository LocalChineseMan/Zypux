package ir.lecer.uwu.enums;

import java.awt.Color;
import net.minecraft.util.ResourceLocation;

public enum Notifications {
  INFO("Info", new ResourceLocation(null), Color.GREEN),
  WARN("Warning", new ResourceLocation(null), Color.YELLOW),
  ERROR("Error", new ResourceLocation(null), Color.RED);
  
  Notifications(String name, ResourceLocation logo, Color color) {
    this.name = name;
    this.logo = logo;
    this.color = color;
  }
  
  private final String name;
  
  private final ResourceLocation logo;
  
  private final Color color;
  
  public String getName() {
    return this.name;
  }
  
  public ResourceLocation getLogo() {
    return this.logo;
  }
  
  public Color getColor() {
    return this.color;
  }
}
