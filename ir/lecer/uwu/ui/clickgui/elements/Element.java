package ir.lecer.uwu.ui.clickgui.elements;

import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.ui.clickgui.ClickGUIRenderer;

public class Element {
  public ClickGUIRenderer clickgui;
  
  public ModuleButton parent;
  
  public Setting setting;
  
  public double offset;
  
  public double x;
  
  public double y;
  
  public double width;
  
  public double height;
  
  public double onebox;
  
  public String elementTitle;
  
  public boolean comboextended;
  
  public void setClickgui(ClickGUIRenderer clickgui) {
    this.clickgui = clickgui;
  }
  
  public void setParent(ModuleButton parent) {
    this.parent = parent;
  }
  
  public void setSetting(Setting setting) {
    this.setting = setting;
  }
  
  public void setOffset(double offset) {
    this.offset = offset;
  }
  
  public void setX(double x) {
    this.x = x;
  }
  
  public void setY(double y) {
    this.y = y;
  }
  
  public void setWidth(double width) {
    this.width = width;
  }
  
  public void setHeight(double height) {
    this.height = height;
  }
  
  public void setOnebox(double onebox) {
    this.onebox = onebox;
  }
  
  public void setElementTitle(String elementTitle) {
    this.elementTitle = elementTitle;
  }
  
  public void setComboextended(boolean comboextended) {
    this.comboextended = comboextended;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Element))
      return false; 
    Element other = (Element)o;
    if (!other.canEqual(this))
      return false; 
    if (Double.compare(getOffset(), other.getOffset()) != 0)
      return false; 
    if (Double.compare(getX(), other.getX()) != 0)
      return false; 
    if (Double.compare(getY(), other.getY()) != 0)
      return false; 
    if (Double.compare(getWidth(), other.getWidth()) != 0)
      return false; 
    if (Double.compare(getHeight(), other.getHeight()) != 0)
      return false; 
    if (Double.compare(getOnebox(), other.getOnebox()) != 0)
      return false; 
    if (isComboextended() != other.isComboextended())
      return false; 
    Object this$clickgui = getClickgui(), other$clickgui = other.getClickgui();
    if ((this$clickgui == null) ? (other$clickgui != null) : !this$clickgui.equals(other$clickgui))
      return false; 
    Object this$parent = getParent(), other$parent = other.getParent();
    if ((this$parent == null) ? (other$parent != null) : !this$parent.equals(other$parent))
      return false; 
    Object this$setting = getSetting(), other$setting = other.getSetting();
    if ((this$setting == null) ? (other$setting != null) : !this$setting.equals(other$setting))
      return false; 
    Object this$elementTitle = getElementTitle(), other$elementTitle = other.getElementTitle();
    return !((this$elementTitle == null) ? (other$elementTitle != null) : !this$elementTitle.equals(other$elementTitle));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Element;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    long $offset = Double.doubleToLongBits(getOffset());
    result = result * 59 + (int)($offset >>> 32L ^ $offset);
    long $x = Double.doubleToLongBits(getX());
    result = result * 59 + (int)($x >>> 32L ^ $x);
    long $y = Double.doubleToLongBits(getY());
    result = result * 59 + (int)($y >>> 32L ^ $y);
    long $width = Double.doubleToLongBits(getWidth());
    result = result * 59 + (int)($width >>> 32L ^ $width);
    long $height = Double.doubleToLongBits(getHeight());
    result = result * 59 + (int)($height >>> 32L ^ $height);
    long $onebox = Double.doubleToLongBits(getOnebox());
    result = result * 59 + (int)($onebox >>> 32L ^ $onebox);
    result = result * 59 + (isComboextended() ? 79 : 97);
    Object $clickgui = getClickgui();
    result = result * 59 + (($clickgui == null) ? 43 : $clickgui.hashCode());
    Object $parent = getParent();
    result = result * 59 + (($parent == null) ? 43 : $parent.hashCode());
    Object $setting = getSetting();
    result = result * 59 + (($setting == null) ? 43 : $setting.hashCode());
    Object $elementTitle = getElementTitle();
    return result * 59 + (($elementTitle == null) ? 43 : $elementTitle.hashCode());
  }
  
  public String toString() {
    return "Element(clickgui=" + getClickgui() + ", parent=" + getParent() + ", setting=" + getSetting() + ", offset=" + getOffset() + ", x=" + getX() + ", y=" + getY() + ", width=" + getWidth() + ", height=" + getHeight() + ", onebox=" + getOnebox() + ", elementTitle=" + getElementTitle() + ", comboextended=" + isComboextended() + ")";
  }
  
  public ClickGUIRenderer getClickgui() {
    return this.clickgui;
  }
  
  public ModuleButton getParent() {
    return this.parent;
  }
  
  public Setting getSetting() {
    return this.setting;
  }
  
  public double getOffset() {
    return this.offset;
  }
  
  public double getX() {
    return this.x;
  }
  
  public double getY() {
    return this.y;
  }
  
  public double getWidth() {
    return this.width;
  }
  
  public double getHeight() {
    return this.height;
  }
  
  public double getOnebox() {
    return this.onebox;
  }
  
  public String getElementTitle() {
    return this.elementTitle;
  }
  
  public boolean isComboextended() {
    return this.comboextended;
  }
  
  public void setup() {
    this.clickgui = this.parent.parent.clickgui;
  }
  
  public void update() {
    this.x = this.parent.x + this.parent.width + 10.0D;
    this.y = this.parent.y + this.offset;
    this.width = this.parent.width + 60.0D;
    this.onebox = 18.0D;
    this.height = this.onebox;
    String name = this.setting.getName();
    String str = name.substring(0, 1).toUpperCase() + name.substring(1);
    if (this.setting.isCheck()) {
      this.elementTitle = str;
      double textx = this.x + this.width - FontUtils.getStringWidth(this.elementTitle);
      if (textx < this.x + 13.0D)
        this.width += this.x + 13.0D - textx + 1.0D; 
    } else if (this.setting.isCombo()) {
      this.height = this.comboextended ? (this.setting.getOptions().size() * (FontUtils.getFontHeight() + 2) + 15) : 15.0D;
      this.elementTitle = str;
      int longest = FontUtils.getStringWidth(this.elementTitle);
      for (String s : this.setting.getOptions()) {
        int temp = FontUtils.getStringWidth(s);
        if (temp > longest)
          longest = temp; 
      } 
      double textx = this.x + this.width - longest;
      if (textx < this.x)
        this.width += this.x - textx + 1.0D; 
    } else if (this.setting.isSlider()) {
      this.elementTitle = str;
      String displaymax = "" + (Math.round(this.setting.getMax() * 100.0D) / 100.0D);
      double textx = this.x + this.width - FontUtils.getStringWidth(this.elementTitle) - FontUtils.getStringWidth(displaymax) - 4.0D;
      if (textx < this.x)
        this.width += this.x - textx + 1.0D; 
    } else if (this.setting.isText()) {
      this.elementTitle = str;
      double textx = this.x + this.width - FontUtils.getStringWidth(this.elementTitle);
      if (textx < this.x)
        this.width += this.x - textx + 1.0D; 
    } else if (this.setting.isColor()) {
      this.elementTitle = str;
      double textx = this.x + this.width - FontUtils.getStringWidth(this.elementTitle);
      if (textx < this.x)
        this.width += this.x - textx + 1.0D; 
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
  
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    return isHovered(mouseX, mouseY);
  }
  
  public void mouseReleased(int mouseX, int mouseY, int state) {}
  
  public void keyTyped(char typedChar, int keyCode) {}
  
  public boolean isHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height);
  }
}
