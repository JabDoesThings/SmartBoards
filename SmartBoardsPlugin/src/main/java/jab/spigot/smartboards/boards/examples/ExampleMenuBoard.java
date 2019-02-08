package jab.spigot.smartboards.boards.examples;

import jab.smartboards.commons.board.menu.MenuBoard;
import jab.smartboards.commons.board.BoardProfile;
import org.jetbrains.annotations.NotNull;

public class ExampleMenuBoard extends MenuBoard {

  /**
   * Main constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   */
  public ExampleMenuBoard(@NotNull BoardProfile profile) {
    super(profile);
  }

  @Override
  protected void onCreate() {
    ExampleSmartMenu smartMenu = new ExampleSmartMenu(getBlockWidth(), getBlockHeight());
    setMenu(smartMenu);
  }
}
