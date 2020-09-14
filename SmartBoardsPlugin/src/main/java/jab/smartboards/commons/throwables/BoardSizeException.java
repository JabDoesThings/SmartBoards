package jab.smartboards.commons.throwables;

import jab.smartboards.commons.utils.BoardSizable;
import org.jetbrains.annotations.NotNull;

/**
 * This exception is fired when two BoardSizable implemented objects do not match their respective
 * dimensions.
 *
 * @author Josh
 */
public class BoardSizeException extends RuntimeException {

  /**
   * Main constructor.
   *
   * @param object The object with the dimensions to compare.
   * @param compared The offending object.
   */
  public BoardSizeException(@NotNull BoardSizable object, @NotNull BoardSizable compared) {
    super(
        object.getClass().getSimpleName()
            + "'s size is incompatible with the "
            + compared.getClass().getSimpleName()
            + "'s size. (["
            + object.getClass().getSimpleName()
            + ": "
            + object.getBlockWidth()
            + ", "
            + object.getBlockHeight()
            + "], "
            + compared.getClass().getSimpleName()
            + ": ["
            + compared.getBlockWidth()
            + ", "
            + compared.getBlockHeight()
            + "])");
  }
}
