package jab.spigot.smartboards.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;

import org.bukkit.map.MapCanvas;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Document.
 *
 * @author Josh
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MapUtils {

  public static final LinkedHashMap<Color, Byte> mapColorCache = new LinkedHashMap<>();
  public static final LinkedHashMap<Integer, Byte> mapColorCacheInt = new LinkedHashMap<>();
  public static final LinkedHashMap<Byte, Integer> mapColorCacheRGB = new LinkedHashMap<>();
  public static final Color[] colors;
  public static final int[] colorsRGB;

  public static final byte TRANSPARENT = 0;
  public static final byte LIGHT_GREEN = 4;
  public static final byte LIGHT_BROWN = 8;
  public static final byte BLACK = 117;
  public static final byte GRAY_1 = 12;
  public static final byte RED = 16;
  public static final byte PALE_BLUE = 20;
  public static final byte GRAY_2 = 24;
  public static final byte DARK_GREEN = 28;
  public static final byte WHITE = 32;
  public static final byte LIGHT_GRAY = 36;
  public static final byte BROWN = 40;
  public static final byte DARK_GRAY = 44;
  public static final byte BLUE = 48;
  public static final byte DARK_BROWN = 52;
  // Cursor directions.
  public static final byte DIRECTION_SOUTH = 0;
  public static final byte DIRECTION_SOUTHWESTSOUTH = 1;
  public static final byte DIRECTION_SOUTHWEST = 2;
  public static final byte DIRECTION_SOUTHWESTWEST = 3;
  public static final byte DIRECTION_WEST = 4;
  public static final byte DIRECTION_NORTHWESTWEST = 5;
  public static final byte DIRECTION_NORTHWEST = 6;
  public static final byte DIRECTION_NORTHWESTNORTH = 7;
  public static final byte DIRECTION_NORTH = 8;
  public static final byte DIRECTION_NORTHEASTNORTH = 9;
  public static final byte DIRECTION_NORTHEAST = 10;
  public static final byte DIRECTION_NORTHEASTEAST = 11;
  public static final byte DIRECTION_EAST = 12;
  public static final byte DIRECTION_SOUTHEASTEAST = 13;
  public static final byte DIRECTION_SOUTHEAST = 14;
  public static final byte DIRECTION_SOUTHEASTSOUTH = 15;
  // Cursors.
  public static final byte CURSOR_POINTER_WHITE = 0;
  public static final byte CURSOR_POINTER_GREEN = 1;
  public static final byte CURSOR_POINTER_RED = 2;
  public static final byte CURSOR_POINTER_BLUE = 3;
  public static final byte CURSOR_CLOVER_WHITE = 4;
  public static final byte CURSOR_POINTER_RED_BOLD = 5;
  public static final byte CURSOR_DOT_WHITE = 6;
  public static final byte CURSOR_SQUARE_BLUE = 7;

  public static void clearCache() {
    mapColorCache.clear();
    mapColorCacheInt.clear();
    mapColorCacheRGB.clear();
    Color alpha = new Color(0, 0, 0, 0);
    int argb = alpha.getRGB();
    mapColorCacheRGB.put((byte) 0, argb);
    mapColorCacheInt.put(argb, (byte) 0);
    mapColorCache.put(alpha, (byte) 0);
    mapColorCacheRGB.put(toColor(BLACK), Color.BLACK.getRGB());
    mapColorCacheInt.put(Color.BLACK.getRGB(), toColor(BLACK));
    mapColorCache.put(Color.BLACK, toColor(BLACK));
    for (int i = 4; i < colors.length; i++) {
      Color color = colors[i];
      byte c = matchColor(color);
      int rgb = color.getRGB();
      mapColorCache.put(color, c);
      mapColorCacheInt.put(rgb, c);
    }
  }

  public static void drawZone(
      Graphics2D g, int centerX, int centerZ, int zoneX, int zoneZ, int radius, Color color) {
    g.setColor(color);
    int minX = ((int) Math.floor(zoneX) - radius);
    int minZ = ((int) Math.floor(zoneZ) - radius);
    int maxX = ((int) Math.floor(zoneX) + radius);
    int maxZ = ((int) Math.floor(zoneZ) + radius);
    int north = -(centerZ - minZ);
    int south = -(centerZ - maxZ);
    int east = -(centerX - maxX);
    int west = -(centerX - minX);
    boolean hasNorth = north >= -64 && north <= 64;
    boolean hasSouth = south >= -64 && south <= 64;
    boolean hasEast = east >= -64 && east <= 64;
    boolean hasWest = west >= -64 && west <= 64;
    if (hasNorth) {
      if (east > -64 && west < 64) {
        int capXMin = hasWest ? west + 64 : 0;
        int capXMax = hasEast ? east + 64 : 128;
        int z = north + 64;
        g.drawLine(capXMin, z, capXMax, z);
      }
    }
    if (hasSouth) {
      if (east > -64 && west < 64) {
        int capXMin = hasWest ? west + 64 : 0;
        int capXMax = hasEast ? east + 64 : 128;
        int z = south + 64;
        g.drawLine(capXMin, z, capXMax, z);
      }
    }
    if (hasEast) {
      if (south > -64 && north < 64) {
        int capZMin = hasNorth ? north + 64 : 0;
        int capZMax = hasSouth ? south + 64 : 128;
        int x = east + 64;
        g.drawLine(x, capZMin, x, capZMax);
      }
    }
    if (hasWest) {
      if (south > -64 && north < 64) {
        int capZMin = hasNorth ? north + 64 : 0;
        int capZMax = hasSouth ? south + 64 : 128;
        int x = west + 64;
        g.drawLine(x, capZMin, x, capZMax);
      }
    }
  }

  public static void fillZone(
      Graphics2D g, int centerX, int centerZ, int minX, int minZ, int maxX, int maxZ, Color color) {
    int capXMin = -(centerX - minX) + 64;
    int capXMax = -(centerX - maxX) + 64;
    int capZMin = -(centerZ - minZ) + 64;
    int capZMax = -(centerZ - maxZ) + 64;
    int width = (capXMax - capXMin);
    int height = (capZMax - capZMin);
    Color oldColor = g.getColor();
    g.setColor(color);
    g.fillRect(capXMin, capZMin, width, height);
    g.setColor(oldColor);
  }

  public static byte getDirection(float yaw) {
    if (yaw < 0) yaw += 360;
    byte direction;
    if (yaw >= (337.5) || yaw < 22.5) {
      direction = DIRECTION_SOUTH;
    } else if (yaw < 45) {
      direction = DIRECTION_SOUTHWESTSOUTH;
    } else if (yaw < (67.5)) {
      direction = DIRECTION_SOUTHWEST;
    } else if (yaw < 90) {
      direction = DIRECTION_SOUTHWESTWEST;
    } else if (yaw < (112.5)) {
      direction = DIRECTION_WEST;
    } else if (yaw < 135) {
      direction = DIRECTION_NORTHWESTWEST;
    } else if (yaw < (157.5)) {
      direction = DIRECTION_NORTHWEST;
    } else if (yaw < 180) {
      direction = DIRECTION_NORTHWESTNORTH;
    } else if (yaw < (202.5)) {
      direction = DIRECTION_NORTH;
    } else if (yaw < 225) {
      direction = DIRECTION_NORTHEASTNORTH;
    } else if (yaw < (247.5)) {
      direction = DIRECTION_NORTHEAST;
    } else if (yaw < 270) {
      direction = DIRECTION_NORTHEASTEAST;
    } else if (yaw < 292.5) {
      direction = DIRECTION_EAST;
    } else if (yaw < 315) {
      direction = DIRECTION_SOUTHEASTEAST;
    } else if (yaw < (337.5)) {
      direction = DIRECTION_SOUTHEAST;
    } else {
      direction = DIRECTION_SOUTHEASTSOUTH;
    }
    return direction;
  }

  /**
   * Converts a given Image into a BufferedImage
   *
   * @param img The Image to be converted
   * @return The converted BufferedImage
   */
  public static BufferedImage toBufferedImage(Image img) {
    if (img instanceof BufferedImage) {
      return (BufferedImage) img;
    }
    // Create a buffered image with transparency
    BufferedImage bimage =
        new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    // Draw the image on to the buffered image
    Graphics2D g = bimage.createGraphics();
    g.drawImage(img, 0, 0, null);
    g.dispose();
    // Return the buffered image
    return bimage;
  }

  public static byte getColor(@NotNull Color color) {
    return matchColor(color.getRGB());
  }

  private static final int TRANSPARENT_RGB = new Color(0, 0, 0, 0).getRGB();

  public static int getRGB(byte color) {
    return colorsRGB[toIndex(color)];
  }

  public static BufferedImage scaleImage(BufferedImage before, int width, int height) {
    BufferedImage after = new BufferedImage(width, height, before.getType());
    Graphics2D graphics2D = after.createGraphics();
    graphics2D.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    graphics2D.drawImage(
        before, 0, 0, width, height, 0, 0, before.getWidth(), before.getHeight(), null);
    graphics2D.dispose();
    return after;
  }

  public static void drawImage(MapCanvas canvas, int x, int y, BufferedImage image) {
    byte[] bytes = imageToBytes(image);
    for (int x2 = 0; x2 < image.getWidth(null); ++x2) {
      for (int y2 = 0; y2 < image.getHeight(null); ++y2) {
        canvas.setPixel(x + x2, y + y2, bytes[y2 * image.getWidth(null) + x2]);
      }
    }
  }

  private static double getDistance(Color c1, Color c2) {
    double rmean = (c1.getRed() + c2.getRed()) / 2.0;
    double r = c1.getRed() - c2.getRed();
    double g = c1.getGreen() - c2.getGreen();
    int b = c1.getBlue() - c2.getBlue();
    double weightR = 2 + rmean / 256.0;
    double weightG = 4.0;
    double weightB = 2 + (255 - rmean) / 256.0;
    return weightR * r * r + weightG * g * g + weightB * b * b;
  }

  private static double getDistance(int r, int g, int b, short[] color) {
    return getDistance(r, g, b, color[0], color[1], color[2]);
  }

  private static double getDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
    double rmean = (r1 + r2) / 2.0;
    double r = r1 - r2;
    double g = g1 - g2;
    int b = b1 - b2;
    double weightR = 2 + rmean / 256.0;
    double weightG = 4.0;
    double weightB = 2 + (255 - rmean) / 256.0;
    return weightR * r * r + weightG * g * g + weightB * b * b;
  }

  /**
   * Resize an image to 128x128.
   *
   * @param image The image to resize.
   * @return The resized image.
   */
  public static BufferedImage resizeImage(Image image) {
    BufferedImage result = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = result.createGraphics();
    graphics.drawImage(image, 0, 0, 128, 128, null);
    graphics.dispose();
    return result;
  }

  /**
   * Convert an Image to a byte[] using the palette.
   *
   * @param image The image to convert.
   * @return A byte[] containing the pixels of the image.
   */
  public static byte[] imageToBytes(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    int size = width * height;
    int[] pixels = image.getRGB(0, 0, width, height, new int[size], 0, width);
    byte[] result = new byte[size];
    for (int index = 0; index < pixels.length; index++) {
      result[index] = matchColor(pixels[index]);
    }
    return result;
  }

  /**
   * @param index The index to convert to a color.
   * @return Returns the byte color value of an index.
   */
  public static byte toColor(int index) {
    return (byte) (index < 128 ? index : -129 + (index - 127));
  }

  /**
   * @param color The byte color value to convert to a index.
   * @return Returns the index of a byte color.
   */
  public static int toIndex(byte color) {
    return color < 0 ? 256 + (color) : color;
  }

  public static void main(String[] args) {

    Color black = new Color(0, 0, 0);
    Color alpha = new Color(0, 0, 0, 0);
    int blackARGB = black.getRGB();
    int alphaARGB = alpha.getRGB();
    int[] blackARGBSplit = splitARGB(black.getRGB());
    int[] alphaARGBSplit = splitARGB(alpha.getRGB());

    System.out.println(
        "BLACK: " + black.getRGB()); // + "#" + Integer.toHexString(black.getRGB()).substring(2));
    System.out.println(
        "\t["
            + blackARGBSplit[0]
            + ", "
            + blackARGBSplit[1]
            + ", "
            + blackARGBSplit[2]
            + ", "
            + blackARGBSplit[3]
            + "]");

    System.out.println(
        "ALPHA: " + alpha.getRGB()); // + "#" + Integer.toHexString(alpha.getRGB()).substring(2));
    System.out.println(
        "\t["
            + alphaARGBSplit[0]
            + ", "
            + alphaARGBSplit[1]
            + ", "
            + alphaARGBSplit[2]
            + ", "
            + alphaARGBSplit[3]
            + "]");

    //    for (int index = 0; index < 256; index++) {
    //      byte color = toColor(index);
    //      int index2 = toIndex(color);
    //      System.out.println("index: " + index + "\tcolor: " + color + "\tindex2: " + index2);
    //    }
  }

  public static int[] splitARGB(int argb) {
    return new int[] {(argb >> 24) & 0xFF, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, (argb) & 0xFF};
  }

  public static int removeAlphaChannel(int argb) {
    int[] split = splitARGB(argb);
    int rgb = 255;
    rgb = (rgb << 8) + split[1];
    rgb = (rgb << 8) + split[2];
    rgb = (rgb << 8) + split[3];
    return rgb;
  }

  public static byte matchColor(int argb) {
    int[] split = splitARGB(argb);
    int rgb = 0; // (Pure alpha)
    // Only match non-alpha colors based on RGB.
    if (split[0] > 0) {
      rgb = removeAlphaChannel(argb);
    }
    // Return transparent if true alpha.
    if (rgb == 0) return 0;
    synchronized (mapColorCacheInt) {
      // Check if the color is already in the cache.
      if (mapColorCacheInt.containsKey(rgb)) {
        return mapColorCacheInt.get(rgb);
      }
      int index = 0;
      double best = Double.NaN;
      for (int i = 4; i < colors.length; i++) {
        double distance = getDistance(split[1], split[2], split[3], colorsShort[i]);
        if (distance < best || Double.isNaN(best)) {
          best = distance;
          index = i;
        }
      }
      System.out.println("index: " + index);
      // Minecraft has 143 colors, some of which have negative byte
      // representations
      byte value = (byte) (index < 128 ? index : -129 + (index - 127));
      mapColorCacheInt.put(rgb, value);
      mapColorCacheRGB.put(value, rgb);
      return value;
    }
  }
  /**
   * Get the index of the closest matching color in the palette to the given color.
   *
   * @param r The red component of the color.
   * @param b The blue component of the color.
   * @param g The green component of the color.
   * @return The index in the palette.
   */
  public static byte matchColor(int r, int g, int b) {
    return matchColor(new Color(r, g, b));
  }

  /**
   * Get the index of the closest matching color in the palette to the given color.
   *
   * @param color The Color to match.
   * @return The index in the palette.
   */
  public static byte matchColor(Color color) {
    synchronized (MapUtils.class) {
      if (color.getAlpha() < 128) return 0;
      Byte b = mapColorCache.get(color);
      if (b == null) {
        int index = 0;
        double best = -1;
        for (int i = 4; i < colors.length; i++) {
          double distance = getDistance(color, colors[i]);
          if (distance < best || best == -1) {
            best = distance;
            index = i;
          }
        }
        // Minecraft has 143 colors, some of which have negative byte
        // representations
        b = (byte) (index < 128 ? index : -129 + (index - 127));
        mapColorCache.put(color, b);
      }
      return b;
    }
  }

  /**
   * Get the value of the given color in the palette.
   *
   * @param index The index in the palette.
   * @return The Color of the palette entry.
   */
  public static Color getColor(byte index) {
    if ((index > -49 && index < 0)) {
      throw new IndexOutOfBoundsException();
    } else {
      // Minecraft has 143 colors, some of which have negative byte
      // representations
      return colors[index >= 0 ? index : index + 256];
    }
  }

  public static byte normalizeByte(int c) {
    if (c < Byte.MIN_VALUE) c = Byte.MIN_VALUE;
    else if (c > Byte.MIN_VALUE) c = Byte.MAX_VALUE;
    return (byte) c;
  }

  public static final short[][] colorsShort = {
    {0, 0, 0, 0},
    {0, 0, 0, 0},
    {0, 0, 0, 0},
    {0, 0, 0, 0},
    {89, 125, 39},
    {109, 153, 48},
    {127, 178, 56},
    {67, 94, 29},
    {174, 164, 115},
    {213, 201, 140},
    {247, 233, 163},
    {130, 123, 86},
    {140, 140, 140},
    {171, 171, 171},
    {199, 199, 199},
    {105, 105, 105},
    {180, 0, 0},
    {220, 0, 0},
    {255, 0, 0},
    {135, 0, 0},
    {112, 112, 180},
    {138, 138, 220},
    {160, 160, 255},
    {84, 84, 135},
    {117, 117, 117},
    {144, 144, 144},
    {167, 167, 167},
    {88, 88, 88},
    {0, 87, 0},
    {0, 106, 0},
    {0, 124, 0},
    {0, 65, 0},
    {180, 180, 180},
    {220, 220, 220},
    {255, 255, 255},
    {135, 135, 135},
    {115, 118, 129},
    {141, 144, 158},
    {164, 168, 184},
    {86, 88, 97},
    {106, 76, 54},
    {130, 94, 66},
    {151, 109, 77},
    {79, 57, 40},
    {79, 79, 79},
    {96, 96, 96},
    {112, 112, 112},
    {59, 59, 59},
    {45, 45, 180},
    {55, 55, 220},
    {64, 64, 255},
    {33, 33, 135},
    {100, 84, 50},
    {123, 102, 62},
    {143, 119, 72},
    {75, 63, 38},
    {180, 177, 172},
    {220, 217, 211},
    {255, 252, 245},
    {135, 133, 129},
    {152, 89, 36},
    {186, 109, 44},
    {216, 127, 51},
    {114, 67, 27},
    {125, 53, 152},
    {153, 65, 186},
    {178, 76, 216},
    {94, 40, 114},
    {72, 108, 152},
    {88, 132, 186},
    {102, 153, 216},
    {54, 81, 114},
    {161, 161, 36},
    {197, 197, 44},
    {229, 229, 51},
    {121, 121, 27},
    {89, 144, 17},
    {109, 176, 21},
    {127, 204, 25},
    {67, 108, 13},
    {170, 89, 116},
    {208, 109, 142},
    {242, 127, 165},
    {128, 67, 87},
    {53, 53, 53},
    {65, 65, 65},
    {76, 76, 76},
    {40, 40, 40},
    {108, 108, 108},
    {132, 132, 132},
    {153, 153, 153},
    {81, 81, 81},
    {53, 89, 108},
    {65, 109, 132},
    {76, 127, 153},
    {40, 67, 81},
    {89, 44, 125},
    {109, 54, 153},
    {127, 63, 178},
    {67, 33, 94},
    {36, 53, 125},
    {44, 65, 153},
    {51, 76, 178},
    {27, 40, 94},
    {72, 53, 36},
    {88, 65, 44},
    {102, 76, 51},
    {54, 40, 27},
    {72, 89, 36},
    {88, 109, 44},
    {102, 127, 51},
    {54, 67, 27},
    {108, 36, 36},
    {132, 44, 44},
    {153, 51, 51},
    {81, 27, 27},
    {17, 17, 17},
    {21, 21, 21},
    {25, 25, 25},
    {13, 13, 13},
    {176, 168, 54},
    {215, 205, 66},
    {250, 238, 77},
    {132, 126, 40},
    {64, 154, 150},
    {79, 188, 183},
    {92, 219, 213},
    {48, 115, 112},
    {52, 90, 180},
    {63, 110, 220},
    {74, 128, 255},
    {39, 67, 135},
    {0, 153, 40},
    {0, 187, 50},
    {0, 217, 58},
    {0, 114, 30},
    {91, 60, 34},
    {111, 74, 42},
    {129, 86, 49},
    {68, 45, 25},
    {79, 1, 0},
    {96, 1, 0},
    {112, 2, 0},
    {59, 1, 0},
    {147, 124, 113},
    {180, 152, 138},
    {209, 177, 161},
    {110, 93, 85},
    {112, 57, 25},
    {137, 70, 31},
    {159, 82, 36},
    {84, 43, 19},
    {105, 61, 76},
    {128, 75, 93},
    {149, 87, 108},
    {78, 46, 57},
    {79, 76, 97},
    {96, 93, 119},
    {112, 108, 138},
    {59, 57, 73},
    {131, 93, 25},
    {160, 114, 31},
    {186, 133, 36},
    {98, 70, 19},
    {72, 82, 37},
    {88, 100, 45},
    {103, 117, 53},
    {54, 61, 28},
    {112, 54, 55},
    {138, 66, 67},
    {160, 77, 78},
    {84, 40, 41},
    {40, 28, 24},
    {49, 35, 30},
    {57, 41, 35},
    {30, 21, 18},
    {95, 75, 69},
    {116, 92, 84},
    {135, 107, 98},
    {71, 56, 51},
    {61, 64, 64},
    {75, 79, 79},
    {87, 92, 92},
    {46, 48, 48},
    {86, 51, 62},
    {105, 62, 75},
    {122, 73, 88},
    {64, 38, 46},
    {53, 43, 64},
    {65, 53, 79},
    {76, 62, 92},
    {40, 32, 48},
    {53, 35, 24},
    {65, 43, 30},
    {76, 50, 35},
    {40, 26, 18},
    {53, 57, 29},
    {65, 70, 36},
    {76, 82, 42},
    {40, 43, 22},
    {100, 42, 32},
    {122, 51, 39},
    {142, 60, 46},
    {75, 31, 24},
    {26, 15, 11},
    {31, 18, 13},
    {37, 22, 16},
    {19, 11, 8}
  };

  static {
    colors = new Color[colorsShort.length];
    colors[0] = colors[1] = colors[2] = colors[3] = new Color(0, 0, 0, 0);
    for (int index = 4; index < colorsShort.length; index++) {
      short[] rgba = colorsShort[index];
      if (rgba.length == 4) {
        colors[index] = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
      } else {
        colors[index] = new Color(rgba[0], rgba[1], rgba[2]);
      }
    }
    colorsRGB = new int[colors.length];
    //    colorsRGB[0] = colorsRGB[1] = colorsRGB[2] = colorsRGB[3] = Integer.MIN_VALUE;
    for (int index = 4; index < colors.length; index++) {
      colorsRGB[index] = colors[index].getRGB();
    }
    clearCache();
  }
}
