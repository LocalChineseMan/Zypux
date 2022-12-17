package ir.lecer.uwu.features;

import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.enums.ConfigType;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.tasks.TaskManager;
import ir.lecer.uwu.ui.clickgui.ClickGUIRenderer;
import ir.lecer.uwu.ui.clickgui.Panel;
import ir.lecer.uwu.ui.editor.ClientSettings;
import ir.lecer.uwu.ui.editor.HUDEditor;
import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;

public final class ConfigManager {
  private ConfigManager() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  private static final File fileCfgModules = new File("zypuxclient/configModules.txt");
  
  private static final File fileCfgRenders = new File("zypuxclient/configRenders.txt");
  
  private static final File fileClientSettings = new File("zypuxclient/clientSettings.txt");
  
  private static final File fileCfgBindings = new File("zypuxclient/configBindings.txt");
  
  private static final File fileCfgHuds = new File("zypuxclient/configHuds.txt");
  
  public static void bootstrap() {
    try {
      File configs = new File("zypuxclient/configs");
      if (!configs.exists())
        FileUtils.forceMkdir(configs); 
      if (!fileCfgHuds.exists())
        saveHud(); 
      if (!fileCfgModules.exists())
        saveConfig(); 
      if (!fileCfgRenders.exists())
        saveConfig(); 
      if (!fileCfgBindings.exists())
        saveBindings(); 
      if (!fileClientSettings.exists())
        saveClientSettings(); 
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void saveConfig() {
    try {
      List<String> modulesLine = new ArrayList<>();
      List<String> rendersLine = new ArrayList<>();
      for (Category category : Category.values()) {
        for (Module module : category.getModules()) {
          boolean isCheatModule = (!category.equals(Category.RENDER) && !category.equals(Category.HUD));
          if (isCheatModule) {
            if (module.isToggleable()) {
              modulesLine.add(String.format("module.%s.%s.istoggled.yes.%s", new Object[] { category.getName(), module.getName(), Boolean.valueOf(module.isToggled()) }));
            } else {
              modulesLine.add(String.format("module.%s.%s.istoggled.no.%s", new Object[] { category.getName(), module.getName(), Boolean.valueOf(module.isToggled()) }));
            } 
          } else if (module.isToggleable()) {
            rendersLine.add(String.format("module.%s.%s.istoggled.yes.%s", new Object[] { category.getName(), module.getName(), Boolean.valueOf(module.isToggled()) }));
          } else {
            rendersLine.add(String.format("module.%s.%s.istoggled.no.%s", new Object[] { category.getName(), module.getName(), Boolean.valueOf(module.isToggled()) }));
          } 
          if ((Zypux.getInstance()).settingsManager.getSettingsByMod(module) != null)
            for (Setting setting : (Zypux.getInstance()).settingsManager.getSettingsByMod(module)) {
              if (setting.isCheck())
                if (isCheatModule) {
                  modulesLine.add(String.format("module.%s.%s.settings.%s.check.%s", new Object[] { category.getName(), module.getName(), setting.getName(), Boolean.valueOf(setting.isBooleanValue()) }));
                } else {
                  rendersLine.add(String.format("module.%s.%s.settings.%s.check.%s", new Object[] { category.getName(), module.getName(), setting.getName(), Boolean.valueOf(setting.isBooleanValue()) }));
                }  
              if (setting.isCombo())
                if (isCheatModule) {
                  modulesLine.add(String.format("module.%s.%s.settings.%s.combo.%s", new Object[] { category.getName(), module.getName(), setting.getName(), setting.getStringValue() }));
                } else {
                  rendersLine.add(String.format("module.%s.%s.settings.%s.combo.%s", new Object[] { category.getName(), module.getName(), setting.getName(), setting.getStringValue() }));
                }  
              if (setting.isText())
                if (isCheatModule) {
                  modulesLine.add(String.format("module.%s.%s.settings.%s.text.%s", new Object[] { category.getName(), module.getName(), setting.getName(), setting.getStringValue() }));
                } else {
                  rendersLine.add(String.format("module.%s.%s.settings.%s.text.%s", new Object[] { category.getName(), module.getName(), setting.getName(), setting.getStringValue() }));
                }  
              if (setting.isSlider())
                if (isCheatModule) {
                  if (setting.isOnlyint()) {
                    modulesLine.add(String.format("module.%s.%s.settings.%s.slider.int.%s", new Object[] { category.getName(), module.getName(), setting.getName(), Double.valueOf(setting.getDoubleValue()) }));
                  } else {
                    modulesLine.add(String.format("module.%s.%s.settings.%s.slider.double.%s", new Object[] { category.getName(), module.getName(), setting.getName(), Double.valueOf(setting.getDoubleValue()) }));
                  } 
                } else if (setting.isOnlyint()) {
                  rendersLine.add(String.format("module.%s.%s.settings.%s.slider.int.%s", new Object[] { category.getName(), module.getName(), setting.getName(), Double.valueOf(setting.getDoubleValue()) }));
                } else {
                  rendersLine.add(String.format("module.%s.%s.settings.%s.slider.double.%s", new Object[] { category.getName(), module.getName(), setting.getName(), Double.valueOf(setting.getDoubleValue()) }));
                }  
              if (setting.isColor()) {
                if (isCheatModule) {
                  modulesLine.add(String.format("module.%s.%s.settings.%s.color.%s,%s,%s,%s", new Object[] { category.getName(), module.getName(), setting.getName(), 
                          Integer.valueOf(setting.getColor().getRed()), Integer.valueOf(setting.getColor().getGreen()), Integer.valueOf(setting.getColor().getBlue()), Integer.valueOf(setting.getColor().getAlpha()) }));
                  continue;
                } 
                rendersLine.add(String.format("module.%s.%s.settings.%s.color.%s,%s,%s,%s", new Object[] { category.getName(), module.getName(), setting.getName(), 
                        Integer.valueOf(setting.getColor().getRed()), Integer.valueOf(setting.getColor().getGreen()), Integer.valueOf(setting.getColor().getBlue()), Integer.valueOf(setting.getColor().getAlpha()) }));
              } 
            }  
        } 
      } 
      Files.write(Paths.get("zypuxclient/configModules.txt", new String[0]), (Iterable)modulesLine, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
      Files.write(Paths.get("zypuxclient/configRenders.txt", new String[0]), (Iterable)rendersLine, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void saveClientSettings() {
    try {
      List<String> settingsLine = new ArrayList<>();
      for (ClientSettings clientSetting : HUDEditor.clientSettings) {
        settingsLine.add(String.format("setting.hudeditor.%s.%s", new Object[] { clientSetting.getName(), Boolean.valueOf(clientSetting.isToggle()) }));
      } 
      Files.write(Paths.get("zypuxclient/clientSettings.txt", new String[0]), (Iterable)settingsLine, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void saveCustomConfig(String name) {
    try {
      List<String> modulesLine = new ArrayList<>();
      for (Category category : Category.values()) {
        if (!category.equals(Category.RENDER) && !category.equals(Category.HUD))
          for (Module module : category.getModules()) {
            if (module.isToggleable()) {
              modulesLine.add(String.format("module.%s.%s.istoggled.yes.%s", new Object[] { category.getName(), module.getName(), Boolean.valueOf(module.isToggled()) }));
            } else {
              modulesLine.add(String.format("module.%s.%s.istoggled.no.%s", new Object[] { category.getName(), module.getName(), Boolean.valueOf(module.isToggled()) }));
            } 
            if ((Zypux.getInstance()).settingsManager.getSettingsByMod(module) != null)
              for (Setting setting : (Zypux.getInstance()).settingsManager.getSettingsByMod(module)) {
                if (setting.isCheck())
                  modulesLine.add(String.format("module.%s.%s.settings.%s.check.%s", new Object[] { category.getName(), module.getName(), setting.getName(), Boolean.valueOf(setting.isBooleanValue()) })); 
                if (setting.isCombo())
                  modulesLine.add(String.format("module.%s.%s.settings.%s.combo.%s", new Object[] { category.getName(), module.getName(), setting.getName(), setting.getStringValue() })); 
                if (setting.isText())
                  modulesLine.add(String.format("module.%s.%s.settings.%s.text.%s", new Object[] { category.getName(), module.getName(), setting.getName(), setting.getStringValue() })); 
                if (setting.isSlider())
                  if (setting.isOnlyint()) {
                    modulesLine.add(String.format("module.%s.%s.settings.%s.slider.int.%s", new Object[] { category.getName(), module.getName(), setting.getName(), Double.valueOf(setting.getDoubleValue()) }));
                  } else {
                    modulesLine.add(String.format("module.%s.%s.settings.%s.slider.double.%s", new Object[] { category.getName(), module.getName(), setting.getName(), Double.valueOf(setting.getDoubleValue()) }));
                  }  
                if (setting.isColor())
                  modulesLine.add(String.format("module.%s.%s.settings.%s.color.%s,%s,%s,%s", new Object[] { category.getName(), module.getName(), setting.getName(), 
                          Integer.valueOf(setting.getColor().getRed()), Integer.valueOf(setting.getColor().getGreen()), Integer.valueOf(setting.getColor().getBlue()), Integer.valueOf(setting.getColor().getAlpha()) })); 
              }  
          }  
      } 
      Files.write(Paths.get("zypuxclient/configs/" + name + ".txt", new String[0]), (Iterable)modulesLine, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void saveHud() {
    try {
      List<String> hudsLine = new ArrayList<>();
      hudsLine.add(String.format("hud.scoreboard.%s,%s", new Object[] { Integer.valueOf(HUDEditor.ScoreboardX), Integer.valueOf(HUDEditor.ScoreboardY) }));
      hudsLine.add(String.format("hud.keystroke.%s,%s", new Object[] { Integer.valueOf(HUDEditor.KeystrokeX), Integer.valueOf(HUDEditor.KeystrokeY) }));
      Files.write(Paths.get("zypuxclient/configHuds.txt", new String[0]), (Iterable)hudsLine, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void saveBindings() {
    try {
      List<String> bindLine = new ArrayList<>();
      for (Category category : Category.values()) {
        for (Module module : category.getModules()) {
          bindLine.add(String.format("bind.%s.%s.%s", new Object[] { category.getName(), module.getName(), Integer.valueOf(module.getKey()) }));
        } 
      } 
      Files.write(Paths.get("zypuxclient/configBindings.txt", new String[0]), (Iterable)bindLine, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void loadConfig(ConfigType configType, String name) {
    try {
      if (configType.equals(ConfigType.MODULES) || configType.equals(ConfigType.RENDERS) || (configType.equals(ConfigType.CUSTOM) && name != null)) {
        String path = configType.getPath();
        if (configType.equals(ConfigType.CUSTOM))
          path = "zypuxclient/configs/" + name + ".txt"; 
        Scanner scanner = new Scanner(new File(path));
        while (scanner.hasNextLine()) {
          String string = scanner.nextLine();
          for (Category category : Category.values()) {
            for (Module module : category.getModules()) {
              if ((Zypux.getInstance()).settingsManager.getSettingsByMod(module) != null)
                for (Setting setting : (Zypux.getInstance()).settingsManager.getSettingsByMod(module)) {
                  if (string.contains(String.format("module.%s.%s.settings.%s.check.", new Object[] { category.getName(), module.getName(), setting.getName() })))
                    setting.setBooleanValue(Boolean.parseBoolean(string.replaceAll(String.format("module.%s.%s.settings.%s.check.", new Object[] { category.getName(), module.getName(), setting.getName() }), ""))); 
                  if (string.contains(String.format("module.%s.%s.settings.%s.combo.", new Object[] { category.getName(), module.getName(), setting.getName() })))
                    setting.setStringValue(string.replaceAll(String.format("module.%s.%s.settings.%s.combo.", new Object[] { category.getName(), module.getName(), setting.getName() }), "")); 
                  if (string.contains(String.format("module.%s.%s.settings.%s.text.", new Object[] { category.getName(), module.getName(), setting.getName() })))
                    setting.setStringValue(string.replaceAll(String.format("module.%s.%s.settings.%s.text.", new Object[] { category.getName(), module.getName(), setting.getName() }), "")); 
                  if (string.contains(String.format("module.%s.%s.settings.%s.slider.int.", new Object[] { category.getName(), module.getName(), setting.getName() }))) {
                    setting.setDoubleValue(Double.parseDouble(string.replaceAll(String.format("module.%s.%s.settings.%s.slider.int.", new Object[] { category.getName(), module.getName(), setting.getName() }), "")));
                    setting.setOnlyint(true);
                  } 
                  if (string.contains(String.format("module.%s.%s.settings.%s.slider.double.", new Object[] { category.getName(), module.getName(), setting.getName() }))) {
                    setting.setDoubleValue(Double.parseDouble(string.replaceAll(String.format("module.%s.%s.settings.%s.slider.double.", new Object[] { category.getName(), module.getName(), setting.getName() }), "")));
                    setting.setOnlyint(false);
                  } 
                  if (string.contains(String.format("module.%s.%s.settings.%s.color.", new Object[] { category.getName(), module.getName(), setting.getName() }))) {
                    String[] theColor = string.replaceAll(String.format("module.%s.%s.settings.%s.color.", new Object[] { category.getName(), module.getName(), setting.getName() }), "").split(",");
                    setting.setColor(new Color(Integer.parseInt(theColor[0]), Integer.parseInt(theColor[1]), Integer.parseInt(theColor[2]), Integer.parseInt(theColor[3])));
                  } 
                }  
              if (string.contains(String.format("module.%s.%s.istoggled.yes.", new Object[] { category.getName(), module.getName() })))
                module.setEnable(Boolean.parseBoolean(string.replaceAll(String.format("module.%s.%s.istoggled.yes.", new Object[] { category.getName(), module.getName() }), ""))); 
            } 
          } 
        } 
        scanner.close();
      } 
      if (configType.equals(ConfigType.BINDS)) {
        Scanner scanner = new Scanner(new File(configType.getPath()));
        while (scanner.hasNextLine()) {
          String string = scanner.nextLine();
          for (Category category : Category.values()) {
            for (Panel panel : ClickGUIRenderer.panels) {
              if (panel.getCategory() == category)
                for (Module module : category.getModules()) {
                  if (string.contains(String.format("bind.%s.%s.", new Object[] { category.getName(), module.getName() })))
                    module.setKey(Integer.parseInt(string.replaceAll(String.format("bind.%s.%s.", new Object[] { category.getName(), module.getName() }), ""))); 
                }  
            } 
          } 
        } 
        scanner.close();
      } 
      if (configType.equals(ConfigType.SETTINGS)) {
        Scanner scanner = new Scanner(new File(configType.getPath()));
        while (scanner.hasNextLine()) {
          String string = scanner.nextLine();
          if (string.contains("setting.hudeditor.Nsfw Mode.true")) {
            ((ClientSettings)HUDEditor.clientSettings.get(0)).setToggle(true);
          } else if (string.contains("setting.hudeditor.Nsfw Mode.false")) {
            ((ClientSettings)HUDEditor.clientSettings.get(0)).setToggle(false);
          } 
          if (string.contains("setting.hudeditor.Shadow.true")) {
            ((ClientSettings)HUDEditor.clientSettings.get(1)).setToggle(true);
            continue;
          } 
          if (string.contains("setting.hudeditor.Shadow.false"))
            ((ClientSettings)HUDEditor.clientSettings.get(1)).setToggle(false); 
        } 
        scanner.close();
      } 
      if (configType.equals(ConfigType.HUD)) {
        Scanner scanner = new Scanner(new File(configType.getPath()));
        while (scanner.hasNextLine()) {
          String string = scanner.nextLine();
          if (string.contains("hud.scoreboard.")) {
            String[] scoreboard = string.replaceAll("hud.scoreboard.", "").split(",");
            HUDEditor.ScoreboardX = Integer.parseInt(scoreboard[0]);
            HUDEditor.ScoreboardY = Integer.parseInt(scoreboard[1]);
          } 
          if (string.contains("hud.keystroke.")) {
            String[] keystroke = string.replaceAll("hud.keystroke.", "").split(",");
            HUDEditor.KeystrokeX = Integer.parseInt(keystroke[0]);
            HUDEditor.KeystrokeY = Integer.parseInt(keystroke[1]);
          } 
        } 
        scanner.close();
      } 
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void removeCustomConfig(String name) {
    try {
      Files.delete(Paths.get("zypuxclient/configs/" + name + ".txt", new String[0]));
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void loadSystemConfig() {
    try {
      bootstrap();
      loadConfig(ConfigType.MODULES, null);
      loadConfig(ConfigType.RENDERS, null);
      loadConfig(ConfigType.BINDS, null);
      loadConfig(ConfigType.SETTINGS, null);
      loadConfig(ConfigType.HUD, null);
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static void startAutoSaving() {
    TaskManager.async(() -> {
          while (Zypux.isStarted()) {
            saveGlobal(false);
            try {
              Thread.sleep(5000L);
            } catch (Exception exception) {}
          } 
        });
  }
  
  public static void saveGlobal(boolean async) {
    if (Zypux.isStarted())
      if (async) {
        TaskManager.async(() -> {
              saveConfig();
              saveClientSettings();
              saveBindings();
              saveHud();
            });
      } else {
        saveConfig();
        saveClientSettings();
        saveBindings();
        saveHud();
      }  
  }
  
  public static File[] getDirConfigFiles() {
    return (new File("zypuxclient/configs")).listFiles();
  }
  
  public static boolean configExists(String name) {
    return Files.exists(Paths.get("zypuxclient/configs/" + name + ".txt", new String[0]), new java.nio.file.LinkOption[0]);
  }
}
