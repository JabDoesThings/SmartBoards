package jab.spigot.smartboards.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;

import jab.spigot.smartboards.PluginSmartBoards;
import jab.spigot.smartboards.SmartBoardThread;
import jab.spigot.smartboards.enums.AnchorFlag;
import jab.spigot.smartboards.exceptions.IllegalDimensionsException;
import net.minecraft.server.v1_13_R2.PacketPlayOutMap;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles storing and caching images for Bukkit maps.
 *
 * <p>TODO: Document & Implement scaled transformations.
 *
 * @author Josh
 */
public class MapImage {

  public static final MapImage[] STATIC;
  public static final MapImage WHITE;
  public static final MapImage BLACK;
  public static final Random random = new Random();

  private static byte[] STATIC_COLORS;

  /** The stored raster of map colors for the image. */
  private final byte[] bytes;
  /** The width in pixels of the image. */
  private int width;
  /** The height in pixels of the image. */
  private int height;
  /** Flag to mark the image as dirty. */
  private boolean dirty;

  private boolean immutable;

  private PacketPlayOutMap packet;
  private long update;

  /**
   * Image constructor.
   *
   * @param image The BufferedImage to render.
   */
  public MapImage(@NotNull BufferedImage image) {
    int bWidth = 0;
    int bHeight = 0;
    int sWidth = image.getWidth();
    int sHeight = image.getHeight();
    while (sWidth > 0) {
      sWidth -= 128;
      bWidth++;
    }
    while (sHeight > 0) {
      sHeight -= 128;
      bHeight++;
    }
    int width = bWidth * 128;
    int height = bHeight * 128;
    setWidth(width);
    setHeight(height);
    this.bytes = new byte[width * height];
    draw(image, 0, 0);
    setDirty(true);
    update = -1L;
  }

  /**
   * Blank constructor.
   *
   * @param width The width of the image in pixels.
   * @param height The height of the image in pixels.
   */
  public MapImage(int width, int height) {
    setWidth(width);
    setHeight(height);
    this.bytes = new byte[width * height];
    fillRect(0, 0, width, height, Color.WHITE);
    update = -1L;
  }

  /**
   * Blank color constructor.
   *
   * @param width The width of the image in pixels.
   * @param height The height of the image in pixels.
   * @param color The color to draw on the initial image.
   */
  public MapImage(int width, int height, @NotNull Color color) {
    setWidth(width);
    setHeight(height);
    this.bytes = new byte[width * height];
    // Create a blank image.
    fillRect(0, 0, width, height, color);
    setDirty(true);
    update = -1L;
  }

  /**
   * Draws a BufferedImage.
   *
   * @param image The image to draw.
   * @param offsetX The starting x offset of the MapImage to draw.
   * @param offsetY The starting y offset of the MapImage to draw.
   * @throws NullPointerException Thrown if the image given is null.
   * @throws IllegalArgumentException Thrown if the image is invalid.
   */
  public void draw(@NotNull BufferedImage image, int offsetX, int offsetY) {
    draw(image, offsetX, offsetY, AnchorFlag.TOP_LEFT);
  }

