package jab.spigot.smartboards.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;

import org.bukkit.map.MapCanvas;

public class MapUtils {

  public static LinkedHashMap<Color, Byte> mapColorCache = new LinkedHashMap<>();

  public static void clearCache() {
    mapColorCache.clear();
    for (Color color : colors) {
      mapColorCache.put(color, matchColor(color));
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
        ;
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
    int x = capXMin;
    int y = capZMin;
    int width = (capXMax - capXMin);
    int height = (capZMax - capZMin);
    Color oldColor = g.getColor();
    g.setColor(color);
    g.fillRect(x, y, width, height);
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

  public static void drawImage(MapCanvas canvas, int x, int y, Image image) {
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
  public static byte[] imageToBytes(Image image) {
    BufferedImage temp =
        new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = temp.createGraphics();
    graphics.drawImage(image, 0, 0, null);
    graphics.dispose();
    int[] pixels = new int[temp.getWidth() * temp.getHeight()];
    temp.getRGB(0, 0, temp.getWidth(), temp.getHeight(), pixels, 0, temp.getWidth());
    byte[] result = new byte[temp.getWidth() * temp.getHeight()];
    for (int i = 0; i < pixels.length; i++) {
      result[i] = matchColor(new Color(pixels[i], true));
    }
    return result;
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
    if ((index > -49 && index < 0) || index > 127) {
      throw new IndexOutOfBoundsException();
    } else {
      // Minecraft has 143 colors, some of which have negative byte
      // representations
      return colors[index >= 0 ? index : index + 256];
    }
  }

  // @formatter:off
  public static final Color[] colors = {
    new Color(0, 0, 0),
    new Color(0, 0, 0),
    new Color(0, 0, 0),
    new Color(0, 0, 0),
    new Color(89, 125, 39),
    new Color(109, 153, 48),
    new Color(127, 178, 56),
    new Color(67, 94, 29),
    new Color(174, 164, 115),
    new Color(213, 201, 140),
    new Color(247, 233, 163),
    new Color(130, 123, 86),
    new Color(140, 140, 140),
    new Color(171, 171, 171),
    new Color(199, 199, 199),
    new Color(105, 105, 105),
    new Color(180, 0, 0),
    new Color(220, 0, 0),
    new Color(255, 0, 0),
    new Color(135, 0, 0),
    new Color(112, 112, 180),
    new Color(138, 138, 220),
    new Color(160, 160, 255),
    new Color(84, 84, 135),
    new Color(117, 117, 117),
    new Color(144, 144, 144),
    new Color(167, 167, 167),
    new Color(88, 88, 88),
    new Color(0, 87, 0),
    new Color(0, 106, 0),
    new Color(0, 124, 0),
    new Color(0, 65, 0),
    new Color(180, 180, 180),
    new Color(220, 220, 220),
    new Color(255, 255, 255),
    new Color(135, 135, 135),
    new Color(115, 118, 129),
    new Color(141, 144, 158),
    new Color(164, 168, 184),
    new Color(86, 88, 97),
    new Color(106, 76, 54),
    new Color(130, 94, 66),
    new Color(151, 109, 77),
    new Color(79, 57, 40),
    new Color(79, 79, 79),
    new Color(96, 96, 96),
    new Color(112, 112, 112),
    new Color(59, 59, 59),
    new Color(45, 45, 180),
    new Color(55, 55, 220),
    new Color(64, 64, 255),
    new Color(33, 33, 135),
    new Color(100, 84, 50),
    new Color(123, 102, 62),
    new Color(143, 119, 72),
    new Color(75, 63, 38),
    new Color(180, 177, 172),
    new Color(220, 217, 211),
    new Color(255, 252, 245),
    new Color(135, 133, 129),
    new Color(152, 89, 36),
    new Color(186, 109, 44),
    new Color(216, 127, 51),
    new Color(114, 67, 27),
    new Color(125, 53, 152),
    new Color(153, 65, 186),
    new Color(178, 76, 216),
    new Color(94, 40, 114),
    new Color(72, 108, 152),
    new Color(88, 132, 186),
    new Color(102, 153, 216),
    new Color(54, 81, 114),
    new Color(161, 161, 36),
    new Color(197, 197, 44),
    new Color(229, 229, 51),
    new Color(121, 121, 27),
    new Color(89, 144, 17),
    new Color(109, 176, 21),
    new Color(127, 204, 25),
    new Color(67, 108, 13),
    new Color(170, 89, 116),
    new Color(208, 109, 142),
    new Color(242, 127, 165),
    new Color(128, 67, 87),
    new Color(53, 53, 53),
    new Color(65, 65, 65),
    new Color(76, 76, 76),
    new Color(40, 40, 40),
    new Color(108, 108, 108),
    new Color(132, 132, 132),
    new Color(153, 153, 153),
    new Color(81, 81, 81),
    new Color(53, 89, 108),
    new Color(65, 109, 132),
    new Color(76, 127, 153),
    new Color(40, 67, 81),
    new Color(89, 44, 125),
    new Color(109, 54, 153),
    new Color(127, 63, 178),
    new Color(67, 33, 94),
    new Color(36, 53, 125),
    new Color(44, 65, 153),
    new Color(51, 76, 178),
    new Color(27, 40, 94),
    new Color(72, 53, 36),
    new Color(88, 65, 44),
    new Color(102, 76, 51),
    new Color(54, 40, 27),
    new Color(72, 89, 36),
    new Color(88, 109, 44),
    new Color(102, 127, 51),
    new Color(54, 67, 27),
    new Color(108, 36, 36),
    new Color(132, 44, 44),
    new Color(153, 51, 51),
    new Color(81, 27, 27),
    new Color(17, 17, 17),
    new Color(21, 21, 21),
    new Color(25, 25, 25),
    new Color(13, 13, 13),
    new Color(176, 168, 54),
    new Color(215, 205, 66),
    new Color(250, 238, 77),
    new Color(132, 126, 40),
    new Color(64, 154, 150),
    new Color(79, 188, 183),
    new Color(92, 219, 213),
    new Color(48, 115, 112),
    new Color(52, 90, 180),
    new Color(63, 110, 220),
    new Color(74, 128, 255),
    new Color(39, 67, 135),
    new Color(0, 153, 40),
    new Color(0, 187, 50),
    new Color(0, 217, 58),
    new Color(0, 114, 30),
    new Color(91, 60, 34),
    new Color(111, 74, 42),
    new Color(129, 86, 49),
    new Color(68, 45, 25),
    new Color(79, 1, 0),
    new Color(96, 1, 0),
    new Color(112, 2, 0),
    new Color(59, 1, 0),
    new Color(147, 124, 113),
    new Color(180, 152, 138),
    new Color(209, 177, 161),
    new Color(110, 93, 85),
    new Color(112, 57, 25),
    new Color(137, 70, 31),
    new Color(159, 82, 36),
    new Color(84, 43, 19),
    new Color(105, 61, 76),
    new Color(128, 75, 93),
    new Color(149, 87, 108),
    new Color(78, 46, 57),
    new Color(79, 76, 97),
    new Color(96, 93, 119),
    new Color(112, 108, 138),
    new Color(59, 57, 73),
    new Color(131, 93, 25),
    new Color(160, 114, 31),
    new Color(186, 133, 36),
    new Color(98, 70, 19),
    new Color(72, 82, 37),
    new Color(88, 100, 45),
    new Color(103, 117, 53),
    new Color(54, 61, 28),
    new Color(112, 54, 55),
    new Color(138, 66, 67),
    new Color(160, 77, 78),
    new Color(84, 40, 41),
    new Color(40, 28, 24),
    new Color(49, 35, 30),
    new Color(57, 41, 35),
    new Color(30, 21, 18),
    new Color(95, 75, 69),
    new Color(116, 92, 84),
    new Color(135, 107, 98),
    new Color(71, 56, 51),
    new Color(61, 64, 64),
    new Color(75, 79, 79),
    new Color(87, 92, 92),
    new Color(46, 48, 48),
    new Color(86, 51, 62),
    new Color(105, 62, 75),
    new Color(122, 73, 88),
    new Color(64, 38, 46),
    new Color(53, 43, 64),
    new Color(65, 53, 79),
    new Color(76, 62, 92),
    new Color(40, 32, 48),
    new Color(53, 35, 24),
    new Color(65, 43, 30),
    new Color(76, 50, 35),
    new Color(40, 26, 18),
    new Color(53, 57, 29),
    new Color(65, 70, 36),
    new Color(76, 82, 42),
    new Color(40, 43, 22),
    new Color(100, 42, 32),
    new Color(122, 51, 39),
    new Color(142, 60, 46),
    new Color(75, 31, 24),
    new Color(26, 15, 11),
    new Color(31, 18, 13),
    new Color(37, 22, 16),
    new Color(19, 11, 8)
  };

  private static byte min = Byte.MIN_VALUE;
  private static byte max = Byte.MAX_VALUE;

  public static byte normalizeByte(int c) {
    if (c < min) c = min;
    else if (c > max) c = max;
    return (byte) c;
  }

  public static final byte TRANSPARENT = 0;
  public static final byte LIGHT_GREEN = 4;
  public static final byte LIGHT_BROWN = 8;
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

  static {
    for (Color color : colors) {
      mapColorCache.put(color, matchColor(color));
    }
  }
  // @formatter:on
}
