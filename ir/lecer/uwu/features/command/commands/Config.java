package ir.lecer.uwu.features.command.commands;

import ir.lecer.uwu.enums.ChatLevels;
import ir.lecer.uwu.enums.ConfigType;
import ir.lecer.uwu.features.ChatManager;
import ir.lecer.uwu.features.ConfigManager;
import ir.lecer.uwu.features.command.Command;
import ir.lecer.uwu.features.notifications.NotificationHelper;
import java.awt.Color;
import java.io.File;

public class Config extends Command {
  public Config() {
    super("Config", "config");
  }
  
  public void onCommand(String[] args) {
    if (args.length == 1) {
      ChatManager.send("List of " + this.name + " commands:", (Enum)ChatLevels.CONFIG);
      ChatManager.raw(String.format("&8 - &7%s%s save <name>", new Object[] { Character.valueOf(prefix), this.command }));
      ChatManager.raw(String.format("&8 - &7%s%s load <name>", new Object[] { Character.valueOf(prefix), this.command }));
      ChatManager.raw(String.format("&8 - &7%s%s remove <name>", new Object[] { Character.valueOf(prefix), this.command }));
      ChatManager.raw(String.format("&8 - &7%s%s list", new Object[] { Character.valueOf(prefix), this.command }));
    } else {
      switch (args[1]) {
        case "save":
          if (args[2] == null) {
            ChatManager.send(String.format("&cUsage: &7%sconfig save <name>", new Object[] { Character.valueOf(prefix) }), (Enum)ChatLevels.CONFIG);
            break;
          } 
          if (!ConfigManager.configExists(args[2])) {
            ChatManager.send(String.format("&7Config %s.txt &asuccessfully &7saved.", new Object[] { args[2] }), (Enum)ChatLevels.CONFIG);
            NotificationHelper.send("Config Manager", String.format("Config %s.txt successfully saved.", new Object[] { args[2] }), new Color(-16711728, true), new Color(-16745370, true), 2.4D);
          } else {
            ChatManager.send(String.format("&7Config %s.txt &asuccessfully &7overwrote.", new Object[] { args[2] }), (Enum)ChatLevels.CONFIG);
            NotificationHelper.send("Config Manager", String.format("Config %s.txt successfully overwrote.", new Object[] { args[2] }), new Color(-16711728, true), new Color(-16745370, true), 2.4D);
          } 
          ConfigManager.saveCustomConfig(args[2]);
          break;
        case "load":
          if (args[2] == null) {
            ChatManager.send(String.format("&cUsage: &7%sconfig load <name>", new Object[] { Character.valueOf(prefix) }), (Enum)ChatLevels.CONFIG);
            break;
          } 
          if (ConfigManager.configExists(args[2])) {
            ConfigManager.loadConfig(ConfigType.CUSTOM, args[2]);
            ChatManager.send(String.format("&7Config %s.txt &asuccessfully &7loaded.", new Object[] { args[2] }), (Enum)ChatLevels.CONFIG);
            NotificationHelper.send("Config Manager", String.format("Config %s.txt successfully loaded.", new Object[] { args[2] }), new Color(-16711728, true), new Color(-16745370, true), 2.4D);
            break;
          } 
          ChatManager.send(String.format("&7Config %s.txt doesn't exist.", new Object[] { args[2] }), (Enum)ChatLevels.ERROR);
          break;
        case "remove":
        case "delete":
          if (args[2] == null) {
            ChatManager.send(String.format("&cUsage: &7%sconfig remove <name>", new Object[] { Character.valueOf(prefix) }), (Enum)ChatLevels.CONFIG);
            break;
          } 
          if (ConfigManager.configExists(args[2])) {
            ConfigManager.removeCustomConfig(args[2]);
            ChatManager.send(String.format("&7Config %s.txt &asuccessfully &7removed.", new Object[] { args[2] }), (Enum)ChatLevels.CONFIG);
            NotificationHelper.send("Config Manager", String.format("Config %s.txt successfully removed.", new Object[] { args[2] }), new Color(-16711728, true), new Color(-16745370, true), 2.4D);
            break;
          } 
          ChatManager.send(String.format("&7Config %s.txt is already removed.", new Object[] { args[2] }), (Enum)ChatLevels.ERROR);
          break;
        case "list":
          if ((ConfigManager.getDirConfigFiles()).length != 0) {
            ChatManager.send("List of configs &8->", (Enum)ChatLevels.CONFIG);
            for (File dirConfigFile : ConfigManager.getDirConfigFiles())
              ChatManager.send(dirConfigFile.getName(), (Enum)ChatLevels.CONFIG); 
            break;
          } 
          ChatManager.send("&7No config file found.", (Enum)ChatLevels.ERROR);
          break;
      } 
    } 
  }
}
