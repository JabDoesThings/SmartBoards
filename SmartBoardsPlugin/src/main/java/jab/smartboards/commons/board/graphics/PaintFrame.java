package jab.smartboards.commons.board.graphics;

import jab.smartboards.commons.utils.MapImage;
import jab.smartboards.commons.utils.MapUtils;

import java.awt.*;

public class PaintFrame extends BoardFrame {

  public PaintFrame(int width, int height) {
    super(width, height, true);
  }

  /**
   * Creates a set of MapImages.
   *
   * @param startingMapIndex The starting mini-map index to assign.
   */
  @Override
  protected void create(int startingMapIndex) {
    Color transparent = new Color(0, 0, 0, 0);
    for (int index = 0; index < getSize(); index++) {
      frames[index] = new MapImage(128, 128, transparent);
      frames[index].setDynamic(true);
      frames[index].createPacket((short) startingMapIndex++);
    }
  }

  public void fillRect(int x, int y, int width, int height, byte color) {
    for (int iy = y; iy < y + height; iy++) {
      for (int ix = x; ix < x + width; ix++) {
        double u = (double) ix / ((double) getBlockWidth() * 128.0);
        double v = (double) iy / ((double) getBlockHeight() * 128.0);
        MapImage frame = getFrameByPixel(u, v);
        while (ix > 127) ix -= 128;
        while (iy > 127) iy -= 128;
        frame.setColor(ix, iy, color);
      }
    }
  }

  public void dispose() {}

  public void drawLine(int x1, int y1, int x2, int y2, Color color) {
    double distance = Math.abs(Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)));
    double stop = distance * 2;
    byte bColor = MapUtils.getColor(color);
    for (int index = 0; index < stop; index++) {
      double lerp = index / stop;
      int fx = (int) Math.floor(lerp(x1, x2, lerp));
      int fy = (int) Math.floor(lerp(y1, y2, lerp));
      double u = (double) fx / ((double) getBlockWidth() * 128.0);
      double v = (double) fy / ((double) getBlockHeight() * 128.0);
      System.out.println("\tfx: " + fx + " fy: " + fy + " u: " + u + " v: " + v + " lerp: " + lerp);
      MapImage frame = getFrameByPixel(u, v);
      while (fx > 127) {
        fx -= 128;
      }
      while (fy > 127) {
        fy -= 128;
      }
      frame.setColor(fx, fy, bColor);
    }
  }

  private static double lerp(double a, double b, double f) {
    return a + f * (b - a);
  }
}
