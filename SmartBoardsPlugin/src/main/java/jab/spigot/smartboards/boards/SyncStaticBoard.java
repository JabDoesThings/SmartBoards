package jab.spigot.smartboards.boards;

import jab.smartboards.commons.board.SyncSmartBoard;
import jab.smartboards.commons.board.graphics.StaticBoardGraphics;
import jab.smartboards.commons.board.BoardProfile;
import org.jetbrains.annotations.NotNull;

public class SyncStaticBoard extends SyncSmartBoard {

  /**
   * Main constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   */
  public SyncStaticBoard(@NotNull BoardProfile profile) {
    super(profile);
    setGraphics(new StaticBoardGraphics(profile.getBlockWidth(), profile.getBlockHeight()));
  }
}
