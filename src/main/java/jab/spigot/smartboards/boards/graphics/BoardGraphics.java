package jab.spigot.smartboards.boards.graphics;

/**
 * This class is designed to handle all SmartBoard graphics operations, including updating and
 * drawing onto graphics frames.
 *
 * @author Josh
 */
public abstract class BoardGraphics {

  private final int width;
  private final int height;

  private boolean dirty = false;

  /**
   * Main constructor.
   *
   * @param width The width of the board graphics. (in blocks)
   * @param height The height of the board graphics. (in blocks)
   */
  public BoardGraphics(int width, int height) {
    this.width = width;
    this.height = height;
  }

  /**
   * Executed when the board graphics updates. This includes logical operations as well as any
   * raster operations supplemented by sub-class implementations.
   */
  public void update() {}

  /** @return Returns true if the board graphics is marked dirty. */
  public boolean isDirty() {
    return this.dirty;
  }

  /**
   * Sets the board graphics dirty.
   *
   * @param flag The flag to set.
   */
  public void setDirty(boolean flag) {
    this.dirty = flag;
  }

  /** @return Returns the width of the board graphics. (in blocks) */
  public int getWidth() {
    return this.width;
  }

  /** @return Returns the height of the board graphics. (in blocks) */
  public int getHeight() {
    return this.height;
  }

  /** @return Returns true if the board is dirty, or needs to be updated for other reasons. */
  public boolean canUpdate() {
    return false;
  }

  /** @return Returns the result frame. (or current frame) */
  public abstract BoardFrame getFrame();
}
