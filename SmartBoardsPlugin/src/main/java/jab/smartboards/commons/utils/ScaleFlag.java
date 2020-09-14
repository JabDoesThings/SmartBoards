package jab.smartboards.commons.utils;

/**
 * This enumeration identifies how images will be positioned on BoardFrames.
 *
 * @author Josh
 */
public enum ScaleFlag {
  NONE(false, false),
  FIT_X_MIPMAP(false, true),
  FIT_X_NEAREST(false, false),
  FIT_Y_MIPMAP(true, true),
  FIT_Y_NEAREST(true, false);

  private final boolean axis;
  private final boolean mip;

  ScaleFlag(boolean axis, boolean mip) {
    this.axis = axis;
    this.mip = mip;
  }

  public boolean isFitX() {
    return !axis;
  }

  public boolean isFitY() {
    return axis;
  }

  public boolean isNearest() {
    return !mip;
  }

  public boolean isLinear() {
    return mip;
  }
}
