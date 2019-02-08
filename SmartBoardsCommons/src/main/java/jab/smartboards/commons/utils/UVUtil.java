package jab.smartboards.commons.utils;

import jab.smartboards.commons.board.SmartBoard;
import jab.smartboards.commons.board.BoardDirection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO: Document.
 *
 * @author Josh
 */
@SuppressWarnings("unused")
public class UVUtil {

  private static volatile double mulStop = 1.0 / 2048.0;
  private static volatile double mulStart = 1.0 / 2.0;
  private static final int timesMax = 64;

  @Nullable
  public static double[] calculateUV(@NotNull SmartBoard board, @NotNull Player player) {
    BoardDirection dir = board.getDirection();
    double[] blockUV;
    switch (dir) {
      case NORTH:
        blockUV = calculateNorth(player, board);
        break;
      case SOUTH:
        blockUV = calculateSouth(player, board);
        break;
      case EAST:
        blockUV = calculateEast(player, board);
        break;
      default:
        blockUV = calculateWest(player, board);
        break;
    }
    return blockUV;
  }

  @Nullable
  private static double[] calculateNorth(@NotNull Player player, @NotNull SmartBoard board) {
    // Define the surface of the frame in 3D space.
    double bz = ((double) board.getLocation().getBlockZ()) + 15.0 / 16.0;
    // Grab the vectors for the location of the player and where the player is looking.
    Vector origin = player.getEyeLocation().toVector();
    Vector dir = player.getLocation().getDirection();
    // Create the ray from the origin of the player.
    Vector ray = origin.clone();
    // The initial multiplier will be 1 to go through each block until getting to the frame, when
    // precision is needed.
    double mul = mulStart;
    double lx = 0, ly = 0, lz = 0;
    int times = 0;
    while (mul != mulStop) {
      if (times == timesMax) break;
      while (ray.getZ() < bz) {
        if (times == timesMax) break;
        times++;
        lx = ray.getX();
        ly = ray.getY();
        lz = ray.getZ();
        // Calculate the current values.
        ray.setX(ray.getX() + (dir.getX() * mul));
        ray.setY(ray.getY() + (dir.getY() * mul));
        ray.setZ(ray.getZ() + (dir.getZ() * mul));
      }
      ray.setX(lx);
      ray.setY(ly);
      ray.setZ(lz);
      mul /= 2.0;
    }
    // At this point, the ray is casted onto the surface or slightly through the frame.
    // Get the top-left location of the board.
    Location boardLocation = board.getLocation();
    double bx = boardLocation.getBlockX();
    double by = boardLocation.getBlockY();
    // Grab the dimensions of the board.
    double boardWidth = board.getBlockWidth();
    double boardHeight = board.getBlockHeight();
    // Interpolate the offset in the block to UV coordinates.
    double ox = -(bx - ray.getX()) / boardWidth;
    double oy = -(by - ray.getY()) / boardHeight;
    // If the UV is outside of the range of the board, then the player has not clicked on the board.
    if (ox < 0 || ox > 1 || oy < 0 || oy > 1) return null;
    // Return the result.
    return new double[] {1 - ox, 1 - oy};
  }

  @Nullable
  private static double[] calculateSouth(@NotNull Player player, @NotNull SmartBoard board) {
    // Define the surface of the frame in 3D space.
    double bz = ((double) board.getLocation().getBlockZ()) + 0.0625;
    // Grab the vectors for the location of the player and where the player is looking.
    Vector origin = player.getEyeLocation().toVector();
    Vector dir = player.getLocation().getDirection();
    // Create the ray from the origin of the player.
    Vector ray = origin.clone();
    // The initial multiplier will be 1 to go through each block until getting to the frame, when
    // precision is needed.
    double mul = mulStart;
    double lx = 0.0, ly = 0.0, lz = 0.0;
    int times = 0;
    while (mul != mulStop) {
      if (times == timesMax) break;
      while (ray.getZ() > bz) {
        if (times == timesMax) break;
        times++;
        lx = ray.getX();
        ly = ray.getY();
        lz = ray.getZ();
        // Calculate the current values.
        ray.setX(ray.getX() + (dir.getX() * mul));
        ray.setY(ray.getY() + (dir.getY() * mul));
        ray.setZ(ray.getZ() + (dir.getZ() * mul));
      }
      ray.setX(lx);
      ray.setY(ly);
      ray.setZ(lz);
      mul /= 2.0;
    }
    // At this point, the ray is casted onto the surface or slightly through the frame.
    // Get the top-left location of the board.
    Location boardLocation = board.getLocation();
    double bx = boardLocation.getBlockX();
    double by = boardLocation.getBlockY();
    // Grab the dimensions of the board.
    double boardWidth = board.getBlockWidth();
    double boardHeight = board.getBlockHeight();
    // Interpolate the offset in the block to UV coordinates.
    double ox = -(bx - ray.getX()) / boardWidth;
    double oy = -(by - ray.getY()) / boardHeight;
    // If the UV is outside of the range of the board, then the player has not clicked on the board.
    if (ox < 0 || ox > 1 || oy < 0 || oy > 1) return null;
    // Return the result.
    return new double[] {ox, 1 - oy};
  }

