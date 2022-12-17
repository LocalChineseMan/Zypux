package ir.lecer.uwu.impl.hud;

import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.events.events.Render2DEvent;
import ir.lecer.uwu.events.handler.EventHandler;
import ir.lecer.uwu.features.notifications.NotificationHelper;
import ir.lecer.uwu.interfaces.Module;

public class Notifications extends Module {
  public static boolean enabled;
  
  public static boolean isEnabled() {
    return enabled;
  }
  
  public static void setEnabled(boolean enabled) {
    Notifications.enabled = enabled;
  }
  
  public Notifications() {
    super("Notifications", Category.HUD, true);
  }
  
  public void onEnable() {
    NotificationHelper.clear();
    setEnabled(true);
    super.onEnable();
  }
  
  public void onDisable() {
    super.onDisable();
    setEnabled(false);
    NotificationHelper.clear();
  }
  
  @EventHandler
  public void onRender2D(Render2DEvent event) {
    NotificationHelper.render();
  }
}
