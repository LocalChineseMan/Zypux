package ir.lecer.uwu.events.events;

import ir.lecer.uwu.events.handler.Event;
import net.minecraft.network.Packet;

public class PacketRecievedEvent extends Event {
  private final Packet<?> packet;
  
  public PacketRecievedEvent(Packet<?> packet) {
    this.packet = packet;
  }
  
  public Packet<?> getPacket() {
    return this.packet;
  }
}
