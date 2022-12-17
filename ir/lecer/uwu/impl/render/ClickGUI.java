package ir.lecer.uwu.impl.render;

import com.google.common.collect.Lists;
import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.ui.clickgui.SettingsManager;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiScreen;

public class ClickGUI extends Module {
  public static final ArrayList<String> mode = Lists.newArrayList((Object[])new String[] { "None", "VerGradient", "HorGradient" });
  
  public ClickGUI() {
    super("ClickGUI", Category.RENDER, false);
    setKey(54);
  }
  
  public void onSetup() {
    SettingsManager settingsManager = (Zypux.getInstance()).settingsManager;
    settingsManager.addSetting(new Setting("Background Mode", this, "None", mode));
    settingsManager.addSetting(new Setting("Background A Color", this, new Color(-1291845632, true), true));
    settingsManager.addSetting(new Setting("Background B Color", this, new Color(-1291845632, true), true));
  }
  
  public void onEnable() {
    super.onEnable();
    this.mc.displayGuiScreen((GuiScreen)Zypux.clickGui);
    toggle();
  }
}
