package jab.spigot.smartboards.editor;

public class SmartBoardFrame {

  private int blockWidth = 1;
  private int blockHeight = 1;

  public SmartBoardFrame() {}

  /** @return Returns the width of the frame. (In blocks) */
  public int getBlockWidth() {
    return this.blockWidth;
  }

  /**
   * Sets the the width of the frame. (In blocks)
   *
   * @param blockWidth The width to set.
   */
  public void setBlockWidth(int blockWidth) {
    this.blockWidth = blockWidth;
  }

  /** @return Returns the height of the frame. (In blocks) */
  public int getBlockHeight() {
    return this.blockHeight;
  }

  /**
   * Sets the height of the frame. (In blocks)
   *
   * @param blockHeight The height to set.
   */
  public void setBlockHeight(int blockHeight) {
    this.blockHeight = blockHeight;
  }
}
