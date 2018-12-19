package jab.spigot.smartboards.exceptions;

import jab.spigot.smartboards.utils.CompilerTask;

public class AlreadyCompiledException extends RuntimeException {

  public AlreadyCompiledException(CompilerTask task) {
    super("Attempted to register a task that is already compiled: " + task);
  }
}
