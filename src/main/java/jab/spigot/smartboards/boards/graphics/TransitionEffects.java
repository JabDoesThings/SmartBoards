package jab.spigot.smartboards.boards.graphics;

import jab.spigot.smartboards.utils.MapImage;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings({"WeakerAccess", "unused"})
public class TransitionEffects {

  public static void enableJavaFX() {
    // Necessary to initialize the JavaFX platform and to avoid "IllegalStateException: Toolkit not
    // initialized"
    new JFXPanel();
  }

  public static void disableJavaFX() {
    // Since we started a JavaFX thread we have to shut it down. Otherwise the JVM
    // won't exit.
    Platform.exit();
  }

  private static final float PI_F = (float) Math.PI;

  /**
   * @param value The lerp value. (between 0.0 and 1.0)
   * @return Returns the adjusted time position.
   */
  public static float lerpOut(float value) {
    value = normalize(0.0F, 1.0F, value);
    return (float) Math.sin(value * PI_F * 0.5F);
  }

  /**
   * @param value The lerp value. (between 0.0 and 1.0)
   * @return Returns the adjusted time position.
   */
  public static float lerpIn(float value) {
    value = normalize(0.0F, 1.0F, value);
    return (float) Math.cos(value * PI_F * 0.5F);
  }

  /**
   * @param value The lerp value. (between 0.0 and 1.0)
   * @return Returns the adjusted time position.
   */
  public static float smoothStep(float value) {
    value = normalize(0.0F, 1.0F, value);
    return value * value * (3.0F - 2.0F * value);
  }

  /**
   * @param value The lerp value. (between 0.0 and 1.0)
   * @return Returns the adjusted time position.
   */
  public static float smootherStep(float value) {
    value = normalize(0.0F, 1.0F, value);
    return value * value * value * (value * (6.0F * value - 15.0F) + 10F);
  }

  private static float normalize(float min, float max, float value) {
    return value > max ? max : value < min ? min : value;
  }

  /**
   * Creates an interpolated set of frames linearly, as a set of BufferedImages.<br>
   * NOTE: If the start and stop image are different dimensions, the transition will only use the
   * largest dimensions.
   *
   * @param start The first frame in the transition.
   * @param stop The last frame in the transition.
   * @param frameCount The amount of frames to interpolate.
   * @return Returns a createTransition transition of the BufferedImages given.
   * @throws NullPointerException Thrown if the start or stop BufferedImage given is null.
   * @throws IllegalArgumentException Thrown if the frameCount given is less than 1.
   */
  @NotNull
  public static BufferedImage[] createTransition(
      @NotNull BufferedImage start, @NotNull BufferedImage stop, int frameCount) {
    return createTransition(
        start, stop, frameCount, TimeEffect.LINEAR, ScaleEffect.NONE, AnimationEffect.NONE);
  }

