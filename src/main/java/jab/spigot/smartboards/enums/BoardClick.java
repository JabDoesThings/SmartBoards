package jab.spigot.smartboards.enums;

/**
 * This enum is the button of the mouse a Player uses when interacting with a Board.
 *
 * @author Josh
 */
public enum BoardClick {
  /** The left mouse-button. */
  LEFT,
  /** The right mouse-button. */
  RIGHT;

  /**
   * This is the type of click the Player gives when interacting with a Board.
   *
   * @author Jab
   */
  public enum Type {
    /** The mouse clicked down, indicating a hold. */
    DOWN,
    /** The mouse clicked up, indicating a release of the button. */
    UP,
    /** The mouse clicked. */
    CLICK,
    /** The mouse is held down past a tick for the SmartBoardThread. */
    HELD;
  }
}
