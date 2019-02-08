package jab.smartboards.commons.board;

import jab.smartboards.commons.utils.BoardSizable;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * This class packages and stores dimensional and locational information for smartboards.
 *
 * @author Josh
 */
public class BoardProfile implements Cloneable, BoardSizable {

  /** The width of the board in blocks. */
  private final int width;
  /** The height of the board in blocks. */
  private final int height;
  /** The direction the board will face. */
  private BoardDirection direction;
  /** The location of the top-left of the board. */
  private Location location;

  /**
   * Main constructor.
   *
   * @param location The top-left location of the board.
   * @param direction The direction the board will face.
   * @param width The width of the board in blocks.
   * @param height The height of the board in blocks.
   */
  public BoardProfile(
      @NotNull Location location, @NotNull BoardDirection direction, int width, int height) {
    this.location = location;
    this.direction = direction;
    this.width = width;
    this.height = height;
  }

  /**
   * Entity-view constructor.
   *
   * @param axis The calculated axis for an entity.
   */
  public BoardProfile(@NotNull BoardAxis axis) {
    this.location = axis.getLocation();
    this.direction = axis.getDirection();
    this.width = axis.getWidth();
    this.height = axis.getHeight();
  }

  /**
   * Clone constructor.
   *
   * @param other The profile to clone.
   */
  public BoardProfile(@NotNull BoardProfile other) {
    this.width = other.width;
    this.height = other.height;
    this.direction = other.direction;
    this.location = other.location.clone();
  }

  @Override
  @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
  protected Object clone() {
    return new BoardProfile(this);
  }

  /** @return Returns the direction the smartboard will face. */
  @NotNull
  public BoardDirection getDirection() {
    return this.direction;
  }

  /** @return Returns the world where the BoardProfile is defined. */
  @NotNull
  public World getWorld() {
    return location.getWorld();
  }

  /** @return Returns the location of the top-left of the smartboard. */
  @NotNull
  public Location getLocation() {
    return this.location;
  }

  /** @return Returns the width of the smartboard in blocks. */
  public int getBlockWidth() {
    return this.width;
  }

  /** @return Returns the height of the smartboard in blocks. */
  public int getBlockHeight() {
    return this.height;
  }
}
