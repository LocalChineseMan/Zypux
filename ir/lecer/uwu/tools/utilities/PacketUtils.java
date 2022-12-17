package ir.lecer.uwu.tools.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public final class PacketUtils {
  private static Minecraft mc;
  
  private PacketUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  public static void sendPacket(Packet<?> packet) {
    mc.thePlayer.sendQueue.addToSendQueue(packet);
  }
}
