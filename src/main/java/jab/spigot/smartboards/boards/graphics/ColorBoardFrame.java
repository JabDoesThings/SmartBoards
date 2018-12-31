package jab.spigot.smartboards.boards.graphics;

import jab.spigot.smartboards.utils.MapImage;
import jab.spigot.smartboards.utils.MapImageUtils;

public class ColorBoardFrame extends BoardFrame {

  /**
   * @param width The width of the colored board frame.
   * @param height The height of the colored board frame.
   */
  public ColorBoardFrame(int width, int height) {
    super(width, height);
    create(MapImageUtils.WHITE);
  }

  public ColorBoardFrame(int width, int height, MapImage color) {
    super(width, height);
    create(color);
  }

  public void create(MapImage color) {
    for (int index = 0; index < getSize(); index++) {
      frames[index] = color;
    }
  }
}
