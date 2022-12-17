package ir.lecer.uwu.tools.renders;

import java.awt.Color;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.opengl.GL11;

public final class ColorUtils {
  private ColorUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  public static Pattern COLOR_PATTERN = Pattern.compile("(?i)&[\\dA-FK-OR]");
  
  public static final char SECTION_CHAR = '§';
  
  public static final char AMPERSAND_CHAR = '&';
  
  public static final String COLOR_CODES_STR = "0123456789abcdefkmnlor";
  
  public static final String[] COLOR_CODES = "0123456789abcdefkmnlor".split("");
  
  public static String BLACK = '§' + COLOR_CODES[0];
  
  public static String DARK_BLUE = '§' + COLOR_CODES[1];
  
  public static String DARK_GREEN = '§' + COLOR_CODES[2];
  
  public static String CYAN = '§' + COLOR_CODES[3];
  
  public static String DARK_RED = '§' + COLOR_CODES[4];
  
  public static String PURPLE = '§' + COLOR_CODES[5];
  
  public static String ORANGE = '§' + COLOR_CODES[6];
  
  public static String GRAY = '§' + COLOR_CODES[7];
  
  public static String DARK_GRAY = '§' + COLOR_CODES[8];
  
  public static String BLUE = '§' + COLOR_CODES[9];
  
  public static String LIME = '§' + COLOR_CODES[10];
  
  public static String AQUA = '§' + COLOR_CODES[11];
  
  public static String RED = '§' + COLOR_CODES[12];
  
  public static String PINK = '§' + COLOR_CODES[13];
  
  public static String YELLOW = '§' + COLOR_CODES[14];
  
  public static String WHITE = '§' + COLOR_CODES[15];
  
  public static String OBFUSCATED = '§' + COLOR_CODES[16];
  
  public static String BOLD = '§' + COLOR_CODES[17];
  
  public static String STRIKETHROUGH = '§' + COLOR_CODES[18];
  
  public static String UNDERLINE = '§' + COLOR_CODES[19];
  
  public static String ITALIC = '§' + COLOR_CODES[20];
  
  public static String RESET = '§' + COLOR_CODES[21];
  
  public static final int getClickGUIBorderColor = (new Color(65526)).getRGB();
  
  public static String colorize(String... messages) {
    return (messages == null) ? null : 
      
      translateAlternateColorCodes('&', '§', String.join(" ", (CharSequence[])messages));
  }
  
  public static String decolorize(String... messages) {
    return (messages == null) ? null : 
      
      translateAlternateColorCodes('§', '&', String.join(" ", (CharSequence[])messages));
  }
  
  private static String translateAlternateColorCodes(char from, char to, String textToTranslate) {
    char[] b = textToTranslate.toCharArray();
    IntStream.range(0, b.length - 1)
      .filter(i -> (b[i] == from && "0123456789abcdefkmnlor".indexOf(b[i + 1]) > -1))
      .forEach(i -> {
          b[i] = to;
          b[i + 1] = Character.toLowerCase(b[i + 1]);
        });
    return new String(b);
  }
  
  public static ChatComponentText colorizedComponent(String... message) {
    return new ChatComponentText(colorize(message));
  }
  
  public static String colorizedFontRenderer(String... message) {
    return FontRenderer.getFormatFromString(colorize(message));
  }
  
  public static Color glColor(int color, float alpha) {
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
    return new Color(red, green, blue, alpha);
  }
  
  public static int rainbow(int speed) {
    int thespeed = speed * 1000;
    float hue = (float)(System.currentTimeMillis() % thespeed) / 10000.0F;
    return Color.HSBtoRGB(hue, 1.0F, 1.0F);
  }
  
  public static void glColor(Color color) {
    GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
  }
  
  public static int getRainbowInt(float seconds, float saturation, float brightness, long index) {
    float hue = (float)((System.currentTimeMillis() + index) % (int)(seconds * 1000.0F)) / seconds * 1000.0F;
    return Color.HSBtoRGB(hue, saturation, brightness);
  }
  
  public static Color getRainbowColor(float seconds, float saturation, float brightness, long index) {
    float hue = (float)(System.currentTimeMillis() + index) % seconds * 1000.0F / seconds * 1000.0F;
    return Color.getHSBColor(hue, saturation, brightness);
  }
  
  public static Color getRainbowColor(float speed) {
    return Color.getHSBColor((float)System.currentTimeMillis() % 360.0F * speed / 360.0F * speed, 1.0F, 1.0F);
  }
  
  public static String strip(String text) {
    return text.replaceAll(String.format("%s[%s]", new Object[] { Character.valueOf('§'), "0123456789abcdefkmnlor" }), "");
  }
  
  public static void glColor(int color) {
    glColor(new Color(color));
  }
  
  public static String calcHigh(double data, double warning, double lifeOrDie) {
    return ((data >= lifeOrDie) ? RED : ((data >= warning) ? YELLOW : LIME)) + data;
  }
  
  public static Object calcLow(double data, double warning, double lifeOrDie) {
    return ((data < warning) ? YELLOW : ((data < lifeOrDie) ? RED : LIME)) + data;
  }
  
