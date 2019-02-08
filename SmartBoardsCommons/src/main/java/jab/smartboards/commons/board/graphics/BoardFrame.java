package jab.smartboards.commons.board.graphics;

import jab.smartboards.commons.SmartBoards;
import jab.smartboards.commons.utils.BoardSizable;
import jab.smartboards.commons.utils.MapImage;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class handles rendered MapImages for BoardGraphics instances.
 *
 * @author Josh
 */
public class BoardFrame implements BoardSizable {

  protected final MapImage[] frames;
  private final int width;
  private final int height;
  private final int size;

  /**
   * Main constructor.
   *
   * @param width The frame width. (in blocks)
   * @param height The frame height. (in blocks)
   * @param startingMapIndex The mini-map index to increment for each mini-map.
   */
  public BoardFrame(int width, int height, int startingMapIndex) {
    this(width, height, false);
    create(startingMapIndex);
  }

  /**
   * Sub-class constructor.
   *
   * @param width The frame width. (in blocks)
   * @param height The frame height. (in blocks)
   * @param generateMapIndexes Set to true to generate map indexes. Set to false to manually assign
   *     them.<br>
   *     NOTE: This constructor does not invoke '<code>create(int startingMapIndex)</code> if this
   *     paramater is set to false. This method must be invoked to use the frame.
   */
  public BoardFrame(int width, int height, boolean generateMapIndexes) {
    this.width = width;
    this.height = height;
    this.size = width * height;
    this.frames = new MapImage[size];
    if (generateMapIndexes) {
      create(SmartBoards.generateMapIndex(size));
    }
  }

  public BoardFrame(@NotNull BufferedImage image) {
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
    width = bWidth;
    height = bHeight;
    this.size = width * height;
    this.frames = new MapImage[size];
    int startingMapIndex = SmartBoards.generateMapIndex(size);
    System.out.println(
        "width: "
            + width
            + " height: "
            + height
            + " size: "
            + size
            + " starting: "
            + startingMapIndex);
    create(startingMapIndex);
    draw(image);
  }

  /**
   * Creates a set of MapImages.
   *
   * @param startingMapIndex The starting mini-map index to assign.
   */
  protected void create(int startingMapIndex) {
    for (int index = 0; index < size; index++) {
      frames[index] = new MapImage(128, 128);
      frames[index].createPacket((short) startingMapIndex++);
    }
  }

  public void draw(@NotNull MapImage image) {
    int imageWidth = image.getWidth();
    int imageHeight = image.getHeight();
    for (int y = 0; y < height; y++) {
      int offsetY = -imageHeight + ((y + 1) * 128);
      for (int x = 0; x < width; x++) {
        int offsetX = x * -128;
        frames[getIndex(x, y)].draw(image, offsetX, offsetY);
      }
    }
  }

  public void draw(@NotNull BufferedImage image) {
    int imageHeight = image.getHeight();
    for (int y = 0; y < height; y++) {
      int offsetY = -imageHeight + ((y + 1) * 128);
      for (int x = 0; x < width; x++) {
        int offsetX = x * -128;
        frames[getIndex(x, y)].draw(image, offsetX, offsetY);
      }
    }
  }

  /** @return Returns all the mini-map frames making up the board frame. */
  @NotNull
  public MapImage[] getFrames() {
    return this.frames;
  }

  /** @return Returns the width of the board frame. (in blocks) */
  public int getBlockWidth() {
    return this.width;
  }

  /** @return Returns the height of the board frame. (in blocks) */
  public int getBlockHeight() {
    return this.height;
  }

  /** @return Returns the amount of mini-map frames used for the board frame. */
  public int getSize() {
    return this.size;
  }

  /**
   * This is the formula for one-dimensional arrays composed of unique indexes of 2-dimensional
   * coordinates: <br>
   * index = (y + width) + (width - 1) + x
   *
   * @param x The x coordinate relative to the board's left side.
   * @param y The y coordinate relative to the board's top side.
   * @return Returns the array index for the given coordinates.
   */
  int getIndex(int x, int y) {
    return getIndex(x, y, getBlockWidth());
  }

  /**
   * This is the formula for one-dimensional arrays composed of unique indexes of 2-dimensional
   * coordinates: <br>
   * index = (y + width) + (width - 1) + x
   *
   * @param x The x coordinate relative to the board's left side.
   * @param y The y coordinate relative to the board's top side.
   * @param width The width of the board.
   * @return Returns the array index for the given coordinates.
   */
  static int getIndex(int x, int y, int width) {
    return (y * width) + (width - 1) - x;
  }

  /** @return Returns a BufferedImage of the frame. */
  @NotNull
  public BufferedImage toBufferedImage() {
    int iw = getBlockWidth() * 128;
    int ih = getBlockHeight() * 128;
    BufferedImage image = new BufferedImage(iw, ih, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g = (Graphics2D) image.getGraphics();
    g.clearRect(0, 0, iw, ih);
    // Draw all frames.
    for (int y = 0; y < getBlockHeight(); y++) {
      for (int x = 0; x < getBlockWidth(); x++) {
        g.drawImage(getFrame(x, y).toBufferedImage(), x * 128, y * 128, null);
      }
    }
    g.dispose();
    return image;
  }

  /**
   * @param x The x coordinate on the frame.
   * @param y The y coordinate on the frame.
   * @return Returns the MapImage at the specified coordinate on the frame.
   */
  @NotNull
  private MapImage getFrame(int x, int y) {
    if (x < 0 || x > width) {
      throw new IndexOutOfBoundsException("Invalid X coordinate: " + x);
    }
    if (y < 0 || y > height) {
      throw new IndexOutOfBoundsException("Invalid Y coordinate: " + y);
    }
    return frames[getIndex(x, y)];
  }

  public void fillRect(int x, int y, int width, int height, byte color) {
    for (int iy = y; iy < y + height; iy++) {
      for (int ix = x; ix < x + width; ix++) {}
    }
  }

  public MapImage getFrameByPixel(double u, double v) {
    int fx = (int) Math.floor(u * width);
    int fy = (int) Math.floor((1.0 - v) * height);
    //    -imageHeight + ((y + 1) * 128);
    return frames[getIndex(fx, fy)];
  }
}