  public void draw(BufferedImage image, int offsetX, int offsetY, @NotNull AnchorFlag anchor) {
    checkImmutable();
    int anchorX = 0;
    int anchorY = 0;
    switch (anchor) {
      case TOP_LEFT:
        break;
      case TOP_RIGHT:
        anchorX = width - image.getWidth();
        break;
      case BOTTOM_RIGHT:
        anchorY = height - image.getHeight();
        break;
      case BOTTOM_LEFT:
        anchorX = width - image.getWidth();
        anchorY = height - image.getHeight();
        break;
      case CENTER:
        anchorX = (width / 2) - (image.getWidth() / 2);
        anchorY = (height / 2) - (image.getHeight() / 2);
        break;
    }
    int width = image.getWidth();
    int height = image.getHeight();
    if (width == 0 || height == 0) {
      throw new IllegalArgumentException(
          "Image is invalid. (width=" + width + ", height=" + height + ")");
    }
    int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int dx = anchorX + offsetX + x;
        int dy = anchorY + offsetY + y;
        if (dx >= 0 && dx < this.width && dy >= 0 && dy < this.height) {
          int offset = getOffset(dx, dy);
          if (offset < bytes.length) {
            this.bytes[offset] = MapUtils.matchColor(pixels[getOffset(x, y, width)]);
          }
        }
      }
    }
    setDirty(true);
  }

  public void fillRect(int offsetX, int offsetY, int width, int height, @NotNull Color color) {
    fillRect(offsetX, offsetY, width, height, MapUtils.getColor(color));
  }

  public void fillRect(int offsetX, int offsetY, int width, int height, byte color) {
    checkImmutable();
    for (int y = offsetY; y < offsetY + height; y++) {
      // Prevent overflow errors.
      if (y >= this.height) break;
      for (int x = offsetX; x < offsetX + width; x++) {
        // Prevent overflow errors.
        if (x >= this.width) break;
        bytes[getOffset(x, y)] = color;
      }
    }
    this.dirty = true;
  }

  public void draw(@NotNull MapImage image, int offsetX, int offsetY) {
    checkImmutable();
    int w = getWidth();
    int h = getHeight();
    int imageWidth = image.getWidth();
    int imageHeight = image.getHeight();

    for (int y = 0; y < imageHeight; y++) {
      int ly = y + offsetY;
      if (ly < 0) {
        continue;
      } else if (ly > h - 1) {
        break;
      }
      for (int x = 0; x < imageWidth; x++) {
        int lx = x + offsetX;
        if (lx < 0) {
          continue;
        } else if (lx > w - 1) {
          break;
        }
        setColor(lx, ly, image.getColor(x, y));
      }
    }
    setDirty(true);
  }

  private void checkImmutable() {
    if (isImmutable()) {
      throw new IllegalStateException("The MapImage is immutable and cannot be altered.");
    }
  }

  /**
   * @param x The x coordinate.
   * @param y The y coordinate.
   * @return Returns the map color for the given pixel coordinates.
   * @throws IllegalArgumentException Thrown if the x or y coordinates are negative or greater than
   *     the last index of the array. (greater than the width and height)
   */
  public byte getColor(int x, int y) {
    // Check to make sure the X value is valid.
    if (x < 0) {
      throw new IllegalArgumentException("X cannot be negative: " + x);
    } else if (x > getWidth() - 1) {
      throw new IllegalArgumentException(
          "X cannot be greater than image width (" + getWidth() + "): " + x);
    }
    // Check to make sure the Y value is valid.
    if (y < 0) {
      throw new IllegalArgumentException("Y cannot be negative: " + y);
    } else if (y > getHeight() - 1) {
      throw new IllegalArgumentException(
          "Y cannot be greater than image height (" + getHeight() + "): " + y);
    }
    // Return the stored color.
    int offset = getOffset(x, y);
    try {
      return bytes[offset];
    } catch (Exception e) {
      System.out.println(
          "Error: getColor("
              + x
              + ", "
              + y
              + ") (width: "
              + width
              + ", height: "
              + height
              + ") (offset: "
              + offset
              + ", length: "
              + bytes.length
              + ")");
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * Sets the map color for the given pixel coordinates.
   *
   * @param x The x coordinate.
   * @param y The y coordinate.
   * @throws IllegalArgumentException Thrown if the x or y coordinates are negative or greater than
   *     the last index of the array. (greater than the width and height)
   */
  private void setColor(int x, int y, byte color) {
    checkImmutable();
    // Check to make sure the X value is valid.
    if (x < 0) {
      throw new IllegalArgumentException("X cannot be negative: " + x);
    } else if (x >= getWidth()) {
      throw new IllegalArgumentException(
          "X cannot be greater than image width (" + getWidth() + "): " + x);
    }
    // Check to make sure the Y value is valid.
    if (y < 0) {
      throw new IllegalArgumentException("Y cannot be negative: " + y);
    } else if (y >= getWidth()) {
      throw new IllegalArgumentException(
          "Y cannot be greater than image height (" + getHeight() + "): " + y);
    }
    bytes[getOffset(x, y)] = color;
    setDirty(true);
  }

  /**
   * Sets the map color for the given pixel coordinates.
   *
   * @param pixel The [x,y] coordinates for the pixel.
   * @param color The color to set.
   */
  public void setColor(@NotNull int[] pixel, byte color) {
    if (pixel.length != 2) {
      throw new IllegalArgumentException("The pixel given is not valid. length: " + pixel.length);
    }
    setColor(pixel[0], pixel[1], color);
    setDirty(true);
  }

  public PacketPlayOutMap createPacket(short index) {
    if (isPacketCreated()) {
      throw new IllegalStateException("Packet is already created for MapImage.");
    }
    byte b = (byte) 0;
    boolean c = true;
    packet = new PacketPlayOutMap(index, b, c, new ArrayList<>(), bytes, 0, 0, 128, 128);
    PacketUtils.setRawByteArrayForMapPacket(packet, bytes);
    SmartBoardThread.instance.addPacket(packet);
    return packet;
  }

  public void dispose() {
    SmartBoardThread.instance.removePacket(packet);
    packet = null;
  }

  /**
   * Sends the map image as a map to a player with the map ID.
   *
   * @param player The player to send the packet to.
   * @param index The index of the map.
   * @throws NullPointerException Thrown if the player given is null.
   * @throws IllegalDimensionsException Thrown if the map image to send is not 128x128.
   */
  public void sendAsMiniMap(@NotNull Player player, short index) {
    if (width != 128 || height != 128) {
      throw new IllegalDimensionsException(width, height, 128, 128);
    }
    try {
      PacketPlayOutMap packet;
      byte[] bytes = getBytes();
      byte b = (byte) 0;
      boolean c = true;
      packet = new PacketPlayOutMap(index, b, c, new ArrayList<>(), bytes, 0, 0, 128, 128);
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    } catch (Exception e) {
      System.err.println("Failed to send packet for MapView(" + index + ")");
      e.printStackTrace();
    }
  }

  /**
   * Sends the map image as a map to a player with the map ID.
   *
   * @param players The players to send the packet to.
   * @param index The index of the map.
   * @throws NullPointerException Thrown if the players array given is null.
   * @throws IllegalDimensionsException Thrown if the map image to send is not 128x128.
   * @throws IllegalArgumentException Thrown if the players array is empty.
   */
  public void sendAsMiniMap(@NotNull Player[] players, short index) {
    if (players.length == 0) {
      throw new IllegalArgumentException("No players were provided to send the map packet.");
    }
    if (width != 128 || height != 128) {
      throw new IllegalDimensionsException(width, height, 128, 128);
    }
    try {
      PacketPlayOutMap packet;
      byte[] bytes = getBytes();
      byte b = (byte) 0;
      boolean c = true;
      packet = new PacketPlayOutMap(index, b, c, new ArrayList<>(), bytes, 0, 0, 128, 128);
      for (Player player : players) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
      }
    } catch (Exception e) {
      System.err.println("Failed to send packet for MapView(" + index + ")");
      e.printStackTrace();
    }
  }

  @NotNull
  public BufferedImage toBufferedImage() {
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    int size = width * height;
    int[] pixels = new int[size];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int offset = getOffset(x, y);
        byte color = bytes[offset];
        pixels[offset] = MapUtils.getRGB(color);
      }
    }
    bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
    return bufferedImage;
  }

  /**
   * @param u The u-coordinate to approximate.
   * @param v The v-coordinate to approximate.
   * @param color The color to set.
   */
  private void setColor(double u, double v, byte color) {
    setColor(toPixel(u, v), color);
    setDirty(true);
  }

  /**
   * Sets the raster of the image.
   *
   * @param bytes The pixel data array.
   * @param width The width of the pixel data array.
   * @param height The height of the pixel data array.
   */
  public void setRaster(@NotNull byte[] bytes, int width, int height) {
    checkImmutable();
    System.arraycopy(bytes, 0, this.bytes, 0, this.bytes.length);
    // Set the dimensions too.
    setWidth(width);
    setHeight(height);
    setDirty(true);
  }

  /**
   * Clears the image.
   *
   * @param color The map color.
   */
  private void clear(byte color) {
    checkImmutable();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        bytes[getOffset(x, y)] = color;
      }
    }
    setDirty(true);
  }

  /**
   * @param u The u-coordinate to approximate.
   * @param v The v-coordinate to approximate.
   * @return Returns the approximate color of the UV coordinate.
   */
  public byte getColorApproximate(double u, double v) {
    int[] pixel = toPixel(u, v);
    return getColor(pixel);
  }

  /**
   * @param u The u-coordinate to approximate.
   * @param v The v-coordinate to approximate.
   * @return Returns the pixel that is closest the to UV-coordinates.
   */
  public int[] toPixel(double u, double v) {
    return toPixel(u, v, getWidth(), getHeight());
  }

  /**
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   * @return Returns the mini-map UV-coordinates for the image.
   */
  double[] toUV(int x, int y) {
    return toUV(x, y, getWidth(), getHeight());
  }

  private int getOffset(int x, int y) {
    return getOffset(x, y, width);
  }

  /**
   * @param pixel The int[x,y] pixel with the x-coordinate, and the y-coordinate.
   * @return returns the map color for the given pixel coordinates.
   */
  public byte getColor(int[] pixel) {
    return getColor(pixel[0], pixel[1]);
  }

  /** Clears the image. (White) */
  private void clear() {
    clear(MapUtils.WHITE);
  }

  /** @return Returns the width in pixels of the mini-map image. */
  public int getWidth() {
    return this.width;
  }

  /**
   * Sets the width in pixels of the mini-map image.
   *
   * @param width The width to set.
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /** @return Returns the height in pixels of the mini-map image. */
  public int getHeight() {
    return this.height;
  }

  /**
   * Sets the height in pixels of the mini-map image.
   *
   * @param height The height to set.
   */
  public void setHeight(int height) {
    this.height = height;
  }

  public boolean isPacketCreated() {
    return this.packet != null;
  }

  public boolean isDirty() {
    return this.dirty;
  }

  public void setDirty(boolean flag) {
    this.dirty = flag;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void setImmutable() {
    this.immutable = true;
  }

  public boolean isImmutable() {
    return this.immutable;
  }

  /**
   * @param value The value to normalize.
   * @return Returns a double value between 0 and 1.
   */
  public static double normalize(double value) {
    if (value < 0) {
      while (value < 0) {
        value++;
      }
    } else if (value > 1) {
      while (value > 1) {
        value--;
      }
    }
    return value;
  }

  /**
   * @param u The u-coordinate.
   * @param v The v-coordinate.
   * @param width The width of the image.
   * @param height The height of the image.
   * @return Returns the pixel that is closest the to UV-coordinates.
   */
  public static int[] toPixel(double u, double v, double width, double height) {
    u = normalize(u);
    v = normalize(v);
    int px = (int) Math.floor(u * width);
    int py = (int) Math.floor(v * height);
    return new int[] {px, py};
  }

  /**
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   * @param width The width of the image.
   * @param height The height of the image.
   * @return Returns the mini-map UV-coordinates for the image.
   */
  public static double[] toUV(int x, int y, int width, int height) {
    double rx = Math.floor((double) x / (double) width);
    double ry = Math.floor((double) y / (double) height);
    return new double[] {rx, ry};
  }

  private static int getOffset(int x, int y, int width) {
    return (y * width) + x;
  }

  public PacketPlayOutMap getPacket() {
    return this.packet;
  }

  public int getPacketId() {
    return PacketUtils.getMapId(getPacket());
  }

  /**
   * Checks if the player has the most up-to-date packet data for the MapImage.
   *
   * <p>NOTE: Override this method if you want to use packets that are not shared globally for
   * players. If the original method is used, the method will check the <code>
   * PluginSmartBoards.mapMapPacketTimes</code> HashMap, which is shared with other default
   * MapImages.
   *
   * @param player The player to test.
   * @return Returns true if the up-to-date packet for the map has already been sent to the player.
   */
  public boolean isSent(@NotNull Player player) {
    Map<Integer, Long> mapTimes = PluginSmartBoards.mapMapPacketTimes.get(player.getUniqueId());
    if (mapTimes != null) {
      int id = getPacketId();
      Long time = mapTimes.get(id);
      // If a time has been registered, and the time is greater than the update time (or equal),
      // then the player has the most up-to-date data for the MapImage.
      if (time != null && time > getUpdateTime()) {
        return true;
      }
    }
    // Return false to state that the packet needs to be sent to the player.
    return false;
  }

  /**
   * Sends the stored packet for the MapImage to the player, logging the time sent to the plugin.
   *
   * <p>NOTE: Override this method if you want to use packets that are not shared globally for *
   * players. If the original method is used, the method will store the time sent to the <code>
   *    * PluginSmartBoards.mapMapPacketTimes</code> HashMap, which is shared with other default *
   * MapImages.
   *
   * @param player The player to send the packet to.
   */
  public void send(@NotNull Player player) {
    UUID playerId = player.getUniqueId();
    int packetId = getPacketId();
    // Make sure to let the plug-in know that the packet is sent at the current time.
    Map<Integer, Long> mapTimes =
        PluginSmartBoards.mapMapPacketTimes.computeIfAbsent(playerId, k -> new HashMap<>());
    mapTimes.put(packetId, System.currentTimeMillis());
    // Send the packet.
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(getPacket());
  }

  private long getUpdateTime() {
    return this.update;
  }

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

  public static MapImage[] getStaticImages() {
    return STATIC;
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
}
