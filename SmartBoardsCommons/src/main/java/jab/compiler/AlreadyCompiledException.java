package jab.compiler;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class AlreadyCompiledException extends RuntimeException {

  public AlreadyCompiledException(CompilerTask task) {
    super("Attempted to register a task that is already compiled: " + task);
  }
}
