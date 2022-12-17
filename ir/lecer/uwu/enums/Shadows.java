package ir.lecer.uwu.enums;

public enum Shadows {
  PANEL_BOTTOM("panelbottom"),
  PANEL_BOTTOM_LEFT("panelbottomleft"),
  PANEL_BOTTOM_RIGHT("panelbottomright"),
  PANEL_LEFT("panelleft"),
  PANEL_RIGHT("panelright"),
  PANEL_TOP("paneltop"),
  PANEL_TOP_LEFT("paneltopleft"),
  PANEL_TOP_RIGHT("paneltopright"),
  SHADOW("shadow");
  
  Shadows(String toText) {
    this.toText = toText;
  }
  
  private final String toText;
  
  public String getToText() {
    return this.toText;
  }
}
