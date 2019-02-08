package jab.spigot.smartboards.boards.menu;

import jab.smartboards.commons.utils.MapImage;
import jab.smartboards.commons.utils.MapUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public enum FrameType {
  FRAME_1("frame1", new Color(198, 198, 198)),
  FRAME_2("frame2", new Color(55, 55, 55)),
  NONE(null, null);

  public final MapImage tl;
  public final MapImage tr;
  public final MapImage bl;
  public final MapImage br;
  public final MapImage t;
  public final MapImage b;
  public final MapImage l;
  public final MapImage r;
  public final String name;

  private final byte background;

  FrameType(String name, Color backgroundColor) {
    this.name = name;
    if (name != null) {
      MenuAssets.load(false);
      this.tl = MenuAssets.get(name + "_tl");
      this.tr = MenuAssets.get(name + "_tr");
      this.bl = MenuAssets.get(name + "_bl");
      this.br = MenuAssets.get(name + "_br");
      this.t = MenuAssets.get(name + "_t");
      this.b = MenuAssets.get(name + "_b");
      this.l = MenuAssets.get(name + "_l");
      this.r = MenuAssets.get(name + "_r");
    } else {
      tl = tr = bl = br = t = b = l = r = null;
    }
    if (backgroundColor != null) {
      this.background = MapUtils.getColor(backgroundColor);
    } else {
      this.background = (byte) 0;
    }
  }

  /**
   * Generates a frame for given dimensions.
   *
   * @param width The width of the frame. (In pixels)
   * @param height The height of the frame. (In pixels)
   * @return Returns the frame as a MapImage.
   */
  @NotNull
  public MapImage generateFrame(int width, int height) {
    MapImage image = new MapImage(width, height, (byte) 0);
    if (this == NONE) {
      return image;
    }
    image.draw(tl, 0, 0);
    image.draw(tr, width - tr.getWidth(), 0);
    image.draw(bl, 0, height - bl.getHeight());
    image.draw(br, width - br.getWidth(), height - br.getHeight());
    // Left
    int lx = 0;
    for (int ly = tl.getHeight(); ly < height - bl.getHeight(); ly++) {
      image.draw(l, lx, ly);
    }
    // Right
    int rx = width - r.getWidth();
    for (int ry = tr.getHeight(); ry < height - br.getHeight(); ry++) {
      image.draw(r, rx, ry);
    }
    // Top
    int ty = 0;
    for (int tx = tl.getWidth(); tx < width - tr.getWidth(); tx++) {
      image.draw(t, tx, ty);
    }
    // Bottom
    int by = height - b.getHeight();
    for (int bx = bl.getWidth(); bx < width - br.getWidth(); bx++) {
      image.draw(b, bx, by);
    }
    return image;
  }

  public MapImage generateBackground(int width, int height) {
    int bWidth = width - l.getWidth() - r.getWidth();
    int bHeight = height - t.getHeight() - b.getHeight();
    MapImage image = new MapImage(width, height, (byte) 0);
    if (this == NONE) {
      return image;
    }
    image.fillRect(tl.getWidth(), tl.getHeight(), bWidth, bHeight, background);
    return image;
  }
}
