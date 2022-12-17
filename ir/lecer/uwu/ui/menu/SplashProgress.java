package ir.lecer.uwu.ui.menu;

import ir.lecer.uwu.tools.renders.RenderUtils;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public final class SplashProgress {
  private SplashProgress() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  private static final ResourceLocation background = new ResourceLocation("zypux/textures/menu/splash/background.png");
  
  public static void drawScreen(TextureManager textureManager) {
    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    int factor = sr.getScaleFactor();
    Framebuffer fb = new Framebuffer(sr.getScaledWidth() * factor, sr.getScaledHeight() * factor, true);
    fb.bindFramebuffer(false);
    GlStateManager.matrixMode(5889);
    GlStateManager.loadIdentity();
    GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
    GlStateManager.matrixMode(5888);
    GlStateManager.loadIdentity();
    GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    GlStateManager.enableTexture2D();
    GlStateManager.disableLighting();
    GlStateManager.disableFog();
    GlStateManager.disableDepth();
    GlStateManager.enableTexture2D();
    textureManager.bindTexture(background);
    GlStateManager.resetColor();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, 1920, 1080, sr.getScaledWidth(), sr.getScaledHeight());
    renderUI(sr.getScaledWidth(), sr.getScaledHeight());
    fb.unbindFramebuffer();
    fb.framebufferRender(sr.getScaledWidth() * factor, sr.getScaledHeight() * factor);
    GlStateManager.enableAlpha();
    GlStateManager.alphaFunc(516, 0.1F);
    Minecraft.getMinecraft().updateDisplay();
  }
  
  private static void renderUI(int width, int height) {
    RenderUtils.drawGradientHRect(0.0F, (height - 5), width, height, (new Color(120, 217, 182, 255))
        .getRGB(), (new Color(0, 48, 98, 255))
        .getRGB());
  }
}
