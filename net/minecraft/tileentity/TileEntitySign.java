package net.minecraft.tileentity;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

class null implements ICommandSender {
  public String getName() {
    return playerIn.getName();
  }
  
  public IChatComponent getDisplayName() {
    return playerIn.getDisplayName();
  }
  
  public void addChatMessage(IChatComponent component) {}
  
  public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
    return (permLevel <= 2);
  }
  
  public BlockPos getPosition() {
    return TileEntitySign.this.pos;
  }
  
  public Vec3 getPositionVector() {
    return new Vec3(TileEntitySign.this.pos.getX() + 0.5D, TileEntitySign.this.pos.getY() + 0.5D, TileEntitySign.this.pos.getZ() + 0.5D);
  }
  
  public World getEntityWorld() {
    return playerIn.getEntityWorld();
  }
  
  public Entity getCommandSenderEntity() {
    return (Entity)playerIn;
  }
  
  public boolean sendCommandFeedback() {
    return false;
  }
  
  public void setCommandStat(CommandResultStats.Type type, int amount) {
    TileEntitySign.access$000(TileEntitySign.this).setCommandStatScore(this, type, amount);
  }
}
