package ir.lecer.uwu;

import ir.lecer.uwu.events.GameEvent;
import ir.lecer.uwu.events.handler.EventManager;
import ir.lecer.uwu.features.Client;
import ir.lecer.uwu.features.ConfigManager;
import ir.lecer.uwu.features.Discord;
import ir.lecer.uwu.features.command.CommandManager;
import ir.lecer.uwu.impl.hud.Watermark;
import ir.lecer.uwu.interfaces.Account;
import ir.lecer.uwu.interfaces.IClient;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.ui.clickgui.ClickGUIRenderer;
import ir.lecer.uwu.ui.clickgui.SettingsManager;
import ir.lecer.uwu.ui.editor.HUDEditor;
import ir.lecer.uwu.ui.menu.MainMenu;

public class Zypux implements IClient {
  public static Zypux getInstance() {
    return instance;
  }
  
  public static final Zypux instance = new Zypux();
  
  public static final String name = "Zypux";
  
  public static final String version = "v1.0 b2";
  
  public static final String author = "Lecer";
  
  public static String getName() {
    return "Zypux";
  }
  
  public static String getVersion() {
    return "v1.0 b2";
  }
  
  public static String getAuthor() {
    return "Lecer";
  }
  
  public static String getTitle() {
    return title;
  }
  
  public static final String title = String.format("%s Client %s - by %s", new Object[] { "Zypux", "v1.0 b2", "Lecer" });
  
  public static boolean isStarted() {
    return started;
  }
  
  public static boolean started = false;
  
  public static Account account;
  
  public SettingsManager settingsManager = new SettingsManager();
  
  public static ClickGUIRenderer clickGui;
  
  public static HUDEditor hudEditor;
  
  public void onLoad() {
    Client.connect();
    MainMenu.setup();
    Discord.setupDiscord();
    Module.setupModules();
  }
  
  public void onStart() {
    Client.client.sendPacket(account.getName() + "," + account.getPassword());
    Discord.update("Starting up...", "");
    clickGui = new ClickGUIRenderer();
    hudEditor = new HUDEditor();
    FontUtils.bootstrap();
    EventManager.register(new GameEvent());
    ConfigManager.bootstrap();
    CommandManager.bootstrap();
  }
  
  public void onPostStart() {
    ConfigManager.loadSystemConfig();
    ConfigManager.startAutoSaving();
    started = true;
    Watermark.playAnimation();
  }
  
  public void onShutdown() {
    started = false;
    GameEvent.callShutdown();
    ConfigManager.saveGlobal(false);
    Discord.update("Shuting down...", "");
    EventManager.unregisterAll();
    Discord.shutdown();
  }
}
