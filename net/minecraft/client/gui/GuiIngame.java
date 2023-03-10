package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ir.lecer.uwu.events.GameEvent;
import ir.lecer.uwu.events.events.Render2DEvent;
import ir.lecer.uwu.impl.hud.Crosshair;
import ir.lecer.uwu.impl.hud.Hotbar;
import ir.lecer.uwu.impl.hud.Scoreboard;
import ir.lecer.uwu.impl.render.Blur;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.font.H2FontRenderer;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.tools.shader.BlurUtils;
import ir.lecer.uwu.ui.editor.HUDEditor;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;
import net.optifine.CustomColors;
import org.lwjgl.input.Mouse;

public class GuiIngame extends Gui {
  private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
  
  private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
  
  private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
  
  private final Random rand = new Random();
  
  private final Minecraft mc;
  
  private final RenderItem itemRenderer;
  
  private final GuiNewChat persistantChatGUI;
  
  private final GuiStreamIndicator streamIndicator;
  
  private int updateCounter;
  
  private String recordPlaying = "";
  
  private int recordPlayingUpFor;
  
  private boolean recordIsPlaying;
  
  public float prevVignetteBrightness = 1.0F;
  
  private int remainingHighlightTicks;
  
  private ItemStack highlightingItemStack;
  
  private final GuiOverlayDebug overlayDebug;
  
  private final GuiSpectator spectatorGui;
  
  private final GuiPlayerTabOverlay overlayPlayerList;
  
  private int titlesTimer;
  
  private String displayedTitle = "";
  
  private String displayedSubTitle = "";
  
  private int titleFadeIn;
  
  private int titleDisplayTime;
  
  private int titleFadeOut;
  
  private int playerHealth = 0;
  
  private int lastPlayerHealth = 0;
  
  private long lastSystemTime = 0L;
  
  private long healthUpdateCounter = 0L;
  
  private static int sbx;
  
  private static int sby;
  
  private static int sbx2;
  
  private static int sby2;
  
  public GuiIngame(Minecraft mcIn) {
    this.mc = mcIn;
    this.itemRenderer = mcIn.getRenderItem();
    this.overlayDebug = new GuiOverlayDebug(mcIn);
    this.spectatorGui = new GuiSpectator(mcIn);
    this.persistantChatGUI = new GuiNewChat(mcIn);
    this.streamIndicator = new GuiStreamIndicator(mcIn);
    this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
    setDefaultTitlesTimes();
  }
  
  public void setDefaultTitlesTimes() {
    this.titleFadeIn = 10;
    this.titleDisplayTime = 70;
    this.titleFadeOut = 20;
  }
  
