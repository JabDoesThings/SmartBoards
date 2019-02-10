// package jab.smartboards.commons.board;
//
// import org.jetbrains.annotations.NotNull;
//
// public class NumberCondition extends Condition<Number> {
//
//  /**
//   * Basic constructor.
//   *
//   * @param name The name of the condition.
//   */
//  public NumberCondition(@NotNull String name) {
//    super(name);
//  }
//
//  /**
//   * Main constructor.
//   *
//   * @param name The name of the condition.
//   * @param value The initial value of the condition.
//   */
//  public NumberCondition(@NotNull String name, @NotNull Number value) {
//    super(name, value);
//  }
//
//  /**
//   * Pass a number, or a number with a poll condition. Otherwise, false is returned.
//   *
//   * @param args Arguments to pass to check the condition.
//   * @return Returns true if the numerical argument passes the poll condition to check.
//   */
//  @Override
//  public boolean poll(Object... args) {
//    if (args.length == 0) {
//      return false;
//    }
//    Object value = args[0];
//    // Check to make sure that if a poll condition is passed, override the default one.
//    NumberPollCondition pollCondition = NumberPollCondition.EQUALS;
//    // If the polling argument is passed, use this instead.
//    if (args.length >= 2 && args[1] instanceof NumberPollCondition) {
//      pollCondition = (NumberPollCondition) args[1];
//    }
//
//    // If the values are both null, return true.
//    if (value == null && getValue() == null) {
//      return true;
//    }
//    // If one of the values is null but the other one is not, then return false.
//    if ((value == null && getValue() != null) || (value != null && getValue() == null)) {
//      return false;
//    }
//    if (value == null) {
//      return false;
//    }
//    double ourValue = getValue().doubleValue();
//    double theirValue;
//    // Attempt to process the comparison as close to the number given as possible.
//    if (value instanceof Number) {
//      // Bring both values to the largest data type, and compare them.
//      theirValue = ((Number) value).doubleValue();
//    } else {
//      try {
//        theirValue = Double.parseDouble(value.toString());
//      } catch (NumberFormatException e) {
//        // No need to throw. Simply return false.
//        return false;
//      }
//    }
//    // Process the arithmetic.
//    switch (pollCondition) {
//      case EQUALS:
//        return theirValue == ourValue;
//      case LESS_THEN_OR_EQUAL_TO:
//        return theirValue <= ourValue;
//      case GREATER_THEN_OR_EQUAL_TO:
//        return theirValue >= ourValue;
//      case LESS_THEN:
//        return theirValue < ourValue;
//      case GREATER_THEN:
//        return theirValue > ourValue;
//    }
//    return false;
//  }
//
//  /**
//   * TODO: Document.
//   *
//   * @author Josh
//   */
//  public enum NumberPollCondition {
//    EQUALS,
//    LESS_THEN_OR_EQUAL_TO,
//    GREATER_THEN_OR_EQUAL_TO,
//    LESS_THEN,
//    GREATER_THEN
//  }
// }
