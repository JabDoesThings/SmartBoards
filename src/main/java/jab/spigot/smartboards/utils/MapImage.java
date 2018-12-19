package jab.spigot.smartboards.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import jab.spigot.smartboards.enums.MapAnchor;
import jab.spigot.smartboards.enums.MapScale;
import jab.spigot.smartboards.exceptions.IllegalDimensionsException;
import net.minecraft.server.v1_13_R2.PacketPlayOutMap;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles storing and caching images for Bukkit maps.
 *
 * <p>TODO: Document & Implement scaled transformations.
 *
 * @author Josh
 */
public class MapImage {

  /** The image source to modify. */
  private BufferedImage image;
  /** The stored raster of map colors for the image. */
  private byte[] bytes;
  /** The width in pixels of the image. */
  private int width;
  /** The height in pixels of the image. */
  private int height;
  /** Flag to mark the image as dirty. */
  private boolean dirty;

  /**
   * Image constructor.
   *
   * @param image The BufferedImage to render.
   */
  public MapImage(@NotNull BufferedImage image) {
    setWidth(image.getWidth());
    setHeight(image.getHeight());
    setImage(image);
    setDirty(true);
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
    // Create a blank image.
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g = (Graphics2D) image.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);
    g.dispose();
    setImage(image);
    setDirty(true);
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
    // Create a blank image.
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g = (Graphics2D) image.getGraphics();
    g.setColor(color);
    g.fillRect(0, 0, width, height);
    g.dispose();
    setImage(image);
    setDirty(true);
  }

  public void draw(@NotNull MapImage image) {
    draw(image, 0, 0, 1, MapAnchor.TOP_LEFT, MapScale.ABSOLUTE);
  }

  public void draw(@NotNull MapImage image, int x, int y) {
    draw(image, x, y, 1, MapAnchor.TOP_LEFT, MapScale.ABSOLUTE);
  }

  public void draw(@NotNull MapImage image, int x, int y, double scale) {
    draw(image, x, y, scale, MapAnchor.TOP_LEFT, MapScale.ABSOLUTE);
  }

  public void draw(
      @NotNull MapImage image, int x, int y, double scale, @NotNull MapAnchor mapAnchor) {
    draw(image, x, y, scale, mapAnchor, MapScale.ABSOLUTE);
  }

  public void draw(
      @NotNull MapImage image,
      int offsetX,
      int offsetY,
      double scale,
      @NotNull MapAnchor mapAnchor,
      @NotNull MapScale mapScale) {
    int w = getWidth();
    int h = getHeight();
    int iw = image.getWidth();
    int ih = image.getHeight();
    for (int y = 0; y < ih; y++) {
      int ly = y + offsetY;
      if (ly < 0) {
        continue;
      } else if (ly > h - 1) {
        break;
      }
      for (int x = 0; x < iw; x++) {
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

  /**
   * Sets the source of the image.
   *
   * @param image The BufferedImage to render on the map image.
   */
  public void setImage(@NotNull BufferedImage image) {
    this.image = image;
    if (bytes == null) {
      bytes = new byte[width * height];
    }
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        bytes[getOffset(x, y)] = MapUtils.matchColor(new Color(image.getRGB(x, y), true));
      }
    }
    setDirty(true);
  }

  /**
   * Clears the image.
   *
   * @param color The map color.
   */
  private void clear(byte color) {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        bytes[getOffset(x, y)] = color;
      }
    }
    setDirty(true);
  }

  /**
   * @param pixel The int[x,y] pixel with the x-coordinate, and the y-coordinate.
   * @return returns the map color for the given pixel coordinates.
   */
  public byte getColor(int[] pixel) {
    return getColor(pixel[0], pixel[1]);
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
    return bytes[getOffset(x, y)];
  }

  private int getOffset(int x, int y) {
    return (y * width) + x;
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
    // Set the raster.
    this.bytes = bytes;
    // Set the dimensions too.
    setWidth(width);
    setHeight(height);
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

  public boolean isDirty() {
    return this.dirty;
  }

  public void setDirty(boolean flag) {
    this.dirty = flag;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public BufferedImage getImage() {
    return this.image;
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
}
