package jab.spigot.smartboards.events;

import jab.spigot.smartboards.boards.SmartBoard;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This is the base event class for all smartboard events.
 *
 * @author Josh
 */
public abstract class SmartBoardEvent extends Event {

  /** The Board authoring the event. */
  private final SmartBoard board;

  /**
   * Main constructor.
   *
   * @param board The Board associated with the event.
   * @param async The flag to mark the event asynchronous.
   */
  public SmartBoardEvent(@NotNull SmartBoard board, boolean async) {
    super(async);
    this.board = board;
  }

  /** @return Returns the Board associated with the event. */
  @NotNull
  public SmartBoard getBoard() {
    return this.board;
  }
}
