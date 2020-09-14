package jab.smartboards.commons.board;

import net.minecraft.server.v1_16_R2.EnumDirection;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public enum BoardDirection {
  NORTH,
  SOUTH,
  EAST,
  WEST,
  UP,
  DOWN;

  @NotNull
  public static EnumDirection toEnumDirection(BoardDirection direction) {
    switch (direction) {
      case NORTH:
        return EnumDirection.NORTH;
      case SOUTH:
        return EnumDirection.SOUTH;
      case EAST:
        return EnumDirection.EAST;
      case WEST:
        return EnumDirection.WEST;
      case UP:
        return EnumDirection.UP;
      case DOWN:
        return EnumDirection.DOWN;
    }
    return null;
  }

  @NotNull
  public BlockFace getFacingDirection() {
    switch (this) {
      case NORTH:
        return BlockFace.NORTH;
      case SOUTH:
        return BlockFace.SOUTH;
      case EAST:
        return BlockFace.EAST;
      case WEST:
        return BlockFace.WEST;
      case UP:
        return BlockFace.UP;
      case DOWN:
        return BlockFace.DOWN;
    }
    return null;
  }
}
