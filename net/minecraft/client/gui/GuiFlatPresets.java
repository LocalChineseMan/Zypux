package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.input.Keyboard;

public class GuiFlatPresets extends GuiScreen {
  private static final List<LayerItem> FLAT_WORLD_PRESETS = Lists.newArrayList();
  
  private final GuiCreateFlatWorld parentScreen;
  
  private String presetsTitle;
  
  private String presetsShare;
  
  private String field_146436_r;
  
  private ListSlot field_146435_s;
  
  private GuiButton field_146434_t;
  
  private GuiTextField field_146433_u;
  
  public GuiFlatPresets(GuiCreateFlatWorld p_i46318_1_) {
    this.parentScreen = p_i46318_1_;
  }
  
  public void initGui() {
    this.buttonList.clear();
    Keyboard.enableRepeatEvents(true);
    this.presetsTitle = I18n.format("createWorld.customize.presets.title", new Object[0]);
    this.presetsShare = I18n.format("createWorld.customize.presets.share", new Object[0]);
    this.field_146436_r = I18n.format("createWorld.customize.presets.list", new Object[0]);
    this;
    this.field_146433_u = new GuiTextField(2, this.fontRendererObj, 50, 40, width - 100, 20);
    this.field_146435_s = new ListSlot(this);
    this.field_146433_u.setMaxStringLength(1230);
    this.field_146433_u.setText(this.parentScreen.func_146384_e());
    this;
    this;
    this.buttonList.add(this.field_146434_t = new GuiButton(0, width / 2 - 155, height - 28, 150, 20, I18n.format("createWorld.customize.presets.select", new Object[0])));
    this;
    this;
    this.buttonList.add(new GuiButton(1, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel", new Object[0])));
    func_146426_g();
  }
  
  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    this.field_146435_s.handleMouseInput();
  }
  
  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    this.field_146433_u.mouseClicked(mouseX, mouseY, mouseButton);
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (!this.field_146433_u.textboxKeyTyped(typedChar, keyCode))
      super.keyTyped(typedChar, keyCode); 
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button.id == 0 && func_146430_p()) {
      this.parentScreen.func_146383_a(this.field_146433_u.getText());
      this.mc.displayGuiScreen(this.parentScreen);
    } else if (button.id == 1) {
      this.mc.displayGuiScreen(this.parentScreen);
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    this.field_146435_s.drawScreen(mouseX, mouseY, partialTicks);
    this;
    drawCenteredString(this.fontRendererObj, this.presetsTitle, width / 2, 8, 16777215);
    drawString(this.fontRendererObj, this.presetsShare, 50, 30, 10526880);
    drawString(this.fontRendererObj, this.field_146436_r, 50, 70, 10526880);
    this.field_146433_u.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  
  public void updateScreen() {
    this.field_146433_u.updateCursorCounter();
    super.updateScreen();
  }
  
  public void func_146426_g() {
    boolean flag = func_146430_p();
    this.field_146434_t.enabled = flag;
  }
  
  private boolean func_146430_p() {
    return ((this.field_146435_s.field_148175_k > -1 && this.field_146435_s.field_148175_k < FLAT_WORLD_PRESETS.size()) || this.field_146433_u.getText().length() > 1);
  }
  
  private static void func_146425_a(String p_146425_0_, Item p_146425_1_, BiomeGenBase p_146425_2_, FlatLayerInfo... p_146425_3_) {
    func_175354_a(p_146425_0_, p_146425_1_, 0, p_146425_2_, (List<String>)null, p_146425_3_);
  }
  
  private static void func_146421_a(String p_146421_0_, Item p_146421_1_, BiomeGenBase p_146421_2_, List<String> p_146421_3_, FlatLayerInfo... p_146421_4_) {
    func_175354_a(p_146421_0_, p_146421_1_, 0, p_146421_2_, p_146421_3_, p_146421_4_);
  }
  
  private static void func_175354_a(String p_175354_0_, Item p_175354_1_, int p_175354_2_, BiomeGenBase p_175354_3_, List<String> p_175354_4_, FlatLayerInfo... p_175354_5_) {
    FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
    for (int i = p_175354_5_.length - 1; i >= 0; i--)
      flatgeneratorinfo.getFlatLayers().add(p_175354_5_[i]); 
    flatgeneratorinfo.setBiome(p_175354_3_.biomeID);
    flatgeneratorinfo.func_82645_d();
    if (p_175354_4_ != null)
      for (String s : p_175354_4_)
        flatgeneratorinfo.getWorldFeatures().put(s, Maps.newHashMap());  
    FLAT_WORLD_PRESETS.add(new LayerItem(p_175354_1_, p_175354_2_, p_175354_0_, flatgeneratorinfo.toString()));
  }
  
  static {
    func_146421_a("Classic Flat", Item.getItemFromBlock((Block)Blocks.grass), BiomeGenBase.plains, Arrays.asList(new String[] { "village" }, ), new FlatLayerInfo[] { new FlatLayerInfo(1, (Block)Blocks.grass), new FlatLayerInfo(2, Blocks.dirt), new FlatLayerInfo(1, Blocks.bedrock) });
    func_146421_a("Tunnelers' Dream", Item.getItemFromBlock(Blocks.stone), BiomeGenBase.extremeHills, Arrays.asList(new String[] { "biome_1", "dungeon", "decoration", "stronghold", "mineshaft" }, ), new FlatLayerInfo[] { new FlatLayerInfo(1, (Block)Blocks.grass), new FlatLayerInfo(5, Blocks.dirt), new FlatLayerInfo(230, Blocks.stone), new FlatLayerInfo(1, Blocks.bedrock) });
    func_146421_a("Water World", Items.water_bucket, BiomeGenBase.deepOcean, Arrays.asList(new String[] { "biome_1", "oceanmonument" }, ), new FlatLayerInfo[] { new FlatLayerInfo(90, (Block)Blocks.water), new FlatLayerInfo(5, (Block)Blocks.sand), new FlatLayerInfo(5, Blocks.dirt), new FlatLayerInfo(5, Blocks.stone), new FlatLayerInfo(1, Blocks.bedrock) });
    func_175354_a("Overworld", Item.getItemFromBlock((Block)Blocks.tallgrass), BlockTallGrass.EnumType.GRASS.getMeta(), BiomeGenBase.plains, Arrays.asList(new String[] { "village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake" }, ), new FlatLayerInfo[] { new FlatLayerInfo(1, (Block)Blocks.grass), new FlatLayerInfo(3, Blocks.dirt), new FlatLayerInfo(59, Blocks.stone), new FlatLayerInfo(1, Blocks.bedrock) });
    func_146421_a("Snowy Kingdom", Item.getItemFromBlock(Blocks.snow_layer), BiomeGenBase.icePlains, Arrays.asList(new String[] { "village", "biome_1" }, ), new FlatLayerInfo[] { new FlatLayerInfo(1, Blocks.snow_layer), new FlatLayerInfo(1, (Block)Blocks.grass), new FlatLayerInfo(3, Blocks.dirt), new FlatLayerInfo(59, Blocks.stone), new FlatLayerInfo(1, Blocks.bedrock) });
    func_146421_a("Bottomless Pit", Items.feather, BiomeGenBase.plains, Arrays.asList(new String[] { "village", "biome_1" }, ), new FlatLayerInfo[] { new FlatLayerInfo(1, (Block)Blocks.grass), new FlatLayerInfo(3, Blocks.dirt), new FlatLayerInfo(2, Blocks.cobblestone) });
    func_146421_a("Desert", Item.getItemFromBlock((Block)Blocks.sand), BiomeGenBase.desert, Arrays.asList(new String[] { "village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon" }, ), new FlatLayerInfo[] { new FlatLayerInfo(8, (Block)Blocks.sand), new FlatLayerInfo(52, Blocks.sandstone), new FlatLayerInfo(3, Blocks.stone), new FlatLayerInfo(1, Blocks.bedrock) });
    func_146425_a("Redstone Ready", Items.redstone, BiomeGenBase.desert, new FlatLayerInfo[] { new FlatLayerInfo(52, Blocks.sandstone), new FlatLayerInfo(3, Blocks.stone), new FlatLayerInfo(1, Blocks.bedrock) });
  }
  
  class GuiFlatPresets {}
  
  static class GuiFlatPresets {}
}
