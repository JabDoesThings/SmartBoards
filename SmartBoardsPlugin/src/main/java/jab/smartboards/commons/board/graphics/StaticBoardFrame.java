package jab.smartboards.commons.board.graphics;

import jab.smartboards.commons.utils.MapImage;

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
    super(width, height, true);
    create();
  }

  private void create() {
    // Grab a randomly generated static MapImage and apply it to each slot.
    for (int index = 0; index < getSize(); index++) {
      frames[index] = MapImage.getStaticImage();
    }
  }
}
