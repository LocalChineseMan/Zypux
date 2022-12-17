package ir.lecer.uwu.impl.hud;

import com.google.common.collect.Lists;
import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.events.events.Render2DEvent;
import ir.lecer.uwu.events.handler.EventHandler;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.tools.tasks.TaskManager;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Watermark extends Module {
  private final ArrayList<String> mode = Lists.newArrayList((Object[])new String[] { "Black", "Logo" });
  
  private final ResourceLocation mainLogo = new ResourceLocation("zypux/textures/logo.png");
  
  private static String text = "Zypux Client";
  
  public Watermark() {
    super("Watermark", Category.HUD, true);
  }
  
  public void onSetup() {
    (Zypux.getInstance()).settingsManager.addSetting(new Setting("Mode", this, "Black", this.mode));
  }
  
  @EventHandler
  public void onRender2D(Render2DEvent event) {
    String stringWidthInfo, wmMode = (Zypux.getInstance()).settingsManager.getSettingByName("Mode").getStringValue().toLowerCase();
    switch (wmMode) {
      case "black":
        stringWidthInfo = "| Version b1 | FPS " + Minecraft.getDebugFPS();
        RenderUtils.rect(5.0F, 5.0F, (105 + FontUtils.getStringWidth(stringWidthInfo)), 20.0F, (new Color(-1342177280, true)).getRGB());
        RenderUtils.drawShadow(5.0F, 5.0F, (100 + FontUtils.getStringWidth(stringWidthInfo)), 15.0F, 10, false);
        FontUtils.jetbrains_mono.drawString(text, 8, 6, (new Color(-1, true)).getRGB());
        FontUtils.jetbrains_mono.drawString(stringWidthInfo, 12 + FontUtils.jetbrains_mono.getStringWidth("Zypux Client"), 6, (new Color(-1, true)).getRGB());
        break;
      case "logo":
        RenderUtils.image(this.mainLogo, 5, 5, 64, 64, 255.0F);
        break;
    } 
  }
  
  public static void playAnimation() {
    TaskManager.async(() -> {
          while (true) {
            try {
              while (true) {
                text = "Zypux Client";
                Thread.sleep(3250L);
                text = "Zypux Clien";
                Thread.sleep(75L);
                text = "Zypux Clie";
                Thread.sleep(75L);
                text = "Zypux Cli";
                Thread.sleep(75L);
                text = "Zypux Cl";
                Thread.sleep(75L);
                text = "Zypux C";
                Thread.sleep(75L);
                text = "Zypux";
                Thread.sleep(75L);
                text = "Zypu";
                Thread.sleep(75L);
                text = "Zyp";
                Thread.sleep(75L);
                text = "Zy";
                Thread.sleep(75L);
                text = "Z";
                Thread.sleep(75L);
                text = " ";
                Thread.sleep(500L);
                text = "b";
                Thread.sleep(75L);
                text = "by";
                Thread.sleep(75L);
                text = "by L";
                Thread.sleep(75L);
                text = "by Le";
                Thread.sleep(75L);
                text = "by Lec";
                Thread.sleep(75L);
                text = "by Lece";
                Thread.sleep(75L);
                text = "by Lecer";
                Thread.sleep(1000L);
                text = "by Lece";
                Thread.sleep(75L);
                text = "by Lec";
                Thread.sleep(75L);
                text = "by Le";
                Thread.sleep(75L);
                text = "by L";
                Thread.sleep(75L);
                text = "by";
                Thread.sleep(75L);
                text = "b";
                Thread.sleep(75L);
                text = " ";
                Thread.sleep(500L);
                text = "Z";
                Thread.sleep(75L);
                text = "Zy";
                Thread.sleep(75L);
                text = "Zyp";
                Thread.sleep(75L);
                text = "Zypu";
                Thread.sleep(75L);
                text = "Zypux";
                Thread.sleep(75L);
                text = "Zypux C";
                Thread.sleep(75L);
                text = "Zypux Cl";
                Thread.sleep(75L);
                text = "Zypux Cli";
                Thread.sleep(75L);
                text = "Zypux Clie";
                Thread.sleep(75L);
                text = "Zypux Clien";
                Thread.sleep(75L);
              } 
              break;
            } catch (Exception exception) {}
          } 
        });
  }
}
