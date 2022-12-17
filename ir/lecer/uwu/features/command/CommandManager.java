package ir.lecer.uwu.features.command;

import ir.lecer.uwu.features.command.commands.Config;

public class CommandManager {
  public static void bootstrap() {
    Command.commands.add(new Config());
  }
}
