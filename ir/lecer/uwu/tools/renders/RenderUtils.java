package ir.lecer.uwu.tools.renders;

import com.google.common.collect.Maps;
import com.jhlabs.image.GaussianFilter;
import ir.lecer.uwu.enums.Shadows;
import ir.lecer.uwu.tools.shader.ShaderShell;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

public final class RenderUtils {
  private RenderUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  public static final Minecraft mc = Minecraft.getMinecraft();
  
  private static final int zLevel = 0;
  
  private static ScaledResolution scaledResolution;
  
  public static void drawEsp(EntityLivingBase ent, float pTicks, int hexColor, int hexColorIn) {
    if (!ent.isEntityAlive())
      return; 
    double x = getDiff(ent.lastTickPosX, ent.posX, pTicks, RenderManager.renderPosX);
    double y = getDiff(ent.lastTickPosY, ent.posY, pTicks, RenderManager.renderPosY);
    double z = getDiff(ent.lastTickPosZ, ent.posZ, pTicks, RenderManager.renderPosZ);
    boundingBox((Entity)ent, x, y, z, hexColor, hexColorIn);
  }
  
  public static void boundingBox(Entity entity, double x, double y, double z, int color, int colorIn) {
    GlStateManager.pushMatrix();
    GL11.glLineWidth(1.0F);
    AxisAlignedBB var11 = entity.getEntityBoundingBox();
    AxisAlignedBB var12 = new AxisAlignedBB(var11.minX - entity.posX + x, var11.minY - entity.posY + y, var11.minZ - entity.posZ + z, var11.maxX - entity.posX + x, var11.maxY - entity.posY + y, var11.maxZ - entity.posZ + z);
    if (color != 0) {
      GlStateManager.disableDepth();
      filledBox(var12, colorIn);
      disableLighting();
      drawOutlinedBoundingBox(var12, color);
    } 
    GlStateManager.popMatrix();
  }
  
