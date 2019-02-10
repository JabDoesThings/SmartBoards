package jab.smartboards.commons.board;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class FrameSwitchCondition {

  private static final Map<String, Condition> mapCustomVariables = new HashMap<>();

  public static Condition getVariable(@NotNull String key) {
    return mapCustomVariables.get(key);
  }

  public static void setVariable(@NotNull String key, @Nullable Object value) {
    // If null is passed, the variable should be removed.
    if (value == null) {
      mapCustomVariables.remove(key);
    }
    // If the value is numeric, store it as a number.
    if (value instanceof Number) {
      mapCustomVariables.put(key, new NumberCondition(key, (Number) value));
    }
  }

  private SwitchCondition condition;
  private String value;
  private boolean active;
  private long timeActivated;
  private long timeValue = -1;

  /**
   * Main constructor.
   *
   * @param condition The condition to check.
   * @param value The value to compare with the condition.
   */
  public FrameSwitchCondition(@NotNull SwitchCondition condition, @NotNull String value) {
    this.condition = condition;
    this.value = value;
  }

  public void activate() {
    this.timeActivated = System.currentTimeMillis();
  }

  public void deactivate() {
    this.timeActivated = -1L;
  }

  public boolean poll() {
    long delta = System.currentTimeMillis() - timeActivated;
    if (condition.isTimed()) {
      return condition.poll(timeValue, delta);
    }
  }

  public SwitchCondition getCondition() {
    return this.condition;
  }

  public String getValue() {
    return this.value;
  }
}
