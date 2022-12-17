package ir.lecer.uwu.ui.clickgui.elements;

import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.ChatLevels;
import ir.lecer.uwu.features.ChatManager;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.ui.clickgui.Panel;
import ir.lecer.uwu.ui.clickgui.elements.menu.ElementCheckBox;
import ir.lecer.uwu.ui.clickgui.elements.menu.ElementColor;
import ir.lecer.uwu.ui.clickgui.elements.menu.ElementComboBox;
import ir.lecer.uwu.ui.clickgui.elements.menu.ElementSlider;
import ir.lecer.uwu.ui.clickgui.elements.menu.ElementTextInput;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class ModuleButton {
  public Module module;
  
  public void setModule(Module module) {
    this.module = module;
  }
  
  public void setMenuElements(ArrayList<Element> menuElements) {
    this.menuElements = menuElements;
  }
  
  public void setParent(Panel parent) {
    this.parent = parent;
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
  
  public void setExtended(boolean extended) {
    this.extended = extended;
  }
  
  public void setListening(boolean listening) {
    this.listening = listening;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ModuleButton))
      return false; 
    ModuleButton other = (ModuleButton)o;
    if (!other.canEqual(this))
      return false; 
    if (Double.compare(getX(), other.getX()) != 0)
      return false; 
    if (Double.compare(getY(), other.getY()) != 0)
      return false; 
    if (Double.compare(getWidth(), other.getWidth()) != 0)
      return false; 
    if (Double.compare(getHeight(), other.getHeight()) != 0)
      return false; 
    if (isExtended() != other.isExtended())
      return false; 
    if (isListening() != other.isListening())
      return false; 
    Object this$module = getModule(), other$module = other.getModule();
    if ((this$module == null) ? (other$module != null) : !this$module.equals(other$module))
      return false; 
    Object<Element> this$menuElements = (Object<Element>)getMenuElements(), other$menuElements = (Object<Element>)other.getMenuElements();
    if ((this$menuElements == null) ? (other$menuElements != null) : !this$menuElements.equals(other$menuElements))
      return false; 
    Object this$parent = getParent(), other$parent = other.getParent();
    return !((this$parent == null) ? (other$parent != null) : !this$parent.equals(other$parent));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ModuleButton;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    long $x = Double.doubleToLongBits(getX());
    result = result * 59 + (int)($x >>> 32L ^ $x);
    long $y = Double.doubleToLongBits(getY());
    result = result * 59 + (int)($y >>> 32L ^ $y);
    long $width = Double.doubleToLongBits(getWidth());
    result = result * 59 + (int)($width >>> 32L ^ $width);
    long $height = Double.doubleToLongBits(getHeight());
    result = result * 59 + (int)($height >>> 32L ^ $height);
    result = result * 59 + (isExtended() ? 79 : 97);
    result = result * 59 + (isListening() ? 79 : 97);
    Object $module = getModule();
    result = result * 59 + (($module == null) ? 43 : $module.hashCode());
    Object<Element> $menuElements = (Object<Element>)getMenuElements();
    result = result * 59 + (($menuElements == null) ? 43 : $menuElements.hashCode());
    Object $parent = getParent();
    return result * 59 + (($parent == null) ? 43 : $parent.hashCode());
  }
  
  public String toString() {
    return "ModuleButton(module=" + getModule() + ", menuElements=" + getMenuElements() + ", parent=" + getParent() + ", x=" + getX() + ", y=" + getY() + ", width=" + getWidth() + ", height=" + getHeight() + ", extended=" + isExtended() + ", listening=" + isListening() + ")";
  }
  
  public Module getModule() {
    return this.module;
  }
  
  public ArrayList<Element> menuElements = new ArrayList<>();
  
  public Panel parent;
  
  public double x;
  
  public double y;
  
  public double width;
  
  public double height;
  
  public ArrayList<Element> getMenuElements() {
    return this.menuElements;
  }
  
  public Panel getParent() {
    return this.parent;
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
  
  public boolean extended = false;
  
  public boolean isExtended() {
    return this.extended;
  }
  
  public boolean listening = false;
  
  public boolean isListening() {
    return this.listening;
  }
  
  public ModuleButton(Module imod, Panel pl) {
    this.module = imod;
    this.parent = pl;
    this.height = ((Minecraft.getMinecraft()).fontRendererObj.FONT_HEIGHT + 2);
    if ((Zypux.getInstance()).settingsManager.getSettingsByMod(imod) != null)
      for (Setting setting : (Zypux.getInstance()).settingsManager.getSettingsByMod(imod)) {
        if (setting.isCheck()) {
          this.menuElements.add(new ElementCheckBox(this, setting));
          continue;
        } 
        if (setting.isSlider()) {
          this.menuElements.add(new ElementSlider(this, setting));
          continue;
        } 
        if (setting.isCombo()) {
          this.menuElements.add(new ElementComboBox(this, setting));
          continue;
        } 
        if (setting.isText()) {
          this.menuElements.add(new ElementTextInput(this, setting));
          continue;
        } 
        if (setting.isColor())
          this.menuElements.add(new ElementColor(this, setting)); 
      }  
  }
  
  public static void setup() {}
  
  public void drawScreen(int mouseX, int mouseY) {
    int textcolor = -5263441;
    String ex = this.extended ? "-" : "+";
    if (this.module.isToggled())
      textcolor = -1052689; 
    if (isHovered(mouseX, mouseY))
      RenderUtils.rect((int)(this.x - 1.0D), (int)this.y, (int)(this.x + this.width + 2.0D), (int)(this.y + this.height + 1.0D), (new Color(1073741824, true)).getRGB()); 
    FontUtils.raleway.drawStringWithShadow(this.module.getName(), this.x + 1.0D, this.y + this.height / 2.0D - 2.0D - (FontUtils.getFontHeight() / 2.0F) + 1.0D, textcolor);
    FontUtils.raleway.drawStringWithShadow(ex, this.x + this.width - 5.0D, this.y + this.height / 2.0D - 2.0D - (FontUtils.getFontHeight() / 2.0F) + 1.0D, textcolor);
  }
  
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (!isHovered(mouseX, mouseY))
      return false; 
    if (mouseButton == 0) {
      this.module.toggle();
    } else if (mouseButton == 1) {
      if (this.menuElements != null && this.menuElements.size() > 0)
        this.extended = !this.extended; 
    } else if (mouseButton == 2) {
      this.listening = true;
    } 
    return true;
  }
  
  public boolean keyTyped(int keyCode) throws IOException {
    if (this.listening) {
      if (keyCode != 1) {
        ChatManager.send(String.format("Bound &8%s&7 on &8%s&7.", new Object[] { this.module.getName(), Keyboard.getKeyName(keyCode) }), (Enum)ChatLevels.BIND);
        this.module.setKey(keyCode);
      } else {
        ChatManager.send(String.format("Unbound &8%s&7.", new Object[] { this.module.getName() }), (Enum)ChatLevels.BIND);
        this.module.setKey(0);
      } 
      this.listening = false;
      return true;
    } 
    return false;
  }
  
  public boolean isHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height);
  }
}