  public void renderGameOverlay(float partialTicks) {
    ScaledResolution scaledresolution = new ScaledResolution(this.mc);
    int i = scaledresolution.getScaledWidth();
    int j = scaledresolution.getScaledHeight();
    this.mc.entityRenderer.setupOverlayRendering();
    GlStateManager.enableBlend();
    if (Config.isVignetteEnabled()) {
      renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledresolution);
    } else {
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    } 
    ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);
    if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin))
      renderPumpkinOverlay(scaledresolution); 
    if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
      float f = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
      if (f > 0.0F)
        renderPortal(f, scaledresolution); 
    } 
    if (this.mc.playerController.isSpectator()) {
      this.spectatorGui.renderTooltip(scaledresolution, partialTicks);
    } else {
      renderTooltip(scaledresolution, partialTicks);
    } 
    Render2DEvent event = new Render2DEvent(this.mc.displayWidth, this.mc.displayHeight);
    event.call();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(icons);
    GlStateManager.enableBlend();
    if (showCrosshair())
      if (Crosshair.enabled) {
        ScaledResolution rs = new ScaledResolution(this.mc);
        float bps = (float)GameEvent.getBps();
        int alpha = (int)Math.max(255.0F - bps * 6.0F, 0.0F);
        int color = (new Color(255, 255, 255, alpha)).getRGB();
        int borderColor = (new Color(0, 0, 0, alpha)).getRGB();
        RenderUtils.borderedRect(rs.getScaledWidth() / 2.0F - 12.0F - bps, rs.getScaledHeight() / 2.0F - 1.2F, rs.getScaledWidth() / 2.0F - 5.0F - bps, rs.getScaledHeight() / 2.0F + 1.2F, color, borderColor);
        RenderUtils.borderedRect(rs.getScaledWidth() / 2.0F - 1.2F, rs.getScaledHeight() / 2.0F - 12.0F - bps, rs.getScaledWidth() / 2.0F + 1.2F, rs.getScaledHeight() / 2.0F - 5.0F - bps, color, borderColor);
        RenderUtils.borderedRect(rs.getScaledWidth() / 2.0F + 5.0F + bps, rs.getScaledHeight() / 2.0F - 1.2F, rs.getScaledWidth() / 2.0F + 12.0F + bps, rs.getScaledHeight() / 2.0F + 1.2F, color, borderColor);
        RenderUtils.borderedRect(rs.getScaledWidth() / 2.0F - 1.2F, rs.getScaledHeight() / 2.0F + 5.0F + bps, rs.getScaledWidth() / 2.0F + 1.2F, rs.getScaledHeight() / 2.0F + 12.0F + bps, color, borderColor);
        RenderUtils.borderedRect(rs.getScaledWidth() / 2.0F - 1.0F, rs.getScaledHeight() / 2.0F - 1.0F, rs.getScaledWidth() / 2.0F + 1.0F, rs.getScaledHeight() / 2.0F + 1.0F, color, borderColor);
      } else {
        GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
        GlStateManager.enableAlpha();
        drawTexturedModalRect(i / 2 - 7, j / 2 - 7, 0, 0, 16, 16);
      }  
    GlStateManager.enableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    this.mc.mcProfiler.startSection("bossHealth");
    renderBossHealth();
    this.mc.mcProfiler.endSection();
    if (this.mc.playerController.shouldDrawHUD())
      renderPlayerStats(scaledresolution); 
    GlStateManager.disableBlend();
    if (this.mc.thePlayer.getSleepTimer() > 0) {
      this.mc.mcProfiler.startSection("sleep");
      GlStateManager.disableDepth();
      GlStateManager.disableAlpha();
      int j1 = this.mc.thePlayer.getSleepTimer();
      float f1 = j1 / 100.0F;
      if (f1 > 1.0F)
        f1 = 1.0F - (j1 - 100) / 10.0F; 
      int k = (int)(220.0F * f1) << 24 | 0x101020;
      drawRect(0, 0, i, j, k);
      GlStateManager.enableAlpha();
      GlStateManager.enableDepth();
      this.mc.mcProfiler.endSection();
    } 
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    int k1 = i / 2 - 91;
    if (this.mc.thePlayer.isRidingHorse()) {
      renderHorseJumpBar(scaledresolution, k1);
    } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
      renderExpBar(scaledresolution, k1);
    } 
    if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
      renderSelectedItem(scaledresolution);
    } else if (this.mc.thePlayer.isSpectator()) {
      this.spectatorGui.renderSelectedItem(scaledresolution);
    } 
    if (this.mc.isDemo())
      renderDemo(scaledresolution); 
    if (this.mc.gameSettings.showDebugInfo)
      this.overlayDebug.renderDebugInfo(scaledresolution); 
    if (this.recordPlayingUpFor > 0) {
      this.mc.mcProfiler.startSection("overlayMessage");
      float f2 = this.recordPlayingUpFor - partialTicks;
      int l1 = (int)(f2 * 255.0F / 20.0F);
      if (l1 > 255)
        l1 = 255; 
      if (l1 > 8) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((i / 2), (j - 68), 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int l = 16777215;
        if (this.recordIsPlaying)
          l = MathHelper.hsvToRGB(f2 / 50.0F, 0.7F, 0.6F) & 0xFFFFFF; 
        getFontRenderer().drawString(this.recordPlaying, -getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, l + (l1 << 24 & 0xFF000000));
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
      } 
      this.mc.mcProfiler.endSection();
    } 
    if (this.titlesTimer > 0) {
      this.mc.mcProfiler.startSection("titleAndSubtitle");
      float f3 = this.titlesTimer - partialTicks;
      int i2 = 255;
      if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
        float f4 = (this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - f3;
        i2 = (int)(f4 * 255.0F / this.titleFadeIn);
      } 
      if (this.titlesTimer <= this.titleFadeOut)
        i2 = (int)(f3 * 255.0F / this.titleFadeOut); 
      i2 = MathHelper.clamp_int(i2, 0, 255);
      if (i2 > 8) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((i / 2), (j / 2), 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.scale(4.0F, 4.0F, 4.0F);
        int j2 = i2 << 24 & 0xFF000000;
        getFontRenderer().drawString(this.displayedTitle, (-getFontRenderer().getStringWidth(this.displayedTitle) / 2), -10.0F, 0xFFFFFF | j2, true);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        getFontRenderer().drawString(this.displayedSubTitle, (-getFontRenderer().getStringWidth(this.displayedSubTitle) / 2), 5.0F, 0xFFFFFF | j2, true);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
      } 
      this.mc.mcProfiler.endSection();
    } 
    Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
    ScoreObjective scoreobjective = null;
    ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());
    if (scoreplayerteam != null) {
      int i1 = scoreplayerteam.getChatFormat().getColorIndex();
      if (i1 >= 0)
        scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1); 
    } 
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.disableAlpha();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0F, (j - 48), 0.0F);
    this.mc.mcProfiler.startSection("chat");
    this.persistantChatGUI.drawChat(this.updateCounter);
    this.mc.mcProfiler.endSection();
    GlStateManager.popMatrix();
    ScoreObjective scoreobjective1 = (scoreobjective != null) ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
    if (scoreobjective1 != null && 
      Scoreboard.isEnabled())
      renderScoreboard(scoreobjective1); 
    scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);
    if (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() > 1 || scoreobjective1 != null)) {
      this.overlayPlayerList.updatePlayerList(true);
      this.overlayPlayerList.renderPlayerlist(i, scoreboard, scoreobjective1);
    } else {
      this.overlayPlayerList.updatePlayerList(false);
    } 
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.disableLighting();
    GlStateManager.enableAlpha();
  }
  
  protected void renderTooltip(ScaledResolution sr, float partialTicks) {
    if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
      if (Blur.isEnabled())
        BlurUtils.INSTANCE.draw(0.0F, (sr.getScaledHeight() - 22), sr.getScaledWidth(), 22.0F, 20.0F); 
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(widgetsTexPath);
      EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
      int i = sr.getScaledWidth() / 2;
      float f = this.zLevel;
      this.zLevel = -90.0F;
      if (!Hotbar.isEnabled()) {
        drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
        drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
      } 
      this.zLevel = f;
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      RenderHelper.enableGUIStandardItemLighting();
      for (int j = 0; j < 9; j++) {
        int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
        int l = sr.getScaledHeight() - 16 - 3;
        renderHotbarItem(j, k, l, partialTicks, entityplayer);
      } 
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
    } 
  }
  
  public void renderHorseJumpBar(ScaledResolution scaledRes, int x) {
    this.mc.mcProfiler.startSection("jumpBar");
    this.mc.getTextureManager().bindTexture(Gui.icons);
    float f = this.mc.thePlayer.getHorseJumpPower();
    int i = 182;
    int j = (int)(f * (i + 1));
    int k = scaledRes.getScaledHeight() - 32 + 3;
    drawTexturedModalRect(x, k, 0, 84, i, 5);
    if (j > 0)
      drawTexturedModalRect(x, k, 0, 89, j, 5); 
    this.mc.mcProfiler.endSection();
  }
  
  public void renderExpBar(ScaledResolution scaledRes, int x) {
    this.mc.mcProfiler.startSection("expBar");
    this.mc.getTextureManager().bindTexture(Gui.icons);
    int i = this.mc.thePlayer.xpBarCap();
    if (i > 0) {
      int j = 182;
      int k = (int)(this.mc.thePlayer.experience * (j + 1));
      int l = scaledRes.getScaledHeight() - 32 + 3;
      drawTexturedModalRect(x, l, 0, 64, j, 5);
      if (k > 0)
        drawTexturedModalRect(x, l, 0, 69, k, 5); 
    } 
    this.mc.mcProfiler.endSection();
    if (this.mc.thePlayer.experienceLevel > 0) {
      this.mc.mcProfiler.startSection("expLevel");
      int k1 = 8453920;
      if (Config.isCustomColors())
        k1 = CustomColors.getExpBarTextColor(k1); 
      String s = "" + this.mc.thePlayer.experienceLevel;
      int l1 = (scaledRes.getScaledWidth() - getFontRenderer().getStringWidth(s)) / 2;
      int i1 = scaledRes.getScaledHeight() - 31 - 4;
      getFontRenderer().drawString(s, l1 + 1, i1, 0);
      getFontRenderer().drawString(s, l1 - 1, i1, 0);
      getFontRenderer().drawString(s, l1, i1 + 1, 0);
      getFontRenderer().drawString(s, l1, i1 - 1, 0);
      getFontRenderer().drawString(s, l1, i1, k1);
      this.mc.mcProfiler.endSection();
    } 
  }
  
  public void renderSelectedItem(ScaledResolution scaledRes) {
    this.mc.mcProfiler.startSection("selectedItemName");
    if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
      String s = this.highlightingItemStack.getDisplayName();
      if (this.highlightingItemStack.hasDisplayName())
        s = EnumChatFormatting.ITALIC + s; 
      int i = (scaledRes.getScaledWidth() - getFontRenderer().getStringWidth(s)) / 2;
      int j = scaledRes.getScaledHeight() - 59;
      if (!this.mc.playerController.shouldDrawHUD())
        j += 14; 
      int k = (int)(this.remainingHighlightTicks * 256.0F / 10.0F);
      if (k > 255)
        k = 255; 
      if (k > 0) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        getFontRenderer().drawStringWithShadow(s, i, j, 16777215 + (k << 24));
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
      } 
    } 
    this.mc.mcProfiler.endSection();
  }
  
  public void renderDemo(ScaledResolution scaledRes) {
    String s;
    this.mc.mcProfiler.startSection("demo");
    if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
      s = I18n.format("demo.demoExpired", new Object[0]);
    } else {
      s = I18n.format("demo.remainingTime", new Object[] { StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime())) });
    } 
    int i = getFontRenderer().getStringWidth(s);
    getFontRenderer().drawStringWithShadow(s, (scaledRes.getScaledWidth() - i - 10), 5.0F, 16777215);
    this.mc.mcProfiler.endSection();
  }
  
  protected boolean showCrosshair() {
    if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo)
      return false; 
    if (this.mc.playerController.isSpectator()) {
      if (this.mc.pointedEntity != null)
        return true; 
      if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
        BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
        return this.mc.theWorld.getTileEntity(blockpos) instanceof net.minecraft.inventory.IInventory;
      } 
      return false;
    } 
    return true;
  }
  
  public void renderStreamIndicator(ScaledResolution scaledRes) {
    this.streamIndicator.render(scaledRes.getScaledWidth() - 10, 10);
  }
  
  private void renderScoreboard(ScoreObjective objective) {
    ScaledResolution sr = new ScaledResolution(this.mc);
    H2FontRenderer fontRenderer = FontUtils.comfortaa_r;
    Scoreboard scoreboard = objective.getScoreboard();
    Collection<Score> collection = scoreboard.getSortedScores(objective);
    List<Score> list = (List<Score>)collection.stream().filter(p_apply_1_ -> (p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#"))).collect(Collectors.toList());
    if (list.size() > 15) {
      collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
    } else {
      collection = list;
    } 
    int i = fontRenderer.getStringWidth(objective.getDisplayName());
    for (Score score : collection) {
      ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
      String s = ScorePlayerTeam.formatPlayerName((Team)scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
      i = Math.max(i, fontRenderer.getStringWidth(s));
    } 
    int scaledWidth = HUDEditor.ScoreboardX;
    int scaledHeight = HUDEditor.ScoreboardY;
    if (HUDEditor.ScoreboardD) {
      HUDEditor.ScoreboardX = Mouse.getX() / 2;
      HUDEditor.ScoreboardY = sr.getScaledHeight() * 2 - Mouse.getY();
    } 
    int i1 = collection.size() * fontRenderer.getHeight();
    int j1 = scaledHeight / 2 + i1 / 3;
    int k1 = -3;
    int l1 = scaledWidth - i - k1;
    int j = 0;
    int backgroundColor = (new Color(1342177280, true)).getRGB();
    int stringColor = (new Color(553648127)).getRGB();
    if (Blur.isEnabled())
      BlurUtils.INSTANCE.draw(l1 - 2.0F, (j1 - i1 - fontRenderer.getHeight() - 1), (i + k1 + 7), (i1 + fontRenderer.getHeight() + 1), 20.0F); 
    for (Score score1 : collection) {
      j++;
      ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
      String s1 = ScorePlayerTeam.formatPlayerName((Team)scoreplayerteam1, score1.getPlayerName());
      int k = j1 - j * fontRenderer.getHeight();
      int l = scaledWidth - k1 + 2;
      RenderUtils.rect((l1 - 2), k, l, (k + fontRenderer.getHeight()), backgroundColor);
      fontRenderer.drawString(s1, l1, k, stringColor);
      if (j == collection.size()) {
        String s3 = objective.getDisplayName();
        RenderUtils.rect((l1 - 2), (k - fontRenderer.getHeight() - 1), l, k, backgroundColor);
        fontRenderer.drawString(s3, l1 + i / 2 - fontRenderer.getStringWidth(s3) / 2, k - fontRenderer.getHeight(), stringColor);
      } 
    } 
    sbx = l1 - 2;
    sby = j1 - i1 - fontRenderer.getHeight() - 1;
    sbx2 = l1 - 2 + i + k1 + 1;
    sby2 = j1 - i1 - fontRenderer.getHeight() - 1 + i1 + fontRenderer.getHeight() + 1;
    RenderUtils.drawShadow((l1 - 2), (j1 - i1 - fontRenderer.getHeight() - 1), (i + k1 + 7), (i1 + fontRenderer.getHeight() + 1), 9, false);
  }
  
  private void renderPlayerStats(ScaledResolution scaledRes) {
    if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
      EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
      int i = MathHelper.ceiling_float_int(entityplayer.getHealth());
      boolean flag = (this.healthUpdateCounter > this.updateCounter && (this.healthUpdateCounter - this.updateCounter) / 3L % 2L == 1L);
      if (i < this.playerHealth && entityplayer.hurtResistantTime > 0) {
        this.lastSystemTime = Minecraft.getSystemTime();
        this.healthUpdateCounter = (this.updateCounter + 20);
      } else if (i > this.playerHealth && entityplayer.hurtResistantTime > 0) {
        this.lastSystemTime = Minecraft.getSystemTime();
        this.healthUpdateCounter = (this.updateCounter + 10);
      } 
      if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
        this.playerHealth = i;
        this.lastPlayerHealth = i;
        this.lastSystemTime = Minecraft.getSystemTime();
      } 
      this.playerHealth = i;
      int j = this.lastPlayerHealth;
      this.rand.setSeed(this.updateCounter * 312871L);
      FoodStats foodstats = entityplayer.getFoodStats();
      int k = foodstats.getFoodLevel();
      IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
      int i1 = scaledRes.getScaledWidth() / 2 - 91;
      int j1 = scaledRes.getScaledWidth() / 2 + 91;
      int k1 = scaledRes.getScaledHeight() - 39;
      float f = (float)iattributeinstance.getAttributeValue();
      float f1 = entityplayer.getAbsorptionAmount();
      int l1 = MathHelper.ceiling_float_int((f + f1) / 2.0F / 10.0F);
      int i2 = Math.max(10 - l1 - 2, 3);
      int j2 = k1 - (l1 - 1) * i2 - 10;
      float f2 = f1;
      int k2 = entityplayer.getTotalArmorValue();
      int l2 = -1;
      if (entityplayer.isPotionActive(Potion.regeneration))
        l2 = this.updateCounter % MathHelper.ceiling_float_int(f + 5.0F); 
      this.mc.mcProfiler.startSection("armor");
      for (int i3 = 0; i3 < 10; i3++) {
        if (k2 > 0) {
          int j3 = i1 + i3 * 8;
          if (i3 * 2 + 1 < k2)
            drawTexturedModalRect(j3, j2, 34, 9, 9, 9); 
          if (i3 * 2 + 1 == k2)
            drawTexturedModalRect(j3, j2, 25, 9, 9, 9); 
          if (i3 * 2 + 1 > k2)
            drawTexturedModalRect(j3, j2, 16, 9, 9, 9); 
        } 
      } 
      this.mc.mcProfiler.endStartSection("health");
      for (int i6 = MathHelper.ceiling_float_int((f + f1) / 2.0F) - 1; i6 >= 0; i6--) {
        int j6 = 16;
        if (entityplayer.isPotionActive(Potion.poison)) {
          j6 += 36;
        } else if (entityplayer.isPotionActive(Potion.wither)) {
          j6 += 72;
        } 
        int k3 = 0;
        if (flag)
          k3 = 1; 
        int l3 = MathHelper.ceiling_float_int((i6 + 1) / 10.0F) - 1;
        int i4 = i1 + i6 % 10 * 8;
        int j4 = k1 - l3 * i2;
        if (i <= 4)
          j4 += this.rand.nextInt(2); 
        if (i6 == l2)
          j4 -= 2; 
        int k4 = 0;
        if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled())
          k4 = 5; 
        drawTexturedModalRect(i4, j4, 16 + k3 * 9, 9 * k4, 9, 9);
        if (flag) {
          if (i6 * 2 + 1 < j)
            drawTexturedModalRect(i4, j4, j6 + 54, 9 * k4, 9, 9); 
          if (i6 * 2 + 1 == j)
            drawTexturedModalRect(i4, j4, j6 + 63, 9 * k4, 9, 9); 
        } 
        if (f2 <= 0.0F) {
          if (i6 * 2 + 1 < i)
            drawTexturedModalRect(i4, j4, j6 + 36, 9 * k4, 9, 9); 
          if (i6 * 2 + 1 == i)
            drawTexturedModalRect(i4, j4, j6 + 45, 9 * k4, 9, 9); 
        } else {
          if (f2 == f1 && f1 % 2.0F == 1.0F) {
            drawTexturedModalRect(i4, j4, j6 + 153, 9 * k4, 9, 9);
          } else {
            drawTexturedModalRect(i4, j4, j6 + 144, 9 * k4, 9, 9);
          } 
          f2 -= 2.0F;
        } 
      } 
      Entity entity = entityplayer.ridingEntity;
      if (entity == null) {
        this.mc.mcProfiler.endStartSection("food");
        for (int k6 = 0; k6 < 10; k6++) {
          int j7 = k1;
          int l7 = 16;
          int k8 = 0;
          if (entityplayer.isPotionActive(Potion.hunger)) {
            l7 += 36;
            k8 = 13;
          } 
          if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (k * 3 + 1) == 0)
            j7 = k1 + this.rand.nextInt(3) - 1; 
          int j9 = j1 - k6 * 8 - 9;
          drawTexturedModalRect(j9, j7, 16 + k8 * 9, 27, 9, 9);
          if (k6 * 2 + 1 < k)
            drawTexturedModalRect(j9, j7, l7 + 36, 27, 9, 9); 
          if (k6 * 2 + 1 == k)
            drawTexturedModalRect(j9, j7, l7 + 45, 27, 9, 9); 
        } 
      } else if (entity instanceof EntityLivingBase) {
        this.mc.mcProfiler.endStartSection("mountHealth");
        EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
        int i7 = (int)Math.ceil(entitylivingbase.getHealth());
        float f3 = entitylivingbase.getMaxHealth();
        int j8 = (int)(f3 + 0.5F) / 2;
        if (j8 > 30)
          j8 = 30; 
        int i9 = k1;
        for (int k9 = 0; j8 > 0; k9 += 20) {
          int l4 = Math.min(j8, 10);
          j8 -= l4;
          for (int i5 = 0; i5 < l4; i5++) {
            int j5 = 52;
            int l5 = j1 - i5 * 8 - 9;
            drawTexturedModalRect(l5, i9, j5, 9, 9, 9);
            if (i5 * 2 + 1 + k9 < i7)
              drawTexturedModalRect(l5, i9, j5 + 36, 9, 9, 9); 
            if (i5 * 2 + 1 + k9 == i7)
              drawTexturedModalRect(l5, i9, j5 + 45, 9, 9, 9); 
          } 
          i9 -= 10;
        } 
      } 
      this.mc.mcProfiler.endStartSection("air");
      if (entityplayer.isInsideOfMaterial(Material.water)) {
        int l6 = this.mc.thePlayer.getAir();
        int k7 = MathHelper.ceiling_double_int((l6 - 2) * 10.0D / 300.0D);
        int i8 = MathHelper.ceiling_double_int(l6 * 10.0D / 300.0D) - k7;
        for (int l8 = 0; l8 < k7 + i8; l8++) {
          if (l8 < k7) {
            drawTexturedModalRect(j1 - l8 * 8 - 9, j2, 16, 18, 9, 9);
          } else {
            drawTexturedModalRect(j1 - l8 * 8 - 9, j2, 25, 18, 9, 9);
          } 
        } 
      } 
      this.mc.mcProfiler.endSection();
    } 
  }
  
  private void renderBossHealth() {
    if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
      BossStatus.statusBarTime--;
      ScaledResolution scaledresolution = new ScaledResolution(this.mc);
      int i = scaledresolution.getScaledWidth();
      int j = 182;
      int k = i / 2 - j / 2;
      int l = (int)(BossStatus.healthScale * (j + 1));
      int i1 = 12;
      drawTexturedModalRect(k, i1, 0, 74, j, 5);
      drawTexturedModalRect(k, i1, 0, 74, j, 5);
      if (l > 0)
        drawTexturedModalRect(k, i1, 0, 79, l, 5); 
      String s = BossStatus.bossName;
      getFontRenderer().drawStringWithShadow(s, (i / 2 - getFontRenderer().getStringWidth(s) / 2), (i1 - 10), 16777215);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
    } 
  }
  
  private void renderPumpkinOverlay(ScaledResolution scaledRes) {
    GlStateManager.disableDepth();
    GlStateManager.depthMask(false);
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.disableAlpha();
    this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldrenderer.pos(0.0D, scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
    worldrenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
    worldrenderer.pos(scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
    worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
    tessellator.draw();
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableAlpha();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  private void renderVignette(float lightLevel, ScaledResolution scaledRes) {
    if (!Config.isVignetteEnabled()) {
      GlStateManager.enableDepth();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    } else {
      lightLevel = 1.0F - lightLevel;
      lightLevel = MathHelper.clamp_float(lightLevel, 0.0F, 1.0F);
      WorldBorder worldborder = this.mc.theWorld.getWorldBorder();
      float f = (float)worldborder.getClosestDistance((Entity)this.mc.thePlayer);
      double d0 = Math.min(worldborder.getResizeSpeed() * worldborder.getWarningTime() * 1000.0D, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
      double d1 = Math.max(worldborder.getWarningDistance(), d0);
      if (f < d1) {
        f = 1.0F - (float)(f / d1);
      } else {
        f = 0.0F;
      } 
      this.prevVignetteBrightness = (float)(this.prevVignetteBrightness + (lightLevel - this.prevVignetteBrightness) * 0.01D);
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
      if (f > 0.0F) {
        GlStateManager.color(0.0F, f, f, 1.0F);
      } else {
        GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
      } 
      this.mc.getTextureManager().bindTexture(vignetteTexPath);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos(0.0D, scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
      worldrenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
      worldrenderer.pos(scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
      worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    } 
  }
  
  private void renderPortal(float timeInPortal, ScaledResolution scaledRes) {
    if (timeInPortal < 1.0F) {
      timeInPortal *= timeInPortal;
      timeInPortal *= timeInPortal;
      timeInPortal = timeInPortal * 0.8F + 0.2F;
    } 
    GlStateManager.disableAlpha();
    GlStateManager.disableDepth();
    GlStateManager.depthMask(false);
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(1.0F, 1.0F, 1.0F, timeInPortal);
    this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
    TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
    float f = textureatlassprite.getMinU();
    float f1 = textureatlassprite.getMinV();
    float f2 = textureatlassprite.getMaxU();
    float f3 = textureatlassprite.getMaxV();
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldrenderer.pos(0.0D, scaledRes.getScaledHeight(), -90.0D).tex(f, f3).endVertex();
    worldrenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0D).tex(f2, f3).endVertex();
    worldrenderer.pos(scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(f2, f1).endVertex();
    worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(f, f1).endVertex();
    tessellator.draw();
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableAlpha();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
    ItemStack itemstack = player.inventory.mainInventory[index];
    if (itemstack != null) {
      float f = itemstack.animationsToGo - partialTicks;
      if (f > 0.0F) {
        GlStateManager.pushMatrix();
        float f1 = 1.0F + f / 5.0F;
        GlStateManager.translate((xPos + 8), (yPos + 12), 0.0F);
        GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
        GlStateManager.translate(-(xPos + 8), -(yPos + 12), 0.0F);
      } 
      this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);
      if (f > 0.0F)
        GlStateManager.popMatrix(); 
      this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, itemstack, xPos, yPos);
    } 
  }
  
  public void updateTick() {
    if (this.recordPlayingUpFor > 0)
      this.recordPlayingUpFor--; 
    if (this.titlesTimer > 0) {
      this.titlesTimer--;
      if (this.titlesTimer <= 0) {
        this.displayedTitle = "";
        this.displayedSubTitle = "";
      } 
    } 
    this.updateCounter++;
    this.streamIndicator.updateStreamAlpha();
    if (this.mc.thePlayer != null) {
      ItemStack itemstack = this.mc.thePlayer.inventory.getCurrentItem();
      if (itemstack == null) {
        this.remainingHighlightTicks = 0;
      } else if (this.highlightingItemStack != null && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
        if (this.remainingHighlightTicks > 0)
          this.remainingHighlightTicks--; 
      } else {
        this.remainingHighlightTicks = 40;
      } 
      this.highlightingItemStack = itemstack;
    } 
  }
  
  public void setRecordPlayingMessage(String recordName) {
    setRecordPlaying(I18n.format("record.nowPlaying", new Object[] { recordName }), true);
  }
  
  public void setRecordPlaying(String message, boolean isPlaying) {
    this.recordPlaying = message;
    this.recordPlayingUpFor = 60;
    this.recordIsPlaying = isPlaying;
  }
  
  public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut) {
    if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0) {
      this.displayedTitle = "";
      this.displayedSubTitle = "";
      this.titlesTimer = 0;
    } else if (title != null) {
      this.displayedTitle = title;
      this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
    } else if (subTitle != null) {
      this.displayedSubTitle = subTitle;
    } else {
      if (timeFadeIn >= 0)
        this.titleFadeIn = timeFadeIn; 
      if (displayTime >= 0)
        this.titleDisplayTime = displayTime; 
      if (timeFadeOut >= 0)
        this.titleFadeOut = timeFadeOut; 
      if (this.titlesTimer > 0)
        this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut; 
    } 
  }
  
  public static boolean isHoveredScoreboard(int mouseX, int mouseY) {
    return (mouseX >= sbx && mouseX <= sbx2 && mouseY >= sby && mouseY <= sby2);
  }
  
  public void setRecordPlaying(IChatComponent component, boolean isPlaying) {
    setRecordPlaying(component.getUnformattedText(), isPlaying);
  }
  
  public GuiNewChat getChatGUI() {
    return this.persistantChatGUI;
  }
  
  public int getUpdateCounter() {
    return this.updateCounter;
  }
  
  public FontRenderer getFontRenderer() {
    return this.mc.fontRendererObj;
  }
  
  public GuiSpectator getSpectatorGui() {
    return this.spectatorGui;
  }
  
  public GuiPlayerTabOverlay getTabList() {
    return this.overlayPlayerList;
  }
  
  public void resetPlayersOverlayFooterHeader() {
    this.overlayPlayerList.resetFooterHeader();
  }
}
