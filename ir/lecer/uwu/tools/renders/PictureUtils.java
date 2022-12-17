package ir.lecer.uwu.tools.renders;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public final class PictureUtils {
  private PictureUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
    BufferedImage buffImg = new BufferedImage(width, height, 6);
    buffImg.getGraphics().drawImage(image.getScaledInstance(width, height, 4), 0, 0, null);
    return buffImg;
  }
  
  public static ByteBuffer readImageToBuffer(BufferedImage bufferedImage) {
    int[] rgbArray = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), (int[])null, 0, bufferedImage.getWidth());
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * rgbArray.length);
    for (int rgb : rgbArray)
      byteBuffer.putInt(rgb << 8 | rgb >> 24 & 0xFF); 
    byteBuffer.flip();
    return byteBuffer;
  }
}
