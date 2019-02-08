package jab.smartboards.commons.events;

import jab.smartboards.commons.board.menu.MenuBoard;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MenuClickEvent extends SmartBoardEvent<MenuBoard> {

  private static final HandlerList handlerList;

  /**
   * Main constructor.
   *
   * @param board The Board associated with the event.
   * @param async The flag to mark the event asynchronous.
   */
  public MenuClickEvent(@NotNull MenuBoard board, boolean async) {
    super(board, async);
  }

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }

  public static HandlerList getHandlerList() {
    return handlerList;
  }

  static {
    handlerList = new HandlerList();
  }
}
