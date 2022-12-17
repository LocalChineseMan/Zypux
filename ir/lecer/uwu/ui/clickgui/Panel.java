package ir.lecer.uwu.ui.clickgui;

import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.ColorUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.ui.clickgui.elements.ModuleButton;
import java.awt.Color;
import java.util.ArrayList;

public class Panel {
  private final Enum<Category> category;
  
  public String title;
  
  public double x;
  
  public double y;
  
  private double x2;
  
  private double y2;
  
  public double width;
  
  public double height;
  
  public boolean dragging;
  
  public boolean extended;
  
  public boolean visible;
  
  public Enum<Category> getCategory() {
    return this.category;
  }
  
  public ArrayList<ModuleButton> Elements = new ArrayList<>();
  
  public ClickGUIRenderer clickgui;
  
  public Panel(String ititle, Enum<Category> category, double ix, double iy, double iwidth, double iheight, boolean iextended, ClickGUIRenderer parent) {
    this.title = ititle;
    this.category = category;
    this.x = ix;
    this.y = iy;
    this.width = iwidth;
    this.height = iheight;
    this.extended = iextended;
    this.dragging = false;
    this.visible = true;
    this.clickgui = parent;
  }
  
  public void drawScreen(int mouseX, int mouseY) {
    if (!this.visible)
      return; 
    if (this.dragging) {
      this.x = this.x2 + mouseX;
      this.y = this.y2 + mouseY;
    } 
    RenderUtils.rect((int)(this.x - 1.0D), (int)(this.y - 1.0D), (int)this.x, (int)(this.y + this.height), ColorUtils.getClickGUIBorderColor);
    RenderUtils.rect((int)this.x, (int)(this.y - 1.0D), (int)(this.x + this.width + 1.0D), (int)this.y, ColorUtils.getClickGUIBorderColor);
    RenderUtils.rect((int)(this.x + this.width), (int)this.y, (int)(this.x + this.width + 1.0D), (int)(this.y + this.height), ColorUtils.getClickGUIBorderColor);
    if (!this.extended) {
      RenderUtils.drawShadow((int)this.x, (int)this.y, (int)this.width, (int)this.height, 9, true);
      RenderUtils.rect((int)(this.x - 1.0D), (int)(this.y + this.height), (int)(this.x + this.width + 1.0D), (int)(this.y + this.height + 1.0D), ColorUtils.getClickGUIBorderColor);
    } 
    RenderUtils.rect((int)this.x, (int)this.y, (int)(this.x + this.width), (int)(this.y + this.height), (new Color(-1090519040, true)).getRGB());
    FontUtils.raleway.drawStringWithShadow(this.title, this.x + 3.0D, this.y + this.height / 2.0D - 2.0D - (FontUtils.getFontHeight() / 2.0F) + 1.0D, (new Color(-1, true)).getRGB());
    if (this.extended && !this.Elements.isEmpty()) {
      double startY = this.y + this.height;
      double TotalHeight = 0.0D;
      for (ModuleButton element : this.Elements) {
        RenderUtils.rect((int)this.x, (int)startY, (int)(this.x + this.width), (int)(startY + element.height + 1.0D), (new Color(1493172224, true)).getRGB());
        RenderUtils.rect((int)(this.x - 1.0D), startY, (int)this.x, (int)(startY + element.height + 1.0D), ColorUtils.getClickGUIBorderColor);
        RenderUtils.rect((int)(this.x + this.width), startY, (int)(this.x + this.width + 1.0D), (int)(startY + element.height + 1.0D), ColorUtils.getClickGUIBorderColor);
        element.x = this.x + 2.0D;
        element.y = startY;
        element.width = this.width - 4.0D;
        element.drawScreen(mouseX, mouseY);
        startY += element.height + 1.0D;
        TotalHeight += element.height;
      } 
      RenderUtils.rect((int)(this.x - 1.0D), startY, (int)(this.x + this.width + 1.0D), (int)(startY + 1.0D), ColorUtils.getClickGUIBorderColor);
      RenderUtils.drawShadow((int)this.x, (int)this.y, (int)this.width, (int)(this.height + TotalHeight + 4.0D), 9, true);
    } 
  }
  
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (!this.visible)
      return false; 
    if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
      this.x2 = this.x - mouseX;
      this.y2 = this.y - mouseY;
      this.dragging = true;
      return true;
    } 
    if (mouseButton == 1 && isHovered(mouseX, mouseY)) {
      this.extended = !this.extended;
      return true;
    } 
    if (this.extended)
      for (ModuleButton element : this.Elements) {
        if (element.mouseClicked(mouseX, mouseY, mouseButton))
          return true; 
      }  
    return false;
  }
  
  public void mouseReleased(int state) {
    if (!this.visible)
      return; 
    if (state == 0)
      this.dragging = false; 
  }
  
  public boolean isHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height);
  }
}
