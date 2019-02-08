package jab.smartboards.commons.events;

import jab.smartboards.commons.board.SmartBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * This is the base event class for smartboard events involving players.
 *
 * @author Josh
 */
public abstract class SmartBoardPlayerEvent extends SmartBoardEvent implements Cancellable {

  private final Player player;

  /**
   * Main constructor.
   *
   * @param board The board authoring the event.
   * @param player The player associated the event.
   */
  public SmartBoardPlayerEvent(@NotNull SmartBoard board, @NotNull Player player) {
    super(board, false);
    this.player = player;
  }

  /**
   * Async constructor.
   *
   * @param board The board associated with the event.
   * @param player The player associated with the event.
   * @param async Flag to set for asynchronous events.
   */
  public SmartBoardPlayerEvent(@NotNull SmartBoard board, @NotNull Player player, boolean async) {
    super(board, async);
    this.player = player;
  }

  /** @return Returns the player associated with the event. */
  @NotNull
  public Player getPlayer() {
    return this.player;
  }
}
