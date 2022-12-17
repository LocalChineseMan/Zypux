package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.optifine.CustomColors;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lagometer;
import net.optifine.RandomEntities;
import net.optifine.SmartAnimations;
import net.optifine.model.BlockModelUtils;
import net.optifine.reflect.Reflector;
import net.optifine.render.ChunkVisibility;
import net.optifine.render.CloudRenderer;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.shaders.ShadowUtils;
import net.optifine.shaders.gui.GuiShaderOptions;
import net.optifine.util.ChunkUtils;
import net.optifine.util.RenderChunkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class RenderGlobal implements IWorldAccess, IResourceManagerReloadListener {
  private static final Logger logger = LogManager.getLogger();
  
  private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
  
  private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
  
  private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
  
  private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
  
  private static final ResourceLocation locationForcefieldPng = new ResourceLocation("textures/misc/forcefield.png");
  
  public final Minecraft mc;
  
  private final TextureManager renderEngine;
  
  private final RenderManager renderManager;
  
  private WorldClient theWorld;
  
  private Set<RenderChunk> chunksToUpdate = Sets.newLinkedHashSet();
  
  private List<ContainerLocalRenderInformation> renderInfos = Lists.newArrayListWithCapacity(69696);
  
  private final Set<TileEntity> setTileEntities = Sets.newHashSet();
  
  private ViewFrustum viewFrustum;
  
  private int starGLCallList = -1;
  
  private int glSkyList = -1;
  
  private int glSkyList2 = -1;
  
  private VertexFormat vertexBufferFormat;
  
  private VertexBuffer starVBO;
  
  private VertexBuffer skyVBO;
  
  private VertexBuffer sky2VBO;
  
  private int cloudTickCounter;
  
  public final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.newHashMap();
  
  private final Map<BlockPos, ISound> mapSoundPositions = Maps.newHashMap();
  
  private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
  
  private Framebuffer entityOutlineFramebuffer;
  
  private ShaderGroup entityOutlineShader;
  
  private double frustumUpdatePosX = Double.MIN_VALUE;
  
  private double frustumUpdatePosY = Double.MIN_VALUE;
  
  private double frustumUpdatePosZ = Double.MIN_VALUE;
  
  private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
  
  private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
  
  private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
  
  private double lastViewEntityX = Double.MIN_VALUE;
  
  private double lastViewEntityY = Double.MIN_VALUE;
  
  private double lastViewEntityZ = Double.MIN_VALUE;
  
  private double lastViewEntityPitch = Double.MIN_VALUE;
  
  private double lastViewEntityYaw = Double.MIN_VALUE;
  
  private final ChunkRenderDispatcher renderDispatcher = new ChunkRenderDispatcher();
  
  private ChunkRenderContainer renderContainer;
  
  private int renderDistanceChunks = -1;
  
  private int renderEntitiesStartupCounter = 2;
  
  private int countEntitiesTotal;
  
  private int countEntitiesRendered;
  
  private int countEntitiesHidden;
  
  private boolean debugFixTerrainFrustum = false;
  
  private ClippingHelper debugFixedClippingHelper;
  
  private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
  
  private final Vector3d debugTerrainFrustumPosition = new Vector3d();
  
  private boolean vboEnabled = false;
  
  IRenderChunkFactory renderChunkFactory;
  
  private double prevRenderSortX;
  
  private double prevRenderSortY;
  
  private double prevRenderSortZ;
  
  public boolean displayListEntitiesDirty = true;
  
  private CloudRenderer cloudRenderer;
  
  public Entity renderedEntity;
  
  public Set chunksToResortTransparency = new LinkedHashSet();
  
  public Set chunksToUpdateForced = new LinkedHashSet();
  
  private Deque visibilityDeque = new ArrayDeque();
  
  private List renderInfosEntities = new ArrayList(1024);
  
  private List renderInfosTileEntities = new ArrayList(1024);
  
  private List renderInfosNormal = new ArrayList(1024);
  
  private List renderInfosEntitiesNormal = new ArrayList(1024);
  
  private List renderInfosTileEntitiesNormal = new ArrayList(1024);
  
  private List renderInfosShadow = new ArrayList(1024);
  
  private List renderInfosEntitiesShadow = new ArrayList(1024);
  
  private List renderInfosTileEntitiesShadow = new ArrayList(1024);
  
  private int renderDistance = 0;
  
  private int renderDistanceSq = 0;
  
  private static final Set SET_ALL_FACINGS = Collections.unmodifiableSet(new HashSet(Arrays.asList((Object[])EnumFacing.VALUES)));
  
  private int countTileEntitiesRendered;
  
  private IChunkProvider worldChunkProvider = null;
  
  private LongHashMap worldChunkProviderMap = null;
  
  private int countLoadedChunksPrev = 0;
  
  private RenderEnv renderEnv = new RenderEnv(Blocks.air.getDefaultState(), new BlockPos(0, 0, 0));
  
  public boolean renderOverlayDamaged = false;
  
  public boolean renderOverlayEyes = false;
  
  private boolean firstWorldLoad = false;
  
  private static int renderEntitiesCounter = 0;
  
  public RenderGlobal(Minecraft mcIn) {
    this.cloudRenderer = new CloudRenderer(mcIn);
    this.mc = mcIn;
    this.renderManager = mcIn.getRenderManager();
    this.renderEngine = mcIn.getTextureManager();
    this.renderEngine.bindTexture(locationForcefieldPng);
    GL11.glTexParameteri(3553, 10242, 10497);
    GL11.glTexParameteri(3553, 10243, 10497);
    GlStateManager.bindTexture(0);
    updateDestroyBlockIcons();
    this.vboEnabled = OpenGlHelper.useVbo();
    if (this.vboEnabled) {
      this.renderContainer = new VboRenderList();
      this.renderChunkFactory = (IRenderChunkFactory)new VboChunkFactory();
    } else {
      this.renderContainer = new RenderList();
      this.renderChunkFactory = (IRenderChunkFactory)new ListChunkFactory();
    } 
    this.vertexBufferFormat = new VertexFormat();
    this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
    generateStars();
    generateSky();
    generateSky2();
  }
  
  public void onResourceManagerReload(IResourceManager resourceManager) {
    updateDestroyBlockIcons();
  }
  
  private void updateDestroyBlockIcons() {
    TextureMap texturemap = this.mc.getTextureMapBlocks();
    for (int i = 0; i < this.destroyBlockIcons.length; i++)
      this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i); 
  }
  
  public void makeEntityOutlineShader() {
    if (OpenGlHelper.shadersSupported) {
      if (ShaderLinkHelper.getStaticShaderLinkHelper() == null)
        ShaderLinkHelper.setNewStaticShaderLinkHelper(); 
      ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");
      try {
        this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocation);
        this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
        this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
      } catch (IOException ioexception) {
        logger.warn("Failed to load shader: " + resourcelocation, ioexception);
        this.entityOutlineShader = null;
        this.entityOutlineFramebuffer = null;
      } catch (JsonSyntaxException jsonsyntaxexception) {
        logger.warn("Failed to load shader: " + resourcelocation, (Throwable)jsonsyntaxexception);
        this.entityOutlineShader = null;
        this.entityOutlineFramebuffer = null;
      } 
    } else {
      this.entityOutlineShader = null;
      this.entityOutlineFramebuffer = null;
    } 
  }
  
  public void renderEntityOutlineFramebuffer() {
    if (isRenderEntityOutlines()) {
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
      this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
      GlStateManager.disableBlend();
    } 
  }
  
  protected boolean isRenderEntityOutlines() {
    return (!Config.isFastRender() && !Config.isShaders() && !Config.isAntialiasing()) ? ((this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.thePlayer != null && this.mc.thePlayer.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown())) : false;
  }
  
  private void generateSky2() {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    if (this.sky2VBO != null)
      this.sky2VBO.deleteGlBuffers(); 
    if (this.glSkyList2 >= 0) {
      GLAllocation.deleteDisplayLists(this.glSkyList2);
      this.glSkyList2 = -1;
    } 
    if (this.vboEnabled) {
      this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
      renderSky(worldrenderer, -16.0F, true);
      worldrenderer.finishDrawing();
      worldrenderer.reset();
      this.sky2VBO.bufferData(worldrenderer.getByteBuffer());
    } else {
      this.glSkyList2 = GLAllocation.generateDisplayLists(1);
      GL11.glNewList(this.glSkyList2, 4864);
      renderSky(worldrenderer, -16.0F, true);
      tessellator.draw();
      GL11.glEndList();
    } 
  }
  
  private void generateSky() {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    if (this.skyVBO != null)
      this.skyVBO.deleteGlBuffers(); 
    if (this.glSkyList >= 0) {
      GLAllocation.deleteDisplayLists(this.glSkyList);
      this.glSkyList = -1;
    } 
    if (this.vboEnabled) {
      this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
      renderSky(worldrenderer, 16.0F, false);
      worldrenderer.finishDrawing();
      worldrenderer.reset();
      this.skyVBO.bufferData(worldrenderer.getByteBuffer());
    } else {
      this.glSkyList = GLAllocation.generateDisplayLists(1);
      GL11.glNewList(this.glSkyList, 4864);
      renderSky(worldrenderer, 16.0F, false);
      tessellator.draw();
      GL11.glEndList();
    } 
  }
  
  private void renderSky(WorldRenderer worldRendererIn, float posY, boolean reverseX) {
    int i = 64;
    int j = 6;
    worldRendererIn.begin(7, DefaultVertexFormats.POSITION);
    int k = (this.renderDistance / 64 + 1) * 64 + 64;
    for (int l = -k; l <= k; l += 64) {
      for (int i1 = -k; i1 <= k; i1 += 64) {
        float f = l;
        float f1 = (l + 64);
        if (reverseX) {
          f1 = l;
          f = (l + 64);
        } 
        worldRendererIn.pos(f, posY, i1).endVertex();
        worldRendererIn.pos(f1, posY, i1).endVertex();
        worldRendererIn.pos(f1, posY, (i1 + 64)).endVertex();
        worldRendererIn.pos(f, posY, (i1 + 64)).endVertex();
      } 
    } 
  }
  
  private void generateStars() {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    if (this.starVBO != null)
      this.starVBO.deleteGlBuffers(); 
    if (this.starGLCallList >= 0) {
      GLAllocation.deleteDisplayLists(this.starGLCallList);
      this.starGLCallList = -1;
    } 
    if (this.vboEnabled) {
      this.starVBO = new VertexBuffer(this.vertexBufferFormat);
      renderStars(worldrenderer);
      worldrenderer.finishDrawing();
      worldrenderer.reset();
      this.starVBO.bufferData(worldrenderer.getByteBuffer());
    } else {
      this.starGLCallList = GLAllocation.generateDisplayLists(1);
      GlStateManager.pushMatrix();
      GL11.glNewList(this.starGLCallList, 4864);
      renderStars(worldrenderer);
      tessellator.draw();
      GL11.glEndList();
      GlStateManager.popMatrix();
    } 
  }
  
  private void renderStars(WorldRenderer worldRendererIn) {
    Random random = new Random(10842L);
    worldRendererIn.begin(7, DefaultVertexFormats.POSITION);
    for (int i = 0; i < 1500; i++) {
      double d0 = (random.nextFloat() * 2.0F - 1.0F);
      double d1 = (random.nextFloat() * 2.0F - 1.0F);
      double d2 = (random.nextFloat() * 2.0F - 1.0F);
      double d3 = (0.15F + random.nextFloat() * 0.1F);
      double d4 = d0 * d0 + d1 * d1 + d2 * d2;
      if (d4 < 1.0D && d4 > 0.01D) {
        d4 = 1.0D / Math.sqrt(d4);
        d0 *= d4;
        d1 *= d4;
        d2 *= d4;
        double d5 = d0 * 100.0D;
        double d6 = d1 * 100.0D;
        double d7 = d2 * 100.0D;
        double d8 = Math.atan2(d0, d2);
        double d9 = Math.sin(d8);
        double d10 = Math.cos(d8);
        double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
        double d12 = Math.sin(d11);
        double d13 = Math.cos(d11);
        double d14 = random.nextDouble() * Math.PI * 2.0D;
        double d15 = Math.sin(d14);
        double d16 = Math.cos(d14);
        for (int j = 0; j < 4; j++) {
          double d17 = 0.0D;
          double d18 = ((j & 0x2) - 1) * d3;
          double d19 = ((j + 1 & 0x2) - 1) * d3;
          double d20 = 0.0D;
          double d21 = d18 * d16 - d19 * d15;
          double d22 = d19 * d16 + d18 * d15;
          double d23 = d21 * d12 + 0.0D * d13;
          double d24 = 0.0D * d12 - d21 * d13;
          double d25 = d24 * d9 - d22 * d10;
          double d26 = d22 * d9 + d24 * d10;
          worldRendererIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
        } 
      } 
    } 
  }
  
  public void setWorldAndLoadRenderers(WorldClient worldClientIn) {
    if (this.theWorld != null)
      this.theWorld.removeWorldAccess(this); 
    this.frustumUpdatePosX = Double.MIN_VALUE;
    this.frustumUpdatePosY = Double.MIN_VALUE;
    this.frustumUpdatePosZ = Double.MIN_VALUE;
    this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
    this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
    this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
    this.renderManager.set((World)worldClientIn);
    this.theWorld = worldClientIn;
    if (Config.isDynamicLights())
      DynamicLights.clear(); 
    ChunkVisibility.reset();
    this.worldChunkProvider = null;
    this.worldChunkProviderMap = null;
    this.renderEnv.reset((IBlockState)null, (BlockPos)null);
    Shaders.checkWorldChanged((World)this.theWorld);
    if (worldClientIn != null) {
      worldClientIn.addWorldAccess(this);
      loadRenderers();
    } else {
      this.chunksToUpdate.clear();
      clearRenderInfos();
      if (this.viewFrustum != null)
        this.viewFrustum.deleteGlResources(); 
      this.viewFrustum = null;
    } 
  }
  
  public void loadRenderers() {
    if (this.theWorld != null) {
      this.displayListEntitiesDirty = true;
      Blocks.leaves.setGraphicsLevel(Config.isTreesFancy());
      Blocks.leaves2.setGraphicsLevel(Config.isTreesFancy());
      BlockModelRenderer.updateAoLightValue();
      if (Config.isDynamicLights())
        DynamicLights.clear(); 
      SmartAnimations.update();
      this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
      this.renderDistance = this.renderDistanceChunks * 16;
      this.renderDistanceSq = this.renderDistance * this.renderDistance;
      boolean flag = this.vboEnabled;
      this.vboEnabled = OpenGlHelper.useVbo();
      if (flag && !this.vboEnabled) {
        this.renderContainer = new RenderList();
        this.renderChunkFactory = (IRenderChunkFactory)new ListChunkFactory();
      } else if (!flag && this.vboEnabled) {
        this.renderContainer = new VboRenderList();
        this.renderChunkFactory = (IRenderChunkFactory)new VboChunkFactory();
      } 
      generateStars();
      generateSky();
      generateSky2();
      if (this.viewFrustum != null)
        this.viewFrustum.deleteGlResources(); 
      stopChunkUpdates();
      synchronized (this.setTileEntities) {
        this.setTileEntities.clear();
      } 
      this.viewFrustum = new ViewFrustum((World)this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
      if (this.theWorld != null) {
        Entity entity = this.mc.getRenderViewEntity();
        if (entity != null)
          this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ); 
      } 
      this.renderEntitiesStartupCounter = 2;
    } 
    if (this.mc.thePlayer == null)
      this.firstWorldLoad = true; 
  }
  
  protected void stopChunkUpdates() {
    this.chunksToUpdate.clear();
    this.renderDispatcher.stopChunkUpdates();
  }
  
  public void createBindEntityOutlineFbs(int width, int height) {
    if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null)
      this.entityOutlineShader.createBindFramebuffers(width, height); 
  }
  
  public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
    // Byte code:
    //   0: iconst_0
    //   1: istore #4
    //   3: getstatic net/optifine/reflect/Reflector.MinecraftForgeClient_getRenderPass : Lnet/optifine/reflect/ReflectorMethod;
    //   6: invokevirtual exists : ()Z
    //   9: ifeq -> 24
    //   12: getstatic net/optifine/reflect/Reflector.MinecraftForgeClient_getRenderPass : Lnet/optifine/reflect/ReflectorMethod;
    //   15: iconst_0
    //   16: anewarray java/lang/Object
    //   19: invokestatic callInt : (Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)I
    //   22: istore #4
    //   24: aload_0
    //   25: getfield renderEntitiesStartupCounter : I
    //   28: ifle -> 50
    //   31: iload #4
    //   33: ifle -> 37
    //   36: return
    //   37: aload_0
    //   38: dup
    //   39: getfield renderEntitiesStartupCounter : I
    //   42: iconst_1
    //   43: isub
    //   44: putfield renderEntitiesStartupCounter : I
    //   47: goto -> 2069
    //   50: aload_1
    //   51: getfield prevPosX : D
    //   54: aload_1
    //   55: getfield posX : D
    //   58: aload_1
    //   59: getfield prevPosX : D
    //   62: dsub
    //   63: fload_3
    //   64: f2d
    //   65: dmul
    //   66: dadd
    //   67: dstore #5
    //   69: aload_1
    //   70: getfield prevPosY : D
    //   73: aload_1
    //   74: getfield posY : D
    //   77: aload_1
    //   78: getfield prevPosY : D
    //   81: dsub
    //   82: fload_3
    //   83: f2d
    //   84: dmul
    //   85: dadd
    //   86: dstore #7
    //   88: aload_1
    //   89: getfield prevPosZ : D
    //   92: aload_1
    //   93: getfield posZ : D
    //   96: aload_1
    //   97: getfield prevPosZ : D
    //   100: dsub
    //   101: fload_3
    //   102: f2d
    //   103: dmul
    //   104: dadd
    //   105: dstore #9
    //   107: aload_0
    //   108: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   111: getfield theProfiler : Lnet/minecraft/profiler/Profiler;
    //   114: ldc_w 'prepare'
    //   117: invokevirtual startSection : (Ljava/lang/String;)V
    //   120: getstatic net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.instance : Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;
    //   123: aload_0
    //   124: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   127: aload_0
    //   128: getfield mc : Lnet/minecraft/client/Minecraft;
    //   131: invokevirtual getTextureManager : ()Lnet/minecraft/client/renderer/texture/TextureManager;
    //   134: aload_0
    //   135: getfield mc : Lnet/minecraft/client/Minecraft;
    //   138: getfield fontRendererObj : Lnet/minecraft/client/gui/FontRenderer;
    //   141: aload_0
    //   142: getfield mc : Lnet/minecraft/client/Minecraft;
    //   145: invokevirtual getRenderViewEntity : ()Lnet/minecraft/entity/Entity;
    //   148: fload_3
    //   149: invokevirtual cacheActiveRenderInfo : (Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/entity/Entity;F)V
    //   152: aload_0
    //   153: getfield renderManager : Lnet/minecraft/client/renderer/entity/RenderManager;
    //   156: aload_0
    //   157: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   160: aload_0
    //   161: getfield mc : Lnet/minecraft/client/Minecraft;
    //   164: getfield fontRendererObj : Lnet/minecraft/client/gui/FontRenderer;
    //   167: aload_0
    //   168: getfield mc : Lnet/minecraft/client/Minecraft;
    //   171: invokevirtual getRenderViewEntity : ()Lnet/minecraft/entity/Entity;
    //   174: aload_0
    //   175: getfield mc : Lnet/minecraft/client/Minecraft;
    //   178: getfield pointedEntity : Lnet/minecraft/entity/Entity;
    //   181: aload_0
    //   182: getfield mc : Lnet/minecraft/client/Minecraft;
    //   185: getfield gameSettings : Lnet/minecraft/client/settings/GameSettings;
    //   188: fload_3
    //   189: invokevirtual cacheActiveRenderInfo : (Lnet/minecraft/world/World;Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Lnet/minecraft/client/settings/GameSettings;F)V
    //   192: getstatic net/minecraft/client/renderer/RenderGlobal.renderEntitiesCounter : I
    //   195: iconst_1
    //   196: iadd
    //   197: putstatic net/minecraft/client/renderer/RenderGlobal.renderEntitiesCounter : I
    //   200: iload #4
    //   202: ifne -> 225
    //   205: aload_0
    //   206: iconst_0
    //   207: putfield countEntitiesTotal : I
    //   210: aload_0
    //   211: iconst_0
    //   212: putfield countEntitiesRendered : I
    //   215: aload_0
    //   216: iconst_0
    //   217: putfield countEntitiesHidden : I
    //   220: aload_0
    //   221: iconst_0
    //   222: putfield countTileEntitiesRendered : I
    //   225: aload_0
    //   226: getfield mc : Lnet/minecraft/client/Minecraft;
    //   229: invokevirtual getRenderViewEntity : ()Lnet/minecraft/entity/Entity;
    //   232: astore #11
    //   234: aload #11
    //   236: getfield lastTickPosX : D
    //   239: aload #11
    //   241: getfield posX : D
    //   244: aload #11
    //   246: getfield lastTickPosX : D
    //   249: dsub
    //   250: fload_3
    //   251: f2d
    //   252: dmul
    //   253: dadd
    //   254: dstore #12
    //   256: aload #11
    //   258: getfield lastTickPosY : D
    //   261: aload #11
    //   263: getfield posY : D
    //   266: aload #11
    //   268: getfield lastTickPosY : D
    //   271: dsub
    //   272: fload_3
    //   273: f2d
    //   274: dmul
    //   275: dadd
    //   276: dstore #14
    //   278: aload #11
    //   280: getfield lastTickPosZ : D
    //   283: aload #11
    //   285: getfield posZ : D
    //   288: aload #11
    //   290: getfield lastTickPosZ : D
    //   293: dsub
    //   294: fload_3
    //   295: f2d
    //   296: dmul
    //   297: dadd
    //   298: dstore #16
    //   300: dload #12
    //   302: putstatic net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.staticPlayerX : D
    //   305: dload #14
    //   307: putstatic net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.staticPlayerY : D
    //   310: dload #16
    //   312: putstatic net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.staticPlayerZ : D
    //   315: aload_0
    //   316: getfield renderManager : Lnet/minecraft/client/renderer/entity/RenderManager;
    //   319: dload #12
    //   321: dload #14
    //   323: dload #16
    //   325: invokevirtual setRenderPosition : (DDD)V
    //   328: aload_0
    //   329: getfield mc : Lnet/minecraft/client/Minecraft;
    //   332: getfield entityRenderer : Lnet/minecraft/client/renderer/EntityRenderer;
    //   335: invokevirtual enableLightmap : ()V
    //   338: aload_0
    //   339: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   342: getfield theProfiler : Lnet/minecraft/profiler/Profiler;
    //   345: ldc_w 'global'
    //   348: invokevirtual endStartSection : (Ljava/lang/String;)V
    //   351: aload_0
    //   352: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   355: invokevirtual getLoadedEntityList : ()Ljava/util/List;
    //   358: astore #18
    //   360: iload #4
    //   362: ifne -> 376
    //   365: aload_0
    //   366: aload #18
    //   368: invokeinterface size : ()I
    //   373: putfield countEntitiesTotal : I
    //   376: invokestatic isFogOff : ()Z
    //   379: ifeq -> 398
    //   382: aload_0
    //   383: getfield mc : Lnet/minecraft/client/Minecraft;
    //   386: getfield entityRenderer : Lnet/minecraft/client/renderer/EntityRenderer;
    //   389: getfield fogStandard : Z
    //   392: ifeq -> 398
    //   395: invokestatic disableFog : ()V
    //   398: getstatic net/optifine/reflect/Reflector.ForgeEntity_shouldRenderInPass : Lnet/optifine/reflect/ReflectorMethod;
    //   401: invokevirtual exists : ()Z
    //   404: istore #19
    //   406: getstatic net/optifine/reflect/Reflector.ForgeTileEntity_shouldRenderInPass : Lnet/optifine/reflect/ReflectorMethod;
    //   409: invokevirtual exists : ()Z
    //   412: istore #20
    //   414: iconst_0
    //   415: istore #21
    //   417: iload #21
    //   419: aload_0
    //   420: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   423: getfield weatherEffects : Ljava/util/List;
    //   426: invokeinterface size : ()I
    //   431: if_icmpge -> 522
    //   434: aload_0
    //   435: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   438: getfield weatherEffects : Ljava/util/List;
    //   441: iload #21
    //   443: invokeinterface get : (I)Ljava/lang/Object;
    //   448: checkcast net/minecraft/entity/Entity
    //   451: astore #22
    //   453: iload #19
    //   455: ifeq -> 481
    //   458: aload #22
    //   460: getstatic net/optifine/reflect/Reflector.ForgeEntity_shouldRenderInPass : Lnet/optifine/reflect/ReflectorMethod;
    //   463: iconst_1
    //   464: anewarray java/lang/Object
    //   467: dup
    //   468: iconst_0
    //   469: iload #4
    //   471: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   474: aastore
    //   475: invokestatic callBoolean : (Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z
    //   478: ifeq -> 516
    //   481: aload_0
    //   482: dup
    //   483: getfield countEntitiesRendered : I
    //   486: iconst_1
    //   487: iadd
    //   488: putfield countEntitiesRendered : I
    //   491: aload #22
    //   493: dload #5
    //   495: dload #7
    //   497: dload #9
    //   499: invokevirtual isInRangeToRender3d : (DDD)Z
    //   502: ifeq -> 516
    //   505: aload_0
    //   506: getfield renderManager : Lnet/minecraft/client/renderer/entity/RenderManager;
    //   509: aload #22
    //   511: fload_3
    //   512: invokevirtual renderEntitySimple : (Lnet/minecraft/entity/Entity;F)Z
    //   515: pop
    //   516: iinc #21, 1
    //   519: goto -> 417
    //   522: aload_0
    //   523: invokevirtual isRenderEntityOutlines : ()Z
    //   526: ifeq -> 822
    //   529: sipush #519
    //   532: invokestatic depthFunc : (I)V
    //   535: invokestatic disableFog : ()V
    //   538: aload_0
    //   539: getfield entityOutlineFramebuffer : Lnet/minecraft/client/shader/Framebuffer;
    //   542: invokevirtual framebufferClear : ()V
    //   545: aload_0
    //   546: getfield entityOutlineFramebuffer : Lnet/minecraft/client/shader/Framebuffer;
    //   549: iconst_0
    //   550: invokevirtual bindFramebuffer : (Z)V
    //   553: aload_0
    //   554: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   557: getfield theProfiler : Lnet/minecraft/profiler/Profiler;
    //   560: ldc_w 'entityOutlines'
    //   563: invokevirtual endStartSection : (Ljava/lang/String;)V
    //   566: invokestatic disableStandardItemLighting : ()V
    //   569: aload_0
    //   570: getfield renderManager : Lnet/minecraft/client/renderer/entity/RenderManager;
    //   573: iconst_1
    //   574: invokevirtual setRenderOutlines : (Z)V
    //   577: iconst_0
    //   578: istore #21
    //   580: iload #21
    //   582: aload #18
    //   584: invokeinterface size : ()I
    //   589: if_icmpge -> 760
    //   592: aload #18
    //   594: iload #21
    //   596: invokeinterface get : (I)Ljava/lang/Object;
    //   601: checkcast net/minecraft/entity/Entity
    //   604: astore #22
    //   606: aload_0
    //   607: getfield mc : Lnet/minecraft/client/Minecraft;
    //   610: invokevirtual getRenderViewEntity : ()Lnet/minecraft/entity/Entity;
    //   613: instanceof net/minecraft/entity/EntityLivingBase
    //   616: ifeq -> 639
    //   619: aload_0
    //   620: getfield mc : Lnet/minecraft/client/Minecraft;
    //   623: invokevirtual getRenderViewEntity : ()Lnet/minecraft/entity/Entity;
    //   626: checkcast net/minecraft/entity/EntityLivingBase
    //   629: invokevirtual isPlayerSleeping : ()Z
    //   632: ifeq -> 639
    //   635: iconst_1
    //   636: goto -> 640
    //   639: iconst_0
    //   640: istore #23
    //   642: aload #22
    //   644: dload #5
    //   646: dload #7
    //   648: dload #9
    //   650: invokevirtual isInRangeToRender3d : (DDD)Z
    //   653: ifeq -> 705
    //   656: aload #22
    //   658: getfield ignoreFrustumCheck : Z
    //   661: ifne -> 693
    //   664: aload_2
    //   665: aload #22
    //   667: invokevirtual getEntityBoundingBox : ()Lnet/minecraft/util/AxisAlignedBB;
    //   670: invokeinterface isBoundingBoxInFrustum : (Lnet/minecraft/util/AxisAlignedBB;)Z
    //   675: ifne -> 693
    //   678: aload #22
    //   680: getfield riddenByEntity : Lnet/minecraft/entity/Entity;
    //   683: aload_0
    //   684: getfield mc : Lnet/minecraft/client/Minecraft;
    //   687: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   690: if_acmpne -> 705
    //   693: aload #22
    //   695: instanceof net/minecraft/entity/player/EntityPlayer
    //   698: ifeq -> 705
    //   701: iconst_1
    //   702: goto -> 706
    //   705: iconst_0
    //   706: istore #24
    //   708: aload #22
    //   710: aload_0
    //   711: getfield mc : Lnet/minecraft/client/Minecraft;
    //   714: invokevirtual getRenderViewEntity : ()Lnet/minecraft/entity/Entity;
    //   717: if_acmpne -> 738
    //   720: aload_0
    //   721: getfield mc : Lnet/minecraft/client/Minecraft;
    //   724: getfield gameSettings : Lnet/minecraft/client/settings/GameSettings;
    //   727: getfield thirdPersonView : I
    //   730: ifne -> 738
    //   733: iload #23
    //   735: ifeq -> 754
    //   738: iload #24
    //   740: ifeq -> 754
    //   743: aload_0
    //   744: getfield renderManager : Lnet/minecraft/client/renderer/entity/RenderManager;
    //   747: aload #22
    //   749: fload_3
    //   750: invokevirtual renderEntitySimple : (Lnet/minecraft/entity/Entity;F)Z
    //   753: pop
    //   754: iinc #21, 1
    //   757: goto -> 580
    //   760: aload_0
    //   761: getfield renderManager : Lnet/minecraft/client/renderer/entity/RenderManager;
    //   764: iconst_0
    //   765: invokevirtual setRenderOutlines : (Z)V
    //   768: invokestatic enableStandardItemLighting : ()V
    //   771: iconst_0
    //   772: invokestatic depthMask : (Z)V
    //   775: aload_0
    //   776: getfield entityOutlineShader : Lnet/minecraft/client/shader/ShaderGroup;
    //   779: fload_3
    //   780: invokevirtual loadShaderGroup : (F)V
    //   783: invokestatic enableLighting : ()V
    //   786: iconst_1
    //   787: invokestatic depthMask : (Z)V
    //   790: aload_0
    //   791: getfield mc : Lnet/minecraft/client/Minecraft;
    //   794: invokevirtual getFramebuffer : ()Lnet/minecraft/client/shader/Framebuffer;
    //   797: iconst_0
    //   798: invokevirtual bindFramebuffer : (Z)V
    //   801: invokestatic enableFog : ()V
    //   804: invokestatic enableBlend : ()V
    //   807: invokestatic enableColorMaterial : ()V
    //   810: sipush #515
    //   813: invokestatic depthFunc : (I)V
    //   816: invokestatic enableDepth : ()V
    //   819: invokestatic enableAlpha : ()V
    //   822: aload_0
    //   823: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   826: getfield theProfiler : Lnet/minecraft/profiler/Profiler;
    //   829: ldc_w 'entities'
    //   832: invokevirtual endStartSection : (Ljava/lang/String;)V
    //   835: invokestatic isShaders : ()Z
    //   838: istore #21
    //   840: iload #21
    //   842: ifeq -> 848
    //   845: invokestatic beginEntities : ()V
    //   848: invokestatic updateItemRenderDistance : ()V
    //   851: aload_0
    //   852: getfield mc : Lnet/minecraft/client/Minecraft;
    //   855: getfield gameSettings : Lnet/minecraft/client/settings/GameSettings;
    //   858: getfield fancyGraphics : Z
    //   861: istore #22
    //   863: aload_0
    //   864: getfield mc : Lnet/minecraft/client/Minecraft;
    //   867: getfield gameSettings : Lnet/minecraft/client/settings/GameSettings;
    //   870: invokestatic isDroppedItemsFancy : ()Z
    //   873: putfield fancyGraphics : Z
    //   876: getstatic net/optifine/shaders/Shaders.isShadowPass : Z
    //   879: ifeq -> 899
    //   882: aload_0
    //   883: getfield mc : Lnet/minecraft/client/Minecraft;
    //   886: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   889: invokevirtual isSpectator : ()Z
    //   892: ifne -> 899
    //   895: iconst_1
    //   896: goto -> 900
    //   899: iconst_0
    //   900: istore #23
    //   902: aload_0
    //   903: getfield renderInfosEntities : Ljava/util/List;
    //   906: invokeinterface iterator : ()Ljava/util/Iterator;
    //   911: astore #24
    //   913: aload #24
    //   915: invokeinterface hasNext : ()Z
    //   920: ifeq -> 1325
    //   923: aload #24
    //   925: invokeinterface next : ()Ljava/lang/Object;
    //   930: astore #25
    //   932: aload #25
    //   934: checkcast net/minecraft/client/renderer/RenderGlobal$ContainerLocalRenderInformation
    //   937: astore #26
    //   939: aload #26
    //   941: getfield renderChunk : Lnet/minecraft/client/renderer/chunk/RenderChunk;
    //   944: invokevirtual getChunk : ()Lnet/minecraft/world/chunk/Chunk;
    //   947: astore #27
    //   949: aload #27
    //   951: invokevirtual getEntityLists : ()[Lnet/minecraft/util/ClassInheritanceMultiMap;
    //   954: aload #26
    //   956: getfield renderChunk : Lnet/minecraft/client/renderer/chunk/RenderChunk;
    //   959: invokevirtual getPosition : ()Lnet/minecraft/util/BlockPos;
    //   962: invokevirtual getY : ()I
    //   965: bipush #16
    //   967: idiv
    //   968: aaload
    //   969: astore #28
    //   971: aload #28
    //   973: invokevirtual isEmpty : ()Z
    //   976: ifne -> 1322
    //   979: aload #28
    //   981: invokevirtual iterator : ()Ljava/util/Iterator;
    //   984: astore #29
    //   986: aload #29
    //   988: invokeinterface hasNext : ()Z
    //   993: ifne -> 999
    //   996: goto -> 913
    //   999: aload #29
    //   1001: invokeinterface next : ()Ljava/lang/Object;
    //   1006: checkcast net/minecraft/entity/Entity
    //   1009: astore #30
    //   1011: iload #19
    //   1013: ifeq -> 1039
    //   1016: aload #30
    //   1018: getstatic net/optifine/reflect/Reflector.ForgeEntity_shouldRenderInPass : Lnet/optifine/reflect/ReflectorMethod;
    //   1021: iconst_1
    //   1022: anewarray java/lang/Object
    //   1025: dup
    //   1026: iconst_0
    //   1027: iload #4
    //   1029: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   1032: aastore
    //   1033: invokestatic callBoolean : (Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z
    //   1036: ifeq -> 986
    //   1039: aload_0
    //   1040: getfield renderManager : Lnet/minecraft/client/renderer/entity/RenderManager;
    //   1043: aload #30
    //   1045: aload_2
    //   1046: dload #5
    //   1048: dload #7
    //   1050: dload #9
    //   1052: invokevirtual shouldRender : (Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;DDD)Z
    //   1055: ifne -> 1073
    //   1058: aload #30
    //   1060: getfield riddenByEntity : Lnet/minecraft/entity/Entity;
    //   1063: aload_0
    //   1064: getfield mc : Lnet/minecraft/client/Minecraft;
    //   1067: getfield thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   1070: if_acmpne -> 1077
    //   1073: iconst_1
    //   1074: goto -> 1078
    //   1077: iconst_0
    //   1078: istore #31
    //   1080: iload #31
    //   1082: ifne -> 1088
    //   1085: goto -> 1244
    //   1088: aload_0
    //   1089: getfield mc : Lnet/minecraft/client/Minecraft;
    //   1092: invokevirtual getRenderViewEntity : ()Lnet/minecraft/entity/Entity;
    //   1095: instanceof net/minecraft/entity/EntityLivingBase
    //   1098: ifeq -> 1117
    //   1101: aload_0
    //   1102: getfield mc : Lnet/minecraft/client/Minecraft;
    //   1105: invokevirtual getRenderViewEntity : ()Lnet/minecraft/entity/Entity;
    //   1108: checkcast net/minecraft/entity/EntityLivingBase
    //   1111: invokevirtual isPlayerSleeping : ()Z
    //   1114: goto -> 1118
    //   1117: iconst_0
    //   1118: istore #32
    //   1120: aload #30
    //   1122: aload_0
    //   1123: getfield mc : Lnet/minecraft/client/Minecraft;
    //   1126: invokevirtual getRenderViewEntity : ()Lnet/minecraft/entity/Entity;
    //   1129: if_acmpne -> 1155
    //   1132: iload #23
    //   1134: ifne -> 1155
    //   1137: aload_0
    //   1138: getfield mc : Lnet/minecraft/client/Minecraft;
    //   1141: getfield gameSettings : Lnet/minecraft/client/settings/GameSettings;
    //   1144: getfield thirdPersonView : I
    //   1147: ifne -> 1155
    //   1150: iload #32
    //   1152: ifeq -> 1241
    //   1155: aload #30
    //   1157: getfield posY : D
    //   1160: dconst_0
    //   1161: dcmpg
    //   1162: iflt -> 1196
    //   1165: aload #30
    //   1167: getfield posY : D
    //   1170: ldc2_w 256.0
    //   1173: dcmpl
    //   1174: ifge -> 1196
    //   1177: aload_0
    //   1178: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   1181: new net/minecraft/util/BlockPos
    //   1184: dup
    //   1185: aload #30
    //   1187: invokespecial <init> : (Lnet/minecraft/entity/Entity;)V
    //   1190: invokevirtual isBlockLoaded : (Lnet/minecraft/util/BlockPos;)Z
    //   1193: ifeq -> 1241
    //   1196: aload_0
    //   1197: dup
    //   1198: getfield countEntitiesRendered : I
    //   1201: iconst_1
    //   1202: iadd
    //   1203: putfield countEntitiesRendered : I
    //   1206: aload_0
    //   1207: aload #30
    //   1209: putfield renderedEntity : Lnet/minecraft/entity/Entity;
    //   1212: iload #21
    //   1214: ifeq -> 1222
    //   1217: aload #30
    //   1219: invokestatic nextEntity : (Lnet/minecraft/entity/Entity;)V
    //   1222: aload_0
    //   1223: getfield renderManager : Lnet/minecraft/client/renderer/entity/RenderManager;
    //   1226: aload #30
    //   1228: fload_3
    //   1229: invokevirtual renderEntitySimple : (Lnet/minecraft/entity/Entity;F)Z
    //   1232: pop
    //   1233: aload_0
    //   1234: aconst_null
    //   1235: putfield renderedEntity : Lnet/minecraft/entity/Entity;
    //   1238: goto -> 1244
    //   1241: goto -> 986
    //   1244: iload #31
    //   1246: ifne -> 1319
    //   1249: aload #30
    //   1251: instanceof net/minecraft/entity/projectile/EntityWitherSkull
    //   1254: ifeq -> 1319
    //   1257: iload #19
    //   1259: ifeq -> 1285
    //   1262: aload #30
    //   1264: getstatic net/optifine/reflect/Reflector.ForgeEntity_shouldRenderInPass : Lnet/optifine/reflect/ReflectorMethod;
    //   1267: iconst_1
    //   1268: anewarray java/lang/Object
    //   1271: dup
    //   1272: iconst_0
    //   1273: iload #4
    //   1275: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   1278: aastore
    //   1279: invokestatic callBoolean : (Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z
    //   1282: ifeq -> 1319
    //   1285: aload_0
    //   1286: aload #30
    //   1288: putfield renderedEntity : Lnet/minecraft/entity/Entity;
    //   1291: iload #21
    //   1293: ifeq -> 1301
    //   1296: aload #30
    //   1298: invokestatic nextEntity : (Lnet/minecraft/entity/Entity;)V
    //   1301: aload_0
    //   1302: getfield mc : Lnet/minecraft/client/Minecraft;
    //   1305: invokevirtual getRenderManager : ()Lnet/minecraft/client/renderer/entity/RenderManager;
    //   1308: aload #30
    //   1310: fload_3
    //   1311: invokevirtual renderWitherSkull : (Lnet/minecraft/entity/Entity;F)V
    //   1314: aload_0
    //   1315: aconst_null
    //   1316: putfield renderedEntity : Lnet/minecraft/entity/Entity;
    //   1319: goto -> 986
    //   1322: goto -> 913
    //   1325: aload_0
    //   1326: getfield mc : Lnet/minecraft/client/Minecraft;
    //   1329: getfield gameSettings : Lnet/minecraft/client/settings/GameSettings;
    //   1332: iload #22
    //   1334: putfield fancyGraphics : Z
    //   1337: iload #21
    //   1339: ifeq -> 1348
    //   1342: invokestatic endEntities : ()V
    //   1345: invokestatic beginBlockEntities : ()V
    //   1348: aload_0
    //   1349: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   1352: getfield theProfiler : Lnet/minecraft/profiler/Profiler;
    //   1355: ldc_w 'blockentities'
    //   1358: invokevirtual endStartSection : (Ljava/lang/String;)V
    //   1361: invokestatic enableStandardItemLighting : ()V
    //   1364: getstatic net/optifine/reflect/Reflector.ForgeTileEntity_hasFastRenderer : Lnet/optifine/reflect/ReflectorMethod;
    //   1367: invokevirtual exists : ()Z
    //   1370: ifeq -> 1379
    //   1373: getstatic net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.instance : Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;
    //   1376: invokevirtual preDrawBatch : ()V
    //   1379: invokestatic updateTextRenderDistance : ()V
    //   1382: aload_0
    //   1383: getfield renderInfosTileEntities : Ljava/util/List;
    //   1386: invokeinterface iterator : ()Ljava/util/Iterator;
    //   1391: astore #24
    //   1393: aload #24
    //   1395: invokeinterface hasNext : ()Z
    //   1400: ifeq -> 1582
    //   1403: aload #24
    //   1405: invokeinterface next : ()Ljava/lang/Object;
    //   1410: astore #25
    //   1412: aload #25
    //   1414: checkcast net/minecraft/client/renderer/RenderGlobal$ContainerLocalRenderInformation
    //   1417: astore #26
    //   1419: aload #26
    //   1421: getfield renderChunk : Lnet/minecraft/client/renderer/chunk/RenderChunk;
    //   1424: invokevirtual getCompiledChunk : ()Lnet/minecraft/client/renderer/chunk/CompiledChunk;
    //   1427: invokevirtual getTileEntities : ()Ljava/util/List;
    //   1430: astore #27
    //   1432: aload #27
    //   1434: invokeinterface isEmpty : ()Z
    //   1439: ifne -> 1579
    //   1442: aload #27
    //   1444: invokeinterface iterator : ()Ljava/util/Iterator;
    //   1449: astore #28
    //   1451: aload #28
    //   1453: invokeinterface hasNext : ()Z
    //   1458: ifne -> 1464
    //   1461: goto -> 1393
    //   1464: aload #28
    //   1466: invokeinterface next : ()Ljava/lang/Object;
    //   1471: checkcast net/minecraft/tileentity/TileEntity
    //   1474: astore #29
    //   1476: iload #20
    //   1478: ifne -> 1484
    //   1481: goto -> 1546
    //   1484: aload #29
    //   1486: getstatic net/optifine/reflect/Reflector.ForgeTileEntity_shouldRenderInPass : Lnet/optifine/reflect/ReflectorMethod;
    //   1489: iconst_1
    //   1490: anewarray java/lang/Object
    //   1493: dup
    //   1494: iconst_0
    //   1495: iload #4
    //   1497: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   1500: aastore
    //   1501: invokestatic callBoolean : (Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z
    //   1504: ifeq -> 1451
    //   1507: aload #29
    //   1509: getstatic net/optifine/reflect/Reflector.ForgeTileEntity_getRenderBoundingBox : Lnet/optifine/reflect/ReflectorMethod;
    //   1512: iconst_0
    //   1513: anewarray java/lang/Object
    //   1516: invokestatic call : (Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;
    //   1519: checkcast net/minecraft/util/AxisAlignedBB
    //   1522: astore #30
    //   1524: aload #30
    //   1526: ifnull -> 1546
    //   1529: aload_2
    //   1530: aload #30
    //   1532: invokeinterface isBoundingBoxInFrustum : (Lnet/minecraft/util/AxisAlignedBB;)Z
    //   1537: ifeq -> 1543
    //   1540: goto -> 1546
    //   1543: goto -> 1451
    //   1546: iload #21
    //   1548: ifeq -> 1556
    //   1551: aload #29
    //   1553: invokestatic nextBlockEntity : (Lnet/minecraft/tileentity/TileEntity;)V
    //   1556: getstatic net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.instance : Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;
    //   1559: aload #29
    //   1561: fload_3
    //   1562: iconst_m1
    //   1563: invokevirtual renderTileEntity : (Lnet/minecraft/tileentity/TileEntity;FI)V
    //   1566: aload_0
    //   1567: dup
    //   1568: getfield countTileEntitiesRendered : I
    //   1571: iconst_1
    //   1572: iadd
    //   1573: putfield countTileEntitiesRendered : I
    //   1576: goto -> 1451
    //   1579: goto -> 1393
    //   1582: aload_0
    //   1583: getfield setTileEntities : Ljava/util/Set;
    //   1586: dup
    //   1587: astore #24
    //   1589: monitorenter
    //   1590: aload_0
    //   1591: getfield setTileEntities : Ljava/util/Set;
    //   1594: invokeinterface iterator : ()Ljava/util/Iterator;
    //   1599: astore #25
    //   1601: aload #25
    //   1603: invokeinterface hasNext : ()Z
    //   1608: ifeq -> 1674
    //   1611: aload #25
    //   1613: invokeinterface next : ()Ljava/lang/Object;
    //   1618: checkcast net/minecraft/tileentity/TileEntity
    //   1621: astore #26
    //   1623: iload #20
    //   1625: ifeq -> 1651
    //   1628: aload #26
    //   1630: getstatic net/optifine/reflect/Reflector.ForgeTileEntity_shouldRenderInPass : Lnet/optifine/reflect/ReflectorMethod;
    //   1633: iconst_1
    //   1634: anewarray java/lang/Object
    //   1637: dup
    //   1638: iconst_0
    //   1639: iload #4
    //   1641: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   1644: aastore
    //   1645: invokestatic callBoolean : (Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z
    //   1648: ifeq -> 1671
    //   1651: iload #21
    //   1653: ifeq -> 1661
    //   1656: aload #26
    //   1658: invokestatic nextBlockEntity : (Lnet/minecraft/tileentity/TileEntity;)V
    //   1661: getstatic net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.instance : Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;
    //   1664: aload #26
    //   1666: fload_3
    //   1667: iconst_m1
    //   1668: invokevirtual renderTileEntity : (Lnet/minecraft/tileentity/TileEntity;FI)V
    //   1671: goto -> 1601
    //   1674: aload #24
    //   1676: monitorexit
    //   1677: goto -> 1688
    //   1680: astore #33
    //   1682: aload #24
    //   1684: monitorexit
    //   1685: aload #33
    //   1687: athrow
    //   1688: getstatic net/optifine/reflect/Reflector.ForgeTileEntity_hasFastRenderer : Lnet/optifine/reflect/ReflectorMethod;
    //   1691: invokevirtual exists : ()Z
    //   1694: ifeq -> 1705
    //   1697: getstatic net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.instance : Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;
    //   1700: iload #4
    //   1702: invokevirtual drawBatch : (I)V
    //   1705: aload_0
    //   1706: iconst_1
    //   1707: putfield renderOverlayDamaged : Z
    //   1710: aload_0
    //   1711: invokespecial preRenderDamagedBlocks : ()V
    //   1714: aload_0
    //   1715: getfield damagedBlocks : Ljava/util/Map;
    //   1718: invokeinterface values : ()Ljava/util/Collection;
    //   1723: invokeinterface iterator : ()Ljava/util/Iterator;
    //   1728: astore #24
    //   1730: aload #24
    //   1732: invokeinterface hasNext : ()Z
    //   1737: ifeq -> 2024
    //   1740: aload #24
    //   1742: invokeinterface next : ()Ljava/lang/Object;
    //   1747: checkcast net/minecraft/client/renderer/DestroyBlockProgress
    //   1750: astore #25
    //   1752: aload #25
    //   1754: invokevirtual getPosition : ()Lnet/minecraft/util/BlockPos;
    //   1757: astore #26
    //   1759: aload_0
    //   1760: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   1763: aload #26
    //   1765: invokevirtual getTileEntity : (Lnet/minecraft/util/BlockPos;)Lnet/minecraft/tileentity/TileEntity;
    //   1768: astore #27
    //   1770: aload #27
    //   1772: instanceof net/minecraft/tileentity/TileEntityChest
    //   1775: ifeq -> 1846
    //   1778: aload #27
    //   1780: checkcast net/minecraft/tileentity/TileEntityChest
    //   1783: astore #28
    //   1785: aload #28
    //   1787: getfield adjacentChestXNeg : Lnet/minecraft/tileentity/TileEntityChest;
    //   1790: ifnull -> 1817
    //   1793: aload #26
    //   1795: getstatic net/minecraft/util/EnumFacing.WEST : Lnet/minecraft/util/EnumFacing;
    //   1798: invokevirtual offset : (Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/BlockPos;
    //   1801: astore #26
    //   1803: aload_0
    //   1804: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   1807: aload #26
    //   1809: invokevirtual getTileEntity : (Lnet/minecraft/util/BlockPos;)Lnet/minecraft/tileentity/TileEntity;
    //   1812: astore #27
    //   1814: goto -> 1846
    //   1817: aload #28
    //   1819: getfield adjacentChestZNeg : Lnet/minecraft/tileentity/TileEntityChest;
    //   1822: ifnull -> 1846
    //   1825: aload #26
    //   1827: getstatic net/minecraft/util/EnumFacing.NORTH : Lnet/minecraft/util/EnumFacing;
    //   1830: invokevirtual offset : (Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/BlockPos;
    //   1833: astore #26
    //   1835: aload_0
    //   1836: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   1839: aload #26
    //   1841: invokevirtual getTileEntity : (Lnet/minecraft/util/BlockPos;)Lnet/minecraft/tileentity/TileEntity;
    //   1844: astore #27
    //   1846: aload_0
    //   1847: getfield theWorld : Lnet/minecraft/client/multiplayer/WorldClient;
    //   1850: aload #26
    //   1852: invokevirtual getBlockState : (Lnet/minecraft/util/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   1855: invokeinterface getBlock : ()Lnet/minecraft/block/Block;
    //   1860: astore #28
    //   1862: iload #20
    //   1864: ifeq -> 1948
    //   1867: iconst_0
    //   1868: istore #29
    //   1870: aload #27
    //   1872: ifnull -> 1992
    //   1875: aload #27
    //   1877: getstatic net/optifine/reflect/Reflector.ForgeTileEntity_shouldRenderInPass : Lnet/optifine/reflect/ReflectorMethod;
    //   1880: iconst_1
    //   1881: anewarray java/lang/Object
    //   1884: dup
    //   1885: iconst_0
    //   1886: iload #4
    //   1888: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   1891: aastore
    //   1892: invokestatic callBoolean : (Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z
    //   1895: ifeq -> 1992
    //   1898: aload #27
    //   1900: getstatic net/optifine/reflect/Reflector.ForgeTileEntity_canRenderBreaking : Lnet/optifine/reflect/ReflectorMethod;
    //   1903: iconst_0
    //   1904: anewarray java/lang/Object
    //   1907: invokestatic callBoolean : (Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Z
    //   1910: ifeq -> 1992
    //   1913: aload #27
    //   1915: getstatic net/optifine/reflect/Reflector.ForgeTileEntity_getRenderBoundingBox : Lnet/optifine/reflect/ReflectorMethod;
    //   1918: iconst_0
    //   1919: anewarray java/lang/Object
    //   1922: invokestatic call : (Ljava/lang/Object;Lnet/optifine/reflect/ReflectorMethod;[Ljava/lang/Object;)Ljava/lang/Object;
    //   1925: checkcast net/minecraft/util/AxisAlignedBB
    //   1928: astore #30
    //   1930: aload #30
    //   1932: ifnull -> 1945
    //   1935: aload_2
    //   1936: aload #30
    //   1938: invokeinterface isBoundingBoxInFrustum : (Lnet/minecraft/util/AxisAlignedBB;)Z
    //   1943: istore #29
    //   1945: goto -> 1992
    //   1948: aload #27
    //   1950: ifnull -> 1989
    //   1953: aload #28
    //   1955: instanceof net/minecraft/block/BlockChest
    //   1958: ifne -> 1985
    //   1961: aload #28
    //   1963: instanceof net/minecraft/block/BlockEnderChest
    //   1966: ifne -> 1985
    //   1969: aload #28
    //   1971: instanceof net/minecraft/block/BlockSign
    //   1974: ifne -> 1985
    //   1977: aload #28
    //   1979: instanceof net/minecraft/block/BlockSkull
    //   1982: ifeq -> 1989
    //   1985: iconst_1
    //   1986: goto -> 1990
    //   1989: iconst_0
    //   1990: istore #29
    //   1992: iload #29
    //   1994: ifeq -> 2021
    //   1997: iload #21
    //   1999: ifeq -> 2007
    //   2002: aload #27
    //   2004: invokestatic nextBlockEntity : (Lnet/minecraft/tileentity/TileEntity;)V
    //   2007: getstatic net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher.instance : Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;
    //   2010: aload #27
    //   2012: fload_3
    //   2013: aload #25
    //   2015: invokevirtual getPartialBlockDamage : ()I
    //   2018: invokevirtual renderTileEntity : (Lnet/minecraft/tileentity/TileEntity;FI)V
    //   2021: goto -> 1730
    //   2024: aload_0
    //   2025: invokespecial postRenderDamagedBlocks : ()V
    //   2028: aload_0
    //   2029: iconst_0
    //   2030: putfield renderOverlayDamaged : Z
    //   2033: iload #21
    //   2035: ifeq -> 2041
    //   2038: invokestatic endBlockEntities : ()V
    //   2041: getstatic net/minecraft/client/renderer/RenderGlobal.renderEntitiesCounter : I
    //   2044: iconst_1
    //   2045: isub
    //   2046: putstatic net/minecraft/client/renderer/RenderGlobal.renderEntitiesCounter : I
    //   2049: aload_0
    //   2050: getfield mc : Lnet/minecraft/client/Minecraft;
    //   2053: getfield entityRenderer : Lnet/minecraft/client/renderer/EntityRenderer;
    //   2056: invokevirtual disableLightmap : ()V
    //   2059: aload_0
    //   2060: getfield mc : Lnet/minecraft/client/Minecraft;
    //   2063: getfield mcProfiler : Lnet/minecraft/profiler/Profiler;
    //   2066: invokevirtual endSection : ()V
    //   2069: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #649	-> 0
    //   #651	-> 3
    //   #653	-> 12
    //   #656	-> 24
    //   #658	-> 31
    //   #660	-> 36
    //   #663	-> 37
    //   #667	-> 50
    //   #668	-> 69
    //   #669	-> 88
    //   #670	-> 107
    //   #671	-> 120
    //   #672	-> 152
    //   #673	-> 192
    //   #675	-> 200
    //   #677	-> 205
    //   #678	-> 210
    //   #679	-> 215
    //   #680	-> 220
    //   #683	-> 225
    //   #684	-> 234
    //   #685	-> 256
    //   #686	-> 278
    //   #687	-> 300
    //   #688	-> 305
    //   #689	-> 310
    //   #690	-> 315
    //   #691	-> 328
    //   #692	-> 338
    //   #693	-> 351
    //   #695	-> 360
    //   #697	-> 365
    //   #700	-> 376
    //   #702	-> 395
    //   #705	-> 398
    //   #706	-> 406
    //   #708	-> 414
    //   #710	-> 434
    //   #712	-> 453
    //   #714	-> 481
    //   #716	-> 491
    //   #718	-> 505
    //   #708	-> 516
    //   #723	-> 522
    //   #725	-> 529
    //   #726	-> 535
    //   #727	-> 538
    //   #728	-> 545
    //   #729	-> 553
    //   #730	-> 566
    //   #731	-> 569
    //   #733	-> 577
    //   #735	-> 592
    //   #736	-> 606
    //   #737	-> 642
    //   #739	-> 708
    //   #741	-> 743
    //   #733	-> 754
    //   #745	-> 760
    //   #746	-> 768
    //   #747	-> 771
    //   #748	-> 775
    //   #749	-> 783
    //   #750	-> 786
    //   #751	-> 790
    //   #752	-> 801
    //   #753	-> 804
    //   #754	-> 807
    //   #755	-> 810
    //   #756	-> 816
    //   #757	-> 819
    //   #760	-> 822
    //   #761	-> 835
    //   #763	-> 840
    //   #765	-> 845
    //   #768	-> 848
    //   #769	-> 851
    //   #770	-> 863
    //   #771	-> 876
    //   #774	-> 902
    //   #776	-> 932
    //   #777	-> 939
    //   #778	-> 949
    //   #780	-> 971
    //   #782	-> 979
    //   #791	-> 986
    //   #793	-> 996
    //   #796	-> 999
    //   #798	-> 1011
    //   #800	-> 1039
    //   #802	-> 1080
    //   #804	-> 1085
    //   #807	-> 1088
    //   #809	-> 1120
    //   #811	-> 1196
    //   #812	-> 1206
    //   #814	-> 1212
    //   #816	-> 1217
    //   #819	-> 1222
    //   #820	-> 1233
    //   #821	-> 1238
    //   #823	-> 1241
    //   #826	-> 1244
    //   #828	-> 1285
    //   #830	-> 1291
    //   #832	-> 1296
    //   #835	-> 1301
    //   #836	-> 1314
    //   #838	-> 1319
    //   #840	-> 1322
    //   #842	-> 1325
    //   #844	-> 1337
    //   #846	-> 1342
    //   #847	-> 1345
    //   #850	-> 1348
    //   #851	-> 1361
    //   #853	-> 1364
    //   #855	-> 1373
    //   #858	-> 1379
    //   #861	-> 1382
    //   #863	-> 1412
    //   #864	-> 1419
    //   #866	-> 1432
    //   #868	-> 1442
    //   #876	-> 1451
    //   #878	-> 1461
    //   #881	-> 1464
    //   #883	-> 1476
    //   #885	-> 1481
    //   #888	-> 1484
    //   #890	-> 1507
    //   #892	-> 1524
    //   #894	-> 1540
    //   #896	-> 1543
    //   #899	-> 1546
    //   #901	-> 1551
    //   #904	-> 1556
    //   #905	-> 1566
    //   #906	-> 1576
    //   #908	-> 1579
    //   #910	-> 1582
    //   #912	-> 1590
    //   #914	-> 1623
    //   #916	-> 1651
    //   #918	-> 1656
    //   #921	-> 1661
    //   #923	-> 1671
    //   #924	-> 1674
    //   #926	-> 1688
    //   #928	-> 1697
    //   #931	-> 1705
    //   #932	-> 1710
    //   #934	-> 1714
    //   #936	-> 1752
    //   #937	-> 1759
    //   #939	-> 1770
    //   #941	-> 1778
    //   #943	-> 1785
    //   #945	-> 1793
    //   #946	-> 1803
    //   #948	-> 1817
    //   #950	-> 1825
    //   #951	-> 1835
    //   #955	-> 1846
    //   #958	-> 1862
    //   #960	-> 1867
    //   #962	-> 1870
    //   #964	-> 1913
    //   #966	-> 1930
    //   #968	-> 1935
    //   #970	-> 1945
    //   #974	-> 1948
    //   #977	-> 1992
    //   #979	-> 1997
    //   #981	-> 2002
    //   #984	-> 2007
    //   #986	-> 2021
    //   #988	-> 2024
    //   #989	-> 2028
    //   #991	-> 2033
    //   #993	-> 2038
    //   #996	-> 2041
    //   #997	-> 2049
    //   #998	-> 2059
    //   #1000	-> 2069
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   453	63	22	entity1	Lnet/minecraft/entity/Entity;
    //   417	105	21	j	I
    //   606	148	22	entity3	Lnet/minecraft/entity/Entity;
    //   642	112	23	flag2	Z
    //   708	46	24	flag3	Z
    //   580	180	21	k	I
    //   1120	121	32	flag5	Z
    //   1011	308	30	entity2	Lnet/minecraft/entity/Entity;
    //   1080	239	31	flag4	Z
    //   986	336	29	iterator	Ljava/util/Iterator;
    //   939	383	26	renderglobal$containerlocalrenderinformation	Lnet/minecraft/client/renderer/RenderGlobal$ContainerLocalRenderInformation;
    //   949	373	27	chunk	Lnet/minecraft/world/chunk/Chunk;
    //   971	351	28	classinheritancemultimap	Lnet/minecraft/util/ClassInheritanceMultiMap;
    //   932	390	25	renderglobal$containerlocalrenderinformation0	Ljava/lang/Object;
    //   1524	19	30	axisalignedbb1	Lnet/minecraft/util/AxisAlignedBB;
    //   1476	100	29	tileentity1	Lnet/minecraft/tileentity/TileEntity;
    //   1451	128	28	iterator1	Ljava/util/Iterator;
    //   1419	160	26	renderglobal$containerlocalrenderinformation1	Lnet/minecraft/client/renderer/RenderGlobal$ContainerLocalRenderInformation;
    //   1432	147	27	list1	Ljava/util/List;
    //   1412	167	25	renderglobal$containerlocalrenderinformation10	Ljava/lang/Object;
    //   1623	48	26	tileentity	Lnet/minecraft/tileentity/TileEntity;
    //   1785	61	28	tileentitychest	Lnet/minecraft/tileentity/TileEntityChest;
    //   1930	15	30	axisalignedbb	Lnet/minecraft/util/AxisAlignedBB;
    //   1870	78	29	flag9	Z
    //   1759	262	26	blockpos	Lnet/minecraft/util/BlockPos;
    //   1770	251	27	tileentity2	Lnet/minecraft/tileentity/TileEntity;
    //   1862	159	28	block	Lnet/minecraft/block/Block;
    //   1992	29	29	flag9	Z
    //   1752	269	25	destroyblockprogress	Lnet/minecraft/client/renderer/DestroyBlockProgress;
    //   69	2000	5	d0	D
    //   88	1981	7	d1	D
    //   107	1962	9	d2	D
    //   234	1835	11	entity	Lnet/minecraft/entity/Entity;
    //   256	1813	12	d3	D
    //   278	1791	14	d4	D
    //   300	1769	16	d5	D
    //   360	1709	18	list	Ljava/util/List;
    //   406	1663	19	flag	Z
    //   414	1655	20	flag1	Z
    //   840	1229	21	flag6	Z
    //   863	1206	22	flag7	Z
    //   902	1167	23	flag8	Z
    //   0	2070	0	this	Lnet/minecraft/client/renderer/RenderGlobal;
    //   0	2070	1	renderViewEntity	Lnet/minecraft/entity/Entity;
    //   0	2070	2	camera	Lnet/minecraft/client/renderer/culling/ICamera;
    //   0	2070	3	partialTicks	F
    //   3	2067	4	i	I
    // Local variable type table:
    //   start	length	slot	name	signature
    //   971	351	28	classinheritancemultimap	Lnet/minecraft/util/ClassInheritanceMultiMap<Lnet/minecraft/entity/Entity;>;
    //   1432	147	27	list1	Ljava/util/List<Lnet/minecraft/tileentity/TileEntity;>;
    //   360	1709	18	list	Ljava/util/List<Lnet/minecraft/entity/Entity;>;
    // Exception table:
    //   from	to	target	type
    //   1590	1677	1680	finally
    //   1680	1685	1680	finally
  }
  
  public String getDebugInfoRenders() {
    int i = this.viewFrustum.renderChunks.length;
    int j = 0;
    for (ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos) {
      CompiledChunk compiledchunk = renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk;
      if (compiledchunk != CompiledChunk.DUMMY && !compiledchunk.isEmpty())
        j++; 
    } 
    return String.format("C: %d/%d %sD: %d, %s", new Object[] { Integer.valueOf(j), Integer.valueOf(i), this.mc.renderChunksMany ? "(s) " : "", Integer.valueOf(this.renderDistanceChunks), this.renderDispatcher.getDebugInfo() });
  }
  
  public String getDebugInfoEntities() {
    return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ", B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered) + ", " + Config.getVersionDebug();
  }
  
  public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
    Frustum frustum;
    if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks)
      loadRenderers(); 
    this.theWorld.theProfiler.startSection("camera");
    double d0 = viewEntity.posX - this.frustumUpdatePosX;
    double d1 = viewEntity.posY - this.frustumUpdatePosY;
    double d2 = viewEntity.posZ - this.frustumUpdatePosZ;
    if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D) {
      this.frustumUpdatePosX = viewEntity.posX;
      this.frustumUpdatePosY = viewEntity.posY;
      this.frustumUpdatePosZ = viewEntity.posZ;
      this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
      this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
      this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
      this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
    } 
    if (Config.isDynamicLights())
      DynamicLights.update(this); 
    this.theWorld.theProfiler.endStartSection("renderlistcamera");
    double d3 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
    double d4 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
    double d5 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
    this.renderContainer.initialize(d3, d4, d5);
    this.theWorld.theProfiler.endStartSection("cull");
    if (this.debugFixedClippingHelper != null) {
      Frustum frustum1 = new Frustum(this.debugFixedClippingHelper);
      frustum1.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
      frustum = frustum1;
    } 
    this.mc.mcProfiler.endStartSection("culling");
    BlockPos blockpos = new BlockPos(d3, d4 + viewEntity.getEyeHeight(), d5);
    RenderChunk renderchunk = this.viewFrustum.getRenderChunk(blockpos);
    new BlockPos(MathHelper.floor_double(d3 / 16.0D) * 16, MathHelper.floor_double(d4 / 16.0D) * 16, MathHelper.floor_double(d5 / 16.0D) * 16);
    this.displayListEntitiesDirty = (this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || viewEntity.posX != this.lastViewEntityX || viewEntity.posY != this.lastViewEntityY || viewEntity.posZ != this.lastViewEntityZ || viewEntity.rotationPitch != this.lastViewEntityPitch || viewEntity.rotationYaw != this.lastViewEntityYaw);
    this.lastViewEntityX = viewEntity.posX;
    this.lastViewEntityY = viewEntity.posY;
    this.lastViewEntityZ = viewEntity.posZ;
    this.lastViewEntityPitch = viewEntity.rotationPitch;
    this.lastViewEntityYaw = viewEntity.rotationYaw;
    boolean flag = (this.debugFixedClippingHelper != null);
    this.mc.mcProfiler.endStartSection("update");
    Lagometer.timerVisibility.start();
    int i = getCountLoadedChunks();
    if (i != this.countLoadedChunksPrev) {
      this.countLoadedChunksPrev = i;
      this.displayListEntitiesDirty = true;
    } 
    int j = 256;
    if (!ChunkVisibility.isFinished())
      this.displayListEntitiesDirty = true; 
    if (!flag && this.displayListEntitiesDirty && Config.isIntegratedServerRunning())
      j = ChunkVisibility.getMaxChunkY((World)this.theWorld, viewEntity, this.renderDistanceChunks); 
    RenderChunk renderchunk1 = this.viewFrustum.getRenderChunk(new BlockPos(viewEntity.posX, viewEntity.posY, viewEntity.posZ));
    if (Shaders.isShadowPass) {
      this.renderInfos = this.renderInfosShadow;
      this.renderInfosEntities = this.renderInfosEntitiesShadow;
      this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;
      if (!flag && this.displayListEntitiesDirty) {
        clearRenderInfos();
        if (renderchunk1 != null && renderchunk1.getPosition().getY() > j)
          this.renderInfosEntities.add(renderchunk1.getRenderInfo()); 
        Iterator<RenderChunk> iterator = ShadowUtils.makeShadowChunkIterator(this.theWorld, partialTicks, viewEntity, this.renderDistanceChunks, this.viewFrustum);
        while (iterator.hasNext()) {
          RenderChunk renderchunk2 = iterator.next();
          if (renderchunk2 != null && renderchunk2.getPosition().getY() <= j) {
            ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = renderchunk2.getRenderInfo();
            if (!renderchunk2.compiledChunk.isEmpty() || renderchunk2.isNeedsUpdate())
              this.renderInfos.add(renderglobal$containerlocalrenderinformation); 
            if (ChunkUtils.hasEntities(renderchunk2.getChunk()))
              this.renderInfosEntities.add(renderglobal$containerlocalrenderinformation); 
            if (renderchunk2.getCompiledChunk().getTileEntities().size() > 0)
              this.renderInfosTileEntities.add(renderglobal$containerlocalrenderinformation); 
          } 
        } 
      } 
    } else {
      this.renderInfos = this.renderInfosNormal;
      this.renderInfosEntities = this.renderInfosEntitiesNormal;
      this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
    } 
    if (!flag && this.displayListEntitiesDirty && !Shaders.isShadowPass) {
      this.displayListEntitiesDirty = false;
      clearRenderInfos();
      this.visibilityDeque.clear();
      Deque<ContainerLocalRenderInformation> deque = this.visibilityDeque;
      boolean flag1 = this.mc.renderChunksMany;
      if (renderchunk != null && renderchunk.getPosition().getY() <= j) {
        boolean flag2 = false;
        ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation4 = new ContainerLocalRenderInformation(renderchunk, (EnumFacing)null, 0);
        Set set1 = SET_ALL_FACINGS;
        if (set1.size() == 1) {
          Vector3f vector3f = getViewVector(viewEntity, partialTicks);
          EnumFacing enumfacing2 = EnumFacing.getFacingFromVector(vector3f.x, vector3f.y, vector3f.z).getOpposite();
          set1.remove(enumfacing2);
        } 
        if (set1.isEmpty())
          flag2 = true; 
        if (flag2 && !playerSpectator) {
          this.renderInfos.add(renderglobal$containerlocalrenderinformation4);
        } else {
          if (playerSpectator && this.theWorld.getBlockState(blockpos).getBlock().isOpaqueCube())
            flag1 = false; 
          renderchunk.setFrameIndex(frameCount);
          deque.add(renderglobal$containerlocalrenderinformation4);
        } 
      } else {
        int j1 = (blockpos.getY() > 0) ? Math.min(j, 248) : 8;
        if (renderchunk1 != null)
          this.renderInfosEntities.add(renderchunk1.getRenderInfo()); 
        for (int k = -this.renderDistanceChunks; k <= this.renderDistanceChunks; k++) {
          for (int l = -this.renderDistanceChunks; l <= this.renderDistanceChunks; l++) {
            RenderChunk renderchunk3 = this.viewFrustum.getRenderChunk(new BlockPos((k << 4) + 8, j1, (l << 4) + 8));
            if (renderchunk3 != null && renderchunk3.isBoundingBoxInFrustum((ICamera)frustum, frameCount)) {
              renderchunk3.setFrameIndex(frameCount);
              ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 = renderchunk3.getRenderInfo();
              renderglobal$containerlocalrenderinformation1.initialize((EnumFacing)null, 0);
              deque.add(renderglobal$containerlocalrenderinformation1);
            } 
          } 
        } 
      } 
      this.mc.mcProfiler.startSection("iteration");
      boolean flag3 = Config.isFogOn();
      while (!deque.isEmpty()) {
        ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation5 = deque.poll();
        RenderChunk renderchunk6 = renderglobal$containerlocalrenderinformation5.renderChunk;
        EnumFacing enumfacing1 = renderglobal$containerlocalrenderinformation5.facing;
        CompiledChunk compiledchunk = renderchunk6.compiledChunk;
        if (!compiledchunk.isEmpty() || renderchunk6.isNeedsUpdate())
          this.renderInfos.add(renderglobal$containerlocalrenderinformation5); 
        if (ChunkUtils.hasEntities(renderchunk6.getChunk()))
          this.renderInfosEntities.add(renderglobal$containerlocalrenderinformation5); 
        if (compiledchunk.getTileEntities().size() > 0)
          this.renderInfosTileEntities.add(renderglobal$containerlocalrenderinformation5); 
        for (EnumFacing enumfacing : flag1 ? ChunkVisibility.getFacingsNotOpposite(renderglobal$containerlocalrenderinformation5.setFacing) : EnumFacing.VALUES) {
          if (!flag1 || enumfacing1 == null || compiledchunk.isVisible(enumfacing1.getOpposite(), enumfacing)) {
            RenderChunk renderchunk4 = getRenderChunkOffset(blockpos, renderchunk6, enumfacing, flag3, j);
            if (renderchunk4 != null && renderchunk4.setFrameIndex(frameCount) && renderchunk4.isBoundingBoxInFrustum((ICamera)frustum, frameCount)) {
              int i1 = renderglobal$containerlocalrenderinformation5.setFacing | 1 << enumfacing.ordinal();
              ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation2 = renderchunk4.getRenderInfo();
              renderglobal$containerlocalrenderinformation2.initialize(enumfacing, i1);
              deque.add(renderglobal$containerlocalrenderinformation2);
            } 
          } 
        } 
      } 
      this.mc.mcProfiler.endSection();
    } 
    this.mc.mcProfiler.endStartSection("captureFrustum");
    if (this.debugFixTerrainFrustum) {
      fixTerrainFrustum(d3, d4, d5);
      this.debugFixTerrainFrustum = false;
    } 
    Lagometer.timerVisibility.end();
    if (Shaders.isShadowPass) {
      Shaders.mcProfilerEndSection();
    } else {
      this.mc.mcProfiler.endStartSection("rebuildNear");
      this.renderDispatcher.clearChunkUpdates();
      Set<RenderChunk> set = this.chunksToUpdate;
      this.chunksToUpdate = Sets.newLinkedHashSet();
      Lagometer.timerChunkUpdate.start();
      for (ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation3 : this.renderInfos) {
        RenderChunk renderchunk5 = renderglobal$containerlocalrenderinformation3.renderChunk;
        if (renderchunk5.isNeedsUpdate() || set.contains(renderchunk5)) {
          this.displayListEntitiesDirty = true;
          BlockPos blockpos1 = renderchunk5.getPosition();
          boolean flag4 = (blockpos.distanceSq((blockpos1.getX() + 8), (blockpos1.getY() + 8), (blockpos1.getZ() + 8)) < 768.0D);
          if (!flag4) {
            this.chunksToUpdate.add(renderchunk5);
            continue;
          } 
          if (!renderchunk5.isPlayerUpdate()) {
            this.chunksToUpdateForced.add(renderchunk5);
            continue;
          } 
          this.mc.mcProfiler.startSection("build near");
          this.renderDispatcher.updateChunkNow(renderchunk5);
          renderchunk5.setNeedsUpdate(false);
          this.mc.mcProfiler.endSection();
        } 
      } 
      Lagometer.timerChunkUpdate.end();
      this.chunksToUpdate.addAll(set);
      this.mc.mcProfiler.endSection();
    } 
  }
  
  private boolean isPositionInRenderChunk(BlockPos pos, RenderChunk renderChunkIn) {
    BlockPos blockpos = renderChunkIn.getPosition();
    return (MathHelper.abs_int(pos.getX() - blockpos.getX()) > 16) ? false : ((MathHelper.abs_int(pos.getY() - blockpos.getY()) > 16) ? false : ((MathHelper.abs_int(pos.getZ() - blockpos.getZ()) <= 16)));
  }
  
  private Set<EnumFacing> getVisibleFacings(BlockPos pos) {
    VisGraph visgraph = new VisGraph();
    BlockPos blockpos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
    Chunk chunk = this.theWorld.getChunkFromBlockCoords(blockpos);
    for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos.add(15, 15, 15))) {
      if (chunk.getBlock((BlockPos)blockpos$mutableblockpos).isOpaqueCube())
        visgraph.func_178606_a((BlockPos)blockpos$mutableblockpos); 
    } 
    return visgraph.func_178609_b(pos);
  }
  
  private RenderChunk getRenderChunkOffset(BlockPos p_getRenderChunkOffset_1_, RenderChunk p_getRenderChunkOffset_2_, EnumFacing p_getRenderChunkOffset_3_, boolean p_getRenderChunkOffset_4_, int p_getRenderChunkOffset_5_) {
    RenderChunk renderchunk = p_getRenderChunkOffset_2_.getRenderChunkNeighbour(p_getRenderChunkOffset_3_);
    if (renderchunk == null)
      return null; 
    if (renderchunk.getPosition().getY() > p_getRenderChunkOffset_5_)
      return null; 
    if (p_getRenderChunkOffset_4_) {
      BlockPos blockpos = renderchunk.getPosition();
      int i = p_getRenderChunkOffset_1_.getX() - blockpos.getX();
      int j = p_getRenderChunkOffset_1_.getZ() - blockpos.getZ();
      int k = i * i + j * j;
      if (k > this.renderDistanceSq)
        return null; 
    } 
    return renderchunk;
  }
  
  private void fixTerrainFrustum(double x, double y, double z) {
    this.debugFixedClippingHelper = (ClippingHelper)new ClippingHelperImpl();
    ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
    Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
    matrix4f.transpose();
    Matrix4f matrix4f1 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
    matrix4f1.transpose();
    Matrix4f matrix4f2 = new Matrix4f();
    Matrix4f.mul((Matrix4f)matrix4f1, (Matrix4f)matrix4f, (Matrix4f)matrix4f2);
    matrix4f2.invert();
    this.debugTerrainFrustumPosition.x = x;
    this.debugTerrainFrustumPosition.y = y;
    this.debugTerrainFrustumPosition.z = z;
    this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
    this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
    this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
    this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
    this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
    this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
    this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);
    for (int i = 0; i < 8; i++) {
      Matrix4f.transform((Matrix4f)matrix4f2, this.debugTerrainMatrix[i], this.debugTerrainMatrix[i]);
      (this.debugTerrainMatrix[i]).x /= (this.debugTerrainMatrix[i]).w;
      (this.debugTerrainMatrix[i]).y /= (this.debugTerrainMatrix[i]).w;
      (this.debugTerrainMatrix[i]).z /= (this.debugTerrainMatrix[i]).w;
      (this.debugTerrainMatrix[i]).w = 1.0F;
    } 
  }
  
  protected Vector3f getViewVector(Entity entityIn, double partialTicks) {
    float f = (float)(entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
    float f1 = (float)(entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);
    if ((Minecraft.getMinecraft()).gameSettings.thirdPersonView == 2)
      f += 180.0F; 
    float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
    float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
    float f4 = -MathHelper.cos(-f * 0.017453292F);
    float f5 = MathHelper.sin(-f * 0.017453292F);
    return new Vector3f(f3 * f4, f5, f2 * f4);
  }
  
  public int renderBlockLayer(EnumWorldBlockLayer blockLayerIn, double partialTicks, int pass, Entity entityIn) {
    RenderHelper.disableStandardItemLighting();
    if (blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT && !Shaders.isShadowPass) {
      this.mc.mcProfiler.startSection("translucent_sort");
      double d0 = entityIn.posX - this.prevRenderSortX;
      double d1 = entityIn.posY - this.prevRenderSortY;
      double d2 = entityIn.posZ - this.prevRenderSortZ;
      if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
        this.prevRenderSortX = entityIn.posX;
        this.prevRenderSortY = entityIn.posY;
        this.prevRenderSortZ = entityIn.posZ;
        int k = 0;
        this.chunksToResortTransparency.clear();
        for (ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos) {
          if (renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) && k++ < 15)
            this.chunksToResortTransparency.add(renderglobal$containerlocalrenderinformation.renderChunk); 
        } 
      } 
      this.mc.mcProfiler.endSection();
    } 
    this.mc.mcProfiler.startSection("filterempty");
    int l = 0;
    boolean flag = (blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT);
    int i1 = flag ? (this.renderInfos.size() - 1) : 0;
    int i = flag ? -1 : this.renderInfos.size();
    int j1 = flag ? -1 : 1;
    int j;
    for (j = i1; j != i; j += j1) {
      RenderChunk renderchunk = ((ContainerLocalRenderInformation)this.renderInfos.get(j)).renderChunk;
      if (!renderchunk.getCompiledChunk().isLayerEmpty(blockLayerIn)) {
        l++;
        this.renderContainer.addRenderChunk(renderchunk, blockLayerIn);
      } 
    } 
    if (l == 0) {
      this.mc.mcProfiler.endSection();
      return l;
    } 
    if (Config.isFogOff() && this.mc.entityRenderer.fogStandard)
      GlStateManager.disableFog(); 
    this.mc.mcProfiler.endStartSection("render_" + blockLayerIn);
    renderBlockLayer(blockLayerIn);
    this.mc.mcProfiler.endSection();
    return l;
  }
  
  private void renderBlockLayer(EnumWorldBlockLayer blockLayerIn) {
    this.mc.entityRenderer.enableLightmap();
    if (OpenGlHelper.useVbo()) {
      GL11.glEnableClientState(32884);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
      GL11.glEnableClientState(32888);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glEnableClientState(32888);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
      GL11.glEnableClientState(32886);
    } 
    if (Config.isShaders())
      ShadersRender.preRenderChunkLayer(blockLayerIn); 
    this.renderContainer.renderChunkLayer(blockLayerIn);
    if (Config.isShaders())
      ShadersRender.postRenderChunkLayer(blockLayerIn); 
    if (OpenGlHelper.useVbo())
      for (VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements()) {
        VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
        int i = vertexformatelement.getIndex();
        switch (vertexformatelement$enumusage) {
          case POSITION:
            GL11.glDisableClientState(32884);
          case UV:
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i);
            GL11.glDisableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
          case COLOR:
            GL11.glDisableClientState(32886);
            GlStateManager.resetColor();
        } 
      }  
    this.mc.entityRenderer.disableLightmap();
  }
  
  private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn) {
    while (iteratorIn.hasNext()) {
      DestroyBlockProgress destroyblockprogress = iteratorIn.next();
      int i = destroyblockprogress.getCreationCloudUpdateTick();
      if (this.cloudTickCounter - i > 400)
        iteratorIn.remove(); 
    } 
  }
  
  public void updateClouds() {
    if (Config.isShaders()) {
      if (Keyboard.isKeyDown(61) && Keyboard.isKeyDown(24)) {
        GuiShaderOptions guishaderoptions = new GuiShaderOptions((GuiScreen)null, Config.getGameSettings());
        Config.getMinecraft().displayGuiScreen((GuiScreen)guishaderoptions);
      } 
      if (Keyboard.isKeyDown(61) && Keyboard.isKeyDown(19)) {
        Shaders.uninit();
        Shaders.loadShaderPack();
      } 
    } 
    this.cloudTickCounter++;
    if (this.cloudTickCounter % 20 == 0)
      cleanupDamagedBlocks(this.damagedBlocks.values().iterator()); 
  }
  
  private void renderSkyEnd() {
    if (Config.isSkyEnabled()) {
      GlStateManager.disableFog();
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.depthMask(false);
      this.renderEngine.bindTexture(locationEndSkyPng);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      for (int i = 0; i < 6; i++) {
        GlStateManager.pushMatrix();
        if (i == 1)
          GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F); 
        if (i == 2)
          GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F); 
        if (i == 3)
          GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F); 
        if (i == 4)
          GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F); 
        if (i == 5)
          GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F); 
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int j = 40;
        int k = 40;
        int l = 40;
        if (Config.isCustomColors()) {
          Vec3 vec3 = new Vec3(j / 255.0D, k / 255.0D, l / 255.0D);
          vec3 = CustomColors.getWorldSkyColor(vec3, (World)this.theWorld, this.mc.getRenderViewEntity(), 0.0F);
          j = (int)(vec3.xCoord * 255.0D);
          k = (int)(vec3.yCoord * 255.0D);
          l = (int)(vec3.zCoord * 255.0D);
        } 
        worldrenderer.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(j, k, l, 255).endVertex();
        worldrenderer.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(j, k, l, 255).endVertex();
        worldrenderer.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(j, k, l, 255).endVertex();
        worldrenderer.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(j, k, l, 255).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
      } 
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
    } 
  }
  
  public void renderSky(float partialTicks, int pass) {
    if (Reflector.ForgeWorldProvider_getSkyRenderer.exists()) {
      WorldProvider worldprovider = this.mc.theWorld.provider;
      Object object = Reflector.call(worldprovider, Reflector.ForgeWorldProvider_getSkyRenderer, new Object[0]);
      if (object != null) {
        Reflector.callVoid(object, Reflector.IRenderHandler_render, new Object[] { Float.valueOf(partialTicks), this.theWorld, this.mc });
        return;
      } 
    } 
    if (this.mc.theWorld.provider.getDimensionId() == 1) {
      renderSkyEnd();
    } else if (this.mc.theWorld.provider.isSurfaceWorld()) {
      GlStateManager.disableTexture2D();
      boolean flag = Config.isShaders();
      if (flag)
        Shaders.disableTexture2D(); 
      Vec3 vec3 = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
      vec3 = CustomColors.getSkyColor(vec3, (IBlockAccess)this.mc.theWorld, (this.mc.getRenderViewEntity()).posX, (this.mc.getRenderViewEntity()).posY + 1.0D, (this.mc.getRenderViewEntity()).posZ);
      if (flag)
        Shaders.setSkyColor(vec3); 
      float f = (float)vec3.xCoord;
      float f1 = (float)vec3.yCoord;
      float f2 = (float)vec3.zCoord;
      if (pass != 2) {
        float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
        float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
        float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
        f = f3;
        f1 = f4;
        f2 = f5;
      } 
      GlStateManager.color(f, f1, f2);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GlStateManager.depthMask(false);
      GlStateManager.enableFog();
      if (flag)
        Shaders.enableFog(); 
      GlStateManager.color(f, f1, f2);
      if (flag)
        Shaders.preSkyList(); 
      if (Config.isSkyEnabled())
        if (this.vboEnabled) {
          this.skyVBO.bindBuffer();
          GL11.glEnableClientState(32884);
          GL11.glVertexPointer(3, 5126, 12, 0L);
          this.skyVBO.drawArrays(7);
          this.skyVBO.unbindBuffer();
          GL11.glDisableClientState(32884);
        } else {
          GlStateManager.callList(this.glSkyList);
        }  
      GlStateManager.disableFog();
      if (flag)
        Shaders.disableFog(); 
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      RenderHelper.disableStandardItemLighting();
      float[] afloat = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);
      if (afloat != null && Config.isSunMoonEnabled()) {
        GlStateManager.disableTexture2D();
        if (flag)
          Shaders.disableTexture2D(); 
        GlStateManager.shadeModel(7425);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F) ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        float f6 = afloat[0];
        float f7 = afloat[1];
        float f8 = afloat[2];
        if (pass != 2) {
          float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
          float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
          float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
          f6 = f9;
          f7 = f10;
          f8 = f11;
        } 
        worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
        int j = 16;
        for (int l = 0; l <= 16; l++) {
          float f18 = l * 3.1415927F * 2.0F / 16.0F;
          float f12 = MathHelper.sin(f18);
          float f13 = MathHelper.cos(f18);
          worldrenderer.pos((f12 * 120.0F), (f13 * 120.0F), (-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
        } 
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.shadeModel(7424);
      } 
      GlStateManager.enableTexture2D();
      if (flag)
        Shaders.enableTexture2D(); 
      GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
      GlStateManager.pushMatrix();
      float f15 = 1.0F - this.theWorld.getRainStrength(partialTicks);
      GlStateManager.color(1.0F, 1.0F, 1.0F, f15);
      GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
      CustomSky.renderSky((World)this.theWorld, this.renderEngine, partialTicks);
      if (flag)
        Shaders.preCelestialRotate(); 
      GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
      if (flag)
        Shaders.postCelestialRotate(); 
      float f16 = 30.0F;
      if (Config.isSunTexture()) {
        this.renderEngine.bindTexture(locationSunPng);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-f16, 100.0D, -f16).tex(0.0D, 0.0D).endVertex();
        worldrenderer.pos(f16, 100.0D, -f16).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(f16, 100.0D, f16).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(-f16, 100.0D, f16).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
      } 
      f16 = 20.0F;
      if (Config.isMoonTexture()) {
        this.renderEngine.bindTexture(locationMoonPhasesPng);
        int i = this.theWorld.getMoonPhase();
        int k = i % 4;
        int i1 = i / 4 % 2;
        float f19 = (k + 0) / 4.0F;
        float f21 = (i1 + 0) / 2.0F;
        float f23 = (k + 1) / 4.0F;
        float f14 = (i1 + 1) / 2.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-f16, -100.0D, f16).tex(f23, f14).endVertex();
        worldrenderer.pos(f16, -100.0D, f16).tex(f19, f14).endVertex();
        worldrenderer.pos(f16, -100.0D, -f16).tex(f19, f21).endVertex();
        worldrenderer.pos(-f16, -100.0D, -f16).tex(f23, f21).endVertex();
        tessellator.draw();
      } 
      GlStateManager.disableTexture2D();
      if (flag)
        Shaders.disableTexture2D(); 
      float f17 = this.theWorld.getStarBrightness(partialTicks) * f15;
      if (f17 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers((World)this.theWorld)) {
        GlStateManager.color(f17, f17, f17, f17);
        if (this.vboEnabled) {
          this.starVBO.bindBuffer();
          GL11.glEnableClientState(32884);
          GL11.glVertexPointer(3, 5126, 12, 0L);
          this.starVBO.drawArrays(7);
          this.starVBO.unbindBuffer();
          GL11.glDisableClientState(32884);
        } else {
          GlStateManager.callList(this.starGLCallList);
        } 
      } 
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableFog();
      if (flag)
        Shaders.enableFog(); 
      GlStateManager.popMatrix();
      GlStateManager.disableTexture2D();
      if (flag)
        Shaders.disableTexture2D(); 
      GlStateManager.color(0.0F, 0.0F, 0.0F);
      double d0 = (this.mc.thePlayer.getPositionEyes(partialTicks)).yCoord - this.theWorld.getHorizon();
      if (d0 < 0.0D) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 12.0F, 0.0F);
        if (this.vboEnabled) {
          this.sky2VBO.bindBuffer();
          GL11.glEnableClientState(32884);
          GL11.glVertexPointer(3, 5126, 12, 0L);
          this.sky2VBO.drawArrays(7);
          this.sky2VBO.unbindBuffer();
          GL11.glDisableClientState(32884);
        } else {
          GlStateManager.callList(this.glSkyList2);
        } 
        GlStateManager.popMatrix();
        float f20 = 1.0F;
        float f22 = -((float)(d0 + 65.0D));
        float f24 = -1.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-1.0D, f22, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, f22, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, f22, -1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(-1.0D, f22, -1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, f22, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, f22, -1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(-1.0D, f22, -1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(-1.0D, f22, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
        tessellator.draw();
      } 
      if (this.theWorld.provider.isSkyColored()) {
        GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
      } else {
        GlStateManager.color(f, f1, f2);
      } 
      if (this.mc.gameSettings.renderDistanceChunks <= 4)
        GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue); 
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, -((float)(d0 - 16.0D)), 0.0F);
      if (Config.isSkyEnabled())
        if (this.vboEnabled) {
          this.sky2VBO.bindBuffer();
          GlStateManager.glEnableClientState(32884);
          GlStateManager.glVertexPointer(3, 5126, 12, 0);
          this.sky2VBO.drawArrays(7);
          this.sky2VBO.unbindBuffer();
          GlStateManager.glDisableClientState(32884);
        } else {
          GlStateManager.callList(this.glSkyList2);
        }  
      GlStateManager.popMatrix();
      GlStateManager.enableTexture2D();
      if (flag)
        Shaders.enableTexture2D(); 
      GlStateManager.depthMask(true);
    } 
  }
  
  public void renderClouds(float partialTicks, int pass) {
    if (!Config.isCloudsOff()) {
      if (Reflector.ForgeWorldProvider_getCloudRenderer.exists()) {
        WorldProvider worldprovider = this.mc.theWorld.provider;
        Object object = Reflector.call(worldprovider, Reflector.ForgeWorldProvider_getCloudRenderer, new Object[0]);
        if (object != null) {
          Reflector.callVoid(object, Reflector.IRenderHandler_render, new Object[] { Float.valueOf(partialTicks), this.theWorld, this.mc });
          return;
        } 
      } 
      if (this.mc.theWorld.provider.isSurfaceWorld()) {
        if (Config.isShaders())
          Shaders.beginClouds(); 
        if (Config.isCloudsFancy()) {
          renderCloudsFancy(partialTicks, pass);
        } else {
          float f9 = partialTicks;
          partialTicks = 0.0F;
          GlStateManager.disableCull();
          float f10 = (float)((this.mc.getRenderViewEntity()).lastTickPosY + ((this.mc.getRenderViewEntity()).posY - (this.mc.getRenderViewEntity()).lastTickPosY) * partialTicks);
          int i = 32;
          int j = 8;
          Tessellator tessellator = Tessellator.getInstance();
          WorldRenderer worldrenderer = tessellator.getWorldRenderer();
          this.renderEngine.bindTexture(locationCloudsPng);
          GlStateManager.enableBlend();
          GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
          Vec3 vec3 = this.theWorld.getCloudColour(partialTicks);
          float f = (float)vec3.xCoord;
          float f1 = (float)vec3.yCoord;
          float f2 = (float)vec3.zCoord;
          this.cloudRenderer.prepareToRender(false, this.cloudTickCounter, f9, vec3);
          if (this.cloudRenderer.shouldUpdateGlList()) {
            this.cloudRenderer.startUpdateGlList();
            if (pass != 2) {
              float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
              float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
              float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
              f = f3;
              f1 = f4;
              f2 = f5;
            } 
            float f11 = 4.8828125E-4F;
            double d2 = (this.cloudTickCounter + partialTicks);
            double d0 = (this.mc.getRenderViewEntity()).prevPosX + ((this.mc.getRenderViewEntity()).posX - (this.mc.getRenderViewEntity()).prevPosX) * partialTicks + d2 * 0.029999999329447746D;
            double d1 = (this.mc.getRenderViewEntity()).prevPosZ + ((this.mc.getRenderViewEntity()).posZ - (this.mc.getRenderViewEntity()).prevPosZ) * partialTicks;
            int k = MathHelper.floor_double(d0 / 2048.0D);
            int l = MathHelper.floor_double(d1 / 2048.0D);
            d0 -= (k * 2048);
            d1 -= (l * 2048);
            float f6 = this.theWorld.provider.getCloudHeight() - f10 + 0.33F;
            f6 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
            float f7 = (float)(d0 * 4.8828125E-4D);
            float f8 = (float)(d1 * 4.8828125E-4D);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            for (int i1 = -256; i1 < 256; i1 += 32) {
              for (int j1 = -256; j1 < 256; j1 += 32) {
                worldrenderer.pos((i1 + 0), f6, (j1 + 32)).tex(((i1 + 0) * 4.8828125E-4F + f7), ((j1 + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                worldrenderer.pos((i1 + 32), f6, (j1 + 32)).tex(((i1 + 32) * 4.8828125E-4F + f7), ((j1 + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                worldrenderer.pos((i1 + 32), f6, (j1 + 0)).tex(((i1 + 32) * 4.8828125E-4F + f7), ((j1 + 0) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
                worldrenderer.pos((i1 + 0), f6, (j1 + 0)).tex(((i1 + 0) * 4.8828125E-4F + f7), ((j1 + 0) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
              } 
            } 
            tessellator.draw();
            this.cloudRenderer.endUpdateGlList();
          } 
          this.cloudRenderer.renderGlList();
          GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
          GlStateManager.disableBlend();
          GlStateManager.enableCull();
        } 
        if (Config.isShaders())
          Shaders.endClouds(); 
      } 
    } 
  }
  
  public boolean hasCloudFog(double x, double y, double z, float partialTicks) {
    return false;
  }
  
  private void renderCloudsFancy(float partialTicks, int pass) {
    partialTicks = 0.0F;
    GlStateManager.disableCull();
    float f = (float)((this.mc.getRenderViewEntity()).lastTickPosY + ((this.mc.getRenderViewEntity()).posY - (this.mc.getRenderViewEntity()).lastTickPosY) * partialTicks);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    float f1 = 12.0F;
    float f2 = 4.0F;
    double d0 = (this.cloudTickCounter + partialTicks);
    double d1 = ((this.mc.getRenderViewEntity()).prevPosX + ((this.mc.getRenderViewEntity()).posX - (this.mc.getRenderViewEntity()).prevPosX) * partialTicks + d0 * 0.029999999329447746D) / 12.0D;
    double d2 = ((this.mc.getRenderViewEntity()).prevPosZ + ((this.mc.getRenderViewEntity()).posZ - (this.mc.getRenderViewEntity()).prevPosZ) * partialTicks) / 12.0D + 0.33000001311302185D;
    float f3 = this.theWorld.provider.getCloudHeight() - f + 0.33F;
    f3 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
    int i = MathHelper.floor_double(d1 / 2048.0D);
    int j = MathHelper.floor_double(d2 / 2048.0D);
    d1 -= (i * 2048);
    d2 -= (j * 2048);
    this.renderEngine.bindTexture(locationCloudsPng);
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    Vec3 vec3 = this.theWorld.getCloudColour(partialTicks);
    float f4 = (float)vec3.xCoord;
    float f5 = (float)vec3.yCoord;
    float f6 = (float)vec3.zCoord;
    this.cloudRenderer.prepareToRender(true, this.cloudTickCounter, partialTicks, vec3);
    if (pass != 2) {
      float f7 = (f4 * 30.0F + f5 * 59.0F + f6 * 11.0F) / 100.0F;
      float f8 = (f4 * 30.0F + f5 * 70.0F) / 100.0F;
      float f9 = (f4 * 30.0F + f6 * 70.0F) / 100.0F;
      f4 = f7;
      f5 = f8;
      f6 = f9;
    } 
    float f26 = f4 * 0.9F;
    float f27 = f5 * 0.9F;
    float f28 = f6 * 0.9F;
    float f10 = f4 * 0.7F;
    float f11 = f5 * 0.7F;
    float f12 = f6 * 0.7F;
    float f13 = f4 * 0.8F;
    float f14 = f5 * 0.8F;
    float f15 = f6 * 0.8F;
    float f16 = 0.00390625F;
    float f17 = MathHelper.floor_double(d1) * 0.00390625F;
    float f18 = MathHelper.floor_double(d2) * 0.00390625F;
    float f19 = (float)(d1 - MathHelper.floor_double(d1));
    float f20 = (float)(d2 - MathHelper.floor_double(d2));
    int k = 8;
    int l = 4;
    float f21 = 9.765625E-4F;
    GlStateManager.scale(12.0F, 1.0F, 12.0F);
    for (int i1 = 0; i1 < 2; i1++) {
      if (i1 == 0) {
        GlStateManager.colorMask(false, false, false, false);
      } else {
        switch (pass) {
          case 0:
            GlStateManager.colorMask(false, true, true, true);
            break;
          case 1:
            GlStateManager.colorMask(true, false, false, true);
            break;
          case 2:
            GlStateManager.colorMask(true, true, true, true);
            break;
        } 
      } 
      this.cloudRenderer.renderGlList();
    } 
    if (this.cloudRenderer.shouldUpdateGlList()) {
      this.cloudRenderer.startUpdateGlList();
      for (int l1 = -3; l1 <= 4; l1++) {
        for (int j1 = -3; j1 <= 4; j1++) {
          worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
          float f22 = (l1 * 8);
          float f23 = (j1 * 8);
          float f24 = f22 - f19;
          float f25 = f23 - f20;
          if (f3 > -5.0F) {
            worldrenderer.pos((f24 + 0.0F), (f3 + 0.0F), (f25 + 8.0F)).tex(((f22 + 0.0F) * 0.00390625F + f17), ((f23 + 8.0F) * 0.00390625F + f18)).color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldrenderer.pos((f24 + 8.0F), (f3 + 0.0F), (f25 + 8.0F)).tex(((f22 + 8.0F) * 0.00390625F + f17), ((f23 + 8.0F) * 0.00390625F + f18)).color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldrenderer.pos((f24 + 8.0F), (f3 + 0.0F), (f25 + 0.0F)).tex(((f22 + 8.0F) * 0.00390625F + f17), ((f23 + 0.0F) * 0.00390625F + f18)).color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
            worldrenderer.pos((f24 + 0.0F), (f3 + 0.0F), (f25 + 0.0F)).tex(((f22 + 0.0F) * 0.00390625F + f17), ((f23 + 0.0F) * 0.00390625F + f18)).color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
          } 
          if (f3 <= 5.0F) {
            worldrenderer.pos((f24 + 0.0F), (f3 + 4.0F - 9.765625E-4F), (f25 + 8.0F)).tex(((f22 + 0.0F) * 0.00390625F + f17), ((f23 + 8.0F) * 0.00390625F + f18)).color(f4, f5, f6, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos((f24 + 8.0F), (f3 + 4.0F - 9.765625E-4F), (f25 + 8.0F)).tex(((f22 + 8.0F) * 0.00390625F + f17), ((f23 + 8.0F) * 0.00390625F + f18)).color(f4, f5, f6, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos((f24 + 8.0F), (f3 + 4.0F - 9.765625E-4F), (f25 + 0.0F)).tex(((f22 + 8.0F) * 0.00390625F + f17), ((f23 + 0.0F) * 0.00390625F + f18)).color(f4, f5, f6, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos((f24 + 0.0F), (f3 + 4.0F - 9.765625E-4F), (f25 + 0.0F)).tex(((f22 + 0.0F) * 0.00390625F + f17), ((f23 + 0.0F) * 0.00390625F + f18)).color(f4, f5, f6, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
          } 
          if (l1 > -1)
            for (int k1 = 0; k1 < 8; k1++) {
              worldrenderer.pos((f24 + k1 + 0.0F), (f3 + 0.0F), (f25 + 8.0F)).tex(((f22 + k1 + 0.5F) * 0.00390625F + f17), ((f23 + 8.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
              worldrenderer.pos((f24 + k1 + 0.0F), (f3 + 4.0F), (f25 + 8.0F)).tex(((f22 + k1 + 0.5F) * 0.00390625F + f17), ((f23 + 8.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
              worldrenderer.pos((f24 + k1 + 0.0F), (f3 + 4.0F), (f25 + 0.0F)).tex(((f22 + k1 + 0.5F) * 0.00390625F + f17), ((f23 + 0.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
              worldrenderer.pos((f24 + k1 + 0.0F), (f3 + 0.0F), (f25 + 0.0F)).tex(((f22 + k1 + 0.5F) * 0.00390625F + f17), ((f23 + 0.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
            }  
          if (l1 <= 1)
            for (int i2 = 0; i2 < 8; i2++) {
              worldrenderer.pos((f24 + i2 + 1.0F - 9.765625E-4F), (f3 + 0.0F), (f25 + 8.0F)).tex(((f22 + i2 + 0.5F) * 0.00390625F + f17), ((f23 + 8.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
              worldrenderer.pos((f24 + i2 + 1.0F - 9.765625E-4F), (f3 + 4.0F), (f25 + 8.0F)).tex(((f22 + i2 + 0.5F) * 0.00390625F + f17), ((f23 + 8.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
              worldrenderer.pos((f24 + i2 + 1.0F - 9.765625E-4F), (f3 + 4.0F), (f25 + 0.0F)).tex(((f22 + i2 + 0.5F) * 0.00390625F + f17), ((f23 + 0.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
              worldrenderer.pos((f24 + i2 + 1.0F - 9.765625E-4F), (f3 + 0.0F), (f25 + 0.0F)).tex(((f22 + i2 + 0.5F) * 0.00390625F + f17), ((f23 + 0.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
            }  
          if (j1 > -1)
            for (int j2 = 0; j2 < 8; j2++) {
              worldrenderer.pos((f24 + 0.0F), (f3 + 4.0F), (f25 + j2 + 0.0F)).tex(((f22 + 0.0F) * 0.00390625F + f17), ((f23 + j2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
              worldrenderer.pos((f24 + 8.0F), (f3 + 4.0F), (f25 + j2 + 0.0F)).tex(((f22 + 8.0F) * 0.00390625F + f17), ((f23 + j2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
              worldrenderer.pos((f24 + 8.0F), (f3 + 0.0F), (f25 + j2 + 0.0F)).tex(((f22 + 8.0F) * 0.00390625F + f17), ((f23 + j2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
              worldrenderer.pos((f24 + 0.0F), (f3 + 0.0F), (f25 + j2 + 0.0F)).tex(((f22 + 0.0F) * 0.00390625F + f17), ((f23 + j2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
            }  
          if (j1 <= 1)
            for (int k2 = 0; k2 < 8; k2++) {
              worldrenderer.pos((f24 + 0.0F), (f3 + 4.0F), (f25 + k2 + 1.0F - 9.765625E-4F)).tex(((f22 + 0.0F) * 0.00390625F + f17), ((f23 + k2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
              worldrenderer.pos((f24 + 8.0F), (f3 + 4.0F), (f25 + k2 + 1.0F - 9.765625E-4F)).tex(((f22 + 8.0F) * 0.00390625F + f17), ((f23 + k2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
              worldrenderer.pos((f24 + 8.0F), (f3 + 0.0F), (f25 + k2 + 1.0F - 9.765625E-4F)).tex(((f22 + 8.0F) * 0.00390625F + f17), ((f23 + k2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
              worldrenderer.pos((f24 + 0.0F), (f3 + 0.0F), (f25 + k2 + 1.0F - 9.765625E-4F)).tex(((f22 + 0.0F) * 0.00390625F + f17), ((f23 + k2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
            }  
          tessellator.draw();
        } 
      } 
      this.cloudRenderer.endUpdateGlList();
    } 
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.disableBlend();
    GlStateManager.enableCull();
  }
  
  public void updateChunks(long finishTimeNano) {
    finishTimeNano = (long)(finishTimeNano + 1.0E8D);
    this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);
    if (this.chunksToUpdateForced.size() > 0) {
      Iterator<RenderChunk> iterator = this.chunksToUpdateForced.iterator();
      while (iterator.hasNext()) {
        RenderChunk renderchunk = iterator.next();
        if (!this.renderDispatcher.updateChunkLater(renderchunk))
          break; 
        renderchunk.setNeedsUpdate(false);
        iterator.remove();
        this.chunksToUpdate.remove(renderchunk);
        this.chunksToResortTransparency.remove(renderchunk);
      } 
    } 
    if (this.chunksToResortTransparency.size() > 0) {
      Iterator<RenderChunk> iterator2 = this.chunksToResortTransparency.iterator();
      if (iterator2.hasNext()) {
        RenderChunk renderchunk2 = iterator2.next();
        if (this.renderDispatcher.updateTransparencyLater(renderchunk2))
          iterator2.remove(); 
      } 
    } 
    double d1 = 0.0D;
    int i = Config.getUpdatesPerFrame();
    if (!this.chunksToUpdate.isEmpty()) {
      Iterator<RenderChunk> iterator1 = this.chunksToUpdate.iterator();
      while (iterator1.hasNext()) {
        boolean flag1;
        RenderChunk renderchunk1 = iterator1.next();
        boolean flag = renderchunk1.isChunkRegionEmpty();
        if (flag) {
          flag1 = this.renderDispatcher.updateChunkNow(renderchunk1);
        } else {
          flag1 = this.renderDispatcher.updateChunkLater(renderchunk1);
        } 
        if (!flag1)
          break; 
        renderchunk1.setNeedsUpdate(false);
        iterator1.remove();
        if (!flag) {
          double d0 = 2.0D * RenderChunkUtils.getRelativeBufferSize(renderchunk1);
          d1 += d0;
          if (d1 > i)
            break; 
        } 
      } 
    } 
  }
  
  public void renderWorldBorder(Entity entityIn, float partialTicks) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    WorldBorder worldborder = this.theWorld.getWorldBorder();
    double d0 = (this.mc.gameSettings.renderDistanceChunks * 16);
    if (entityIn.posX >= worldborder.maxX() - d0 || entityIn.posX <= worldborder.minX() + d0 || entityIn.posZ >= worldborder.maxZ() - d0 || entityIn.posZ <= worldborder.minZ() + d0) {
      if (Config.isShaders()) {
        Shaders.pushProgram();
        Shaders.useProgram(Shaders.ProgramTexturedLit);
      } 
      double d1 = 1.0D - worldborder.getClosestDistance(entityIn) / d0;
      d1 = Math.pow(d1, 4.0D);
      double d2 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
      double d3 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
      double d4 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
      this.renderEngine.bindTexture(locationForcefieldPng);
      GlStateManager.depthMask(false);
      GlStateManager.pushMatrix();
      int i = worldborder.getStatus().getID();
      float f = (i >> 16 & 0xFF) / 255.0F;
      float f1 = (i >> 8 & 0xFF) / 255.0F;
      float f2 = (i & 0xFF) / 255.0F;
      GlStateManager.color(f, f1, f2, (float)d1);
      GlStateManager.doPolygonOffset(-3.0F, -3.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlpha();
      GlStateManager.disableCull();
      float f3 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
      float f4 = 0.0F;
      float f5 = 0.0F;
      float f6 = 128.0F;
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.setTranslation(-d2, -d3, -d4);
      double d5 = Math.max(MathHelper.floor_double(d4 - d0), worldborder.minZ());
      double d6 = Math.min(MathHelper.ceiling_double_int(d4 + d0), worldborder.maxZ());
      if (d2 > worldborder.maxX() - d0) {
        float f7 = 0.0F;
        for (double d7 = d5; d7 < d6; f7 += 0.5F) {
          double d8 = Math.min(1.0D, d6 - d7);
          float f8 = (float)d8 * 0.5F;
          worldrenderer.pos(worldborder.maxX(), 256.0D, d7).tex((f3 + f7), (f3 + 0.0F)).endVertex();
          worldrenderer.pos(worldborder.maxX(), 256.0D, d7 + d8).tex((f3 + f8 + f7), (f3 + 0.0F)).endVertex();
          worldrenderer.pos(worldborder.maxX(), 0.0D, d7 + d8).tex((f3 + f8 + f7), (f3 + 128.0F)).endVertex();
          worldrenderer.pos(worldborder.maxX(), 0.0D, d7).tex((f3 + f7), (f3 + 128.0F)).endVertex();
          d7++;
        } 
      } 
      if (d2 < worldborder.minX() + d0) {
        float f9 = 0.0F;
        for (double d9 = d5; d9 < d6; f9 += 0.5F) {
          double d12 = Math.min(1.0D, d6 - d9);
          float f12 = (float)d12 * 0.5F;
          worldrenderer.pos(worldborder.minX(), 256.0D, d9).tex((f3 + f9), (f3 + 0.0F)).endVertex();
          worldrenderer.pos(worldborder.minX(), 256.0D, d9 + d12).tex((f3 + f12 + f9), (f3 + 0.0F)).endVertex();
          worldrenderer.pos(worldborder.minX(), 0.0D, d9 + d12).tex((f3 + f12 + f9), (f3 + 128.0F)).endVertex();
          worldrenderer.pos(worldborder.minX(), 0.0D, d9).tex((f3 + f9), (f3 + 128.0F)).endVertex();
          d9++;
        } 
      } 
      d5 = Math.max(MathHelper.floor_double(d2 - d0), worldborder.minX());
      d6 = Math.min(MathHelper.ceiling_double_int(d2 + d0), worldborder.maxX());
      if (d4 > worldborder.maxZ() - d0) {
        float f10 = 0.0F;
        for (double d10 = d5; d10 < d6; f10 += 0.5F) {
          double d13 = Math.min(1.0D, d6 - d10);
          float f13 = (float)d13 * 0.5F;
          worldrenderer.pos(d10, 256.0D, worldborder.maxZ()).tex((f3 + f10), (f3 + 0.0F)).endVertex();
          worldrenderer.pos(d10 + d13, 256.0D, worldborder.maxZ()).tex((f3 + f13 + f10), (f3 + 0.0F)).endVertex();
          worldrenderer.pos(d10 + d13, 0.0D, worldborder.maxZ()).tex((f3 + f13 + f10), (f3 + 128.0F)).endVertex();
          worldrenderer.pos(d10, 0.0D, worldborder.maxZ()).tex((f3 + f10), (f3 + 128.0F)).endVertex();
          d10++;
        } 
      } 
      if (d4 < worldborder.minZ() + d0) {
        float f11 = 0.0F;
        for (double d11 = d5; d11 < d6; f11 += 0.5F) {
          double d14 = Math.min(1.0D, d6 - d11);
          float f14 = (float)d14 * 0.5F;
          worldrenderer.pos(d11, 256.0D, worldborder.minZ()).tex((f3 + f11), (f3 + 0.0F)).endVertex();
          worldrenderer.pos(d11 + d14, 256.0D, worldborder.minZ()).tex((f3 + f14 + f11), (f3 + 0.0F)).endVertex();
          worldrenderer.pos(d11 + d14, 0.0D, worldborder.minZ()).tex((f3 + f14 + f11), (f3 + 128.0F)).endVertex();
          worldrenderer.pos(d11, 0.0D, worldborder.minZ()).tex((f3 + f11), (f3 + 128.0F)).endVertex();
          d11++;
        } 
      } 
      tessellator.draw();
      worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
      GlStateManager.enableCull();
      GlStateManager.disableAlpha();
      GlStateManager.doPolygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      if (Config.isShaders())
        Shaders.popProgram(); 
    } 
  }
  
  private void preRenderDamagedBlocks() {
    GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
    GlStateManager.enableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
    GlStateManager.doPolygonOffset(-1.0F, -10.0F);
    GlStateManager.enablePolygonOffset();
    GlStateManager.alphaFunc(516, 0.1F);
    GlStateManager.enableAlpha();
    GlStateManager.pushMatrix();
    if (Config.isShaders())
      ShadersRender.beginBlockDamage(); 
  }
  
  private void postRenderDamagedBlocks() {
    GlStateManager.disableAlpha();
    GlStateManager.doPolygonOffset(0.0F, 0.0F);
    GlStateManager.disablePolygonOffset();
    GlStateManager.enableAlpha();
    GlStateManager.depthMask(true);
    GlStateManager.popMatrix();
    if (Config.isShaders())
      ShadersRender.endBlockDamage(); 
  }
  
  public void drawBlockDamageTexture(Tessellator tessellatorIn, WorldRenderer worldRendererIn, Entity entityIn, float partialTicks) {
    double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
    double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
    double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
    if (!this.damagedBlocks.isEmpty()) {
      this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      preRenderDamagedBlocks();
      worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
      worldRendererIn.setTranslation(-d0, -d1, -d2);
      worldRendererIn.noColor();
      Iterator<DestroyBlockProgress> iterator = this.damagedBlocks.values().iterator();
      while (iterator.hasNext()) {
        boolean flag;
        DestroyBlockProgress destroyblockprogress = iterator.next();
        BlockPos blockpos = destroyblockprogress.getPosition();
        double d3 = blockpos.getX() - d0;
        double d4 = blockpos.getY() - d1;
        double d5 = blockpos.getZ() - d2;
        Block block = this.theWorld.getBlockState(blockpos).getBlock();
        if (Reflector.ForgeTileEntity_canRenderBreaking.exists()) {
          boolean flag1 = (block instanceof net.minecraft.block.BlockChest || block instanceof net.minecraft.block.BlockEnderChest || block instanceof net.minecraft.block.BlockSign || block instanceof net.minecraft.block.BlockSkull);
          if (!flag1) {
            TileEntity tileentity = this.theWorld.getTileEntity(blockpos);
            if (tileentity != null)
              flag1 = Reflector.callBoolean(tileentity, Reflector.ForgeTileEntity_canRenderBreaking, new Object[0]); 
          } 
          flag = !flag1;
        } else {
          flag = (!(block instanceof net.minecraft.block.BlockChest) && !(block instanceof net.minecraft.block.BlockEnderChest) && !(block instanceof net.minecraft.block.BlockSign) && !(block instanceof net.minecraft.block.BlockSkull));
        } 
        if (flag) {
          if (d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D) {
            iterator.remove();
            continue;
          } 
          IBlockState iblockstate = this.theWorld.getBlockState(blockpos);
          if (iblockstate.getBlock().getMaterial() != Material.air) {
            int i = destroyblockprogress.getPartialBlockDamage();
            TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[i];
            BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
            blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, (IBlockAccess)this.theWorld);
          } 
        } 
      } 
      tessellatorIn.draw();
      worldRendererIn.setTranslation(0.0D, 0.0D, 0.0D);
      postRenderDamagedBlocks();
    } 
  }
  
  public void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks) {
    if (execute == 0 && movingObjectPositionIn.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
      GL11.glLineWidth(2.0F);
      GlStateManager.disableTexture2D();
      if (Config.isShaders())
        Shaders.disableTexture2D(); 
      GlStateManager.depthMask(false);
      float f = 0.002F;
      BlockPos blockpos = movingObjectPositionIn.getBlockPos();
      Block block = this.theWorld.getBlockState(blockpos).getBlock();
      if (block.getMaterial() != Material.air && this.theWorld.getWorldBorder().contains(blockpos)) {
        block.setBlockBoundsBasedOnState((IBlockAccess)this.theWorld, blockpos);
        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        AxisAlignedBB axisalignedbb = block.getSelectedBoundingBox((World)this.theWorld, blockpos);
        Block.EnumOffsetType block$enumoffsettype = block.getOffsetType();
        if (block$enumoffsettype != Block.EnumOffsetType.NONE)
          axisalignedbb = BlockModelUtils.getOffsetBoundingBox(axisalignedbb, block$enumoffsettype, blockpos); 
        drawSelectionBoundingBox(axisalignedbb.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2));
      } 
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      if (Config.isShaders())
        Shaders.enableTexture2D(); 
      GlStateManager.disableBlend();
    } 
  }
  
  public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(3, DefaultVertexFormats.POSITION);
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
    tessellator.draw();
    worldrenderer.begin(3, DefaultVertexFormats.POSITION);
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
    tessellator.draw();
    worldrenderer.begin(1, DefaultVertexFormats.POSITION);
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
    tessellator.draw();
  }
  
  public static void drawOutlinedBoundingBox(AxisAlignedBB boundingBox, int red, int green, int blue, int alpha) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    tessellator.draw();
    worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    tessellator.draw();
    worldrenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
    tessellator.draw();
  }
  
  private void markBlocksForUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
    this.viewFrustum.markBlocksForUpdate(x1, y1, z1, x2, y2, z2);
  }
  
  public void markBlockForUpdate(BlockPos pos) {
    int i = pos.getX();
    int j = pos.getY();
    int k = pos.getZ();
    markBlocksForUpdate(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
  }
  
  public void notifyLightSet(BlockPos pos) {
    int i = pos.getX();
    int j = pos.getY();
    int k = pos.getZ();
    markBlocksForUpdate(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
  }
  
  public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
    markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
  }
  
  public void playRecord(String recordName, BlockPos blockPosIn) {
    ISound isound = this.mapSoundPositions.get(blockPosIn);
    if (isound != null) {
      this.mc.getSoundHandler().stopSound(isound);
      this.mapSoundPositions.remove(blockPosIn);
    } 
    if (recordName != null) {
      ItemRecord itemrecord = ItemRecord.getRecord(recordName);
      if (itemrecord != null)
        this.mc.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordNameLocal()); 
      PositionedSoundRecord positionedsoundrecord = PositionedSoundRecord.create(new ResourceLocation(recordName), blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ());
      this.mapSoundPositions.put(blockPosIn, positionedsoundrecord);
      this.mc.getSoundHandler().playSound((ISound)positionedsoundrecord);
    } 
  }
  
  public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {}
  
  public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch) {}
  
  public void spawnParticle(int particleID, boolean ignoreRange, final double xCoord, final double yCoord, final double zCoord, double xOffset, double yOffset, double zOffset, int... parameters) {
    try {
      spawnEntityFX(particleID, ignoreRange, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
    } catch (Throwable throwable) {
      CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
      CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
      crashreportcategory.addCrashSection("ID", Integer.valueOf(particleID));
      if (parameters != null)
        crashreportcategory.addCrashSection("Parameters", parameters); 
      crashreportcategory.addCrashSectionCallable("Position", new Callable<String>() {
            public String call() throws Exception {
              return CrashReportCategory.getCoordinateInfo(xCoord, yCoord, zCoord);
            }
          });
      throw new ReportedException(crashreport);
    } 
  }
  
  private void spawnParticle(EnumParticleTypes particleIn, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters) {
    spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
  }
  
  private EntityFX spawnEntityFX(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters) {
    if (this.mc != null && this.mc.getRenderViewEntity() != null && this.mc.effectRenderer != null) {
      int i = this.mc.gameSettings.particleSetting;
      if (i == 1 && this.theWorld.rand.nextInt(3) == 0)
        i = 2; 
      double d0 = (this.mc.getRenderViewEntity()).posX - xCoord;
      double d1 = (this.mc.getRenderViewEntity()).posY - yCoord;
      double d2 = (this.mc.getRenderViewEntity()).posZ - zCoord;
      if (particleID == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion())
        return null; 
      if (particleID == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion())
        return null; 
      if (particleID == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion())
        return null; 
      if (particleID == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles())
        return null; 
      if (particleID == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles())
        return null; 
      if (particleID == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke())
        return null; 
      if (particleID == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke())
        return null; 
      if (particleID == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles())
        return null; 
      if (particleID == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles())
        return null; 
      if (particleID == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles())
        return null; 
      if (particleID == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles())
        return null; 
      if (particleID == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles())
        return null; 
      if (particleID == EnumParticleTypes.PORTAL.getParticleID() && !Config.isPortalParticles())
        return null; 
      if (particleID == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame())
        return null; 
      if (particleID == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone())
        return null; 
      if (particleID == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava())
        return null; 
      if (particleID == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava())
        return null; 
      if (particleID == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles())
        return null; 
      if (!ignoreRange) {
        double d3 = 256.0D;
        if (particleID == EnumParticleTypes.CRIT.getParticleID())
          d3 = 38416.0D; 
        if (d0 * d0 + d1 * d1 + d2 * d2 > d3)
          return null; 
        if (i > 1)
          return null; 
      } 
      EntityFX entityfx = this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
      if (particleID == EnumParticleTypes.WATER_BUBBLE.getParticleID())
        CustomColors.updateWaterFX(entityfx, (IBlockAccess)this.theWorld, xCoord, yCoord, zCoord, this.renderEnv); 
      if (particleID == EnumParticleTypes.WATER_SPLASH.getParticleID())
        CustomColors.updateWaterFX(entityfx, (IBlockAccess)this.theWorld, xCoord, yCoord, zCoord, this.renderEnv); 
      if (particleID == EnumParticleTypes.WATER_DROP.getParticleID())
        CustomColors.updateWaterFX(entityfx, (IBlockAccess)this.theWorld, xCoord, yCoord, zCoord, this.renderEnv); 
      if (particleID == EnumParticleTypes.TOWN_AURA.getParticleID())
        CustomColors.updateMyceliumFX(entityfx); 
      if (particleID == EnumParticleTypes.PORTAL.getParticleID())
        CustomColors.updatePortalFX(entityfx); 
      if (particleID == EnumParticleTypes.REDSTONE.getParticleID())
        CustomColors.updateReddustFX(entityfx, (IBlockAccess)this.theWorld, xCoord, yCoord, zCoord); 
      return entityfx;
    } 
    return null;
  }
  
  public void onEntityAdded(Entity entityIn) {
    RandomEntities.entityLoaded(entityIn, (World)this.theWorld);
    if (Config.isDynamicLights())
      DynamicLights.entityAdded(entityIn, this); 
  }
  
  public void onEntityRemoved(Entity entityIn) {
    RandomEntities.entityUnloaded(entityIn, (World)this.theWorld);
    if (Config.isDynamicLights())
      DynamicLights.entityRemoved(entityIn, this); 
  }
  
  public void deleteAllDisplayLists() {}
  
  public void broadcastSound(int soundID, BlockPos pos, int data) {
    switch (soundID) {
      case 1013:
      case 1018:
        if (this.mc.getRenderViewEntity() != null) {
          double d0 = pos.getX() - (this.mc.getRenderViewEntity()).posX;
          double d1 = pos.getY() - (this.mc.getRenderViewEntity()).posY;
          double d2 = pos.getZ() - (this.mc.getRenderViewEntity()).posZ;
          double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
          double d4 = (this.mc.getRenderViewEntity()).posX;
          double d5 = (this.mc.getRenderViewEntity()).posY;
          double d6 = (this.mc.getRenderViewEntity()).posZ;
          if (d3 > 0.0D) {
            d4 += d0 / d3 * 2.0D;
            d5 += d1 / d3 * 2.0D;
            d6 += d2 / d3 * 2.0D;
          } 
          if (soundID == 1013) {
            this.theWorld.playSound(d4, d5, d6, "mob.wither.spawn", 1.0F, 1.0F, false);
            break;
          } 
          this.theWorld.playSound(d4, d5, d6, "mob.enderdragon.end", 5.0F, 1.0F, false);
        } 
        break;
    } 
  }
  
  public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int data) {
    int i, j;
    double d0, d1, d2;
    int i1;
    Block block;
    double d3, d4, d5;
    int k, j1;
    float f, f1, f2;
    EnumParticleTypes enumparticletypes;
    int k1;
    double d6, d8, d10;
    int l1;
    double d22;
    int l;
    Random random = this.theWorld.rand;
    switch (sfxType) {
      case 1000:
        this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.0F, false);
        break;
      case 1001:
        this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.2F, false);
        break;
      case 1002:
        this.theWorld.playSoundAtPos(blockPosIn, "random.bow", 1.0F, 1.2F, false);
        break;
      case 1003:
        this.theWorld.playSoundAtPos(blockPosIn, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
        break;
      case 1004:
        this.theWorld.playSoundAtPos(blockPosIn, "random.fizz", 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
        break;
      case 1005:
        if (Item.getItemById(data) instanceof ItemRecord) {
          this.theWorld.playRecord(blockPosIn, "records." + ((ItemRecord)Item.getItemById(data)).recordName);
          break;
        } 
        this.theWorld.playRecord(blockPosIn, (String)null);
        break;
      case 1006:
        this.theWorld.playSoundAtPos(blockPosIn, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
        break;
      case 1007:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.charge", 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1008:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1009:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1010:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.wood", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1011:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.metal", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1012:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.woodbreak", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1014:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.wither.shoot", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1015:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.bat.takeoff", 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1016:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.infect", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1017:
        this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.unfect", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
        break;
      case 1020:
        this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
        break;
      case 1021:
        this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
        break;
      case 1022:
        this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
        break;
      case 2000:
        i = data % 3 - 1;
        j = data / 3 % 3 - 1;
        d0 = blockPosIn.getX() + i * 0.6D + 0.5D;
        d1 = blockPosIn.getY() + 0.5D;
        d2 = blockPosIn.getZ() + j * 0.6D + 0.5D;
        for (i1 = 0; i1 < 10; i1++) {
          double d15 = random.nextDouble() * 0.2D + 0.01D;
          double d16 = d0 + i * 0.01D + (random.nextDouble() - 0.5D) * j * 0.5D;
          double d17 = d1 + (random.nextDouble() - 0.5D) * 0.5D;
          double d18 = d2 + j * 0.01D + (random.nextDouble() - 0.5D) * i * 0.5D;
          double d19 = i * d15 + random.nextGaussian() * 0.01D;
          double d20 = -0.03D + random.nextGaussian() * 0.01D;
          double d21 = j * d15 + random.nextGaussian() * 0.01D;
          spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d16, d17, d18, d19, d20, d21, new int[0]);
        } 
        return;
      case 2001:
        block = Block.getBlockById(data & 0xFFF);
        if (block.getMaterial() != Material.air)
          this.mc.getSoundHandler().playSound((ISound)new PositionedSoundRecord(new ResourceLocation(block.stepSound.getBreakSound()), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getFrequency() * 0.8F, blockPosIn.getX() + 0.5F, blockPosIn.getY() + 0.5F, blockPosIn.getZ() + 0.5F)); 
        this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 0xFF));
        break;
      case 2002:
        d3 = blockPosIn.getX();
        d4 = blockPosIn.getY();
        d5 = blockPosIn.getZ();
        for (k = 0; k < 8; k++) {
          spawnParticle(EnumParticleTypes.ITEM_CRACK, d3, d4, d5, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] { Item.getIdFromItem((Item)Items.potionitem), data });
        } 
        j1 = Items.potionitem.getColorFromDamage(data);
        f = (j1 >> 16 & 0xFF) / 255.0F;
        f1 = (j1 >> 8 & 0xFF) / 255.0F;
        f2 = (j1 >> 0 & 0xFF) / 255.0F;
        enumparticletypes = EnumParticleTypes.SPELL;
        if (Items.potionitem.isEffectInstant(data))
          enumparticletypes = EnumParticleTypes.SPELL_INSTANT; 
        for (k1 = 0; k1 < 100; k1++) {
          double d7 = random.nextDouble() * 4.0D;
          double d9 = random.nextDouble() * Math.PI * 2.0D;
          double d11 = Math.cos(d9) * d7;
          double d23 = 0.01D + random.nextDouble() * 0.5D;
          double d24 = Math.sin(d9) * d7;
          EntityFX entityfx = spawnEntityFX(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d3 + d11 * 0.1D, d4 + 0.3D, d5 + d24 * 0.1D, d11, d23, d24, new int[0]);
          if (entityfx != null) {
            float f3 = 0.75F + random.nextFloat() * 0.25F;
            entityfx.setRBGColorF(f * f3, f1 * f3, f2 * f3);
            entityfx.multiplyVelocity((float)d7);
          } 
        } 
        this.theWorld.playSoundAtPos(blockPosIn, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
        break;
      case 2003:
        d6 = blockPosIn.getX() + 0.5D;
        d8 = blockPosIn.getY();
        d10 = blockPosIn.getZ() + 0.5D;
        for (l1 = 0; l1 < 8; l1++) {
          spawnParticle(EnumParticleTypes.ITEM_CRACK, d6, d8, d10, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] { Item.getIdFromItem(Items.ender_eye) });
        } 
        for (d22 = 0.0D; d22 < 6.283185307179586D; d22 += 0.15707963267948966D) {
          spawnParticle(EnumParticleTypes.PORTAL, d6 + Math.cos(d22) * 5.0D, d8 - 0.4D, d10 + Math.sin(d22) * 5.0D, Math.cos(d22) * -5.0D, 0.0D, Math.sin(d22) * -5.0D, new int[0]);
          spawnParticle(EnumParticleTypes.PORTAL, d6 + Math.cos(d22) * 5.0D, d8 - 0.4D, d10 + Math.sin(d22) * 5.0D, Math.cos(d22) * -7.0D, 0.0D, Math.sin(d22) * -7.0D, new int[0]);
        } 
        return;
      case 2004:
        for (l = 0; l < 20; l++) {
          double d12 = blockPosIn.getX() + 0.5D + (this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
          double d13 = blockPosIn.getY() + 0.5D + (this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
          double d14 = blockPosIn.getZ() + 0.5D + (this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
          this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d12, d13, d14, 0.0D, 0.0D, 0.0D, new int[0]);
          this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d12, d13, d14, 0.0D, 0.0D, 0.0D, new int[0]);
        } 
        return;
      case 2005:
        ItemDye.spawnBonemealParticles((World)this.theWorld, blockPosIn, data);
        break;
    } 
  }
  
  public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
    if (progress >= 0 && progress < 10) {
      DestroyBlockProgress destroyblockprogress = this.damagedBlocks.get(Integer.valueOf(breakerId));
      if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != pos.getX() || destroyblockprogress.getPosition().getY() != pos.getY() || destroyblockprogress.getPosition().getZ() != pos.getZ()) {
        destroyblockprogress = new DestroyBlockProgress(breakerId, pos);
        this.damagedBlocks.put(Integer.valueOf(breakerId), destroyblockprogress);
      } 
      destroyblockprogress.setPartialBlockDamage(progress);
      destroyblockprogress.setCloudUpdateTick(this.cloudTickCounter);
    } else {
      this.damagedBlocks.remove(Integer.valueOf(breakerId));
    } 
  }
  
  public void setDisplayListEntitiesDirty() {
    this.displayListEntitiesDirty = true;
  }
  
  public boolean hasNoChunkUpdates() {
    return (this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasChunkUpdates());
  }
  
  public void resetClouds() {
    this.cloudRenderer.reset();
  }
  
  public int getCountRenderers() {
    return this.viewFrustum.renderChunks.length;
  }
  
  public int getCountActiveRenderers() {
    return this.renderInfos.size();
  }
  
  public int getCountEntitiesRendered() {
    return this.countEntitiesRendered;
  }
  
  public int getCountTileEntitiesRendered() {
    return this.countTileEntitiesRendered;
  }
  
  public int getCountLoadedChunks() {
    if (this.theWorld == null)
      return 0; 
    IChunkProvider ichunkprovider = this.theWorld.getChunkProvider();
    if (ichunkprovider == null)
      return 0; 
    if (ichunkprovider != this.worldChunkProvider) {
      this.worldChunkProvider = ichunkprovider;
      this.worldChunkProviderMap = (LongHashMap)Reflector.getFieldValue(ichunkprovider, Reflector.ChunkProviderClient_chunkMapping);
    } 
    return (this.worldChunkProviderMap == null) ? 0 : this.worldChunkProviderMap.getNumHashElements();
  }
  
  public int getCountChunksToUpdate() {
    return this.chunksToUpdate.size();
  }
  
  public RenderChunk getRenderChunk(BlockPos p_getRenderChunk_1_) {
    return this.viewFrustum.getRenderChunk(p_getRenderChunk_1_);
  }
  
  public WorldClient getWorld() {
    return this.theWorld;
  }
  
  private void clearRenderInfos() {
    if (renderEntitiesCounter > 0) {
      this.renderInfos = new ArrayList<>(this.renderInfos.size() + 16);
      this.renderInfosEntities = new ArrayList(this.renderInfosEntities.size() + 16);
      this.renderInfosTileEntities = new ArrayList(this.renderInfosTileEntities.size() + 16);
    } else {
      this.renderInfos.clear();
      this.renderInfosEntities.clear();
      this.renderInfosTileEntities.clear();
    } 
  }
  
  public void onPlayerPositionSet() {
    if (this.firstWorldLoad) {
      loadRenderers();
      this.firstWorldLoad = false;
    } 
  }
  
  public void pauseChunkUpdates() {
    if (this.renderDispatcher != null)
      this.renderDispatcher.pauseChunkUpdates(); 
  }
  
  public void resumeChunkUpdates() {
    if (this.renderDispatcher != null)
      this.renderDispatcher.resumeChunkUpdates(); 
  }
  
  public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd) {
    synchronized (this.setTileEntities) {
      this.setTileEntities.removeAll(tileEntitiesToRemove);
      this.setTileEntities.addAll(tileEntitiesToAdd);
    } 
  }
  
  public static class ContainerLocalRenderInformation {
    final RenderChunk renderChunk;
    
    EnumFacing facing;
    
    int setFacing;
    
    public ContainerLocalRenderInformation(RenderChunk p_i2_1_, EnumFacing p_i2_2_, int p_i2_3_) {
      this.renderChunk = p_i2_1_;
      this.facing = p_i2_2_;
      this.setFacing = p_i2_3_;
    }
    
    public void setFacingBit(byte p_setFacingBit_1_, EnumFacing p_setFacingBit_2_) {
      this.setFacing = this.setFacing | p_setFacingBit_1_ | 1 << p_setFacingBit_2_.ordinal();
    }
    
    public boolean isFacingBit(EnumFacing p_isFacingBit_1_) {
      return ((this.setFacing & 1 << p_isFacingBit_1_.ordinal()) > 0);
    }
    
    private void initialize(EnumFacing p_initialize_1_, int p_initialize_2_) {
      this.facing = p_initialize_1_;
      this.setFacing = p_initialize_2_;
    }
  }
}
