package ir.lecer.uwu.enums;

public enum ChatLevels {
  INFO("Info", 1, 'a'),
  DEBUG("Debug", 2, '6'),
  ERROR("Error", 3, 'c'),
  BIND("Bind", 4, 'b'),
  CONFIG("Config", 5, 'd');
  
  ChatLevels(String name, int number, char color) {
    this.name = name;
    this.number = number;
    this.color = color;
  }
  
  private final String name;
  
  private final int number;
  
  private final char color;
  
  public String getName() {
    return this.name;
  }
  
  public int getNumber() {
    return this.number;
  }
  
  public char getColor() {
    return this.color;
  }
}
