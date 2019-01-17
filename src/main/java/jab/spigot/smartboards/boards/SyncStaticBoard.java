package jab.spigot.smartboards.boards;

import jab.spigot.smartboards.boards.graphics.StaticBoardGraphics;
import jab.spigot.smartboards.utils.BoardProfile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SyncStaticBoard extends SyncSmartBoard {

  /**
   * Main constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   */
  public SyncStaticBoard(@NotNull BoardProfile profile) {
    super(profile);
    setGraphics(new StaticBoardGraphics(profile.getWidth(), profile.getHeight()));
  }
}
