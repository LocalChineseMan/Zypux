package ir.lecer.uwu.events.events;

import ir.lecer.uwu.events.handler.Event;
import net.minecraft.world.World;

public class WorldChangedEvent extends Event {
  private final World oldWorld;
  
  private final World newWorld;
  
  public WorldChangedEvent(World oldWorld, World newWorld) {
    this.oldWorld = oldWorld;
    this.newWorld = newWorld;
  }
  
  public World getOldWorld() {
    return this.oldWorld;
  }
  
  public World getNewWorld() {
    return this.newWorld;
  }
}
