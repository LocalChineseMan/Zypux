package ir.lecer.uwu.events;

import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.events.events.ChatSendEvent;
import ir.lecer.uwu.events.events.MoveEvent;
import ir.lecer.uwu.events.events.PressedKeyEvent;
import ir.lecer.uwu.events.events.ShutdownEvent;
import ir.lecer.uwu.events.handler.EventHandler;
import ir.lecer.uwu.features.command.Command;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.tools.game.MoveUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GameEvent {
  private static double bps;
  
  public static double getBps() {
    return bps;
  }
  
  public static void callShutdown() {
    ShutdownEvent shutdownEvent = new ShutdownEvent();
    shutdownEvent.call();
  }
  
  public static double getRoundedBPS() {
    return Math.round(bps * 100.0D) / 100.0D;
  }
  
  @EventHandler
  public void onPressedKeyEvent(PressedKeyEvent event) {
    for (Category category : Category.values()) {
      for (Module module : category.getModules()) {
        if (module.getKey() == event.getKey())
          module.toggle(); 
      } 
    } 
    if (event.getKey() == 56) {
      Minecraft.getMinecraft().displayGuiScreen(null);
      Minecraft.getMinecraft().displayGuiScreen((GuiScreen)Zypux.hudEditor);
    } 
  }
  
  @EventHandler
  public void onMoveEvent(MoveEvent event) {
    bps = (20.0F * MoveUtils.getSpeed());
    bps *= MoveUtils.getTimer();
    bps = Math.round(bps * 100.0D) / 100.0D;
  }
  
  @EventHandler
  public void onChatSendEvent(ChatSendEvent event) {
    for (Command command : Command.commands) {
      String[] args = event.getMessage().split(" ");
      if (args[0].equals(Command.prefix + command.command)) {
        event.setCancelled(true);
        command.onCommand(args);
      } 
    } 
  }
}
