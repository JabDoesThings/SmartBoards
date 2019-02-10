package jab.smartboards.commons.board;

/**
 * This enumeration is used for conditions to switch frames for SmartBoards.
 *
 * @author Josh
 */
public enum ConditionType {
  VALUE_EQUALS(true),
  VALUE_GREATER_THAN(true),
  VALUE_LESS_THAN(true),
  VALUE_GREATER_THAN_OR_EQUALS(true),
  VALUE_LESS_THAN_OR_EQUALS(true),
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
  private final boolean custom;
  private final boolean value;

  /** Basic constructor. */
  ConditionType() {
    this.custom = true;
    this.value = false;
    this.timed = false;
    this.multiplier = 1;
  }

  /**
   * Value constructor.
   *
   * @param value The value flag.
   */
  ConditionType(boolean value) {
    this.custom = false;
    this.value = value;
    this.timed = false;
    this.multiplier = 1;
  }

  /**
   * Timed constructor.
   *
   * @param multiplier The multiplier for time values to poll.
   */
  ConditionType(long multiplier) {
    this.custom = false;
    this.value = false;
    this.timed = true;
    this.multiplier = multiplier;
    // This check is here just in-case the non-original author modifies this code.
    if (multiplier < 1L) {
      throw new IllegalArgumentException("Millisecond multipliers cannot be less than 1.");
    }
  }

  /**
   * NOTE: Only <code>isTimeCheck()</code> conditions will be met.
   *
   * @param value The value to meet. (multiplied by <code>getMultiplier()</code>)
   * @param delta The value to test.
   * @return Returns true if the value (when multiplied), given has been exceeded by or equal to the
   *     delta provided.
   */
  public boolean poll(long value, long delta) {
    // Only time events can be polled.
    if (!isTimeCheck()) {
      return false;
    }
    return value * multiplier <= delta;
  }

  /** @return Returns true if the condition is a time condition. */
  public boolean isTimeCheck() {
    return this.timed;
  }

  /**
   * @return Returns the multiplier for the condition poll check.
   *     <p>NOTE: If the condition is not timed, 1 will be returned.
   */
  public long getMultiplier() {
    return this.multiplier;
  }

  public boolean isCustom() {
    return this.custom;
  }

  public boolean isValueCheck() {
    return this.value;
  }

  public boolean pollValue(double a, double b) {
    if (isValueCheck()) {
      switch (this) {
        case VALUE_EQUALS:
          return a == b;
        case VALUE_GREATER_THAN:
          return a > b;
        case VALUE_LESS_THAN:
          return a < b;
        case VALUE_GREATER_THAN_OR_EQUALS:
          return a >= b;
        case VALUE_LESS_THAN_OR_EQUALS:
          return a <= b;
        case TIME_PASSED_MS:
        case TIME_PASSED_SECONDS:
        case TIME_PASSED_MINUTES:
        case TIME_PASSED_HOURS:
        case TIME_PASSED_DAYS:
        case CUSTOM_EVENT:
          return false;
      }
    }
    return false;
  }
}
