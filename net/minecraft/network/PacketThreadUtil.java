package net.minecraft.network;

import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.src.Config;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {
  public static int lastDimensionId = Integer.MIN_VALUE;
  
  public static <T extends INetHandler> void checkThreadAndEnqueue(Packet<T> packet, T t, IThreadListener threadListener) throws ThreadQuickExitException {
    if (!threadListener.isCallingFromMinecraftThread()) {
      threadListener.addScheduledTask(() -> {
            clientPreProcessPacket(packet);
            packet.processPacket(t);
          });
      throw ThreadQuickExitException.INSTANCE;
    } 
    clientPreProcessPacket(packet);
  }
  
  protected static void clientPreProcessPacket(Packet packet) {
    if (packet instanceof net.minecraft.network.play.server.S08PacketPlayerPosLook)
      Config.getRenderGlobal().onPlayerPositionSet(); 
    if (packet instanceof S07PacketRespawn) {
      S07PacketRespawn s07packetrespawn = (S07PacketRespawn)packet;
      lastDimensionId = s07packetrespawn.getDimensionID();
    } else if (packet instanceof S01PacketJoinGame) {
      S01PacketJoinGame s01packetjoingame = (S01PacketJoinGame)packet;
      lastDimensionId = s01packetjoingame.getDimension();
    } else {
      lastDimensionId = Integer.MIN_VALUE;
    } 
  }
}
