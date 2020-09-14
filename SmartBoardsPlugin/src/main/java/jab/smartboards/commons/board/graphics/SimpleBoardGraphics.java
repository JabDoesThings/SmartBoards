package jab.smartboards.commons.board.graphics;

import jab.smartboards.commons.utils.MapImage;
import org.jetbrains.annotations.Nullable;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class SimpleBoardGraphics extends BoardGraphics {

  private final BoardFrame DEFAULT_FRAME;

  private BoardFrame boardFrame;

  /**
   * Main constructor.
   *
   * @param width The width of the board graphics. (in blocks)
   * @param height The height of the board graphics. (in blocks)
   */
  public SimpleBoardGraphics(int width, int height) {
    super(width, height);
    DEFAULT_FRAME = new ColorBoardFrame(width, height, MapImage.WHITE);
    boardFrame = DEFAULT_FRAME;
  }

  /** @return Returns the set frame. */
  @Override
  public BoardFrame getFrame() {
    return this.boardFrame;
  }

  /**
   * Sets the current frame for the board graphics.
   *
   * @param boardFrame The frame to set.
   */
  public void setFrame(@Nullable BoardFrame boardFrame) {
    if (boardFrame != null) {
      this.boardFrame = boardFrame;
    } else {
      this.boardFrame = DEFAULT_FRAME;
    }
  }
}
