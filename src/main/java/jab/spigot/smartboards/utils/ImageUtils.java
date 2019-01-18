package jab.spigot.smartboards.utils;

import jab.spigot.smartboards.enums.AnchorFlag;
import jab.spigot.smartboards.enums.ScaleFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * TODO: Document.
 *
 * @author Josh
 */
@SuppressWarnings("WeakerAccess")
public class ImageUtils {

  @NotNull
  public static BufferedImage formatImage(@NotNull BufferedImage image, int bWidth, int bHeight) {
    return formatImage(image, bWidth, bHeight, null, AnchorFlag.CENTER);
  }

  @NotNull
  public static BufferedImage formatImage(
      @NotNull BufferedImage image,
      int bWidth,
      int bHeight,
      Color backgroundColor,
      @NotNull AnchorFlag anchor) {
    return formatImage(image, bWidth, bHeight, backgroundColor, anchor, ScaleFlag.NONE);
  }

  @NotNull
  public static BufferedImage formatImage(
      @NotNull BufferedImage image, int bWidth, int bHeight, @NotNull AnchorFlag anchor) {
    return formatImage(image, bWidth, bHeight, null, anchor);
  }

  /**
   * Formats an image to given board dimensions, the anchor to project the image, and how to scale
   * the image.
   *
   * <p>Board dimensions are by block units, which is 128 pixels. (A map is also 128x128 pixels, and
   * is placed on a ItemFrame which equates to 1 block's worth of space)
   *
   * <p>If the image is smaller than the board dimensions, then it will be positioned based on the
   * anchor with remaining space as either a specified background color, or transparent space. If
   * the image is larger than the board dimensions.
   *
   * <p>If a scale flag is passed, the image will be scaled appropriately. If the flag is for
   * mip-mapping, then the 'BICUBIC' algorithm will be used to scale the image. then the image will
   * be scaled depending on the scale parameter passed.
   *
   * @param image The image to format.
   * @param bWidth The width of the board. (In blocks)
   * @param bHeight The height of the board. (In blocks)
   * @param background The background color to draw before the image is formatted. (Optional)
   * @param anchor The anchor on the board to project the image.
   * @param scale The scale flag used for scaling the image.
   * @return Returns the formatted image.
   * @throws NullPointerException Thrown if the image, anchor, or scale parameters passed are null.
   */
  @NotNull
  public static BufferedImage formatImage(
      @NotNull BufferedImage image,
      int bWidth,
      int bHeight,
      @Nullable Color background,
      @NotNull AnchorFlag anchor,
      @NotNull ScaleFlag scale) {
    int iWidth = image.getWidth();
    int iHeight = image.getHeight();
    BufferedImage ret =
        new BufferedImage(bWidth * 128, bHeight * 128, BufferedImage.TYPE_4BYTE_ABGR);
    int rWidth = ret.getWidth();
    int rHeight = ret.getHeight();
    Graphics2D g = (Graphics2D) ret.getGraphics();
    if (background != null) {
      g.setColor(background);
      g.fillRect(0, 0, ret.getWidth(), ret.getHeight());
    }

    BufferedImage fImage = image;
    boolean exact = true;
    if (scale != ScaleFlag.NONE) {
      if (iWidth > rWidth || iHeight > rHeight) {
        exact = false;
      } else if (iWidth < rWidth || iHeight < rHeight) {
        exact = false;
      }
      if (!exact) {
        boolean xBigger = iWidth != iHeight && iWidth > iHeight;
        boolean yBigger = iWidth != iHeight && !xBigger;
        // Calculate the aspect ratio for scaling.
        double ar = (double) iWidth / (double) iHeight;
        double arWidth, arHeight;
        // Calculate the new dimensions by scaling the image's original dimensions with the aspect
        // ratio to the scale function.
        if (scale.isFitX()) {
          arWidth = rWidth;
          arHeight = xBigger || yBigger ? arWidth / ar : arWidth;
        } else { // Fit Y.
          arHeight = rHeight;
          arWidth = xBigger || yBigger ? arHeight * ar : arHeight;
        }
        int nWidth = (int) Math.floor(arWidth);
        int nHeight = (int) Math.floor(arHeight);
        // Grab which hint we will use to scale the image.
        Object hint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        if (scale.isLinear()) {
          hint = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        }
        // Create the new image.
        fImage = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_4BYTE_ABGR);
        // Draw the scaled image.
        Graphics2D g2 = (Graphics2D) fImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
        g2.drawImage(image, 0, 0, nWidth, nHeight, null);
        g2.dispose();
        // Adjust the image dimensions for calculating the position.
        iWidth = nWidth;
        iHeight = nHeight;
      }
    }
    int fx = 0;
    int fy = 0;
    if (anchor == AnchorFlag.CENTER) {
      fx = (ret.getWidth() / 2) - (iWidth / 2);
      fy = (ret.getHeight() / 2) - (iHeight / 2);
    } else if (anchor == AnchorFlag.TOP_RIGHT) {
      fx = ret.getWidth() - iWidth;
    } else if (anchor == AnchorFlag.BOTTOM_RIGHT) {
      fx = ret.getWidth() - iWidth;
      fy = ret.getHeight() - iHeight;
    } else if (anchor == AnchorFlag.BOTTOM_LEFT) {
      fy = ret.getHeight() - iHeight;
    }
    g.drawImage(fImage, fx, fy, null);
    g.dispose();
    return ret;
  }
}