  public static Color astolfo(float yDist, float yTotal, float saturation, float speedt) {
    float speed = 1800.0F;
    float hue = (float)(System.currentTimeMillis() % (int)speed) + (yTotal - yDist) * speedt;
    while (hue > speed)
      hue -= speed; 
    hue /= speed;
    if (hue > 0.5D)
      hue = 0.5F - hue - 0.5F; 
    hue += 0.5F;
    return Color.getHSBColor(hue, saturation, 1.0F);
  }
  
  public static int reAlpha(int color, float alpha) {
    Color c = new Color(color);
    float r = 0.003921569F * c.getRed();
    float g = 0.003921569F * c.getGreen();
    float b = 0.003921569F * c.getBlue();
    return (new Color(r, g, b, alpha)).getRGB();
  }
  
  public static String removeColorCode(String text) {
    String finalText = text;
    if (text.contains("??"))
      for (int i = 0; i < finalText.length(); i++) {
        if (Character.toString(finalText.charAt(i)).equals("??"))
          try {
            String part1 = finalText.substring(0, i);
            String part2 = finalText.substring(Math.min(i + 2, finalText.length()));
            finalText = part1 + part2;
          } catch (Exception exception) {} 
      }  
    return finalText;
  }
  
  public static int astolfoColors(int yOffset, int yTotal) {
    float speed = 2900.0F;
    float hue = (float)(System.currentTimeMillis() % (int)speed) + ((yTotal - yOffset) * 9);
    while (hue > speed)
      hue -= speed; 
    if ((hue /= speed) > 0.5D)
      hue = 0.5F - hue - 0.5F; 
    return Color.HSBtoRGB(hue + 0.5F, 0.5F, 1.0F);
  }
  
  public static int[] getFractionIndicies(float[] fractions, float progress) {
    int[] range = new int[2];
    int startPoint;
    for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; startPoint++);
    if (startPoint >= fractions.length)
      startPoint = fractions.length - 1; 
    range[0] = startPoint - 1;
    range[1] = startPoint;
    return range;
  }
  
  public static Color blendColors(float[] fractions, Color[] colors, float progress) {
    Color color = null;
    if (fractions != null && colors != null && fractions.length == colors.length) {
      int[] indicies = getFractionIndicies(fractions, progress);
      if (indicies[0] < 0 || indicies[0] >= fractions.length || indicies[1] < 0 || indicies[1] >= fractions.length)
        return colors[0]; 
      float[] range = { fractions[indicies[0]], fractions[indicies[1]] };
      Color[] colorRange = { colors[indicies[0]], colors[indicies[1]] };
      float max = range[1] - range[0];
      float value = progress - range[0];
      float weight = value / max;
      color = blend(colorRange[0], colorRange[1], (1.0F - weight));
    } 
    return color;
  }
  
  public static Color blend(Color color1, Color color2, double ratio) {
    float r = (float)ratio;
    float ir = 1.0F - r;
    float[] rgb1 = new float[3];
    float[] rgb2 = new float[3];
    color1.getColorComponents(rgb1);
    color2.getColorComponents(rgb2);
    float red = rgb1[0] * r + rgb2[0] * ir;
    float green = rgb1[1] * r + rgb2[1] * ir;
    float blue = rgb1[2] * r + rgb2[2] * ir;
    if (red < 0.0F) {
      red = 0.0F;
    } else if (red > 255.0F) {
      red = 255.0F;
    } 
    if (green < 0.0F) {
      green = 0.0F;
    } else if (green > 255.0F) {
      green = 255.0F;
    } 
    if (blue < 0.0F) {
      blue = 0.0F;
    } else if (blue > 255.0F) {
      blue = 255.0F;
    } 
    return new Color(red, green, blue);
  }
  
  public static int getColor(Color color) {
    return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }
  
  public static int getColor(int bright) {
    return getColor(bright, bright, bright, 255);
  }
  
  public static Color getColorWithOpacity(Color color, int alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }
  
  public static int getColor(int red, int green, int blue) {
    return getColor(red, green, blue, 255);
  }
  
  public static int getColor(int red, int green, int blue, int alpha) {
    int color = 0;
    color |= alpha << 24;
    color |= red << 16;
    color |= green << 8;
    return color | blue;
  }
  
  public static int getColor(int brightness, int alpha) {
    return getColor(brightness, brightness, brightness, alpha);
  }
  
  public static Color rainbow(int delay, float saturation, float brightness) {
    double rainbow = Math.ceil(((float)(System.currentTimeMillis() + delay) / 16.0F));
    return Color.getHSBColor((float)(rainbow % 360.0D / 360.0D), saturation, brightness);
  }
  
  public static Color rainbow2(int delay, float saturation, float brightness) {
    double rainbow = Math.ceil((float)(System.currentTimeMillis() / delay));
    return Color.getHSBColor((float)(rainbow % 360.0D / 360.0D), saturation, brightness);
  }
  
  public static String stripColor(String name) {
    return COLOR_PATTERN.matcher(name).replaceAll("");
  }
}
