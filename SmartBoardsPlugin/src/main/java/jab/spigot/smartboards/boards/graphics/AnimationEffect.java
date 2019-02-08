package jab.spigot.smartboards.boards.graphics;

/**
 * AnimationEffects are used for effects when rendering transitions between BoardFrames.
 *
 * @author Josh
 */
public enum AnimationEffect {
  NONE(false),
  SLIDE_TOP(false),
  SLIDE_BOTTOM(false),
  SLIDE_LEFT(false),
  SLIDE_RIGHT(false),
  SLIDE_TOP_LEFT(false),
  SLIDE_TOP_RIGHT(false),
  SLIDE_BOTTOM_RIGHT(false),
  SLIDE_BOTTOM_LEFT(false),
  FALL_UP(false),
  FALL_DOWN(false),
  FALL_LEFT(false),
  FALL_RIGHT(false),
  SPIN_CW(true),
  SPIN_CCW(true),
  SPIN_TOP_LEFT_CW(true),
  SPIN_TOP_LEFT_CCW(true),
  SPIN_TOP_RIGHT_CW(true),
  SPIN_TOP_RIGHT_CCW(true),
  SPIN_BOTTOM_RIGHT_CW(true),
  SPIN_BOTTOM_RIGHT_CCW(true),
  SPIN_BOTTOM_LEFT_CW(true),
  SPIN_BOTTOM_LEFT_CCW(true);

  private final boolean spin;

  AnimationEffect(boolean spin) {
    this.spin = spin;
  }

  public boolean isSpin() {
    return this.spin;
  }
}
