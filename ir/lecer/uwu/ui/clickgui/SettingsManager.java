package ir.lecer.uwu.ui.clickgui;

import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.interfaces.Setting;
import java.util.ArrayList;

public class SettingsManager {
  private final ArrayList<Setting> settings = new ArrayList<>();
  
  public void addSetting(Setting in) {
    this.settings.add(in);
  }
  
  public ArrayList<Setting> getSettingsByMod(Module module) {
    ArrayList<Setting> out = new ArrayList<>();
    for (Setting setting : this.settings) {
      if (setting.getParent().getName().equals(module.getName()))
        out.add(setting); 
    } 
    if (out.isEmpty())
      return null; 
    return out;
  }
  
  public Setting getSettingByName(String name) {
    for (Setting setting : this.settings) {
      if (setting.getName().equalsIgnoreCase(name))
        return setting; 
    } 
    System.err.println("setting not found!");
    return null;
  }
}
