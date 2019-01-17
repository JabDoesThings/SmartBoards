package jab.spigot.smartboards.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

/**
 * This class provides utilities for MapImages.
 *
 * @author Josh
 */
@SuppressWarnings("WeakerAccess")
public class MapImageUtils {

  public static final MapImage[] STATIC;
  public static final MapImage WHITE;
  public static final MapImage BLACK;
  public static final Random random = new Random();

  private static byte[] STATIC_COLORS;

  /**
   * Generates a MapImage that is random gray-scale shades.
   *
   * @param width The width of the image. (in pixels)
   * @param height The height of the image. (in pixels)
   * @param mapIndex The map-index to assign to the image.
   * @param colors The color palette to populate the image.
   * @return Returns the generated MapImage.
   */
  @NotNull
  public static MapImage createStaticMap(int width, int height, int mapIndex, byte[] colors) {
    MapImage mapImage = new MapImage(width, height);
    mapImage.createPacket((short) mapIndex);
    int size = width * height;
    byte[] raster = new byte[size];
    for (int index = 0; index < size; index++) {
      raster[index] = colors[random.nextInt(colors.length)];
    }
    mapImage.setRaster(raster, 128, 128);
    mapImage.setImmutable();
    return mapImage;
  }

  /** @return Returns a random generated static MapImage. */
  @NotNull
  public static MapImage getStaticImage() {
    return STATIC[random.nextInt(STATIC.length)];
  }

  static {
    WHITE = new MapImage(128, 128, Color.WHITE);
    BLACK = new MapImage(128, 128, Color.BLACK);
    WHITE.createPacket((short) 100);
    BLACK.createPacket((short) 101);
    WHITE.setImmutable();
    BLACK.setImmutable();
    STATIC_COLORS =
        new byte[] {
          MapUtils.WHITE,
          MapUtils.LIGHT_GRAY,
          MapUtils.GRAY_1,
          MapUtils.GRAY_2,
          MapUtils.DARK_GRAY,
          MapUtils.BLACK
        };
    STATIC =
        new MapImage[] {
          createStaticMap(128, 128, 200, STATIC_COLORS),
          createStaticMap(128, 128, 201, STATIC_COLORS),
          createStaticMap(128, 128, 202, STATIC_COLORS),
          createStaticMap(128, 128, 203, STATIC_COLORS),
          createStaticMap(128, 128, 204, STATIC_COLORS),
          createStaticMap(128, 128, 205, STATIC_COLORS),
          createStaticMap(128, 128, 206, STATIC_COLORS),
          createStaticMap(128, 128, 207, STATIC_COLORS),
          createStaticMap(128, 128, 208, STATIC_COLORS),
          createStaticMap(128, 128, 209, STATIC_COLORS)
        };
  }

  public static MapImage[] getStaticImages() {
    return STATIC;
  }
}
