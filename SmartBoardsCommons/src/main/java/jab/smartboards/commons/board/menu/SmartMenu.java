package jab.smartboards.commons.board.menu;

import jab.smartboards.commons.board.graphics.BoardGraphics;
import jab.smartboards.commons.board.graphics.SimpleBoardGraphics;
import jab.smartboards.commons.board.menu.adapter.AdaptableObject;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public abstract class SmartMenu extends MenuElement {

  private BoardGraphics graphics;

  /**
   * Main constructor.
   *
   * @param width The width of the menu. (In blocks)
   * @param height The height of the menu. (In blocks)
   */
  public SmartMenu(int width, int height) {
    super(0, 0, width * 128, height * 128);
    this.graphics = new SimpleBoardGraphics(width, height);
  }

  /** @return Returns the graphics object used to render the menu. */
  public BoardGraphics getGraphics() {
    return this.graphics;
  }

  /**
   * Sets the graphics object used to render the menu.
   *
   * @param graphics The graphics object to set.
   */
  public void setGraphics(BoardGraphics graphics) {
    this.graphics = graphics;
  }

  @Override
  public void render(AdaptableObject parent, BoardGraphics graphics) {
    if (this.graphics == null) {
      return;
    }
    super.render(parent, this.graphics);
  }
}
