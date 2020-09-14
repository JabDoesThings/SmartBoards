package jab.smartboards.commons.board;

import jab.smartboards.commons.BoardVariables;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class BoardCondition {

  private final BoardVariables variables;
  private final String key;
  private final Object value;
  private ConditionType type;
  private Long timeValue;
  private Double numberValue;
  private long timeActivated;
  private boolean activated;

  /**
   * Main constructor.
   *
   * @param type The type of condition to check.
   * @param value The value to compare with the condition.
   */
  public BoardCondition(
      BoardVariables variables,
      @NotNull ConditionType type,
      @NotNull String key,
      @NotNull String value) {
    this.variables = variables;
    this.type = type;
    this.key = key;
    this.value = value;
    // If the type checks for time, convert the value and store it.
    if (type.isTimeCheck()) {
      timeValue = Long.parseLong(value);
    }
    // If the type checks a value, conver the value and store it.
    else if (type.isValueCheck()) {
      numberValue = Double.parseDouble(value);
    }
  }

  /** Activates the check. */
  public void activate() {
    this.timeActivated = System.currentTimeMillis();
    this.activated = true;
  }

  /** Deactivates the check. */
  public void deactivate() {
    this.timeActivated = -1L;
    this.activated = false;
  }

  /** @return Returns true if the poll check passes the condition set. */
  public boolean poll() {
    long delta = System.currentTimeMillis() - timeActivated;
    if (type.isTimeCheck()) {
      return type.poll(timeValue, delta);
    } else if (type.isValueCheck()) {
      return variables.isNumber(key)
          && type.pollValue(numberValue, variables.getNumber(key).doubleValue());
    } else if (type.isCustom()) {
      return value.equals(variables.get(key));
    }
    return false;
  }

  @NotNull
  public ConditionType getType() {
    return this.type;
  }

  @NotNull
  public String getKey() {
    return this.key;
  }

  @NotNull
  public Object getValue() {
    return this.value;
  }

  /** @return Returns true if the condition is active and being polled. */
  public boolean isActivated() {
    return this.activated;
  }
}
