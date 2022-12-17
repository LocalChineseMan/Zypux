package ir.lecer.uwu.features.notifications;

import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;

public class Notification {
  private final String title;
  
  private final String messsage;
  
  private final Color lineColor;
  
  private final Color backgroundColor;
  
  private final long fadedIn;
  
  private final long fadeOut;
  
  private final long end;
  
  private long start;
  
  public Notification(String title, String messsage, Color lineColor, Color backgroundColor, double length) {
    this.title = title;
    this.messsage = messsage;
    this.lineColor = lineColor;
    this.backgroundColor = backgroundColor;
    this.fadedIn = (long)(500.0D * length);
    this.fadeOut = (long)(this.fadedIn + 2500.0D * length);
    this.end = this.fadeOut + this.fadedIn;
  }
  
  public void start() {
    this.start = System.currentTimeMillis();
  }
  
  public boolean isRunning() {
    return (getTime() <= this.end);
  }
  
  private long getTime() {
    return System.currentTimeMillis() - this.start;
  }
  
  public void render() {
    double offset;
    int fontSize = Math.max(FontUtils.jetbrains_mono.getStringWidth(this.messsage), FontUtils.jetbrains_mono.getStringWidth(this.title));
    int width = fontSize + 10;
    int height = 24;
    long time = getTime();
    if (time < this.fadedIn) {
      offset = Math.tanh(time / this.fadedIn * 3.0D) * width;
    } else if (time > this.fadeOut) {
      offset = Math.tanh(3.0D - (time - this.fadeOut) / (this.end - this.fadeOut) * 3.0D) * width;
    } else {
      offset = width;
    } 
    offset += 7.0D;
    int addition = NotificationHelper.getRunningNotficiations().indexOf(this) * 30 + 25;
    int y = GuiScreen.height - 5 + addition - height;
    float progress = (float)(System.currentTimeMillis() - this.start) / (float)this.end * height / 2.0F;
    RenderUtils.drawShadow((float)(GuiScreen.width - offset), y, width, height, 9, false);
    RenderUtils.rect(GuiScreen.width - offset, y, (GuiScreen.width + width) - offset, (y + height), (new Color(this.backgroundColor.getRed(), this.backgroundColor.getGreen(), this.backgroundColor.getBlue(), 110)).getRGB());
    FontUtils.jetbrains_mono.drawStringWithShadow(this.title, (int)(GuiScreen.width - offset + 5.0D), (GuiScreen.height - addition + 2 - height - 3), Color.WHITE);
    FontUtils.jetbrains_mono.drawStringWithShadow(this.messsage, (int)(GuiScreen.width - offset + 5.0D), (GuiScreen.height - addition + 15 - 3), new Color(-1842205, true));
    RenderUtils.rect(GuiScreen.width - offset, (y + height / 2.0F - progress), GuiScreen.width - offset + 1.0D, (y + height / 2.0F), (new Color(this.lineColor.getRed(), this.lineColor.getGreen(), this.lineColor.getBlue(), 180)).getRGB());
    RenderUtils.rect(GuiScreen.width - offset, (y + height / 2.0F), GuiScreen.width - offset + 1.0D, (y + height / 2.0F + progress), (new Color(this.lineColor.getRed(), this.lineColor.getGreen(), this.lineColor.getBlue(), 180)).getRGB());
  }
}
