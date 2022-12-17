package ir.lecer.uwu.ui.editor;

import ir.lecer.uwu.enums.Settings;
import ir.lecer.uwu.impl.hud.Keystroke;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.ColorUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class HUDEditor extends GuiScreen {
  private final ResourceLocation tick = new ResourceLocation("zypux/textures/clickgui/tick.png");
  
  public static final ArrayList<ClientSettings> clientSettings = new ArrayList<>();
  
  public static int ScoreboardX = 400;
  
  public static int ScoreboardY = 400;
  
  public static boolean ScoreboardD;
  
  public static int KeystrokeX = 250;
  
  public static int KeystrokeY = 250;
  
  public static boolean KeystrokeD;
  
  public HUDEditor() {
    int Sx = 20;
    int Sy = 20;
    int Swidth = 250;
    int Sheight = 20;
    double SyPlus = 22.0D;
    for (Settings setting : Settings.values()) {
      clientSettings.add(new ClientSettings(setting.getName(), setting.getText(), Sx, Sy, 250, 20, setting.isToggle()));
      Sy = (int)(Sy + 22.0D);
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    RenderUtils.rect(17.0F, 17.0F, 273.0F, 18.0F, ColorUtils.getClickGUIBorderColor);
    RenderUtils.rect(272.0F, 18.0F, 273.0F, 64.0F, ColorUtils.getClickGUIBorderColor);
    RenderUtils.rect(17.0F, 17.0F, 18.0F, 64.0F, ColorUtils.getClickGUIBorderColor);
    RenderUtils.rect(17.0F, 64.0F, 273.0F, 65.0F, ColorUtils.getClickGUIBorderColor);
    RenderUtils.rect(18.0F, 18.0F, 272.0F, 64.0F, (new Color(31, 31, 31, 102)).getRGB());
    for (ClientSettings clientSetting : clientSettings) {
      RenderUtils.rect(clientSetting.getX(), clientSetting.getY(), (clientSetting.getX() + clientSetting.getWidth()), (clientSetting.getY() + clientSetting.getHeight()), (new Color(0, 0, 0, 128))
          .getRGB());
      if (clientSetting.isToggle())
        RenderUtils.image(this.tick, clientSetting.getX() + 3, clientSetting.getY() + 2, 16, 16, 255.0F); 
      int color = clientSetting.isHovered(mouseX, mouseY) ? (new Color(9079434)).getRGB() : (new Color(16777215)).getRGB();
      FontUtils.comic.drawStringWithShadow(String.format("(%s) %s", new Object[] { clientSetting.getName(), clientSetting.getText() }), (clientSetting.getX() + 24), (clientSetting.getY() + 3), color);
    } 
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if (mouseButton != 0)
      return; 
    if (isHoveredNSFW(mouseX, mouseY))
      ((ClientSettings)clientSettings.get(0)).setToggle(!((ClientSettings)clientSettings.get(0)).isToggle()); 
    if (isHoveredShadow(mouseX, mouseY))
      ((ClientSettings)clientSettings.get(1)).setToggle(!((ClientSettings)clientSettings.get(1)).isToggle()); 
    if (GuiIngame.isHoveredScoreboard(mouseX, mouseY))
      ScoreboardD = true; 
    if (Keystroke.isHovered(mouseX, mouseY))
      KeystrokeD = true; 
  }
  
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    ScoreboardD = false;
    KeystrokeD = false;
    super.mouseReleased(mouseX, mouseY, state);
  }
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public boolean isHoveredNSFW(int mouseX, int mouseY) {
    return (mouseX >= 20 && mouseY >= 20 && mouseX <= 270 && mouseY <= 40);
  }
  
  public boolean isHoveredShadow(int mouseX, int mouseY) {
    return (mouseX >= 20 && mouseY >= 42 && mouseX <= 270 && mouseY <= 62);
  }
}
