package jab.smartboards.commons.throwables;

import jab.smartboards.commons.board.menu.adapter.AdaptableObject;

public class LoopCycleException extends RuntimeException {

  public LoopCycleException(AdaptableObject object) {
    super(
        "Looped cycle detected: "
            + object.getClass().getSimpleName()
            + ". (The element is updating itself)");
  }
}
