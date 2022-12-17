package ir.lecer.uwu.impl.hud;

import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.events.events.Render2DEvent;
import ir.lecer.uwu.events.handler.EventHandler;
import ir.lecer.uwu.impl.render.Blur;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.tools.shader.BlurUtils;
import ir.lecer.uwu.ui.editor.HUDEditor;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class Keystroke extends Module {
  public Keystroke() {
    super("Keystroke", Category.HUD, true);
  }
  
  @EventHandler
  public void onRender2D(Render2DEvent event) {
    ScaledResolution sr = new ScaledResolution(this.mc);
    if (HUDEditor.KeystrokeD) {
      HUDEditor.KeystrokeX = Mouse.getX() / 2;
      HUDEditor.KeystrokeY = sr.getScaledHeight() - Mouse.getY() / 2;
    } 
    int colorW = this.mc.gameSettings.keyBindForward.isKeyDown() ? (new Color(-2147483648, true)).getRGB() : (new Color(1291845632, true)).getRGB();
    if (Blur.isEnabled())
      BlurUtils.INSTANCE.draw(HUDEditor.KeystrokeX, HUDEditor.KeystrokeY, 15.0F, 15.0F, 20.0F); 
    RenderUtils.rect(HUDEditor.KeystrokeX, HUDEditor.KeystrokeY, (HUDEditor.KeystrokeX + 15), (HUDEditor.KeystrokeY + 15), colorW);
    RenderUtils.drawShadow(HUDEditor.KeystrokeX, HUDEditor.KeystrokeY, 15.0F, 15.0F, 5, false);
    FontUtils.minecraft.drawString("W", HUDEditor.KeystrokeX + 5, HUDEditor.KeystrokeY + 4, (new Color(-859059253, true)).getRGB());
    int colorA = this.mc.gameSettings.keyBindLeft.isKeyDown() ? (new Color(-2147483648, true)).getRGB() : (new Color(1291845632, true)).getRGB();
    if (Blur.isEnabled())
      BlurUtils.INSTANCE.draw((HUDEditor.KeystrokeX - 18), (HUDEditor.KeystrokeY + 18), 15.0F, 15.0F, 20.0F); 
    RenderUtils.rect((HUDEditor.KeystrokeX - 18), (HUDEditor.KeystrokeY + 18), (HUDEditor.KeystrokeX - 3), (HUDEditor.KeystrokeY + 33), colorA);
    RenderUtils.drawShadow((HUDEditor.KeystrokeX - 18), (HUDEditor.KeystrokeY + 18), 15.0F, 15.0F, 5, false);
    FontUtils.minecraft.drawString("A", HUDEditor.KeystrokeX - 13, HUDEditor.KeystrokeY + 22, (new Color(-859059253, true)).getRGB());
    int colorS = this.mc.gameSettings.keyBindBack.isKeyDown() ? (new Color(-2147483648, true)).getRGB() : (new Color(1291845632, true)).getRGB();
    if (Blur.isEnabled())
      BlurUtils.INSTANCE.draw(HUDEditor.KeystrokeX, (HUDEditor.KeystrokeY + 18), 15.0F, 15.0F, 20.0F); 
    RenderUtils.rect(HUDEditor.KeystrokeX, (HUDEditor.KeystrokeY + 18), (HUDEditor.KeystrokeX + 15), (HUDEditor.KeystrokeY + 33), colorS);
    RenderUtils.drawShadow(HUDEditor.KeystrokeX, (HUDEditor.KeystrokeY + 18), 15.0F, 15.0F, 5, false);
    FontUtils.minecraft.drawString("S", HUDEditor.KeystrokeX + 5, HUDEditor.KeystrokeY + 22, (new Color(-859059253, true)).getRGB());
    int colorD = this.mc.gameSettings.keyBindRight.isKeyDown() ? (new Color(-2147483648, true)).getRGB() : (new Color(1291845632, true)).getRGB();
    if (Blur.isEnabled())
      BlurUtils.INSTANCE.draw((HUDEditor.KeystrokeX + 18), (HUDEditor.KeystrokeY + 18), 15.0F, 15.0F, 20.0F); 
    RenderUtils.rect((HUDEditor.KeystrokeX + 18), (HUDEditor.KeystrokeY + 18), (HUDEditor.KeystrokeX + 33), (HUDEditor.KeystrokeY + 33), colorD);
    RenderUtils.drawShadow((HUDEditor.KeystrokeX + 18), (HUDEditor.KeystrokeY + 18), 15.0F, 15.0F, 5, false);
    FontUtils.minecraft.drawString("D", HUDEditor.KeystrokeX + 23, HUDEditor.KeystrokeY + 22, (new Color(-859059253, true)).getRGB());
  }
  
  public static boolean isHovered(int mouseX, int mouseY) {
    return (mouseX >= HUDEditor.KeystrokeX - 33 && mouseX <= HUDEditor.KeystrokeX + 33 && mouseY >= HUDEditor.KeystrokeY && mouseY <= HUDEditor.KeystrokeY + 33);
  }
}
