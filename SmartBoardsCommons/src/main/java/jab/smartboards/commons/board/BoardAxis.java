package jab.smartboards.commons.board;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class calculates a smartboard's relative position to an entity.
 *
 * @author Josh
 */
public class BoardAxis implements Cloneable {

  /** The entity used to calculate smartboard dimensions. (Optional) */
  private LivingEntity entity;
  /** The location of the top-left of the smartboard. */
  private Location location;
  /** The direction the smartboard will face. */
  private BoardDirection direction;
  /** The width of the smartboard in blocks. */
  private int width;
  /** The height of the smartboard in blocks. */
  private int height;

  /**
   * Main constructor.
   *
   * @param entity The entity to reference both location and direction.
   * @param width The width of the board in blocks.
   * @param height The height of the board in blocks.
   * @param radius The radius from the player to spawn the map.
   */
  @NotNull
  public BoardAxis(@NotNull LivingEntity entity, int width, int height, double radius) {
    this.entity = entity;
    Vector direction = entity.getLocation().getDirection();
    Location location = entity.getEyeLocation().add(direction.multiply(radius));
    BoardDirection boardDirection = getDirection(entity);
    this.direction = inverse(boardDirection);
    // Calculate the top-left of the map based on where the entity is looking.
    if (boardDirection.equals(BoardDirection.NORTH)) {
      this.location = location.add(-(int) ((float) width / 2), -(int) ((float) height / 2), 0);
    } else if (boardDirection.equals(BoardDirection.SOUTH)) {
      this.location = location.add(-(int) ((float) width / 2), -(int) ((float) height / 2), 0);
    } else if (boardDirection.equals(BoardDirection.EAST)) {
      this.location = location.add(0, -(int) ((float) height / 2), -(int) ((float) width / 2));
    } else {
      this.location = location.add(0, -(int) ((float) height / 2), -(int) ((float) width / 2));
    }
    this.width = width;
    this.height = height;
  }

  /**
   * Alternative constructor.
   *
   * @param entity The entity to reference both location and direction.
   * @param board The board to reference for dimensions.
   * @param radius The radius from the player to spawn the map.
   */
  public BoardAxis(@NotNull LivingEntity entity, @NotNull SmartBoard board, double radius) {
    this.entity = entity;
    Vector direction = entity.getLocation().getDirection();
    Location location = entity.getEyeLocation().add(direction.multiply(radius));
    BoardDirection boardDirection = getDirection(entity);
    this.direction = inverse(boardDirection);
    BoardProfile profile = board.getProfile();
    this.width = profile.getBlockWidth();
    this.height = profile.getBlockHeight();
    // Calculate the top-left of the map based on where the entity is looking.
    if (boardDirection.equals(BoardDirection.NORTH)) {
      this.location = location.add(-(int) ((float) width / 2), -(int) ((float) height / 2), 0);
    } else if (boardDirection.equals(BoardDirection.SOUTH)) {
      this.location = location.add(-(int) ((float) width / 2), -(int) ((float) height / 2), 0);
    } else if (boardDirection.equals(BoardDirection.EAST)) {
      this.location = location.add(0, -(int) ((float) height / 2), -(int) ((float) width / 2));
    } else {
      this.location = location.add(0, -(int) ((float) height / 2), -(int) ((float) width / 2));
    }
  }

  /**
   * Clone constructor.
   *
   * @param other The BoardAxis to clone.
   */
  public BoardAxis(@NotNull BoardAxis other) {
    this.entity = other.entity;
    this.direction = other.direction;
    this.width = other.width;
    this.height = other.height;
    this.location = other.location.clone();
  }

  @Override
  @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
  protected Object clone() {
    return new BoardAxis(this);
  }

  /** @return Returns the entity used to calculate smartboard dimensions. (Optional) */
  @Nullable
  public LivingEntity getEntity() {
    return this.entity;
  }

  /** @return Returns the direction the smartboard will face. */
  @NotNull
  public BoardDirection getDirection() {
    return this.direction;
  }

  /** @return Returns the location of the top-left of the smartboard. */
  @NotNull
  public Location getLocation() {
    return this.location;
  }

  /** @return Returns the width of the smartboard in blocks. */
  public int getWidth() {
    return this.width;
  }

  /** @return Returns the height of the smartboard in blocks. */
  public int getHeight() {
    return this.height;
  }

  /**
   * @param enumDirection The direction to invert.
   * @return Returns the inverse direction of the one given.
   */
  @NotNull
  private static BoardDirection inverse(@NotNull BoardDirection enumDirection) {
    if (enumDirection.equals(BoardDirection.SOUTH)) {
      return BoardDirection.NORTH;
    } else if (enumDirection.equals(BoardDirection.NORTH)) {
      return BoardDirection.SOUTH;
    } else if (enumDirection.equals(BoardDirection.WEST)) {
      return BoardDirection.EAST;
    } else if (enumDirection.equals(BoardDirection.EAST)) {
      return BoardDirection.WEST;
    }
    return BoardDirection.SOUTH;
  }

  /**
   * @param entity The entity facing a direction.
   * @return Returns the direction the entity is facing.
   */
  @NotNull
  public static BoardDirection getDirection(@NotNull LivingEntity entity) {
    float yaw = entity.getLocation().getYaw();
    if (yaw < 0) yaw += 360;
    if (yaw >= 315 || yaw < 45) {
      return BoardDirection.SOUTH;
    } else if (yaw < 135) {
      return BoardDirection.WEST;
    } else if (yaw < 225) {
      return BoardDirection.NORTH;
    } else {
      return BoardDirection.EAST;
    }
  }
}
