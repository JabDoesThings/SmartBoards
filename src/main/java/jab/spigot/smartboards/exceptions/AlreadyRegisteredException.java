package jab.spigot.smartboards.exceptions;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class AlreadyRegisteredException extends RuntimeException {

  public AlreadyRegisteredException(Object registrar, Object registered) {
    super(
        "Object is already registered: (Registrar="
            + registrar
            + ", Registered="
            + registered
            + ")");
  }
}
