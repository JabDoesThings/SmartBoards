package jab.spigot.smartboards.boards.graphics;

import jab.spigot.smartboards.utils.MapImageUtils;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles sequenced frames for board graphics.
 *
 * @author Josh
 */
public class SequencedBoardGraphics extends BoardGraphics {

  private final ColorBoardFrame DEFAULT_FRAME;

  final Object lock = new Object();

  private BoardFrame[] frames;
  private int[] frameTimes;
  private int index;
  private int time;

  /**
   * Main constructor.
   *
   * @param width The width of the board graphics. (in blocks)
   * @param height The height of the board graphics. (in blocks)
   */
  public SequencedBoardGraphics(int width, int height) {
    super(width, height);
    DEFAULT_FRAME = new ColorBoardFrame(width, height, MapImageUtils.WHITE);
    frames = new BoardFrame[0];
    frameTimes = new int[0];
  }

  @Override
  public void update() {
    synchronized (lock) {
      if (time == 0) {
        index++;
        if (index == frames.length) index = 0;
        time = frameTimes[index];
        setDirty(true);
      } else {
        time--;
      }
    }
  }

  @Override
  public boolean canUpdate() {
    return true;
  }

  public void addFrame(@NotNull BoardFrame frame, int time) {
    synchronized (lock) {
      // Add the frame.
      BoardFrame[] framesNew = new BoardFrame[frames.length + 1];
      System.arraycopy(frames, 0, framesNew, 0, frames.length);
      framesNew[frames.length] = frame;
      frames = framesNew;
      // Add the time.
      int[] frameTimesNew = new int[frameTimes.length + 1];
      System.arraycopy(frameTimes, 0, frameTimesNew, 0, frameTimes.length);
      frameTimesNew[frameTimes.length] = time;
      frameTimes = frameTimesNew;
    }
  }

  @Override
  public BoardFrame getFrame() {
    return frames.length != 0 ? frames[index] : DEFAULT_FRAME;
  }
}
