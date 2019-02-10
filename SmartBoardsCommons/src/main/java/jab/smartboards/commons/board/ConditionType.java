package jab.smartboards.commons.board;

/**
 * This enumeration is used for conditions to switch frames for SmartBoards.
 *
 * @author Josh
 */
public enum SwitchCondition {
  // Time variables.
  TIME_PASSED_MS(1),
  TIME_PASSED_SECONDS(1000),
  TIME_PASSED_MINUTES(60000),
  TIME_PASSED_HOURS(3600000),
  TIME_PASSED_DAYS(86400000),
  // Event variables
  // TODO: Implement Bukkit events.
  CUSTOM_EVENT();

  private final long multiplier;
  private final boolean timed;

  /** Basic constructor. */
  SwitchCondition() {
    this.timed = false;
    this.multiplier = 1;
  }

  /**
   * Timed constructor.
   *
   * @param multiplier The multiplier for time values to poll.
   */
  SwitchCondition(long multiplier) {
    this.timed = true;
    this.multiplier = multiplier;
    // This check is here just in-case the non-original author modifies this code.
    if (multiplier < 1L) {
      throw new IllegalArgumentException("Millisecond multipliers cannot be less than 1.");
    }
  }

  /**
   * NOTE: Only <code>isTimed()</code> conditions will be met.
   *
   * @param value The value to meet. (multiplied by <code>getMultiplier()</code>)
   * @param delta The value to test.
   * @return Returns true if the value (when multiplied), given has been exceeded by or equal to the
   *     delta provided.
   */
  public boolean poll(long value, long delta) {
    // Only time events can be polled.
    if (!isTimed()) {
      return false;
    }
    return value * multiplier <= delta;
  }

  /** @return Returns true if the condition is a time condition. */
  public boolean isTimed() {
    return this.timed;
  }

  /**
   * @return Returns the multiplier for the condition poll check.
   *     <p>NOTE: If the condition is not timed, 1 will be returned.
   */
  public long getMultiplier() {
    return this.multiplier;
  }
}
