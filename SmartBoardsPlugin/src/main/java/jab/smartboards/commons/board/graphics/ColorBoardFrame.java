package jab.smartboards.commons.board.graphics;

import jab.smartboards.commons.utils.MapImage;
import org.jetbrains.annotations.NotNull;

/**
 * ColorBoardFrames are for rendering simplistic colored frames for BoardGraphics objects.
 *
 * @author Josh
 */
public class ColorBoardFrame extends BoardFrame {

  /**
   * Basic constructor. Uses the color 'WHITE'.
   *
   * @param width The width of the frame. (In blocks)
   * @param height The height of the frame. (In blocks)
   */
  public ColorBoardFrame(int width, int height) {
    super(width, height, true);
    create(MapImage.WHITE);
  }

  /**
   * Full constructor.
   *
   * @param width The width of the frame. (In blocks)
   * @param height The height of the frame. (In blocks)
   * @param color The color to apply.
   */
  public ColorBoardFrame(int width, int height, MapImage color) {
    super(width, height, true);
    create(color);
  }

  /**
   * Creates the frame using a color.
   *
   * @param color The color to apply to the frame.
   */
  public void create(@NotNull MapImage color) {
    for (int index = 0; index < getSize(); index++) {
      frames[index] = color;
    }
  }
}
