package jab.spigot.smartboards.boards.examples;

import jab.smartboards.commons.board.BoardProfile;
import jab.smartboards.commons.board.graphics.PaintFrame;
import jab.smartboards.commons.board.graphics.SimpleBoardGraphics;
import jab.smartboards.commons.events.SmartBoardClickEvent;
import jab.spigot.smartboards.boards.SyncStaticBoard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaintBoard extends SyncStaticBoard {

  PaintFrame frame;

  /**
   * Main constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   */
  public PaintBoard(@NotNull BoardProfile profile) {
    super(profile);
    setCanClick(true);
    load();
  }

  void load() {
    SimpleBoardGraphics graphics = new SimpleBoardGraphics(getBlockWidth(), getBlockHeight());
    frame = new PaintFrame(getBlockWidth(), getBlockHeight());
    graphics.setFrame(frame);
    setGraphics(graphics);
  }

  private Map<UUID, int[]> lastXY = new HashMap<>();
  private Map<UUID, Long> lastClicked = new HashMap<>();

  @Override
  public void onClick(SmartBoardClickEvent event) {
    Player player = event.getPlayer();

    UUID playerId = player.getUniqueId();

    boolean line = false;

    long time = System.currentTimeMillis();

    int[] lxy = null;
    if (lastXY.containsKey(playerId)) {
      lxy = lastXY.get(playerId);
    }

    if (lastClicked.containsKey(playerId)) {
      //      long delta = time - lastClicked.get(playerId);
      //      System.out.println("DELTA: " + delta);
      //      if (delta <= 400) {
      line = true;
      //      }
    }

    int x = event.getX();
    int y = event.getY();
    if (lxy != null && line) {
      frame.drawLine(lxy[0], lxy[1], x, y, Color.RED);
      getGraphics().setDirty(true);
      setDirty(true);
    }
    //    else {
    //      frame.drawCircle(x, y, Color.RED);
    //    }

    lastXY.put(playerId, new int[] {x, y});
    lastClicked.put(playerId, time);
  }
}
