package jab.smartboards.commons;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BoardConstants {

  private static final Map<String, Object> mapConstants = new HashMap<>();

  public void clear() {
    mapConstants.clear();
  }

  public <T> T get(@NotNull String key, Class<T> clazz) {
    return (T) mapConstants.get(key);
  }
}
