package jab.spigot.smartboards.utils;

import jab.spigot.smartboards.SmartBoard;
import org.jetbrains.annotations.NotNull;

/**
 * This utility class stores search results for board ray-casting. <br>
 * TODO: Document.
 *
 * @author Josh
 */
public class SmartBoardSearch {

  private SmartBoard board;
  private double[] uv;

  public SmartBoardSearch(@NotNull SmartBoard board, @NotNull double[] uv) {
    this.board = board;
    this.uv = uv;
  }

  @NotNull
  public SmartBoard getBoard() {
    return this.board;
  }

  @NotNull
  public double[] getUV() {
    return this.uv;
  }
}
