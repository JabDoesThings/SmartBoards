package jab.spigot.smartboards.events;

import jab.spigot.smartboards.SmartBoard;
import jab.spigot.smartboards.enums.BoardClick;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class SmartBoardClickEvent extends SmartBoardPlayerEvent {

  private static final HandlerList handlerList;

  private BoardClick click;
  private BoardClick.Type type;
  private double[] uv;
  private boolean cancelled;

  /**
   * Async constructor.
   *
   * @param board The board authoring the event.
   * @param player The player authoring the event.
   * @param async Flag to set for asynchronous events.
   */
  public SmartBoardClickEvent(
      @NotNull SmartBoard board,
      @NotNull Player player,
      boolean async,
      BoardClick click,
      BoardClick.Type type,
      double[] uv) {
    super(board, player, async);
    this.click = click;
    this.type = type;
    this.uv = uv;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean flag) {
    this.cancelled = flag;
  }

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }

  /**
   * Overrides the UV for the event.
   *
   * @param u The u-coordinate for the position clicked on the Board.
   * @param v The v-coordinate for the position clicked on the Board.
   */
  public void setUV(double u, double v) {
    u = normalize(u, 0, 1);
    v = normalize(v, 0, 1);
    this.uv = new double[] {u, v};
  }

  /** Cancels the event. */
  public void cancel() {
    this.cancelled = true;
  }

  /** @return Returns the u-coordinate for the position clicked on the Board. */
  public double getU() {
    return this.uv[0];
  }

  /** @return Returns the v-coordinate for the position clicked on the Board. */
  public double getV() {
    return this.uv[1];
  }

  /** @return Returns the calculated X coordinate on the board from the given U coordinate. */
  public int getX() {
    return (int) Math.floor(getU() * (getBoard().getWidth() * 128)) + 1;
  }

  /** @return Returns the calculated Y coordinate on the board from the given V coordinate. */
  public int getY() {
    return (int) Math.floor(getV() * (getBoard().getHeight() * 128)) + 1;
  }

  /** @return Returns true if the left-button on the mouse is clicked. */
  public boolean isLeftClick() {
    return this.click == BoardClick.LEFT;
  }

  /** @return Returns true if the right-button on the mouse is clicked. */
  public boolean isRightClick() {
    return this.click == BoardClick.RIGHT;
  }

  /** @return Returns the button clicked on the mouse. */
  public BoardClick getButtonClicked() {
    return this.click;
  }

  /** @return Returns the type of click. */
  public BoardClick.Type getClickType() {
    return this.type;
  }

  /**
   * Normalizes a value to the given minimum and maximum ranges.
   *
   * @param value The value to normalize.
   * @param min The minimum value allowed.
   * @param max The maximum value allowed.
   * @return Returns the normalized value.
   */
  private static double normalize(double value, double min, double max) {
    if (value > max) value = max;
    else if (value < min) value = min;
    return value;
  }

  public static HandlerList getHandlerList() {
    return handlerList;
  }

  static {
    handlerList = new HandlerList();
  }
}
