package ir.lecer.uwu.interfaces;

import java.awt.Color;
import java.util.ArrayList;

public class Setting {
  private final String name;
  
  private final Module parent;
  
  private final String mode;
  
  private String stringValue;
  
  private ArrayList<String> options;
  
  private boolean booleanValue;
  
  private double doubleValue;
  
  private double min;
  
  private double max;
  
  private Color color;
  
  private boolean alpha;
  
  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }
  
  public void setOptions(ArrayList<String> options) {
    this.options = options;
  }
  
  public void setBooleanValue(boolean booleanValue) {
    this.booleanValue = booleanValue;
  }
  
  public void setDoubleValue(double doubleValue) {
    this.doubleValue = doubleValue;
  }
  
  public void setMin(double min) {
    this.min = min;
  }
  
  public void setMax(double max) {
    this.max = max;
  }
  
  public void setColor(Color color) {
    this.color = color;
  }
  
  public void setAlpha(boolean alpha) {
    this.alpha = alpha;
  }
  
  public void setSpacebar(boolean spacebar) {
    this.spacebar = spacebar;
  }
  
  public void setOnlyint(boolean onlyint) {
    this.onlyint = onlyint;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Setting))
      return false; 
    Setting other = (Setting)o;
    if (!other.canEqual(this))
      return false; 
    if (isBooleanValue() != other.isBooleanValue())
      return false; 
    if (Double.compare(getDoubleValue(), other.getDoubleValue()) != 0)
      return false; 
    if (Double.compare(getMin(), other.getMin()) != 0)
      return false; 
    if (Double.compare(getMax(), other.getMax()) != 0)
      return false; 
    if (isAlpha() != other.isAlpha())
      return false; 
    if (isSpacebar() != other.isSpacebar())
      return false; 
    if (isOnlyint() != other.isOnlyint())
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$parent = getParent(), other$parent = other.getParent();
    if ((this$parent == null) ? (other$parent != null) : !this$parent.equals(other$parent))
      return false; 
    Object this$mode = getMode(), other$mode = other.getMode();
    if ((this$mode == null) ? (other$mode != null) : !this$mode.equals(other$mode))
      return false; 
    Object this$stringValue = getStringValue(), other$stringValue = other.getStringValue();
    if ((this$stringValue == null) ? (other$stringValue != null) : !this$stringValue.equals(other$stringValue))
      return false; 
    Object<String> this$options = (Object<String>)getOptions(), other$options = (Object<String>)other.getOptions();
    if ((this$options == null) ? (other$options != null) : !this$options.equals(other$options))
      return false; 
    Object this$color = getColor(), other$color = other.getColor();
    return !((this$color == null) ? (other$color != null) : !this$color.equals(other$color));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Setting;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isBooleanValue() ? 79 : 97);
    long $doubleValue = Double.doubleToLongBits(getDoubleValue());
    result = result * 59 + (int)($doubleValue >>> 32L ^ $doubleValue);
    long $min = Double.doubleToLongBits(getMin());
    result = result * 59 + (int)($min >>> 32L ^ $min);
    long $max = Double.doubleToLongBits(getMax());
    result = result * 59 + (int)($max >>> 32L ^ $max);
    result = result * 59 + (isAlpha() ? 79 : 97);
    result = result * 59 + (isSpacebar() ? 79 : 97);
    result = result * 59 + (isOnlyint() ? 79 : 97);
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $parent = getParent();
    result = result * 59 + (($parent == null) ? 43 : $parent.hashCode());
    Object $mode = getMode();
    result = result * 59 + (($mode == null) ? 43 : $mode.hashCode());
    Object $stringValue = getStringValue();
    result = result * 59 + (($stringValue == null) ? 43 : $stringValue.hashCode());
    Object<String> $options = (Object<String>)getOptions();
    result = result * 59 + (($options == null) ? 43 : $options.hashCode());
    Object $color = getColor();
    return result * 59 + (($color == null) ? 43 : $color.hashCode());
  }
  
  public String toString() {
    return "Setting(name=" + getName() + ", parent=" + getParent() + ", mode=" + getMode() + ", stringValue=" + getStringValue() + ", options=" + getOptions() + ", booleanValue=" + isBooleanValue() + ", doubleValue=" + getDoubleValue() + ", min=" + getMin() + ", max=" + getMax() + ", color=" + getColor() + ", alpha=" + isAlpha() + ", spacebar=" + isSpacebar() + ", onlyint=" + isOnlyint() + ")";
  }
  
  public String getName() {
    return this.name;
  }
  
  public Module getParent() {
    return this.parent;
  }
  
  public String getMode() {
    return this.mode;
  }
  
  public String getStringValue() {
    return this.stringValue;
  }
  
  public ArrayList<String> getOptions() {
    return this.options;
  }
  
  public boolean isBooleanValue() {
    return this.booleanValue;
  }
  
  public double getDoubleValue() {
    return this.doubleValue;
  }
  
  public double getMin() {
    return this.min;
  }
  
  public double getMax() {
    return this.max;
  }
  
  public Color getColor() {
    return this.color;
  }
  
  public boolean isAlpha() {
    return this.alpha;
  }
  
  private boolean spacebar = false;
  
  public boolean isSpacebar() {
    return this.spacebar;
  }
  
  private boolean onlyint = false;
  
  public boolean isOnlyint() {
    return this.onlyint;
  }
  
  public Setting(String name, Module parent, String stringValue, ArrayList<String> options) {
    this.name = name;
    this.parent = parent;
    this.stringValue = stringValue.toLowerCase();
    this.options = options;
    this.mode = "Combo";
  }
  
  public Setting(String name, Module parent, boolean booleanValue) {
    this.name = name;
    this.parent = parent;
    this.booleanValue = booleanValue;
    this.mode = "Check";
  }
  
  public Setting(String name, Module parent, double doubleValue, double min, double max, boolean onlyint) {
    this.name = name;
    this.parent = parent;
    this.doubleValue = doubleValue;
    this.min = min;
    this.max = max;
    this.onlyint = onlyint;
    this.mode = "Slider";
  }
  
  public Setting(String name, Module parent, String stringValue, boolean spacebar, int max) {
    this.name = name;
    this.parent = parent;
    this.stringValue = stringValue;
    this.spacebar = spacebar;
    this.max = max;
    this.mode = "Text";
  }
  
  public Setting(String name, Module parent, Color color, boolean alpha) {
    this.name = name;
    this.parent = parent;
    this.color = color;
    this.alpha = alpha;
    this.mode = "Color";
  }
  
  public boolean isCombo() {
    return this.mode.equalsIgnoreCase("Combo");
  }
  
  public boolean isCheck() {
    return this.mode.equalsIgnoreCase("Check");
  }
  
  public boolean isSlider() {
    return this.mode.equalsIgnoreCase("Slider");
  }
  
  public boolean isText() {
    return this.mode.equalsIgnoreCase("Text");
  }
  
  public boolean isColor() {
    return this.mode.equalsIgnoreCase("Color");
  }
}
