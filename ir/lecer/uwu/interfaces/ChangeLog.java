package ir.lecer.uwu.interfaces;

import java.awt.Color;

public class ChangeLog {
  private final int defY;
  
  private int y;
  
  private final String text;
  
  private final Color color;
  
  public void setY(int y) {
    this.y = y;
  }
  
  public ChangeLog(int defY, int y, String text, Color color) {
    this.defY = defY;
    this.y = y;
    this.text = text;
    this.color = color;
  }
  
  public int getDefY() {
    return this.defY;
  }
  
  public int getY() {
    return this.y;
  }
  
  public String getText() {
    return this.text;
  }
  
  public Color getColor() {
    return this.color;
  }
}