  @NotNull
  public static BufferedImage[] createTransform3D(@NotNull BufferedImage start, @NotNull BufferedImage stop, int frameCount, @NotNull TimeEffect timeEffect, @NotNull ScaleEffect scaleEffect, @NotNull AnimationEffect effect) {
    checkFrameCountArg(frameCount);

    int startWidth = start.getWidth();
    int startHeight = start.getHeight();
    int width = Math.max(startWidth, stop.getWidth());
    int height = Math.max(startHeight, stop.getHeight());
    int centerX = (int) Math.floor(width / 2.0F);
    int centerY = (int) Math.floor(height / 2.0F);
    BufferedImage[] frames = new BufferedImage[frameCount];

    float frameStep = 1.0F / (frameCount);
    float frameOffsetX = 0.0F;
    float frameOffsetY = 0.0F;
    float frameScaleX = 0.0F;
    float frameScaleY = 0.0F;
    boolean shrink = false;
    boolean grow = false;
    boolean dissolve = false;
    switch (effect) {
      case NONE:
        break;
      case SLIDE_TOP:
        frameOffsetY = -frameStep;
        break;
      case SLIDE_BOTTOM:
        frameOffsetY = frameStep;
        break;
      case SLIDE_LEFT:
        frameOffsetX = -frameStep;
        break;
      case SLIDE_RIGHT:
        frameOffsetX = frameStep;
        break;
      case SLIDE_TOP_LEFT:
        frameOffsetX = -frameStep;
        frameOffsetY = -frameStep;
        break;
      case SLIDE_TOP_RIGHT:
        frameOffsetX = frameStep;
        frameOffsetY = -frameStep;
        break;
      case SLIDE_BOTTOM_RIGHT:
        frameOffsetX = frameStep;
        frameOffsetY = frameStep;
        break;
      case SLIDE_BOTTOM_LEFT:
        frameOffsetX = -frameStep;
        frameOffsetY = frameStep;
        break;
      case SHRINK:
        shrink = true;
        break;
      case GROW:
        grow = true;
        break;
      case DISSOLVE:
        dissolve = true;
        break;
    }

    switch (scaleEffect) {
      case NONE:
        break;
      case GROW:
        grow = true;
        break;
      case SHRINK:
        shrink = true;
        break;
    }

    for (int index = 0; index < frameCount; index++) {
      float lerp = ((float) (index + 1)) / (float) frameCount;
      float flerp = lerp;
      switch (timeEffect) {
        case CONSTANT:
          flerp = index == frameCount - 1 ? 1.0F : 0.0F;
        case LINEAR:
          break;
        case EASE_IN:
          flerp = 1.0F - lerpIn(lerp);
          break;
        case EASE_OUT:
          flerp = lerpOut(lerp);
          break;
        case EASE_IN_OUT:
          flerp = smoothStep(lerp);
          break;
        case EASE_IN_OUT_SMOOTH:
          flerp = smootherStep(lerp);
          break;
      }
      int xOffset = (int) Math.floor((frameOffsetX * (flerp * frameCount)) * startWidth);
      int yOffset = (int) Math.floor((frameOffsetY * (flerp * frameCount)) * startHeight);
      float alphaStop = 1.0F;
      float alphaStart = 1.0F - flerp;
      System.out.println("flerp: " + flerp);
      System.out.println("offset: " + xOffset + " " + yOffset);
      // Draw the frame.
      frames[index] = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      Graphics2D g = (Graphics2D) frames[index].getGraphics();
      if (!grow && !shrink && !dissolve) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alphaStop));
      }
      g.drawImage(stop, 0, 0, null);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaStart));
      if (grow || shrink) {
        int fWidth;
        int fHeight;
        if (grow) {
          fWidth = (int) Math.floor((1.0F + lerp) * (float) startWidth);
          fHeight = (int) Math.floor((1.0F + lerp) * (float) startHeight);
        } else {
          fWidth = (int) Math.floor((1.0F - (lerp / 2.0F)) * (float) startWidth);
          fHeight = (int) Math.floor((1.0F - (lerp / 2.0F)) * (float) startHeight);
        }
        int fx = centerX - (fWidth / 2);
        int fy = centerY - (fHeight / 2);
        System.out.println("f: " + fx + " " + fy + " " + fWidth + " " + fHeight);
        g.drawImage(start, fx + xOffset, fy + yOffset, fWidth, fHeight, null);
      } else if (dissolve) {
        // TODO: Implement.
      } else {
        g.drawImage(start, xOffset, yOffset, null);
      }
      g.dispose();
    }
    return frames;
  }

  /**
   * Creates an interpolated set of frames linearly, as a set of BufferedImages.<br>
   * NOTE: If the start and stop image are different dimensions, the transition will only use the
   * largest dimensions.
   *
   * @param start The first frame in the transition.
   * @param stop The last frame in the transition.
   * @param frameCount The amount of frames to interpolate.
   * @return Returns a createTransition transition of the BufferedImages given.
   * @throws NullPointerException Thrown if the start or stop BufferedImage given is null.
   * @throws IllegalArgumentException Thrown if the frameCount given is less than 1.
   */
  @NotNull
  public static BufferedImage[] createTransition(
      @NotNull BufferedImage start,
      @NotNull BufferedImage stop,
      int frameCount,
      @NotNull TimeEffect timeEffect,
      @NotNull ScaleEffect scaleEffect,
      @NotNull AnimationEffect effect) {
    checkFrameCountArg(frameCount);

    int startWidth = start.getWidth();
    int startHeight = start.getHeight();
    int width = Math.max(startWidth, stop.getWidth());
    int height = Math.max(startHeight, stop.getHeight());
    int centerX = (int) Math.floor(width / 2.0F);
    int centerY = (int) Math.floor(height / 2.0F);
    BufferedImage[] frames = new BufferedImage[frameCount];

    float frameStep = 1.0F / (frameCount);
    float frameOffsetX = 0.0F;
    float frameOffsetY = 0.0F;
    float frameScaleX = 0.0F;
    float frameScaleY = 0.0F;
    boolean shrink = false;
    boolean grow = false;
    boolean dissolve = false;
    switch (effect) {
      case NONE:
        break;
      case SLIDE_TOP:
        frameOffsetY = -frameStep;
        break;
      case SLIDE_BOTTOM:
        frameOffsetY = frameStep;
        break;
      case SLIDE_LEFT:
        frameOffsetX = -frameStep;
        break;
      case SLIDE_RIGHT:
        frameOffsetX = frameStep;
        break;
      case SLIDE_TOP_LEFT:
        frameOffsetX = -frameStep;
        frameOffsetY = -frameStep;
        break;
      case SLIDE_TOP_RIGHT:
        frameOffsetX = frameStep;
        frameOffsetY = -frameStep;
        break;
      case SLIDE_BOTTOM_RIGHT:
        frameOffsetX = frameStep;
        frameOffsetY = frameStep;
        break;
      case SLIDE_BOTTOM_LEFT:
        frameOffsetX = -frameStep;
        frameOffsetY = frameStep;
        break;
      case SHRINK:
        shrink = true;
        break;
      case GROW:
        grow = true;
        break;
      case DISSOLVE:
        dissolve = true;
        break;
    }

    switch (scaleEffect) {
      case NONE:
        break;
      case GROW:
        grow = true;
        break;
      case SHRINK:
        shrink = true;
        break;
    }

    for (int index = 0; index < frameCount; index++) {
      float lerp = ((float) (index + 1)) / (float) frameCount;
      float flerp = lerp;
      switch (timeEffect) {
        case CONSTANT:
          flerp = index == frameCount - 1 ? 1.0F : 0.0F;
        case LINEAR:
          break;
        case EASE_IN:
          flerp = 1.0F - lerpIn(lerp);
          break;
        case EASE_OUT:
          flerp = lerpOut(lerp);
          break;
        case EASE_IN_OUT:
          flerp = smoothStep(lerp);
          break;
        case EASE_IN_OUT_SMOOTH:
          flerp = smootherStep(lerp);
          break;
      }
      int xOffset = (int) Math.floor((frameOffsetX * (flerp * frameCount)) * startWidth);
      int yOffset = (int) Math.floor((frameOffsetY * (flerp * frameCount)) * startHeight);
      float alphaStop = 1.0F;
      float alphaStart = 1.0F - flerp;
      System.out.println("flerp: " + flerp);
      System.out.println("offset: " + xOffset + " " + yOffset);
      // Draw the frame.
      frames[index] = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      Graphics2D g = (Graphics2D) frames[index].getGraphics();
      if (!grow && !shrink && !dissolve) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alphaStop));
      }
      g.drawImage(stop, 0, 0, null);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaStart));
      if (grow || shrink) {
        int fWidth;
        int fHeight;
        if (grow) {
          fWidth = (int) Math.floor((1.0F + lerp) * (float) startWidth);
          fHeight = (int) Math.floor((1.0F + lerp) * (float) startHeight);
        } else {
          fWidth = (int) Math.floor((1.0F - (lerp / 2.0F)) * (float) startWidth);
          fHeight = (int) Math.floor((1.0F - (lerp / 2.0F)) * (float) startHeight);
        }
        int fx = centerX - (fWidth / 2);
        int fy = centerY - (fHeight / 2);
        System.out.println("f: " + fx + " " + fy + " " + fWidth + " " + fHeight);
        g.drawImage(start, fx + xOffset, fy + yOffset, fWidth, fHeight, null);
      } else if (dissolve) {
        // TODO: Implement.
      } else {
        g.drawImage(start, xOffset, yOffset, null);
      }
      g.dispose();
    }
    return frames;
  }

  /**
   * Creates an interpolated set of frames linearly, as a set of BoardFrames.<br>
   * NOTE: If the start and stop image are different dimensions, the transition will only use the
   * largest dimensions.
   *
   * @param start The first frame in the transition.
   * @param stop The last frame in the transition.
   * @param frameCount The amount of frames to interpolate.
   * @return Returns a createTransition transition of the BoardFrames given.
   * @throws NullPointerException Thrown if the start or stop MapImage given is null.
   * @throws IllegalArgumentException Thrown if the frameCount given is less than 1.
   */
  @NotNull
  public static BoardFrame[] createTransition(
      @NotNull BoardFrame start, @NotNull BoardFrame stop, int frameCount) {
    checkFrameCountArg(frameCount);
    BufferedImage imageStart = start.toBufferedImage();
    BufferedImage imageStop = stop.toBufferedImage();
    BufferedImage[] frames = createTransition(imageStart, imageStop, frameCount);
    return toBoardFrames(frames);
  }

  /**
   * Creates an interpolated set of frames linearly, as a set of MapImages.<br>
   * NOTE: If the start and stop image are different dimensions, the transition will only use the
   * largest dimensions.
   *
   * @param start The first frame in the transition.
   * @param stop The last frame in the transition.
   * @param frameCount The amount of frames to interpolate.
   * @return Returns a createTransition transition of the MapImages given.
   * @throws NullPointerException Thrown if the start or stop MapImage given is null.
   * @throws IllegalArgumentException Thrown if the frameCount given is less than 1.
   */
  @NotNull
  public static MapImage[] createTransition(
      @NotNull MapImage start, @NotNull MapImage stop, int frameCount) {
    checkFrameCountArg(frameCount);
    BufferedImage imageStart = start.toBufferedImage();
    BufferedImage imageStop = stop.toBufferedImage();
    BufferedImage[] frames = createTransition(imageStart, imageStop, frameCount);
    return toMapImages(frames);
  }

  /**
   * Converts an array of BoardFrames to BufferedImages, returned as an array.<br>
   * NOTE: If the array is empty is given, an empty array is returned.
   *
   * @param boardFrames The array of BoardFrames to convert to BufferedImages.
   * @return Returns an array of converted BufferedImages.
   * @throws NullPointerException Thrown if the array given is null.
   */
  @NotNull
  public static BufferedImage[] toBufferedImages(@NotNull BoardFrame[] boardFrames) {
    BufferedImage[] images = new BufferedImage[boardFrames.length];
    if (boardFrames.length > 0) {
      for (int index = 0; index < boardFrames.length; index++) {
        images[index] = boardFrames[index].toBufferedImage();
      }
    }
    return images;
  }

  /**
   * Converts an array of MapImages to BufferedImages, returned as an array.<br>
   * NOTE: If the array is empty is given, an empty array is returned.
   *
   * @param mapImages The array of MapImages to convert to BufferedImages.
   * @return Returns an array of converted BufferedImages.
   * @throws NullPointerException Thrown if the array given is null.
   */
  @NotNull
  public static BufferedImage[] toBufferedImages(@NotNull MapImage[] mapImages) {
    BufferedImage[] images = new BufferedImage[mapImages.length];
    if (mapImages.length > 0) {
      for (int index = 0; index < mapImages.length; index++) {
        images[index] = mapImages[index].toBufferedImage();
      }
    }
    return images;
  }

  /**
   * Converts an array of BufferedImages to BoardFrames, returned as an array.<br>
   * NOTE: If the array is empty is given, an empty array is returned.
   *
   * @param images The array of BufferedImages to convert to BoardFrames.
   * @return Returns an array of converted BoardFrames.
   * @throws NullPointerException Thrown if the array given is null.
   */
  @NotNull
  public static BoardFrame[] toBoardFrames(@NotNull BufferedImage[] images) {
    BoardFrame[] frames = new BoardFrame[images.length];
    if (images.length > 0) {
      for (int index = 0; index < images.length; index++) {
        frames[index] = new BoardFrame(images[index]);
      }
    }
    return frames;
  }

  /**
   * Converts an array of BufferedImages to MapImages, returned as an array.<br>
   * NOTE: If the array is empty is given, an empty array is returned.
   *
   * @param images The array of BufferedImages to convert to MapImages.
   * @return Returns an array of converted MapImages.
   * @throws NullPointerException Thrown if the array given is null.
   */
  @NotNull
  public static MapImage[] toMapImages(@NotNull BufferedImage[] images) {
    MapImage[] mapImages = new MapImage[images.length];
    if (images.length > 0) {
      for (int index = 0; index < images.length; index++) {
        mapImages[index] = new MapImage(images[index]);
      }
    }
    return mapImages;
  }

  private static void checkFrameCountArg(int frameCount) {
    if (frameCount < 1) {
      throw new IllegalArgumentException(
          "Frame counts cannot be less than 1. Given: " + frameCount);
    }
  }
}
