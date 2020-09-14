package jab.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class handles organization of custom variable checks for SmartBoard condition checks.
 *
 * @author Josh
 * @param <V> The Type of value associated with the condition.
 */
public class Condition<V> {

  private final Object lock = new Object();
  private final String name;
  private V value;

  /**
   * Basic constructor. Sets the initial value as null.
   *
   * @param name The name of the condition.
   */
  public Condition(@NotNull String name) {
    this(name, null);
  }

  /**
   * Main constructor.
   *
   * @param name The name of the condition.
   * @param value The initial value to set for the condition.
   */
  public Condition(@NotNull String name, @Nullable V value) {
    this.name = name;
    this.value = value;
  }

  /** @return Returns the value set for the condition. */
  public V getValue() {
    synchronized (lock) {
      return this.value;
    }
  }

  /**
   * Sets the value for the condition.
   *
   * @param value The value to set.
   */
  public void setValue(@Nullable V value) {
    synchronized (lock) {
      this.value = value;
    }
  }

  /** @return Returns the name of the condition. */
  public String getName() {
    return this.name;
  }

  /**
   * Polls the condition to see if the condition passes.
   *
   * @param args Arguments to pass to check the condition.
   * @return Returns true if the condition passes.
   */
  public boolean poll(@Nullable Object... args) {
    synchronized (lock) {
      return args != null && args.length > 0 && args[0] != null && args[0].equals(getValue());
    }
  }
}
