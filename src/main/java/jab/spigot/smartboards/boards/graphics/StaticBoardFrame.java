package jab.spigot.smartboards.boards.graphics;

import jab.spigot.smartboards.utils.MapImageUtils;

/**
 * This BoardFrame creates a frame based on randomly selected static MapImages.
 *
 * @author Josh
 */
public class StaticBoardFrame extends BoardFrame {

  /**
   * Main constructor.
   *
   * @param width The width of the board. (in blocks)
   * @param height The height of the board. (in blocks)
   */
  public StaticBoardFrame(int width, int height) {
    super(width, height);
    create();
  }

  private void create() {
    for (int index = 0; index < getSize(); index++) {
      frames[index] = MapImageUtils.getStaticImage();
    }
  }
}
