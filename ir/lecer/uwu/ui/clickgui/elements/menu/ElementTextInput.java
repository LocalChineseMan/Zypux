package ir.lecer.uwu.ui.clickgui.elements.menu;

import com.google.common.collect.Lists;
import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.ui.clickgui.ClickGUIRenderer;
import ir.lecer.uwu.ui.clickgui.elements.Element;
import ir.lecer.uwu.ui.clickgui.elements.ModuleButton;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Optional;

public class ElementTextInput extends Element {
  public void setFocused(boolean focused) {
    this.focused = focused;
  }
  
  private final ArrayList<Character> safeChars = Lists.newArrayList((Object[])new Character[] { 
        Character.valueOf('a'), Character.valueOf('b'), Character.valueOf('c'), Character.valueOf('d'), Character.valueOf('e'), Character.valueOf('f'), Character.valueOf('g'), Character.valueOf('h'), Character.valueOf('i'), Character.valueOf('j'), 
        Character.valueOf('k'), Character.valueOf('l'), Character.valueOf('m'), Character.valueOf('n'), Character.valueOf('o'), Character.valueOf('p'), Character.valueOf('q'), Character.valueOf('r'), Character.valueOf('s'), Character.valueOf('t'), 
        Character.valueOf('u'), Character.valueOf('v'), Character.valueOf('w'), Character.valueOf('x'), Character.valueOf('y'), Character.valueOf('z'), 
        Character.valueOf('A'), Character.valueOf('B'), Character.valueOf('C'), Character.valueOf('D'), 
        Character.valueOf('E'), Character.valueOf('F'), Character.valueOf('G'), Character.valueOf('H'), Character.valueOf('I'), Character.valueOf('J'), Character.valueOf('K'), Character.valueOf('L'), Character.valueOf('M'), Character.valueOf('N'), 
        Character.valueOf('O'), Character.valueOf('P'), Character.valueOf('Q'), Character.valueOf('R'), Character.valueOf('S'), Character.valueOf('T'), Character.valueOf('U'), Character.valueOf('V'), Character.valueOf('W'), Character.valueOf('X'), 
        Character.valueOf('Y'), Character.valueOf('Z'), 
        Character.valueOf('0'), Character.valueOf('1'), Character.valueOf('2'), Character.valueOf('3'), Character.valueOf('4'), Character.valueOf('5'), Character.valueOf('6'), Character.valueOf('7'), 
        Character.valueOf('8'), Character.valueOf('9'), Character.valueOf(' '), 
        Character.valueOf('`'), Character.valueOf('!'), Character.valueOf('@'), Character.valueOf('#'), Character.valueOf('$'), Character.valueOf('%'), Character.valueOf('^'), 
        Character.valueOf('&'), Character.valueOf('*'), Character.valueOf('('), Character.valueOf(')'), Character.valueOf('-'), Character.valueOf('_'), Character.valueOf('='), Character.valueOf('+'), Character.valueOf('|'), Character.valueOf(';'), 
        Character.valueOf(':'), Character.valueOf('"'), Character.valueOf(','), Character.valueOf('.'), Character.valueOf('/') });
  
  private boolean focused;
  
  public ArrayList<Character> getSafeChars() {
    return this.safeChars;
  }
  
  public boolean isFocused() {
    return this.focused;
  }
  
  public ElementTextInput(ModuleButton iparent, Setting iset) {
    this.parent = iparent;
    this.setting = iset;
    setup();
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    FontUtils.raleway.drawString(this.elementTitle, this.x + 3.0D, this.y + (FontUtils.getFontHeight() / 2.0F) - 1.0D, (new Color(-1)).getRGB());
    RenderUtils.rect(this.x + FontUtils.raleway.getStringWidth(this.elementTitle) + 6.0D, this.y + 2.0D, this.x + this.width - 2.0D, this.y + this.height - 2.0D, (new Color(-1291845632, true)).getRGB());
    FontUtils.jetbrains_mono.drawString(this.setting.getStringValue(), this.x + FontUtils.raleway.getStringWidth(this.elementTitle) + 8.0D, this.y + 3.0D, (new Color(16777215)).getRGB());
    if (isFocused())
      FontUtils.jetbrains_mono.drawString(ClickGUIRenderer.underlineChar, this.x + FontUtils.raleway.getStringWidth(this.elementTitle) + 8.0D + FontUtils.jetbrains_mono.getStringWidth(this.setting.getStringValue()), this.y + 3.0D, (new Color(16777215))
          .getRGB()); 
  }
  
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (isHovered(mouseX, mouseY)) {
      setFocused(true);
      return true;
    } 
    setFocused(false);
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  public void keyTyped(char typedChar, int keyCode) {
    if (isFocused()) {
      if (keyCode == 14) {
        this.setting.setStringValue(removeLastCharOptional(this.setting.getStringValue()));
        return;
      } 
      if (this.x + FontUtils.raleway.getStringWidth(this.elementTitle) + 13.0D + FontUtils.jetbrains_mono.getStringWidth(this.setting.getStringValue()) >= this.x + this.width - 2.0D && this.setting.getStringValue().length() >= this.setting.getMax())
        return; 
      for (Character safeChar : this.safeChars) {
        if (safeChar.equals(Character.valueOf(typedChar))) {
          if (this.setting.isSpacebar() && safeChar.equals(Character.valueOf(' '))) {
            this.setting.setStringValue(this.setting.getStringValue() + typedChar);
            continue;
          } 
          this.setting.setStringValue(this.setting.getStringValue() + typedChar);
        } 
      } 
    } 
    super.keyTyped(typedChar, keyCode);
  }
  
  private String removeLastCharOptional(String s) {
    return Optional.<String>ofNullable(s)
      .filter(str -> (str.length() != 0))
      .map(str -> str.substring(0, str.length() - 1))
      .orElse(s);
  }
  
  public boolean isHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x + FontUtils.raleway.getStringWidth(this.elementTitle) + 8.0D && mouseX <= this.x + this.width - 2.0D && mouseY >= this.y + 2.0D && mouseY <= this.y + this.height - 2.0D);
  }
}
