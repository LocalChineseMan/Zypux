package ir.lecer.uwu.enums;

public enum Settings {
  NSFW("Nsfw Mode", "Enables Anime Background.", false),
  SHADOW("Shadow", "Renders shadow for rects.", false);
  
  Settings(String name, String text, boolean toggle) {
    this.name = name;
    this.text = text;
    this.toggle = toggle;
  }
  
  private final String name;
  
  private final String text;
  
  private final boolean toggle;
  
  public String getName() {
    return this.name;
  }
  
  public String getText() {
    return this.text;
  }
  
  public boolean isToggle() {
    return this.toggle;
  }
}