  private static void setupFBO(Framebuffer fbo) {
    EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
    int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
    EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
    EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, (Minecraft.getMinecraft()).displayWidth, (Minecraft.getMinecraft()).displayHeight);
    EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
    EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
  }
  
  public static void smoothRect(float left, float top, float right, float bottom, int color) {
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    rect(left, top, right, bottom, color);
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    rect((left * 2.0F - 1.0F), (top * 2.0F), (left * 2.0F), (bottom * 2.0F - 1.0F), color);
    rect((left * 2.0F), (top * 2.0F - 1.0F), (right * 2.0F), (top * 2.0F), color);
    rect((right * 2.0F), (top * 2.0F), (right * 2.0F + 1.0F), (bottom * 2.0F - 1.0F), color);
    rect((left * 2.0F), (bottom * 2.0F - 1.0F), (right * 2.0F), (bottom * 2.0F), color);
    GL11.glDisable(3042);
    GL11.glScalef(2.0F, 2.0F, 2.0F);
  }
  
  public static void glowRoundedRect(float startX, float startY, float endX, float endY, int color, float radius, float force) {
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glDisable(3008);
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    ShaderShell.ROUNDED_RECT.attach();
    ShaderShell.ROUNDED_RECT.set4F("color", red, green, blue, alpha);
    ShaderShell.ROUNDED_RECT.set2F("resolution", (Minecraft.getMinecraft()).displayWidth, (Minecraft.getMinecraft()).displayHeight);
    ShaderShell.ROUNDED_RECT.set2F("center", (startX + (endX - startX) / 2.0F) * 2.0F, (startY + (endY - startY) / 2.0F) * 2.0F);
    ShaderShell.ROUNDED_RECT.set2F("dst", (endX - startX - radius) * 2.0F, (endY - startY - radius) * 2.0F);
    ShaderShell.ROUNDED_RECT.set1F("radius", radius);
    ShaderShell.ROUNDED_RECT.set1F("force", force);
    GL11.glBegin(7);
    GL11.glVertex2d(endX, startY);
    GL11.glVertex2d(startX, startY);
    GL11.glVertex2d(startX, endY);
    GL11.glVertex2d(endX, endY);
    GL11.glEnd();
    ShaderShell.ROUNDED_RECT.detach();
    GL11.glEnable(3008);
    GL11.glDisable(3042);
    GL11.glPopMatrix();
  }
  
  public static void drawCircle3D(Entity entity, double radius, float partialTicks, int points, float width, int color) {
    GL11.glPushMatrix();
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GL11.glDisable(2929);
    GL11.glLineWidth(width);
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(2929);
    GL11.glBegin(3);
    double var10000 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
    double x = var10000 - RenderManager.renderPosX;
    var10000 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
    double y = var10000 - RenderManager.renderPosY;
    var10000 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
    double z = var10000 - RenderManager.renderPosZ;
    setColor(color);
    for (int i = 0; i <= points; i++)
      GL11.glVertex3d(x + radius * Math.cos((i * 6.2831855F / points)), y, z + radius * Math.sin((i * 6.2831855F / points))); 
    GL11.glEnd();
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GL11.glEnable(2929);
    GL11.glDisable(2848);
    GL11.glEnable(2929);
    GL11.glEnable(3553);
    GL11.glPopMatrix();
  }
  
  public static void setColor(int color) {
    GL11.glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
  }
  
  public static int loadGlTexture(BufferedImage bufferedImage) {
    int textureId = GL11.glGenTextures();
    GL11.glBindTexture(3553, textureId);
    GL11.glTexParameteri(3553, 10242, 10497);
    GL11.glTexParameteri(3553, 10243, 10497);
    GL11.glTexParameteri(3553, 10241, 9729);
    GL11.glTexParameteri(3553, 10240, 9729);
    GL11.glTexImage2D(3553, 0, 6408, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, 6408, 5121, 
        readImageToBuffer(bufferedImage));
    GL11.glBindTexture(3553, 0);
    return textureId;
  }
  
  public static ByteBuffer readImageToBuffer(BufferedImage bufferedImage) {
    int[] rgbArray = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * rgbArray.length);
    for (int rgb : rgbArray)
      byteBuffer.putInt(rgb << 8 | rgb >> 24 & 0xFF); 
    byteBuffer.flip();
    return byteBuffer;
  }
  
  public static void checkSetupFBO() {
    Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
    if (fbo != null && 
      fbo.depthBuffer > -1) {
      setupFBO(fbo);
      fbo.depthBuffer = -1;
    } 
  }
  
  public static void renderOne(float lineWidth) {
    checkSetupFBO();
    GL11.glPushAttrib(1048575);
    GL11.glDisable(3008);
    GL11.glDisable(3553);
    GL11.glDisable(2896);
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(lineWidth);
    GL11.glEnable(2848);
    GL11.glEnable(2960);
    GL11.glClear(1024);
    GL11.glClearStencil(15);
    GL11.glStencilFunc(512, 1, 15);
    GL11.glStencilOp(7681, 7681, 7681);
    GL11.glPolygonMode(1032, 6913);
  }
  
  public static void renderTwo() {
    GL11.glStencilFunc(512, 0, 15);
    GL11.glStencilOp(7681, 7681, 7681);
    GL11.glPolygonMode(1032, 6914);
  }
  
  public static void renderThree() {
    GL11.glStencilFunc(514, 1, 15);
    GL11.glStencilOp(7680, 7680, 7680);
    GL11.glPolygonMode(1032, 6913);
  }
  
  public static void renderFour(Color color) {
    ColorUtils.glColor(color);
    GL11.glDepthMask(false);
    GL11.glDisable(2929);
    GL11.glEnable(10754);
    GL11.glPolygonOffset(1.0F, -2000000.0F);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
  }
  
  public static void renderFive() {
    GL11.glPolygonOffset(1.0F, 2000000.0F);
    GL11.glDisable(10754);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(2960);
    GL11.glDisable(2848);
    GL11.glHint(3154, 4352);
    GL11.glEnable(3042);
    GL11.glEnable(2896);
    GL11.glEnable(3553);
    GL11.glEnable(3008);
    GL11.glPopAttrib();
  }
  
  public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    worldRenderer.begin(3, DefaultVertexFormats.POSITION);
    worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
    worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
    worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
    worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
    worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
    tessellator.draw();
    worldRenderer.begin(3, DefaultVertexFormats.POSITION);
    worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
    worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
    worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
    worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
    worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
    tessellator.draw();
    worldRenderer.begin(1, DefaultVertexFormats.POSITION);
    worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
    worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
    worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
    worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
    worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
    worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
    worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
    worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
    tessellator.draw();
  }
  
  public static void drawOutlinedBoundingBox(AxisAlignedBB bb, int color) {
    float red = (color >> 16 & 0xFF) / 255.0F;
    float blue = (color >> 8 & 0xFF) / 255.0F;
    float green = (color & 0xFF) / 255.0F;
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float[] color1 = { red, blue, green, alpha };
    GL11.glLineWidth(1.0F);
    GL11.glColor4f(color1[0], color1[1], color1[2], 0.8F);
    RenderGlobal.drawSelectionBoundingBox(bb);
    GlStateManager.disableDepth();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  public static void enableLighting() {
    GL11.glDisable(3042);
    GL11.glEnable(3553);
    GL11.glDisable(2848);
    GL11.glDisable(3042);
    OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GL11.glMatrixMode(5890);
    GL11.glLoadIdentity();
    float var3 = 0.0039063F;
    GL11.glScalef(var3, var3, var3);
    GL11.glTranslatef(8.0F, 8.0F, 8.0F);
    GL11.glMatrixMode(5888);
    GL11.glTexParameteri(3553, 10241, 9729);
    GL11.glTexParameteri(3553, 10240, 9729);
    GL11.glTexParameteri(3553, 10242, 10496);
    GL11.glTexParameteri(3553, 10243, 10496);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
  }
  
  public static void disableLighting() {
    OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GL11.glDisable(3553);
    OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glDisable(2896);
    GL11.glDisable(3553);
  }
  
  public static void enableGL3D(float lineWidth) {
    GL11.glDisable(3008);
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glEnable(2884);
    (Minecraft.getMinecraft()).entityRenderer.disableLightmap();
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GL11.glHint(3155, 4354);
    GL11.glLineWidth(lineWidth);
  }
  
  public static void disableGL3D() {
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDisable(3042);
    GL11.glEnable(3008);
    GL11.glDepthMask(true);
    GL11.glCullFace(1029);
    GL11.glDisable(2848);
    GL11.glHint(3154, 4352);
    GL11.glHint(3155, 4352);
  }
  
  public static void rect(int mode, double left, double top, double right, double bottom, int color) {
    if (left < right) {
      double i = left;
      left = right;
      right = i;
    } 
    if (top < bottom) {
      double j = top;
      top = bottom;
      bottom = j;
    } 
    float f3 = (color >> 24 & 0xFF) / 255.0F;
    float f = (color >> 16 & 0xFF) / 255.0F;
    float f1 = (color >> 8 & 0xFF) / 255.0F;
    float f2 = (color & 0xFF) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(f, f1, f2, f3);
    worldrenderer.begin(mode, DefaultVertexFormats.POSITION);
    worldrenderer.pos(left, bottom, 0.0D).endVertex();
    worldrenderer.pos(right, bottom, 0.0D).endVertex();
    worldrenderer.pos(right, top, 0.0D).endVertex();
    worldrenderer.pos(left, top, 0.0D).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void borderedRect(float x, float y, float x1, float y1, float width, int internalColor, int borderColor) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    glColor(internalColor);
    rect(x, y, x1, y1);
    glColor(borderColor);
    rect(x - width, y - width, x1 + width, y);
    rect(x - width, y, x, y1);
    rect(x1, y, x1 + width, y1);
    rect(x - width, y1, x1 + width, y1 + width);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void borderedRect(int x, int y, int x1, int y1, int insideC, int borderC) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    drawVLine((x *= 2), (y *= 2), ((y1 *= 2) - 1), borderC);
    drawVLine(((x1 *= 2) - 1), y, y1, borderC);
    drawHLine(x, (x1 - 1), y, borderC);
    drawHLine(x, (x1 - 2), (y1 - 1), borderC);
    rect((x + 1), (y + 1), (x1 - 1), (y1 - 1), insideC);
    GL11.glScalef(2.0F, 2.0F, 2.0F);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void drawBorderedRectReliant(float x, float y, float x1, float y1, float lineWidth, int inside, int border) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    rect(x, y, x1, y1, inside);
    renderLine(x, y, x1, y1, lineWidth, border);
  }
  
  public static void drawGradientBorderedRectReliant(float x, float y, float x1, float y1, float lineWidth, int border, int bottom, int top) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    drawGradientRect(x, y, x1, y1, top, bottom);
    renderLine(x, y, x1, y1, lineWidth, border);
  }
  
  private static void renderLine(float x, float y, float x1, float y1, float lineWidth, int border) {
    glColor(border);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(lineWidth);
    GL11.glBegin(3);
    GL11.glVertex2f(x, y);
    GL11.glVertex2f(x, y1);
    GL11.glVertex2f(x1, y1);
    GL11.glVertex2f(x1, y);
    GL11.glVertex2f(x, y);
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void roundedRect(double x, double y, double width, double height, double edgeRadius, Color color) {
    double halfRadius = edgeRadius / 2.0D;
    width -= halfRadius;
    height -= halfRadius;
    float sideLength = (float)edgeRadius;
    sideLength /= 2.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2884);
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    if (color != null)
      GL11.glColor4d((color.getRed() / 255.0F), (color.getGreen() / 255.0F), (color.getBlue() / 255.0F), (color.getAlpha() / 255.0F)); 
    GL11.glBegin(6);
    double i;
    for (i = 180.0D; i <= 270.0D; i++) {
      double angle = i * 6.283185307179586D / 360.0D;
      GL11.glVertex2d(x + sideLength * Math.cos(angle) + sideLength, y + sideLength * Math.sin(angle) + sideLength);
    } 
    GL11.glVertex2d(x + sideLength, y + sideLength);
    GL11.glEnd();
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glColor4d(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), 255.0D);
    sideLength = (float)edgeRadius;
    sideLength /= 2.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2884);
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    if (color != null)
      GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), 255.0D); 
    GL11.glEnable(2848);
    GL11.glBegin(6);
    for (i = 0.0D; i <= 90.0D; i++) {
      double angle = i * 6.283185307179586D / 360.0D;
      GL11.glVertex2d(x + width + sideLength * Math.cos(angle), y + height + sideLength * Math.sin(angle));
    } 
    GL11.glVertex2d(x + width, y + height);
    GL11.glEnd();
    GL11.glDisable(2848);
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glColor4d(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), 255.0D);
    sideLength = (float)edgeRadius;
    sideLength /= 2.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2884);
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    if (color != null)
      GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), 255.0D); 
    GL11.glEnable(2848);
    GL11.glBegin(6);
    for (i = 270.0D; i <= 360.0D; i++) {
      double angle = i * 6.283185307179586D / 360.0D;
      GL11.glVertex2d(x + width + sideLength * Math.cos(angle), y + sideLength * Math.sin(angle) + sideLength);
    } 
    GL11.glVertex2d(x + width, y + sideLength);
    GL11.glEnd();
    GL11.glDisable(2848);
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glColor4d(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), 255.0D);
    sideLength = (float)edgeRadius;
    sideLength /= 2.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2884);
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    if (color != null)
      GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), 255.0D); 
    GL11.glEnable(2848);
    GL11.glBegin(6);
    for (i = 90.0D; i <= 180.0D; i++) {
      double angle = i * 6.283185307179586D / 360.0D;
      GL11.glVertex2d(x + sideLength * Math.cos(angle) + sideLength, y + height + sideLength * Math.sin(angle));
    } 
    GL11.glVertex2d(x + sideLength, y + height);
    GL11.glEnd();
    GL11.glDisable(2848);
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glColor4d(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), 255.0D);
    rect(x + halfRadius, y + halfRadius, width - halfRadius, height - halfRadius, color.getRGB());
    rect(x, y + halfRadius, edgeRadius / 2.0D, height - halfRadius, color.getRGB());
    rect(x + width, y + halfRadius, edgeRadius / 2.0D, height - halfRadius, color.getRGB());
    rect(x + halfRadius, y, width - halfRadius, halfRadius, color.getRGB());
    rect(x + halfRadius, y + height, width - halfRadius, halfRadius, color.getRGB());
  }
  
  public static void roundedOutLine(double x, double y, double width, double height, double thickness, double edgeRadius, Color color) {
    double halfRadius = edgeRadius / 2.0D;
    width -= halfRadius;
    height -= halfRadius;
    float sideLength = (float)edgeRadius;
    sideLength /= 2.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2884);
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    if (color != null)
      GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), 255.0D); 
    GL11.glEnable(2848);
    GL11.glBegin(1);
    double i;
    for (i = 180.0D; i <= 270.0D; i++) {
      double angle = i * 6.283185307179586D / 360.0D;
      GL11.glVertex2d(x + sideLength * Math.cos(angle) + sideLength, y + sideLength * Math.sin(angle) + sideLength);
    } 
    GL11.glVertex2d(x + sideLength, y + sideLength);
    GL11.glEnd();
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glColor4d(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), 255.0D);
    sideLength = (float)edgeRadius;
    sideLength /= 2.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2884);
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    if (color != null)
      GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), 255.0D); 
    GL11.glEnable(2848);
    GL11.glBegin(1);
    for (i = 0.0D; i <= 90.0D; i++) {
      double angle = i * 6.283185307179586D / 360.0D;
      GL11.glVertex2d(x + width + sideLength * Math.cos(angle), y + height + sideLength * Math.sin(angle));
    } 
    GL11.glVertex2d(x + width, y + height);
    GL11.glEnd();
    GL11.glDisable(2848);
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glColor4d(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), 255.0D);
    sideLength = (float)edgeRadius;
    sideLength /= 2.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2884);
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    if (color != null)
      GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), 255.0D); 
    GL11.glEnable(2848);
    GL11.glBegin(1);
    for (i = 270.0D; i <= 360.0D; i++) {
      double angle = i * 6.283185307179586D / 360.0D;
      GL11.glVertex2d(x + width + sideLength * Math.cos(angle), y + sideLength * Math.sin(angle) + sideLength);
    } 
    GL11.glVertex2d(x + width, y + sideLength);
    GL11.glEnd();
    GL11.glDisable(2848);
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glColor4d(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), 255.0D);
    sideLength = (float)edgeRadius;
    sideLength /= 2.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2884);
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    if (color != null)
      GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), 255.0D); 
    GL11.glEnable(2848);
    GL11.glBegin(1);
    for (i = 90.0D; i <= 180.0D; i++) {
      double angle = i * 6.283185307179586D / 360.0D;
      GL11.glVertex2d(x + sideLength * Math.cos(angle) + sideLength, y + height + sideLength * Math.sin(angle));
    } 
    GL11.glVertex2d(x + sideLength, y + height);
    GL11.glEnd();
    GL11.glDisable(2848);
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glColor4d(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), 255.0D);
  }
  
  public static void drawGradientRect(float x, float y, float x1, float y1, Color topColor, Color bottomColor) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glShadeModel(7425);
    GL11.glBegin(7);
    glColor(topColor);
    GL11.glVertex2f(x, y1);
    GL11.glVertex2f(x1, y1);
    glColor(bottomColor);
    GL11.glVertex2f(x1, y);
    GL11.glVertex2f(x, y);
    GL11.glEnd();
    GL11.glShadeModel(7424);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void drawGradientHRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glShadeModel(7425);
    GL11.glBegin(7);
    glColor(topColor);
    GL11.glVertex2f(x, y);
    GL11.glVertex2f(x, y1);
    glColor(bottomColor);
    GL11.glVertex2f(x1, y1);
    GL11.glVertex2f(x1, y);
    GL11.glEnd();
    GL11.glShadeModel(7424);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void drawGradientHRect(double x, double y, double x1, double y1, int topColor, int bottomColor) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glShadeModel(7425);
    GL11.glBegin(7);
    glColor(topColor);
    GL11.glVertex2d(x, y);
    GL11.glVertex2d(x, y1);
    glColor(bottomColor);
    GL11.glVertex2d(x1, y1);
    GL11.glVertex2d(x1, y);
    GL11.glEnd();
    GL11.glShadeModel(7424);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void drawGradientRect(double x, double y, double x2, double y2, int col1, int col2) {
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glShadeModel(7425);
    GL11.glPushMatrix();
    GL11.glBegin(7);
    glColor(col1);
    GL11.glVertex2d(x2, y);
    GL11.glVertex2d(x, y);
    glColor(col2);
    GL11.glVertex2d(x, y2);
    GL11.glVertex2d(x2, y2);
    GL11.glEnd();
    GL11.glPopMatrix();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glDisable(2848);
    GL11.glShadeModel(7424);
  }
  
  public static Color TwoColoreffect(Color cl1, Color cl2, double speed) {
    double thing = speed / 4.0D % 1.0D;
    float val = MathHelper.clamp_float((float)Math.sin(18.84955592153876D * thing) / 2.0F + 0.5F, 0.0F, 1.0F);
    return new Color(lerp(cl1.getRed() / 255.0F, cl2.getRed() / 255.0F, val), lerp(cl1.getGreen() / 255.0F, cl2.getGreen() / 255.0F, val), lerp(cl1.getBlue() / 255.0F, cl2.getBlue() / 255.0F, val));
  }
  
  public static void drawGradientBorderedRect(double x, double y, double x2, double y2, float l1, int col1, int col2, int col3) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glPushMatrix();
    glColor(col1);
    GL11.glLineWidth(1.0F);
    GL11.glBegin(1);
    GL11.glVertex2d(x, y);
    GL11.glVertex2d(x, y2);
    GL11.glVertex2d(x2, y2);
    GL11.glVertex2d(x2, y);
    GL11.glVertex2d(x, y);
    GL11.glVertex2d(x2, y);
    GL11.glVertex2d(x, y2);
    GL11.glVertex2d(x2, y2);
    GL11.glEnd();
    GL11.glPopMatrix();
    drawGradientRect(x, y, x2, y2, col2, col3);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void drawStrip(int x, int y, float width, double angle, float points, float radius, int color) {
    float f1 = (color >> 24 & 0xFF) / 255.0F;
    float f2 = (color >> 16 & 0xFF) / 255.0F;
    float f3 = (color >> 8 & 0xFF) / 255.0F;
    float f4 = (color & 0xFF) / 255.0F;
    GL11.glPushMatrix();
    GL11.glTranslated(x, y, 0.0D);
    GL11.glColor4f(f2, f3, f4, f1);
    GL11.glLineWidth(width);
    if (angle > 0.0D) {
      GL11.glBegin(3);
      int i = 0;
      while (i < angle) {
        float a = (float)(i * angle * Math.PI / points);
        float xc = (float)(Math.cos(a) * radius);
        float yc = (float)(Math.sin(a) * radius);
        GL11.glVertex2f(xc, yc);
        i++;
      } 
      GL11.glEnd();
    } 
    if (angle < 0.0D) {
      GL11.glBegin(3);
      int i = 0;
      while (i > angle) {
        float a = (float)(i * angle * Math.PI / points);
        float xc = (float)(Math.cos(a) * -radius);
        float yc = (float)(Math.sin(a) * -radius);
        GL11.glVertex2f(xc, yc);
        i--;
      } 
      GL11.glEnd();
    } 
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GL11.glDisable(3479);
    GL11.glPopMatrix();
  }
  
  public static void drawHLine(float x, float y, float x1, int y1) {
    if (y < x) {
      float var5 = x;
      x = y;
      y = var5;
    } 
    rect(x, x1, y + 1.0F, x1 + 1.0F, y1);
  }
  
  public static void drawVLine(float x, float y, float x1, int y1) {
    if (x1 < y) {
      float var5 = y;
      y = x1;
      x1 = var5;
    } 
    rect(x, y + 1.0F, x + 1.0F, x1, y1);
  }
  
  public static void drawHLine(float x, float y, float x1, int y1, int y2) {
    if (y < x) {
      float var5 = x;
      x = y;
      y = var5;
    } 
    drawGradientRect(x, x1, (y + 1.0F), (x1 + 1.0F), y1, y2);
  }
  
  public static void rect(float x, float y, float x1, float y1, float r, float g, float b, float a) {
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glColor4f(r, g, b, a);
    rect(x, y, x1, y1);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void rect(float x, float y, float x1, float y1) {
    if (x < x1) {
      float i = x;
      x = x1;
      x1 = i;
    } 
    if (y < y1) {
      float j = y;
      y = y1;
      y1 = j;
    } 
    GL11.glBegin(7);
    GL11.glVertex2f(x, y1);
    GL11.glVertex2f(x1, y1);
    GL11.glVertex2f(x1, y);
    GL11.glVertex2f(x, y);
    GL11.glEnd();
  }
  
  public static void circle(float cx, float cy, float r, int c) {
    GL11.glPushMatrix();
    cx *= 2.0F;
    cy *= 2.0F;
    int num_segments = (int)(r * 3.0F);
    float f = (c >> 24 & 0xFF) / 255.0F;
    float f1 = (c >> 16 & 0xFF) / 255.0F;
    float f2 = (c >> 8 & 0xFF) / 255.0F;
    float f3 = (c & 0xFF) / 255.0F;
    float theta = (float)(6.2831852D / num_segments);
    float p = (float)Math.cos(theta);
    float s = (float)Math.sin(theta);
    float x = r *= 2.0F;
    float y = 0.0F;
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    GL11.glColor4f(f1, f2, f3, f);
    GL11.glBegin(2);
    int ii = 0;
    while (ii < num_segments) {
      GL11.glVertex2f(x + cx, y + cy);
      float t = x;
      x = p * x - s * y;
      y = s * t + p * y;
      ii++;
    } 
    GL11.glEnd();
    GL11.glScalef(2.0F, 2.0F, 2.0F);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GL11.glPopMatrix();
  }
  
  public static void drawFullCircle(float cx, float cy, float r, int c) {
    r = (float)(r * 2.0D);
    cx *= 2.0F;
    cy *= 2.0F;
    float f = (c >> 24 & 0xFF) / 255.0F;
    float f1 = (c >> 16 & 0xFF) / 255.0F;
    float f2 = (c >> 8 & 0xFF) / 255.0F;
    float f3 = (c & 0xFF) / 255.0F;
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    GL11.glColor4f(f1, f2, f3, f);
    GL11.glBegin(6);
    int i = 0;
    while (i <= 360) {
      double x = Math.sin(i * Math.PI / 180.0D) * r;
      double y = Math.cos(i * Math.PI / 180.0D) * r;
      GL11.glVertex2d(cx + x, cy + y);
      i++;
    } 
    GL11.glEnd();
    GL11.glScalef(2.0F, 2.0F, 2.0F);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void glColor(int hex) {
    float alpha = (hex >> 24 & 0xFF) / 255.0F;
    float red = (hex >> 16 & 0xFF) / 255.0F;
    float green = (hex >> 8 & 0xFF) / 255.0F;
    float blue = (hex & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
  }
  
  public static void glColor(int red, int green, int blue, int alpha) {
    GlStateManager.color(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
  }
  
  public static void glColor(Color color) {
    float red = color.getRed() / 255.0F;
    float green = color.getGreen() / 255.0F;
    float blue = color.getBlue() / 255.0F;
    float alpha = color.getAlpha() / 255.0F;
    GlStateManager.color(red, green, blue, alpha);
  }
  
  public static void glColor(Color color, int alpha) {
    glColor(color, alpha / 255.0F);
  }
  
  public static void glColor(Color color, float alpha) {
    float red = color.getRed() / 255.0F;
    float green = color.getGreen() / 255.0F;
    float blue = color.getBlue() / 255.0F;
    GlStateManager.color(red, green, blue, alpha);
  }
  
  public static void glColor(int hex, int alpha) {
    float red = (hex >> 16 & 0xFF) / 255.0F;
    float green = (hex >> 8 & 0xFF) / 255.0F;
    float blue = (hex & 0xFF) / 255.0F;
    GlStateManager.color(red, green, blue, alpha / 255.0F);
  }
  
  public static void glColor(int hex, float alpha) {
    float red = (hex >> 16 & 0xFF) / 255.0F;
    float green = (hex >> 8 & 0xFF) / 255.0F;
    float blue = (hex & 0xFF) / 255.0F;
    GlStateManager.color(red, green, blue, alpha);
  }
  
  public static void updateScaledResolution() {
    scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
  }
  
  public static ScaledResolution getScaledResolution() {
    return scaledResolution;
  }
  
  public static void prepareScissorBox(float x, float y, float x2, float y2) {
    updateScaledResolution();
    int factor = scaledResolution.getScaleFactor();
    GL11.glScissor((int)(x * factor), (int)((scaledResolution.getScaledHeight() - y2) * factor), (int)((x2 - x) * factor), (int)((y2 - y) * factor));
  }
  
  public static void drawOutlinedBox(AxisAlignedBB boundingBox) {
    if (boundingBox == null)
      return; 
    GL11.glBegin(3);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxZ, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxZ, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glEnd();
    GL11.glBegin(3);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxZ, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxZ, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glEnd();
    GL11.glBegin(1);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxZ, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxZ, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxZ, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxZ, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxZ, boundingBox.maxZ);
    GL11.glEnd();
  }
  
  public static void drawBox(AxisAlignedBB box) {
    GL11.glEnable(1537);
    if (box == null)
      return; 
    GL11.glBegin(7);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.minX, box.maxZ, box.maxZ);
    GL11.glVertex3d(box.maxZ, box.maxZ, box.maxZ);
    GL11.glEnd();
  }
  
  public static void filledBox(AxisAlignedBB bb, int color) {
    float var11 = (color >> 24 & 0xFF) / 255.0F;
    float var6 = (color >> 16 & 0xFF) / 255.0F;
    float var7 = (color >> 8 & 0xFF) / 255.0F;
    float var8 = (color & 0xFF) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(bb.minX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(bb.minX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(bb.minX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.minX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(var6, var7, var8, var11).endVertex();
    tessellator.draw();
  }
  
  private static double getDiff(double lastI, double i, float ticks, double ownI) {
    return lastI + (i - lastI) * ticks - ownI;
  }
  
  public static void drawBeacon(BlockPos pos, int color, int colorIn, float partialTicks) {
    GlStateManager.pushMatrix();
    EntityPlayerSP player = (Minecraft.getMinecraft()).thePlayer;
    double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
    double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
    double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
    GL11.glLineWidth(1.0F);
    AxisAlignedBB var11 = new AxisAlignedBB((pos.getX() + 1), pos.getY(), (pos.getZ() + 1), pos.getX(), (pos.getY() + 200), pos.getZ());
    AxisAlignedBB var12 = new AxisAlignedBB(var11.minX - x, var11.minY - y, var11.minZ - z, var11.maxX - x, var11.maxY - y, var11.maxZ - z);
    if (color != 0) {
      GlStateManager.disableDepth();
      filledBox(var12, colorIn);
      disableLighting();
      drawOutlinedBoundingBox(var12, color);
    } 
    GlStateManager.popMatrix();
  }
  
  public static void drawProgressBar(float x, float y, float width, float height, float progress, String progressName, int color) {
    float x1 = x - width, x2 = x + width, y1 = y - height, y2 = y + height;
    (Minecraft.getMinecraft()).fontRendererObj.drawStringWithShadow(progressName, x - (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(progressName) / 2.0F, y1 - (Minecraft.getMinecraft()).fontRendererObj.FONT_HEIGHT, -1);
    borderedRect(x1, y1, x2, y2, 0.5F, Color.BLACK.getRGB(), Color.BLACK.getRGB());
    borderedRect(x1, y1, x1 + width * 2.0F * progress * 0.01F, y2, 0.5F, color, Color.BLACK.getRGB());
  }
  
  public static void rect(float x1, float y1, float x2, float y2, int fill) {
    GlStateManager.color(0.0F, 0.0F, 0.0F);
    GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
    float f = (fill >> 24 & 0xFF) / 255.0F;
    float f1 = (fill >> 16 & 0xFF) / 255.0F;
    float f2 = (fill >> 8 & 0xFF) / 255.0F;
    float f3 = (fill & 0xFF) / 255.0F;
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glPushMatrix();
    GL11.glColor4f(f1, f2, f3, f);
    GL11.glBegin(7);
    GL11.glVertex2d(x2, y1);
    GL11.glVertex2d(x1, y1);
    GL11.glVertex2d(x1, y2);
    GL11.glVertex2d(x2, y2);
    GL11.glEnd();
    GL11.glPopMatrix();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glDisable(2848);
  }
  
  public static float lerp(float a, float b, float f) {
    return a + f * (b - a);
  }
  
  public static void rect(double x1, double y1, double x2, double y2, int fill) {
    GlStateManager.color(0.0F, 0.0F, 0.0F);
    GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
    float f = (fill >> 24 & 0xFF) / 255.0F;
    float f1 = (fill >> 16 & 0xFF) / 255.0F;
    float f2 = (fill >> 8 & 0xFF) / 255.0F;
    float f3 = (fill & 0xFF) / 255.0F;
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glPushMatrix();
    GL11.glColor4f(f1, f2, f3, f);
    GL11.glBegin(7);
    GL11.glVertex2d(x2, y1);
    GL11.glVertex2d(x1, y1);
    GL11.glVertex2d(x1, y2);
    GL11.glVertex2d(x2, y2);
    GL11.glEnd();
    GL11.glPopMatrix();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glDisable(2848);
  }
  
  public static void startClip(float x1, float y1, float x2, float y2) {
    if (y1 > y2) {
      float temp = y2;
      y2 = y1;
      y1 = temp;
    } 
    GL11.glScissor((int)x1, (int)(Display.getHeight() - y2), (int)(x2 - x1), (int)(y2 - y1));
    GL11.glEnable(3089);
  }
  
  public static void enableGL2D() {
    GL11.glDisable(2929);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glDepthMask(true);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GL11.glHint(3155, 4354);
  }
  
  public static void disableGL2D() {
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glEnable(2929);
    GL11.glDisable(2848);
    GL11.glHint(3154, 4352);
    GL11.glHint(3155, 4352);
  }
  
  public static void endClip() {
    GL11.glDisable(3089);
  }
  
  public static void renderOutlines(double x, double y, double z, float width, float height, Color c) {
    float halfwidth = width / 2.0F;
    float halfheight = height / 2.0F;
    GlStateManager.pushMatrix();
    GlStateManager.depthMask(false);
    GlStateManager.disableTexture2D();
    GlStateManager.disableLighting();
    GlStateManager.disableCull();
    GlStateManager.disableBlend();
    GlStateManager.disableDepth();
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
    y++;
    GL11.glLineWidth(1.2F);
    worldRenderer.pos(x - halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    tessellator.draw();
    GlStateManager.enableDepth();
    GlStateManager.depthMask(true);
    GlStateManager.enableTexture2D();
    GlStateManager.enableLighting();
    GlStateManager.enableCull();
    GlStateManager.enableBlend();
    GlStateManager.popMatrix();
  }
  
  public static void tracer(Entity entity, Color color) {
    RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
    double x = entity.posX - RenderManager.renderPosX;
    double y = entity.posY - RenderManager.renderPosY;
    double z = entity.posZ - RenderManager.renderPosZ;
    Vec3 eyeVector = (new Vec3(0.0D, 0.0D, 1.0D)).rotatePitch(-((float)Math.toRadians((Minecraft.getMinecraft()).thePlayer.rotationPitch))).rotateYaw(-((float)Math.toRadians((Minecraft.getMinecraft()).thePlayer.rotationYaw)));
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(3042);
    GL11.glLineWidth(2.0F);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    ColorUtils.glColor(color);
    GL11.glBegin(1);
    GL11.glVertex3d(eyeVector.xCoord, (Minecraft.getMinecraft()).thePlayer.getEyeHeight() + eyeVector.yCoord, eyeVector.zCoord);
    GL11.glVertex3d(x, y, z);
    GL11.glVertex3d(x, y, z);
    GL11.glVertex3d(x, y + entity.height, z);
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GlStateManager.resetColor();
  }
  
  public static void renderBoxWithOutline(double x, double y, double z, float width, float height, Color c) {
    renderBox(x, y, z, width, height, c);
    renderOutlines(x, y, z, width, height, c);
  }
  
  public static void ESP2D(EntityPlayer ep, double d, double d1, double d2) {
    float distance = (Minecraft.getMinecraft()).thePlayer.getDistanceToEntity((Entity)ep);
    float scale = (float)(0.09D + (Minecraft.getMinecraft()).thePlayer.getDistance(ep.posX, ep.posY, ep.posZ) / 10000.0D);
    GL11.glPushMatrix();
    GL11.glTranslatef((float)d, (float)d1, (float)d2);
    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
    GL11.glRotatef(-(Minecraft.getMinecraft().getRenderManager()).playerViewY, 0.0F, 1.0F, 0.0F);
    GL11.glScalef(-scale, -scale, scale);
    GL11.glDisable(2896);
    GL11.glDisable(2929);
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    outlineRect(-13.0F, -45.0F, 13.0F, 5.0F, -65536);
    GL11.glScaled(2.0D, 2.0D, 2.0D);
    GL11.glDisable(3042);
    GL11.glEnable(2929);
    GL11.glEnable(2896);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPopMatrix();
  }
  
  public static void outlineRect(float drawX, float drawY, float drawWidth, float drawHeight, int color) {
    rect(drawX, drawY, drawWidth, drawY + 0.5F, color);
    rect(drawX, drawY + 0.5F, drawX + 0.5F, drawHeight, color);
    rect(drawWidth - 0.5F, drawY + 0.5F, drawWidth, drawHeight - 0.5F, color);
    rect(drawX + 0.5F, drawHeight - 0.5F, drawWidth, drawHeight, color);
  }
  
  public static void borderedRect(float x2, float y2, float x1, float y1, int insideC, int borderC) {
    enableGL2D();
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    drawVLine(x2 *= 2.0F, y2 *= 2.0F, y1 *= 2.0F, borderC);
    drawVLine((x1 *= 2.0F) - 1.0F, y2, y1, borderC);
    drawHLine(x2, x1 - 1.0F, y2, borderC);
    drawHLine(x2, x1 - 2.0F, y1 - 1.0F, borderC);
    rect(x2 + 1.0F, y2 + 1.0F, x1 - 1.0F, y1 - 1.0F, insideC);
    GL11.glScalef(2.0F, 2.0F, 2.0F);
    disableGL2D();
  }
  
  public static void rectBorder(float x1, float y1, float x2, float y2, int outline) {
    rect(x1 + 1.0F, y2 - 1.0F, x2, y2, outline);
    rect(x1 + 1.0F, y1, x2, y1 + 1.0F, outline);
    rect(x1, y1, x1 + 1.0F, y2, outline);
    rect(x2 - 1.0F, y1 + 1.0F, x2, y2 - 1.0F, outline);
  }
  
  public static void renderBox(double x, double y, double z, float width, float height, Color c) {
    float halfwidth = width / 2.0F;
    float halfheight = height / 2.0F;
    GlStateManager.pushMatrix();
    GlStateManager.depthMask(false);
    GlStateManager.disableTexture2D();
    GlStateManager.disableLighting();
    GlStateManager.disableCull();
    GlStateManager.disableBlend();
    GlStateManager.disableDepth();
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    y++;
    worldRenderer.pos(x - halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y + halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x + halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z + halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    worldRenderer.pos(x - halfwidth, y - halfheight, z - halfwidth).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
    tessellator.draw();
    GlStateManager.enableDepth();
    GlStateManager.depthMask(true);
    GlStateManager.enableTexture2D();
    GlStateManager.enableLighting();
    GlStateManager.enableCull();
    GlStateManager.enableBlend();
    GlStateManager.popMatrix();
  }
  
  public static void outlineEntity(AxisAlignedBB axisalignedbb, float width, float red, float green, float blue, float alpha) {
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(2896);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glLineWidth(width);
    GL11.glColor4f(red, green, blue, alpha);
    drawOutlinedBox(axisalignedbb);
    GL11.glLineWidth(1.0F);
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glEnable(2896);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
  }
  
  public static void borderedCircle(int x, int y, float radius, int outsideC, int insideC) {
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glPushMatrix();
    float scale = 0.1F;
    GL11.glScalef(scale, scale, scale);
    x = (int)(x * 1.0F / scale);
    y = (int)(y * 1.0F / scale);
    radius *= 1.0F / scale;
    circle(x, y, radius, insideC);
    unfilledCircle(x, y, radius, 1.0F, outsideC);
    GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
    GL11.glPopMatrix();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glDisable(2848);
  }
  
  public static void borderedCircle(float x, float y, float radius, int outsideC, int insideC) {
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glPushMatrix();
    float scale = 0.1F;
    GL11.glScalef(scale, scale, scale);
    x = (int)(x * 1.0F / scale);
    y = (int)(y * 1.0F / scale);
    radius *= 1.0F / scale;
    circle(x, y, radius, insideC);
    unfilledCircle(x, y, radius, 1.0F, outsideC);
    GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
    GL11.glPopMatrix();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glDisable(2848);
  }
  
  public static void unfilledCircle(int x, int y, float radius, float lineWidth, int color) {
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
    GL11.glLineWidth(lineWidth);
    GL11.glEnable(2848);
    GL11.glBegin(2);
    for (int i = 0; i <= 360; i++)
      GL11.glVertex2d(x + Math.sin(i * 3.141526D / 180.0D) * radius, y + Math.cos(i * 3.141526D / 180.0D) * radius); 
    GL11.glEnd();
    GL11.glDisable(2848);
  }
  
  public static void unfilledCircle(double x, double y, float radius, float lineWidth, int color) {
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
    GL11.glLineWidth(lineWidth);
    GL11.glEnable(2848);
    GL11.glBegin(2);
    for (int i = 0; i <= 360; i++)
      GL11.glVertex2d(x + Math.sin(i * 3.141526D / 180.0D) * radius, y + Math.cos(i * 3.141526D / 180.0D) * radius); 
    GL11.glEnd();
    GL11.glDisable(2848);
  }
  
  public static void circle(int x, int y, float radius, int color) {
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
    GL11.glBegin(9);
    for (int i = 0; i <= 360; i++)
      GL11.glVertex2d(x + Math.sin(i * 3.141526D / 180.0D) * radius, y + Math.cos(i * 3.141526D / 180.0D) * radius); 
    GL11.glEnd();
  }
  
  public static void filledBBESP(AxisAlignedBB axisalignedbb, int color) {
    GL11.glPushMatrix();
    float red = (color >> 24 & 0xFF) / 255.0F;
    float green = (color >> 16 & 0xFF) / 255.0F;
    float blue = (color >> 8 & 0xFF) / 255.0F;
    float alpha = (color & 0xFF) / 255.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(2896);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glColor4f(red, green, blue, alpha);
    filledBox(axisalignedbb);
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glEnable(2896);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GL11.glPopMatrix();
  }
  
  public static void filledBox(AxisAlignedBB boundingBox) {
    if (boundingBox == null)
      return; 
    GL11.glBegin(7);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
    GL11.glEnd();
  }
  
  public static void boundingBoxESP(AxisAlignedBB axisalignedbb, float width, int color) {
    GL11.glPushMatrix();
    float red = (color >> 24 & 0xFF) / 255.0F;
    float green = (color >> 16 & 0xFF) / 255.0F;
    float blue = (color >> 8 & 0xFF) / 255.0F;
    float alpha = (color & 0xFF) / 255.0F;
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(2896);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glLineWidth(width);
    GL11.glColor4f(red, green, blue, alpha);
    drawOutlinedBox(axisalignedbb);
    GL11.glLineWidth(1.0F);
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glEnable(2896);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GL11.glPopMatrix();
  }
  
  public static void borderedCorneredRect(float x, float y, float x2, float y2, float lineWidth, int lineColor, int bgColor) {
    rect(x, y, x2, y2, bgColor);
    rect(x - 1.0F, y - 1.0F, x2 + 1.0F, y, lineColor);
    rect(x - 1.0F, y, x, y2, lineColor);
    rect(x - 1.0F, y2, x2 + 1.0F, y2 + 1.0F, lineColor);
    rect(x2, y, x2 + 1.0F, y2, lineColor);
  }
  
  public static ScaledResolution getScaledRes() {
    return new ScaledResolution(Minecraft.getMinecraft());
  }
  
  public static void texture(double x, double y, double imageWidth, double imageHeight, double maxWidth, double maxHeight, float alpha) {
    GL11.glPushMatrix();
    double sizeWidth = maxWidth / imageWidth;
    double sizeHeight = maxHeight / imageHeight;
    GL11.glScaled(sizeWidth, sizeHeight, 0.0D);
    if (alpha <= 1.0F) {
      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
    } 
    texturedModalRect(x / sizeWidth, y / sizeHeight, x / sizeWidth + imageWidth, y / sizeHeight + imageHeight);
    if (alpha <= 1.0F) {
      GlStateManager.disableAlpha();
      GlStateManager.disableBlend();
    } 
    GL11.glPopMatrix();
  }
  
  public static void rawTexture(double x, double y, double imageWidth, double imageHeight, double maxWidth, double maxHeight) {
    GL11.glPushMatrix();
    double sizeWidth = maxWidth / imageWidth;
    double sizeHeight = maxHeight / imageHeight;
    GL11.glScaled(sizeWidth, sizeHeight, 0.0D);
    texturedModalRect(x / sizeWidth, y / sizeHeight, x / sizeWidth + imageWidth, y / sizeHeight + imageHeight);
    GL11.glPopMatrix();
  }
  
  public static void texture(double x, double y, double imageWidth, double imageHeight, double maxWidth, double maxHeight) {
    texture(x, y, imageWidth, imageHeight, maxWidth, maxHeight, 1.0F);
  }
  
  public static void texture(double x, double y, double texturePosX, double texturePosY, double imageWidth, double imageHeight, double maxWidth, double maxHeight, float alpha) {
    GL11.glPushMatrix();
    double sizeWidth = maxWidth / imageWidth;
    double sizeHeight = maxHeight / imageHeight;
    GL11.glScaled(sizeWidth, sizeHeight, 0.0D);
    if (alpha <= 1.0F) {
      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
    } 
    drawUVTexture(x / sizeWidth, y / sizeHeight, texturePosX, texturePosY, x / sizeWidth + imageWidth - x / sizeWidth, y / sizeHeight + imageHeight - y / sizeHeight);
    if (alpha <= 1.0F) {
      GlStateManager.disableAlpha();
      GlStateManager.disableBlend();
    } 
    GL11.glPopMatrix();
  }
  
  private static void drawUVTexture(double x, double y, double textureX, double textureY, double width, double height) {
    float f = 0.00390625F;
    float f1 = 0.00390625F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldrenderer.pos(x + 0.0D, y + height, 0.0D).tex(((float)(textureX + 0.0D) * f), ((float)(textureY + height) * f1)).endVertex();
    worldrenderer.pos(x + width, y + height, 0.0D).tex(((float)(textureX + width) * f), ((float)(textureY + height) * f1)).endVertex();
    worldrenderer.pos(x + width, y + 0.0D, 0.0D).tex(((float)(textureX + width) * f), ((float)(textureY + 0.0D) * f1)).endVertex();
    worldrenderer.pos(x + 0.0D, y + 0.0D, 0.0D).tex(((float)(textureX + 0.0D) * f), ((float)(textureY + 0.0D) * f1)).endVertex();
    tessellator.draw();
  }
  
  public static void texture(double x, double y, double texturePosX, double texturePosY, double imageWidth, double imageHeight, double maxWidth, double maxHeight) {
    texture(x, y, texturePosX, texturePosY, imageWidth, imageHeight, maxWidth, maxHeight, 1.0F);
  }
  
  public static void texturedModalRect(double left, double top, double right, double bottom) {
    double textureX = 0.0D;
    double textureY = 0.0D;
    double width = right - left;
    double height = bottom - top;
    float f = 0.00390625F;
    float f1 = 0.00390625F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldrenderer.pos(left + 0.0D, top + height, 0.0D).tex(((float)(textureX + 0.0D) * f), ((float)(textureY + height) * f1)).endVertex();
    worldrenderer.pos(left + width, top + height, 0.0D).tex(((float)(textureX + width) * f), ((float)(textureY + height) * f1)).endVertex();
    worldrenderer.pos(left + width, top + 0.0D, 0.0D).tex(((float)(textureX + width) * f), ((float)(textureY + 0.0D) * f1)).endVertex();
    worldrenderer.pos(left + 0.0D, top + 0.0D, 0.0D).tex(((float)(textureX + 0.0D) * f), ((float)(textureY + 0.0D) * f1)).endVertex();
    tessellator.draw();
  }
  
  public static void image(ResourceLocation image, double x, double y, double width, double height, float opacity) {
    image(image, (int)x, (int)y, (int)width, (int)height, opacity);
  }
  
  public static void image(ResourceLocation image, int x, int y, int width, int height, float opacity) {
    GL11.glPushMatrix();
    GL11.glDisable(2929);
    GL11.glEnable(3042);
    GL11.glDepthMask(false);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, opacity / 255.0F);
    Minecraft.getMinecraft().getTextureManager().bindTexture(image);
    GlStateManager.glTexParameteri(3553, 10240, 9729);
    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GL11.glEnable(2929);
    GL11.glPopMatrix();
  }
  
  public static void drawGlowingRect(float x, float y, float width, float height, int color) {
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glPushMatrix();
    ColorUtils.glColor(color);
    GL11.glShadeModel(7425);
    GL11.glBegin(7);
    GL11.glVertex2d(width, y);
    GL11.glVertex2d(x, y);
    GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
    GL11.glVertex2d(x, (height * 2.0F));
    GL11.glVertex2d(width, (height * 2.0F));
    GL11.glEnd();
    GL11.glShadeModel(7424);
    GL11.glPopMatrix();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glDisable(2848);
    GL11.glPopMatrix();
  }
  
  private static final HashMap<Integer, Integer> shadowCache = Maps.newHashMap();
  
  public static void scissor(double x, double y, double width, double height) {
    ScaledResolution sr = new ScaledResolution(mc);
    double scale = sr.getScaleFactor();
    y = sr.getScaledHeight() - y;
    x *= scale;
    y *= scale;
    width *= scale;
    height *= scale;
    GL11.glScissor((int)x, (int)(y - height), (int)width, (int)height);
  }
  
  public static void drawBlurredShadow(int x, int y, int width, int height, int blurRadius, Color color) {
    GlStateManager.alphaFunc(516, 0.01F);
    width += blurRadius * 2;
    height += blurRadius * 2;
    x -= blurRadius;
    y -= blurRadius;
    float _X = x - 0.25F;
    float _Y = y + 0.25F;
    int identifier = width * height + width + color.hashCode() * blurRadius + blurRadius;
    GL11.glEnable(3553);
    GL11.glDisable(2884);
    GL11.glEnable(3008);
    GL11.glEnable(3042);
    if (shadowCache.containsKey(Integer.valueOf(identifier))) {
      int texId = ((Integer)shadowCache.get(Integer.valueOf(identifier))).intValue();
      GlStateManager.bindTexture(texId);
    } else {
      BufferedImage original = new BufferedImage(width, height, 2);
      Graphics g = original.getGraphics();
      g.setColor(color);
      g.fillRect(blurRadius, blurRadius, width - blurRadius * 2, height - blurRadius * 2);
      g.dispose();
      GaussianFilter op = new GaussianFilter(blurRadius);
      BufferedImage blurred = op.filter(original, null);
      int texId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false);
      shadowCache.put(Integer.valueOf(identifier), Integer.valueOf(texId));
    } 
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glBegin(7);
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex2f(_X, _Y);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex2f(_X, _Y + height);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex2f(_X + width, _Y + height);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex2f(_X + width, _Y);
    GL11.glEnd();
    GL11.glDisable(3553);
  }
  
  public static void drawHead3(ResourceLocation skin, int x, int y, int width, int height) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
    Gui.drawScaledCustomSizeModalRect(x, y, 8.0F, 8.0F, 8, 8, width, height, 64.0F, 64.0F);
  }
  
  public static void drawHead(ResourceLocation skin, int width, int height) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
    Gui.drawScaledCustomSizeModalRect(width, height, 8.0F, 8.0F, 8, 8, 37, 37, 64.0F, 64.0F);
  }
  
  public static void drawHead1(ResourceLocation skin, int width, int height) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
    Gui.drawScaledCustomSizeModalRect(width, height, 8.0F, 8.0F, 8, 8, 48, 48, 64.0F, 64.0F);
  }
  
  public static void drawHead3(ResourceLocation skin, int width, int height) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
    Gui.drawScaledCustomSizeModalRect(width, height, 8.0F, 8.0F, 8, 8, 31, 31, 64.0F, 64.0F);
  }
  
  public static void drawShadow(float x, float y, float width, float height, int radius, boolean glow) {
    drawTexturedRect(x - radius, y - radius, radius, radius, Shadows.PANEL_TOP_LEFT, glow);
    drawTexturedRect(x - radius, y + height, radius, radius, Shadows.PANEL_BOTTOM_LEFT, glow);
    drawTexturedRect(x + width, y + height, radius, radius, Shadows.PANEL_BOTTOM_RIGHT, glow);
    drawTexturedRect(x + width, y - radius, radius, radius, Shadows.PANEL_TOP_RIGHT, glow);
    drawTexturedRect(x - radius, y, radius, height, Shadows.PANEL_LEFT, glow);
    drawTexturedRect(x + width, y, radius, height, Shadows.PANEL_RIGHT, glow);
    drawTexturedRect(x, y - radius, width, radius, Shadows.PANEL_TOP, glow);
    drawTexturedRect(x, y + height, width, radius, Shadows.PANEL_BOTTOM, glow);
  }
  
  public static void drawTexturedRect(float x, float y, float width, float height, Shadows shadow, boolean glow) {
    String text = glow ? "glow" : "";
    GL11.glPushMatrix();
    boolean enableBlend = GL11.glIsEnabled(3042);
    boolean disableAlpha = !GL11.glIsEnabled(3008);
    if (!enableBlend)
      GL11.glEnable(3042); 
    if (!disableAlpha)
      GL11.glDisable(3008); 
    mc.getTextureManager().bindTexture(new ResourceLocation("zypux/textures/shadow/" + text + shadow.getToText() + ".png"));
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
    if (!enableBlend)
      GL11.glDisable(3042); 
    if (!disableAlpha)
      GL11.glEnable(3008); 
    GL11.glPopMatrix();
  }
  
  public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
    float f = 1.0F / textureWidth;
    float f1 = 1.0F / textureHeight;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldrenderer.pos(x, (y + height), 0.0D).tex((u * f), ((v + height) * f1)).endVertex();
    worldrenderer.pos((x + width), (y + height), 0.0D).tex(((u + width) * f), ((v + height) * f1)).endVertex();
    worldrenderer.pos((x + width), y, 0.0D).tex(((u + width) * f), (v * f1)).endVertex();
    worldrenderer.pos(x, y, 0.0D).tex((u * f), (v * f1)).endVertex();
    tessellator.draw();
  }
}
