package ir.lecer.uwu.tools.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

public class BlurUtils {
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  public static BlurUtils INSTANCE = new BlurUtils();
  
  private static ShaderGroup blurShader;
  
  private static int lastScale;
  
  private static int lastScaleWidth;
  
  private static int lastScaleHeight;
  
  public BlurUtils() {
    try {
      blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("shaders/post/blurArea.json"));
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  private void reinitShader() {
    blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
    Framebuffer buffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    buffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
  }
  
  public final void draw(float x, float y, float width, float height, float radius) {
    ScaledResolution resolution = new ScaledResolution(mc);
    int factor = resolution.getScaleFactor();
    int factor2 = resolution.getScaledWidth();
    int factor3 = resolution.getScaledHeight();
    if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3)
      reinitShader(); 
    lastScale = factor;
    lastScaleWidth = factor2;
    lastScaleHeight = factor3;
    ((Shader)blurShader.getListShaders().get(0)).getShaderManager().getShaderUniform("BlurXY").set(x, factor3 - y - height);
    ((Shader)blurShader.getListShaders().get(1)).getShaderManager().getShaderUniform("BlurXY").set(x, factor3 - y - height);
    ((Shader)blurShader.getListShaders().get(0)).getShaderManager().getShaderUniform("BlurCoord").set(width, height);
    ((Shader)blurShader.getListShaders().get(1)).getShaderManager().getShaderUniform("BlurCoord").set(width, height);
    ((Shader)blurShader.getListShaders().get(0)).getShaderManager().getShaderUniform("Radius").set(radius);
    ((Shader)blurShader.getListShaders().get(1)).getShaderManager().getShaderUniform("Radius").set(radius);
    blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
    mc.getFramebuffer().bindFramebuffer(true);
  }
}
