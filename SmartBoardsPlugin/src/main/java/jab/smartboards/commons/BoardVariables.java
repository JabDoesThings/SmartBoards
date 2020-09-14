package jab.smartboards.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document.
 *
 * @author Josh
 */
@SuppressWarnings({"unused", "WeakerAccess", "BooleanMethodIsAlwaysInverted"})
public class BoardVariables {

  public static final BoardVariables global = new BoardVariables();

  private final Map<String, Object> mapConstants;

  /** Main constructor. */
  BoardVariables() {
    this.mapConstants = new HashMap<>();
  }

  /** Clears all variables stored for SmartBoards. */
  public void clear() {
    mapConstants.clear();
  }

  /**
   * @param key The key storing the value.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  public Boolean getBoolean(@NotNull String key) {
    return get(key, Boolean.class);
  }

  /**
   * @param key The key storing the value.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  public Byte getByte(@NotNull String key) {
    return get(key, Byte.class);
  }

  /**
   * @param key The key storing the value.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  public Short getShort(@NotNull String key) {
    return get(key, Short.class);
  }

  /**
   * @param key The key storing the value.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  public Integer getInt(@NotNull String key) {
    return get(key, Integer.class);
  }

  /**
   * @param key The key storing the value.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  public Double getDouble(@NotNull String key) {
    return get(key, Double.class);
  }

  /**
   * @param key The key storing the value.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  public Long getLong(@NotNull String key) {
    return get(key, Long.class);
  }

  /**
   * @param key The key storing the value.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  public Number getNumber(String key) {
    return get(key, Number.class);
  }

  /**
   * @param key The key storing the value.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  public String getString(@NotNull String key) {
    return get(key, String.class);
  }

  /**
   * @param key The key storing the value.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  public Object get(@NotNull String key) {
    return mapConstants.get(key);
  }

  /**
   * @param key The key storing the value.
   * @param clazz The type of Object stored.
   * @return Returns the value stored for the constant. If a value is not stored, null is returned.
   */
  @SuppressWarnings({"unused", "unchecked"})
  public <T> T get(@NotNull String key, @NotNull Class<T> clazz) {
    return (T) get(key);
  }

  /**
   * Sets the value for a given key.
   *
   * @param key The key for the value to set.
   * @param value The value to set.
   */
  public void set(@NotNull String key, @Nullable Object value) {
    if (value == null) {
      mapConstants.remove(key);
    } else {
      mapConstants.put(key, value);
    }
  }

  /**
   * Sets the value stored for the key as null.
   *
   * @param key The key for the value to nullify.
   */
  public void setNull(@NotNull String key) {
    set(key, null);
  }

  /**
   * @param key The key to test.
   * @return Returns true if the value stored for the given key is null.
   */
  public boolean isNull(@NotNull String key) {
    return !mapConstants.containsKey(key);
  }

  /**
   * @param key The key to test.
   * @return Returns true if the key stores a value and the value is a String.
   */
  public boolean isString(@NotNull String key) {
    return !isNull(key) && mapConstants.get(key) instanceof String;
  }

  /**
   * @param key The key to test.
   * @return Returns true if the key stores a value and the value is a Boolean.
   */
  public boolean isBoolean(@NotNull String key) {
    return !isNull(key) && mapConstants.get(key) instanceof Boolean;
  }

  /**
   * @param key The key to test.
   * @return Returns true if the key stores a value and the value is a Byte.
   */
  public boolean isByte(@NotNull String key) {
    return !isNull(key) && mapConstants.get(key) instanceof Byte;
  }

  /**
   * @param key The key to test.
   * @return Returns true if the key stores a value and the value is a Short.
   */
  public boolean isShort(@NotNull String key) {
    return !isNull(key) && mapConstants.get(key) instanceof Short;
  }

  /**
   * @param key The key to test.
   * @return Returns true if the key stores a value and the value is a Integer.
   */
  public boolean isInt(@NotNull String key) {
    return !isNull(key) && mapConstants.get(key) instanceof Integer;
  }

  /**
   * @param key The key to test.
   * @return Returns true if the key stores a value and the value is a Double.
   */
  public boolean isDouble(@NotNull String key) {
    return !isNull(key) && mapConstants.get(key) instanceof Double;
  }

  /**
   * @param key The key to test.
   * @return Returns true if the key stores a value and the value is a Long.
   */
  public boolean isLong(@NotNull String key) {
    return !isNull(key) && mapConstants.get(key) instanceof Long;
  }

  /**
   * @param key The key to test.
   * @return Returns true if the key stores a value and the value is a Number.
   */
  public boolean isNumber(@NotNull String key) {
    return !isNull(key) && mapConstants.get(key) instanceof Number;
  }
}
