package ir.lecer.uwu.ui.editor;

public class ClientSettings {
  private final String name;
  
  private final String text;
  
  private int x;
  
  private int y;
  
  private final int width;
  
  private final int height;
  
  private boolean toggle;
  
  public void setX(int x) {
    this.x = x;
  }
  
  public void setY(int y) {
    this.y = y;
  }
  
  public void setToggle(boolean toggle) {
    this.toggle = toggle;
  }
  
  public ClientSettings(String name, String text, int x, int y, int width, int height, boolean toggle) {
    this.name = name;
    this.text = text;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.toggle = toggle;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getText() {
    return this.text;
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public boolean isToggle() {
    return this.toggle;
  }
  
  public boolean isHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseY >= this.y && mouseX <= this.x + this.width && mouseY <= this.y + this.height);
  }
}
