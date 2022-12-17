package ir.lecer.uwu.tools.utilities;

import java.util.Random;

public final class MathUtils {
  private MathUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  public static Random getRandom() {
    return random;
  }
  
  private static final Random random = new Random();
  
  public static int randInt(int min, int max) {
    return randInt(max) + min;
  }
  
  public static double randDouble(double min, double max) {
    return Math.random() * (max - min) + min;
  }
  
  public static int randInt(int max) {
    return getRandom().nextInt(max);
  }
  
  public static boolean percentage(double chance) {
    return (randDouble(0.0D, 100.0D) <= chance);
  }
  
  public static float randFloat(float min, float max) {
    return (float)(Math.random() * (max - min) + min);
  }
}
