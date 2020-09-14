package jab.smartboards.commons.utils;

public interface BoardSizable {

  default boolean matches(BoardSizable other) {
    return other != null && matches(other.getBlockWidth(), other.getBlockHeight());
  }

  default boolean matches(int width, int height) {
    return getBlockWidth() == width && getBlockHeight() == height;
  }

  int getBlockWidth();

  int getBlockHeight();
}
