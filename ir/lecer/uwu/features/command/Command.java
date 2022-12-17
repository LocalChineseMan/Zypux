package ir.lecer.uwu.features.command;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class Command {
  public void setMc(Minecraft mc) {
    this.mc = mc;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setCommand(String command) {
    this.command = command;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Command))
      return false; 
    Command other = (Command)o;
    if (!other.canEqual(this))
      return false; 
    Object this$mc = getMc(), other$mc = other.getMc();
    if ((this$mc == null) ? (other$mc != null) : !this$mc.equals(other$mc))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$command = getCommand(), other$command = other.getCommand();
    return !((this$command == null) ? (other$command != null) : !this$command.equals(other$command));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Command;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $mc = getMc();
    result = result * 59 + (($mc == null) ? 43 : $mc.hashCode());
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $command = getCommand();
    return result * 59 + (($command == null) ? 43 : $command.hashCode());
  }
  
  public String toString() {
    return "Command(mc=" + getMc() + ", name=" + getName() + ", command=" + getCommand() + ")";
  }
  
  public static ArrayList<Command> commands = new ArrayList<>();
  
  protected Minecraft mc = Minecraft.getMinecraft();
  
  public Minecraft getMc() {
    return this.mc;
  }
  
  public static char prefix = '.';
  
  public String name;
  
  public String command;
  
  public String getName() {
    return this.name;
  }
  
  public String getCommand() {
    return this.command;
  }
  
  public Command(String name, String command) {
    this.name = name;
    this.command = command;
  }
  
  public void onCommand(String[] args) {}
}
