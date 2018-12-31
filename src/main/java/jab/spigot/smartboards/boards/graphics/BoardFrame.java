package jab.spigot.smartboards.boards.graphics;

import jab.spigot.smartboards.utils.MapImage;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles rendered MapImages for BoardGraphics instances.
 *
 * @author Josh
 */
public class BoardFrame {

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
    this(width, height);
    create(startingMapIndex);
  }

  /**
   * Sub-class constructor.
   *
   * @param width The frame width. (in blocks)
   * @param height The frame height. (in blocks)
   */
  public BoardFrame(int width, int height) {
    this.width = width;
    this.height = height;
    this.size = width * height;
    this.frames = new MapImage[size];
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

  /** @return Returns all the mini-map frames making up the board frame. */
  @NotNull
  public MapImage[] getFrames() {
    return this.frames;
  }

  /** @return Returns the width of the board frame. (in blocks) */
  public int getWidth() {
    return this.width;
  }

  /** @return Returns the height of the board frame. (in blocks) */
  public int getHeight() {
    return this.height;
  }

  /** @return Returns the amount of mini-map frames used for the board frame. */
  public int getSize() {
    return this.size;
  }
}
