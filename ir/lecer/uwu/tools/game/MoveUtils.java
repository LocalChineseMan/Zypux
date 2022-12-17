package ir.lecer.uwu.tools.game;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public final class MoveUtils {
  private MoveUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  public static float getSpeed() {
    return MathHelper.sqrt_double(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
  }
  
  public static float getTimer() {
    return (Minecraft.getMinecraft()).timer.timerSpeed;
  }
  
  public static void setTimer(float timer) {
    mc.timer.timerSpeed = timer;
  }
}
