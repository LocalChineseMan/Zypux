package net.minecraft.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.events.events.PressedKeyEvent;
import ir.lecer.uwu.impl.render.Performance;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.tools.renders.PictureUtils;
import ir.lecer.uwu.ui.menu.SplashProgress;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.stream.GuiStreamUnavailable;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Minecraft implements IThreadListener, IPlayerUsage {
  private static final Logger logger = LogManager.getLogger();
  
  public static final boolean isRunningOnMac = (Util.getOSType() == Util.EnumOS.OSX);
  
  public static byte[] memoryReserve = new byte[10485760];
  
  private static final List<DisplayMode> macDisplayModes = Lists.newArrayList((Object[])new DisplayMode[] { new DisplayMode(2560, 1600), new DisplayMode(2880, 1800) });
  
  private final File fileResourcepacks;
  
  private final PropertyMap twitchDetails;
  
  private final PropertyMap profileProperties;
  
  private ServerData currentServerData;
  
  private TextureManager renderEngine;
  
  private static Minecraft theMinecraft;
  
  public PlayerControllerMP playerController;
  
  private boolean fullscreen;
  
  private boolean hasCrashed;
  
  private CrashReport crashReporter;
  
  public int displayWidth;
  
  public int displayHeight;
  
  private boolean connectedToRealms = false;
  
  public final Timer timer = new Timer(20.0F);
  
  private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
  
  public WorldClient theWorld;
  
  public RenderGlobal renderGlobal;
  
  private RenderManager renderManager;
  
  private RenderItem renderItem;
  
  private ItemRenderer itemRenderer;
  
  public EntityPlayerSP thePlayer;
  
  private Entity renderViewEntity;
  
  public Entity pointedEntity;
  
  public EffectRenderer effectRenderer;
  
  private final Session session;
  
  private boolean isGamePaused;
  
  public FontRenderer fontRendererObj;
  
  public FontRenderer standardGalacticFontRenderer;
  
  public GuiScreen currentScreen;
  
  public LoadingScreenRenderer loadingScreen;
  
  public EntityRenderer entityRenderer;
  
  private int leftClickCounter;
  
  private final int tempDisplayWidth;
  
  private final int tempDisplayHeight;
  
  private IntegratedServer theIntegratedServer;
  
  public GuiAchievement guiAchievement;
  
  public GuiIngame ingameGUI;
  
  public boolean skipRenderWorld;
  
  public MovingObjectPosition objectMouseOver;
  
  public GameSettings gameSettings;
  
  public MouseHelper mouseHelper;
  
  public final File mcDataDir;
  
  private final File fileAssets;
  
  private final String launchedVersion;
  
  private final Proxy proxy;
  
  private ISaveFormat saveLoader;
  
  private static int debugFPS;
  
  private int rightClickDelayTimer;
  
  private String serverName;
  
  private int serverPort;
  
  public boolean inGameHasFocus;
  
  long systemTime = getSystemTime();
  
  private int joinPlayerCounter;
  
  public final FrameTimer frameTimer = new FrameTimer();
  
  long startNanoTime = System.nanoTime();
  
  private final boolean jvm64bit;
  
  private final boolean isDemo;
  
  private NetworkManager myNetworkManager;
  
  private boolean integratedServerIsRunning;
  
  public final Profiler mcProfiler = new Profiler();
  
  private long debugCrashKeyPressTime = -1L;
  
  private IReloadableResourceManager mcResourceManager;
  
  private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
  
  private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
  
  private final DefaultResourcePack mcDefaultResourcePack;
  
  private ResourcePackRepository mcResourcePackRepository;
  
  private LanguageManager mcLanguageManager;
  
  private IStream stream;
  
  private Framebuffer framebufferMc;
  
  private TextureMap textureMapBlocks;
  
  private SoundHandler mcSoundHandler;
  
  private MusicTicker mcMusicTicker;
  
  private ResourceLocation mojangLogo;
  
  private MinecraftSessionService sessionService;
  
  private SkinManager skinManager;
  
  private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
  
  private final Thread mcThread = Thread.currentThread();
  
  private BlockRendererDispatcher blockRenderDispatcher;
  
  public static boolean running = true;
  
  public String debug = "";
  
  public boolean renderChunksMany = true;
  
  long debugUpdateTime = getSystemTime();
  
  int fpsCounter;
  
  long prevFrameTime = -1L;
  
  private String debugProfilerName = "root";
  
  public Minecraft(GameConfiguration gameConfig) {
    theMinecraft = this;
    this.mcDataDir = gameConfig.folderInfo.mcDataDir;
    this.fileAssets = gameConfig.folderInfo.assetsDir;
    this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
    this.launchedVersion = gameConfig.gameInfo.version;
    this.twitchDetails = gameConfig.userInfo.userProperties;
    this.profileProperties = gameConfig.userInfo.profileProperties;
    this.mcDefaultResourcePack = new DefaultResourcePack((new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex)).getResourceMap());
    this.proxy = (gameConfig.userInfo.proxy == null) ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
    Proxy infoProxy = gameConfig.userInfo.proxy;
    if (infoProxy != null)
      this.sessionService = (new YggdrasilAuthenticationService(infoProxy, UUID.randomUUID().toString())).createMinecraftSessionService(); 
    this.session = gameConfig.userInfo.session;
    logger.info("Setting user: " + this.session.getUsername());
    logger.info("(Session ID is " + this.session.getSessionID() + ")");
    this.isDemo = gameConfig.gameInfo.isDemo;
    this.displayWidth = (gameConfig.displayInfo.width > 0) ? gameConfig.displayInfo.width : 1;
    this.displayHeight = (gameConfig.displayInfo.height > 0) ? gameConfig.displayInfo.height : 1;
    this.tempDisplayWidth = gameConfig.displayInfo.width;
    this.tempDisplayHeight = gameConfig.displayInfo.height;
    this.fullscreen = gameConfig.displayInfo.fullscreen;
    this.jvm64bit = isJvm64bit();
    this.theIntegratedServer = new IntegratedServer(this);
    if (gameConfig.serverInfo.serverName != null) {
      this.serverName = gameConfig.serverInfo.serverName;
      this.serverPort = gameConfig.serverInfo.serverPort;
    } 
    ImageIO.setUseCache(false);
    Bootstrap.register();
  }
  
  public void run() {
    running = true;
    try {
      startGame();
    } catch (Throwable throwable) {
      CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
      crashreport.makeCategory("Initialization");
      displayCrashReport(addGraphicsAndWorldToCrashReport(crashreport));
      return;
    } 
    try {
      while (running) {
        if (!this.hasCrashed || this.crashReporter == null) {
          try {
            runGameLoop();
          } catch (OutOfMemoryError var10) {
            freeMemory();
            displayGuiScreen((GuiScreen)new GuiMemoryErrorScreen());
            System.gc();
          } 
          continue;
        } 
        displayCrashReport(this.crashReporter);
      } 
    } catch (MinecraftError var12) {
    
    } catch (ReportedException reportedexception) {
      addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
      freeMemory();
      logger.fatal("Reported exception thrown!", (Throwable)reportedexception);
      displayCrashReport(reportedexception.getCrashReport());
    } catch (Throwable throwable1) {
      CrashReport crashreport1 = addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
      freeMemory();
      logger.fatal("Unreported exception thrown!", throwable1);
      displayCrashReport(crashreport1);
    } finally {
      shutdownMinecraftApplet();
    } 
  }
  
  private void startGame() throws LWJGLException {
    this.gameSettings = new GameSettings(this, this.mcDataDir);
    this.defaultResourcePacks.add(this.mcDefaultResourcePack);
    startTimerHackThread();
    if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
      this.displayWidth = this.gameSettings.overrideWidth;
      this.displayHeight = this.gameSettings.overrideHeight;
    } 
    logger.info("LWJGL Version: " + Sys.getVersion());
    setWindowIcon();
    setInitialDisplayMode();
    createDisplay();
    OpenGlHelper.initializeTextures();
    this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
    this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
    registerMetadataSerializers();
    this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), (IResourcePack)this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
    this.mcResourceManager = (IReloadableResourceManager)new SimpleReloadableResourceManager(this.metadataSerializer_);
    this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)this.mcLanguageManager);
    refreshResources();
    this.renderEngine = new TextureManager((IResourceManager)this.mcResourceManager);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)this.renderEngine);
    Zypux zypux = Zypux.getInstance();
    zypux.onLoad();
    SplashProgress.drawScreen(this.renderEngine);
    initStream();
    this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
    this.saveLoader = (ISaveFormat)new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
    this.mcSoundHandler = new SoundHandler((IResourceManager)this.mcResourceManager, this.gameSettings);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)this.mcSoundHandler);
    this.mcMusicTicker = new MusicTicker(this);
    this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
    if (this.gameSettings.language != null) {
      this.fontRendererObj.setUnicodeFlag(isUnicode());
      this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
    } 
    this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)this.fontRendererObj);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)this.standardGalacticFontRenderer);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)new GrassColorReloadListener());
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)new FoliageColorReloadListener());
    AchievementList.openInventory.setStatStringFormatter(str -> {
          try {
            return String.format(str, new Object[] { GameSettings.getKeyDisplayString(this.gameSettings.keyBindInventory.getKeyCode()) });
          } catch (Exception exception) {
            return "Error: " + exception.getLocalizedMessage();
          } 
        });
    this.mouseHelper = new MouseHelper();
    checkGLError("Pre startup");
    GlStateManager.enableTexture2D();
    GlStateManager.shadeModel(7425);
    GlStateManager.clearDepth(1.0D);
    GlStateManager.enableDepth();
    GlStateManager.depthFunc(515);
    GlStateManager.enableAlpha();
    GlStateManager.alphaFunc(516, 0.1F);
    GlStateManager.cullFace(1029);
    GlStateManager.matrixMode(5889);
    GlStateManager.loadIdentity();
    GlStateManager.matrixMode(5888);
    checkGLError("Startup");
    zypux.onStart();
    this.textureMapBlocks = new TextureMap("textures");
    this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
    this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, (ITickableTextureObject)this.textureMapBlocks);
    this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
    this.textureMapBlocks.setBlurMipmapDirect(false, (this.gameSettings.mipmapLevels > 0));
    ModelManager modelManager = new ModelManager(this.textureMapBlocks);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)modelManager);
    this.renderItem = new RenderItem(this.renderEngine, modelManager);
    this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
    this.itemRenderer = new ItemRenderer(this);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)this.renderItem);
    this.entityRenderer = new EntityRenderer(this, (IResourceManager)this.mcResourceManager);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)this.entityRenderer);
    this.blockRenderDispatcher = new BlockRendererDispatcher(modelManager.getBlockModelShapes(), this.gameSettings);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)this.blockRenderDispatcher);
    this.renderGlobal = new RenderGlobal(this);
    this.mcResourceManager.registerReloadListener((IResourceManagerReloadListener)this.renderGlobal);
    this.guiAchievement = new GuiAchievement(this);
    GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
    this.effectRenderer = new EffectRenderer((World)this.theWorld, this.renderEngine);
    checkGLError("Post startup");
    this.ingameGUI = new GuiIngame(this);
    displayGuiScreen((this.serverName != null) ? (GuiScreen)new GuiConnecting((GuiScreen)new GuiMainMenu(), this, this.serverName, this.serverPort) : (GuiScreen)new GuiMainMenu());
    this.renderEngine.deleteTexture(this.mojangLogo);
    this.mojangLogo = null;
    this.loadingScreen = new LoadingScreenRenderer(this);
    zypux.onPostStart();
    if (this.gameSettings.fullScreen && !this.fullscreen)
      toggleFullscreen(); 
    try {
      Display.setVSyncEnabled(this.gameSettings.enableVsync);
    } catch (OpenGLException ex) {
      this.gameSettings.enableVsync = false;
      this.gameSettings.saveOptions();
    } 
    this.renderGlobal.makeEntityOutlineShader();
  }
  
  private void registerMetadataSerializers() {
    this.metadataSerializer_.registerMetadataSectionType((IMetadataSectionSerializer)new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
    this.metadataSerializer_.registerMetadataSectionType((IMetadataSectionSerializer)new FontMetadataSectionSerializer(), FontMetadataSection.class);
    this.metadataSerializer_.registerMetadataSectionType((IMetadataSectionSerializer)new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
    this.metadataSerializer_.registerMetadataSectionType((IMetadataSectionSerializer)new PackMetadataSectionSerializer(), PackMetadataSection.class);
    this.metadataSerializer_.registerMetadataSectionType((IMetadataSectionSerializer)new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
  }
  
  private void initStream() {
    try {
      this.stream = (IStream)new TwitchStream(this, (Property)Iterables.getFirst(this.twitchDetails.get("twitch_access_token"), null));
    } catch (Throwable throwable) {
      this.stream = (IStream)new NullStream(throwable);
      logger.error("Couldn't initialize twitch stream");
    } 
  }
  
  private void createDisplay() throws LWJGLException {
    Display.setResizable(true);
    Display.setTitle(Zypux.title);
    try {
      Display.create((new PixelFormat()).withDepthBits(24));
    } catch (LWJGLException lwjglexception) {
      logger.error("Couldn't set pixel format", (Throwable)lwjglexception);
      try {
        Thread.sleep(1000L);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      } 
      if (this.fullscreen)
        updateDisplayMode(); 
      Display.create();
    } 
  }
  
  private void setInitialDisplayMode() throws LWJGLException {
    if (this.fullscreen) {
      Display.setFullscreen(true);
      DisplayMode displaymode = Display.getDisplayMode();
      this.displayWidth = Math.max(1, displaymode.getWidth());
      this.displayHeight = Math.max(1, displaymode.getHeight());
    } else {
      Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
    } 
  }
  
  private void setWindowIcon() {
    Util.EnumOS util$enumos = Util.getOSType();
    if (util$enumos != Util.EnumOS.OSX)
      try {
        BufferedImage image = ImageIO.read(Objects.<InputStream>requireNonNull(getClass().getResourceAsStream("/assets/minecraft/zypux/textures/logo.png")));
        if (image.getWidth() != 32 || image.getHeight() != 32)
          image = PictureUtils.resizeImage(image, 32, 32); 
        Display.setIcon(new ByteBuffer[] { PictureUtils.readImageToBuffer(PictureUtils.resizeImage(image, 16, 16)), PictureUtils.readImageToBuffer(image) });
      } catch (IOException ex) {
        ex.printStackTrace();
      }  
  }
  
  private static boolean isJvm64bit() {
    String[] astring = { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };
    for (String s : astring) {
      String s1 = System.getProperty(s);
      if (s1 != null && s1.contains("64"))
        return true; 
    } 
    return false;
  }
  
  public Framebuffer getFramebuffer() {
    return this.framebufferMc;
  }
  
  public String getVersion() {
    return this.launchedVersion;
  }
  
  private void startTimerHackThread() {
    Thread thread = new Thread("Timer hack thread") {
        public void run() {
          try {
            while (Minecraft.running)
              Thread.sleep(2147483647L); 
          } catch (Throwable $ex) {
            throw $ex;
          } 
        }
      };
    thread.setDaemon(true);
    thread.start();
  }
  
  public void crashed(CrashReport crash) {
    this.hasCrashed = true;
    this.crashReporter = crash;
  }
  
  public void displayCrashReport(CrashReport crashReportIn) {
    File file1 = new File((getMinecraft()).mcDataDir, "crash-reports");
    File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
    Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());
    if (crashReportIn.getFile() != null) {
      Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
      System.exit(-1);
    } else if (crashReportIn.saveToFile(file2)) {
      Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
      System.exit(-1);
    } else {
      Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
      System.exit(-2);
    } 
  }
  
  public boolean isUnicode() {
    return (this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont);
  }
  
  public void refreshResources() {
    List<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);
    for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries())
      list.add(resourcepackrepository$entry.getResourcePack()); 
    if (this.mcResourcePackRepository.getResourcePackInstance() != null)
      list.add(this.mcResourcePackRepository.getResourcePackInstance()); 
    try {
      this.mcResourceManager.reloadResources(list);
    } catch (RuntimeException runtimeexception) {
      logger.info("Caught error stitching, removing all assigned resourcepacks", runtimeexception);
      list.clear();
      list.addAll(this.defaultResourcePacks);
      this.mcResourcePackRepository.setRepositories(Collections.emptyList());
      this.mcResourceManager.reloadResources(list);
      this.gameSettings.resourcePacks.clear();
      this.gameSettings.incompatibleResourcePacks.clear();
      this.gameSettings.saveOptions();
    } 
    this.mcLanguageManager.parseLanguageMetadata(list);
    if (this.renderGlobal != null)
      this.renderGlobal.loadRenderers(); 
  }
  
  private void updateDisplayMode() throws LWJGLException {
    Set<DisplayMode> set = Sets.newHashSet();
    Collections.addAll(set, Display.getAvailableDisplayModes());
    DisplayMode displaymode = Display.getDesktopDisplayMode();
    if (!set.contains(displaymode) && Util.getOSType() == Util.EnumOS.OSX)
      for (DisplayMode displaymode1 : macDisplayModes) {
        boolean flag = true;
        for (DisplayMode displaymode2 : set) {
          if (displaymode2.getBitsPerPixel() == 32 && displaymode2.getWidth() == displaymode1.getWidth() && displaymode2.getHeight() == displaymode1.getHeight()) {
            flag = false;
            break;
          } 
        } 
        if (!flag) {
          Iterator<DisplayMode> iterator = set.iterator();
          while (iterator.hasNext()) {
            DisplayMode displaymode3 = iterator.next();
            if (displaymode3.getBitsPerPixel() == 32 && displaymode3.getWidth() == displaymode1.getWidth() / 2 && displaymode3.getHeight() == displaymode1.getHeight() / 2)
              displaymode = displaymode3; 
          } 
        } 
      }  
    Display.setDisplayMode(displaymode);
    this.displayWidth = displaymode.getWidth();
    this.displayHeight = displaymode.getHeight();
  }
  
  public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha) {
    float f = 0.00390625F;
    float f1 = 0.00390625F;
    WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
    worldrenderer.pos(posX, (posY + height), 0.0D).tex((texU * f), ((texV + height) * f1)).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos((posX + width), (posY + height), 0.0D).tex(((texU + width) * f), ((texV + height) * f1)).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos((posX + width), posY, 0.0D).tex(((texU + width) * f), (texV * f1)).color(red, green, blue, alpha).endVertex();
    worldrenderer.pos(posX, posY, 0.0D).tex((texU * f), (texV * f1)).color(red, green, blue, alpha).endVertex();
    Tessellator.getInstance().draw();
  }
  
  public ISaveFormat getSaveLoader() {
    return this.saveLoader;
  }
  
  public void displayGuiScreen(GuiScreen guiScreenIn) {
    GuiMainMenu guiMainMenu;
    GuiGameOver guiGameOver;
    if (this.currentScreen != null)
      this.currentScreen.onGuiClosed(); 
    if (guiScreenIn == null && this.theWorld == null) {
      guiMainMenu = new GuiMainMenu();
    } else if (guiMainMenu == null && this.thePlayer.getHealth() <= 0.0F) {
      guiGameOver = new GuiGameOver();
    } 
    if (guiGameOver instanceof GuiMainMenu) {
      this.gameSettings.showDebugInfo = false;
      this.ingameGUI.getChatGUI().clearChatMessages();
    } 
    this.currentScreen = (GuiScreen)guiGameOver;
    if (guiGameOver != null) {
      setIngameNotInFocus();
      ScaledResolution scaledresolution = new ScaledResolution(this);
      int i = scaledresolution.getScaledWidth();
      int j = scaledresolution.getScaledHeight();
      guiGameOver.setWorldAndResolution(this, i, j);
      this.skipRenderWorld = false;
    } else {
      this.mcSoundHandler.resumeSounds();
      setIngameFocus();
    } 
  }
  
  private void checkGLError(String message) {
    int i = GL11.glGetError();
    if (i != 0) {
      String s = GLU.gluErrorString(i);
      logger.error("########## GL ERROR ##########");
      logger.error("@ " + message);
      logger.error(i + ": " + s);
    } 
  }
  
  public void shutdownMinecraftApplet() {
    Zypux zypux = new Zypux();
    zypux.onShutdown();
    try {
      this.stream.shutdownStream();
      logger.info("Stopping!");
      try {
        loadWorld(null);
      } catch (Throwable ex) {
        ex.printStackTrace();
      } 
      this.mcSoundHandler.unloadSounds();
    } finally {
      Display.destroy();
      if (!this.hasCrashed)
        System.exit(0); 
    } 
    System.gc();
  }
  
  private void runGameLoop() throws IOException {
    long i = System.nanoTime();
    this.mcProfiler.startSection("root");
    if (Display.isCreated() && Display.isCloseRequested())
      shutdown(); 
    if (this.isGamePaused && this.theWorld != null) {
      float f = this.timer.renderPartialTicks;
      this.timer.updateTimer();
      this.timer.renderPartialTicks = f;
    } else {
      this.timer.updateTimer();
    } 
    this.mcProfiler.startSection("scheduledExecutables");
    synchronized (this.scheduledTasks) {
      while (!this.scheduledTasks.isEmpty())
        Util.runTask(this.scheduledTasks.poll(), logger); 
    } 
    this.mcProfiler.endSection();
    this.mcProfiler.startSection("tick");
    for (int j = 0; j < this.timer.elapsedTicks; j++)
      runTick(); 
    this.mcProfiler.endStartSection("preRenderErrors");
    checkGLError("Pre render");
    this.mcProfiler.endStartSection("sound");
    this.mcSoundHandler.setListener((EntityPlayer)this.thePlayer, this.timer.renderPartialTicks);
    this.mcProfiler.endSection();
    this.mcProfiler.startSection("render");
    GlStateManager.pushMatrix();
    GlStateManager.clear(16640);
    this.framebufferMc.bindFramebuffer(true);
    this.mcProfiler.startSection("display");
    GlStateManager.enableTexture2D();
    if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock())
      this.gameSettings.thirdPersonView = 0; 
    this.mcProfiler.endSection();
    if (!this.skipRenderWorld) {
      this.mcProfiler.endStartSection("gameRenderer");
      this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks, i);
      this.mcProfiler.endSection();
    } 
    this.mcProfiler.endSection();
    if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
      if (!this.mcProfiler.profilingEnabled)
        this.mcProfiler.clearProfiling(); 
      this.mcProfiler.profilingEnabled = true;
      displayDebugInfo();
    } else {
      this.mcProfiler.profilingEnabled = false;
      this.prevFrameTime = System.nanoTime();
    } 
    this.guiAchievement.updateAchievementWindow();
    this.framebufferMc.unbindFramebuffer();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    this.entityRenderer.renderStreamIndicator();
    GlStateManager.popMatrix();
    this.mcProfiler.startSection("root");
    updateDisplay();
    Thread.yield();
    this.mcProfiler.startSection("stream");
    this.mcProfiler.startSection("update");
    this.stream.func_152935_j();
    this.mcProfiler.endStartSection("submit");
    this.stream.func_152922_k();
    this.mcProfiler.endSection();
    this.mcProfiler.endSection();
    checkGLError("Post render");
    this.fpsCounter++;
    this.isGamePaused = (isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic());
    long k = System.nanoTime();
    this.frameTimer.addFrame(k - this.startNanoTime);
    this.startNanoTime = k;
    while (getSystemTime() >= this.debugUpdateTime + 1000L) {
      debugFPS = this.fpsCounter;
      this.debug = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", new Object[] { Integer.valueOf(debugFPS), Integer.valueOf(RenderChunk.renderChunksUpdated), (RenderChunk.renderChunksUpdated != 1) ? "s" : "", (this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax()) ? "inf" : Integer.valueOf(this.gameSettings.limitFramerate), this.gameSettings.enableVsync ? " vsync" : "", this.gameSettings.fancyGraphics ? "" : " fast", (this.gameSettings.clouds == 0) ? "" : ((this.gameSettings.clouds == 1) ? " fast-clouds" : " fancy-clouds"), OpenGlHelper.useVbo() ? " vbo" : "" });
      RenderChunk.renderChunksUpdated = 0;
      this.debugUpdateTime += 1000L;
      this.fpsCounter = 0;
      this.usageSnooper.addMemoryStatsToSnooper();
      if (!this.usageSnooper.isSnooperRunning())
        this.usageSnooper.startSnooper(); 
    } 
    if (isFramerateLimitBelowMax()) {
      this.mcProfiler.startSection("fpslimit_wait");
      Display.sync(getLimitFramerate());
      this.mcProfiler.endSection();
    } 
    this.mcProfiler.endSection();
  }
  
  public void updateDisplay() {
    this.mcProfiler.startSection("display_update");
    Display.update();
    this.mcProfiler.endSection();
    checkWindowResize();
  }
  
  protected void checkWindowResize() {
    if (!this.fullscreen && Display.wasResized()) {
      int i = this.displayWidth;
      int j = this.displayHeight;
      this.displayWidth = Display.getWidth();
      this.displayHeight = Display.getHeight();
      if (this.displayWidth != i || this.displayHeight != j) {
        if (this.displayWidth <= 0)
          this.displayWidth = 1; 
        if (this.displayHeight <= 0)
          this.displayHeight = 1; 
        resize(this.displayWidth, this.displayHeight);
      } 
    } 
  }
  
  public int getLimitFramerate() {
    int unfocusedFps = (int)(Zypux.getInstance()).settingsManager.getSettingByName("Unfocused Fps").getDoubleValue();
    if (Performance.isEnabled() && !this.inGameHasFocus)
      return unfocusedFps; 
    int smoothFps = (int)(Zypux.getInstance()).settingsManager.getSettingByName("Smooth Fps").getDoubleValue();
    int i = (this.theWorld == null && this.currentScreen != null) ? unfocusedFps : this.gameSettings.limitFramerate;
    for (Module module : Category.RENDER.getModules()) {
      if (module.getName().equalsIgnoreCase("performance")) {
        if (smoothFps >= 300)
          return i; 
        if (Performance.isEnabled())
          return smoothFps; 
      } 
    } 
    return i;
  }
  
  public boolean isFramerateLimitBelowMax() {
    return (getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax());
  }
  
  public void freeMemory() {
    try {
      memoryReserve = new byte[0];
      this.renderGlobal.deleteAllDisplayLists();
    } catch (Throwable throwable) {}
    try {
      System.gc();
      loadWorld(null);
    } catch (Throwable throwable) {}
    System.gc();
  }
  
  private void updateDebugProfilerName(int keyCount) {
    List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
    if (list != null && !list.isEmpty()) {
      Profiler.Result profiler$result = list.remove(0);
      if (keyCount == 0) {
        if (profiler$result.field_76331_c.length() > 0) {
          int i = this.debugProfilerName.lastIndexOf(".");
          if (i >= 0)
            this.debugProfilerName = this.debugProfilerName.substring(0, i); 
        } 
      } else {
        keyCount--;
        if (keyCount < list.size() && !((Profiler.Result)list.get(keyCount)).field_76331_c.equals("unspecified")) {
          if (this.debugProfilerName.length() > 0)
            this.debugProfilerName += "."; 
          this.debugProfilerName += ((Profiler.Result)list.get(keyCount)).field_76331_c;
        } 
      } 
    } 
  }
  
  private void displayDebugInfo() {
    if (this.mcProfiler.profilingEnabled) {
      List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
      Profiler.Result profiler$result = list.remove(0);
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.enableColorMaterial();
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0D, this.displayWidth, this.displayHeight, 0.0D, 1000.0D, 3000.0D);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
      GL11.glLineWidth(1.0F);
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      int i = 160;
      int j = this.displayWidth - i - 10;
      int k = this.displayHeight - i * 2;
      GlStateManager.enableBlend();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldrenderer.pos((j - i * 1.1F), (k - i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
      worldrenderer.pos((j - i * 1.1F), (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
      worldrenderer.pos((j + i * 1.1F), (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
      worldrenderer.pos((j + i * 1.1F), (k - i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
      tessellator.draw();
      GlStateManager.disableBlend();
      double d0 = 0.0D;
      for (Profiler.Result profiler$result1 : list) {
        int i1 = MathHelper.floor_double(profiler$result1.field_76332_a / 4.0D) + 1;
        worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
        int j1 = profiler$result1.getColor();
        int k1 = j1 >> 16 & 0xFF;
        int l1 = j1 >> 8 & 0xFF;
        int i2 = j1 & 0xFF;
        worldrenderer.pos(j, k, 0.0D).color(k1, l1, i2, 255).endVertex();
        for (int j2 = i1; j2 >= 0; j2--) {
          float f = (float)((d0 + profiler$result1.field_76332_a * j2 / i1) * Math.PI * 2.0D / 100.0D);
          float f1 = MathHelper.sin(f) * i;
          float f2 = MathHelper.cos(f) * i * 0.5F;
          worldrenderer.pos((j + f1), (k - f2), 0.0D).color(k1, l1, i2, 255).endVertex();
        } 
        tessellator.draw();
        worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);
        for (int i3 = i1; i3 >= 0; i3--) {
          float f3 = (float)((d0 + profiler$result1.field_76332_a * i3 / i1) * Math.PI * 2.0D / 100.0D);
          float f4 = MathHelper.sin(f3) * i;
          float f5 = MathHelper.cos(f3) * i * 0.5F;
          worldrenderer.pos((j + f4), (k - f5), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
          worldrenderer.pos((j + f4), (k - f5 + 10.0F), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
        } 
        tessellator.draw();
        d0 += profiler$result1.field_76332_a;
      } 
      DecimalFormat decimalformat = new DecimalFormat("##0.00");
      GlStateManager.enableTexture2D();
      String s = "";
      if (!profiler$result.field_76331_c.equals("unspecified"))
        s = s + "[0] "; 
      if (profiler$result.field_76331_c.length() == 0) {
        s = s + "ROOT ";
      } else {
        s = s + profiler$result.field_76331_c + " ";
      } 
      int l2 = 16777215;
      this.fontRendererObj.drawStringWithShadow(s, (j - i), (k - i / 2 - 16), l2);
      this.fontRendererObj.drawStringWithShadow(s = decimalformat.format(profiler$result.field_76330_b) + "%", (j + i - this.fontRendererObj.getStringWidth(s)), (k - i / 2 - 16), l2);
      for (int k2 = 0; k2 < list.size(); k2++) {
        Profiler.Result profiler$result2 = list.get(k2);
        String s1 = "";
        if (profiler$result2.field_76331_c.equals("unspecified")) {
          s1 = s1 + "[?] ";
        } else {
          s1 = s1 + "[" + (k2 + 1) + "] ";
        } 
        s1 = s1 + profiler$result2.field_76331_c;
        this.fontRendererObj.drawStringWithShadow(s1, (j - i), (k + i / 2 + k2 * 8 + 20), profiler$result2.getColor());
        this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.field_76332_a) + "%", (j + i - 50 - this.fontRendererObj.getStringWidth(s1)), (k + i / 2 + k2 * 8 + 20), profiler$result2.getColor());
        this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.field_76330_b) + "%", (j + i - this.fontRendererObj.getStringWidth(s1)), (k + i / 2 + k2 * 8 + 20), profiler$result2.getColor());
      } 
    } 
  }
  
  public void shutdown() {
    running = false;
  }
  
  public void setIngameFocus() {
    if (Display.isActive() && 
      !this.inGameHasFocus) {
      this.inGameHasFocus = true;
      this.mouseHelper.grabMouseCursor();
      displayGuiScreen(null);
      this.leftClickCounter = 10000;
    } 
  }
  
  public void setIngameNotInFocus() {
    if (this.inGameHasFocus) {
      KeyBinding.unPressAllKeys();
      this.inGameHasFocus = false;
      this.mouseHelper.ungrabMouseCursor();
    } 
  }
  
  public void displayInGameMenu() {
    if (this.currentScreen == null) {
      displayGuiScreen((GuiScreen)new GuiIngameMenu());
      if (isSingleplayer() && !this.theIntegratedServer.getPublic())
        this.mcSoundHandler.pauseSounds(); 
    } 
  }
  
  private void sendClickBlockToController(boolean leftClick) {
    if (!leftClick)
      this.leftClickCounter = 0; 
    if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem())
      if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
        BlockPos blockpos = this.objectMouseOver.getBlockPos();
        if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
          this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
          this.thePlayer.swingItem();
        } 
      } else {
        this.playerController.resetBlockRemoving();
      }  
  }
  
  private void clickMouse() {
    if (this.leftClickCounter <= 0) {
      this.thePlayer.swingItem();
      if (this.objectMouseOver == null) {
        logger.error("Null returned as 'hitResult', this shouldn't happen!");
        if (this.playerController.isNotCreative())
          this.leftClickCounter = 10; 
      } else {
        BlockPos blockpos;
        switch (this.objectMouseOver.typeOfHit) {
          case FURNACE:
            this.playerController.attackEntity((EntityPlayer)this.thePlayer, this.objectMouseOver.entityHit);
            return;
          case CHEST:
            blockpos = this.objectMouseOver.getBlockPos();
            if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
              this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
              return;
            } 
            break;
        } 
        if (this.playerController.isNotCreative())
          this.leftClickCounter = 10; 
      } 
    } 
  }
  
  private void rightClickMouse() {
    if (!this.playerController.getIsHittingBlock()) {
      this.rightClickDelayTimer = 4;
      boolean flag = true;
      ItemStack itemstack = this.thePlayer.inventory.getCurrentItem();
      if (this.objectMouseOver == null) {
        logger.warn("Null returned as 'hitResult', this shouldn't happen!");
      } else {
        BlockPos blockpos;
        switch (this.objectMouseOver.typeOfHit) {
          case FURNACE:
            if (this.playerController.isPlayerRightClickingOnEntity((EntityPlayer)this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver)) {
              flag = false;
              break;
            } 
            if (this.playerController.interactWithEntitySendPacket((EntityPlayer)this.thePlayer, this.objectMouseOver.entityHit))
              flag = false; 
            break;
          case CHEST:
            blockpos = this.objectMouseOver.getBlockPos();
            if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
              int i = (itemstack != null) ? itemstack.stackSize : 0;
              if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, itemstack, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                flag = false;
                this.thePlayer.swingItem();
              } 
              if (itemstack == null)
                return; 
              if (itemstack.stackSize == 0) {
                this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                break;
              } 
              if (itemstack.stackSize != i || this.playerController.isInCreativeMode())
                this.entityRenderer.itemRenderer.resetEquippedProgress(); 
            } 
            break;
        } 
      } 
      if (flag) {
        ItemStack itemstack1 = this.thePlayer.inventory.getCurrentItem();
        if (itemstack1 != null && this.playerController.sendUseItem((EntityPlayer)this.thePlayer, (World)this.theWorld, itemstack1))
          this.entityRenderer.itemRenderer.resetEquippedProgress2(); 
      } 
    } 
  }
  
  public void toggleFullscreen() {
    try {
      this.fullscreen = !this.fullscreen;
      this.gameSettings.fullScreen = this.fullscreen;
      if (this.fullscreen) {
        updateDisplayMode();
        this.displayWidth = Display.getDisplayMode().getWidth();
        this.displayHeight = Display.getDisplayMode().getHeight();
      } else {
        Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
        this.displayWidth = this.tempDisplayWidth;
        this.displayHeight = this.tempDisplayHeight;
      } 
      if (this.displayWidth <= 0)
        this.displayWidth = 1; 
      if (this.displayHeight <= 0)
        this.displayHeight = 1; 
      if (this.currentScreen != null) {
        resize(this.displayWidth, this.displayHeight);
      } else {
        updateFramebufferSize();
      } 
      Display.setFullscreen(this.fullscreen);
      Display.setVSyncEnabled(this.gameSettings.enableVsync);
      updateDisplay();
    } catch (Exception exception) {
      logger.error("Couldn't toggle fullscreen", exception);
    } 
  }
  
  private void resize(int width, int height) {
    this.displayWidth = Math.max(1, width);
    this.displayHeight = Math.max(1, height);
    if (this.currentScreen != null) {
      ScaledResolution scaledresolution = new ScaledResolution(this);
      this.currentScreen.onResize(this, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
    } 
    this.loadingScreen = new LoadingScreenRenderer(this);
    updateFramebufferSize();
  }
  
  private void updateFramebufferSize() {
    this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);
    if (this.entityRenderer != null)
      this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight); 
  }
  
  public MusicTicker getMusicTicker() {
    return this.mcMusicTicker;
  }
  
  public void runTick() throws IOException {
    if (this.rightClickDelayTimer > 0)
      this.rightClickDelayTimer--; 
    this.mcProfiler.startSection("gui");
    if (!this.isGamePaused)
      this.ingameGUI.updateTick(); 
    this.mcProfiler.endSection();
    this.entityRenderer.getMouseOver(1.0F);
    this.mcProfiler.startSection("gameMode");
    if (!this.isGamePaused && this.theWorld != null)
      this.playerController.updateController(); 
    this.mcProfiler.endStartSection("textures");
    if (!this.isGamePaused)
      this.renderEngine.tick(); 
    if (this.currentScreen == null && this.thePlayer != null) {
      if (this.thePlayer.getHealth() <= 0.0F) {
        displayGuiScreen(null);
      } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
        displayGuiScreen((GuiScreen)new GuiSleepMP());
      } 
    } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
      displayGuiScreen(null);
    } 
    if (this.currentScreen != null) {
      this.leftClickCounter = 10000;
      try {
        this.currentScreen.handleInput();
      } catch (Throwable throwable1) {
        CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
        CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
        crashreportcategory.addCrashSectionCallable("Screen name", () -> this.currentScreen.getClass().getCanonicalName());
        throw new ReportedException(crashreport);
      } 
      if (this.currentScreen != null)
        try {
          this.currentScreen.updateScreen();
        } catch (Throwable throwable) {
          CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
          CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
          crashreportcategory1.addCrashSectionCallable("Screen name", () -> this.currentScreen.getClass().getCanonicalName());
          throw new ReportedException(crashreport1);
        }  
    } 
    if (this.currentScreen == null || this.currentScreen.allowUserInput) {
      this.mcProfiler.endStartSection("mouse");
      while (Mouse.next()) {
        int i = Mouse.getEventButton();
        KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());
        if (Mouse.getEventButtonState())
          if (this.thePlayer.isSpectator() && i == 2) {
            this.ingameGUI.getSpectatorGui().func_175261_b();
          } else {
            KeyBinding.onTick(i - 100);
          }  
        long i1 = getSystemTime() - this.systemTime;
        if (i1 <= 200L) {
          int j = Mouse.getEventDWheel();
          if (j != 0)
            if (this.thePlayer.isSpectator()) {
              j = (j < 0) ? -1 : 1;
              if (this.ingameGUI.getSpectatorGui().func_175262_a()) {
                this.ingameGUI.getSpectatorGui().func_175259_b(-j);
              } else {
                float f = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + j * 0.005F, 0.0F, 0.2F);
                this.thePlayer.capabilities.setFlySpeed(f);
              } 
            } else {
              this.thePlayer.inventory.changeCurrentItem(j);
            }  
          if (this.currentScreen == null) {
            if (!this.inGameHasFocus && Mouse.getEventButtonState())
              setIngameFocus(); 
            continue;
          } 
          this.currentScreen.handleMouseInput();
        } 
      } 
      if (this.leftClickCounter > 0)
        this.leftClickCounter--; 
      this.mcProfiler.endStartSection("keyboard");
      while (Keyboard.next()) {
        int k = (Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + 256) : Keyboard.getEventKey();
        KeyBinding.setKeyBindState(k, Keyboard.getEventKeyState());
        if (Keyboard.getEventKeyState())
          KeyBinding.onTick(k); 
        if (this.debugCrashKeyPressTime > 0L) {
          if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L)
            throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable())); 
          if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61))
            this.debugCrashKeyPressTime = -1L; 
        } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
          this.debugCrashKeyPressTime = getSystemTime();
        } 
        dispatchKeypresses();
        if (Keyboard.getEventKeyState()) {
          if (k == 62 && this.entityRenderer != null)
            this.entityRenderer.switchUseShader(); 
          if (this.currentScreen != null) {
            this.currentScreen.handleKeyboardInput();
          } else {
            PressedKeyEvent eventKey = new PressedKeyEvent(k);
            eventKey.call();
            if (k == 1)
              displayInGameMenu(); 
            if (k == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null)
              this.ingameGUI.getChatGUI().clearChatMessages(); 
            if (k == 31 && Keyboard.isKeyDown(61))
              refreshResources(); 
            if (k == 17)
              Keyboard.isKeyDown(61); 
            if (k == 18)
              Keyboard.isKeyDown(61); 
            if (k == 47)
              Keyboard.isKeyDown(61); 
            if (k == 38)
              Keyboard.isKeyDown(61); 
            if (k == 22)
              Keyboard.isKeyDown(61); 
            if (k == 20 && Keyboard.isKeyDown(61))
              refreshResources(); 
            if (k == 33 && Keyboard.isKeyDown(61))
              this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1); 
            if (k == 30 && Keyboard.isKeyDown(61))
              this.renderGlobal.loadRenderers(); 
            if (k == 35 && Keyboard.isKeyDown(61)) {
              this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
              this.gameSettings.saveOptions();
            } 
            if (k == 48 && Keyboard.isKeyDown(61))
              this.renderManager.setDebugBoundingBox(!this.renderManager.isDebugBoundingBox()); 
            if (k == 25 && Keyboard.isKeyDown(61)) {
              this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
              this.gameSettings.saveOptions();
            } 
            if (k == 59)
              this.gameSettings.hideGUI = !this.gameSettings.hideGUI; 
            if (k == 61) {
              this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
              this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
              this.gameSettings.showLagometerR = GuiScreen.isAltKeyDown();
            } 
            if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
              this.gameSettings.thirdPersonView++;
              if (this.gameSettings.thirdPersonView > 2)
                this.gameSettings.thirdPersonView = 0; 
              if (this.gameSettings.thirdPersonView == 0) {
                this.entityRenderer.loadEntityShader(getRenderViewEntity());
              } else if (this.gameSettings.thirdPersonView == 1) {
                this.entityRenderer.loadEntityShader(null);
              } 
              this.renderGlobal.setDisplayListEntitiesDirty();
            } 
            if (this.gameSettings.keyBindSmoothCamera.isPressed())
              this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera; 
          } 
          if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
            if (k == 11)
              updateDebugProfilerName(0); 
            for (int j1 = 0; j1 < 9; j1++) {
              if (k == 2 + j1)
                updateDebugProfilerName(j1 + 1); 
            } 
          } 
        } 
      } 
      for (int l = 0; l < 9; l++) {
        if (this.gameSettings.keyBindsHotbar[l].isPressed())
          if (this.thePlayer.isSpectator()) {
            this.ingameGUI.getSpectatorGui().func_175260_a(l);
          } else {
            this.thePlayer.inventory.currentItem = l;
          }  
      } 
      boolean flag = (this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN);
      while (this.gameSettings.keyBindInventory.isPressed()) {
        if (this.playerController.isRidingHorse()) {
          this.thePlayer.sendHorseInventory();
          continue;
        } 
        getNetHandler().addToSendQueue((Packet)new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        displayGuiScreen((GuiScreen)new GuiInventory((EntityPlayer)this.thePlayer));
      } 
      while (this.gameSettings.keyBindDrop.isPressed()) {
        if (!this.thePlayer.isSpectator())
          this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown()); 
      } 
      while (this.gameSettings.keyBindChat.isPressed() && flag)
        displayGuiScreen((GuiScreen)new GuiChat()); 
      if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag)
        displayGuiScreen((GuiScreen)new GuiChat("/")); 
      if (this.thePlayer.isUsingItem()) {
        if (!this.gameSettings.keyBindUseItem.isKeyDown())
          this.playerController.onStoppedUsingItem((EntityPlayer)this.thePlayer); 
      } else {
        while (this.gameSettings.keyBindAttack.isPressed())
          clickMouse(); 
        while (this.gameSettings.keyBindUseItem.isPressed())
          rightClickMouse(); 
        while (this.gameSettings.keyBindPickBlock.isPressed())
          middleClickMouse(); 
      } 
      if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem())
        rightClickMouse(); 
      sendClickBlockToController((this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus));
    } 
    if (this.theWorld != null) {
      if (this.thePlayer != null) {
        this.joinPlayerCounter++;
        if (this.joinPlayerCounter == 30) {
          this.joinPlayerCounter = 0;
          this.theWorld.joinEntityInSurroundings((Entity)this.thePlayer);
        } 
      } 
      this.mcProfiler.endStartSection("gameRenderer");
      if (!this.isGamePaused)
        this.entityRenderer.updateRenderer(); 
      this.mcProfiler.endStartSection("levelRenderer");
      if (!this.isGamePaused)
        this.renderGlobal.updateClouds(); 
      this.mcProfiler.endStartSection("level");
      if (!this.isGamePaused) {
        if (this.theWorld.getLastLightningBolt() > 0)
          this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1); 
        this.theWorld.updateEntities();
      } 
    } else if (this.entityRenderer.isShaderActive()) {
      this.entityRenderer.stopUseShader();
    } 
    if (!this.isGamePaused) {
      this.mcMusicTicker.update();
      this.mcSoundHandler.update();
    } 
    if (this.theWorld != null) {
      if (!this.isGamePaused) {
        this.theWorld.setAllowedSpawnTypes((this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL), true);
        try {
          this.theWorld.tick();
        } catch (Throwable throwable2) {
          CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");
          if (this.theWorld == null) {
            CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
            crashreportcategory2.addCrashSection("Problem", "Level is null!");
          } else {
            this.theWorld.addWorldInfoToCrashReport(crashreport2);
          } 
          throw new ReportedException(crashreport2);
        } 
      } 
      this.mcProfiler.endStartSection("animateTick");
      if (!this.isGamePaused && this.theWorld != null)
        this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ)); 
      this.mcProfiler.endStartSection("particles");
      if (!this.isGamePaused)
        this.effectRenderer.updateEffects(); 
    } else if (this.myNetworkManager != null) {
      this.mcProfiler.endStartSection("pendingConnection");
      this.myNetworkManager.processReceivedPackets();
    } 
    this.mcProfiler.endSection();
    this.systemTime = getSystemTime();
  }
  
  public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
    loadWorld(null);
    System.gc();
    ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
    WorldInfo worldinfo = isavehandler.loadWorldInfo();
    if (worldinfo == null && worldSettingsIn != null) {
      worldinfo = new WorldInfo(worldSettingsIn, folderName);
      isavehandler.saveWorldInfo(worldinfo);
    } 
    if (worldSettingsIn == null)
      worldSettingsIn = new WorldSettings(Objects.<WorldInfo>requireNonNull(worldinfo)); 
    try {
      this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
      this.theIntegratedServer.startServerThread();
      this.integratedServerIsRunning = true;
    } catch (Throwable throwable) {
      CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
      CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
      crashreportcategory.addCrashSection("Level ID", folderName);
      crashreportcategory.addCrashSection("Level Name", worldName);
      throw new ReportedException(crashreport);
    } 
    this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel", new Object[0]));
    while (!this.theIntegratedServer.serverIsInRunLoop()) {
      String s = this.theIntegratedServer.getUserMessage();
      if (s != null) {
        this.loadingScreen.displayLoadingString(I18n.format(s, new Object[0]));
      } else {
        this.loadingScreen.displayLoadingString("");
      } 
      try {
        Thread.sleep(200L);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      } 
    } 
    displayGuiScreen(null);
    SocketAddress socketaddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
    NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
    networkmanager.setNetHandler((INetHandler)new NetHandlerLoginClient(networkmanager, this, null));
    networkmanager.sendPacket((Packet)new C00Handshake(47, socketaddress.toString(), 0, EnumConnectionState.LOGIN));
    networkmanager.sendPacket((Packet)new C00PacketLoginStart(getSession().getProfile()));
    this.myNetworkManager = networkmanager;
  }
  
  public void loadWorld(WorldClient worldClientIn) {
    loadWorld(worldClientIn, "");
  }
  
  public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
    if (worldClientIn == null) {
      NetHandlerPlayClient nethandlerplayclient = getNetHandler();
      if (nethandlerplayclient != null)
        nethandlerplayclient.cleanup(); 
      if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
        this.theIntegratedServer.initiateShutdown();
        this.theIntegratedServer.setStaticInstance();
      } 
      this.theIntegratedServer = null;
      this.guiAchievement.clearAchievements();
      this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
    } 
    this.renderViewEntity = null;
    this.myNetworkManager = null;
    if (this.loadingScreen != null) {
      this.loadingScreen.resetProgressAndMessage(loadingMessage);
      this.loadingScreen.displayLoadingString("");
    } 
    if (worldClientIn == null && this.theWorld != null) {
      this.mcResourcePackRepository.clearResourcePack();
      this.ingameGUI.resetPlayersOverlayFooterHeader();
      setServerData(null);
      this.integratedServerIsRunning = false;
    } 
    this.mcSoundHandler.stopSounds();
    this.theWorld = worldClientIn;
    if (worldClientIn != null) {
      if (this.renderGlobal != null)
        this.renderGlobal.setWorldAndLoadRenderers(worldClientIn); 
      if (this.effectRenderer != null)
        this.effectRenderer.clearEffects((World)worldClientIn); 
      if (this.thePlayer == null) {
        this.thePlayer = this.playerController.func_178892_a((World)worldClientIn, new StatFileWriter());
        this.playerController.flipPlayer((EntityPlayer)this.thePlayer);
      } 
      this.thePlayer.preparePlayerToSpawn();
      worldClientIn.spawnEntityInWorld((Entity)this.thePlayer);
      this.thePlayer.movementInput = (MovementInput)new MovementInputFromOptions(this.gameSettings);
      this.playerController.setPlayerCapabilities((EntityPlayer)this.thePlayer);
      this.renderViewEntity = (Entity)this.thePlayer;
    } else {
      this.saveLoader.flushCache();
      this.thePlayer = null;
    } 
    System.gc();
    this.systemTime = 0L;
  }
  
  public void setDimensionAndSpawnPlayer(int dimension) {
    this.theWorld.setInitialSpawnLocation();
    this.theWorld.removeAllEntities();
    int i = 0;
    String s = null;
    if (this.thePlayer != null) {
      i = this.thePlayer.getEntityId();
      this.theWorld.removeEntity((Entity)this.thePlayer);
      s = this.thePlayer.getClientBrand();
    } 
    this.renderViewEntity = null;
    EntityPlayerSP entityplayersp = this.thePlayer;
    this.thePlayer = this.playerController.func_178892_a((World)this.theWorld, (this.thePlayer == null) ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
    this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(((EntityPlayerSP)Objects.<EntityPlayerSP>requireNonNull(entityplayersp)).getDataWatcher().getAllWatched());
    this.thePlayer.dimension = dimension;
    this.renderViewEntity = (Entity)this.thePlayer;
    this.thePlayer.preparePlayerToSpawn();
    this.thePlayer.setClientBrand(s);
    this.theWorld.spawnEntityInWorld((Entity)this.thePlayer);
    this.playerController.flipPlayer((EntityPlayer)this.thePlayer);
    this.thePlayer.movementInput = (MovementInput)new MovementInputFromOptions(this.gameSettings);
    this.thePlayer.setEntityId(i);
    this.playerController.setPlayerCapabilities((EntityPlayer)this.thePlayer);
    this.thePlayer.setReducedDebug(entityplayersp.hasReducedDebug());
    if (this.currentScreen instanceof GuiGameOver)
      displayGuiScreen(null); 
  }
  
  public final boolean isDemo() {
    return this.isDemo;
  }
  
  public NetHandlerPlayClient getNetHandler() {
    return (this.thePlayer != null) ? this.thePlayer.sendQueue : null;
  }
  
  public static boolean isGuiEnabled() {
    return (theMinecraft == null || !theMinecraft.gameSettings.hideGUI);
  }
  
  public static boolean isAmbientOcclusionEnabled() {
    return (theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0);
  }
  
  private void middleClickMouse() {
    if (this.objectMouseOver != null) {
      Item item;
      boolean flag = this.thePlayer.capabilities.isCreativeMode;
      int i = 0;
      boolean flag1 = false;
      TileEntity tileentity = null;
      if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
        BlockPos blockpos = this.objectMouseOver.getBlockPos();
        Block block = this.theWorld.getBlockState(blockpos).getBlock();
        if (block.getMaterial() == Material.air)
          return; 
        item = block.getItem((World)this.theWorld, blockpos);
        if (item == null)
          return; 
        if (flag && GuiScreen.isCtrlKeyDown())
          tileentity = this.theWorld.getTileEntity(blockpos); 
        Block block1 = (item instanceof net.minecraft.item.ItemBlock && !block.isFlowerPot()) ? Block.getBlockFromItem(item) : block;
        i = block1.getDamageValue((World)this.theWorld, blockpos);
        flag1 = item.getHasSubtypes();
      } else {
        if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !flag)
          return; 
        if (this.objectMouseOver.entityHit instanceof net.minecraft.entity.item.EntityPainting) {
          item = Items.painting;
        } else if (this.objectMouseOver.entityHit instanceof net.minecraft.entity.EntityLeashKnot) {
          item = Items.lead;
        } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
          EntityItemFrame entityitemframe = (EntityItemFrame)this.objectMouseOver.entityHit;
          ItemStack itemstack = entityitemframe.getDisplayedItem();
          if (itemstack == null) {
            item = Items.item_frame;
          } else {
            item = itemstack.getItem();
            i = itemstack.getMetadata();
            flag1 = true;
          } 
        } else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
          EntityMinecart entityminecart = (EntityMinecart)this.objectMouseOver.entityHit;
          switch (entityminecart.getMinecartType()) {
            case FURNACE:
              item = Items.furnace_minecart;
              break;
            case CHEST:
              item = Items.chest_minecart;
              break;
            case TNT:
              item = Items.tnt_minecart;
              break;
            case HOPPER:
              item = Items.hopper_minecart;
              break;
            case COMMAND_BLOCK:
              item = Items.command_block_minecart;
              break;
            default:
              item = Items.minecart;
              break;
          } 
        } else if (this.objectMouseOver.entityHit instanceof net.minecraft.entity.item.EntityBoat) {
          item = Items.boat;
        } else if (this.objectMouseOver.entityHit instanceof net.minecraft.entity.item.EntityArmorStand) {
          ItemArmorStand itemArmorStand = Items.armor_stand;
        } else {
          item = Items.spawn_egg;
          i = EntityList.getEntityID(this.objectMouseOver.entityHit);
          flag1 = true;
          if (!EntityList.entityEggs.containsKey(Integer.valueOf(i)))
            return; 
        } 
      } 
      InventoryPlayer inventoryplayer = this.thePlayer.inventory;
      if (tileentity == null) {
        inventoryplayer.setCurrentItem(item, i, flag1, flag);
      } else {
        ItemStack itemstack1 = pickBlockWithNBT(item, i, tileentity);
        inventoryplayer.setInventorySlotContents(inventoryplayer.currentItem, itemstack1);
      } 
      if (flag) {
        int j = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventoryplayer.currentItem;
        this.playerController.sendSlotPacket(inventoryplayer.getStackInSlot(inventoryplayer.currentItem), j);
      } 
    } 
  }
  
  private ItemStack pickBlockWithNBT(Item itemIn, int meta, TileEntity tileEntityIn) {
    ItemStack itemstack = new ItemStack(itemIn, 1, meta);
    NBTTagCompound nbttagcompound = new NBTTagCompound();
    tileEntityIn.writeToNBT(nbttagcompound);
    if (itemIn == Items.skull && nbttagcompound.hasKey("Owner")) {
      NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
      NBTTagCompound nbttagcompound3 = new NBTTagCompound();
      nbttagcompound3.setTag("SkullOwner", (NBTBase)nbttagcompound2);
      itemstack.setTagCompound(nbttagcompound3);
    } else {
      itemstack.setTagInfo("BlockEntityTag", (NBTBase)nbttagcompound);
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      NBTTagList nbttaglist = new NBTTagList();
      nbttaglist.appendTag((NBTBase)new NBTTagString("(+NBT)"));
      nbttagcompound1.setTag("Lore", (NBTBase)nbttaglist);
      itemstack.setTagInfo("display", (NBTBase)nbttagcompound1);
    } 
    return itemstack;
  }
  
  public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
    theCrash.getCategory().addCrashSectionCallable("Launched Version", () -> this.launchedVersion);
    theCrash.getCategory().addCrashSectionCallable("LWJGL", Sys::getVersion);
    theCrash.getCategory().addCrashSectionCallable("OpenGL", () -> GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936));
    theCrash.getCategory().addCrashSectionCallable("GL Caps", OpenGlHelper::getLogText);
    theCrash.getCategory().addCrashSectionCallable("Using VBOs", () -> this.gameSettings.useVbo ? "Yes" : "No");
    theCrash.getCategory().addCrashSectionCallable("Is Modded", () -> {
          String s = ClientBrandRetriever.getClientModName();
          return !s.equals("vanilla") ? ("Definitely; Client brand changed to '" + s + "'") : ((Minecraft.class.getSigners() == null) ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.");
        });
    theCrash.getCategory().addCrashSectionCallable("Type", () -> "Client (map_client.txt)");
    theCrash.getCategory().addCrashSectionCallable("Resource Packs", () -> {
          StringBuilder stringbuilder = new StringBuilder();
          for (String s : this.gameSettings.resourcePacks) {
            if (stringbuilder.length() > 0)
              stringbuilder.append(", "); 
            stringbuilder.append(s);
            if (this.gameSettings.incompatibleResourcePacks.contains(s))
              stringbuilder.append(" (incompatible)"); 
          } 
          return stringbuilder.toString();
        });
    theCrash.getCategory().addCrashSectionCallable("Current Language", () -> this.mcLanguageManager.getCurrentLanguage().toString());
    theCrash.getCategory().addCrashSectionCallable("Profiler Position", () -> this.mcProfiler.profilingEnabled ? this.mcProfiler.getNameOfLastSection() : "N/A (disabled)");
    theCrash.getCategory().addCrashSectionCallable("CPU", OpenGlHelper::getCpu);
    if (this.theWorld != null)
      this.theWorld.addWorldInfoToCrashReport(theCrash); 
    return theCrash;
  }
  
  public static Minecraft getMinecraft() {
    return theMinecraft;
  }
  
  public ListenableFuture<Object> scheduleResourcesRefresh() {
    return addScheduledTask(this::refreshResources);
  }
  
  public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
    playerSnooper.addClientStat("fps", Integer.valueOf(debugFPS));
    playerSnooper.addClientStat("vsync_enabled", Boolean.valueOf(this.gameSettings.enableVsync));
    playerSnooper.addClientStat("display_frequency", Integer.valueOf(Display.getDisplayMode().getFrequency()));
    playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
    playerSnooper.addClientStat("run_time", Long.valueOf((MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L));
    playerSnooper.addClientStat("current_action", getCurrentAction());
    String s = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) ? "little" : "big";
    playerSnooper.addClientStat("endianness", s);
    playerSnooper.addClientStat("resource_packs", Integer.valueOf(this.mcResourcePackRepository.getRepositoryEntries().size()));
    int i = 0;
    for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries())
      playerSnooper.addClientStat("resource_pack[" + i++ + "]", resourcepackrepository$entry.getResourcePackName()); 
    if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null)
      playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID()); 
  }
  
  private String getCurrentAction() {
    return (this.theIntegratedServer != null) ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer") : ((this.currentServerData != null) ? (this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer") : "out_of_game");
  }
  
  public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper) {
    playerSnooper.addStatToSnooper("opengl_version", GL11.glGetString(7938));
    playerSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(7936));
    playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
    playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
    ContextCapabilities contextcapabilities = GLContext.getCapabilities();
    playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", Boolean.valueOf(contextcapabilities.GL_ARB_arrays_of_arrays));
    playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", Boolean.valueOf(contextcapabilities.GL_ARB_base_instance));
    playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", Boolean.valueOf(contextcapabilities.GL_ARB_blend_func_extended));
    playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_clear_buffer_object));
    playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_color_buffer_float));
    playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", Boolean.valueOf(contextcapabilities.GL_ARB_compatibility));
    playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", Boolean.valueOf(contextcapabilities.GL_ARB_compressed_texture_pixel_storage));
    playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_compute_shader));
    playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_buffer));
    playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_image));
    playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_buffer_float));
    playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_compute_shader));
    playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_buffer));
    playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_image));
    playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_buffer_float));
    playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_clamp));
    playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_texture));
    playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_buffers));
    playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_buffers_blend));
    playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_elements_base_vertex));
    playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_indirect));
    playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_instanced));
    playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", Boolean.valueOf(contextcapabilities.GL_ARB_explicit_attrib_location));
    playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", Boolean.valueOf(contextcapabilities.GL_ARB_explicit_uniform_location));
    playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_layer_viewport));
    playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_program));
    playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_shader));
    playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_program_shadow));
    playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_framebuffer_object));
    playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", Boolean.valueOf(contextcapabilities.GL_ARB_framebuffer_sRGB));
    playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", Boolean.valueOf(contextcapabilities.GL_ARB_geometry_shader4));
    playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", Boolean.valueOf(contextcapabilities.GL_ARB_gpu_shader5));
    playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", Boolean.valueOf(contextcapabilities.GL_ARB_half_float_pixel));
    playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", Boolean.valueOf(contextcapabilities.GL_ARB_half_float_vertex));
    playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", Boolean.valueOf(contextcapabilities.GL_ARB_instanced_arrays));
    playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", Boolean.valueOf(contextcapabilities.GL_ARB_map_buffer_alignment));
    playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", Boolean.valueOf(contextcapabilities.GL_ARB_map_buffer_range));
    playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", Boolean.valueOf(contextcapabilities.GL_ARB_multisample));
    playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", Boolean.valueOf(contextcapabilities.GL_ARB_multitexture));
    playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", Boolean.valueOf(contextcapabilities.GL_ARB_occlusion_query2));
    playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_pixel_buffer_object));
    playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", Boolean.valueOf(contextcapabilities.GL_ARB_seamless_cube_map));
    playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_objects));
    playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_stencil_export));
    playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_texture_lod));
    playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", Boolean.valueOf(contextcapabilities.GL_ARB_shadow));
    playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", Boolean.valueOf(contextcapabilities.GL_ARB_shadow_ambient));
    playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", Boolean.valueOf(contextcapabilities.GL_ARB_stencil_texturing));
    playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", Boolean.valueOf(contextcapabilities.GL_ARB_sync));
    playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_tessellation_shader));
    playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_border_clamp));
    playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_buffer_object));
    playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_cube_map));
    playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_cube_map_array));
    playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_non_power_of_two));
    playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_uniform_buffer_object));
    playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_blend));
    playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_buffer_object));
    playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_program));
    playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_shader));
    playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", Boolean.valueOf(contextcapabilities.GL_EXT_bindable_uniform));
    playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_equation_separate));
    playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_func_separate));
    playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_minmax));
    playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_subtract));
    playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", Boolean.valueOf(contextcapabilities.GL_EXT_draw_instanced));
    playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_multisample));
    playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_object));
    playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_sRGB));
    playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", Boolean.valueOf(contextcapabilities.GL_EXT_geometry_shader4));
    playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", Boolean.valueOf(contextcapabilities.GL_EXT_gpu_program_parameters));
    playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", Boolean.valueOf(contextcapabilities.GL_EXT_gpu_shader4));
    playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", Boolean.valueOf(contextcapabilities.GL_EXT_multi_draw_arrays));
    playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", Boolean.valueOf(contextcapabilities.GL_EXT_packed_depth_stencil));
    playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", Boolean.valueOf(contextcapabilities.GL_EXT_paletted_texture));
    playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", Boolean.valueOf(contextcapabilities.GL_EXT_rescale_normal));
    playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", Boolean.valueOf(contextcapabilities.GL_EXT_separate_shader_objects));
    playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", Boolean.valueOf(contextcapabilities.GL_EXT_shader_image_load_store));
    playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", Boolean.valueOf(contextcapabilities.GL_EXT_shadow_funcs));
    playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", Boolean.valueOf(contextcapabilities.GL_EXT_shared_texture_palette));
    playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_clear_tag));
    playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_two_side));
    playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_wrap));
    playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_3d));
    playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_array));
    playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_buffer_object));
    playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_integer));
    playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_lod_bias));
    playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_sRGB));
    playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", Boolean.valueOf(contextcapabilities.GL_EXT_vertex_shader));
    playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", Boolean.valueOf(contextcapabilities.GL_EXT_vertex_weighting));
    playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", Integer.valueOf(GL11.glGetInteger(35658)));
    GL11.glGetError();
    playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", Integer.valueOf(GL11.glGetInteger(35657)));
    GL11.glGetError();
    playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", Integer.valueOf(GL11.glGetInteger(34921)));
    GL11.glGetError();
    playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", Integer.valueOf(GL11.glGetInteger(35660)));
    GL11.glGetError();
    playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", Integer.valueOf(GL11.glGetInteger(34930)));
    GL11.glGetError();
    playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", Integer.valueOf(GL11.glGetInteger(35071)));
    GL11.glGetError();
    playerSnooper.addStatToSnooper("gl_max_texture_size", Integer.valueOf(getGLMaximumTextureSize()));
  }
  
  public static int getGLMaximumTextureSize() {
    for (int i = 16384; i > 0; i >>= 1) {
      GL11.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (ByteBuffer)null);
      int j = GL11.glGetTexLevelParameteri(32868, 0, 4096);
      if (j != 0)
        return i; 
    } 
    return -1;
  }
  
  public boolean isSnooperEnabled() {
    return this.gameSettings.snooperEnabled;
  }
  
  public void setServerData(ServerData serverDataIn) {
    this.currentServerData = serverDataIn;
  }
  
  public ServerData getCurrentServerData() {
    return this.currentServerData;
  }
  
  public boolean isIntegratedServerRunning() {
    return this.integratedServerIsRunning;
  }
  
  public boolean isSingleplayer() {
    return (this.integratedServerIsRunning && this.theIntegratedServer != null);
  }
  
  public IntegratedServer getIntegratedServer() {
    return this.theIntegratedServer;
  }
  
  public static void stopIntegratedServer() {
    if (theMinecraft != null) {
      IntegratedServer integratedserver = theMinecraft.getIntegratedServer();
      if (integratedserver != null)
        integratedserver.stopServer(); 
    } 
  }
  
  public PlayerUsageSnooper getPlayerUsageSnooper() {
    return this.usageSnooper;
  }
  
  public static long getSystemTime() {
    return Sys.getTime() * 1000L / Sys.getTimerResolution();
  }
  
  public boolean isFullScreen() {
    return this.fullscreen;
  }
  
  public Session getSession() {
    return this.session;
  }
  
  public PropertyMap getTwitchDetails() {
    return this.twitchDetails;
  }
  
  public PropertyMap getProfileProperties() {
    if (this.profileProperties.isEmpty()) {
      GameProfile gameprofile = getSessionService().fillProfileProperties(this.session.getProfile(), false);
      this.profileProperties.putAll((Multimap)gameprofile.getProperties());
    } 
    return this.profileProperties;
  }
  
  public Proxy getProxy() {
    return this.proxy;
  }
  
  public TextureManager getTextureManager() {
    return this.renderEngine;
  }
  
  public IResourceManager getResourceManager() {
    return (IResourceManager)this.mcResourceManager;
  }
  
  public ResourcePackRepository getResourcePackRepository() {
    return this.mcResourcePackRepository;
  }
  
  public LanguageManager getLanguageManager() {
    return this.mcLanguageManager;
  }
  
  public TextureMap getTextureMapBlocks() {
    return this.textureMapBlocks;
  }
  
  public boolean isJava64bit() {
    return this.jvm64bit;
  }
  
  public boolean isGamePaused() {
    return this.isGamePaused;
  }
  
  public SoundHandler getSoundHandler() {
    return this.mcSoundHandler;
  }
  
  public MusicTicker.MusicType getAmbientMusicType() {
    return (this.thePlayer != null) ? ((this.thePlayer.worldObj.provider instanceof net.minecraft.world.WorldProviderHell) ? MusicTicker.MusicType.NETHER : ((this.thePlayer.worldObj.provider instanceof net.minecraft.world.WorldProviderEnd) ? ((BossStatus.bossName != null && BossStatus.statusBarTime > 0) ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END) : ((this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying) ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME))) : MusicTicker.MusicType.MENU;
  }
  
  public IStream getTwitchStream() {
    return this.stream;
  }
  
  public void dispatchKeypresses() {
    int i = (Keyboard.getEventKey() == 0) ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
    if (i != 0 && !Keyboard.isRepeatEvent() && (
      !(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L))
      if (Keyboard.getEventKeyState()) {
        if (i == this.gameSettings.keyBindStreamStartStop.getKeyCode()) {
          if (getTwitchStream().isBroadcasting()) {
            getTwitchStream().stopBroadcasting();
          } else if (getTwitchStream().isReadyToBroadcast()) {
            displayGuiScreen((GuiScreen)new GuiYesNo((result, id) -> {
                    if (result)
                      getTwitchStream().func_152930_t(); 
                    displayGuiScreen(null);
                  }I18n.format("stream.confirm_start", new Object[0]), "", 0));
          } else if (getTwitchStream().func_152928_D() && getTwitchStream().func_152936_l()) {
            if (this.theWorld != null)
              this.ingameGUI.getChatGUI().printChatMessage((IChatComponent)new ChatComponentText("Not ready to start streaming yet!")); 
          } else {
            GuiStreamUnavailable.func_152321_a(this.currentScreen);
          } 
        } else if (i == this.gameSettings.keyBindStreamPauseUnpause.getKeyCode()) {
          if (getTwitchStream().isBroadcasting())
            if (getTwitchStream().isPaused()) {
              getTwitchStream().unpause();
            } else {
              getTwitchStream().pause();
            }  
        } else if (i == this.gameSettings.keyBindStreamCommercials.getKeyCode()) {
          if (getTwitchStream().isBroadcasting())
            getTwitchStream().requestCommercial(); 
        } else if (i == this.gameSettings.keyBindStreamToggleMic.getKeyCode()) {
          this.stream.muteMicrophone(true);
        } else if (i == this.gameSettings.keyBindFullscreen.getKeyCode()) {
          toggleFullscreen();
        } else if (i == this.gameSettings.keyBindScreenshot.getKeyCode()) {
          this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
        } 
      } else if (i == this.gameSettings.keyBindStreamToggleMic.getKeyCode()) {
        this.stream.muteMicrophone(false);
      }  
  }
  
  public MinecraftSessionService getSessionService() {
    return this.sessionService;
  }
  
  public SkinManager getSkinManager() {
    return this.skinManager;
  }
  
  public Entity getRenderViewEntity() {
    return this.renderViewEntity;
  }
  
  public void setRenderViewEntity(Entity viewingEntity) {
    this.renderViewEntity = viewingEntity;
    this.entityRenderer.loadEntityShader(viewingEntity);
  }
  
  public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
    Validate.notNull(callableToSchedule);
    if (!isCallingFromMinecraftThread()) {
      ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callableToSchedule);
      synchronized (this.scheduledTasks) {
        this.scheduledTasks.add(listenablefuturetask);
        return (ListenableFuture<V>)listenablefuturetask;
      } 
    } 
    try {
      return Futures.immediateFuture(callableToSchedule.call());
    } catch (Exception exception) {
      return (ListenableFuture<V>)Futures.immediateFailedCheckedFuture(exception);
    } 
  }
  
  public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
    Validate.notNull(runnableToSchedule);
    return addScheduledTask(Executors.callable(runnableToSchedule));
  }
  
  public boolean isCallingFromMinecraftThread() {
    return (Thread.currentThread() == this.mcThread);
  }
  
  public BlockRendererDispatcher getBlockRendererDispatcher() {
    return this.blockRenderDispatcher;
  }
  
  public RenderManager getRenderManager() {
    return this.renderManager;
  }
  
  public RenderItem getRenderItem() {
    return this.renderItem;
  }
  
  public ItemRenderer getItemRenderer() {
    return this.itemRenderer;
  }
  
  public static int getDebugFPS() {
    return debugFPS;
  }
  
  public FrameTimer getFrameTimer() {
    return this.frameTimer;
  }
  
  public static Map<String, String> getSessionInfo() {
    Map<String, String> map = Maps.newHashMap();
    map.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
    map.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
    map.put("X-Minecraft-Version", "1.8.9");
    return map;
  }
  
  public boolean isConnectedToRealms() {
    return this.connectedToRealms;
  }
  
  public void setConnectedToRealms(boolean isConnected) {
    this.connectedToRealms = isConnected;
  }
}
