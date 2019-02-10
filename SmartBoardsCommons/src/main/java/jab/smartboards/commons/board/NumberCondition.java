package jab.smartboards.commons.board;

import org.jetbrains.annotations.NotNull;

public class IntegerCondition extends Condition<Integer> {

  /**
   * Basic constructor.
   *
   * @param name The name of the condition.
   */
  public IntegerCondition(@NotNull String name) {
    super(name);
  }

  /**
   * Main constructor.
   *
   * @param name The name of the condition.
   * @param value The initial value of the condition.
   */
  public IntegerCondition(@NotNull String name, @NotNull Integer value) {
    super(name, value);
  }

  @Override
  public boolean poll(Object value) {
    // If the values are both null, return true.
    if (value == null && getValue() == null) {
      return true;
    }
    // If one of the values is null but the other one is not, then return false.
    if ((value == null && getValue() != null) || (value != null && getValue() == null)) {
      return false;
    }
    // Attempt to process the comparison as close to the number given as possible.
    if (value instanceof Number) {
      // If both are the appropriate data type, compare them.
      if (value instanceof Integer) {
        return getValue() == (int) value;
      }
      // Bring both values to the largest data type, and compare them.
      else {
        return ((Number) value).doubleValue() == getValue().doubleValue();
      }
    }
    return false;
  }
}
