package jab.spigot.smartboards.boards.graphics;

public class StaticBoardGraphics extends SequencedBoardGraphics {

  /**
   * Main constructor.
   *
   * @param width The width of the board graphics. (in blocks)
   * @param height The height of the board graphics. (in blocks)
   */
  public StaticBoardGraphics(int width, int height) {
    super(width, height);
    create();
  }

  private void create() {
    for (int index = 0; index < 3; index++) {
      addFrame(new StaticBoardFrame(getWidth(), getHeight()), 2);
    }
  }
}
