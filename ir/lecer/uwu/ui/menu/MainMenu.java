package ir.lecer.uwu.ui.menu;

import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.interfaces.ChangeLog;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.ColorUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.tools.tasks.TaskManager;
import ir.lecer.uwu.tools.utilities.TimerUtils;
import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class MainMenu {
  private MainMenu() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  private static final ResourceLocation mmBackground = new ResourceLocation("zypux/textures/menu/main/background.png");
  
  public static final ResourceLocation singleplayerIcon = new ResourceLocation("zypux/textures/menu/main/singleplayer.png");
  
  public static final ResourceLocation multiplayerIcon = new ResourceLocation("zypux/textures/menu/main/multiplayer.png");
  
  public static final ResourceLocation settingsIcon = new ResourceLocation("zypux/textures/menu/main/settings.png");
  
  public static final ResourceLocation resourcePackIcon = new ResourceLocation("zypux/textures/menu/main/resource-pack.png");
  
  public static final ResourceLocation quitIcon = new ResourceLocation("zypux/textures/menu/main/exit.png");
  
  private static final ArrayList<ChangeLog> changelogs = new ArrayList<>();
  
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  public static void drawScreen() {
    ScaledResolution rs = new ScaledResolution(mc);
    int height = rs.getScaledHeight();
    int width = rs.getScaledWidth();
    RenderUtils.image(mmBackground, 0, 0, width, height, 255.0F);
    RenderUtils.drawGradientRect(175.0D, 0.0D, 177.0D, height, (new Color(120, 217, 182, 255))
        .getRGB(), (new Color(0, 48, 98, 255))
        .getRGB());
    RenderUtils.drawGradientRect(0.0D, 0.0D, 175.0D, height, (new Color(120, 217, 182, 102))
        .getRGB(), (new Color(0, 48, 98, 102))
        .getRGB());
    RenderUtils.drawGradientHRect(width / 2.0F, height / 2.0F - 175.0F, width / 2.0F + 300.0F, height / 2.0F + 175.0F, (new Color(120, 217, 182, 102))
        .getRGB(), (new Color(0, 48, 98, 102))
        .getRGB());
    RenderUtils.drawGradientRect((width / 2.0F - 2.0F), (height / 2.0F - 177.0F), (width / 2.0F), (height / 2.0F + 175.0F), (new Color(120, 217, 182, 255))
        .getRGB(), (new Color(0, 48, 98, 255))
        .getRGB());
    RenderUtils.drawGradientHRect(width / 2.0F - 2.0F, height / 2.0F + 175.0F, width / 2.0F + 302.0F, height / 2.0F + 177.0F, (new Color(0, 48, 98, 255))
        .getRGB(), (new Color(120, 217, 182, 255))
        .getRGB());
    RenderUtils.drawGradientRect((width / 2.0F + 300.0F), (height / 2.0F - 177.0F), (width / 2.0F + 302.0F), (height / 2.0F + 177.0F), (new Color(0, 48, 98, 255))
        .getRGB(), (new Color(120, 217, 182, 255))
        .getRGB());
    RenderUtils.drawGradientHRect(width / 2.0F - 2.0F, height / 2.0F - 177.0F, width / 2.0F + 302.0F, height / 2.0F - 175.0F, (new Color(120, 217, 182, 255))
        .getRGB(), (new Color(0, 48, 98, 255))
        .getRGB());
    changelogs.stream().filter(changelog -> (changelog.getY() + height / 2.0F - 170.0F < height / 2.0F + 165.0F))
      .filter(changelog -> (changelog.getY() + height / 2.0F - 170.0F > height / 2.0F - 165.0F))
      .forEach(changelog -> {
          RenderUtils.rect(width / 2.0F + 8.0F, changelog.getY() + height / 2.0F - 170.0F, width / 2.0F + 11.0F + FontUtils.comic.getStringWidth(ColorUtils.colorize(new String[] { changelog.getText() }, )), changelog.getY() + height / 2.0F - 158.0F, (new Color(1711276032, true)).getRGB());
          FontUtils.comic.drawString(ColorUtils.colorize(new String[] { changelog.getText() }, ), (width / 2.0F + 10.0F), (changelog.getY() + height / 2.0F - 172.0F), changelog.getColor().getRGB());
        });
    GL11.glPushMatrix();
    GL11.glTranslatef(0.0F, 0.0F, 0.0F);
    GL11.glScalef(2.0F, 2.0F, 2.0F);
    FontUtils.comfortaa_r.drawStringWithShadow("Zypux Client", 14.0F, 5.0F, (new Color(16777215)).getRGB());
    GL11.glPopMatrix();
    FontUtils.jetbrains_mono.drawStringWithShadow("Version " + Zypux.getVersion(), 38.0F, 30.0F, (new Color(16777215)).getRGB());
  }
  
  public static void setup() {
    InputStream is = FontUtils.getStream("text/change_log.txt");
    Scanner scanner = new Scanner(is);
    int plusY = 0;
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if (line.contains("{0}")) {
        plusY += 10;
        changelogs.add(new ChangeLog(plusY, plusY, line.replaceAll("[{]0[}]", "&3*** &b")
              .replaceAll("[{]0e[}]", "&3 ***"), Color.BLUE));
        plusY += 10;
      } else if (line.contains("{1}")) {
        changelogs.add(new ChangeLog(plusY, plusY, line.replaceAll("[{]1[}]", "[+] &f"), new Color(3145472)));
      } else if (line.contains("{2}")) {
        changelogs.add(new ChangeLog(plusY, plusY, line.replaceAll("[{]2[}]", "[*] &f"), new Color(16766208)));
      } else if (line.contains("{3}")) {
        changelogs.add(new ChangeLog(plusY, plusY, line.replaceAll("[{]3[}]", "[-] &f"), new Color(16711680)));
      } 
      plusY += 16;
    } 
    scanner.close();
  }
  
  public static void mouseWheel() {
    if (Mouse.hasWheel()) {
      int wheel = Mouse.getDWheel();
      boolean handledScroll = false;
      if (!handledScroll)
        handleScroll(wheel); 
    } 
  }
  
  private static void handleScroll(int wheel) {
    if (wheel == 0)
      return; 
    if (!(mc.currentScreen instanceof net.minecraft.client.gui.GuiMainMenu))
      return; 
    System.out.println(mc.currentScreen);
    int height = (new ScaledResolution(mc)).getScaledHeight();
    if (((ChangeLog)changelogs.get(changelogs.size() - 1)).getY() + height / 2.0F + 170.0F <= height / 2.0F + 165.0F) {
      changelogs.forEach(changelog -> changelog.setY(changelog.getY() + 10));
      return;
    } 
    changelogs.forEach(changelog -> changelog.setY(changelog.getY() + wheel / 12));
  }
  
  public static void initGui() {
    TimerUtils timer = new TimerUtils(600L);
    TaskManager.async(() -> {
          while (!timer.isEnd())
            changelogs.forEach(()); 
        });
  }
}
