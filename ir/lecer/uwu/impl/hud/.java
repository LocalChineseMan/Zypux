package ir.lecer.uwu.impl.hud;

import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.enums.Shadows;
import ir.lecer.uwu.events.GameEvent;
import ir.lecer.uwu.events.events.Render2DEvent;
import ir.lecer.uwu.events.handler.EventHandler;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.ColorUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import java.awt.Color;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;

public class Hotbar extends Module {
  public static boolean enabled;
  
  public static boolean isEnabled() {
    return enabled;
  }
  
  public static void setEnabled(boolean enabled) {
    Hotbar.enabled = enabled;
  }
  
  public Hotbar() {
    super("Hotbar", Category.HUD, true);
  }
  
  public void onEnable() {
    setEnabled(true);
    super.onEnable();
  }
  
  public void onDisable() {
    super.onDisable();
    setEnabled(false);
  }
  
  @EventHandler
  public void onRender2D(Render2DEvent event) {
    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
    ScaledResolution sr = new ScaledResolution(this.mc);
    EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
    String rightSideText = ColorUtils.colorize(new String[] { "&f" + formatter.format(Instant.now()) });
    RenderUtils.rect(0.0F, (sr.getScaledHeight() - 22), sr.getScaledWidth(), sr.getScaledHeight(), (new Color(1711276032, true)).getRGB());
    RenderUtils.rect((sr.getScaledWidth() - 6), (sr.getScaledHeight() - 22), sr.getScaledWidth(), sr.getScaledHeight(), (new Color(1711276032, true)).getRGB());
    RenderUtils.rect(sr.getScaledWidth() / 2.0F - 91.0F - 1.0F + (entityplayer.inventory.currentItem * 20) + 5.0F, (sr.getScaledHeight() - 1), sr
        .getScaledWidth() / 2.0F - 91.0F - 1.0F + (entityplayer.inventory.currentItem * 20) + 19.0F, sr.getScaledHeight(), ColorUtils.getClickGUIBorderColor);
    RenderUtils.drawTexturedRect(0.0F, (sr.getScaledHeight() - 31), sr.getScaledWidth(), 9.0F, Shadows.PANEL_TOP, false);
    FontUtils.jetbrains_mono.drawString(ColorUtils.colorize(new String[] { "&fBps. " + GameEvent.getRoundedBPS() }, ), 20.0D, sr.getScaledHeight() - 18.5D, 0);
    FontUtils.jetbrains_mono.drawString(rightSideText, (sr.getScaledWidth() - FontUtils.getStringWidth(rightSideText) - 20), sr.getScaledHeight() - 18.5D, 0);
  }
}
