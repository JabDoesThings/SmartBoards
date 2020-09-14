package jab.smartboards.commons.events;

import jab.smartboards.commons.board.SmartBoard;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This is the base event class for all smartboard events.
 *
 * @author Josh
 */
public abstract class SmartBoardEvent<B extends SmartBoard> extends Event {

  /** The Board authoring the event. */
  private final B board;

  /**
   * Main constructor.
   *
   * @param board The Board associated with the event.
   * @param async The flag to mark the event asynchronous.
   */
  public SmartBoardEvent(@NotNull B board, boolean async) {
    super(async);
    this.board = board;
  }

  /** @return Returns the Board associated with the event. */
  @NotNull
  public B getBoard() {
    return this.board;
  }
}
