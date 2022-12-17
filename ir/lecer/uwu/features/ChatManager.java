package ir.lecer.uwu.features;

import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.ChatLevels;
import ir.lecer.uwu.tools.renders.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

public final class ChatManager {
  private ChatManager() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  private static final String prefix = ColorUtils.colorize(new String[] { "&8(&b" + Zypux.getName() + "&8)" });
  
  private static final Minecraft minecraft = Minecraft.getMinecraft();
  
  public static void send(Object message, Enum<ChatLevels> level) {
    for (ChatLevels chatLevel : ChatLevels.values()) {
      if (chatLevel.getNumber() == ChatLevels.valueOf(String.valueOf(level)).getNumber()) {
        String format = String.format("%s &%s%s &8&l» &7%s", new Object[] { prefix, Character.valueOf(chatLevel.getColor()), chatLevel.getName(), message });
        raw(format);
      } 
    } 
  }
  
  public static void raw(String message) {
    if (minecraft.thePlayer == null)
      return; 
    minecraft.thePlayer.addChatMessage((IChatComponent)ColorUtils.colorizedComponent(new String[] { message }));
  }
}
