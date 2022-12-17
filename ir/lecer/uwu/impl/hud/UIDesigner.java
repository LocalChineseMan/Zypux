package ir.lecer.uwu.impl.hud;

import com.google.common.collect.Lists;
import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.events.events.ClickGUIOpenEvent;
import ir.lecer.uwu.events.events.GuiContainerInitEvent;
import ir.lecer.uwu.events.events.GuiContainerRenderEvent;
import ir.lecer.uwu.events.events.PreClickGUIRenderEvent;
import ir.lecer.uwu.events.handler.EventHandler;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.tools.tasks.TaskManager;
import ir.lecer.uwu.ui.clickgui.SettingsManager;
import ir.lecer.uwu.ui.editor.ClientSettings;
import ir.lecer.uwu.ui.editor.HUDEditor;
import java.util.ArrayList;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class UIDesigner extends Module {
  private final ArrayList<String> animeList = Lists.newArrayList((Object[])new String[] { 
        "IcyXenoviaQuarta", "XenoviaQuartaSinger", "XenoviaQuartaWarrior", "Rossweisse", "CuteRossweisse", "BlueRossweisse", "Akeno", "SpookyAkeno", "PinkyAkeno", "AzurLane", 
        "ShidouIrina", "NursePhenex", "RiasGremory", "ZeroTwo" });
  
  private String loc = null;
  
  private double width = 0.0D, height = 0.0D;
  
  private double x = 1920.0D;
  
  public UIDesigner() {
    super("UI Designer", Category.HUD, true);
  }
  
  public void onSetup() {
    SettingsManager settingsManager = (Zypux.getInstance()).settingsManager;
    settingsManager.addSetting(new Setting("Render Anime", this, false));
    settingsManager.addSetting(new Setting("Custom Background", this, false));
    settingsManager.addSetting(new Setting("Anime", this, "IcyXenoviaQuarta", this.animeList));
    settingsManager.addSetting(new Setting("Devide", this, 4.0D, 1.0D, 8.0D, false));
    settingsManager.addSetting(new Setting("Alpha", this, 200.0D, 1.0D, 255.0D, true));
  }
  
  public void onEnable() {
    ScaledResolution resolution = new ScaledResolution(this.mc);
    this.x = resolution.getScaledWidth();
    super.onEnable();
  }
  
  @EventHandler
  public void onClickGUIOpenEvent(ClickGUIOpenEvent event) {
    animationAnime();
  }
  
  @EventHandler
  public void onPreClickGUIRenderEvent(PreClickGUIRenderEvent event) {
    renderAnime();
  }
  
  @EventHandler
  public void onGuiContainerInitEvent(GuiContainerInitEvent event) {
    animationAnime();
  }
  
  @EventHandler
  public void onGuiContainerRenderEvent(GuiContainerRenderEvent event) {
    renderAnime();
  }
  
  public void animationAnime() {
    boolean renderAnime = (Zypux.getInstance()).settingsManager.getSettingByName("Render Anime").isBooleanValue();
    ScaledResolution resolution = new ScaledResolution(this.mc);
    this.x = resolution.getScaledWidth();
    if (!renderAnime)
      return; 
    TaskManager.async(() -> {
          double a = 2.7D;
          while (this.x > resolution.getScaledWidth() - this.width) {
            this.x -= a;
            if (a >= 0.54D)
              a -= 0.013D; 
            try {
              Thread.sleep(1L);
            } catch (Exception exception) {}
          } 
        });
  }
  
  public void renderAnime() {
    if (!((ClientSettings)HUDEditor.clientSettings.get(0)).isToggle())
      return; 
    boolean renderAnime = (Zypux.getInstance()).settingsManager.getSettingByName("Render Anime").isBooleanValue();
    String anime = (Zypux.getInstance()).settingsManager.getSettingByName("Anime").getStringValue().toLowerCase();
    double devide = (Zypux.getInstance()).settingsManager.getSettingByName("Devide").getDoubleValue();
    float alpha = (float)(Zypux.getInstance()).settingsManager.getSettingByName("Alpha").getDoubleValue();
    ScaledResolution resolution = new ScaledResolution(this.mc);
    if (!renderAnime)
      return; 
    switch (anime) {
      case "icyxenoviaquarta":
        this.width = 1078.0D;
        this.height = 1488.0D;
        this.loc = "IcyXenoviaQuarta";
        break;
      case "xenoviaquartasinger":
        this.width = 1491.0D;
        this.height = 1591.0D;
        this.loc = "XenoviaQuartaSinger";
        break;
      case "xenoviaquartawarrior":
        this.width = 1218.0D;
        this.height = 1419.0D;
        this.loc = "XenoviaQuartaWarrior";
        break;
      case "rossweisse":
        this.width = 1273.0D;
        this.height = 1561.0D;
        this.loc = "Rossweisse";
        break;
      case "bluerossweisse":
        this.width = 1118.0D;
        this.height = 1454.0D;
        this.loc = "BlueRossweisse";
        break;
      case "cuterossweisse":
        this.width = 1265.0D;
        this.height = 995.0D;
        this.loc = "CuteRossweisse";
        break;
      case "akeno":
        this.width = 1174.0D;
        this.height = 1649.0D;
        this.loc = "Akeno";
        break;
      case "pinkyakeno":
        this.width = 1225.0D;
        this.height = 1533.0D;
        this.loc = "PinkyAkeno";
        break;
      case "spookyakeno":
        this.width = 1183.0D;
        this.height = 1514.0D;
        this.loc = "SpookyAkeno";
        break;
      case "azurlane":
        this.width = 1209.0D;
        this.height = 1898.0D;
        this.loc = "AzurLane";
        break;
      case "shidouirina":
        this.width = 1514.0D;
        this.height = 1381.0D;
        this.loc = "ShidouIrina";
        break;
      case "nursephenex":
        this.width = 834.0D;
        this.height = 1600.0D;
        this.loc = "NursePhenex";
        break;
      case "riasgremory":
        this.width = 1265.0D;
        this.height = 1206.0D;
        this.loc = "RiasGremory";
        break;
      case "zerotwo":
        this.width = 720.0D;
        this.height = 1045.0D;
        this.loc = "ZeroTwo";
        break;
    } 
    this.width /= devide;
    this.height /= devide;
    RenderUtils.image(new ResourceLocation("zypux/textures/girls/" + this.loc + ".png"), this.x, resolution.getScaledHeight() - this.height, this.width, this.height, alpha);
  }
}
