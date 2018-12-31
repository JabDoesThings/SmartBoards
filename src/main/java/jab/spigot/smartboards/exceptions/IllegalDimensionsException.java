package jab.spigot.smartboards.exceptions;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class IllegalDimensionsException extends RuntimeException {

  public IllegalDimensionsException(int width, int height, int reqWidth, int reqHeight) {
    super(
        "Illegal dimensions: ["
            + width
            + ", "
            + height
            + "]. Must be: ["
            + reqWidth
            + ", "
            + reqHeight
            + "].");
  }
}
