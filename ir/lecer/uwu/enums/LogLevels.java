package ir.lecer.uwu.enums;

import java.awt.Color;

public enum LogLevels {
  INFO(new Color(6749952)),
  WARN(new Color(16766208)),
  ERROR(new Color(16711680));
  
  LogLevels(Color color) {
    this.color = color;
  }
  
  private final Color color;
  
  public Color getColor() {
    return this.color;
  }
}