  @Nullable
  private static double[] calculateEast(@NotNull Player player, @NotNull SmartBoard board) {
    // Define the surface of the frame in 3D space.
    double bx = ((double) board.getLocation().getBlockX()) + 0.0625;
    // Grab the vectors for the location of the player and where the player is looking.
    Vector origin = player.getEyeLocation().toVector();
    Vector dir = player.getLocation().getDirection();
    // Create the ray from the origin of the player.
    Vector ray = origin.clone();
    // The initial multiplier will be 1 to go through each block until getting to the frame, when
    // precision is needed.
    double mul = mulStart;
    double lx = 0, ly = 0, lz = 0;
    int times = 0;
    while (mul != mulStop) {
      if (times == timesMax) break;
      while (ray.getX() > bx) {
        if (times == timesMax) break;
        times++;
        lx = ray.getX();
        ly = ray.getY();
        lz = ray.getZ();
        // Calculate the current values.
        ray.setX(ray.getX() + (dir.getX() * mul));
        ray.setY(ray.getY() + (dir.getY() * mul));
        ray.setZ(ray.getZ() + (dir.getZ() * mul));
      }
      ray.setX(lx);
      ray.setY(ly);
      ray.setZ(lz);
      mul /= 2.0;
    }
    // At this point, the ray is casted onto the surface or slightly through the frame.
    // Get the top-left location of the board.
    Location boardLocation = board.getLocation();
    double bz = boardLocation.getBlockZ();
    double by = boardLocation.getBlockY();
    // Grab the dimensions of the board.
    double boardWidth = board.getBlockWidth();
    double boardHeight = board.getBlockHeight();
    // Interpolate the offset in the block to UV coordinates.
    double ox = -(bz - ray.getZ()) / boardWidth;
    double oy = -(by - ray.getY()) / boardHeight;
    // If the UV is outside of the range of the board, then the player has not clicked on the board.
    if (ox < 0 || ox > 1 || oy < 0 || oy > 1) return null;
    // Return the result.
    return new double[] {1 - ox, 1 - oy};
  }

  @Nullable
  private static double[] calculateWest(@NotNull Player player, @NotNull SmartBoard board) {
    // Define the surface of the frame in 3D space.
    double bx = ((double) board.getLocation().getBlockX()) + 15.0 / 16.0;
    // Grab the vectors for the location of the player and where the player is looking.
    Vector origin = player.getEyeLocation().toVector();
    Vector dir = player.getLocation().getDirection();
    // Create the ray from the origin of the player.
    Vector ray = origin.clone();
    // The initial multiplier will be 1 to go through each block until getting to the frame, when
    // precision is needed.
    double mul = mulStart;
    double lx = 0, ly = 0, lz = 0;
    int times = 0;
    while (mul != mulStop) {
      if (times == timesMax) break;
      while (ray.getX() < bx) {
        if (times == timesMax) break;
        times++;
        lx = ray.getX();
        ly = ray.getY();
        lz = ray.getZ();
        // Calculate the current values.
        ray.setX(ray.getX() + (dir.getX() * mul));
        ray.setY(ray.getY() + (dir.getY() * mul));
        ray.setZ(ray.getZ() + (dir.getZ() * mul));
      }
      ray.setX(lx);
      ray.setY(ly);
      ray.setZ(lz);
      mul /= 2.0;
    }
    // At this point, the ray is casted onto the surface or slightly through the frame.
    // Get the top-left location of the board.
    Location boardLocation = board.getLocation();
    double bz = boardLocation.getBlockZ();
    double by = boardLocation.getBlockY();
    // Grab the dimensions of the board.
    double boardWidth = board.getBlockWidth();
    double boardHeight = board.getBlockHeight();
    // Interpolate the offset in the block to UV coordinates.
    double ox = -(bz - ray.getZ()) / boardWidth;
    double oy = -(by - ray.getY()) / boardHeight;
    // If the UV is outside of the range of the board, then the player has not clicked on the board.
    if (ox < 0 || ox > 1 || oy < 0 || oy > 1) return null;
    // Return the result.
    return new double[] {ox, 1 - oy};
  }
}
