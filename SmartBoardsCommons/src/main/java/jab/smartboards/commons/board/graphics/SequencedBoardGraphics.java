package jab.smartboards.commons.board.graphics;

import jab.smartboards.commons.utils.MapImage;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

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
    DEFAULT_FRAME = new ColorBoardFrame(width, height, MapImage.BLACK);
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
    if (frame.getBlockWidth() != getBlockWidth()) {
      throw new IllegalArgumentException(
          "Width of the frame does not match the graphics object: (frame: "
              + frame.getBlockWidth()
              + " graphics: "
              + getBlockWidth()
              + ")");
    }
    if (frame.getBlockHeight() != getBlockHeight()) {
      throw new IllegalArgumentException(
          "Height of the frame does not match the graphics object: (frame: "
              + frame.getBlockHeight()
              + " graphics: "
              + getBlockHeight()
              + ")");
    }
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

  public void addImageAsFrame(@NotNull BufferedImage frame, int time) {
    addFrame(new BoardFrame(frame), time);
  }

  public void addImagesAsFrames(@NotNull BufferedImage[] frames, int time) {
    if (frames.length == 0) {
      return;
    }
    for (BufferedImage frame : frames) {
      addFrame(new BoardFrame(frame), time);
    }
  }
}
