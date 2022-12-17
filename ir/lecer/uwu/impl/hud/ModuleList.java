package ir.lecer.uwu.impl.hud;

import com.google.common.collect.Lists;
import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.enums.Shadows;
import ir.lecer.uwu.events.events.Render2DEvent;
import ir.lecer.uwu.events.handler.EventHandler;
import ir.lecer.uwu.impl.render.Blur;
import ir.lecer.uwu.interfaces.IModuleList;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.tools.shader.BlurUtils;
import ir.lecer.uwu.ui.clickgui.SettingsManager;
import ir.lecer.uwu.ui.editor.ClientSettings;
import ir.lecer.uwu.ui.editor.HUDEditor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class ModuleList extends Module {
  private final ArrayList<String> fontMode = Lists.newArrayList((Object[])new String[] { "Minecraft", "Raleway", "Comic", "JetBrainsMono" });
  
  private FontRenderer currentFont;
  
  public ModuleList() {
    super("Module List", Category.HUD, true);
  }
  
  public void onSetup() {
    SettingsManager settingsManager = (Zypux.getInstance()).settingsManager;
    settingsManager.addSetting(new Setting("Show Renders", this, true));
    settingsManager.addSetting(new Setting("Glow Shadow", this, true));
    settingsManager.addSetting(new Setting("Fonts", this, "Minecraft", this.fontMode));
    settingsManager.addSetting(new Setting("Background Color", this, new Color(1291864959, true), true));
  }
  
  @EventHandler
  public void onRender2DEvent(Render2DEvent event) {
    boolean renders = (Zypux.getInstance()).settingsManager.getSettingByName("Show Renders").isBooleanValue();
    boolean glow = (Zypux.getInstance()).settingsManager.getSettingByName("Glow Shadow").isBooleanValue();
    String fontM = (Zypux.getInstance()).settingsManager.getSettingByName("Fonts").getStringValue().toLowerCase();
    Color bgc = (Zypux.getInstance()).settingsManager.getSettingByName("Background Color").getColor();
    switch (fontM) {
      case "minecraft":
        this.currentFont = FontUtils.minecraft;
        break;
      case "raleway":
        this.currentFont = (FontRenderer)FontUtils.raleway;
        break;
      case "comic":
        this.currentFont = (FontRenderer)FontUtils.comic;
        break;
      case "jetbrainsmono":
        this.currentFont = (FontRenderer)FontUtils.jetbrains_mono;
        break;
    } 
    ArrayList<String> strings = new ArrayList<>();
    for (IModuleList iModuleList : modulesArray) {
      if (renders) {
        strings.add(iModuleList.getTitle());
        continue;
      } 
      if (!iModuleList.getCategory().equals(Category.RENDER) && !iModuleList.getCategory().equals(Category.HUD))
        strings.add(iModuleList.getTitle()); 
    } 
    strings.sort(Comparator.comparing(this.currentFont::getStringWidth));
    Collections.reverse(strings);
    ScaledResolution resolution = new ScaledResolution(this.mc);
    int SheightPlus = 0;
    int plusLine = 4;
    boolean isNew = false;
    int oldX = 0;
    int oldX2 = 0;
    int lastX = 0;
    int lastX2 = 0;
    int lastY = 0;
    if (((ClientSettings)HUDEditor.clientSettings.get(1)).isToggle()) {
      int firstStringWidth = this.currentFont.getStringWidth(strings.get(0));
      RenderUtils.drawTexturedRect((resolution.getScaledWidth() - firstStringWidth - 8), -1.0F, (firstStringWidth + 4), 5.0F, Shadows.PANEL_TOP, glow);
      RenderUtils.drawTexturedRect((resolution.getScaledWidth() - firstStringWidth - 13), -1.0F, 5.0F, 5.0F, Shadows.PANEL_TOP_LEFT, glow);
      RenderUtils.drawTexturedRect((resolution.getScaledWidth() - 4), -1.0F, 5.0F, 5.0F, Shadows.PANEL_TOP_RIGHT, glow);
    } 
    for (String string : strings) {
      if (((ClientSettings)HUDEditor.clientSettings.get(1)).isToggle()) {
        RenderUtils.drawTexturedRect((resolution.getScaledWidth() - this.currentFont.getStringWidth(string) - 9 - plusLine), (SheightPlus + 4), 5.0F, 12.0F, Shadows.PANEL_LEFT, glow);
        RenderUtils.drawTexturedRect((resolution.getScaledWidth() - 4), (SheightPlus + 4), 5.0F, 12.0F, Shadows.PANEL_RIGHT, glow);
        if (isNew) {
          RenderUtils.drawTexturedRect(oldX2, (SheightPlus + 4), (oldX - this.currentFont
              .getStringWidth(string)), 5.0F, Shadows.PANEL_BOTTOM, glow);
          RenderUtils.drawTexturedRect((oldX2 - 5), (SheightPlus + 4), 5.0F, 5.0F, Shadows.PANEL_BOTTOM_LEFT, glow);
        } 
      } 
      if (Blur.isEnabled())
        BlurUtils.INSTANCE.draw((resolution.getScaledWidth() - this.currentFont.getStringWidth(string) - 4 - plusLine), (SheightPlus + 4), (plusLine + this.currentFont
            .getStringWidth(string)), 12.0F, 30.0F); 
      RenderUtils.rect((resolution.getScaledWidth() - this.currentFont.getStringWidth(string) - 4 - plusLine), (SheightPlus + 4), (resolution.getScaledWidth() - plusLine), (SheightPlus + 12 + 4), bgc.getRGB());
      if (this.currentFont == FontUtils.minecraft) {
        this.currentFont.drawStringWithShadow(string, (resolution.getScaledWidth() - this.currentFont.getStringWidth(string)) - 1.5F - plusLine, (SheightPlus + 2 + 4), (new Color(16777215)).getRGB());
      } else if (this.currentFont == FontUtils.comic || this.currentFont == FontUtils.jetbrains_mono) {
        this.currentFont.drawStringWithShadow(string, (resolution.getScaledWidth() - this.currentFont.getStringWidth(string)) - 1.5F - plusLine, (SheightPlus + 3), (new Color(16777215)).getRGB());
      } else {
        this.currentFont.drawStringWithShadow(string, (resolution.getScaledWidth() - this.currentFont.getStringWidth(string)) - 1.5F - plusLine, (SheightPlus + 4), (new Color(16777215)).getRGB());
      } 
      if (!isNew)
        isNew = true; 
      oldX = this.currentFont.getStringWidth(string);
      oldX2 = resolution.getScaledWidth() - this.currentFont.getStringWidth(string) - 4 - plusLine;
      SheightPlus += 12;
      lastX = this.currentFont.getStringWidth(string) - 4 - plusLine;
      lastX2 = resolution.getScaledWidth() - this.currentFont.getStringWidth(string) - 4 - plusLine;
      lastY = SheightPlus + 4;
    } 
    if (((ClientSettings)HUDEditor.clientSettings.get(1)).isToggle()) {
      RenderUtils.drawTexturedRect(lastX2, lastY, (lastX + 12), 5.0F, Shadows.PANEL_BOTTOM, glow);
      RenderUtils.drawTexturedRect((lastX2 - 5), lastY, 5.0F, 5.0F, Shadows.PANEL_BOTTOM_LEFT, glow);
      RenderUtils.drawTexturedRect((resolution.getScaledWidth() - 4), lastY, 5.0F, 5.0F, Shadows.PANEL_BOTTOM_RIGHT, glow);
    } 
  }
}
