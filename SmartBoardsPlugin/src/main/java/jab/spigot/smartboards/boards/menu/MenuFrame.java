package jab.spigot.smartboards.boards.menu;

import jab.smartboards.commons.board.graphics.BoardFrame;
import jab.smartboards.commons.board.graphics.BoardGraphics;
import jab.smartboards.commons.board.menu.MenuElement;
import jab.smartboards.commons.board.menu.adapter.AdaptableObject;
import jab.smartboards.commons.events.MenuClickEvent;
import jab.smartboards.commons.utils.BoardSizable;
import jab.smartboards.commons.utils.MapImage;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class MenuFrame extends MenuElement implements BoardSizable {

  private final FrameType type;
  private final MapImage image;
  private final int width;
  private final int height;

  /**
   * Main constructor.
   *
   * @param type The type of frame to render.
   * @param width The width of the frame. (In blocks)
   * @param height The height of the frame. (In blocks)
   */
  public MenuFrame(@NotNull FrameType type, int width, int height) {
    super(width * 128, height * 128);
    this.type = type;
    this.width = width;
    this.height = height;
    this.image = create();
  }

  /** @return Returns a MapImage of the frame. */
  @NotNull
  public MapImage create() {
    return type.generateFrame(width * 128, height * 128);
  }

  /** @return Returns the type of frame. */
  @NotNull
  public FrameType getType() {
    return this.type;
  }

  /** @return Returns the width of the frame. (In blocks) */
  @Override
  public int getBlockWidth() {
    return this.width;
  }

  /** @return Returns the height of the frame. (In blocks) */
  @Override
  public int getBlockHeight() {
    return this.height;
  }

  @Override
  public void onClick(AdaptableObject parent, MenuClickEvent event) {
    throw new IllegalStateException("MenuFrames are not clickable.");
  }

  @Override
  public void onUpdate(AdaptableObject parent) {}

  @Override
  public void onRender(AdaptableObject parent, BoardGraphics graphics) {
    if (graphics.getBlockWidth() != getWidth() && graphics.getBlockHeight() != getHeight()) {
      throw new IllegalStateException("");
    }
    BoardFrame frame = graphics.getFrame();
    if (frame == null) {
      return;
    }
    // Draw the frame image onto the board.
    frame.draw(image);
  }

  @Override
  public boolean isClickable() {
    return false;
  }
}
